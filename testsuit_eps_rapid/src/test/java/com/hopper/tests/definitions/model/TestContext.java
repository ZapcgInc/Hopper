package com.hopper.tests.definitions.model;

import com.google.common.collect.ImmutableMap;
import com.hopper.tests.authorization.Authorization;
import com.hopper.tests.config.model.TestConfig;
import com.hopper.tests.constants.RequestType;
import com.hopper.tests.constants.SupportedPartners;
import com.hopper.tests.data.model.request.RequestParams;
import com.hopper.tests.data.model.request.booking.Customer;
import com.hopper.tests.data.model.response.booking.BookingResponse;
import com.hopper.tests.data.model.response.booking.BookingRetrieveResponse;
import com.hopper.tests.data.model.response.payment.PaymentOptionResponse;
import com.hopper.tests.data.model.response.prebooking.PreBookingResponse;
import com.hopper.tests.data.model.response.shopping.ShoppingResponse;
import com.hopper.tests.api.APIEndPointGenerator;
import io.restassured.response.Response;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Simple POJO to hold test criteria
 */
public class TestContext
{
    public static final boolean LOGGING_ENABLED = false;
    private static final String AUTH_HEADER_KEY = "Authorization";
    private static final String CHECKIN_DATE_KEY = "checkin";
    private static final String CHECKOUT_DATE_KEY = "checkout";

    private String bookingOverrideElementName = null;
    private String bookingOverrideElementValue = "";
    private final SupportedPartners m_partner;
    private final TestConfig m_testConfig;

    private String m_host;
    private String m_version;
    private Map<String, String> m_headers = new HashMap<>();
    private SimpleDateFormat m_requestDateFormat;
    private String m_checkInDate;
    private String m_checkOutDate;
    private String m_bookingAffiliateId;

    private EnumMap<RequestType, String> m_requestTypeToAPIPath = new EnumMap<RequestType, String>(RequestType.class);
    private EnumMap<RequestType, RequestParams> m_requestTypeToQueryParams = new EnumMap<RequestType, RequestParams>(RequestType.class);
    private EnumMap<RequestType, Response> m_requestTypeToResponse = new EnumMap<RequestType, Response>(RequestType.class);

    private ShoppingResponse m_shoppingResponse;
    private PaymentOptionResponse m_paymentOptionResponse;
    private PreBookingResponse m_preBookingResponse;
    private BookingResponse m_bookingResponse;
    private BookingRetrieveResponse m_bookingRetrieveResponse;

    private EnumMap<RequestType, Map<String, Object>> m_requestTypeToPostBody = new EnumMap<RequestType, Map<String, Object>>(RequestType.class);

    public TestContext(final TestConfig config)
    {
        m_testConfig = config;
        m_partner = config.getPartner();
        m_host = config.getAPI();
        m_version = config.getVersion();
        m_headers.putAll(config.getHeaders());
        m_headers.put(
                AUTH_HEADER_KEY,
                Authorization.getAuthKey(config.getPartner(), config.getAuthParams())
        );
        m_requestTypeToAPIPath.put(RequestType.SHOPPING, config.getShoppingEndPoint());
    }

    public String getBookingOverrideElementValue() {
        return bookingOverrideElementValue;
    }

    public void setBookingOverrideElementValue(String bookingOverrideElementValue) {
        this.bookingOverrideElementValue = bookingOverrideElementValue;
    }

    public String getBookingOverrideElementName() {
        return bookingOverrideElementName;
    }

    public void setBookingOverrideElementName(String bookingOverrideElementName) {
        this.bookingOverrideElementName = bookingOverrideElementName;
    }


    public SupportedPartners getPartner()
    {
        return m_partner;
    }

    public String getHost()
    {
        return m_host;
    }

    public String getVersion()
    {
        return m_version;
    }

    public void setVersion(String version)
    {
        m_version = version;
    }

    public String getApiPath(RequestType requestType)
    {
        return m_requestTypeToAPIPath.get(requestType);
    }

    public void setApiPath(RequestType requestType, String apiPath)
    {
        m_requestTypeToAPIPath.put(requestType, apiPath);
    }

    public void setRequestDateFormat(SimpleDateFormat requestDateFormat)
    {
        m_requestDateFormat = requestDateFormat;
    }

    public Map<String, String> getHeaders()
    {
        return m_headers;
    }

    public void addHeader(String header, String value)
    {
        m_headers.put(header, value);
    }

    public void removeHeader(String header)
    {
        m_headers.remove(header);
    }

    public Response getResponse(RequestType requestType)
    {
        return m_requestTypeToResponse.get(requestType);
    }
    
    public void setResponse(RequestType requestType, Response response)
    {
        m_requestTypeToResponse.put(requestType, response);
    }

    public ShoppingResponse getShoppingResponse()
    {
        return m_shoppingResponse;
    }

    public void setShoppingResponse(ShoppingResponse shoppingResponse)
    {
        m_shoppingResponse = shoppingResponse;
    }

    public String getCheckInDate()
    {
        return m_checkInDate;
    }

    public String getCheckOutDate()
    {
        return m_checkOutDate;
    }

    public PaymentOptionResponse getPaymentOptionResponse()
    {
        return m_paymentOptionResponse;
    }

    public void setPaymentOptionResponse(PaymentOptionResponse paymentOptionResponse)
    {
        m_paymentOptionResponse = paymentOptionResponse;
    }

    public PreBookingResponse getPreBookingResponse()
    {
        return m_preBookingResponse;
    }

    public void setPreBookingResponse(PreBookingResponse m_preBookingResponse)
    {
        this.m_preBookingResponse = m_preBookingResponse;
    }

    public void setPostBody(final Map<String, Object> postMessage, final RequestType requestType)
    {
        m_requestTypeToPostBody.put(requestType, postMessage);

    }

    public Map<String, Object> getPostBody(final RequestType requestType)
    {
        return m_requestTypeToPostBody.get(requestType);
    }

    public BookingResponse getBookingResponse()
    {
        return m_bookingResponse;
    }

    public void setBookingResponse(BookingResponse bookingResponse)
    {
        m_bookingResponse = bookingResponse;
    }

    public Optional<String> getBookingAffiliateId()
    {
        return Optional.ofNullable(m_bookingAffiliateId);
    }

    public void setBookingAffiliateId(String bookingAffiliateId)
    {
        m_bookingAffiliateId = bookingAffiliateId;
    }

    public BookingRetrieveResponse getBookingRetrieveResponse()
    {
        return m_bookingRetrieveResponse;
    }

    public void setBookingRetrieveResponse(BookingRetrieveResponse bookingRetrieveResponse)
    {
        m_bookingRetrieveResponse = bookingRetrieveResponse;
    }

    public TestConfig getTestConfig()
    {
        return m_testConfig;
    }

    /* START - Request Query Params */

    public void addCheckInDate(Calendar date, RequestType requestType)
    {
        addParam(CHECKIN_DATE_KEY, _convertToDate(date), requestType);
        m_checkInDate = _convertToDate(date);
    }

    public void addCheckOutDate(Calendar date, RequestType requestType)
    {
        addParam(CHECKOUT_DATE_KEY, _convertToDate(date), requestType);
        m_checkOutDate = _convertToDate(date);
    }

    public void setParams(Map<String, String> params, RequestType requestType)
    {
        _initRequestParams(requestType);
        m_requestTypeToQueryParams.get(requestType).setParams(params);
    }

    public void addParam(String header, String value, RequestType requestType)
    {
        _initRequestParams(requestType);
        m_requestTypeToQueryParams.get(requestType).addParam(header, value);
    }

    public void removeParam(String header, RequestType requestType)
    {
        Optional.ofNullable(m_requestTypeToQueryParams.get(requestType))
                .ifPresent(params -> params.removeParam(header));
    }


    public void addParamWithMultipleValues(String header, List<String> multipleValues, RequestType requestType)
    {
        _initRequestParams(requestType);
        m_requestTypeToQueryParams.get(requestType).addParamWithMultipleValues(header, multipleValues);
    }

    public void removeParamWithMultipleValues(String header, RequestType requestType)
    {
        Optional.ofNullable(m_requestTypeToQueryParams.get(requestType))
                .ifPresent(params -> params.removeParamWithMultipleValues(header));
    }

    public Map<String, List<String>> getParamsWithMultipleValues(RequestType requestType)
    {
        return m_requestTypeToQueryParams.containsKey(requestType)
                ? m_requestTypeToQueryParams.get(requestType).getParamsWithMultipleValues()
                : null;
    }

    public Map<String, String> getParams(RequestType requestType)
    {
        return m_requestTypeToQueryParams.containsKey(requestType)
                ? m_requestTypeToQueryParams.get(requestType).getParams()
                : null;
    }


    public List<String> getParamValues(RequestType requestType, String queryParam)
    {
        return m_requestTypeToQueryParams.containsKey(requestType)
                ? m_requestTypeToQueryParams.get(requestType).getParamValues(queryParam)
                : null;
    }
    private void _initRequestParams(RequestType requestType)
    {
        if (!m_requestTypeToQueryParams.containsKey(requestType))
        {
            m_requestTypeToQueryParams.put(requestType, new RequestParams());
        }
    }

    /* END - Request Query Params */

    private String _convertToDate(final Calendar dateTime)
    {
        return m_requestDateFormat.format(dateTime.getTime());
    }

    public String toString()
    {
        final StringBuilder sb = new StringBuilder();

        sb.append("HOST").append(" : ").append(getHost()).append("\n");
        sb.append("VERSION").append(" : ").append(getVersion()).append("\n");

        for (RequestType reqType : RequestType.values())
        {
            final String apiPath = getApiPath(reqType);
            if (apiPath != null && getResponse(reqType) != null)
            {

                sb.append("\t").append("Request Type").append(" : ").append(reqType).append("\n");
                sb.append("\t").append("API End Point").append(" : ").append(getApiPath(reqType)).append("\n");

                sb.append("\t").append("Request Information").append("\n");
                //sb.append("\t").append("Request Body").append(":").append(getPostBody(reqType)).append("\n");


                sb.append("\t").append("\t").append("End Point").append(" : ").append(APIEndPointGenerator.create(this, reqType)).append("\n");
                sb.append("\t").append("\t").append("Headers").append(" : ").append(getHeaders()).append("\n");
                sb.append("\t").append("\t").append("Query Params").append(" : ").append(getParams(reqType)).append("\n");
                sb.append("\t").append("\t").append("Query Params with multiple values").append(" : ").append(getParamsWithMultipleValues(reqType)).append("\n");


                sb.append("\t").append("Response Information").append("\n");
                sb.append("\t").append("\t").append("Status Code").append(" : ").append(getResponse(reqType).getStatusCode()).append("\n");
                sb.append("\t").append("\t").append("Status Line").append(" : ").append(getResponse(reqType).getStatusLine()).append("\n");
                sb.append("\t").append("\t").append("Headers").append(" : ").append(getResponse(reqType).getHeaders()).append("\n");
                sb.append("\t").append("\t").append("Response Time").append(" : ").append(getResponse(reqType).getTime()).append("\n");
                sb.append("\t").append("\t").append("Content Type").append(" : ").append(getResponse(reqType).getContentType()).append("\n");
                sb.append("\t").append("\t").append("Body").append(" : ").append(getResponse(reqType).asString()).append("\n");
            }
        }

        return sb.toString();
    }


    public void validateElement(RequestType requestType, String element) {




    }
}
