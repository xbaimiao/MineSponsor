package com.xbaimiao.mine.sponsor.mirai;


import org.apache.commons.codec.binary.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

public class Test {

    public static void main(String[] args) throws Exception {
        Long timestamp = 1661569470194L;
        String appSecret = "8Zqf0dnN8MFxuOhczHkuIG9znBdglqm3fOkI9M8Id14FshUGMvsuWMI8NrtZG1ag";
        String stringToSign = timestamp + "\n" + appSecret;
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(appSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        byte[] signData = mac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8));
        String sign = new String(Base64.encodeBase64(signData));
        System.out.println(sign);
    }

}
