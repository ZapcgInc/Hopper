package com.hopper.tests.util.validations;

import io.restassured.response.Response;
import org.junit.Assert;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * These methods need to be clean up.
 */
public class CleanUpValidationUtil
{
    public static void validateBillableCurrency(Response response, String field)
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

    public static void validateTotalPricePreBooking(Response response)
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

    public static void validateCurrencyCode(Response response, String expectedValue)
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

    public static void validateTotalPrice(Response response)
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
}
