package com.hopper.tests.model;

import com.hopper.tests.constants.RequestType;
import com.hopper.tests.constants.SupportedPartners;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Simple POJO to hold test criteria
 */
public class TestContext
{
    private static final String AUTH_HEADER_KEY = "Authorization";

    private SupportedPartners m_partner;
    private String m_host;
    private String m_version;
    private EnumMap<RequestType, String> m_requestTypeToAPIPath = new EnumMap<RequestType, String>(RequestType.class);
    private Map<String, String> m_headers = new HashMap<>();
    private Map<String, String> m_params = new HashMap<>();
    private Map<String, List<String>> m_paramsWithMultipleValues = new HashMap<>();
    private ShoppingResponse m_shoppingResponse = null;

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

    public String getApiPath(RequestType requestType)
    {
        return m_requestTypeToAPIPath.get(requestType);
    }

    public void setApiPath(RequestType requestType, String apiPath)
    {
        m_requestTypeToAPIPath.put(requestType, apiPath);
    }

    public void setAuthKey(String authKey)
    {
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

    public void addParamWithMultipleValues(String header, List<String> multipleValues)
    {
        m_paramsWithMultipleValues.put(header, multipleValues);
    }

    public void removeParamWithMultipleValues(String header)
    {
        m_paramsWithMultipleValues.remove(header);
    }

    public ShoppingResponse getShoppingResponse()
    {
        return m_shoppingResponse;
    }

    public void setShoppingResponse(ShoppingResponse shoppingResponse)
    {
        m_shoppingResponse = shoppingResponse;
    }
}
