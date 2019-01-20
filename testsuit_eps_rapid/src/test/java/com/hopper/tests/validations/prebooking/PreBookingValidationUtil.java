package com.hopper.tests.validations.prebooking;

import com.hopper.tests.constants.RequestType;
import com.hopper.tests.definitions.model.TestContext;
import com.hopper.tests.data.model.response.prebooking.PreBookingResponse;
import com.hopper.tests.validations.CommonValidationUtil;
import com.hopper.tests.validations.constants.ResponseValidationField;
import com.hopper.tests.validations.model.Range;
import org.junit.Assert;

import java.util.List;

/**
 * Util class for PreBooking Response Validations
 */
public class PreBookingValidationUtil
{
    public static void validate(final TestContext context, final ResponseValidationField validateField)
    {
        final PreBookingResponse response = context.getPreBookingResponse();
        Assert.assertNotNull(
                "PreBooking response is missing",
                response
        );

        switch (validateField)
        {
            case BOOKING_LINK:
            {
                _validateBookingLink(response);
                break;
            }
            case SHOPPING_LINK:
            {
                _validateShoppingLink(response);
                break;
            }
            case TOTAL_PRICE:
            {
                CommonValidationUtil.validateTotalPrice(response.getRoomPriceByOccupany().values());
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
        final PreBookingResponse response = context.getPreBookingResponse();
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
        final PreBookingResponse response = context.getPreBookingResponse();
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
        final PreBookingResponse response = context.getPreBookingResponse();
        Assert.assertNotNull(
                "PreBooking response is missing",
                response
        );

        switch (validateField)
        {
            case STATUS:
            {
                _validateStatus(response, expectedValues);
                break;
            }
            case OCCUPANCY:
            {
                CommonValidationUtil.occupancyValidator(response.getRoomPriceByOccupany(), expectedValues);
                break;
            }
            case STAY_PRICE_TYPE:
            {
                CommonValidationUtil.stayPriceTypeValidator(response.getRoomPriceByOccupany().values(), expectedValues);
                break;
            }
            case NIGHTLY_PRICE_TYPE:
            {
                CommonValidationUtil.nightlyPriceTypeValidator(response.getRoomPriceByOccupany().values(), expectedValues);
                break;
            }
            case NIGHTLY_PRICE_COUNT:
            {
                CommonValidationUtil.nightlyPriceCountValidator(response.getRoomPriceByOccupany().values(), expectedValues);
                break;
            }
            default:
            {
                throw new UnsupportedOperationException(validateField + ", not supported");
            }
        }
    }

    private static void _validateStatus(final PreBookingResponse response, final List<String> values)
    {
        Assert.assertTrue("status invalid", values.contains(response.getStatus()));
    }

    private static void _validateBookingLink(final PreBookingResponse response)
    {
        CommonValidationUtil.validateLink(
                response.getLinks().get("book"),
                RequestType.SHOPPING,
                "",
                "booking_link"
        );
    }

    private static void _validateShoppingLink(final PreBookingResponse response)
    {
        if ("sold_out".equals(response.getStatus()))
        {
            CommonValidationUtil.validateLink(
                    response.getLinks().get("shop"),
                    RequestType.SHOPPING,
                    "",
                    "booking_link"
            );
        }
    }
}
