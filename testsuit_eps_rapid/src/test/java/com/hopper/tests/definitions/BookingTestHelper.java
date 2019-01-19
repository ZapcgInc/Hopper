package com.hopper.tests.definitions;

import com.hopper.tests.constants.GlobalConstants;
import com.hopper.tests.constants.RequestType;
import com.hopper.tests.model.TestContext;
import com.hopper.tests.model.response.shopping.ShoppingResponse;
import com.hopper.tests.util.data.ResponseSupplierFactory;
import com.hopper.tests.util.parser.ShoppingResponseParser;
import io.restassured.response.Response;

public class BookingTestHelper
{
    public static void runShoppingAndPreBookingForBooking(final TestContext context)
    {
        final Response response = ResponseSupplierFactory.create(
                context,
                GlobalConstants.GET,
                RequestType.SHOPPING).get();

        context.setResponse(RequestType.SHOPPING, response);

        final ShoppingResponse shoppingResponse = ShoppingResponseParser.parse(context.getResponse(RequestType.SHOPPING));
        context.setShoppingResponse(shoppingResponse);
    }
}
