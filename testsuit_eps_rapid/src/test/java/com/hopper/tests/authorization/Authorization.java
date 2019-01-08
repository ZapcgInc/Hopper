package com.hopper.tests.authorization;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import com.hopper.tests.constants.SupportedPartners;
import org.apache.commons.codec.digest.MessageDigestAlgorithms;
import org.apache.commons.lang.StringUtils;

/**
 * Util class for generating API Authorization key
 */
public class Authorization
{
    private static final String EPS_AUTH_KEY_FORMAT = "EAN APIKey=%s,Signature=%s,timestamp=%s";
    private static final String AUTH_API_KEY = "apikey";
    private static final String AUTH_SECRET_KEY = "secret";

    public static String getAuthKey(final SupportedPartners partner, final Map<String, String> authKeyMap)
    {
        switch (partner)
        {
            case EPS:
            {
                return _getEPSAuthKey(authKeyMap);
            }
            default:
            {
                throw new UnsupportedOperationException("Authorization for Partner :" + partner.name() + "is currently unsupported");
            }
        }
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
