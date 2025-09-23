package cn.bugstack.service.impl;

import cn.bugstack.common.constants.Constants;
import cn.bugstack.dao.IOrderDao;
import cn.bugstack.domain.po.PayOrder;
import cn.bugstack.domain.req.ShopCartReq;
import cn.bugstack.domain.res.PayOrderRes;
import cn.bugstack.domain.vo.ProductVO;
import cn.bugstack.service.ILoginService;
import cn.bugstack.service.IOrderService;
import cn.bugstack.service.rpc.ProductRPC;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayConfig;
import com.alipay.api.request.AlipayTradePagePayRequest;

import com.google.common.eventbus.EventBus;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 @author Euphoria
 @version 1.0
 @description: TODO
 @date 2025/9/22 下午2:07 */
@Service
@Slf4j
public class OrderServiceImpl implements IOrderService {

    @Resource
    private IOrderDao orderDao;

    @Resource
    private ProductRPC productRPC;

    @Resource
    private AlipayClient alipayClient;

    @Value("${alipay.notify_url}")
    private String notifyUrl;

    @Value("${alipay.return_url}")
    private String returnUrl;

    @Resource
    private EventBus eventBus;


    @Override
    public PayOrderRes createOrder(ShopCartReq shopCartReq) throws Exception {

        // 1. 查询当前用户是否存在未支付订单或者掉单
        PayOrder payOrder = new PayOrder();
        payOrder.setUserId(shopCartReq.getUserId());
        payOrder.setProductId(shopCartReq.getProductId());

        PayOrder unpayOrder = orderDao.queryUnpayOrder(payOrder);
        if (unpayOrder != null && Constants.OrderStatusEnum.PAY_WAIT.getCode().equals(unpayOrder.getStatus())) {
            log.info("创建订单-存在，已存在未支付订单。userId:{} productId:{} orderId:{}", shopCartReq.getUserId(), shopCartReq.getProductId(), unpayOrder.getOrderId());
            return PayOrderRes.builder()
                    .orderId(unpayOrder.getOrderId())
                    .payUrl(unpayOrder.getPayUrl())
                    .build();
        }else if(unpayOrder != null && Constants.OrderStatusEnum.CREATE.getCode().equals(unpayOrder.getStatus())){
            log.info("创建订单-存在，存在未创建支付单订单，创建支付单开始 userId:{} productId:{} orderId:{}", shopCartReq.getUserId(), shopCartReq.getProductId(), unpayOrder.getOrderId());
            PayOrder payOrder1 = doPrepayOrder(unpayOrder.getProductId(),unpayOrder.getProductName(),unpayOrder.getOrderId(),unpayOrder.getTotalAmount());
            return PayOrderRes.builder()
                    .orderId(payOrder1.getOrderId())
                    .payUrl(payOrder1.getPayUrl())
                    .build();
        }

        // 查询商品创建订单
        ProductVO productVO = productRPC.queryProductByProductId(shopCartReq.getProductId());

        String orderId = RandomStringUtils.randomNumeric(16);

        orderDao.insert(PayOrder.builder()
                        .userId(shopCartReq.getUserId())
                        .orderId(orderId)
                        .productId(shopCartReq.getProductId())
                        .productName(productVO.getProductName())
                        .totalAmount(productVO.getPrice())
                        .orderTime(new Date())
                        .status(Constants.OrderStatusEnum.CREATE.getCode())
                .build());

        // 创建支付单
        PayOrder payOrder1 = doPrepayOrder(productVO.getProductId(), productVO.getProductName(), orderId, productVO.getPrice());

        return PayOrderRes.builder()
                .orderId(orderId)
                .payUrl(payOrder1.getPayUrl())
                .build();
    }

    @Override
    public void changeOrderPaySuccess(String orderId)  {

        PayOrder payOrderReq = new PayOrder();
        payOrderReq.setOrderId(orderId);

        payOrderReq.setStatus(Constants.OrderStatusEnum.PAY_SUCCESS.getCode());
        orderDao.changeOrderPaySuccess(payOrderReq);

        eventBus.post(JSON.toJSONString(payOrderReq));
    }

    @Override
    public List<String> queryNoPayNotifyOrder()  {
        return orderDao.queryNoPayNotifyOrder();
    }

    @Override
    public List<String> queryTimeoutCloseOrderList() {
        return orderDao.queryTimeoutCloseOrderList();
    }

    @Override
    public Boolean changeOrderClose(String orderId) {
        return orderDao.changeOrderClose(orderId);
    }

    private PayOrder doPrepayOrder(String productId,String productName, String orderId, BigDecimal totalAmount) throws Exception {

        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        request.setNotifyUrl(notifyUrl);
        request.setReturnUrl(returnUrl);

        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", orderId);
        bizContent.put("total_amount", totalAmount.toString());
        bizContent.put("subject", productName);
        bizContent.put("product_code", "FAST_INSTANT_TRADE_PAY");
        request.setBizContent(bizContent.toString());

        String form = alipayClient.pageExecute(request).getBody();

        PayOrder payOrder = new PayOrder();
        payOrder.setOrderId(orderId);
        payOrder.setPayUrl(form);
        payOrder.setStatus(Constants.OrderStatusEnum.PAY_WAIT.getCode());

        orderDao.updateOrderPayInfo(payOrder);

        return payOrder;

    }
}
