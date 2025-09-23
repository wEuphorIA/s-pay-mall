package cn.bugstack.service.weixin;


import cn.bugstack.domain.vo.WeixinTemplateMessageVO;
import cn.bugstack.domain.req.WeixinQrCodeReq;
import cn.bugstack.domain.res.WeixinQrCodeRes;
import cn.bugstack.domain.res.WeixinTokenRes;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

//retrofit2
public interface IWeixinApiService {

    /**
     * @param grantType 填写 client_credential
     * @param appId 账号的唯一凭证，即 AppID
     * @param secret 唯一凭证密钥，即 AppSecret
     * @return
     */
    @GET("cgi-bin/token")
    Call<WeixinTokenRes> getToken(
            @Query("grant_type") String grantType,
            @Query("appid") String appId,
            @Query("secret") String secret
    );

    @POST("cgi-bin/qrcode/create")
    Call<WeixinQrCodeRes> createQrCode(@Query("access_token") String accessToken, @Body WeixinQrCodeReq weixinQrCodeReq);


    /**
     * 发送微信公众号模板消息
     * 文档：https://mp.weixin.qq.com/debug/cgi-bin/readtmpl?t=tmplmsg/faq_tmpl
     *
     * @param accessToken              getToken 获取的 token 信息
     * @param weixinTemplateMessageVO 入参对象
     * @return 应答结果
     */
    @POST("cgi-bin/message/template/send")
    Call<Void> sendMessage(@Query("access_token") String accessToken, @Body WeixinTemplateMessageVO weixinTemplateMessageVO);

}
