package com.hopper.tests.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hopper.tests.model.response.booking.BookingResponse;
import io.restassured.response.Response;
import org.junit.Assert;

import java.io.IOException;

/**
 * Parses Booking API Response.
 */
public class BookingResponseParser
{
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    public static BookingResponse parse(final Response apiResponse)
    {
        Assert.assertNotNull("Booking API response is null", apiResponse);

        try
        {
            return OBJECT_MAPPER.readValue(apiResponse.getBody().asString(), BookingResponse.class);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            System.out.println("Failed to parse Booking Response");
        }

        return null;
    }
}
