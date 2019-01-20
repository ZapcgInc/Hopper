package com.hopper.tests.data;

import com.hopper.tests.constants.RequestType;
import com.hopper.tests.constants.SupportedPartners;
import com.hopper.tests.model.TestContext;
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
        final SupportedPartners partner = criteria.getPartner();
        switch (partner)
        {
            case EPS:
            {
                return new EPSResponseSupplier(criteria, httpMethod, requestType);
            }
            default:
            {
                throw new UnsupportedOperationException("Authorization for Partner :" + partner.name() + "is currently unsupported");
            }
        }
    }
}
