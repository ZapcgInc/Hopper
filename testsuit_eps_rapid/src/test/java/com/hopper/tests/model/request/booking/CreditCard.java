package com.hopper.tests.model.request.booking;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

public class CreditCard
{
    public static Map<String, Object> createDummy()
    {
        final ImmutableMap.Builder<String, Object> addressMap = ImmutableMap.builder();
        addressMap.put("line_1", "555 1st St");
        addressMap.put("line_2", "10th Floor");
        addressMap.put("line_3", "Unit 12");
        addressMap.put("city", "Seattle");
        addressMap.put("state_province_code", "WA");
        addressMap.put("postal_code", "98121");
        addressMap.put("country_code", "US");

        final ImmutableMap.Builder<String, Object> billingContactMap = ImmutableMap.builder();
        billingContactMap.put("given_name", "John");
        billingContactMap.put("family_name", "Smith");
        billingContactMap.put("email", "smith@example.com");
        billingContactMap.put("phone", "4875550077");
        billingContactMap.put("address", addressMap.build());


        final ImmutableMap.Builder<String, Object> builder = ImmutableMap.builder();
        builder.put("type", "customer_card");
        builder.put("card_type", "VI");
        builder.put("number", "4111111111111111");
        builder.put("security_code", "123");
        builder.put("expiration_month", "12");
        builder.put("expiration_year", "2025");
        builder.put("billing_contact", billingContactMap.build());

        return builder.build();
    }
}
