package com.hopper.tests.validations.retrievebooking;

import com.hopper.tests.validations.constants.ResponseValidationField;
import io.restassured.response.Response;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;

import java.util.List;
import java.util.Map;

public class RetrieveBookingValidation {


    public static void validateElement(Response response, ResponseValidationField element) {
        switch(element) {

            case EMAIL_ADDRESS:
            {
                _validateEmailAddress(response);
                break;
            }
            case ITINERARY_ID:
            {
                _validateItineraryId(response);
                break;
            }
            case CANCEL_HREF:
            {
                _validateCancelHref(response);
                break;
            }
            case ROOM_ID:
            {
                _validateRoomId(response);
                break;
            }
            case CHECK_IN_DATE:
            {
                _validateCheckinDate(response);
                break;
            }
            case CHECK_OUT_DATE:
            {
                _validateCheckoutDate(response);
                break;
            }
            case NUM_ADULTS:
            {
                _validateNumOfAdults(response);
                break;
            }
            case RATE_ID:
            {
                _validateRateId(response);
                break;
            }
            case GIVEN_NAME:
            {
                _validateGivenName(response);
                break;
            }
            case FAMILY_NAME:
            {
                _validateFamilyName(response);
                break;
            }
            case PHONE:
            {
                _validatePhone(response);
                break;
            }
            case NIGHTLY_RATE:
            {
                _validateNightlyRate(response);
                break;
            }
            case SMOKING:
            {
                _validateSmoking(response);
                break;
            }
            case REFUNDABLE:
            {
                _validateRefundable(response);
                break;
            }
            case CURRENCY_CODE:
            {
                _validateCurrencyCode(response);
                break;
            }
            case ADDRESS:
            {
                _validateAddress(response);
                break;
            }
            case COUNTRY_CODE:
            {
                _validateCountryCode(response);
                break;
            }
            case CREATION_DATE_TIME:
            {
                _validateCreationDateTime(response);
                break;
            }
            case FEES:
            {
                _validateFees(response);
                break;
            }


            default:
            {
                throw new UnsupportedOperationException("Validation Field [" + element + "] unsupported");
            }
        }
    }

    private static void _validateFees(Response response) {
        List<Map<String,Object>> rooms = response.jsonPath().get("rooms");
        rooms.forEach(room-> {
            Map<String, Object> rate = (Map) room.get("rate");
            Assert.assertNotNull("fees node is missing ", rate.get("fees"));
        });
    }

    private static void _validateCreationDateTime(Response response) {
        Assert.assertTrue(StringUtils.isNotEmpty(response.jsonPath().get("creation_date_time")));
    }

    private static void _validateCountryCode(Response response) {
        Map<String,Object> contact = response.jsonPath().get("billing_contact");
        Map<String,String> address = (Map)contact.get("address");
        Assert.assertTrue("Country code is invalid",address.get("country_code").length()==2);
    }

    private static void _validateAddress(Response response) {
        Map<String,Object> contact = response.jsonPath().get("billing_contact");
        Assert.assertNotNull("Address is missing",contact.get("address"));
    }

    private static void _validateCurrencyCode(Response response) {
        List<Map<String,Object>> rooms = response.jsonPath().get("rooms");
        rooms.forEach(room->{
            Map<String, Object > rate = (Map) room.get("rate");
            List<List<Map<String, String>>> nightly = (List)rate.get("nightly");
            for(List<Map<String,String>>night: nightly){
                night.forEach(charge->
                        Assert.assertTrue("Currency is not a 3 character code  ", charge.get("currency").length()==3)
                );
            }
        });
    }

    private static void _validateRefundable(Response response) {
        List<Map<String,Object>> rooms = response.jsonPath().get("rooms");
        rooms.forEach(room->{
            Map<String, Object > rate = (Map) room.get("rate");
            Assert.assertFalse("Refundable is true",Boolean.valueOf(rate.get("refundable").toString()));
        });
    }

    private static void _validateSmoking(Response response) {
        List<Map<String,Object>> rooms = response.jsonPath().get("rooms");
        rooms.forEach(room->{
            Assert.assertNotNull("Smoking is missing",Boolean.valueOf(room.get("smoking").toString()));
        });
    }

    private static void _validateNightlyRate(Response response) {
        List<Map<String,Object>> rooms = response.jsonPath().get("rooms");
        rooms.forEach(room->{
            Map<String, Object > rate = (Map) room.get("rate");
            List<List<Map<String, String>>> nightly = (List)rate.get("nightly");
            for(List<Map<String,String>>night: nightly){
                night.forEach(charge->
                        Assert.assertNotNull("Value is missing", charge.get("value"))
                );
            }
        });
    }

    private static void _validatePhone(Response response) {
        Map<String,Object> contact = response.jsonPath().get("billing_contact");
        Assert.assertTrue("Phone is missing",StringUtils.isNotEmpty(contact.get("phone").toString()));
    }

    private static void _validateGivenName(Response response) {
        Map<String,Object> contact = response.jsonPath().get("billing_contact");
        Assert.assertTrue(StringUtils.isNotEmpty(contact.get("given_name").toString()));
    }

    private static void _validateFamilyName(Response response) {
        Map<String,Object> contact = response.jsonPath().get("billing_contact");
        Assert.assertTrue("Family Name is missing",StringUtils.isNotEmpty(contact.get("family_name").toString()));
    }

    private static void _validateRateId(Response response) {
        List<Map<String,Object>> rooms = response.jsonPath().get("rooms");
        rooms.forEach(room->{
            Map<String, Object > rate = (Map) room.get("rate");
            Assert.assertTrue("Rate Id is missing",StringUtils.isNotEmpty(rate.get("id").toString()));
        });
    }

    private static void _validateNumOfAdults(Response response) {
        List<Map<String,Object>> rooms = response.jsonPath().get("rooms");
        rooms.forEach(room->{
            Assert.assertTrue(0!=Integer.valueOf(room.get("number_of_adults").toString()));
        });
    }

    private static void _validateCheckoutDate(Response response) {
        List<Map<String,Object>> rooms = response.jsonPath().get("rooms");
        rooms.forEach(room->{
            Assert.assertTrue(StringUtils.isNotEmpty(room.get("checkout").toString()));
            System.out.println(room.get("checkout"));
        });

    }

    private static void _validateRoomId(Response response) {
        List<Map<String,Object>> rooms = response.jsonPath().get("rooms");
        rooms.forEach(room->{
            Assert.assertTrue(StringUtils.isNotEmpty(room.get("id").toString()));
            System.out.println(room.get("id"));
        });
    }

    private static void _validateEmailAddress(Response response) {
        Map<String,Object> contact = response.jsonPath().get("billing_contact");
        Assert.assertTrue("Email Address is missing",StringUtils.isNotEmpty(contact.get("email").toString()));
    }


    private  static void _validateItineraryId(Response response){
        Assert.assertNotNull(response.jsonPath().getString("itinerary_id"));

    }

    private static void _validateCancelHref(Response response){
        if(response.jsonPath().getString("links")!=null) {
            Assert.assertNotNull(response.jsonPath().getString("links.cancel.href"));
            Assert.assertTrue("DELETE".equals(response.jsonPath().getString("links.cancel.method")));
        }
    }

    private static void _validateCheckinDate(Response response){
        List<Map<String,Object>> rooms = response.jsonPath().get("rooms");
        rooms.forEach(room->{
            Assert.assertTrue(StringUtils.isNotEmpty(room.get("id").toString()));
        });

    }

    public static void validateElement(Response response, ResponseValidationField element, List<String> expectedValues)
    {
        switch(element){
            case STATUS:
                _validateStatus(response,expectedValues);
                break;

                default:
                    throw new UnsupportedOperationException("Validation Field [" + element + "] unsupported");

        }
    }

    private static void _validateStatus(Response response, List<String> expectedValues) {

        List<Map<String,Object>> rooms = response.jsonPath().get("rooms");
        rooms.forEach(room->{
            Assert.assertTrue(expectedValues.contains(room.get("status")));
        });
    }
}

