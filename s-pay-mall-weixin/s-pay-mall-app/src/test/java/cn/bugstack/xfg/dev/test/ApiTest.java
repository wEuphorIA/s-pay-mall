package cn.bugstack.xfg.dev.test;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * 模板消息案例
 * 打开微信公众号，测试平台。https://mp.weixin.qq.com/debug/cgi-bin/sandboxinfo?action=showinfo&t=sandbox/index 新增加一个模板消息，结构如下；
 *
 * 项目：{{repo_name.DATA}} 分支：{{branch_name.DATA}} 作者：{{commit_author.DATA}} 说明：{{commit_message.DATA}}
 *
 * 创建后，你会获得一个 template_id
 */
@Slf4j
public class ApiTest {

    // 换成自己的 appid、secret；https://mp.weixin.qq.com/debug/cgi-bin/sandboxinfo?action=showinfo&t=sandbox/index
    private static final String APPID = "wx5a228ff69e28a91f";
    private static final String SECRET = "0bea03aa1310bac050aae79dd8703928";
    private static final String GRANT_TYPE = "client_credential";
    private static final String URL_TEMPLATE = "https://api.weixin.qq.com/cgi-bin/token?grant_type=%s&appid=%s&secret=%s";

    public static void main(String[] args) throws Exception {
        String accessToken = getAccessToken(APPID, SECRET);

        Map<String, Map<String, String>> data = new HashMap<>();
        TemplateMessageDTO.put(data, TemplateMessageDTO.TemplateKey.REPO_NAME, "big-market");
        TemplateMessageDTO.put(data, TemplateMessageDTO.TemplateKey.BRANCH_NAME, "240702-xfg-refactor");
        TemplateMessageDTO.put(data, TemplateMessageDTO.TemplateKey.COMMIT_AUTHOR, "fuzhengwei");
        TemplateMessageDTO.put(data, TemplateMessageDTO.TemplateKey.COMMIT_MESSAGE, "feat: 抽奖订单功能实现");

        // 通知结构
        // touser 关注你的公众号，获得你自己的ID，否则消息不会发给自己
        // template_id 模板id，换成你自己的
        TemplateMessageDTO templateMessageDTO = new TemplateMessageDTO("or0Ab6ivwmypESVp_bYuk92T6SvU", "l2HTkntHB71R4NQTW77UkcqvSOIFqE_bss1DAVQSybc");
        templateMessageDTO.setUrl("https://gaga.plus");
        templateMessageDTO.setData(data);

        // 发送通知
        URL url = new URL(String.format("https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=%s", accessToken));
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; utf-8");
        conn.setRequestProperty("Accept", "application/json");
        conn.setDoOutput(true);

        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = JSON.toJSONString(templateMessageDTO).getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        try (Scanner scanner = new Scanner(conn.getInputStream(), StandardCharsets.UTF_8.name())) {
            String response = scanner.useDelimiter("\\A").next();
            log.info("openai-code-review weixin template message! {}", response);
        }

    }

    public static String getAccessToken(String APPID, String SECRET) {
        try {
            String urlString = String.format(URL_TEMPLATE, GRANT_TYPE, APPID, SECRET);
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                // Print the response
                System.out.println("Response: " + response.toString());

                Token token = JSON.parseObject(response.toString(), Token.class);

                return token.getAccess_token();
            } else {
                System.out.println("GET request failed");
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static class Token {
        private String access_token;
        private Integer expires_in;

        public String getAccess_token() {
            return access_token;
        }

        public void setAccess_token(String access_token) {
            this.access_token = access_token;
        }

        public Integer getExpires_in() {
            return expires_in;
        }

        public void setExpires_in(Integer expires_in) {
            this.expires_in = expires_in;
        }
    }

    public static class TemplateMessageDTO {

        private String touser = "or0Ab6ivwmypESVp_bYuk92T6SvU";
        private String template_id = "GLlAM-Q4jdgsktdNd35hnEbHVam2mwsW2YWuxDhpQkU";
        private String url = "https://weixin.qq.com";
        private Map<String, Map<String, String>> data = new HashMap<>();

        public TemplateMessageDTO(String touser, String template_id) {
            this.touser = touser;
            this.template_id = template_id;
        }

        public void put(TemplateKey key, String value) {
            data.put(key.getCode(), new HashMap<String, String>() {
                private static final long serialVersionUID = 7092338402387318563L;

                {
                    put("value", value);
                }
            });
        }

        public static void put(Map<String, Map<String, String>> data, TemplateKey key, String value) {
            data.put(key.getCode(), new HashMap<String, String>() {
                private static final long serialVersionUID = 7092338402387318563L;

                {
                    put("value", value);
                }
            });
        }


        public enum TemplateKey {
            REPO_NAME("repo_name","项目名称"),
            BRANCH_NAME("branch_name","分支名称"),
            COMMIT_AUTHOR("commit_author","提交者"),
            COMMIT_MESSAGE("commit_message","提交信息"),
            ;

            private String code;
            private String desc;

            TemplateKey(String code, String desc) {
                this.code = code;
                this.desc = desc;
            }

            public String getCode() {
                return code;
            }

            public void setCode(String code) {
                this.code = code;
            }

            public String getDesc() {
                return desc;
            }

            public void setDesc(String desc) {
                this.desc = desc;
            }
        }


        public String getTouser() {
            return touser;
        }

        public void setTouser(String touser) {
            this.touser = touser;
        }

        public String getTemplate_id() {
            return template_id;
        }

        public void setTemplate_id(String template_id) {
            this.template_id = template_id;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public Map<String, Map<String, String>> getData() {
            return data;
        }

        public void setData(Map<String, Map<String, String>> data) {
            this.data = data;
        }

    }

}
