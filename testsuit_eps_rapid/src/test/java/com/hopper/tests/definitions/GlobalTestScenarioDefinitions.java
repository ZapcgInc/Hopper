package com.hopper.tests.definitions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.hopper.tests.authorization.Authorization;

import com.hopper.tests.constants.GlobalConstants;
import com.hopper.tests.constants.SupportedPartners;
import com.hopper.tests.model.TestCriteria;
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
    private TestCriteria m_testCriteria;
    private CheckAPIAvailability m_checkAPIAvailability;
    private Supplier<Response> m_responseSupplier;

    @Given("^simple init$")
    public void simple_init()
    {
        m_testCriteria = new TestCriteria();
        m_checkAPIAvailability = new CheckAPIAvailability();
    }

    @Given("^partner is \"(.*?)\"$")
    public void partner_is(String partnerName) throws Throwable
    {
        try
        {
            SupportedPartners partner = SupportedPartners.valueOf(partnerName);
            m_testCriteria.setPartner(partner);
        }
        catch (Exception exp)
        {
            throw new Exception("Invalid Partner : " + partnerName);
        }
    }

    @Given("^web application endpoint url is \"(.*?)\"$")
    public void web_application_endpoint_url_is(String url)
    {
        m_testCriteria.setHost(url);
    }

    @Given("^version is \"(.*?)\"$")
    public void version_is(String version)
    {
        if (version != null && !GlobalConstants.NULL_STRING.equalsIgnoreCase(version))
        {
            m_testCriteria.setVersion(version);
        }
    }

    @Given("^headers are$")
    public void headers_are(DataTable headers)
    {
        final Map<String, String> headerMap = headers.asMap(String.class, String.class);
        m_testCriteria.setHeaders(headerMap);
    }

    @Given("^Generate authHeaderKey with$")
    public void generate_authHeaderKey_with(DataTable authKeys)
    {
        final Map<String, String> authKeyMap = authKeys.asMap(String.class, String.class);
        final String authKey = Authorization.getAuthKey(m_testCriteria.getPartner(), authKeyMap);
        m_testCriteria.setAuthKey(authKey);
    }

    @Given("^queryParams are$")
    public void queryparams_are(DataTable params)
    {
        final Map<String, String> paramsMap = params.asMap(String.class, String.class);
        m_testCriteria.setParams(paramsMap);
    }

    @Given("^Basic web application is running$")
    public void basic_web_application_is_running()
    {
        m_checkAPIAvailability.test(m_testCriteria);
    }

    @When("^user sets GET request to \"(.*?)\"$")
    public void user_sets_GET_request_to(String uri)
    {
        m_testCriteria.setApiPath(uri);
    }

    @When("^user sets header \"(.*?)\" value \"(.*?)\"$")
    public void user_sets_header_value(String header, String value)
    {
        if (value.equalsIgnoreCase(GlobalConstants.NULL_STRING))
        {
            m_testCriteria.removeHeader(header);
        }
        else
        {
            m_testCriteria.addHeader(header, value);
        }

    }

    @When("^user sets queryParam \"(.*?)\" value \"(.*?)\"$")
    public void user_sets_queryParam_value(String param, String value)
    {
        if (value.equalsIgnoreCase(GlobalConstants.NULL_STRING))
        {
            m_testCriteria.removeParam(param);
            m_testCriteria.removeParamWithMultipleValues(param);
        }
        else
        {
            m_testCriteria.addParam(param, value);
        }
    }

    @Given("^set multiple values for queryParam \"(.*?)\" with \"(.*?)\"$")
    public void set_multiple_values_for_queryParam_with(String queryParam, String values)
    {
        final List<String> listOfValues = Arrays.stream(values.split(GlobalConstants.MULTI_VALUE_DELIMITER))
                .collect(Collectors.toList());

        m_testCriteria.addParamWithMultipleValues(queryParam, listOfValues);
    }

    @When("^performs GET request$")
    public void performs_GET_request()
    {
        m_responseSupplier = ResponseSupplierFactory.create(m_testCriteria, GlobalConstants.GET);
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
