package com.hopper.tests.util.validations;

import io.restassured.response.Response;
import org.junit.Assert;

import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * Util class for Response Validations
 */
public class ResponseValidationUtil
{
    public static void validateHTTPResponseCode(@NotNull final Response restResponse, final int expectedCode)
    {
        final int responseCode = restResponse.getStatusCode();
        final String errorMessage = "Expected response code : " + expectedCode + "and actual response code : " + responseCode + "are not matching";
        Assert.assertTrue(errorMessage, expectedCode == responseCode);
    }


    public static void validateResponseBody(final Response restResponse, final Map<String, String> expectedResponseMap, final String field)
    {
        final Map<String, String> fieldResponseMap = restResponse.jsonPath().get(field);

        for (String key : expectedResponseMap.keySet())
        {
            final String actualValue = fieldResponseMap.get(key);
            final String expectedValue = expectedResponseMap.get(key);
            final String errorMessage = "For field" + field + "Expected value : " + expectedValue + "and actual value : " + actualValue + "are not matching";

            Assert.assertEquals(errorMessage, expectedValue, actualValue);
        }
    }
}
