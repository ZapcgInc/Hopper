package com.hopper.tests.model;

import java.util.Map;

/**
 * Value Object to hold EPS Request Values.
 */
public class EPSRequest
{
    private final String m_host;
    private final String m_version;
    private final String m_apiPath;
    private final String m_authKey;
    private final Map<String, String> m_headers;
    private final Map<String, String> m_params;

    public EPSRequest(TestCriteria criteria)
    {
        m_host = criteria.getHost();
        m_version = criteria.getVersion();
        m_apiPath = criteria.getApiPath();
        m_authKey = criteria.getAuthKey();
        m_headers = criteria.getHeaders();
        m_params = criteria.getParams();
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

}
