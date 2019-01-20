package com.hopper.tests.definitions;

import com.hopper.tests.constants.GlobalConstants;
import com.hopper.tests.constants.RequestType;
import com.hopper.tests.model.TestContext;
import com.hopper.tests.model.response.Link;
import com.hopper.tests.model.response.prebooking.PreBookingResponse;
import com.hopper.tests.model.response.shopping.BedGroups;
import com.hopper.tests.model.response.shopping.Property;
import com.hopper.tests.model.response.shopping.Rate;
import com.hopper.tests.model.response.shopping.Room;
import com.hopper.tests.model.response.shopping.ShoppingResponse;
import com.hopper.tests.util.data.ResponseSupplierFactory;
import com.hopper.tests.util.parser.PreBookingResponseParser;
import com.hopper.tests.util.parser.ShoppingResponseParser;
import io.restassured.response.Response;
import org.junit.Assert;

public class BookingTestHelper
{
    public static void runShoppingAndPreBookingForBooking(final TestContext context)
    {
        final Response shopResponse = ResponseSupplierFactory.create(context, GlobalConstants.GET, RequestType.SHOPPING).get();
        context.setResponse(RequestType.SHOPPING, shopResponse);

        final ShoppingResponse shoppingResponse = ShoppingResponseParser.parse(context.getResponse(RequestType.SHOPPING));
        context.setShoppingResponse(shoppingResponse);

        if (shoppingResponse == null || shoppingResponse.getProperties().isEmpty())
        {
            Assert.fail("No available properties returned in Shopping");
        }

        boolean foundPropertyWithAvailability = false;
        for (Property property : shoppingResponse.getProperties())
        {
            for (Room room : property.getRooms())
            {
                for (Rate rate : room.getRates())
                {
                    for (BedGroups bedGroup : rate.getBedGroups())
                    {
                        final Link priceCheckLink = bedGroup.getLinks().get("price_check");
                        context.setApiPath(RequestType.PRE_BOOKING, priceCheckLink.getHref());

                        final Response response = ResponseSupplierFactory.create(context, priceCheckLink.getMethod(), RequestType.PRE_BOOKING).get();

                        if (response.getStatusCode() == 200)
                        {
                            final PreBookingResponse preBookingResponse = PreBookingResponseParser.parse(response);
                            if (preBookingResponse != null && preBookingResponse.getStatus().equals("matched"))
                            {
                                context.setPreBookingResponse(preBookingResponse);
                                context.setResponse(RequestType.PRE_BOOKING, response);
                                foundPropertyWithAvailability = true;
                                break;
                            }
                        }

                    }
                }

            }
        }

        Assert.assertTrue("Prebooking failed for all properties / rooms", foundPropertyWithAvailability);
    }
}
