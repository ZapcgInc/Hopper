package com.hopper.tests.util.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hopper.tests.model.response.prebooking.PreBookingResponse;
import io.restassured.response.Response;
import org.junit.Assert;

import java.io.IOException;
/**
 * Parses PreBooking API Response.
 */
public class PreBookingResponseParser
{
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    public static PreBookingResponse parse(final Response apiResponse)
    {
        Assert.assertNotNull("Shopping API response is null", apiResponse);

        try
        {
            return OBJECT_MAPPER.readValue(apiResponse.getBody().asString(), PreBookingResponse.class);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            System.out.println("Failed to parse Shopping Response");
        }

        return null;
    }
}
