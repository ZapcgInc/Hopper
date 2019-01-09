package com.hopper.tests.util.data;

import com.hopper.tests.constants.RequestType;
import com.hopper.tests.model.TestContext;
import com.hopper.tests.util.APIEndPointGenerator;
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

    /*package-private*/ EPSResponseSupplier(final TestContext criteria,
                                            final String httpMethod,
                                            final RequestType requestType)
    {
        m_response = _getEPSResponse(criteria, httpMethod, requestType);
        Assert.assertNotNull("Something went wrong, API response is null", m_response);
    }

    private Response _getEPSResponse(final TestContext criteria,
                                     final String httpMethod,
                                     final RequestType requestType)
    {
        final RequestSpecification requestSpecifications = RestAssured.with().headers(criteria.getHeaders());

        if (criteria.getParams(requestType) != null)
        {
            requestSpecifications.queryParams(criteria.getParams(requestType));
        }

        if (criteria.getParamsWithMultipleValues(requestType) != null)
        {
            criteria.getParamsWithMultipleValues(requestType).forEach(requestSpecifications::queryParam);
        }

        if (criteria.LOGGING_ENABLED)
        {
            System.out.println(requestSpecifications.log().all());
        }

        try
        {
            switch (httpMethod)
            {
                case "GET":
                {
                    final String apiEndPoint = APIEndPointGenerator.create(criteria, requestType);
                    final Response response = requestSpecifications.get(apiEndPoint);

                    if (criteria.LOGGING_ENABLED)
                    {
                        System.out.println(response.asString());
                    }

                    return response;
                }
                default:
                {
                    Assert.fail("This type of method is not supported yet.");
                }
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
