package com.hopper.tests.util.validations;

import com.hopper.tests.constants.RequestType;
import com.hopper.tests.model.TestContext;
import com.hopper.tests.util.validations.constants.ResponseValidationField;
import org.junit.Assert;

import java.util.List;

/**
 * Util class for PreBooking Response Validations
 */
public class PreBookingValidationUtil
{
    public static void validateField(final TestContext context, final ResponseValidationField validateField, final List<String> expectedValues)
    {
        switch (validateField)
        {
            case STATUS:
            {
                _validateStatus(context, expectedValues);
                break;
            }
            case BOOKING_LINK:
            {
                _validateHrefInBook(context);
                break;
            }
            case SHOPPING_LINK:
            {
                _validateHrefInShop(context);
                break;
            }
            default:
            {
                throw new UnsupportedOperationException(validateField + ", not supported");
            }
        }
    }

    private static void _validateStatus(TestContext context, List<String> values)
    {
        Assert.assertTrue(
                "status invalid",
                values.contains(context.getPreBookingResponse().getStatus())
        );
    }

    private static void _validateHrefInBook(final TestContext context)
    {
        CommonValidationUtil.validateLink(
                context.getPreBookingResponse().getLinks().get("book"),
                RequestType.SHOPPING,
                "",
                "booking_link"
        );
    }

    private static void _validateHrefInShop(final TestContext context)
    {
        if ("sold_out".equals(context.getPreBookingResponse().getStatus()))
        {
            CommonValidationUtil.validateLink(
                    context.getPreBookingResponse().getLinks().get("shop"),
                    RequestType.SHOPPING,
                    "",
                    "booking_link"
            );
        }
    }
}
