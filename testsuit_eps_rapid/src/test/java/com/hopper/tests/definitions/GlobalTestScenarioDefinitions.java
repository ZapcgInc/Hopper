package com.hopper.tests.definitions;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;

import com.hopper.tests.constants.GlobalConstants;
import com.hopper.tests.constants.RequestType;
import com.hopper.tests.data.model.response.Link;
import com.hopper.tests.data.model.response.shopping.ShoppingResponse;
import com.hopper.tests.definitions.model.TestContext;
import com.hopper.tests.config.ConfigurationFileParser;
import com.hopper.tests.data.ResponseSupplierFactory;
import com.hopper.tests.definitions.util.BookingTestHelper;
import com.hopper.tests.util.logging.LoggingUtil;
import com.hopper.tests.data.parser.PaymentOptionResponseParser;
import com.hopper.tests.data.parser.PreBookingResponseParser;
import com.hopper.tests.data.parser.ShoppingResponseParser;
import com.hopper.tests.api.CheckAPIAvailability;
import com.hopper.tests.validations.ResponseValidationUtil;
import com.hopper.tests.validations.constants.ResponseValidationField;
import com.hopper.tests.validations.model.Range;
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

    @Before
    public void before(Scenario scenario)
    {
        LoggingUtil.printTestDetails(scenario);
    }

    @After
    public void after(Scenario scenario)
    {
        LoggingUtil.printTestStatus(scenario, m_testContext);
    }

    @Given("^setup for partner with config at \"([^\"]*)\"$")
    public void setUp(final String pathToConfig)
    {
        m_testContext = new TestContext(ConfigurationFileParser.parse(pathToConfig));
        m_checkAPIAvailability = new CheckAPIAvailability();
    }

    @And("^for version \"([^\"]*)\"$")
    public void forVersion(String version)
    {
        if (version != null && !GlobalConstants.NULL_STRING.equalsIgnoreCase(version))
        {
            m_testContext.setVersion(version);
        }
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
        m_testContext.setPreBookingResponse(PreBookingResponseParser.parse(response));
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
    public void user_should_see_json_response_with_paris_on_the_filtered_node(final String field, final DataTable expectedResponse)
    {
        user_should_see_response_with_paris_on_the_filtered_node("SHOPPING", field, expectedResponse);
    }

    @Then("^user should see \"(.*?)\" response with paris on the filtered \"(.*?)\" node$")
    public void user_should_see_response_with_paris_on_the_filtered_node(String requestType, final String field, final DataTable expectedResponse)
    {
        final Map<String, String> expectedResponseMap = expectedResponse.asMap(String.class, String.class);

        ResponseValidationUtil.validateResponseBody(
                m_testContext.getResponse(RequestType.valueOf(requestType)),
                expectedResponseMap,
                field
        );
    }

    @Then("^validate \"(.*?)\" response element \"(.*?)\" matches values \"(.*?)\"$")
    public void validateResponseField(String requestType, String node, String values)
    {
        final List<String> listOfValues = Arrays.stream(values.split(GlobalConstants.MULTI_VALUE_DELIMITER))
                .collect(Collectors.toList());
        ResponseValidationUtil.validate(
                ResponseValidationField.valueOf(node),
                RequestType.valueOf(requestType),
                m_testContext,
                listOfValues);
    }

    @Then("^the element \"(.*?)\" count per \"(.*?)\" for \"(.*?)\" should be between \"(.*?)\" and \"(.*?)\"$")
    public void validateRange(String validationField, String parentNode, String requestType, String minValue, String maxValue)
    {
        final Range<Integer> range = new Range<>(Integer.parseInt(minValue),
                Integer.parseInt(maxValue),
                Range.BoundType.INCLUSIVE);

        ResponseValidationUtil.validate(
                ResponseValidationField.valueOf(validationField),
                RequestType.valueOf(requestType),
                m_testContext,
                range
        );
    }

    @Then("^the element \"(.*?)\" for \"(.*?)\" should have value belongs to \"(.*?)\"$")
    public void validateValueBelongsTo(String field, String requestType, String expectedValues)
    {
        final List<String> listOfValues = Arrays.stream(expectedValues.split(GlobalConstants.MULTI_VALUE_DELIMITER))
                .collect(Collectors.toList());

        ResponseValidationUtil.validate(
                ResponseValidationField.valueOf(field),
                RequestType.valueOf(requestType),
                m_testContext,
                listOfValues);
    }

    @Then("^the \"(.*?)\" for \"(.*?)\" should be equal to \"(.*?)\"$")
    public void validateValueEqualsTo(String validationField, String requestType, String expectedValue)
    {
        ResponseValidationUtil.validate(
                ResponseValidationField.valueOf(validationField),
                RequestType.valueOf(requestType),
                m_testContext,
                ImmutableList.of(expectedValue)
        );
    }

    @Then("^the element \"(.*?)\" start and end date \\(under cancel_penalties\\) for \"(.*?)\" are within check in and check out dates$")
    public void validateShoppingCancelPenalties(String validationField, String requestType)
    {
        ResponseValidationUtil.validate(
                ResponseValidationField.valueOf(validationField),
                RequestType.valueOf(requestType),
                m_testContext)
        ;
    }

    @Then("^the element \"(.*?)\" for \"(.*?)\" either have both amenityId and description or have no amenity ID and description \\(mutually inclusive\\)$")
    public void validateAmenities(String validationField, String requestType)
    {
        ResponseValidationUtil.validate(
                ResponseValidationField.valueOf(validationField),
                RequestType.valueOf(requestType),
                m_testContext)
        ;
    }

    @Then("^validate that \"(.*?)\" for \"(.*?)\" is the sum of individual room stay values with taxes and fees$")
    public void validateTotalPrice(String field, String requestType)
    {
        ResponseValidationUtil.validate(ResponseValidationField.valueOf(field), RequestType.valueOf(requestType), m_testContext);
    }

    @And("^validate \"([^\"]*)\" for \"([^\"]*)\" should be (\\d+)$")
    public void validateCount(String validationField, String requestType, int count)
    {
        ResponseValidationUtil.validate(
                ResponseValidationField.valueOf(validationField),
                RequestType.valueOf(requestType),
                m_testContext,
                count
        );
    }

    @And("^validate \"([^\"]*)\"  for \"([^\"]*)\"$")
    public void validate(String validationField, String requestType)
    {
        ResponseValidationUtil.validate(
                ResponseValidationField.valueOf(validationField),
                RequestType.valueOf(requestType),
                m_testContext
        );
    }

    @And("^run shopping and preBooking for Booking$")
    public void runShoppingAndPreBookingForBooking()
    {
        BookingTestHelper.runShoppingAndPreBookingForBooking(m_testContext);
    }

    @And("^run booking with hold \"([^\"]*)\"$")
    public void runBookingWithHold(String holdBooking)
    {
        BookingTestHelper.runBooking(m_testContext, Boolean.valueOf(holdBooking));
    }

    @And("^retrieve booking$")
    public void retrieveBooking()
    {
        BookingTestHelper.retrieveBooking(m_testContext);
    }

    @And("^cancel booking$")
    public void cancelBooking()
    {
        BookingTestHelper.cancelBooking(m_testContext);
    }

    @And("^cancel room booking$")
    public void cancelRoomBooking()
    {
        BookingTestHelper.cancelRoomBooking(m_testContext);
    }

    @And("^run resume booking$")
    public void runResumeBooking()
    {
        BookingTestHelper.resumeBooking(m_testContext);
    }
}
