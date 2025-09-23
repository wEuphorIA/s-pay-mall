package cn.bugstack.domain.res;

import lombok.Data;

/**
 @author Euphoria
 @version 1.0
 @description: 获取access_token 的dto对象
 @date 2025/9/21 下午2:32 */
@Data
public class WeixinTokenRes {

    private String access_token;
    private int expires_in;
    private String errcode;
    private String errmsg;
}
