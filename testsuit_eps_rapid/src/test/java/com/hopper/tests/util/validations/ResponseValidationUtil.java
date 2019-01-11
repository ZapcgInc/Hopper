package com.hopper.tests.util.validations;

import com.hopper.tests.constants.RequestType;
import com.hopper.tests.model.TestContext;
import io.restassured.response.Response;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;

import javax.validation.constraints.NotNull;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

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
            case "amenities":
                validateAmenities(testContext);
                break;
            case "fenced_deal":
                validateFencedDeal(testContext);
                break;
            case "occupancy":
                validateOccupancy(testContext);
                break;
            case "deposit_policy_true":
                validateDepositPolicies(testContext);
                break;
            case "stay_node":
                validateStayNode(testContext);
                break;
            case "night_room_prices_are_available_for_all_LOS":
                validateNightRoomPrices(testContext);
                break;
            case "total_price":
                validateTotalPrice(testContext);
                break;
            case "response_currency_code":
                validateCurrencyCode(testContext);
                break;
            case "no_include":
                validateWithNoInclude(testContext);
                break;
        }
    }

    private static void validateWithNoInclude(TestContext testContext) {
        if (testContext.getParams(RequestType.SHOPPING).get("include") == null) {
            ArrayList<LinkedHashMap> responseAsList = testContext.getResponse(RequestType.SHOPPING).as(ArrayList.class);

            for (LinkedHashMap<String, Object> responseMap : responseAsList) {

                ArrayList<LinkedHashMap> roomsArr = (ArrayList<LinkedHashMap>) responseMap.get("rooms");
                LinkedHashMap<String, LinkedHashMap<String, String>> links = (LinkedHashMap) responseMap.get("links");
                if (links == null || StringUtils.isEmpty(links.get("additional_rates").get("href"))) {
                    Assert.fail("link for additional rates not available");
                }
                if (roomsArr.size() != 1) {
                    Assert.fail("Size of rooms array is not 1 ");
                }
                for (LinkedHashMap<String, Object> room : roomsArr) {

                    ArrayList<LinkedHashMap> rateList = (ArrayList<LinkedHashMap>) room.get("rates");
                    if (rateList.size() != 1) {
                        Assert.fail("Size of rate array is not 1");
                    }
                }
            }
        }
    }

    private static void validateCurrencyCode(TestContext testContext) {
        ArrayList<LinkedHashMap> responseAsList = testContext.getResponse(RequestType.SHOPPING).as(ArrayList.class);

        for (LinkedHashMap<String, Object> responseMap : responseAsList) {

            ArrayList<LinkedHashMap> roomsArr = (ArrayList<LinkedHashMap>) responseMap.get("rooms");
            for (LinkedHashMap<String, Object> room : roomsArr) {

                ArrayList<LinkedHashMap> rateList = (ArrayList<LinkedHashMap>) room.get("rates");
                String roomId = (String) room.get("id");
                for (LinkedHashMap<String, Object> rate : rateList) {

                    LinkedHashMap<String, LinkedHashMap> occupancies = (LinkedHashMap) rate.get("occupancies");
                    for (Map.Entry<String, LinkedHashMap> occupancy : occupancies.entrySet()) {
                        LinkedHashMap<String, Object> roomRates = occupancy.getValue();
                        ArrayList<ArrayList> nightlyList = (ArrayList) roomRates.get("nightly");
                        for (ArrayList<LinkedHashMap> nightly : nightlyList) {

                            for (LinkedHashMap<String,String> map : nightly) {

                                String currency = map.get("currency");
                                if (!testContext.getParams(RequestType.SHOPPING).get("currency").equals(currency))
                                    Assert.fail("Response currency in nightly does not match with requested currency for room_id: " + roomId);
                            }
                        }

                        LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, String>>> totals = (LinkedHashMap) roomRates.get("totals");

                        String currencyInclusive = totals.get("inclusive").get("billable_currency").get("currency");
                        if (!testContext.getParams(RequestType.SHOPPING).get("currency").equals(currencyInclusive)) {
                            Assert.fail("Response currency in totals inclusive does not match with requested currency for room_id: " + roomId);
                        }
                        String currencyExclusive = totals.get("exclusive").get("billable_currency").get("currency");
                        if (!testContext.getParams(RequestType.SHOPPING).get("currency").equals(currencyExclusive)) {
                            Assert.fail("Response currency in totals exclusive does not match with requested currency for room_id: " + roomId);
                        }
                        if (totals.get("strikethrough") != null) {
                            String currencyStrikethrough = totals.get("strikethrough").get("billable_currency").get("currency");
                            if (!testContext.getParams(RequestType.SHOPPING).get("currency").equals(currencyStrikethrough)) {
                                Assert.fail("Response currency in totals strikethrough does not match with requested currency for room_id: " + roomId);
                            }
                        }
                        if (totals.get("marketing_fee") != null) {
                            String currencyMarketing = totals.get("marketing_fee").get("billable_currency").get("currency");
                            if (!testContext.getParams(RequestType.SHOPPING).get("currency").equals(currencyMarketing)) {
                                Assert.fail("Response currency in totals marketing does not match with requested currency for room_id: " + roomId);
                            }
                        }
                        if (totals.get("minimum_selling_price") != null) {
                            String currencySP = totals.get("minimum_selling_price").get("billable_currency").get("currency");
                            if (!testContext.getParams(RequestType.SHOPPING).get("currency").equals(currencySP)) {
                                Assert.fail("Response currency in totals selling_price does not match with requested currency for room_id: " + roomId);
                            }
                        }
                    }
                }
            }
        }
    }

    private static void validateTotalPrice(TestContext testContext) {
        ArrayList<LinkedHashMap> responseAsList = testContext.getResponse(RequestType.SHOPPING).as(ArrayList.class);
        DecimalFormat df = new DecimalFormat("###.##");
        for (LinkedHashMap<String, Object> responseMap : responseAsList) {

            ArrayList<LinkedHashMap> roomsArr = (ArrayList<LinkedHashMap>) responseMap.get("rooms");
            for (LinkedHashMap<String, Object> room : roomsArr) {

                ArrayList<LinkedHashMap> rateList = (ArrayList<LinkedHashMap>) room.get("rates");
                String roomId = (String) room.get("id");
                for (LinkedHashMap<String, Object> rate : rateList) {

                    LinkedHashMap<String, LinkedHashMap> occupancies = (LinkedHashMap) rate.get("occupancies");
                    for (Map.Entry<String, LinkedHashMap> occupancy : occupancies.entrySet()) {
                        Double baseRate = 0.0;
                        Double taxRate = 0.0;
                        Double extraPersonfee = 0.0;
                        LinkedHashMap<String, Object> roomRates = occupancy.getValue();
                        ArrayList<ArrayList> list = (ArrayList) roomRates.get("nightly");
                        for (ArrayList<LinkedHashMap> n : list) {

                            for (LinkedHashMap map : n) {

                                String value = (String) map.get("value");
                                if (map.get("type").equals("base_rate"))
                                    baseRate = baseRate + Double.parseDouble(value);
                                else if (map.get("type").equals("extra_person_fee"))
                                    extraPersonfee = extraPersonfee + Double.parseDouble(value);
                                else
                                    taxRate = taxRate + Double.parseDouble(value);
                            }
                        }

                        LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, String>>> j = (LinkedHashMap) roomRates.get("totals");

                        Double billableInclusiveTotal = Double.parseDouble(j.get("inclusive").get("billable_currency").get("value"));
                        Double expectedBillableIncTotal = Double.parseDouble(df.format(baseRate + taxRate + extraPersonfee));
                        Double billableExclusiveTotal = Double.parseDouble(j.get("exclusive").get("billable_currency").get("value"));
                        Double expectedBillableExTotal = Double.parseDouble(df.format(baseRate + extraPersonfee));
                        if (!billableInclusiveTotal.equals(expectedBillableIncTotal) ||
                                !billableExclusiveTotal.equals(expectedBillableExTotal)) {

                            Assert.fail("Expected totals does not match billableTotals for room_id: " + roomId);

                        }
                    }

                }
            }
        }
    }

    private static void validateNightRoomPrices(TestContext testContext) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String checkin = testContext.getParams(RequestType.SHOPPING).get("checkin");
        String checkout = testContext.getParams(RequestType.SHOPPING).get("checkout");
        long lengthOfStay = TimeUnit.MILLISECONDS.toDays(sdf.parse(checkout).getTime() - (sdf.parse(checkin)).getTime());
        String[] allowedTypes = {"base_rate", "tax_and_service_fee", "extra_person_fee", "property_fee", "sales_tax",
                "adjustment"};
        ArrayList<LinkedHashMap> responseAsList = testContext.getResponse(RequestType.SHOPPING).as(ArrayList.class);
        for (LinkedHashMap<String, Object> responseMap : responseAsList) {

            ArrayList<LinkedHashMap> roomsArr = (ArrayList<LinkedHashMap>) responseMap.get("rooms");
            for (LinkedHashMap<String, Object> room : roomsArr) {

                ArrayList<LinkedHashMap> rateList = (ArrayList<LinkedHashMap>) room.get("rates");
                String roomId = (String) room.get("id");
                for (LinkedHashMap<String, Object> rate : rateList) {

                    LinkedHashMap<String, LinkedHashMap> occupancies = (LinkedHashMap) rate.get("occupancies");
                    for (Map.Entry<String, LinkedHashMap> e : occupancies.entrySet()) {

                        LinkedHashMap<String, Object> s = e.getValue();
                        ArrayList<ArrayList> list = (ArrayList) s.get("nightly");
                        if (list.size() != lengthOfStay) {
                            Assert.fail("Night prices should be available for all LOS ");
                        }
                        for (ArrayList<LinkedHashMap> n : list) {

                            for (LinkedHashMap map : n) {
                                String type = (String) map.get("type");
                                boolean contains = Arrays.stream(allowedTypes).anyMatch(type::equals);
                                if (!contains) {
                                    Assert.fail("type: " + type + " is invalid for room_id: " + roomId);
                                }
                            }
                        }
                    }
                }
            }
        }

    }

    private static void validateStayNode(TestContext testContext) {
        ArrayList<LinkedHashMap> responseAsList = testContext.getResponse(RequestType.SHOPPING).as(ArrayList.class);
        for (LinkedHashMap<String, Object> responseMap : responseAsList) {

            ArrayList<LinkedHashMap> roomsArr = (ArrayList<LinkedHashMap>) responseMap.get("rooms");
            for (LinkedHashMap<String, Object> room : roomsArr) {

                ArrayList<LinkedHashMap> rateList = (ArrayList<LinkedHashMap>) room.get("rates");
                String roomId = (String) room.get("id");
                for (LinkedHashMap<String, Object> rate : rateList) {
                    LinkedHashMap<String, LinkedHashMap> occupancies = (LinkedHashMap) rate.get("occupancies");
                    for (Map.Entry<String, LinkedHashMap> e : occupancies.entrySet()) {
                        LinkedHashMap<String, LinkedHashMap> s = e.getValue();
                        if (s.get("stay") != null) {
                            String[] values = {"base_rate", "tax_and_service_fee", "extra_person_fee", "property_fee", "sales_tax",
                                    "adjustment"};
                            String type = (String) s.get("stay").get("type");
                            boolean contains = Arrays.stream(values).anyMatch(type::equals);
                            if (!contains) {
                                Assert.fail("type: " + type + " is invalid for room_id: " + roomId);
                            }
                        }
                    }

                }
            }
        }
    }

    private static void validateDepositPolicies(TestContext testContext) {
        ArrayList<LinkedHashMap> responseAsList = testContext.getResponse(RequestType.SHOPPING).as(ArrayList.class);
        for (LinkedHashMap<String, Object> responseMap : responseAsList) {

            ArrayList<LinkedHashMap> roomsArr = (ArrayList<LinkedHashMap>) responseMap.get("rooms");
            for (LinkedHashMap<String, Object> room : roomsArr) {

                ArrayList<LinkedHashMap> rateList = (ArrayList<LinkedHashMap>) room.get("rates");
                String roomId = (String) room.get("id");
                for (LinkedHashMap<String, Object> rate : rateList) {

                    Boolean depositRequired = (Boolean) rate.get("deposit_required");
                    if (depositRequired) {

                        LinkedHashMap<String, LinkedHashMap> linksMap = (LinkedHashMap) rate.get("links");
                        LinkedHashMap<String, String> depositPolicies = linksMap.get("deposit_policies");
                        if (depositPolicies != null && StringUtils.isEmpty(depositPolicies.get("href"))) {

                            Assert.fail("link should be present for deposit policies when deposit_required is " +
                                    "true for room_id: " + roomId);
                        }
                    }
                }
            }
        }
    }

    private static void validateOccupancy(TestContext testContext)
    {
        List<String> requestOccupancies = new ArrayList<>();
        ArrayList<String> responseOccupancies = new ArrayList<>();
        ArrayList<LinkedHashMap> responseAsList = testContext.getResponse(RequestType.SHOPPING).as(ArrayList.class);
        for (LinkedHashMap<String, Object> responseMap : responseAsList)
        {
            ArrayList<LinkedHashMap> roomsArr = (ArrayList<LinkedHashMap>) responseMap.get("rooms");
            for (LinkedHashMap<String, Object> room : roomsArr)
            {
                ArrayList<LinkedHashMap> rateList = (ArrayList<LinkedHashMap>) room.get("rates");
                String roomId = (String) room.get("id");
                for (LinkedHashMap<String, Object> rate : rateList)
                {
                    LinkedHashMap occupancies = (LinkedHashMap) rate.get("occupancies");
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
        for (LinkedHashMap<String, Object> responseMap : responseAsList)
        {
            ArrayList<LinkedHashMap> roomsArr = (ArrayList<LinkedHashMap>) responseMap.get("rooms");
            for (LinkedHashMap<String, Object> room : roomsArr)
            {
                ArrayList<LinkedHashMap> rateList = (ArrayList<LinkedHashMap>) room.get("rates");
                String roomId = (String) room.get("id");
                for (LinkedHashMap<String, Object> rate : rateList)
                {
                    Boolean value = (Boolean) rate.get("fenced_deal");
                    if (value)
                    {
                        Assert.fail("fenced_deal should be set to false for roomId: " + roomId);
                    }
                }
            }
        }
    }

    private static void validateAmenities(TestContext testContext)
    {
        ArrayList<LinkedHashMap> responseAsList = testContext.getResponse(RequestType.SHOPPING).as(ArrayList.class);
        for (LinkedHashMap<String, Object> responseMap : responseAsList)
        {
            ArrayList<LinkedHashMap> roomsArr = (ArrayList<LinkedHashMap>) responseMap.get("rooms");
            for (LinkedHashMap<String, Object> room : roomsArr)
            {
                ArrayList<LinkedHashMap> rateList = (ArrayList<LinkedHashMap>) room.get("rates");
                String roomId = (String) room.get("id");
                for (LinkedHashMap<String, Object> rate : rateList)
                {
                    ArrayList<LinkedHashMap> amenities = (ArrayList<LinkedHashMap>) rate.get("amenities");
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
        for (LinkedHashMap<String, Object> responseMap : responseAsList)
        {
            ArrayList<LinkedHashMap> roomsArr = (ArrayList<LinkedHashMap>) responseMap.get("rooms");
            for (LinkedHashMap<String, Object> room : roomsArr)
            {
                ArrayList<LinkedHashMap> rateList = (ArrayList<LinkedHashMap>) room.get("rates");
                String roomId = (String) room.get("id");
                for (LinkedHashMap<String, Object> rate : rateList)
                {
                    boolean value = (boolean) rate.get("refundable");
                    if (!value)
                    {
                        Assert.fail(" field refundable is not set to true in the response." + roomId);
                    }
                    else
                    {
                        String checkin = testContext.getParams(RequestType.SHOPPING).get("checkin");
                        String checkout = testContext.getParams(RequestType.SHOPPING).get("checkout");
                        ArrayList<LinkedHashMap> cancelPanaltyList = (ArrayList) rate.get("cancel_penalties");
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
        for (LinkedHashMap<String, Object> responseMap : responseAsList)
        {
            ArrayList<LinkedHashMap> roomsArr = (ArrayList<LinkedHashMap>) responseMap.get("rooms");
            for (LinkedHashMap<String, Object> room : roomsArr)
            {
                ArrayList<LinkedHashMap> rateList = (ArrayList<LinkedHashMap>) room.get("rates");
                String roomId = (String) room.get("id");
                for (LinkedHashMap<String, Object> rate : rateList)
                {
                    ArrayList<LinkedHashMap> bedGroupsList = (ArrayList) rate.get("bed_groups");
                    for (LinkedHashMap<String,LinkedHashMap<String,LinkedHashMap<String,String>>> bedgroup : bedGroupsList)
                    {
                        String hrefLink = bedgroup.get("links").get("price_check").get("href");
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
        for (LinkedHashMap<String, Object> responseMap : responseAsList)
        {
            ArrayList<LinkedHashMap> roomsArr = (ArrayList<LinkedHashMap>) responseMap.get("rooms");
            for (LinkedHashMap<String, Object> room : roomsArr)
            {
                ArrayList<LinkedHashMap> rateList = (ArrayList<LinkedHashMap>) room.get("rates");
                String roomId = (String) room.get("id");
                for (LinkedHashMap<String, Object> rate : rateList)
                {
                    LinkedHashMap<String, LinkedHashMap> linksMap = (LinkedHashMap) rate.get("links");
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
        for (LinkedHashMap<String, Object> responseMap : responseAsList)
        {
            ArrayList<LinkedHashMap> roomsArr = (ArrayList<LinkedHashMap>) responseMap.get("rooms");
            for (LinkedHashMap<String, Object> room : roomsArr)
            {
                ArrayList<LinkedHashMap> rateList = (ArrayList<LinkedHashMap>) room.get("rates");
                String roomId = (String) room.get("id");
                for (LinkedHashMap<String, Object> rate : rateList)
                {
                    String value = (String) rate.get("merchant_of_record");
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
        for (LinkedHashMap<String, Object> responseMap : responseAsList)
        {
            ArrayList<LinkedHashMap> roomsArr = (ArrayList<LinkedHashMap>) responseMap.get("rooms");
            for (LinkedHashMap<String, Object> room : roomsArr)
            {
                ArrayList<LinkedHashMap> rateList = (ArrayList<LinkedHashMap>) room.get("rates");
                String roomId = (String) room.get("id");
                for (LinkedHashMap<String, Object> rate : rateList)
                {
                    int numAvailableRooms = (Integer) rate.get("available_rooms");
                    if (numAvailableRooms == 0)
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
            requestPropIds.forEach(id->{
                if(!requestPropIds.contains(id))
                    Assert.fail("The propertyId: "+id+ " is not present in the request");
            });
        }
        else
        {
            Assert.fail("Property Ids in the response is more than the requested response");
        }
    }

}
