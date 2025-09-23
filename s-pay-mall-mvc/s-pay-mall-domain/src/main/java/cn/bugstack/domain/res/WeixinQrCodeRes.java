package cn.bugstack.domain.res;

import lombok.Data;

/**
 @author Euphoria
 @version 1.0
 @description: TODO
 @date 2025/9/21 下午8:50 */
@Data
public class WeixinQrCodeRes {
    private String ticket;
    private Long expire_seconds;
    private String url;
}
