package com.hopper.tests.util.validations;

import com.hopper.tests.constants.RequestType;
import com.hopper.tests.model.response.Link;
import com.hopper.tests.model.response.Price;
import com.hopper.tests.model.response.RoomPrice;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;

import java.util.Collection;
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
            // TODO: can we have a response without nightly prices ?
            //Assert.assertFalse("Stay prices missing", roomPrice.getNightlyPrice().isEmpty());
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
            // TODO: can we have a response without nightly prices ?
            //Assert.assertFalse("Stay prices missing", roomPrice.getNightlyPrice().isEmpty());
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
            // TODO: can we have a response without stay prices ?
            //Assert.assertFalse("Stay prices missing", roomPrice.getStayPrice().isEmpty());
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
}
