package com.hopper.tests.util.validations;

import com.google.common.collect.ImmutableList;
import com.hopper.tests.constants.RequestType;
import com.hopper.tests.model.TestContext;
import com.hopper.tests.util.validations.constants.ResponseValidationField;
import io.restassured.response.Response;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;

import javax.validation.constraints.NotNull;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.*;

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

    public static void validateArraySize(final Response restResponse, final String field, final int expectedSize)
    {
        final List<Object> fieldValues = restResponse.jsonPath().get(field);
        final String errorMessage = "Expected Array size for element \"" + field + " is: " + expectedSize + "and actual size is : " + fieldValues.size();
        Assert.assertTrue(errorMessage, expectedSize == fieldValues.size());
    }

    public static void validateArraySizeBetweenVal(final RequestType requestType, final TestContext context, String validationField, String minValue, String maxValue)
    {
        if (requestType == RequestType.SHOPPING)
        {
            switch (validationField)
            {
                case "available_rooms":
                    ShoppingResponseValidationUtil.validate(context, ResponseValidationField.valueOf(validationField), ImmutableList.of(maxValue));
                    break;
            }
        }

    }

    public static void validateNodeForValues(final Response restResponse, final String node, List<String> expectedValues)
    {
        switch (node)
        {
            case "occupancy_SHOPPING":
                validateOccupancy(restResponse, expectedValues);
                break;
            case "occupancy_PREBOOKING":
                validateOccupancyPreCheck(restResponse, expectedValues);
                break;
            case "nighthly_SHOPPING":
                validateNightlyArrSize(restResponse, expectedValues.get(0));
                break;
            case "nightly_PRE_BOOKING":
                validateNightlyArrSizePreCheck(restResponse, expectedValues.get(0));
                break;
            case "currency_SHOPPING":
                validateCurrencyCode(restResponse, expectedValues.get(0));
                break;
            case "fenced_deal_SHOPPING":
                validateFencedDeal(restResponse, expectedValues.get(0));
        }
    }

    private static void validateStayNodePreBooking(Response restResponse, List<String> expectedValues)
    {
        HashMap<String, HashMap> roomPriceCheckMap = restResponse.jsonPath().get(".");
        HashMap<String, HashMap> occupancies = roomPriceCheckMap.get("occupancies");
        for (Map.Entry<String, HashMap> occupancy : occupancies.entrySet())
        {

            HashMap<String, ArrayList> roomRates = occupancy.getValue();
            ArrayList<HashMap> stayNodeList = roomRates.get("stay_node");
            final String errorMessage = "Expected Stay node is" + expectedValues + "and actual values are : " + stayNodeList;
            if (stayNodeList != null)
            {
                for (HashMap stayNode : stayNodeList)
                {
                    String type = (String) stayNode.get("type");
                    Assert.assertTrue(errorMessage, expectedValues.contains(type));
                }
            }
        }

    }

    private static void validateOccupancyPreCheck(Response restResponse, List<String> expectedValues)
    {
        HashMap<String, HashMap> roomPriceCheckMap = restResponse.jsonPath().get(".");
        HashMap<String, HashMap> occupancies = roomPriceCheckMap.get("occupancies");
        List<String> responseValues = new ArrayList<String>(occupancies.keySet());
        final String errorMessage = "Expected Occupancies is" + expectedValues + "and actual values are : " + responseValues;
        Assert.assertTrue(errorMessage, CollectionUtils.isEqualCollection(responseValues, expectedValues));

    }

    private static void validateNightlyArrSizePreCheck(Response restResponse, String expectedValue)
    {
        HashMap<String, HashMap> roomPriceCheckMap = restResponse.jsonPath().get(".");
        HashMap<String, HashMap> occupancies = roomPriceCheckMap.get("occupancies");
        for (Map.Entry<String, HashMap> occupancy : occupancies.entrySet())
        {
            HashMap roomRates = occupancy.getValue();
            ArrayList list = (ArrayList) roomRates.get("nightly");
            Assert.assertTrue(list.size() == Integer.parseInt(expectedValue));
        }
    }

    public static void validateFieldValueNotEqualTo(final TestContext context, final RequestType requestType, String field, String value)
    {
        switch (requestType)
        {
            case SHOPPING:
            {
                ShoppingResponseValidationUtil.validate(context, ResponseValidationField.valueOf(field), ImmutableList.of(value));
                break;
            }
            case PAYMENT_OPTIONS:
            {
                PaymentOptionsResponseValidationUtil.validate(context, ResponseValidationField.valueOf(field));
                break;
            }
            case PRE_BOOKING:
            {
                PreBookingValidationUtil.validateField(context, ResponseValidationField.valueOf(field), null);
                break;
            }
            default:
            {
                throw new UnsupportedOperationException("Request Type : [" + requestType.name() + "], unsupported");
            }
        }
    }

    private static void validateOccupancy(final Response restResponse, final List<String> expectedValues)
    {
        ArrayList<LinkedHashMap> responseAsList = restResponse.as(ArrayList.class);
        for (LinkedHashMap<String, Object> responseMap : responseAsList)
        {
            ArrayList<LinkedHashMap> roomsArr = (ArrayList<LinkedHashMap>) responseMap.get("rooms");
            for (LinkedHashMap<String, Object> room : roomsArr)
            {
                ArrayList<LinkedHashMap> rateList = (ArrayList<LinkedHashMap>) room.get("rates");
                String roomId = (String) room.get("id");
                for (LinkedHashMap<String, Object> rate : rateList)
                {
                    LinkedHashMap<String, Object> occupancies = (LinkedHashMap) rate.get("occupancies");
                    List<String> responseValues = new ArrayList<String>(occupancies.keySet());
                    final String errorMessage = "Expected Occupancies for rate id \"" + roomId + " is: " + expectedValues + "and actual values are : " + responseValues;
                    Assert.assertTrue(errorMessage, CollectionUtils.isEqualCollection(responseValues, expectedValues));
                }

            }

        }
    }

    public static void validateResponseBodyForNode(final String node, final RequestType requestType, final TestContext context) throws ParseException
    {
        switch (requestType)
        {
            case SHOPPING:
                ShoppingResponseValidationUtil.validate(context, ResponseValidationField.valueOf(node), null);
                break;
            default:
                System.out.println("Request Not Present");

        }
    }


    private static void validateCurrencyCode(Response response, String expectedValue)
    {
        ArrayList<LinkedHashMap> responseAsList = response.as(ArrayList.class);

        for (LinkedHashMap<String, Object> responseMap : responseAsList)
        {

            ArrayList<LinkedHashMap> roomsArr = (ArrayList<LinkedHashMap>) responseMap.get("rooms");
            for (LinkedHashMap<String, Object> room : roomsArr)
            {

                ArrayList<LinkedHashMap> rateList = (ArrayList<LinkedHashMap>) room.get("rates");
                String roomId = (String) room.get("id");
                for (LinkedHashMap<String, Object> rate : rateList)
                {

                    LinkedHashMap<String, LinkedHashMap> occupancies = (LinkedHashMap) rate.get("occupancies");
                    for (Map.Entry<String, LinkedHashMap> occupancy : occupancies.entrySet())
                    {
                        LinkedHashMap<String, Object> roomRates = occupancy.getValue();
                        ArrayList<ArrayList> nightlyList = (ArrayList) roomRates.get("nightly");
                        for (ArrayList<LinkedHashMap> nightly : nightlyList)
                        {

                            for (LinkedHashMap<String, String> map : nightly)
                            {

                                String currency = map.get("currency");
                                if (!expectedValue.equals(currency))
                                    Assert.fail("Response currency in nightly does not match with requested currency for room_id: " + roomId);
                            }
                        }

                        LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, String>>> totals = (LinkedHashMap) roomRates.get("totals");

                        String currencyInclusive = totals.get("inclusive").get("billable_currency").get("currency");
                        if (!expectedValue.equals(currencyInclusive))
                        {
                            Assert.fail("Response currency in totals inclusive does not match with requested currency for room_id: " + roomId);
                        }
                        String currencyExclusive = totals.get("exclusive").get("billable_currency").get("currency");
                        if (!expectedValue.equals(currencyExclusive))
                        {
                            Assert.fail("Response currency in totals exclusive does not match with requested currency for room_id: " + roomId);
                        }
                        if (totals.get("strikethrough") != null)
                        {
                            String currencyStrikethrough = totals.get("strikethrough").get("billable_currency").get("currency");
                            if (!expectedValue.equals(currencyStrikethrough))
                            {
                                Assert.fail("Response currency in totals strikethrough does not match with requested currency for room_id: " + roomId);
                            }
                        }
                        if (totals.get("marketing_fee") != null)
                        {
                            String currencyMarketing = totals.get("marketing_fee").get("billable_currency").get("currency");
                            if (!expectedValue.equals(currencyMarketing))
                            {
                                Assert.fail("Response currency in totals marketing does not match with requested currency for room_id: " + roomId);
                            }
                        }
                        if (totals.get("minimum_selling_price") != null)
                        {
                            String currencySP = totals.get("minimum_selling_price").get("billable_currency").get("currency");
                            if (!expectedValue.equals(currencySP))
                            {
                                Assert.fail("Response currency in totals selling_price does not match with requested currency for room_id: " + roomId);
                            }
                        }
                    }
                }
            }
        }
    }

    private static void validateTotalPrice(Response response)
    {
        ArrayList<LinkedHashMap> responseAsList = response.as(ArrayList.class);
        DecimalFormat df = new DecimalFormat("###.##");
        for (LinkedHashMap<String, Object> responseMap : responseAsList)
        {

            ArrayList<LinkedHashMap> roomsArr = (ArrayList<LinkedHashMap>) responseMap.get("rooms");
            for (LinkedHashMap<String, Object> room : roomsArr)
            {

                ArrayList<LinkedHashMap> rateList = (ArrayList<LinkedHashMap>) room.get("rates");
                String roomId = (String) room.get("id");
                for (LinkedHashMap<String, Object> rate : rateList)
                {

                    LinkedHashMap<String, LinkedHashMap> occupancies = (LinkedHashMap) rate.get("occupancies");
                    for (Map.Entry<String, LinkedHashMap> occupancy : occupancies.entrySet())
                    {
                        Double baseRate = 0.0;
                        Double taxRate = 0.0;
                        Double extraPersonfee = 0.0;
                        LinkedHashMap<String, Object> roomRates = occupancy.getValue();
                        ArrayList<ArrayList> list = (ArrayList) roomRates.get("nightly");
                        for (ArrayList<LinkedHashMap> n : list)
                        {

                            for (LinkedHashMap map : n)
                            {

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
                                !billableExclusiveTotal.equals(expectedBillableExTotal))
                        {

                            Assert.fail("Expected totals does not match billableTotals for room_id: " + roomId);

                        }
                    }

                }
            }
        }
    }

    private static void validateNightlyArrSize(Response response, String expectedValue)
    {
        ArrayList<LinkedHashMap> responseAsList = response.as(ArrayList.class);
        for (LinkedHashMap<String, Object> responseMap : responseAsList)
        {

            ArrayList<LinkedHashMap> roomsArr = (ArrayList<LinkedHashMap>) responseMap.get("rooms");
            for (LinkedHashMap<String, Object> room : roomsArr)
            {

                ArrayList<LinkedHashMap> rateList = (ArrayList<LinkedHashMap>) room.get("rates");
                String roomId = (String) room.get("id");
                for (LinkedHashMap<String, Object> rate : rateList)
                {

                    LinkedHashMap<String, LinkedHashMap> occupancies = (LinkedHashMap) rate.get("occupancies");
                    for (Map.Entry<String, LinkedHashMap> e : occupancies.entrySet())
                    {

                        LinkedHashMap<String, Object> s = e.getValue();
                        ArrayList<ArrayList> list = (ArrayList) s.get("nightly");
                        Assert.assertTrue(list.size() == Integer.parseInt(expectedValue));
                    }
                }
            }
        }

    }

    private static void validateStayNode(Response response, List<String> expectedValues)
    {
        ArrayList<LinkedHashMap> responseAsList = response.as(ArrayList.class);
        for (LinkedHashMap<String, Object> responseMap : responseAsList)
        {

            ArrayList<LinkedHashMap> roomsArr = (ArrayList<LinkedHashMap>) responseMap.get("rooms");
            for (LinkedHashMap<String, Object> room : roomsArr)
            {

                ArrayList<LinkedHashMap> rateList = (ArrayList<LinkedHashMap>) room.get("rates");
                String roomId = (String) room.get("id");
                for (LinkedHashMap<String, Object> rate : rateList)
                {
                    LinkedHashMap<String, LinkedHashMap> occupancies = (LinkedHashMap) rate.get("occupancies");
                    for (Map.Entry<String, LinkedHashMap> occupancy : occupancies.entrySet())
                    {
                        LinkedHashMap<String, Object> s = occupancy.getValue();
                        ArrayList<LinkedHashMap> stayNodes = (ArrayList) s.get("stay");
                        if (stayNodes != null)
                        {
                            for (LinkedHashMap stayNode : stayNodes)
                            {
                                String type = (String) stayNode.get("type");
                                boolean contains = expectedValues.contains(type);
                                if (!contains)
                                {
                                    Assert.fail("type: " + type + " is invalid for room_id: " + roomId);
                                }
                            }
                        }
                    }

                }
            }
        }
    }

    private static void validateFencedDeal(Response response, String expectedValue)
    {
        ArrayList<LinkedHashMap> responseAsList = response.as(ArrayList.class);
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
                    if (value != Boolean.valueOf(expectedValue))
                    {
                        Assert.fail("fenced_deal is true for roomId: " + roomId);
                    }
                }
            }
        }
    }

    private static void validateMerchantOfRecord(Response response, List<String> values)
    {
        ArrayList<LinkedHashMap> responseAsList = response.as(ArrayList.class);
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

                    if (!values.contains(value))
                    {
                        Assert.fail(" merchant record field does not match any of the expected values for roomId: " + roomId);
                    }
                }
            }
        }
    }

    public static void validateFieldValueBelongsToExpectedValues(String field, TestContext context, RequestType requestType, List<String> values)
    {
        switch (field)
        {
            case "merchant_of_record_SHOPPING":
                ShoppingResponseValidationUtil.validate(context, ResponseValidationField.valueOf(field), values);
                break;
            case "nightly_type_SHOPPING":
                //validateNightlyTypes(response, values);
                break;
            case "stay_node_SHOPPING":
                //validateStayNode(response, values);
                break;
            case "stay_node_PRE_BOOKING":
                //validateStayNodePreBooking(response, values);
            case "status_PRE_BOOKING":
                PreBookingValidationUtil.validateField(context, ResponseValidationField.valueOf(field), values);
                break;
        }
    }

    private static void validateNightlyTypes(Response response, List<String> expectedValues)
    {

        ArrayList<LinkedHashMap> responseAsList = response.as(ArrayList.class);
        for (LinkedHashMap<String, Object> responseMap : responseAsList)
        {

            ArrayList<LinkedHashMap> roomsArr = (ArrayList<LinkedHashMap>) responseMap.get("rooms");
            for (LinkedHashMap<String, Object> room : roomsArr)
            {

                ArrayList<LinkedHashMap> rateList = (ArrayList<LinkedHashMap>) room.get("rates");
                String roomId = (String) room.get("id");
                for (LinkedHashMap<String, Object> rate : rateList)
                {

                    LinkedHashMap<String, LinkedHashMap> occupancies = (LinkedHashMap) rate.get("occupancies");
                    for (Map.Entry<String, LinkedHashMap> e : occupancies.entrySet())
                    {

                        LinkedHashMap<String, Object> s = e.getValue();
                        ArrayList<ArrayList> list = (ArrayList) s.get("nightly");
                        for (ArrayList<LinkedHashMap> n : list)
                        {

                            for (LinkedHashMap map : n)
                            {
                                String type = (String) map.get("type");
                                boolean contains = expectedValues.contains(type);
                                if (!contains)
                                {
                                    Assert.fail("type: " + type + " is invalid for room_id: " + roomId);
                                }
                            }
                        }
                    }
                }
            }
        }

    }


    public static void validateNodeInResponseBody(String field, Response response)
    {
        switch (field)
        {
            case "totals_SHOPPING":
                validateTotalPrice(response);
                break;
            case "totals_PRE_BOOKING":
                validateTotalPricePreBooking(response);
        }
    }

    private static void validateTotalPricePreBooking(Response response)
    {

        DecimalFormat df = new DecimalFormat("###.##");
        HashMap<String, HashMap> roomPriceCheckMap = response.jsonPath().get(".");
        HashMap<String, HashMap> occupancies = roomPriceCheckMap.get("occupancies");
        for (Map.Entry<String, HashMap> occupancy : occupancies.entrySet())
        {
            Double baseRate = 0.0;
            Double taxRate = 0.0;
            Double extraPersonfee = 0.0;
            Double adjustment = 0.0;
            HashMap roomRates = occupancy.getValue();
            ArrayList<ArrayList> list = (ArrayList) roomRates.get("nightly");
            for (ArrayList<HashMap> n : list)
            {
                for (HashMap map : n)
                {

                    String value = (String) map.get("value");
                    if (map.get("type").equals("base_rate"))
                        baseRate = baseRate + Double.parseDouble(value);
                    else if (map.get("type").equals("extra_person_fee"))
                        extraPersonfee = extraPersonfee + Double.parseDouble(value);
                    else if (map.get("type").equals("adjustment"))
                        adjustment = adjustment + Double.parseDouble(value);
                    else
                        taxRate = taxRate + Double.parseDouble(value);
                }
            }

            HashMap<String, HashMap<String, HashMap<String, String>>> j = (HashMap) roomRates.get("totals");
            Double billableInclusiveTotal = Double.parseDouble(j.get("inclusive").get("billable_currency").get("value"));
            Double expectedBillableIncTotal = Double.parseDouble(df.format(baseRate + taxRate + extraPersonfee + adjustment));
            Double billableExclusiveTotal = Double.parseDouble(j.get("exclusive").get("billable_currency").get("value"));
            Double expectedBillableExTotal = Double.parseDouble(df.format(baseRate + extraPersonfee + adjustment));
            if (!billableInclusiveTotal.equals(expectedBillableIncTotal) ||
                    !billableExclusiveTotal.equals(expectedBillableExTotal))
            {

                Assert.fail("Expected totals does not match billableTotals: ");

            }
        }
    }


    public static void validateFieldsInResponseBody(String field1, Response response, String field2)
    {
        switch (field1)
        {
            case "billable_currency":
                validateBillableCurrency(response, field2);
                break;
        }
    }

    private static void validateBillableCurrency(Response response, String field)
    {
        ArrayList<LinkedHashMap> responseAsList = response.as(ArrayList.class);

        for (LinkedHashMap<String, Object> responseMap : responseAsList)
        {

            ArrayList<LinkedHashMap> roomsArr = (ArrayList<LinkedHashMap>) responseMap.get("rooms");
            for (LinkedHashMap<String, Object> room : roomsArr)
            {

                ArrayList<LinkedHashMap> rateList = (ArrayList<LinkedHashMap>) room.get("rates");
                String roomId = (String) room.get("id");
                for (LinkedHashMap<String, Object> rate : rateList)
                {

                    LinkedHashMap<String, LinkedHashMap> occupancies = (LinkedHashMap) rate.get("occupancies");
                    for (Map.Entry<String, LinkedHashMap> occupancy : occupancies.entrySet())
                    {

                        LinkedHashMap<String, Object> roomRates = occupancy.getValue();
                        LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, String>>> totals = (LinkedHashMap) roomRates.get("totals");
                        String currencyInclusiveBillable = totals.get("inclusive").get("billable_currency").get("currency");
                        String currencyInclusiveRequested = totals.get("inclusive").get(field).get("currency");
                        if (!currencyInclusiveBillable.equals(currencyInclusiveRequested))
                        {

                            Assert.fail("Billable currency in totals inclusive does not match with requested currency for room_id: " + roomId);

                        }

                        String currencyExclusiveBillable = totals.get("exclusive").get("billable_currency").get("currency");
                        String currencyExclusiveRequested = totals.get("exclusive").get(field).get("currency");
                        if (!currencyExclusiveBillable.equals(currencyExclusiveRequested))
                        {

                            Assert.fail("Billable currency in totals exclusive does not match with requested currency for room_id: " + roomId);

                        }
                    }
                }
            }
        }
    }
}
