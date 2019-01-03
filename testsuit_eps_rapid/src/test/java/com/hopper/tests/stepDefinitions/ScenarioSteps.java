package com.hopper.tests.stepDefinitions;

import java.util.Map;

import com.hopper.tests.Request;
import com.hopper.tests.authorization.Authorization;
import com.hopper.tests.stepImplementation.ScenarioImpl;

import cucumber.api.DataTable;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;


public class ScenarioSteps {
	
    Request request;
    ScenarioImpl scenarioImpl;


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
    
    @Given("^queryParams are$")
    public void queryparams_are(DataTable params) throws Throwable {
       	Map<String, String> paramsMap = params.asMap(String.class, String.class);	
       	request.setParams(paramsMap);
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
			request.getParams().remove(param);
		} else {
			request.getParams().put(param, value);
		}
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

}
