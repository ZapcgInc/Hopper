package com.hopper.tests.util.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hopper.tests.model.response.booking.BookingRetrieveResponse;
import io.restassured.response.Response;
import org.junit.Assert;

import java.io.IOException;

/**
 * Parses Booking Retrieve API Response.
 */
public class BookingRetrieveResponseParser
{
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    public static BookingRetrieveResponse parse(final Response apiResponse)
    {
        Assert.assertNotNull("Booking API response is null", apiResponse);

        try
        {
            return OBJECT_MAPPER.readValue(apiResponse.getBody().asString(), BookingRetrieveResponse.class);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            System.out.println("Failed to parse Booking Response");
        }

        return null;
    }
}
