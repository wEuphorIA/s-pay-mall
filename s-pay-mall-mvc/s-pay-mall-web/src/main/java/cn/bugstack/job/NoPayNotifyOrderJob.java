package cn.bugstack.job;

import cn.bugstack.service.IOrderService;
import com.alipay.api.AlipayClient;
import com.alipay.api.domain.AlipayTradeQueryModel;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 @author Euphoria
 @version 1.0
 @description: TODO
 @date 2025/9/23 下午9:24 */
@Slf4j
@Component
public class NoPayNotifyOrderJob {

    @Resource
    private IOrderService orderService;

    @Resource
    private AlipayClient alipayClient;

    @Scheduled(cron = "0 * * * * ?")
    public void exec(){
        try {
            log.info("任务；检测未接收到或未正确处理的支付回调通知");
            List<String> orderIds = orderService.queryNoPayNotifyOrder();
            if (!CollectionUtils.isEmpty(orderIds)) {
                for (String orderId : orderIds) {
                    AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
                    AlipayTradeQueryModel bizModel = new AlipayTradeQueryModel();
                    bizModel.setOutTradeNo(orderId);
                    request.setBizModel(bizModel);

                    AlipayTradeQueryResponse alipayTradeQueryResponse = alipayClient.execute(request);
                    String code = alipayTradeQueryResponse.getCode();
                    String tradeStatus = alipayTradeQueryResponse.getTradeStatus();
                    log.info("状态码{}",tradeStatus);
                    // 判断状态码
                    // if ("10000".equals(code)) {
                    //     orderService.changeOrderPaySuccess(orderId);
                    // }
                    // 判断状态在更新订单
                    if( tradeStatus!=null && tradeStatus.equals("TRADE_SUCCESS")){
                        orderService.changeOrderPaySuccess(orderId);
                    }
                }
            }
        }catch (Exception e){
            log.error("检测未接收到或未正确处理的支付回调通知失败", e);
        }
    }

}