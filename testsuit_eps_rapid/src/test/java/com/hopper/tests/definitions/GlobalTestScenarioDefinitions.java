package com.hopper.tests.definitions;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

import com.hopper.tests.authorization.Authorization;

import com.hopper.tests.constants.GlobalConstants;
import com.hopper.tests.constants.RequestType;
import com.hopper.tests.constants.SupportedPartners;
import com.hopper.tests.model.ShoppingResponse;
import com.hopper.tests.model.TestContext;
import com.hopper.tests.util.APIEndPointGenerator;
import com.hopper.tests.util.data.ResponseSupplierFactory;
import com.hopper.tests.util.parser.ShoppingResponseParser;
import com.hopper.tests.util.validations.CheckAPIAvailability;
import com.hopper.tests.util.validations.ResponseValidationUtil;
import cucumber.api.DataTable;
import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import io.restassured.response.Response;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;

/**
 * Implementation for Global Test scenarios definitions that can be shared across for testing different end points.
 */
public class GlobalTestScenarioDefinitions
{
    private TestContext m_testContext;
    private CheckAPIAvailability m_checkAPIAvailability;
	
    @Given("^setup for partner \"(.*?)\"$")
    public void setup(final String partnerName)
    {
        m_testContext = new TestContext(SupportedPartners.valueOf(partnerName));
        m_checkAPIAvailability = new CheckAPIAvailability();
    }

    @Given("^API at \"(.*?)\"$")
    public void setAPI(final String api)
    {
        m_testContext.setHost(api);
    }

    @Given("^for version \"(.*?)\"$")
    public void setVersion(final String version)
    {
        if (version != null && !GlobalConstants.NULL_STRING.equalsIgnoreCase(version))
        {
            m_testContext.setVersion(version);
        }
    }

    @Given("^with request headers$")
    public void setRequestHeaders(final DataTable headers)
    {
        final Map<String, String> headerMap = headers.asMap(String.class, String.class);
        m_testContext.setHeaders(headerMap);
    }


    @Given("^Generate authHeaderKey with$")
    public void generateAuthHeaderKey(final DataTable authKeys)
    {
        final Map<String, String> authKeyMap = authKeys.asMap(String.class, String.class);
        final String authKey = Authorization.getAuthKey(m_testContext.getPartner(), authKeyMap);
        m_testContext.setAuthKey(authKey);
    }

    @Given("^with shopping query parameters$")
    public void setShoppingQueryParameters(final DataTable shoppingQueryParams)
    {
        final Map<String, String> shoppingQueryParamsMap = shoppingQueryParams.asMap(String.class, String.class);
        m_testContext.setParams(shoppingQueryParamsMap, RequestType.SHOPPING);
    }

    @And("^with request DateFormat \"([^\"]*)\"$")
    public void setRequestDateFormat(final String dateFormat)
    {
        m_testContext.setRequestDateFormat(new SimpleDateFormat(dateFormat));
    }

    @Given("^Basic web application is running$")
    public void checkApiIsRunning()
    {
        m_checkAPIAvailability.test(m_testContext);
    }

    @Given("^with shopping end point \"(.*?)\"$")
    public void setShoppingEndPoint(final String shoppingEndPoint)
    {
        m_testContext.setApiPath(RequestType.SHOPPING, shoppingEndPoint);
    }

    @Given("^set checkin \"(.*?)\" from today with lengthOfStay \"(.*?)\"$")
    public void setCheckInDate(final String numOfDaysFromToday, final String lengthOfStay)
    {
        Assert.assertFalse(StringUtils.isEmpty(numOfDaysFromToday));
        Assert.assertFalse(StringUtils.isEmpty(lengthOfStay));

        final Calendar checkInDate = Calendar.getInstance();
        checkInDate.add(Calendar.DATE, Integer.parseInt(numOfDaysFromToday));

        final Calendar checkOutDate = Calendar.getInstance();
        checkOutDate.add(Calendar.DATE, Integer.parseInt(numOfDaysFromToday) + Integer.parseInt(lengthOfStay));

        m_testContext.addCheckInDate(checkInDate, RequestType.SHOPPING);
        m_testContext.addCheckOutDate(checkOutDate, RequestType.SHOPPING);
    }

    @When("^set header \"([^\"]*)\" value \"([^\"]*)\"$")
    public void setHeaderValue(final String header, final String value)
    {
        if (value.equalsIgnoreCase(GlobalConstants.NULL_STRING))
        {
            m_testContext.removeHeader(header);
        }
        else
        {
            m_testContext.addHeader(header, value);
        }
    }

    @When("^set \"([^\"]*)\" queryParam \"([^\"]*)\" value \"([^\"]*)\"$")
    public void setQueryParamValue(final String requestType, final String param, final String value)
    {
        if (value.equalsIgnoreCase(GlobalConstants.NULL_STRING))
        {
            m_testContext.removeParam(param, RequestType.SHOPPING);
            m_testContext.removeParamWithMultipleValues(param, RequestType.valueOf(requestType));
        }
        else
        {
            m_testContext.addParam(param, value, RequestType.valueOf(requestType));
        }
    }

    @And("^set multiple values for \"([^\"]*)\" queryParam \"([^\"]*)\" with \"([^\"]*)\"$")
    public void setMultipleValuesForQueryParamWith(final String requestType, final String queryParam, final String values)
    {
        final List<String> listOfValues = Arrays.stream(values.split(GlobalConstants.MULTI_VALUE_DELIMITER))
                .collect(Collectors.toList());

        m_testContext.addParamWithMultipleValues(queryParam, listOfValues, RequestType.valueOf(requestType));
    }

    @Given("^run shopping$")
    public void runShopping()
    {
        final Response response = ResponseSupplierFactory.create(m_testContext, GlobalConstants.GET, RequestType.SHOPPING).get();
        m_testContext.setResponse(RequestType.SHOPPING, response);
    }

    @When("^run preBooking$")
    public void runPreBooking()
    {
        final ShoppingResponse shoppingResponse = ShoppingResponseParser.parse(m_testContext.getResponse(RequestType.SHOPPING));
        m_testContext.setApiPath(RequestType.PRE_BOOKING, shoppingResponse.getPriceCheckEndPoint());

        final Response response = ResponseSupplierFactory.create(
                m_testContext,
                shoppingResponse.getPriceCheckHTTPMethod(),
                RequestType.PRE_BOOKING
        ).get();

        m_testContext.setResponse(RequestType.PRE_BOOKING, response);
    }

    @When("^run paymentOptions$")
    public void runPaymentOptions()
    {
        final ShoppingResponse shoppingResponse = ShoppingResponseParser.parse(m_testContext.getResponse(RequestType.SHOPPING));
        m_testContext.setApiPath(RequestType.PAYMENT_OPTIONS, shoppingResponse.getPaymentOptionsEndPoint());

        final Response response = ResponseSupplierFactory.create(
                m_testContext,
                shoppingResponse.getPaymentOptionsHTTPMethod(),
                RequestType.PAYMENT_OPTIONS
        ).get();

        m_testContext.setResponse(RequestType.PAYMENT_OPTIONS, response);
    }

    @Then("^the response code for \"(.*?)\" should be (\\d+)$")
    public void validateResponseCode(final String requestType, final int expectedCode)
    {
        ResponseValidationUtil.validateHTTPResponseCode(
                m_testContext.getResponse(RequestType.valueOf(requestType)),
                expectedCode
        );
    }

    @Then("^the response code for \"(.*?)\" should be \"(.*?)\"$")
    public void validateResponseCode(final String requestType, final String expectedCode)
    {
        ResponseValidationUtil.validateHTTPResponseCode(
                m_testContext.getResponse(RequestType.valueOf(requestType)),
                Integer.parseInt(expectedCode)
        );
    }

    @Then("^user should see json response with paris on the filtered \"(.*?)\" node$")
    public void user_should_see_json_response_with_paris_on_the_filtered_node(final String field, final DataTable expectedResponse)
    {
        final Map<String, String> expectedResponseMap = expectedResponse.asMap(String.class, String.class);

        ResponseValidationUtil.validateResponseBody(
                m_testContext.getResponse(RequestType.SHOPPING),
                expectedResponseMap,
                field
        );
    }

    @Then("^validate response element \"([^\"]*)\"$")
    public void validateResponseElement(final String nodeToValidate) throws Throwable
    {
        ResponseValidationUtil.validateResponseBodyForNode(nodeToValidate, m_testContext);
    }
    
    @Then("^the element \"(.*?)\" count per \"(.*?)\" for \"(.*?)\" should be (\\d+)$")
    public void the_element_count_per_for_should_be(String field, String arg2, String requestType, int expectedValue) throws Throwable {
    	ResponseValidationUtil.validateArraySize(m_testContext.getResponse(RequestType.valueOf(requestType)), field, expectedValue);
    }
    
    @Then("^validate \"(.*?)\" response element \"(.*?)\" matches values \"(.*?)\"$")
    public void validate_response_element_matches_values(String requestType, String node, String values) throws Throwable {
    	final List<String> listOfValues = Arrays.stream(values.split(GlobalConstants.MULTI_VALUE_DELIMITER))
                .collect(Collectors.toList());
    	ResponseValidationUtil.validateNodeforValues(m_testContext.getResponse(RequestType.valueOf(requestType)), node, listOfValues);
    }
    
	@Before
	public void before(Scenario scenario) {		
		printTestDetails(scenario);
	}
	
	private void printTestDetails(Scenario scenario){		
		log("======================================Scenario=================================================");
		log("Scearnio id: "+ scenario.getId() );
		log("Scenario name: "+ scenario.getName() );
		log("Scenario tage: "+ scenario.getSourceTagNames() );
		log("Scenario status: Started at :"+ new java.util.Date());
	}
	
	@After
	public void after(Scenario scenario) {
		printTestStatus(scenario);
	}
	
	private void printTestStatus(Scenario scenario) {
		log("Scnario success: " + (!scenario.isFailed()));
		log("Scenario status: " + scenario.getStatus() + " at :"+ new java.util.Date());
		if(scenario.isFailed()) {
			log("Scenario Execution Details :");
			printContext();
		}
	}
	
	private void printContext() {
		log("Host: " + m_testContext.getHost());
    	log("Version: " + m_testContext.getVersion());
    	for (RequestType reqType : RequestType.values()) {
    		String apiPath = m_testContext.getApiPath(reqType);
    		if(apiPath != null) {
    			log(reqType.toString());
    			log("     " + "API URI: "+ apiPath);
    			log("     " + "RequInfo:");
    			log("     " +  "    " + "End Point: "+ APIEndPointGenerator.create(m_testContext, reqType));
    			log("     " +  "    " + "Headers: "+ m_testContext.getHeaders());
    			log("     " +  "    " + "Query Params: "+ m_testContext.getParams(reqType));
    			log("     " +  "    " + "Query Params with multiple values: "+ m_testContext.getParamsWithMultipleValues(reqType));
    			log("     " + "Response:");
    			log("     " + "     " +  "Status Code: "+ m_testContext.getResponse(reqType).getStatusCode());
    			log("     " + "     " +  "Status Line: "+ m_testContext.getResponse(reqType).getStatusLine());
    			log("     " + "     " +  "Headers: "+ m_testContext.getResponse(reqType).getHeaders());
    			log("     " + "     " +  "Response Time: "+ m_testContext.getResponse(reqType).getTime());
    			log("     " + "     " +  "Contet Type: "+ m_testContext.getResponse(reqType).getContentType());
    			log("     " + "     " +  "Body: "+ m_testContext.getResponse(reqType).asString());
    			//log("     " + "     " +  "Body: "+ m_testContext.getResponse(reqType).getBody().jsonPath().prettyPrint());    			
    		}
    	}

	}
	
	private void log(String line) {
		System.out.println(line);
	}
}
