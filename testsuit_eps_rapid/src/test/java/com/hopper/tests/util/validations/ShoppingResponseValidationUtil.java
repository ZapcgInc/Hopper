package com.hopper.tests.util.validations;

import com.hopper.tests.constants.RequestType;
import com.hopper.tests.model.BedGroups;
import com.hopper.tests.model.Property;
import com.hopper.tests.model.Rate;
import com.hopper.tests.model.Room;
import com.hopper.tests.model.TestContext;
import io.restassured.response.Response;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;

import java.util.HashMap;

/**
 * Util class for Shopping Response Validations
 */
public class ShoppingResponseValidationUtil
{
    enum LinkValidatorField
    {
        PRICE_CHECK,
        PAYMENT_OPTIONS,
        DEPOSIT_POLICIES,
        ;
    }

    public static void validateFieldValueNotEqualTo(final TestContext context, String validateField, String value)
    {
        switch (validateField)
        {
            case "price_check_href":
            {
                _validateLink(context, LinkValidatorField.PRICE_CHECK);
                break;
            }
            case "payment_option_href":
            {
                _validateLink(context, LinkValidatorField.PAYMENT_OPTIONS);
                break;
            }
            case "links.deposit_policies":
            {
                _validateLink(context, LinkValidatorField.DEPOSIT_POLICIES);
                break;
            }
            case "links.shop.href":
            {
                _validateHrefInShop(context.getResponse(RequestType.SHOPPING));
                break;
            }
        }
    }

    private static void _validateLink(final TestContext context, LinkValidatorField validatorField)
    {
        for (Property property : context.getShoppingResponse().getProperties())
        {
            for (Room room : property.getRooms())
            {
                for (Rate rate : room.getRates())
                {
                    switch (validatorField)
                    {
                        case PRICE_CHECK:
                        {
                            _priceCheckLinkValidator(rate, room.getId());
                            break;
                        }
                        case PAYMENT_OPTIONS:
                        {
                            _paymentOptionsLinkValidator(rate, room.getId());
                            break;
                        }
                        case DEPOSIT_POLICIES:
                        {
                            _depositPoliciesLinkValidator(rate, room.getId());
                            break;
                        }
                        default:
                        {
                            throw new UnsupportedOperationException(validatorField.name() + ", currently not supported.");
                        }
                    }
                }
            }
        }
    }

    private static void _priceCheckLinkValidator(final Rate rate, final String roomId)
    {
        for (BedGroups bedGroup : rate.getBedGroups())
        {
            Assert.assertTrue("Response doesn't contain Price Check Link for Room Id" + roomId, bedGroup.getLinks().containsKey("price_check"));
            Assert.assertTrue("Price Check Method empty of Room Id" + roomId, StringUtils.isNotEmpty(bedGroup.getLinks().get("price_check").getMethod()));
            Assert.assertTrue("Price Check HREF empty of Room Id" + roomId, StringUtils.isNotEmpty(bedGroup.getLinks().get("price_check").getHref()));
        }
    }

    private static void _paymentOptionsLinkValidator(final Rate rate, final String roomId)
    {
        Assert.assertTrue("Response doesn't contain Payment Options Link for Room Id" + roomId, rate.getLinks().containsKey("payment_options"));
        Assert.assertTrue("Payment Options Method empty of Room Id" + roomId, StringUtils.isNotEmpty(rate.getLinks().get("payment_options").getMethod()));
        Assert.assertTrue("Payment Options HREF empty of Room Id" + roomId, StringUtils.isNotEmpty(rate.getLinks().get("payment_options").getHref()));
    }

    private static void _depositPoliciesLinkValidator(final Rate rate, final String roomId)
    {
        Assert.assertTrue("Response doesn't contain Deposit Policies Link for Room Id" + roomId, rate.getLinks().containsKey("payment_options"));
        Assert.assertTrue("Deposit Policies Method empty of Room Id" + roomId, StringUtils.isNotEmpty(rate.getLinks().get("payment_options").getMethod()));
        Assert.assertTrue("Deposit Policies HREF empty of Room Id" + roomId, StringUtils.isNotEmpty(rate.getLinks().get("payment_options").getHref()));
    }

    private static void _validateHrefInShop(Response restResponse)
    {
        HashMap<String, HashMap<String, HashMap>> roomPriceCheckMap = restResponse.jsonPath().get(".");
        if ("sold_out".equals(roomPriceCheckMap.get("status")))
        {
            String hrefShop = (String) roomPriceCheckMap.get("links").get("shop").get("href");
            Assert.assertTrue("href is not found", hrefShop != null);
        }
    }
}
