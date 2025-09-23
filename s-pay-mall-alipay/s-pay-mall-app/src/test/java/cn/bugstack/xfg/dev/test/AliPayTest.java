package cn.bugstack.xfg.dev.test;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeQueryModel;
import com.alipay.api.domain.AlipayTradeRefundModel;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradeRefundResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

@Slf4j
public class AliPayTest {

    // 「沙箱环境」应用ID - 您的APPID，收款账号既是你的APPID对应支付宝账号。获取地址；https://open.alipay.com/develop/sandbox/app
    public static String app_id = "9021000154654929";

    // 「沙箱环境」商户私钥，你的PKCS8格式RSA2私钥 -【秘钥工具】所创建的公户私钥
    public static String merchant_private_key = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDIzBGasC98NZx0sgUquiAotfzcWzMWH1Jd54pydhp1Hz/b2bF87Hh1n6uoEVEacH+AEqs4Hq/vMbD5Fp1ZgTTsHsEP+r9aYYWPOxWbigNt/jBihyaacH1Iu957aTlXMtcn72HM0ZaVmT9kVsNyt9kN+FZdUNMlVJDjQWGY1cA4MkvlIfRv158HRm9nR+5aZyFXhdTte3v/Sn0NcX9Y+ffgd2tlbiBuVuO7+DFNQ4+PCKEXQy4lu6gy1E5oylo6A+g2P1bKyI8BhETwHOIa/XoRu5o7+F/a/aMbNerAoxlEvRRcV0nVzkM1tcsHh4a6kbSpyx1Nb+QwrnZGEGf2qvwxAgMBAAECggEBAI6+9Czh34APpMAY5i9vzfjjF3WXqAOsFKQJPJUOfNSSeJVUdyb1/NSxxBhg5yVCoIFtxCbLOgtzafpKJ2Y/wl2xp1Iy0eHwvkAKotvGC5YgRElnBvx3x7jzRcC6Fzd0Pxx2wzIWNVT4cuZj/+IH7NhPmSYRlWlOawoDqtw56MKJS7QZOT+XaTHmrsQ8BRVXpAUjtuyGaQyVmphy9DQX1l6nn+ncJxWx5lwG2zU9Z3+BMJinn2hifkxNfcWcWtATmausIjSlUdhodTrP1ZEuaqlfAHWjdE1qizeqTRJCFc8x8KXLAzlDf4iT6PV6W8Y19AZSIzV1MsCpC8tERg4EZAECgYEA9PIgbudU9LqF0dwq6lWIvzcdAGsKIEOhx/CVGDzYEU+W6DBHaOgLVCjcZUjWUENaSzp0DWW6PbyP/9qJdyUjlJzyjzLwFfBhVCbwmOOkHGs46SaFWAQM2AGE+r67GkNKflln9QaUN/KEIRcCI2yoYSK2nAWbIcOcu3NfWzDhLmECgYEA0dvj1OUSOj5sPAdYWga2AB48KyNoz5Ql8rgwU2wGlthzl7eCFAjuxr4EDYk7NUSqej/jmdqlAzYfU6u0X4j6Vw2zm+I5mzOnHPIR647p0syq+BgkaYU1CNlfU1lrjQQFQKK5TSjGrAnG6h8ZJUbNf+bUoC9r1Ktb1yFU+9YSf9ECgYBT1heRve3PAZRR2th5ciMmx1dO0FQb7OoPg+GT9SdC7YnKGNQoLNvLx1x/8ikgOZ1LCEu2Vi4jeUmUGZlZRUXjVMCUkKup5qd5ZQaOXcPK0kwdSMj08hXvTL3WELf7ajKyAinZrJHIgGIG7VgXBdavwHDd//Ez4j7fzcJCdP1toQKBgCLhhpvjO0VdDMZpfiDwjp20aNHOv+QIvsn6HWYwRWQbNBNamA9WcMGt4aSSU7oF4MQTDHSNJ/JBs7FHj+J1NQ71Iw5c6rBGRJeGEL5zcZ/tDtnPwXWcg3UmFDcZXMwCiBD3Ow99IJCFy38sjhRpEU0aA+tgFm8x0EEQtDxrl4WRAoGBAIUtXJW7x/lo7KBY0Nh6gCZ//e2/50aS3E/RkT0lnwsX5LmdTtMDzSMUW/wrElhkkeTGPCOtOErPJiyLjhR5B2bviIWgcXwTUFsjCMW+39KaohoWxhot1finqICt3nVRrgz2xtAMvzHv6yahUsbdytCWnJcRsI5ZleRkoE22ltE3";

    // 「沙箱环境」支付宝公钥 -【秘钥填写】后提供给你的支付宝公钥
    public static String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAsk2VxIHJgbquBQXhNpO5SaVqzb+3Nv5ogIv+oxDVQ/ebpJwKWmcMq85K+QIS3WBbI8ncaf2in6Yh0x+C1W3rtrbadokjoC8uoM3mmz1HLhx8xPRRLdsDOglX9H7Hjet4xsuJIWHYJO/1mA6Je7YXU23XsmoYU2Psugi+3P4Pb9iP7h8KBdnjBaTiICQDrqxLWfDhBwoYUQkhGPebcAjfdNGFbYJO1ZEHMB2zNK6rBaOtWK0g9xzsWAlNOF82+dtOKwKDT8rpa+nTjKjyfA109PhR8S77FeDL84cCwRoFELQsszDbRmFPYlLyiG+5iXnjxTWN+BI/gvTYG1Jn4Y+wgQIDAQAB";
    // 「沙箱环境」服务器异步通知回调地址
    public static String notify_url = "http://s8bcbbc6.natappfree.cc/api/v1/alipay/alipay_notify_url";
    // 「沙箱环境」页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    public static String return_url = "https://gaga.plus";
    // 「沙箱环境」
    public static String gatewayUrl = "https://openapi-sandbox.dl.alipaydev.com/gateway.do";
    // 签名方式
    public static String sign_type = "RSA2";
    // 字符编码格式
    public static String charset = "utf-8";

    private AlipayClient alipayClient;

    @Before
    public void init() {
        this.alipayClient = new DefaultAlipayClient(gatewayUrl,
                app_id,
                merchant_private_key,
                "json",
                charset,
                alipay_public_key,
                sign_type);
    }

    @Test
    public void test_aliPay_pageExecute() throws AlipayApiException {
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();  // 发送请求的 Request类
        request.setNotifyUrl(notify_url);
        request.setReturnUrl(return_url);

        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", "xfg2024092709120005");  // 我们自己生成的订单编号
        bizContent.put("total_amount", "0.01"); // 订单的总金额
        bizContent.put("subject", "测试商品");   // 支付的名称
        bizContent.put("product_code", "FAST_INSTANT_TRADE_PAY");  // 固定配置
        request.setBizContent(bizContent.toString());

        String form = alipayClient.pageExecute(request).getBody();
        log.info("测试结果\r\n\n把我复制到 index.html ->：{}", form);

    }

    /**
     * 查询订单
     */
    @Test
    public void test_alipay_certificateExecute() throws AlipayApiException {

        AlipayTradeQueryModel bizModel = new AlipayTradeQueryModel();
        bizModel.setOutTradeNo("xfg2024092709120005");

        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        request.setBizModel(bizModel);

        String body = alipayClient.execute(request).getBody();
        log.info("测试结果：{}", body);
    }

    /**
     * 退款接口
     */
    @Test
    public void test_alipay_refund() throws AlipayApiException {
        AlipayTradeRefundRequest request =new AlipayTradeRefundRequest();
        AlipayTradeRefundModel refundModel =new AlipayTradeRefundModel();
        refundModel.setOutTradeNo("daniel82AAAA000032333361X03");
        refundModel.setRefundAmount("1.00");
        refundModel.setRefundReason("退款说明");
        request.setBizModel(refundModel);

        AlipayTradeRefundResponse execute = alipayClient.execute(request);
        log.info("测试结果：{}", execute.isSuccess());
    }

}