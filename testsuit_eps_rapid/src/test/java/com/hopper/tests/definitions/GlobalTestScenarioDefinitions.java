package com.hopper.tests.definitions;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.hopper.tests.authorization.Authorization;

import com.hopper.tests.constants.GlobalConstants;
import com.hopper.tests.constants.RequestType;
import com.hopper.tests.constants.SupportedPartners;
import com.hopper.tests.model.ShoppingResponse;
import com.hopper.tests.model.TestContext;
import com.hopper.tests.util.data.ResponseSupplierFactory;
import com.hopper.tests.util.parser.ShoppingResponseParser;
import com.hopper.tests.util.validations.CheckAPIAvailability;
import com.hopper.tests.util.validations.ResponseValidationUtil;
import cucumber.api.DataTable;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import io.restassured.response.Response;

/**
 * Implementation for Global Test scenarios definitions that can be shared across for testing different end points.
 */
public class GlobalTestScenarioDefinitions
{
    private TestContext m_testContext;
    private CheckAPIAvailability m_checkAPIAvailability;

    @Given("^setup for partner \"(.*?)\"$")
    public void setup_for_partner(final String partnerName)
    {
        m_testContext = new TestContext(SupportedPartners.valueOf(partnerName));
        m_checkAPIAvailability = new CheckAPIAvailability();
    }

    @Given("^API at \"(.*?)\"$")
    public void api_at(final String api)
    {
        m_testContext.setHost(api);
    }

    @Given("^for version \"(.*?)\"$")
    public void for_version(final String version)
    {
        if (version != null && !GlobalConstants.NULL_STRING.equalsIgnoreCase(version))
        {
            m_testContext.setVersion(version);
        }
    }

    @Given("^with request headers$")
    public void with_request_headers(final DataTable headers)
    {
        final Map<String, String> headerMap = headers.asMap(String.class, String.class);
        m_testContext.setHeaders(headerMap);
    }


    @Given("^Generate authHeaderKey with$")
    public void generate_authHeaderKey_with(DataTable authKeys)
    {
        final Map<String, String> authKeyMap = authKeys.asMap(String.class, String.class);
        final String authKey = Authorization.getAuthKey(m_testContext.getPartner(), authKeyMap);
        m_testContext.setAuthKey(authKey);
    }

    @Given("^with shopping query parameters$")
    public void with_shopping_query_parameters(final DataTable shoppingQueryParams)
    {
        final Map<String, String> shoppingQueryParamsMap = shoppingQueryParams.asMap(String.class, String.class);
        m_testContext.setParams(shoppingQueryParamsMap, RequestType.SHOPPING);
    }

    @Given("^Basic web application is running$")
    public void basic_web_application_is_running()
    {
        m_checkAPIAvailability.test(m_testContext);
    }

    @Given("^with shopping end point \"(.*?)\"$")
    public void with_shopping_end_point(String shoppingEndPoint)
    {
        m_testContext.setApiPath(RequestType.SHOPPING, shoppingEndPoint);
    }

    @When("^user sets header \"(.*?)\" value \"(.*?)\"$")
    public void user_sets_header_value(String header, String value)
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

    @When("^user sets queryParam \"(.*?)\" value \"(.*?)\"$")
    public void user_sets_queryParam_value(String param, String value)
    {
        if (value.equalsIgnoreCase(GlobalConstants.NULL_STRING))
        {
            m_testContext.removeParam(param, RequestType.SHOPPING);
            m_testContext.removeParamWithMultipleValues(param, RequestType.SHOPPING);
        }
        else
        {
            m_testContext.addParam(param, value, RequestType.SHOPPING);
        }
    }

    @Given("^set multiple values for queryParam \"(.*?)\" with \"(.*?)\"$")
    public void set_multiple_values_for_queryParam_with(String queryParam, String values)
    {
        final List<String> listOfValues = Arrays.stream(values.split(GlobalConstants.MULTI_VALUE_DELIMITER))
                .collect(Collectors.toList());

        m_testContext.addParamWithMultipleValues(queryParam, listOfValues, RequestType.SHOPPING);
    }
    @Given("^run shopping$")
    public void run_shopping()
    {
        final Response response = ResponseSupplierFactory.create(m_testContext, GlobalConstants.GET, RequestType.SHOPPING).get();
        m_testContext.setResponse(RequestType.SHOPPING, response);
    }

    @When("^run preBooking$")
    public void run_preBooking()
    {
        final ShoppingResponse shoppingResponse = ShoppingResponseParser.parse(m_testContext.getResponse(RequestType.SHOPPING));
        m_testContext.setApiPath(RequestType.PRE_BOOKING, shoppingResponse.getPriceCheckEndPoint());

        final Response response = ResponseSupplierFactory.create(m_testContext,
                GlobalConstants.GET,
                RequestType.PRE_BOOKING).get();

        m_testContext.setResponse(RequestType.PRE_BOOKING, response);
    }

    @Then("^the response code for \"(.*?)\" should be (\\d+)$")
    public void the_response_code_for_should_be(String requestType, int expectedCode)
    {
        ResponseValidationUtil.validateHTTPResponseCode(
                m_testContext.getResponse(RequestType.valueOf(requestType)),
                expectedCode
        );
    }

    @Then("^the response code for \"(.*?)\" should be \"(.*?)\"$")
    public void the_response_code_for_should_be(String requestType, String expectedCode)
    {
        ResponseValidationUtil.validateHTTPResponseCode(
                m_testContext.getResponse(RequestType.valueOf(requestType)),
                Integer.parseInt(expectedCode)
        );
    }

    @Then("^user should see json response with paris on the filtered \"(.*?)\" node$")
    public void user_should_see_json_response_with_paris_on_the_filtered_node(String field, DataTable expectedResponse)
    {
        final Map<String, String> expectedResponseMap = expectedResponse.asMap(String.class, String.class);

        ResponseValidationUtil.validateResponseBody(
                m_testContext.getResponse(RequestType.SHOPPING),
                expectedResponseMap,
                field
        );
    }

}
