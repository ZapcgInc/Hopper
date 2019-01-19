package com.hopper.tests.util.validations;

import com.hopper.tests.constants.RequestType;
import com.hopper.tests.model.response.BedGroups;
import com.hopper.tests.model.response.Property;
import com.hopper.tests.model.response.Rate;
import com.hopper.tests.model.response.Room;
import com.hopper.tests.model.TestContext;
import com.hopper.tests.model.response.shopping.CancelPolicies;
import com.hopper.tests.util.validations.constants.ResponseValidationField;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Util class for Shopping Response Validations
 */
public class ShoppingResponseValidationUtil
{

    public static void validate(final TestContext context, final ResponseValidationField validateField, final List<String> expectedValues)
    {
        switch (validateField)
        {
            case PRICE_CHECK_LINK:
            {
                _validateRateFields(context, ResponseValidationField.PRICE_CHECK_LINK, expectedValues);
                break;
            }
            case PAYMENT_OPTIONS_LINK:
            {
                _validateRateFields(context, ResponseValidationField.PAYMENT_OPTIONS_LINK, expectedValues);
                break;
            }
            case DEPOSIT_POLICIES_LINK:
            {
                _validateRateFields(context, ResponseValidationField.DEPOSIT_POLICIES_LINK, expectedValues);
                break;
            }
            case CANCEL_PENALTIES:
            {
                _validateRateFields(context, ResponseValidationField.CANCEL_PENALTIES, expectedValues);
                break;
            }
            case AVAILABLE_ROOMS:
            {
                _validateRateFields(context, ResponseValidationField.AVAILABLE_ROOMS, expectedValues);
                break;
            }
            case AMENITIES:
            {
                _validateRateFields(context, ResponseValidationField.AMENITIES, expectedValues);
                break;
            }
            default:
            {
                throw new UnsupportedOperationException("Validation Field [" + validateField + "] unsupported");
            }
        }
    }

    private static void _validateRateFields(final TestContext context, ResponseValidationField validatorField, final List<String> expectedValues)
    {
        for (Property property : context.getShoppingResponse().getProperties())
        {
            for (Room room : property.getRooms())
            {
                for (Rate rate : room.getRates())
                {
                    switch (validatorField)
                    {
                        case MERCHANT_OF_RECORD:
                        {
                            _validateMerchantOfRecord(rate, room.getId(), expectedValues);
                        }
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
                            _availableRoomsValidator(rate, room.getId(), expectedValues.get(0));
                            break;
                        }
                        case AMENITIES:
                        {
                            _amenitiesValidator(rate, room.getId());
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

    private static void _amenitiesValidator(Rate rate, String roomID)
    {
        if (rate.getAmenities().isEmpty())
        {
            return;
        }

        rate.getAmenities()
                .forEach(amenity ->
                {
                    if ((StringUtils.isNotEmpty(amenity.getId()) && StringUtils.isEmpty(amenity.getName()))
                            || (StringUtils.isEmpty(amenity.getId()) && StringUtils.isNotEmpty(amenity.getName())))
                    {
                        Assert.fail("amenity ID and description both should be present or both should be absent for a valid response, for room ID : [" + roomID + "]");
                    }
                });
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
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try
        {
            if (sdf.parse(startDate).before(sdf.parse(checkin)) && sdf.parse(endDate).before(sdf.parse(checkout)))
            {
                return true;
            }
        }
        catch (ParseException e)
        {
            Assert.fail("Unable to Parse dates");
        }

        return false;
    }

    private static void _priceCheckLinkValidator(final Rate rate, final String roomId)
    {
        for (BedGroups bedGroup : rate.getBedGroups())
        {
            CommonValidationUtil.validateLink(
                    bedGroup.getLinks().get("price_check"),
                    RequestType.SHOPPING,
                    "room ID" + roomId,
                    "price_check"
            );
        }
    }

    private static void _paymentOptionsLinkValidator(final Rate rate, final String roomId)
    {
        CommonValidationUtil.validateLink(
                rate.getLinks().get("payment_options"),
                RequestType.SHOPPING,
                "room ID" + roomId,
                "payment_options"
        );
    }

    private static void _depositPoliciesLinkValidator(final Rate rate, final String roomId)
    {
        if (rate.isDepositRequired())
        {
            CommonValidationUtil.validateLink(
                    rate.getLinks().get("deposit_policies"),
                    RequestType.SHOPPING,
                    "room ID" + roomId,
                    "deposit_policies"
            );

        }
    }

    private static void _validateMerchantOfRecord(final Rate rate, final String roomId, List<String> expectedValues)
    {
        if (!expectedValues.contains(rate.getMerchantOfRecord()))
        {
            Assert.fail(" merchant record field does not match any of the expected values for roomId :[" + roomId + "]");
        }
    }

    private static void _validatePropertyId(TestContext testContext)
    {
        List<String> requestPropertyIds = testContext.getParamValues(RequestType.SHOPPING, "property_id");

        if (requestPropertyIds == null || requestPropertyIds.isEmpty())
        {
            return;
        }

        List<String> responsePropertyIds = testContext.getShoppingResponse().getProperties()
                .stream()
                .map(Property::getPropertyId)
                .collect(Collectors.toList());


        if (responsePropertyIds.size() <= requestPropertyIds.size())
        {
            responsePropertyIds.forEach(id ->
            {
                if (!requestPropertyIds.contains(id))
                {
                    Assert.fail("The propertyId: " + id + " is not present in the request");
                }
            });
        }
        else
        {
            Assert.fail("Property Ids in the response is more than the requested response");
        }
    }
}
