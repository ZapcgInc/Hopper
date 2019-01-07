package com.hopper.tests.util.data;

import com.hopper.tests.constants.SupportedPartners;
import com.hopper.tests.model.TestCriteria;
import io.restassured.response.Response;

import java.util.function.Supplier;

public class ResponseSupplierFactory
{
    public static Supplier<Response> create(TestCriteria criteria, String httpMethod)
    {
        final SupportedPartners partner = criteria.getPartner();
        switch (partner)
        {
            case EPS:
                return new EPSResponseSupplier(criteria, httpMethod);
            default:
                throw new UnsupportedOperationException("Authorization for Partner :" + partner.name() + "is currently unsupported");
        }
    }
}
