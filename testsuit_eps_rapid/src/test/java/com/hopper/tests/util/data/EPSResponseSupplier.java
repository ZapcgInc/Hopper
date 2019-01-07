package com.hopper.tests.util.data;

import com.hopper.tests.model.EPSRequest;
import com.hopper.tests.model.TestCriteria;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.Assert;

import java.util.function.Supplier;

/**
 * Calls EPS API and returns a response.
 */
public class EPSResponseSupplier implements Supplier<Response>
{
    private final Response m_response;

    static
    {
        // Sets the default parser to JSON.
        io.restassured.RestAssured.defaultParser = io.restassured.parsing.Parser.JSON;
    }

    /*package-private*/ EPSResponseSupplier(TestCriteria criteria, String httpMethod)
    {
        m_response = _getEPSResponse(criteria, httpMethod);
    }

    private Response _getEPSResponse(TestCriteria criteria, String httpMethod)
    {
        final EPSRequest request = new EPSRequest(criteria);
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
                    return requestSpecifications.get(request.getEndPoint());
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

        return null;
    }

    @Override
    public Response get()
    {
        return m_response;
    }
}
