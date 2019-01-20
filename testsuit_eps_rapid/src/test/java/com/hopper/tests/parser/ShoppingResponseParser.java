package com.hopper.tests.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hopper.tests.model.response.shopping.Property;
import com.hopper.tests.model.response.shopping.ShoppingResponse;
import io.restassured.response.Response;
import org.junit.Assert;

import java.io.IOException;

/**
 * Parses Shopping API Response.
 */
public class ShoppingResponseParser
{
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    public static ShoppingResponse parse(final Response apiResponse)
    {
        Assert.assertNotNull("Shopping API response is null", apiResponse);

        try
        {
            if (apiResponse.getStatusCode() == 200)
            {
                Property[] properties = OBJECT_MAPPER.readValue(apiResponse.getBody().asString(), Property[].class);
                return new ShoppingResponse(properties);
            }
            else
            {
                return OBJECT_MAPPER.readValue(apiResponse.getBody().asString(), ShoppingResponse.class);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
            System.out.println("Failed to parse Shopping Response");
        }

        return null;
    }
}
