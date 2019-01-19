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
import com.hopper.tests.model.response.Link;
import com.hopper.tests.model.response.shopping.ShoppingResponse;
import com.hopper.tests.model.TestContext;
import com.hopper.tests.util.data.ResponseSupplierFactory;
import com.hopper.tests.util.parser.PaymentOptionResponseParser;
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
        final ShoppingResponse shoppingResponse = ShoppingResponseParser.parse(m_testContext.getResponse(RequestType.SHOPPING));
        m_testContext.setShoppingResponse(shoppingResponse);
    }

    @When("^run preBooking$")
    public void runPreBooking()
    {
        final Link priceCheckLink = m_testContext.getShoppingResponse().getProperties().get(0).getRooms().get(0).getRates().get(0).getBedGroups().get(0).getLinks().get("price_check");

        m_testContext.setApiPath(RequestType.PRE_BOOKING, priceCheckLink.getHref());
        final Response response = ResponseSupplierFactory.create(
                m_testContext,
                priceCheckLink.getMethod(),
                RequestType.PRE_BOOKING
        ).get();

        m_testContext.setResponse(RequestType.PRE_BOOKING, response);

    }

    @When("^run paymentOptions$")
    public void runPaymentOptions()
    {

        final Link paymentMethodLink = m_testContext.getShoppingResponse().getProperties().get(0).getRooms().get(0).getRates().get(0).getLinks().get("payment_options");

        m_testContext.setApiPath(RequestType.PAYMENT_OPTIONS, paymentMethodLink.getHref());

        final Response response = ResponseSupplierFactory.create(
                m_testContext,
                paymentMethodLink.getMethod(),
                RequestType.PAYMENT_OPTIONS
        ).get();

        m_testContext.setResponse(RequestType.PAYMENT_OPTIONS, response);
        m_testContext.setPaymentOptionResponse(PaymentOptionResponseParser.parse(response));
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
    public void user_should_see_json_response_with_paris_on_the_filtered_node(final String field, final DataTable expectedResponse) throws Throwable
    {
        user_should_see_response_with_paris_on_the_filtered_node("SHOPPING", field, expectedResponse);
    }

    @Then("^user should see \"(.*?)\" response with paris on the filtered \"(.*?)\" node$")
    public void user_should_see_response_with_paris_on_the_filtered_node(String requestType, final String field, final DataTable expectedResponse) throws Throwable
    {
        final Map<String, String> expectedResponseMap = expectedResponse.asMap(String.class, String.class);

        ResponseValidationUtil.validateResponseBody(
                m_testContext.getResponse(RequestType.valueOf(requestType)),
                expectedResponseMap,
                field
        );
    }

    @Then("^the element \"(.*?)\" count per \"(.*?)\" for \"(.*?)\" should be (\\d+)$")
    public void the_element_count_per_for_should_be(String field, String arg2, String requestType, int expectedValue) throws Throwable
    {
        ResponseValidationUtil.validateArraySize(m_testContext.getResponse(RequestType.valueOf(requestType)), field, expectedValue);
    }

    @Then("^validate \"(.*?)\" response element \"(.*?)\" matches values \"(.*?)\"$")
    public void validate_response_element_matches_values(String requestType, String node, String values) throws Throwable
    {
        final List<String> listOfValues = Arrays.stream(values.split(GlobalConstants.MULTI_VALUE_DELIMITER))
                .collect(Collectors.toList());
        ResponseValidationUtil.validateNodeForValues(m_testContext.getResponse(RequestType.valueOf(requestType)), node + "_" + requestType, listOfValues);
    }

    @Then("^the element \"(.*?)\" count per \"(.*?)\" for \"(.*?)\" should be between \"(.*?)\" and \"(.*?)\"$")
    public void the_element_count_per_for_should_be_between_and(String validationField, String parentNode, String requestType, String minValue, String maxValue)
    {
        ResponseValidationUtil.validateArraySizeBetweenVal(RequestType.valueOf(requestType), m_testContext, validationField, minValue, maxValue);
    }

    @Then("^the element \"(.*?)\" for \"(.*?)\" should have value belongs to \"(.*?)\"$")
    public void the_element_for_should_have_value_belongs_to(String field, String requestType, String expectedValues) throws Throwable
    {
        final List<String> listOfValues = Arrays.stream(expectedValues.split(GlobalConstants.MULTI_VALUE_DELIMITER))
                .collect(Collectors.toList());
        ResponseValidationUtil.validateFieldValueBelongsToExpectedValues(m_testContext.getResponse(RequestType.valueOf(requestType)), field + "_" + requestType, listOfValues);
    }

    @Then("^the element \"(.*?)\"  for \"(.*?)\" should not be \"(.*?)\"$")
    public void the_element_for_should_not_be(String field, String requestType, String value) throws Throwable
    {
        ResponseValidationUtil.validateFieldValueNotEqualTo(
                m_testContext,
                RequestType.valueOf(requestType),
                field,
                value);
    }

    @Then("^the \"(.*?)\" for \"(.*?)\" should be equal to \"(.*?)\"$")
    public void the_for_should_be_equal_to(String field1, String responseType, String field2)
    {
        ResponseValidationUtil.validateFieldsInResponseBody(field1, m_testContext.getResponse(RequestType.valueOf(responseType)), field2);
    }

    @Then("^the element \"(.*?)\" start and end date \\(under cancel_penalties\\) for \"(.*?)\" are within check in and check out dates$")
    public void the_element_start_and_end_date_under_cancel_penalties_for_are_within_check_in_and_check_out_dates(String field, String requestType) throws Throwable
    {
        ResponseValidationUtil.validateResponseBodyForNode(field, m_testContext.getParams(RequestType.valueOf(requestType)), m_testContext.getResponse(RequestType.valueOf(requestType)));
    }

    @Then("^the element \"(.*?)\" for \"(.*?)\" either have both amenityId and description or have no amenity ID and description \\(mutually inclusive\\)$")
    public void the_element_for_either_have_both_amenity_ID_and_description_or_have_no_amenity_ID_and_description_mutually_inclusive(String field, String requestType) throws Throwable
    {
        ResponseValidationUtil.validateResponseBodyForNode(field + "_" + requestType, m_testContext.getParams(RequestType.valueOf(requestType)), m_testContext.getResponse(RequestType.valueOf(requestType)));
    }

    @Then("^validate that \"(.*?)\" for \"(.*?)\" is the sum of individual room stay values with taxes and fees$")
    public void validate_that_for_is_the_sum_of_individual_room_stay_values_with_taxes_and_fees(String field, String responseType) throws Throwable
    {
        ResponseValidationUtil.validateNodeInResponseBody(field + "_" + responseType, m_testContext.getResponse(RequestType.valueOf(responseType)));
    }


    @Before
    public void before(Scenario scenario)
    {
        printTestDetails(scenario);
    }

    private void printTestDetails(Scenario scenario)
    {
        log("======================================Scenario=================================================");
        log("Scearnio id: " + scenario.getId());
        log("Scenario name: " + scenario.getName());
        log("Scenario stage: " + scenario.getSourceTagNames());
        log("Scenario status: Started at :" + new java.util.Date());
    }

    @After
    public void after(Scenario scenario)
    {
        printTestStatus(scenario);
    }

    private void printTestStatus(Scenario scenario)
    {
        log("Scenario success: " + (!scenario.isFailed()));
        log("Scenario status: " + scenario.getStatus() + " at :" + new java.util.Date());
        if (scenario.isFailed())
        {
            log("Scenario Execution Details :");
            log(m_testContext.toString());
        }
    }

    private void log(String line)
    {
        System.out.println(line);
    }
}
