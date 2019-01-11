package com.hopper.tests.stepDefinitions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import com.hopper.tests.Request;
import com.hopper.tests.authorization.Authorization;
import com.hopper.tests.stepImplementation.ScenarioImpl;

import cucumber.api.DataTable;
import cucumber.api.java.ca.Cal;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;


public class ScenarioSteps {
	
    Request request;
    ScenarioImpl scenarioImpl;
	SimpleDateFormat sdf;

    @Given("^simple init$")
    public void simple_init() {
        request = new Request();
        scenarioImpl = new ScenarioImpl();
        scenarioImpl.setRequest(request);
    }
    
    @Given("^web application endpoint url is \"(.*?)\"$")
    public void web_application_endpoint_url_is(String url) throws Throwable {
        request.setUrl(url);
    }

    @Given("^version is \"(.*?)\"$")
    public void version_is(String version) throws Throwable {
    	if(version.equalsIgnoreCase("null")) {
    		version = null;
    	}
        request.setVersion(version);
    }


	@Given("^headers are$")
	public void headers_are(DataTable headers) throws Throwable {
		Map<String, String> headerMap = headers.asMap(String.class, String.class);
        request.setHeaders(headerMap);
	}

    @Given("^Generate authHeaderKey with$")
    public void generate_authHeaderKey_with(DataTable authKeys) {
    	Map<String, String> authKeyMap = authKeys.asMap(String.class, String.class);	
        request.setAuthKey(Authorization.getAuthKey(authKeyMap));
        request.getHeaders().put("Authorization", request.getAuthKey());        
    }
	@Given("^set dateFormat \"(.*?)\"$")
	public void set_dateFormat(String pattern) throws Throwable {
		sdf = new SimpleDateFormat(pattern);
	}

	@Given("^set checkin \"(.*?)\" from today with lengthOfStay \"(.*?)\"$")
	public void set_checkin_from_today_with_lengthOfStay(String numOfDaysFromToday, String lengthOfStay) throws Throwable {

    	if (!StringUtils.isEmpty(numOfDaysFromToday)){
			Calendar checkin = Calendar.getInstance();
			checkin.add(checkin.DATE, Integer.parseInt(numOfDaysFromToday));
			String checkinValue = convertToDate(checkin, sdf);
			request.getParams().put("checkin", checkinValue);
			if(!StringUtils.isEmpty(lengthOfStay)) {
				Calendar checkout = checkin;
				checkout.add(checkin.DATE, Integer.parseInt(lengthOfStay));
				String checkoutValue = convertToDate(checkout, sdf);
				request.getParams().put("checkout", checkoutValue);
			}
		}
	}
    
    @Given("^queryParams are$")
    public void queryparams_are(DataTable params) throws Throwable {
       	Map<String, String> paramsMap = params.asMap(String.class, String.class);
       	//Map<String,String> paramsMapFinal = new LinkedHashMap<>();
       	//paramsMapFinal.putAll(paramsMap);
       	//this method will set the checkin/checkout dates in case values are CHECK_IN/CHECK_OUT in the Background
		//setCheckInCheckOutDate(paramsMapFinal);
		request.setParams(paramsMap);
    }

	private void setCheckInCheckOutDate(Map<String, String> paramsMap) throws ParseException {
		Calendar checkin = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String value = null;
		if (paramsMap.get("checkin")!=null){
			if("CHECK_IN".equals(paramsMap.get("checkin"))) {
				checkin.add(checkin.DATE, 60);
				value = convertToDate(checkin, sdf);
				paramsMap.put("checkin",value);
			} else {
				Date date = sdf.parse(paramsMap.get("checkin"));
				checkin.setTime(date);
			}
		}
		if (paramsMap.get("checkout")!=null && "CHECK_OUT".equals(paramsMap.get("checkout"))) {
				Calendar checkout = checkin;
				checkout.add(checkin.DATE,5);
				value = convertToDate(checkout,sdf);
				paramsMap.put("checkout",value);
			}
		}


//		for(Map.Entry<String,String> m: paramsMap.entrySet()) {
//			String value = m.getValue();
//			if ("checkin".equals(m.getKey())) {
//				if ("CHECK_IN".equals(m.getValue())) {
//					checkin.add(checkin.DATE,60);
//					value = convertToDate(checkin,sdf);
//				} else {
//
//
//				}
//			} else if ("checkout".equals(m.getKey())) {
//				if ("CHECK_OUT".equals(m.getValue())) {
//					Calendar checkout = checkin;
//					checkout.add(checkin.DATE,5);
//					value = convertToDate(checkout,sdf);
//				}
//			}
//			paramsFinalMap.put(m.getKey(),value);
//		}
//		return paramsFinalMap;
//	}
	private String convertToDate(Calendar calendar, SimpleDateFormat dateFormat) {
		Date date = calendar.getTime();
		return dateFormat.format(date);
	}

	@Given("^Basic web application is running$")
	public void basic_web_application_is_running() throws Throwable {
		scenarioImpl.checkIfAppRunning();
	}	

	@When("^user sets GET request to \"(.*?)\"$")
	public void user_sets_GET_request_to(String uri) throws Throwable {
	   request.setUri(uri);
	}
	
	@When("^user sets header \"(.*?)\" value \"(.*?)\"$")
	public void user_sets_header_value(String header, String value) throws Throwable {
		if(value.equalsIgnoreCase("null")) {
			request.getHeaders().remove(header);
		} else {
			request.getHeaders().put(header, value);
		}

	}	
	
	@When("^user sets queryParam \"(.*?)\" value \"(.*?)\"$")
	public void user_sets_queryParam_value(String param, String value) throws Throwable {
		if(value.equalsIgnoreCase("null")) {
			if(request.getParams() != null) {
				request.getParams().remove(param);
			}
			if(request.getParamWithMulitpleValues() != null) {
				request.getParamWithMulitpleValues().remove(param);
			}
		} else {
			if(request.getParamWithMulitpleValues().containsKey(param)){
				request.getParamWithMulitpleValues().remove(param);
//				List<String> values = new ArrayList<>();
//				values.add(value);
//				request.getParamWithMulitpleValues().put(param,values);
			}
			request.getParams().put(param, value);
		}
	}



	@Given("^set mulitple values for queryParam \"(.*?)\" with \"(.*?)\"$")
	public void set_mulitple_values_for_queryParam_with(String queryParam, String values) throws Throwable {
		List<String> myList = new ArrayList<String>(Arrays.asList(values.split("\\|")));
		Map<String, List<String>> paramWithMultipleValues;
		if(request.getParamWithMulitpleValues() == null) {
			paramWithMultipleValues = new HashMap<String, List<String>>();
			request.setParamWithMulitpleValues(paramWithMultipleValues);
		}
		request.getParamWithMulitpleValues().put(queryParam, myList);
		System.out.println(queryParam+":"+myList);
	}
	
	
	@When("^performs GET request$")
	public void performs_GET_request() throws Throwable {
		scenarioImpl.raiseHTTPRequest("GET");
	}
	
	@Then("^the response code should be \"(.*?)\"$")
	public void the_response_code_should_be(String responseCode) throws Throwable {
		int expectedCode = Integer.valueOf(responseCode);
		scenarioImpl.validateHTTPresponseCode(expectedCode);
	}
	
	@Then("^the response code should be (\\d+)$")
	public void the_response_code_should_be(int expectedCode) throws Throwable {
		scenarioImpl.validateHTTPresponseCode(expectedCode);
	}
	
	
	@Then("^user should see json response with paris on the filterd \"(.*?)\" node$")
	public void user_should_see_json_response_with_paris_on_the_filterd_node(String field, DataTable expectedResponse) throws Throwable {
		Map<String, String> expectedResponseMap = expectedResponse.asMap(String.class, String.class);
		scenarioImpl.validateResponseBody(expectedResponseMap, field);
	}
	@Then("^user should get valid response for \"(.*?)\"$")
	public void user_should_get_valid_response_for(String node) throws Throwable {
		scenarioImpl.validateResponseBodyForNode(node);
	}


}
