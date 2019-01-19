package com.hopper.tests.util.validations;

import com.hopper.tests.constants.RequestType;
import com.hopper.tests.model.response.BedGroups;
import com.hopper.tests.model.response.Property;
import com.hopper.tests.model.response.Rate;
import com.hopper.tests.model.response.Room;
import com.hopper.tests.model.TestContext;
import com.hopper.tests.model.response.shopping.CancelPolicies;
import io.restassured.response.Response;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;

/**
 * Util class for Shopping Response Validations
 */
public class ShoppingResponseValidationUtil
{
    enum ValidatorField
    {
        PRICE_CHECK_LINK,
        PAYMENT_OPTIONS_LINK,
        DEPOSIT_POLICIES_LINK,
        CANCEL_PENALTIES,
        AVAILABLE_ROOMS,
        ;
    }

    public static void validateAvailableRooms(final RequestType requestType, final TestContext context, String maxValue)
    {
        if (requestType == RequestType.SHOPPING)
        {
            List<Property> properties = context.getShoppingResponse().getProperties();
            for (Property property : properties)
            {
                if (property.getRooms() != null)
                {
                    for (Room room : property.getRooms())
                    {
                        if (room.getRates() != null)
                        {
                            for (Rate rate : room.getRates())
                            {
                                int numAvailableRooms = rate.getAvailableRooms();
                                if (numAvailableRooms <= 0 || numAvailableRooms > Integer.parseInt(maxValue))
                                {
                                    Assert.fail("Number of available rooms in the response is invalid for room_id: " + room.getId());
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public static void validateFieldValueNotEqualTo(final TestContext context, String validateField, String maxValue)
    {
        switch (validateField)
        {
            case "price_check_href":
            {
                _validateRateFields(context, ValidatorField.PRICE_CHECK_LINK, maxValue);
                break;
            }
            case "payment_option_href":
            {
                _validateRateFields(context, ValidatorField.PAYMENT_OPTIONS_LINK, maxValue);
                break;
            }
            case "links.deposit_policies":
            {
                _validateRateFields(context, ValidatorField.DEPOSIT_POLICIES_LINK, maxValue);
                break;
            }
            case "links.shop.href":
            {
                _validateHrefInShop(context.getResponse(RequestType.SHOPPING));
                break;
            }
            case "cancel_penalties" :
            {
                _validateRateFields(context, ValidatorField.CANCEL_PENALTIES, maxValue);
                break;
            }
            case "availability":
            {
                _validateRateFields(context, ValidatorField.AVAILABLE_ROOMS, maxValue);
                break;
            }
        }
    }

    private static void _validateRateFields(final TestContext context, ValidatorField validatorField, String maxValue)
    {
        for (Property property : context.getShoppingResponse().getProperties())
        {
            for (Room room : property.getRooms())
            {
                for (Rate rate : room.getRates())
                {
                    switch (validatorField)
                    {
                        case PRICE_CHECK_LINK:
                        {
                            _priceCheckLinkValidator(rate, room.getId());
                            break;
                        }
                        case PAYMENT_OPTIONS_LINK:
                        {
                            _paymentOptionsLinkValidator(rate, room.getId());
                            break;
                        }
                        case DEPOSIT_POLICIES_LINK:
                        {
                            _depositPoliciesLinkValidator(rate, room.getId());
                            break;
                        }
                        case CANCEL_PENALTIES:
                        {
                            _cancelPenaltiesValidator(rate, room.getId(), context);
                            break;
                        }
                        case AVAILABLE_ROOMS:
                        {
                            _availableRoomsValidator(rate, room.getId(), maxValue);
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

    private static void _availableRoomsValidator(Rate rate, String roomID, String maxValue)
    {
        final int numAvailableRooms = rate.getAvailableRooms();
        if (numAvailableRooms <= 0 || numAvailableRooms > Integer.parseInt(maxValue))
        {
            Assert.fail("Number of available rooms in the response is invalid for room_id: [" + roomID + "]");
        }
    }

    private static void _cancelPenaltiesValidator(Rate rate, String roomID, TestContext context)
    {
        final String checkInDate = context.getCheckInDate();
        final String checkOutDate = context.getCheckOutDate();

        if (rate.isRefundable())
        {
            Assert.assertFalse("Cancel Penalties missing for Room : [" + roomID + "]",
                    rate.getCancelPolicies().isEmpty());

            for (CancelPolicies cancelPolicies : rate.getCancelPolicies())
            {
                String startDate = cancelPolicies.getStart();
                String endDate = cancelPolicies.getEnd();

                if (!validateStartEndDate(checkInDate, checkOutDate, startDate, endDate))
                {
                    Assert.fail("cancel policy start and end date are not within check in and check " +
                            "out dates for roomId :[" + roomID + "]");
                }

            }
        }

    }

    // TODO: clean up this code.
    private static boolean validateStartEndDate(String checkin, String checkout, String startDate, String endDate)
    {
        boolean flag = false;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try
        {
            if (sdf.parse(startDate).before(sdf.parse(checkin)) && sdf.parse(endDate).before(sdf.parse(checkout)))
            {
                flag = true;
            }
        }
        catch (ParseException e)
        {
            Assert.fail("Unable to Parse dates");
        }
        return flag;
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
