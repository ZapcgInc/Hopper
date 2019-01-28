package com.hopper.tests.data;

import com.hopper.tests.constants.GlobalConstants;
import com.hopper.tests.constants.RequestType;
import com.hopper.tests.definitions.model.TestContext;
import io.restassured.response.Response;

import java.util.function.Supplier;

/**
 * Factory Class for {@link Response} Supplier.
 * Calls the End point and returns the Response.
 */
public class ResponseSupplierFactory
{
    public static Supplier<Response> create(final TestContext criteria,
                                            final String httpMethod,
                                            final RequestType requestType)
    {
        final String partner = criteria.getPartner();
        switch (partner)
        {
            case GlobalConstants.DEFAULT_PARTNER:
            {
                return new EPSResponseSupplier(criteria, httpMethod, requestType);
            }
            default:
            {
            	return new EPSResponseSupplier(criteria, httpMethod, requestType);
            }
        }
    }
}
