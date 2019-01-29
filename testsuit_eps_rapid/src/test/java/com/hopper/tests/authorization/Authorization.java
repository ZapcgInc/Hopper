package com.hopper.tests.authorization;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Map;

import com.hopper.tests.constants.GlobalConstants;
import org.apache.commons.codec.digest.MessageDigestAlgorithms;
import org.apache.commons.lang.StringUtils;

/**
 * Util class for generating API Authorization key
 */
public class Authorization
{
    public static final String AUTH_TYPE = "auth_type";
    public static final String DEFAULT_AUTH = "EPS";
    
    private static final String EPS_AUTH_KEY_FORMAT = "EAN APIKey=%s,Signature=%s,timestamp=%s";
    private static final String AUTH_API_KEY = "apikey";
    private static final String AUTH_SECRET_KEY = "secret";
    private static final String USER_NAME = "user_name";
    private static final String PASSWORD = "password";
    private static final String BASIC_AUTH = "basic_auth";

    public static String getAuthKey(final Map<String, String> authKeyMap)
    {
    	String auth_type = authKeyMap.get(AUTH_TYPE);
    	if(auth_type == null) {
    		auth_type = DEFAULT_AUTH;
    	}
        switch (auth_type)
        {
            case DEFAULT_AUTH:
            {
                return _getEPSAuthKey(authKeyMap);
            }
            case BASIC_AUTH:
            {
            	return _getBasicAuthKey(authKeyMap);
            }
            default:
            {
            	return _getEPSAuthKey(authKeyMap);
            }
        }
    }
    
    private static String _getBasicAuthKey(final Map<String, String> authKeyMap)
    {
        final String user = authKeyMap.get(USER_NAME);
        final String password = authKeyMap.get(PASSWORD);
        String auth = user + ":" + password;
        String basicAuthPayload = "Basic " + Base64.getEncoder().encodeToString(auth.getBytes());
        return basicAuthPayload;
    }

    private static String _getEPSAuthKey(final Map<String, String> authKeyMap)
    {
        final String apiKey = authKeyMap.get(AUTH_API_KEY);
        final String secret = authKeyMap.get(AUTH_SECRET_KEY);
        final long systemTimeInUnixTimestampInSeconds = System.currentTimeMillis() / 1000L;

        try
        {
            if (StringUtils.isNotEmpty(apiKey) || StringUtils.isNotEmpty(secret))
            {
                final String seed = apiKey + secret + systemTimeInUnixTimestampInSeconds;

                final MessageDigest md = MessageDigest.getInstance(MessageDigestAlgorithms.SHA_512);
                final byte[] bytes = md.digest(seed.getBytes(StandardCharsets.UTF_8));

                final StringBuilder sb = new StringBuilder();
                for (byte aByte : bytes)
                {
                    sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
                }
                final String signature = sb.toString();

                return String.format(EPS_AUTH_KEY_FORMAT, apiKey, signature, systemTimeInUnixTimestampInSeconds);
            }
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }

        return null;
    }
}
