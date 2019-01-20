package com.hopper.tests.validations;

import com.hopper.tests.constants.RequestType;
import com.hopper.tests.data.model.response.Link;
import com.hopper.tests.data.model.response.Price;
import com.hopper.tests.data.model.response.RoomPrice;
import com.hopper.tests.data.model.response.TotalPrice;
import com.hopper.tests.util.math.FuzzyCompareDouble;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Util class with common Response Validations
 */
public class CommonValidationUtil
{
    public static void validateLink(final Link link, final RequestType requestType, String message, String linkType)
    {
        final String requestTypeMsg = String.format("Request Type [%s] : ", requestType.name());

        Assert.assertNotNull(requestTypeMsg + "Link Missing ",
                link);

        Assert.assertTrue(
                requestTypeMsg + "Link Method missing for : [" + message + "], of Type [" + linkType + "]",
                StringUtils.isNotEmpty(link.getMethod())
        );

        Assert.assertTrue(
                requestTypeMsg + "Link HREF missing for : [" + message + "], of Type [" + linkType + "]",
                StringUtils.isNotEmpty(link.getHref())
        );
    }

    public static void nightlyPriceCountValidator(final Collection<RoomPrice> roomPrices, List<String> expectedValues)
    {
        for (RoomPrice roomPrice : roomPrices)
        {
            if (roomPrice.getNightlyPrice().isEmpty())
            {
                continue;
            }

            Assert.assertEquals(
                    "Count of nightly prices is invalid",
                    roomPrice.getNightlyPrice().size(),
                    Integer.parseInt(expectedValues.get(0))
            );
        }
    }

    public static void nightlyPriceTypeValidator(final Collection<RoomPrice> roomPrices, List<String> expectedValues)
    {
        for (RoomPrice roomPrice : roomPrices)
        {
            if (roomPrice.getNightlyPrice().isEmpty())
            {
                continue;
            }

            for (List<Price> nightlyPrices : roomPrice.getNightlyPrice())
            {
                for (Price nightlyPrice : nightlyPrices)
                {
                    Assert.assertTrue(
                            "Nightly Price Type: " + nightlyPrice.getType() + " is invalid",
                            expectedValues.contains(nightlyPrice.getType())
                    );
                }
            }
        }
    }

    public static void stayPriceTypeValidator(final Collection<RoomPrice> roomPrices, List<String> expectedValues)
    {
        for (RoomPrice roomPrice : roomPrices)
        {
            if (roomPrice.getStayPrice().isEmpty())
            {
                continue;
            }

            for (Price stayPrice : roomPrice.getStayPrice())
            {
                Assert.assertTrue(
                        "Stay Price Type: " + stayPrice.getType() + " is invalid",
                        expectedValues.contains(stayPrice.getType())
                );
            }
        }
    }

    public static void occupancyValidator(final Map<String, RoomPrice> roomPriceByOccupancy, final List<String> expectedValues)
    {
        Assert.assertFalse("No Occupancies returned", roomPriceByOccupancy.isEmpty());

        final Set<String> responseOccupancyValues = roomPriceByOccupancy.keySet();
        final String errorMsg = "Expected Occupancies for : "
                + String.join(",", expectedValues)
                + " and actual values are : "
                + String.join(",", responseOccupancyValues);

        Assert.assertTrue(
                errorMsg,
                CollectionUtils.isEqualCollection(responseOccupancyValues, expectedValues)
        );
    }

    public static void validateCurrencyCode(final Collection<RoomPrice> roomPrices, String expectedCurrencyCode)
    {

        for (RoomPrice price : roomPrices)
        {
            final TotalPrice totalPrice = price.getTotals();
            if (price.getTotals() == null)
            {
                Assert.fail("Total currency missing");
            }

            if (totalPrice.getExclusive() != null)
            {
                Assert.assertEquals(
                        "Exclusive Request currency does not match with requested currency",
                        expectedCurrencyCode,
                        totalPrice.getExclusive().getRequest().getCurrency()
                );

            }

            if (totalPrice.getInclusive() != null)
            {
                Assert.assertEquals(
                        "Inclusive Request currency does not match with requested currency",
                        expectedCurrencyCode,
                        totalPrice.getInclusive().getRequest().getCurrency()
                );
            }

            if (totalPrice.getMarketingFee() != null)
            {
                Assert.assertEquals(
                        "Marketing Fee Request currency does not match with requested currency",
                        expectedCurrencyCode,
                        totalPrice.getMarketingFee().getRequest().getCurrency()
                );

            }

            if (totalPrice.getMinSellingPrice() != null)
            {
                Assert.assertEquals(
                        "Min Selling Price Request currency does not match with requested currency",
                        expectedCurrencyCode,
                        totalPrice.getMinSellingPrice().getRequest().getCurrency()
                );
            }

            if (totalPrice.getStrikeThrough() != null)
            {
                Assert.assertEquals(
                        "Strike-through Request currency does not match with requested currency",
                        expectedCurrencyCode,
                        totalPrice.getStrikeThrough().getRequest().getCurrency()
                );
            }
        }
    }

    public static void validateBillableCurrency(final Collection<RoomPrice> roomPrices)
    {

        for (RoomPrice price : roomPrices)
        {
            final TotalPrice totalPrice = price.getTotals();
            if (price.getTotals() == null)
            {
                Assert.fail("Total currency missing");
            }

            final Set<String> billableCurrencyCodes = new HashSet<>();

            if (totalPrice.getExclusive() != null)
            {
                billableCurrencyCodes.add(totalPrice.getExclusive().getBillable().getCurrency());
            }

            if (totalPrice.getInclusive() != null)
            {
                billableCurrencyCodes.add(totalPrice.getInclusive().getBillable().getCurrency());
            }

            if (totalPrice.getMarketingFee() != null)
            {
                billableCurrencyCodes.add(totalPrice.getMarketingFee().getBillable().getCurrency());
            }

            if (totalPrice.getMinSellingPrice() != null)
            {
                billableCurrencyCodes.add(totalPrice.getMinSellingPrice().getBillable().getCurrency());
            }

            if (totalPrice.getStrikeThrough() != null)
            {
                billableCurrencyCodes.add(totalPrice.getStrikeThrough().getBillable().getCurrency());
            }


            Assert.assertTrue(
                    "Multiple currency codes found in billable pricing : [" + String.join(",", billableCurrencyCodes) + "]",
                    billableCurrencyCodes.size() == 1
            );

        }
    }

    public static void validateTotalPrice(final Collection<RoomPrice> roomPrices)
    {
        for (RoomPrice roomPrice : roomPrices)
        {
            Double baseRate = 0.0;
            Double taxRate = 0.0;
            Double extraPersonFee = 0.0;
            Double adjustment = 0.0;

            for (List<Price> nightlyPrice : roomPrice.getNightlyPrice())
            {
                for (Price price : nightlyPrice)
                {
                    switch (price.getType())
                    {
                        case "base_rate":
                        {
                            baseRate += price.getValue();
                            break;
                        }
                        case "extra_person_fee":
                        {
                            extraPersonFee += price.getValue();
                            break;
                        }
                        case "adjustment":
                        {
                            adjustment += price.getValue();
                            break;
                        }
                        default:
                        {
                            taxRate += price.getValue();
                            break;
                        }
                    }
                }
            }

            Assert.assertTrue(
                    "Total Inclusive Price doesn't match the break-down",
                    FuzzyCompareDouble.equal(
                            baseRate + extraPersonFee + adjustment + taxRate,
                            roomPrice.getTotals().getInclusive().getBillable().getValue()
                    )
            );

            Assert.assertTrue(
                    "Total Exclusive Price doesn't match the break-down",
                    FuzzyCompareDouble.equal(
                            baseRate + extraPersonFee + adjustment,
                            roomPrice.getTotals().getExclusive().getBillable().getValue()
                    )
            );
        }
    }
}
