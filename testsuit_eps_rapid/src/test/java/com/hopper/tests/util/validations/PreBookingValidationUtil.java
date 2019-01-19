package com.hopper.tests.util.validations;

import com.hopper.tests.constants.RequestType;
import com.hopper.tests.model.TestContext;
import io.restassured.response.Response;
import org.junit.Assert;

import java.util.HashMap;

public class PreBookingValidationUtil
{
    enum ValidatorField
    {
        BOOKING_LINK,
        ;
    }

    public static void validateField(final TestContext context, String validateField)
    {
        switch (validateField)
        {
            case "links.book.href":
            {
                _validateHrefInBook(context.getResponse(RequestType.PRE_BOOKING));
                break;
            }
            default:
            {
                throw new UnsupportedOperationException(validateField + ", not supported");
            }
        }
    }
    public static void _validateHrefInBook(Response restResponse)
    {
        HashMap<String, HashMap<String, HashMap>> roomPriceCheckMap = restResponse.jsonPath().get(".");
        String hrefShop = (String) roomPriceCheckMap.get("links").get("book").get("href");
        Assert.assertTrue("href is not found", hrefShop != null);
    }

}
