package com.hopper.tests.definitions;

import com.hopper.tests.authorization.Authorization;
import com.hopper.tests.constants.GlobalConstants;
import com.hopper.tests.constants.RequestType;
import com.hopper.tests.constants.SupportedPartners;
import com.hopper.tests.model.ShoppingResponse;
import com.hopper.tests.model.TestContext;
import com.hopper.tests.util.data.ResponseSupplierFactory;
import com.hopper.tests.util.parser.ShoppingResponseParser;
import com.hopper.tests.util.validations.ResponseValidationUtil;
import cucumber.api.DataTable;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import io.restassured.response.Response;

import java.util.Map;
import java.util.function.Supplier;

/**
 * Implementation for PreBooking End points.
 */
public class PreBookingTestScenarioDefinitions
{
    private TestContext m_testContext;
    private Supplier<Response> m_preBookResponseSupplier;

    @Given("^setup$")
    public void setup()
    {
        m_testContext = new TestContext();
    }

    @Given("^for partner \"(.*?)\"$")
    public void for_partner(String partnerName) throws Throwable
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

    @Given("^API at \"(.*?)\"$")
    public void api_at(final String api)
    {
        m_testContext.setHost(api);
    }

    @Given("^for version \"(.*?)\"$")
    public void for_version(final String version)
    {
        m_testContext.setVersion(version);
    }

    @Given("^with request headers$")
    public void with_request_headers(final DataTable headers)
    {
        final Map<String, String> headerMap = headers.asMap(String.class, String.class);
        m_testContext.setHeaders(headerMap);
    }

    @Given("^with authHeaderKey$")
    public void with_authHeaderKey(final DataTable authKeys)
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

    @Given("^with shopping end point \"(.*?)\"$")
    public void with_shopping_end_point(String shoppingEndPoint)
    {
        m_testContext.setApiPath(RequestType.SHOPPING, shoppingEndPoint);
    }

    @Given("^shopping complete")
    public void shopping_complete()
    {
        final Response response = ResponseSupplierFactory.create(m_testContext, GlobalConstants.GET, RequestType.SHOPPING).get();
        final ShoppingResponse shoppingResponse = ShoppingResponseParser.parse(response);
        m_testContext.setShoppingResponse(shoppingResponse);
    }

    @When("^run preBooking$")
    public void run_preBooking()
    {
        m_preBookResponseSupplier = ResponseSupplierFactory.create(m_testContext, GlobalConstants.GET, RequestType.PRE_BOOKING);
    }

    @Then("^verify success response (\\d+)$")
    public void verify_success_response(int expectedCode)
    {
        ResponseValidationUtil.validateHTTPResponseCode(m_preBookResponseSupplier.get(), expectedCode);
    }
}
