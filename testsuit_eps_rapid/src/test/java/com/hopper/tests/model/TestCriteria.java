package com.hopper.tests.model;

import com.hopper.tests.constants.SupportedPartners;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Simple POJO to hold test criteria
 */
public class TestCriteria
{
    private static final String AUTH_HEADER_KEY = "Authorization";
    private SupportedPartners m_partner;
    private String m_host;
    private String m_version;
    private String m_apiPath;
    private String m_authKey;
    private Map<String, String> m_headers = new HashMap<>();
    private Map<String, String> m_params = new HashMap<>();
    private Map<String, List<String>> m_paramsWithMultipleValues = new HashMap<>();

    public SupportedPartners getPartner()
    {
        return m_partner;
    }

    public void setPartner(SupportedPartners partner)
    {
        m_partner = partner;
    }

    public String getHost()
    {
        return m_host;
    }

    public void setHost(String host)
    {
        m_host = host;
    }

    public String getVersion()
    {
        return m_version;
    }

    public void setVersion(String version)
    {
        m_version = version;
    }

    public String getApiPath()
    {
        return m_apiPath;
    }

    public void setApiPath(String apiPath)
    {
        m_apiPath = apiPath;
    }

    public String getAuthKey()
    {
        return m_authKey;
    }

    public void setAuthKey(String authKey)
    {
        m_authKey = authKey;
        m_headers.put(AUTH_HEADER_KEY, authKey);
    }

    public Map<String, String> getHeaders()
    {
        return m_headers;
    }

    public void setHeaders(Map<String, String> headers)
    {
        if (headers != null)
        {
            m_headers.putAll(headers);
        }
    }

    public void addHeader(String header, String value)
    {
        m_headers.put(header, value);
    }

    public void removeHeader(String header)
    {
        m_headers.remove(header);
    }

    public Map<String, String> getParams()
    {
        return m_params;
    }

    public void setParams(Map<String, String> params)
    {
        if (m_params != null)
        {
            m_params.putAll(params);
        }
    }

    public void addParam(String header, String value)
    {
        m_params.put(header, value);
    }

    public void removeParam(String header)
    {
        m_params.remove(header);
    }

    public Map<String, List<String>> getParamsWithMultipleValues()
    {
        return m_paramsWithMultipleValues;
    }

    public void setParamsWithMultipleValues(Map<String, List<String>> paramsWithMultipleValues)
    {
        if (paramsWithMultipleValues != null)
        {
            m_paramsWithMultipleValues.putAll(paramsWithMultipleValues);
        }
    }

    public void addParamWithMultipleValues(String header, List<String> multipleValues)
    {
        m_paramsWithMultipleValues.put(header, multipleValues);
    }

    public void removeParamWithMultipleValues(String header)
    {
        m_paramsWithMultipleValues.remove(header);
    }
}
