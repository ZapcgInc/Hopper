package com.hopper.tests.util.validations;

import com.hopper.tests.constants.RequestType;
import com.hopper.tests.model.TestContext;
import io.restassured.response.Response;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;

import javax.validation.constraints.NotNull;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Util class for Response Validations
 */
public class ResponseValidationUtil
{
    public static void validateHTTPResponseCode(@NotNull final Response restResponse, final int expectedCode)
    {
        final int responseCode = restResponse.getStatusCode();
        final String errorMessage = "Expected response code : " + expectedCode + "and actual response code : " + responseCode + "are not matching";
        Assert.assertTrue(errorMessage, expectedCode == responseCode);
    }


    public static void validateResponseBody(final Response restResponse, final Map<String, String> expectedResponseMap, final String field)
    {
        final Map<String, String> fieldResponseMap = restResponse.jsonPath().get(field);

        for (String key : expectedResponseMap.keySet())
        {
            final String actualValue = fieldResponseMap.get(key);
            final String expectedValue = expectedResponseMap.get(key);
            final String errorMessage = "For field" + field + "Expected value : " + expectedValue + "and actual value : " + actualValue + "are not matching";

            Assert.assertEquals(errorMessage, expectedValue, actualValue);
        }
    }

    public static void validateResponseBodyForNode(String node, TestContext testContext) throws ParseException
    {
        switch (node)
        {
            case "property_id":
                validatePropertyId(testContext);
                break;
            case "available_rooms_for_all_room_type":
                validateAvailableRooms(testContext);
                break;
            case "merchant_of_record":
                validateMerchantOfRecord(testContext);
                break;
            case "href_price_check":
                validateHrefPriceCheck(testContext);
                break;
            case "href_payment_options":
                validateHrefPaymentOption(testContext);
                break;
            case "response_cancel_policies_for_refundable_rates":
                validateResponseCancelPoliciesForRefundableRates(testContext);
                break;
            case "promo_fields":
                validatePromoFields();
                break;
            case "amenities":
                validateAminities(testContext);
            case "fenced_deal":
                validateFencedDeal(testContext);
            case "occupancy":
                validateOccupancy(testContext);
            default:
                System.out.println("something not supported");
        }
    }

    private static void validateOccupancy(TestContext testContext)
    {
        List<String> requestOccupancies = new ArrayList<>();
        ArrayList<String> responseOccupancies = new ArrayList<>();
        ArrayList<LinkedHashMap> responseAsList = testContext.getResponse(RequestType.SHOPPING).as(ArrayList.class);
        for (LinkedHashMap<String, Object> m : responseAsList)
        {
            ArrayList<LinkedHashMap> roomsArr = (ArrayList<LinkedHashMap>) m.get("rooms");
            for (LinkedHashMap<String, Object> l : roomsArr)
            {
                ArrayList<LinkedHashMap> rateList = (ArrayList<LinkedHashMap>) l.get("rates");
                String roomId = (String) l.get("id");
                for (LinkedHashMap<String, Object> k : rateList)
                {
                    LinkedHashMap occupancies = (LinkedHashMap) k.get("occupancies");
                    responseOccupancies.addAll(occupancies.keySet());
                }
                break;
            }
            break;
        }
        if (testContext.getParamsWithMultipleValues(RequestType.SHOPPING).get("occupancy") != null)
        {
            requestOccupancies.addAll(testContext.getParamsWithMultipleValues(RequestType.SHOPPING).get("occupancy"));
        }
        if (testContext.getParams(RequestType.SHOPPING).get("occupancy") != null)
        {
            requestOccupancies.add(testContext.getParams(RequestType.SHOPPING).get("occupancy"));
        }
        if (responseOccupancies.size() == requestOccupancies.size())
        {
            Assert.assertTrue(CollectionUtils.isEqualCollection(responseOccupancies, requestOccupancies));
        }
        else
        {
            Assert.fail("Occupancy in the response mismatches with the requested occupancy");
        }
    }

    private static void validateFencedDeal(TestContext testContext)
    {
        ArrayList<LinkedHashMap> responseAsList = testContext.getResponse(RequestType.SHOPPING).as(ArrayList.class);
        for (LinkedHashMap<String, Object> m : responseAsList)
        {
            ArrayList<LinkedHashMap> roomsArr = (ArrayList<LinkedHashMap>) m.get("rooms");
            for (LinkedHashMap<String, Object> l : roomsArr)
            {
                ArrayList<LinkedHashMap> rateList = (ArrayList<LinkedHashMap>) l.get("rates");
                String roomId = (String) l.get("id");
                for (LinkedHashMap<String, Object> k : rateList)
                {
                    Boolean value = (Boolean) k.get("fenced_deal");
                    if (value)
                    {
                        Assert.fail("fenced_deal should be set to false for roomId: " + roomId);
                    }
                }
            }
        }
    }

    private static void validatePromoFields()
    {

    }

    private static void validateAminities(TestContext testContext)
    {
        ArrayList<LinkedHashMap> responseAsList = testContext.getResponse(RequestType.SHOPPING).as(ArrayList.class);
        for (LinkedHashMap<String, Object> m : responseAsList)
        {
            ArrayList<LinkedHashMap> roomsArr = (ArrayList<LinkedHashMap>) m.get("rooms");
            for (LinkedHashMap<String, Object> l : roomsArr)
            {
                ArrayList<LinkedHashMap> rateList = (ArrayList<LinkedHashMap>) l.get("rates");
                String roomId = (String) l.get("id");
                for (LinkedHashMap<String, Object> k : rateList)
                {
                    ArrayList<LinkedHashMap> amenities = (ArrayList<LinkedHashMap>) k.get("amenities");
                    if (!CollectionUtils.isEmpty(amenities))
                    {
                        for (LinkedHashMap<String, Object> amenity : amenities)
                        {
                            Integer id = (Integer) amenity.get("id");
                            String name = (String) amenity.get("name");
                            if ((id != null && StringUtils.isEmpty(name)) || (id == null && StringUtils.isNotEmpty(name)))
                            {
                                Assert.fail("amenity ID and description both should be present or both should be absent for a valid response.");
                            }
                        }
                    }
                }
            }
        }
    }

    private static void validateResponseCancelPoliciesForRefundableRates(TestContext testContext) throws ParseException
    {
        ArrayList<LinkedHashMap> responseAsList = testContext.getResponse(RequestType.SHOPPING).as(ArrayList.class);
        for (LinkedHashMap<String, Object> m : responseAsList)
        {
            ArrayList<LinkedHashMap> roomsArr = (ArrayList<LinkedHashMap>) m.get("rooms");
            for (LinkedHashMap<String, Object> l : roomsArr)
            {
                ArrayList<LinkedHashMap> rateList = (ArrayList<LinkedHashMap>) l.get("rates");
                String roomId = (String) l.get("id");
                for (LinkedHashMap<String, Object> k : rateList)
                {
                    boolean value = (boolean) k.get("refundable");
                    if (!value)
                    {
                        Assert.fail(" field refundable is not set to true in the response." + roomId);
                    }
                    else
                    {
                        String checkin = testContext.getParams(RequestType.SHOPPING).get("checkin");
                        String checkout = testContext.getParams(RequestType.SHOPPING).get("checkout");
                        ArrayList<LinkedHashMap> cancelPanaltyList = (ArrayList) k.get("cancel_penalties");
                        for (LinkedHashMap<String, String> t : cancelPanaltyList)
                        {
                            String startDate = t.get("start");
                            String endDate = t.get("end");
                            if (!validateStartEndDate(checkin, checkout, startDate, endDate))
                            {
                                Assert.fail("cancel policy start and end date are not within check in and check " +
                                        "out dates for roomId: " + roomId);
                            }

                        }
                    }
                }
            }
        }
    }

    private static boolean validateStartEndDate(String checkin, String checkout, String startDate, String endDate) throws ParseException
    {
        boolean flag = false;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if (sdf.parse(checkin).before(sdf.parse(startDate)) && sdf.parse(endDate).before(sdf.parse(checkout)))
        {
            flag = true;
        }
        return flag;
    }

    private static void validateHrefPriceCheck(TestContext testContext)
    {
        ArrayList<LinkedHashMap> responseAsList = testContext.getResponse(RequestType.SHOPPING).as(ArrayList.class);
        for (LinkedHashMap<String, Object> m : responseAsList)
        {
            ArrayList<LinkedHashMap> roomsArr = (ArrayList<LinkedHashMap>) m.get("rooms");
            for (LinkedHashMap<String, Object> l : roomsArr)
            {
                ArrayList<LinkedHashMap> rateList = (ArrayList<LinkedHashMap>) l.get("rates");
                String roomId = (String) l.get("id");
                for (LinkedHashMap<String, Object> k : rateList)
                {
                    ArrayList<LinkedHashMap> bedGroupsList = (ArrayList) k.get("bed_groups");
                    for (LinkedHashMap b : bedGroupsList)
                    {
                        LinkedHashMap c = (LinkedHashMap) b.get("links");
                        LinkedHashMap d = (LinkedHashMap) c.get("price_check");
                        String hrefLink = (String) d.get("href");
                        if (StringUtils.isEmpty(hrefLink))
                        {
                            Assert.fail("hrefLink empty for roomId" + roomId);
                        }
                    }
                }
            }
        }
    }

    private static void validateHrefPaymentOption(TestContext testContext)
    {
        ArrayList<LinkedHashMap> responseAsList = testContext.getResponse(RequestType.SHOPPING).as(ArrayList.class);
        for (LinkedHashMap<String, Object> m : responseAsList)
        {
            ArrayList<LinkedHashMap> roomsArr = (ArrayList<LinkedHashMap>) m.get("rooms");
            for (LinkedHashMap<String, Object> l : roomsArr)
            {
                ArrayList<LinkedHashMap> rateList = (ArrayList<LinkedHashMap>) l.get("rates");
                String roomId = (String) l.get("id");
                for (LinkedHashMap<String, Object> k : rateList)
                {
                    LinkedHashMap<String, LinkedHashMap> linksMap = (LinkedHashMap) k.get("links");
                    LinkedHashMap<String, String> paymentOptionsMap = linksMap.get("payment_options");
                    String hrefLink = paymentOptionsMap.get("href");
                    if (StringUtils.isEmpty(hrefLink))
                    {
                        Assert.fail("hrefLink empty for roomId" + roomId);
                    }
                }
            }
        }
    }

    private static void validateMerchantOfRecord(TestContext testContext)
    {
        ArrayList<LinkedHashMap> responseAsList = testContext.getResponse(RequestType.SHOPPING).as(ArrayList.class);
        for (LinkedHashMap<String, Object> m : responseAsList)
        {
            ArrayList<LinkedHashMap> roomsArr = (ArrayList<LinkedHashMap>) m.get("rooms");
            for (LinkedHashMap<String, Object> l : roomsArr)
            {
                ArrayList<LinkedHashMap> rateList = (ArrayList<LinkedHashMap>) l.get("rates");
                String roomId = (String) l.get("id");
                for (LinkedHashMap<String, Object> k : rateList)
                {
                    String value = (String) k.get("merchant_of_record");
                    if (!("expedia".equals(value) || "property".equals(value)))
                    {
                        Assert.fail(" merchant record field matches the partner name or property for room_id: " + roomId);
                    }
                }
            }
        }
    }

    private static void validateAvailableRooms(TestContext testContext)
    {
        ArrayList<LinkedHashMap> responseAsList = testContext.getResponse(RequestType.SHOPPING).as(ArrayList.class);
        for (LinkedHashMap<String, Object> m : responseAsList)
        {
            ArrayList<LinkedHashMap> roomsArr = (ArrayList<LinkedHashMap>) m.get("rooms");
            for (LinkedHashMap<String, Object> l : roomsArr)
            {
                ArrayList<LinkedHashMap> n = (ArrayList<LinkedHashMap>) l.get("rates");
                String roomId = (String) l.get("id");
                for (LinkedHashMap<String, Object> k : n)
                {
                    int b = (Integer) k.get("available_rooms");
                    if (b == 0)
                    {
                        Assert.fail("Number of available rooms in the response is 0 for room_id: " + roomId);
                    }
                }
            }
        }
    }

    private static void validatePropertyId(TestContext testContext)
    {
        ArrayList<LinkedHashMap> responseAsList = testContext.getResponse(RequestType.SHOPPING).as(ArrayList.class);
        List<String> requestPropIds = new ArrayList<>();
        ArrayList<String> responsePropIds = new ArrayList<>();
        for (LinkedHashMap response : responseAsList)
        {
            String value = response.get("property_id").toString();
            responsePropIds.add(value);
        }
        if (testContext.getParamsWithMultipleValues(RequestType.SHOPPING).get("property_id") != null)
        {
            requestPropIds.addAll(testContext.getParamsWithMultipleValues(RequestType.SHOPPING).get("property_id"));
        }
        if (testContext.getParams(RequestType.SHOPPING).get("property_id") != null)
        {
            requestPropIds.add(testContext.getParams(RequestType.SHOPPING).get("property_id"));
        }
        if (responsePropIds.size() <= requestPropIds.size())
        {
            Assert.assertTrue(CollectionUtils.containsAny(responsePropIds, requestPropIds));
        }
        else
        {
            Assert.fail("Property Ids in the response is more than the requested response");
        }
    }

}
