package com.hopper.tests.validations.booking;

import com.hopper.tests.model.TestContext;
import com.hopper.tests.model.response.booking.BookingResponse;
import com.hopper.tests.validations.constants.ResponseValidationField;
import com.hopper.tests.validations.model.Range;
import org.junit.Assert;

import java.util.List;

/**
 * Util class for Booking Response Validations
 */
public class BookingValidationUtil
{
    public static void validate(final TestContext context, final ResponseValidationField validateField)
    {
        final BookingResponse response = context.getBookingResponse();
        Assert.assertNotNull(
                "PreBooking response is missing",
                response
        );

        switch (validateField)
        {
            case RETRIEVE_BOOKING_LINK:
            {
                _validateRetrieveBookingLink(response);
                break;
            }
            case RESUME_BOOKING_LINK:
            {
                _validateResumeBookingLink(response);
                break;
            }
            default:
            {
                throw new UnsupportedOperationException("Validation Field [" + validateField + "] unsupported");
            }
        }
    }

    public static void validate(final TestContext context, final ResponseValidationField validateField, final int count)
    {
        final BookingResponse response = context.getBookingResponse();
        Assert.assertNotNull(
                "PreBooking response is missing",
                response
        );

        switch (validateField)
        {
            default:
            {
                throw new UnsupportedOperationException("Validation Field [" + validateField + "] unsupported");
            }
        }
    }

    public static void validate(final TestContext context, final ResponseValidationField validateField, Range<Integer> expectedRange)
    {
        final BookingResponse response = context.getBookingResponse();
        Assert.assertNotNull(
                "PreBooking response is missing",
                response
        );

        switch (validateField)
        {
            default:
            {
                throw new UnsupportedOperationException(validateField + ", not supported");
            }
        }
    }

    public static void validate(final TestContext context, final ResponseValidationField validateField, final List<String> expectedValues)
    {
        final BookingResponse response = context.getBookingResponse();
        Assert.assertNotNull(
                "PreBooking response is missing",
                response
        );

        switch (validateField)
        {
            default:
            {
                throw new UnsupportedOperationException(validateField + ", not supported");
            }
        }
    }


    private static void _validateRetrieveBookingLink(final BookingResponse response)
    {
        Assert.assertTrue(
                "No Retrieve Booking link",
                response.getLinks().containsKey("retrieve")
        );
    }

    private static void _validateResumeBookingLink(BookingResponse response)
    {
        Assert.assertTrue(
                "No Retrieve Booking link",
                response.getLinks().containsKey("resume")
        );
    }
}
