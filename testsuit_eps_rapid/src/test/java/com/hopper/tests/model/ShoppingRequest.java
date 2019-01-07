package com.hopper.tests.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Value Object to hold Shopping Request Values.
 */
public class ShoppingRequest
{
    private final String m_host;
    private final String m_version;
    private final String m_apiPath;
    private final String m_authKey;
    private final Map<String, String> m_headers;
    private final Map<String, String> m_params;

    private ShoppingRequest(Builder builder)
    {
        m_host = builder.m_host;
        m_version = builder.m_version;
        m_apiPath = builder.m_apiPath;
        m_authKey = builder.m_authKey;
        m_headers = builder.m_headers;
        m_params = builder.m_params;
    }

    public String getHost()
    {
        return m_host;
    }

    public String getVersion()
    {
        return m_version;
    }

    public String getAPIPath()
    {
        return m_apiPath;
    }

    public String getAuthKey()
    {
        return m_authKey;
    }

    public Map<String, String> getHeaders()
    {
        return m_headers;
    }

    public Map<String, String> getParams()
    {
        return m_params;
    }

    public String getEndPoint()
    {
        final StringBuffer endPoint = new StringBuffer(m_host);

        if (m_version != null)
        {
            endPoint.append("/");
            endPoint.append(m_version);
        }

        if (m_apiPath != null)
        {
            endPoint.append("/");
            endPoint.append(m_apiPath);
        }
        return endPoint.toString();
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public static class Builder
    {
        private static final String AUTH_HEADER_KEY = "Authorization";
        private String m_host;
        private String m_version;
        private String m_apiPath;
        private String m_authKey;
        private Map<String, String> m_headers = new HashMap<>();
        private Map<String, String> m_params;

        public void withHost(String host)
        {
            m_host = host;
        }


        public void withVersion(String version)
        {
            m_version = version;
        }


        public void withAPIPath(String apiPath)
        {
            m_apiPath = apiPath;
        }


        public void withAuthKey(String authKey)
        {
            m_authKey = authKey;
            m_headers.put(AUTH_HEADER_KEY, authKey);
        }


        public void withHeaders(Map<String, String> headers)
        {
            if (headers != null)
            {
                m_headers.putAll(headers);
            }
        }

        public void addHeaders(String key, String value)
        {
            m_headers.put(key, value);
        }

        public void removeHeader(String key)
        {
            m_headers.remove(key);
        }

        public void withParams(Map<String, String> params)
        {
            m_params = new HashMap<>(params);
        }

        public void addParam(String key, String value)
        {
            m_params.put(key, value);
        }

        public void removeParam(String key)
        {
            m_params.remove(key);
        }

        public ShoppingRequest build()
        {
            return new ShoppingRequest(this);
        }
    }
}
