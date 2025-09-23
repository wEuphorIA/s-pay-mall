package cn.bugstack.service;

import cn.bugstack.domain.req.ShopCartReq;
import cn.bugstack.domain.res.PayOrderRes;

import java.util.List;

public interface IOrderService {

    PayOrderRes createOrder(ShopCartReq shopCartReq) throws Exception;

    //更新订单状态
    void changeOrderPaySuccess(String orderId) ;

    //查询有效期范围内未接受到支付回调的订单
    List<String> queryNoPayNotifyOrder() ;

    //查询超时订单
    List<String> queryTimeoutCloseOrderList() ;

    Boolean changeOrderClose(String orderId);
}
