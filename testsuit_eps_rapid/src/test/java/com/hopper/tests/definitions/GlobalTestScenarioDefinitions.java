package com.hopper.tests.definitions;

import java.util.Map;

import com.hopper.tests.authorization.Authorization;

import com.hopper.tests.constants.SupportedPartners;
import com.hopper.tests.util.data.ShoppingTestDataSupplier;
import com.hopper.tests.util.validations.ResponseValidationUtil;
import cucumber.api.DataTable;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

/**
 * Implementation for Global Test scenarios definitions that can be shared across for testing different end points.
 */
public class GlobalTestScenarioDefinitions
{
    private ShoppingTestDataSupplier m_testDataSupplier;
    private SupportedPartners m_partner;

    @Given("^simple init$")
    public void simple_init()
    {
        m_testDataSupplier = new ShoppingTestDataSupplier();
    }

    @Given("^partner is \"(.*?)\"$")
    public void partner_is(String partner)
    {
        m_partner = SupportedPartners.valueOf(partner);
    }

    @Given("^web application endpoint url is \"(.*?)\"$")
    public void web_application_endpoint_url_is(String url)
    {
        m_testDataSupplier.withRequestHost(url);
    }

    @Given("^version is \"(.*?)\"$")
    public void version_is(String version)
    {
        if (version.equalsIgnoreCase("null"))
        {
            version = null;
        }
        m_testDataSupplier.withRequestVersion(version);
    }


    @Given("^headers are$")
    public void headers_are(DataTable headers)
    {
        final Map<String, String> headerMap = headers.asMap(String.class, String.class);
        m_testDataSupplier.withRequestHeaders(headerMap);
    }

    @Given("^Generate authHeaderKey with$")
    public void generate_authHeaderKey_with(DataTable authKeys)
    {
        final Map<String, String> authKeyMap = authKeys.asMap(String.class, String.class);
        m_testDataSupplier.withRequestAuthKey(Authorization.getAuthKey(m_partner, authKeyMap));
    }

    @Given("^queryParams are$")
    public void queryparams_are(DataTable params)
    {
        final Map<String, String> paramsMap = params.asMap(String.class, String.class);
        m_testDataSupplier.withRequestParams(paramsMap);
    }

    @Given("^Basic web application is running$")
    public void basic_web_application_is_running()
    {
        m_testDataSupplier.checkAppIsRunning();
    }

    @When("^user sets GET request to \"(.*?)\"$")
    public void user_sets_GET_request_to(String uri)
    {
        m_testDataSupplier.withRequestAPIPath(uri);
    }

    @When("^user sets header \"(.*?)\" value \"(.*?)\"$")
    public void user_sets_header_value(String header, String value)
    {
        if (value.equalsIgnoreCase("null"))
        {
            m_testDataSupplier.removeRequestHeader(header);
        }
        else
        {
            m_testDataSupplier.addRequestHeaders(header, value);
        }

    }

    @When("^user sets queryParam \"(.*?)\" value \"(.*?)\"$")
    public void user_sets_queryParam_value(String param, String value)
    {
        if (value.equalsIgnoreCase("null"))
        {
            m_testDataSupplier.removeRequestParam(param);
        }
        else
        {
            m_testDataSupplier.addRequestParam(param, value);
        }
    }

    @When("^performs GET request$")
    public void performs_GET_request()
    {
        m_testDataSupplier.callEndPoint("GET");
    }

    @Then("^the response code should be \"(.*?)\"$")
    public void the_response_code_should_be(String responseCode)
    {
        final int expectedCode = Integer.valueOf(responseCode);
        ResponseValidationUtil.validateHTTPResponseCode(m_testDataSupplier.getResponse(), expectedCode);
    }

    @Then("^the response code should be (\\d+)$")
    public void the_response_code_should_be(int expectedCode)
    {
        ResponseValidationUtil.validateHTTPResponseCode(m_testDataSupplier.getResponse(), expectedCode);
    }

    @Then("^user should see json response with paris on the filtered \"(.*?)\" node$")
    public void user_should_see_json_response_with_paris_on_the_filtered_node(String field, DataTable expectedResponse)
    {
        final Map<String, String> expectedResponseMap = expectedResponse.asMap(String.class, String.class);

        ResponseValidationUtil.validateResponseBody(m_testDataSupplier.getResponse(), expectedResponseMap, field);
    }

}
