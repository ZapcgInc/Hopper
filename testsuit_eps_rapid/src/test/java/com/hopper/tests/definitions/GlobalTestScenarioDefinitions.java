package com.hopper.tests.definitions;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.hopper.tests.authorization.Authorization;

import com.hopper.tests.constants.GlobalConstants;
import com.hopper.tests.constants.RequestType;
import com.hopper.tests.constants.SupportedPartners;
import com.hopper.tests.model.TestContext;
import com.hopper.tests.util.data.ResponseSupplierFactory;
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
    private Supplier<Response> m_responseSupplier;

    @Given("^simple init$")
    public void simple_init()
    {
        m_testContext = new TestContext();
        m_checkAPIAvailability = new CheckAPIAvailability();
    }

    @Given("^partner is \"(.*?)\"$")
    public void partner_is(String partnerName) throws Throwable
    {
        try
        {
            SupportedPartners partner = SupportedPartners.valueOf(partnerName);
            m_testContext.setPartner(partner);
        }
        catch (Exception exp)
        {
            throw new Exception("Invalid Partner : " + partnerName);
        }
    }

    @Given("^web application endpoint url is \"(.*?)\"$")
    public void web_application_endpoint_url_is(String url)
    {
        m_testContext.setHost(url);
    }

    @Given("^version is \"(.*?)\"$")
    public void version_is(String version)
    {
        if (version != null && !GlobalConstants.NULL_STRING.equalsIgnoreCase(version))
        {
            m_testContext.setVersion(version);
        }
    }

    @Given("^headers are$")
    public void headers_are(DataTable headers)
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

    @Given("^queryParams are$")
    public void queryparams_are(DataTable params)
    {
        final Map<String, String> paramsMap = params.asMap(String.class, String.class);
        m_testContext.setParams(paramsMap, RequestType.SHOPPING);
    }

    @Given("^Basic web application is running$")
    public void basic_web_application_is_running()
    {
        m_checkAPIAvailability.test(m_testContext);
    }

    @When("^user sets GET request to \"(.*?)\"$")
    public void user_sets_GET_request_to(String uri)
    {
        m_testContext.setApiPath(RequestType.SHOPPING, uri);
    }

    @Given("^set shopping end point to \"(.*?)\"$")
    public void set_shopping_end_point_to(String uri) throws Throwable
    {
        m_testContext.setApiPath(RequestType.SHOPPING, uri);
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

    @When("^performs GET request$")
    public void performs_GET_request()
    {
        m_responseSupplier = ResponseSupplierFactory.create(m_testContext, GlobalConstants.GET, RequestType.SHOPPING);
    }

    @Then("^the response code should be \"(.*?)\"$")
    public void the_response_code_should_be(String responseCode)
    {
        final int expectedCode = Integer.valueOf(responseCode);
        ResponseValidationUtil.validateHTTPResponseCode(m_responseSupplier.get(), expectedCode);
    }

    @Then("^the response code should be (\\d+)$")
    public void the_response_code_should_be(int expectedCode)
    {
        ResponseValidationUtil.validateHTTPResponseCode(m_responseSupplier.get(), expectedCode);
    }

    @Then("^user should see json response with paris on the filtered \"(.*?)\" node$")
    public void user_should_see_json_response_with_paris_on_the_filtered_node(String field, DataTable expectedResponse)
    {
        final Map<String, String> expectedResponseMap = expectedResponse.asMap(String.class, String.class);

        ResponseValidationUtil.validateResponseBody(m_responseSupplier.get(), expectedResponseMap, field);
    }

}
