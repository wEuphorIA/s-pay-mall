package cn.bugstack.job;

import cn.bugstack.service.IOrderService;
import com.alipay.api.AlipayClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

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



}
