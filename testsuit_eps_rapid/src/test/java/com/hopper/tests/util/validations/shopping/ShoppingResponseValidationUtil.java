package com.hopper.tests.util.validations.shopping;

import com.hopper.tests.constants.RequestType;
import com.hopper.tests.model.response.shopping.BedGroups;
import com.hopper.tests.model.response.shopping.Property;
import com.hopper.tests.model.response.shopping.Rate;
import com.hopper.tests.model.response.shopping.Room;
import com.hopper.tests.model.TestContext;
import com.hopper.tests.model.response.CancelPolicies;
import com.hopper.tests.util.validations.CommonValidationUtil;
import com.hopper.tests.util.validations.constants.ResponseValidationField;
import com.hopper.tests.util.validations.model.Range;
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
    public static void validate(final TestContext context, final ResponseValidationField validationField)
    {
        switch (validationField)
        {
            case PRICE_CHECK_LINK:
            {
                _validateRateFields(context, ResponseValidationField.PRICE_CHECK_LINK, null, null);
                break;
            }
            case PAYMENT_OPTIONS_LINK:
            {
                _validateRateFields(context, ResponseValidationField.PAYMENT_OPTIONS_LINK, null, null);
                break;
            }
            case DEPOSIT_POLICIES_LINK:
            {
                _validateRateFields(context, ResponseValidationField.DEPOSIT_POLICIES_LINK, null, null);
                break;
            }
            case CANCEL_PENALTIES:
            {
                _validateRateFields(context, ResponseValidationField.CANCEL_PENALTIES, null, null);
                break;
            }
            case AMENITIES:
            {
                _validateRateFields(context, ResponseValidationField.AMENITIES, null, null);
                break;
            }
            case TOTAL_PRICE:
            {
                _validateRateFields(context, ResponseValidationField.TOTAL_PRICE, null, null);
                break;
            }
            default:
            {
                throw new UnsupportedOperationException("field [" + validationField.name() + "], not supported.");
            }
        }
    }


    public static void validate(final TestContext context, final ResponseValidationField validateField, final int count)
    {
        switch (validateField)
        {
            case PROPERTY_LINK_COUNT:
            {
                _propertyLinkCountValidator(context, count);
                break;
            }
            case ROOM_COUNT:
            {
                _roomCountValidator(context, count);
                break;
            }
            case ROOM_RATES_COUNT:
            {
                _roomRatesCountValidator(context, count);
                break;
            }
            default:
            {
                throw new UnsupportedOperationException("Validation Field [" + validateField + "] unsupported");
            }
        }
    }

    public static void validate(final TestContext context, final ResponseValidationField validateField, final Range<Integer> expectedRange)
    {
        switch (validateField)
        {
            case AVAILABLE_ROOMS:
            {
                _validateRateFields(context, ResponseValidationField.AVAILABLE_ROOMS, null, expectedRange);
                break;
            }
            default:
            {
                throw new UnsupportedOperationException("Validation Field [" + validateField + "] unsupported");
            }
        }
    }

    public static void validate(final TestContext context, final ResponseValidationField validateField, final List<String> expectedValues)
    {
        switch (validateField)
        {
            case OCCUPANCY:
            {
                _validateRateFields(context, ResponseValidationField.OCCUPANCY, expectedValues, null);
                break;
            }
            case FENCED_DEAL:
            {
                _validateRateFields(context, ResponseValidationField.FENCED_DEAL, expectedValues, null);
                break;
            }
            case STAY_PRICE_TYPE:
            {
                _validateRateFields(context, ResponseValidationField.STAY_PRICE_TYPE, expectedValues, null);
                break;
            }
            case MERCHANT_OF_RECORD:
            {
                _validateRateFields(context, ResponseValidationField.MERCHANT_OF_RECORD, expectedValues, null);
                break;
            }
            case NIGHTLY_PRICE_TYPE:
            {
                _validateRateFields(context, ResponseValidationField.NIGHTLY_PRICE_TYPE, expectedValues, null);
                break;
            }
            case NIGHTLY_PRICE_COUNT:
            {
                _validateRateFields(context, ResponseValidationField.NIGHTLY_PRICE_COUNT, expectedValues, null);
                break;
            }
            case CURRENCY_CODE:
            {
                _validateRateFields(context, ResponseValidationField.CURRENCY_CODE, expectedValues, null);
                break;
            }
            case BILLABLE_CURRENCY:
            {
                _validateRateFields(context, ResponseValidationField.BILLABLE_CURRENCY, expectedValues, null);
                break;
            }
            default:
            {
                throw new UnsupportedOperationException("Validation Field [" + validateField + "] unsupported");
            }
        }
    }

    private static void _validateRateFields(final TestContext context, ResponseValidationField validatorField, final List<String> expectedValues, final Range<Integer> expectedRange)
    {
        for (Property property : context.getShoppingResponse().getProperties())
        {
            // TODO: add room and rate empty checks.
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
                            _availableRoomsValidator(rate, room.getId(), expectedRange);
                            break;
                        }
                        case AMENITIES:
                        {
                            _amenitiesValidator(rate, room.getId());
                            break;
                        }
                        case OCCUPANCY:
                        {
                            CommonValidationUtil.occupancyValidator(rate.getRoomPriceByOccupancy(), expectedValues);
                            break;
                        }
                        case FENCED_DEAL:
                        {
                            _fencedDealValidator(rate, room.getId(), expectedValues.get(0));
                            break;
                        }
                        case STAY_PRICE_TYPE:
                        {
                            CommonValidationUtil.stayPriceTypeValidator(rate.getRoomPriceByOccupancy().values(), expectedValues);
                            break;
                        }
                        case NIGHTLY_PRICE_TYPE:
                        {
                            CommonValidationUtil.nightlyPriceTypeValidator(rate.getRoomPriceByOccupancy().values(), expectedValues);
                            break;
                        }
                        case NIGHTLY_PRICE_COUNT:
                        {
                            CommonValidationUtil.nightlyPriceCountValidator(rate.getRoomPriceByOccupancy().values(), expectedValues);
                            break;
                        }
                        case CURRENCY_CODE:
                        {
                            CommonValidationUtil.validateCurrencyCode(rate.getRoomPriceByOccupancy().values(), expectedValues.get(0));
                            break;
                        }
                        case BILLABLE_CURRENCY:
                        {
                            CommonValidationUtil.validateBillableCurrency(rate.getRoomPriceByOccupancy().values());
                            break;
                        }
                        case TOTAL_PRICE:
                        {
                            CommonValidationUtil.validateTotalPrice(rate.getRoomPriceByOccupancy().values());
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

    private static void _roomRatesCountValidator(TestContext context, int count)
    {
        for (Property property : context.getShoppingResponse().getProperties())
        {
            for (Room room : property.getRooms())
            {
                Assert.assertEquals(count, room.getRates().size());
            }
        }
    }

    private static void _roomCountValidator(TestContext context, int count)
    {
        for (Property property : context.getShoppingResponse().getProperties())
        {
            Assert.assertEquals(count, property.getRooms().size());
        }
    }

    private static void _propertyLinkCountValidator(TestContext context, int count)
    {
        for (Property property : context.getShoppingResponse().getProperties())
        {
            Assert.assertEquals(count, property.getLinks().size());
        }
    }

    private static void _fencedDealValidator(Rate rate, String roomID, String expectedValue)
    {
        final boolean expectedFencedDeal = Boolean.valueOf(expectedValue);
        Assert.assertEquals(
                "fenced deal is true for roomId: " + roomID,
                expectedFencedDeal,
                rate.isFencedDeal()
        );
    }

    private static void _amenitiesValidator(Rate rate, String roomID)
    {
        rate.getAmenities().forEach(amenity ->
        {
            if ((StringUtils.isNotEmpty(amenity.getId()) && StringUtils.isEmpty(amenity.getName()))
                    || (StringUtils.isEmpty(amenity.getId()) && StringUtils.isNotEmpty(amenity.getName())))
            {
                Assert.fail("amenity ID and description both should be present or both should be absent for a valid response, for room ID : [" + roomID + "]");
            }
        });
    }

    private static void _availableRoomsValidator(Rate rate, String roomID, Range<Integer> expectedRange)
    {
        Assert.assertTrue(
                "Number of available rooms in the response is invalid for room_id: [" + roomID + "]",
                expectedRange.isWithInBounds(rate.getAvailableRooms())
        );
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

    private static void _validatePropertyId(final TestContext testContext)
    {
        final List<String> requestPropertyIds = testContext.getParamValues(RequestType.SHOPPING, "property_id");

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
