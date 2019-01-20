package com.hopper.tests.util.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hopper.tests.model.response.payment.PaymentOptionResponse;
import io.restassured.response.Response;
import org.junit.Assert;

import java.io.IOException;

/**
 * Parses PaymentOptions API Response.
 */
public class PaymentOptionResponseParser
{
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    public static PaymentOptionResponse parse(final Response apiResponse)
    {
        Assert.assertNotNull("Payment Options API response is null", apiResponse);

        try
        {
            return OBJECT_MAPPER.readValue(apiResponse.getBody().asString(), PaymentOptionResponse.class);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            System.out.println("Failed to parse Payment Options Response");
        }

        return null;
    }
}
