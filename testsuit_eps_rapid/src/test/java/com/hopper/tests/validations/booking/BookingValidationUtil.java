package com.hopper.tests.validations.booking;

import com.hopper.tests.definitions.model.TestContext;
import com.hopper.tests.data.model.response.booking.BookingResponse;
import com.hopper.tests.validations.constants.ResponseValidationField;
import com.hopper.tests.validations.model.Range;
import org.apache.commons.lang.StringUtils;
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
            case ITINERARY_ID:
            {
                _validateItineraryId(response);
                break;
            }
            case LINKS:
            {
                _validateLinks(response);
                break;
            }
            case RETRIEVE_METHOD:
            {
                _validateRetrieveMethod(response);
                break;
            }
            case RETRIEVE_HREF:
            {
                _validateRetrieveHref(response);
                break;
            }
            case CANCEL_HREF:
            {
                _validateCancelHref(response);
                break;
            }
            default:
            {
                throw new UnsupportedOperationException("Validation Field [" + validateField + "] unsupported");
            }
        }
    }

    private static void _validateCancelHref(BookingResponse response)
    {
        if (response.getLinks().get("cancel") != null)
        {
            Assert.assertNotNull("href link is missing",
                                 response.getLinks().get("cancel").getHref()
            );
        }
    }

    private static void _validateRetrieveHref(BookingResponse response)
    {
        Assert.assertNotNull("href link is missing",
                             response.getLinks().get("retrieve").getHref()
        );
    }

    private static void _validateRetrieveMethod(BookingResponse response)
    {
        Assert.assertTrue("method value is not GET",
                          response.getLinks().get("retrieve").getMethod().equals("GET")
        );
    }

    private static void _validateLinks(BookingResponse response)
    {
        Assert.assertNotNull("links node is missing",
                             response.getLinks()
        );
    }

    private static void _validateItineraryId(BookingResponse response)
    {
        Assert.assertTrue("itinerary_id is missing",
                          !StringUtils.isEmpty(response.getItineraryId())
        );
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
        case CANCEL_METHOD:
            validateCancelMethod(expectedValues.get(0), response);
            break;
        default:
        {
            throw new UnsupportedOperationException(validateField + ", not supported");
        }
        }
    }

    private static void validateCancelMethod(String expectedValue, final BookingResponse response)
    {
        if (response.getLinks().get("cancel") != null)
            Assert.assertEquals("cancel is not DELETE", expectedValue, response.getLinks().get("cancel").getMethod());
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
