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
    private static final String RESPONSE_CODE_VALIDATION_ERR_MSG = "Expected response code : %d and actual response code : %d are not matching";

    public static void validateHTTPResponseCode(@NotNull final Response restResponse, final int expectedCode)
    {
        final int responseCode = restResponse.getStatusCode();
        final String errorMessage = String.format(RESPONSE_CODE_VALIDATION_ERR_MSG, expectedCode, responseCode);
        Assert.assertTrue(errorMessage, expectedCode == responseCode);
    }


    public static void validateResponseBody(final Response restResponse, final Map<String, String> expectedResponseMap, final String field)
    {
        final Map<String, String> fieldResponseMap = restResponse.jsonPath().get(field);

        for (String key : expectedResponseMap.keySet())
        {
            final String actualValue = fieldResponseMap.get(key);
            final String expectedValue = expectedResponseMap.get(key);
            Assert.assertTrue(expectedValue.equals(actualValue));
        }
    }
}
