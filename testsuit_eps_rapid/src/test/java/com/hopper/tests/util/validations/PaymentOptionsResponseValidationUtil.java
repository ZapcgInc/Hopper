package com.hopper.tests.util.validations;

import io.restassured.response.Response;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.HashMap;

public class PaymentOptionsResponseValidationUtil
{
    public static void validateCardType(Response restResponse)
    {
        HashMap<String, HashMap> paymentOptionMap = restResponse.jsonPath().get(".");
        ArrayList<HashMap> cardOptions = (ArrayList) paymentOptionMap.get("credit_card").get("card_options");
        cardOptions.forEach(cardOption ->
        {
            Assert.assertTrue(
                    "Card name and Card type is not found", cardOption.get("name") != null && cardOption.get("card_type") != null);
        });
    }

}
