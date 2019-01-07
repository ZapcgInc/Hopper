package com.hopper.tests.util.data;

import com.hopper.tests.model.ShoppingRequest;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.Assert;

import java.util.Map;

/**
 * 1. Creates a Request based on the Defined test conditions.
 * 2. Calls the API and parses the response.
 */
public class ShoppingTestDataSupplier
{
    private final ShoppingRequest.Builder m_requestBuilder;
    private boolean m_appRunning;
    private Response m_response = null;

    static
    {
        // Sets the default parser to JSON.
        // TODO: should be configurable by partner.
        io.restassured.RestAssured.defaultParser = io.restassured.parsing.Parser.JSON;
    }

    public ShoppingTestDataSupplier()
    {
        m_requestBuilder = ShoppingRequest.builder();
    }

    /***Start Request Builder***/
    public void withRequestHost(String url)
    {
        m_requestBuilder.withHost(url);
    }


    public void withRequestVersion(String version)
    {
        m_requestBuilder.withVersion(version);
    }


    public void withRequestAPIPath(String uri)
    {
        m_requestBuilder.withAPIPath(uri);
    }


    public void withRequestAuthKey(String authKey)
    {
        m_requestBuilder.withAuthKey(authKey);
    }


    public void withRequestHeaders(Map<String, String> headers)
    {
        m_requestBuilder.withHeaders(headers);
    }

    public void withRequestParams(Map<String, String> params)
    {
        m_requestBuilder.withParams(params);
    }

    public void addRequestHeaders(String key, String value)
    {
        m_requestBuilder.addHeaders(key, value);
    }

    public void removeRequestHeader(String key)
    {
        m_requestBuilder.removeHeader(key);
    }

    public void addRequestParam(String key, String value)
    {
        m_requestBuilder.addParam(key, value);
    }

    public void removeRequestParam(String key)
    {
        m_requestBuilder.removeParam(key);
    }
    /***End Request Builder***/

    /**
     * Calls the request end point and saves the response for further validations.
     */
    public void callEndPoint(String httpMethod)
    {
        final ShoppingRequest request = m_requestBuilder.build();
        final RequestSpecification requestSpecifications = RestAssured.with().headers(request.getHeaders());

        if (request.getParams() != null)
        {
            requestSpecifications.queryParams(request.getParams());
        }

        try
        {
            switch (httpMethod)
            {
                case "GET":
                    m_response = requestSpecifications.get(request.getEndPoint());
                    break;
                default:
                    Assert.fail("This type of method is not supported yet.");
            }
        }
        catch (Exception e)
        {
            if (e.getMessage().contains("Connection refused"))
            {
                Assert.fail("Web application is not running.");
            }
            else
            {
                Assert.fail("Unknown exception while invoking this request.");
                e.printStackTrace();
            }
        }
    }

    /**
     * Checks if the API is available and actively servicing requests.
     */
    public void checkAppIsRunning()
    {
        if (!m_appRunning)
        {
            try
            {
                final ShoppingRequest request = m_requestBuilder.build();
                m_response = RestAssured.with().get(request.getHost());
            }
            catch (Exception e)
            {
                if (e.getMessage().contains("Connection refused"))
                {
                    Assert.fail("ERROR :Web application is not running. So cannot test functionality.");
                    return;
                }
                else
                {
                    e.printStackTrace();
                }
            }
            m_appRunning = true;
        }
    }

    public Response getResponse()
    {
        return m_response;
    }
}
