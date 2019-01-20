package com.hopper.tests.util.data;

import com.hopper.tests.constants.GlobalConstants;
import com.hopper.tests.constants.RequestType;
import com.hopper.tests.model.TestContext;
import com.hopper.tests.util.api.APIEndPointGenerator;
import com.hopper.tests.util.logging.LoggingUtil;
import io.restassured.RestAssured;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import org.apache.commons.lang.RandomStringUtils;
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

    private Response _getEPSResponse(final TestContext context,
                                     final String httpMethod,
                                     final RequestType requestType)
    {
        //final RequestSpecification requestSpecifications = RestAssured.with().headers(context.getHeaders());
        final RequestSpecification requestSpecifications = onrequest().with().headers(context.getHeaders());
        if (context.getParams(requestType) != null)
        {
            requestSpecifications.queryParams(context.getParams(requestType));
        }

        if (context.getParamsWithMultipleValues(requestType) != null)
        {
            context.getParamsWithMultipleValues(requestType).forEach(requestSpecifications::queryParam);
        }

        if (TestContext.LOGGING_ENABLED)
        {
            System.out.println(requestSpecifications.log().all());
        }

        try
        {
            switch (httpMethod)
            {
                case "GET":
                {
                    final String apiEndPoint = APIEndPointGenerator.create(context, requestType);
                    final Response response = requestSpecifications.get(apiEndPoint);

                    if (TestContext.LOGGING_ENABLED)
                    {
                        System.out.println(response.asString());
                    }
                    return response;
                }
                case "POST":
                {

                    final String apiEndPoint = APIEndPointGenerator.create(context, requestType);
                    final Response response = RestAssured.given()
                            .headers(context.getHeaders())
                            .contentType("application/json")
                            .body(context.getPostBody(requestType))
                            .post(apiEndPoint);

                    if (TestContext.LOGGING_ENABLED)
                    {
                        System.out.println(response.asString());
                    }

                    return response;
                }
                case "DELETE":
                {
                    final String apiEndPoint = APIEndPointGenerator.create(context, requestType);
                    final Response response = RestAssured.given()
                            .headers(context.getHeaders())
                            .contentType("application/json")
                            .delete(apiEndPoint);

                    if (TestContext.LOGGING_ENABLED)
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
            System.out.println(e.getMessage());
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

    /**
     * initialize a RequestSpecification default timeout
     */
    private RequestSpecification onrequest()
    {
        HttpClientConfig httpConfig = HttpClientConfig.httpClientConfig();
        httpConfig.setParam("http.socket.timeout", GlobalConstants.SOCKET_TIMEOUT);

        return RestAssured.given()
                .config(RestAssuredConfig.config().httpClient(httpConfig));
    }
}
