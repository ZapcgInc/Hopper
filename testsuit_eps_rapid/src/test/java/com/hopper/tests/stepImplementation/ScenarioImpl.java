package com.hopper.tests.stepImplementation;

import org.junit.Assert;

import com.hopper.tests.Request;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.with;

import java.util.List;
import java.util.Map;

public class ScenarioImpl {
	
    private static boolean isWebAppRunning = false;
	Request request;
    private Response restResponse = null;
    
    

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
}
