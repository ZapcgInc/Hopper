package com.hopper.tests.authorization;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang.StringUtils;


public class Authorization {

	public static String getAuthKey(Map<String, String> authKeyMap) {
		String authKey = null;
        String apiKey = authKeyMap.get("apikey");
        String secret = authKeyMap.get("secret");
        Date date = new java.util.Date();
        Long timestamp = (date.getTime() / 1000);
        String signature = null;
        try {
            if (StringUtils.isNotEmpty(apiKey) || StringUtils.isNotEmpty(secret)) {
                String toBeHashed = apiKey + secret + timestamp;
                MessageDigest md = MessageDigest.getInstance("SHA-512");
                byte[] bytes = md.digest(toBeHashed.getBytes("UTF-8"));
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < bytes.length; i++) {
                    sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
                }
                signature = sb.toString();
                authKey = "EAN APIKey=" + apiKey + ",Signature=" + signature + ",timestamp=" + timestamp;
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return authKey;
    }

}
