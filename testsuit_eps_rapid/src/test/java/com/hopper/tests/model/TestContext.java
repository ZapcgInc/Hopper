package com.hopper.tests.model;

import com.hopper.tests.constants.RequestType;
import com.hopper.tests.constants.SupportedPartners;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Simple POJO to hold test criteria
 */
public class TestContext
{
    public static final boolean LOGGING_ENABLED = false;
    private static final String AUTH_HEADER_KEY = "Authorization";
    private static final String CHECKIN_DATE_KEY = "checkin";
    private static final String CHECKOUT_DATE_KEY = "checkout";

    private final SupportedPartners m_partner;

    private String m_host;
    private String m_version;
    private Map<String, String> m_headers = new HashMap<>();
    private SimpleDateFormat m_requestDateFormat;

    private EnumMap<RequestType, String> m_requestTypeToAPIPath = new EnumMap<RequestType, String>(RequestType.class);
    private EnumMap<RequestType, RequestParams> m_requestTypeToQueryParams = new EnumMap<RequestType, RequestParams>(RequestType.class);
    private EnumMap<RequestType, Response> m_requestTypeToResponse = new EnumMap<RequestType, Response>(RequestType.class);
    

    public TestContext(final SupportedPartners partner)
    {
        m_partner = partner;
    }

    public SupportedPartners getPartner()
    {
        return m_partner;
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

    public SimpleDateFormat getRequestDateFormat()
    {
        return m_requestDateFormat;
    }

    public void setRequestDateFormat(SimpleDateFormat requestDateFormat)
    {
        m_requestDateFormat = requestDateFormat;
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

    public Response getResponse(RequestType requestType)
    {
        return m_requestTypeToResponse.get(requestType);
    }
    
    public void setResponse(RequestType requestType, Response response)
    {
        m_requestTypeToResponse.put(requestType, response);
    }
    /* START - Request Query Params */

    public void addCheckInDate(Calendar date, RequestType requestType)
    {
        addParam(CHECKIN_DATE_KEY, _convertToDate(date), requestType);
    }

    public void addCheckOutDate(Calendar date, RequestType requestType)
    {
        addParam(CHECKOUT_DATE_KEY, _convertToDate(date), requestType);
    }

    public void setParams(Map<String, String> params, RequestType requestType)
    {
        _initRequestParams(requestType);
        m_requestTypeToQueryParams.get(requestType).setParams(params);
    }

    public void addParam(String header, String value, RequestType requestType)
    {
        _initRequestParams(requestType);
        m_requestTypeToQueryParams.get(requestType).addParam(header, value);
    }

    public void removeParam(String header, RequestType requestType)
    {
        Optional.ofNullable(m_requestTypeToQueryParams.get(requestType))
                .ifPresent(params -> params.removeParam(header));
    }

    public void addParamWithMultipleValues(String header, List<String> multipleValues, RequestType requestType)
    {
        _initRequestParams(requestType);
        m_requestTypeToQueryParams.get(requestType).addParamWithMultipleValues(header, multipleValues);
    }

    public void removeParamWithMultipleValues(String header, RequestType requestType)
    {
        Optional.ofNullable(m_requestTypeToQueryParams.get(requestType))
                .ifPresent(params -> params.removeParamWithMultipleValues(header));
    }

    public Map<String, List<String>> getParamsWithMultipleValues(RequestType requestType)
    {
        return m_requestTypeToQueryParams.containsKey(requestType)
                ? m_requestTypeToQueryParams.get(requestType).getParamsWithMultipleValues()
                : null;
    }

    public Map<String, String> getParams(RequestType requestType)
    {
        return m_requestTypeToQueryParams.containsKey(requestType)
                ? m_requestTypeToQueryParams.get(requestType).getParams()
                : null;
    }


    private void _initRequestParams(RequestType requestType)
    {
        if (!m_requestTypeToQueryParams.containsKey(requestType))
        {
            m_requestTypeToQueryParams.put(requestType, new RequestParams());
        }
    }
    /* END - Request Query Params */

    private String _convertToDate(final Calendar dateTime)
    {
        return m_requestDateFormat.format(dateTime.getTime());
    }
}
