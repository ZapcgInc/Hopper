package com.hopper.tests.stepImplementation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.reflect.TypeToken;
import gherkin.deps.com.google.gson.Gson;
import gherkin.deps.com.google.gson.JsonObject;
import net.sf.json.JSONString;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.Assert;

import com.hopper.tests.Request;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import javax.validation.constraints.AssertTrue;

import static com.sun.corba.se.impl.util.Version.asString;
import static io.restassured.RestAssured.with;

import java.io.IOException;
import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ScenarioImpl {
	
    private static boolean isWebAppRunning = false;
	Request request;
    private Response restResponse = null;
    JSONObject jsonObject = null;
    String restResponseString = null;
    ArrayList<LinkedHashMap> responseAsList;
    

	public void setRequest(Request request) {
		this.request = request;	
	}
	
    public void checkIfAppRunning() {
       	if(!isWebAppRunning) {
	        try {
	        	restResponse = with().get(request.getUrl());
	        } catch (Exception e) {
	            if (e.getMessage().contains("Connection refused")) {
	                //System.out.println("Web application is not running.");
	                Assert.fail("ERROR :Web application is not running. So cannot test functionality.");
	                return;
	            } else {
	                System.out.println("Unknown exception while invoking this request.");
	                e.printStackTrace();
	            }
	        }
	        isWebAppRunning = true;
       	}
    }
	
    public void raiseHTTPRequest(String httpMethod) {
    	io.restassured.RestAssured.defaultParser = io.restassured.parsing.Parser.JSON;
    	RequestSpecification reqspecs = with().headers(request.getHeaders());
    	System.out.println(request.getHeaders());
    	if(request.getParams() != null) {
    		reqspecs.queryParams(request.getParams());
    	}
    	if(request.getParamWithMulitpleValues() != null) {
	 		for (String param : request.getParamWithMulitpleValues().keySet()) {
	 			reqspecs.queryParam(param, request.getParamWithMulitpleValues().get(param));
	 		}
    	}
    	System.out.println(reqspecs.log().all());
        switch (httpMethod) {
            case "GET":
                try {
                	restResponse = reqspecs.get(request.getEndPoint());
                    responseAsList =  restResponse.as(ArrayList.class);
                	System.out.println("Response: "+ restResponse.asString());
                	System.out.println("Response Headers:" + restResponse.getHeaders());
                } catch (Exception e) {
                    if (e.getMessage().contains("Connection refused")) {
                        Assert.fail("Web application is not running.");
                        return;
                    } else {
                        Assert.fail("Unknown exception while invoking this request.");
                        e.printStackTrace();
                    }
                    return;
                }
                break;
            default:
                Assert.fail("This type of method is not supported yet.");
        }
    }
    
    public void validateHTTPresponseCode(int expectedCode) {
        int responseCode = restResponse.getStatusCode();        
        if (responseCode != expectedCode) {
            Assert.fail("Expected response code : " + expectedCode +
                    " and actual response code : " + responseCode +
                    " are not matching");
        }
    }
    
    
    public void validateResponseBody(Map<String, String> expectedResponseMap, String field) {        
        Map<String, String> fieldResponseMap = (Map<String, String>) restResponse.jsonPath().get(field);
             
        for (String key : expectedResponseMap.keySet()) {
        	String actualValue = (String)fieldResponseMap.get(key);
        	String expectedValue = (String)expectedResponseMap.get(key);
        	Assert.assertTrue(expectedValue.equals(actualValue));
        }
    }

    public void validateResponseBodyForNode(String node) throws ParseException {
        switch(node){
            case "property_id":
                validatePropertyId();
                break;
            case "available_rooms_for_all_room_type":
                validateAvailableRooms();
                break;
            case "merchant_of_record":
                validateMerchantOfRecord();
                break;
            case "href_price_check":
                validateHrefPriceCheck();
                break;
            case "href_payment_options":
                validateHrefPaymentOption();
                break;
            case "response_cancel_policies_for_refundable_rates":
                validateResponseCancelPoliciesForRefundableRates();
                break;
            case "promo_fields":
                validatePromoFields();
                break;
            case "amenities":
                validateAminities();
            case "fenced_deal":
                validateFencedDeal();
            case "occupancy":
                validateOccupancy();
            default:
                System.out.println("something not supported");
        }
    }

    private void validateOccupancy() {
        List<String> requestOccupancies = new ArrayList<>();
        ArrayList<String> responseOccupancies = new ArrayList<>();
        for (LinkedHashMap<String,Object> m: responseAsList) {
            ArrayList<LinkedHashMap> roomsArr = (ArrayList<LinkedHashMap>) m.get("rooms");
            for (LinkedHashMap<String, Object> l : roomsArr) {
                ArrayList<LinkedHashMap> rateList = (ArrayList<LinkedHashMap>) l.get("rates");
                String roomId = (String) l.get("id");
                for (LinkedHashMap<String, Object> k : rateList) {
                    LinkedHashMap occupancies = (LinkedHashMap) k.get("occupancies");
                    responseOccupancies.addAll(occupancies.keySet());
                }
                break;
            }
            break;
        }
        if(request.getParamWithMulitpleValues().get("occupancy")!=null){
            requestOccupancies.addAll(request.getParamWithMulitpleValues().get("occupancy"));
        }
        if(request.getParams().get("occupancy")!=null){
            requestOccupancies.add(request.getParams().get("occupancy"));
        }
        if (responseOccupancies.size()==requestOccupancies.size()) {
            Assert.assertTrue(CollectionUtils.isEqualCollection(responseOccupancies,requestOccupancies));
        } else {
            Assert.fail("Occupancy in the response mismatches with the requested occupancy");
        }
    }

    private void validateFencedDeal() {
        for (LinkedHashMap<String,Object> m: responseAsList) {
            ArrayList<LinkedHashMap> roomsArr = (ArrayList<LinkedHashMap>) m.get("rooms");
            for (LinkedHashMap<String, Object> l : roomsArr) {
                ArrayList<LinkedHashMap> rateList = (ArrayList<LinkedHashMap>) l.get("rates");
                String roomId = (String) l.get("id");
                for (LinkedHashMap<String, Object> k : rateList) {
                    Boolean value = (Boolean) k.get("fenced_deal");
                    if (value) {
                        Assert.fail("fenced_deal should be set to false for roomId: " + roomId);
                    }
                }
            }
        }
    }

    private void validatePromoFields(){

    }
    private void validateAminities() {
        for (LinkedHashMap<String, Object> m : responseAsList) {
            ArrayList<LinkedHashMap> roomsArr = (ArrayList<LinkedHashMap>) m.get("rooms");
            for (LinkedHashMap<String, Object> l : roomsArr) {
                ArrayList<LinkedHashMap> rateList = (ArrayList<LinkedHashMap>) l.get("rates");
                String roomId = (String) l.get("id");
                for (LinkedHashMap<String, Object> k : rateList) {
                    ArrayList<LinkedHashMap> amenities = (ArrayList<LinkedHashMap>) k.get("amenities");
                    if (!CollectionUtils.isEmpty(amenities)) {
                        for (LinkedHashMap<String, Object> amenity : amenities) {
                            Integer id = (Integer) amenity.get("id");
                            String name = (String) amenity.get("name");
                            if ((id != null && StringUtils.isEmpty(name)) || (id == null && StringUtils.isNotEmpty(name))) {
                                Assert.fail("amenity ID and description both should be present or both should be absent for a valid response.");
                            }
                        }
                    }
                }
            }
        }
    }
    private void validateResponseCancelPoliciesForRefundableRates() throws ParseException {
        for (LinkedHashMap<String,Object> m: responseAsList){
            ArrayList<LinkedHashMap> roomsArr= (ArrayList<LinkedHashMap>) m.get("rooms");
            for(LinkedHashMap<String,Object> l:roomsArr){
                ArrayList<LinkedHashMap> rateList = (ArrayList<LinkedHashMap>) l.get("rates");
                String roomId =(String) l.get("id");
                for(LinkedHashMap<String,Object> k: rateList){
                    boolean value = (boolean) k.get("refundable");
                    if(!value){
                        Assert.fail(" field refundable is not set to true in the response."+ roomId);
                    } else {
                        String checkin = request.getParams().get("checkin");
                        String checkout = request.getParams().get("checkout");
                        ArrayList<LinkedHashMap> cancelPanaltyList = (ArrayList) k.get("cancel_penalties");
                        for (LinkedHashMap<String,String> t: cancelPanaltyList){
                            String startDate = t.get("start");
                            String endDate = t.get("end");
                            if(!validateStartEndDate(checkin,checkout,startDate,endDate)){
                                Assert.fail("cancel policy start and end date are not within check in and check " +
                                        "out dates for roomId: "+roomId);
                            }

                        }
                    }
                }
            }
        }
    }

    private boolean validateStartEndDate(String checkin, String checkout, String startDate, String endDate) throws ParseException {
        boolean flag = false;
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if(sdf.parse(checkin).before(sdf.parse(startDate)) && sdf.parse(endDate).before(sdf.parse(checkout))) {
            flag = true;
        }
        return flag;
    }

    private void validateHrefPriceCheck(){
        for (LinkedHashMap<String,Object> m: responseAsList){
            ArrayList<LinkedHashMap> roomsArr= (ArrayList<LinkedHashMap>) m.get("rooms");
            for(LinkedHashMap<String,Object> l:roomsArr){
                ArrayList<LinkedHashMap> rateList = (ArrayList<LinkedHashMap>) l.get("rates");
                String roomId =(String) l.get("id");
                for(LinkedHashMap<String,Object> k: rateList){
                    ArrayList<LinkedHashMap> bedGroupsList = (ArrayList)k.get("bed_groups");
                    for(LinkedHashMap b: bedGroupsList){
                        LinkedHashMap c = (LinkedHashMap) b.get("links");
                        LinkedHashMap d = (LinkedHashMap) c.get("price_check");
                        String hrefLink = (String) d.get("href");
                        if(StringUtils.isEmpty(hrefLink)){
                           Assert.fail("hrefLink empty for roomId"+ roomId);
                    }
                    }
                }
            }
        }
    }
    private void validateHrefPaymentOption(){
        for (LinkedHashMap<String,Object> m: responseAsList){
            ArrayList<LinkedHashMap> roomsArr= (ArrayList<LinkedHashMap>) m.get("rooms");
            for(LinkedHashMap<String,Object> l:roomsArr){
                ArrayList<LinkedHashMap> rateList = (ArrayList<LinkedHashMap>) l.get("rates");
                String roomId =(String) l.get("id");
                for(LinkedHashMap<String,Object> k: rateList){
                    LinkedHashMap<String,LinkedHashMap> linksMap = (LinkedHashMap) k.get("links");
                    LinkedHashMap<String,String> paymentOptionsMap = linksMap.get("payment_options");
                    String hrefLink = paymentOptionsMap.get("href");
                    if(StringUtils.isEmpty(hrefLink)){
                        Assert.fail("hrefLink empty for roomId"+ roomId);
                    }
                }
            }
        }
    }
    private void validateMerchantOfRecord(){
        for (LinkedHashMap<String,Object> m: responseAsList){
            ArrayList<LinkedHashMap> roomsArr= (ArrayList<LinkedHashMap>) m.get("rooms");
            for(LinkedHashMap<String,Object> l:roomsArr){
                ArrayList<LinkedHashMap> rateList = (ArrayList<LinkedHashMap>) l.get("rates");
                String roomId =(String) l.get("id");
                for(LinkedHashMap<String,Object> k: rateList){
                    String value = (String) k.get("merchant_of_record");
                    if(!("expedia".equals(value)||"property".equals(value))){
                        Assert.fail(" merchant record field matches the partner name or property for room_id: "+ roomId);
                    }
                }
            }
        }
    }

    private void validateAvailableRooms() {
        for (LinkedHashMap<String,Object> m: responseAsList){
            ArrayList<LinkedHashMap> roomsArr= (ArrayList<LinkedHashMap>) m.get("rooms");
            for(LinkedHashMap<String,Object> l:roomsArr){
                ArrayList<LinkedHashMap> n = (ArrayList<LinkedHashMap>) l.get("rates");
                String roomId =(String) l.get("id");
                for(LinkedHashMap<String,Object> k: n){
                    int b = (Integer) k.get("available_rooms");
                    if(b==0){
                        Assert.fail("Number of available rooms in the response is 0 for room_id: "+ roomId);
                    }
                }
            }
        }
    }

    private void validatePropertyId() {
	    List<String> requestPropIds = new ArrayList<>();
        ArrayList<String> responsePropIds = new ArrayList<>();
        for(LinkedHashMap response:responseAsList){
            String value = response.get("property_id").toString();
            responsePropIds.add(value);
        }
	    if(request.getParamWithMulitpleValues().get("property_id")!=null){
	        requestPropIds.addAll(request.getParamWithMulitpleValues().get("property_id"));
	    }
	    if(request.getParams().get("property_id")!=null){
	        requestPropIds.add(request.getParams().get("property_id"));
	    }
        if (responsePropIds.size()<=requestPropIds.size()) {
            Assert.assertTrue(CollectionUtils.containsAny(responsePropIds,requestPropIds));
        } else {
            Assert.fail("Property Ids in the response is more than the requested response");
        }
    }
}
