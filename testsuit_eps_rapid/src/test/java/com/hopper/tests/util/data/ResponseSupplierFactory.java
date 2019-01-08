package com.hopper.tests.util.data;

import com.hopper.tests.constants.RequestType;
import com.hopper.tests.constants.SupportedPartners;
import com.hopper.tests.model.TestContext;
import io.restassured.response.Response;

import java.util.function.Supplier;

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
