package com.hopper.tests.api;

import com.hopper.tests.constants.RequestType;
import com.hopper.tests.definitions.model.TestContext;

/**
 * Util class for Dynamically Generating API End Points
 */
public class APIEndPointGenerator
{
    public static String create(final TestContext criteria, final RequestType requestType)
    {
        switch (requestType)
        {
            case SHOPPING:
            {
                return _getShoppingEndPoint(criteria);
            }
            case PRE_BOOKING:
            {
                return _getPreBookingEndPoint(criteria);
            }
            case PAYMENT_OPTIONS:
            {
                return _getPaymentOptionsEndPoint(criteria);
            }
            case BOOKING:
            {
                return _getBookingEndPoint(criteria);
            }
            case RETRIEVE_BOOKING:
            {
                return _getBookingRetrieveEndPoint(criteria);
            }
            case RETRIEVE_BOOKING_ALL_ITINERARIES:
            {
                return _getBookingRetrieveEndPointForAllItineraries(criteria);
            }
            case CANCEL_BOOKING:
            {
                return _cancelBookingEndPoint(criteria);
            }
            case RESUME_BOOKING:
            {
                return _resumeBookingEndPoint(criteria);
            }
            default:
            {
                throw new UnsupportedOperationException("Request type :" + requestType.name() + "is currently unsupported");
            }
        }
    }

    private static String _getBookingRetrieveEndPointForAllItineraries(TestContext criteria) {
        final StringBuilder endPoint = new StringBuilder(criteria.getHost());

        if (criteria.getVersion() != null)
        {
            endPoint.append("/");
            endPoint.append(criteria.getVersion());
        }

        final String apiPath = criteria.getApiPath(RequestType.RETRIEVE_BOOKING_ALL_ITINERARIES);
        if (apiPath != null)
        {
            endPoint.append("/");
            endPoint.append(apiPath);
        }
        return endPoint.toString();

    }

    private static String _resumeBookingEndPoint(TestContext criteria)
    {
        final StringBuilder endPoint = new StringBuilder(criteria.getHost());

        final String apiPath = criteria.getApiPath(RequestType.RESUME_BOOKING);
        if (apiPath != null)
        {
            endPoint.append(apiPath);
        }

        return endPoint.toString();
    }

    private static String _cancelBookingEndPoint(TestContext criteria)
    {
        final StringBuilder endPoint = new StringBuilder(criteria.getHost());

        final String apiPath = criteria.getApiPath(RequestType.CANCEL_BOOKING);
        if (apiPath != null)
        {
            endPoint.append(apiPath);
        }

        return endPoint.toString();
    }

    private static String _getBookingRetrieveEndPoint(TestContext criteria)
    {
        final StringBuilder endPoint = new StringBuilder(criteria.getHost());

        final String apiPath = criteria.getApiPath(RequestType.RETRIEVE_BOOKING);
        if (apiPath != null)
        {
            endPoint.append(apiPath);
        }

        return endPoint.toString();
    }

    private static String _getBookingEndPoint(TestContext criteria)
    {
        final StringBuilder endPoint = new StringBuilder(criteria.getHost());

        final String apiPath = criteria.getApiPath(RequestType.BOOKING);
        if (apiPath != null)
        {
            endPoint.append(apiPath);
        }

        return endPoint.toString();
    }

    private static String _getPaymentOptionsEndPoint(TestContext criteria)
    {
        final StringBuilder endPoint = new StringBuilder(criteria.getHost());

        final String apiPath = criteria.getApiPath(RequestType.PAYMENT_OPTIONS);
        if (apiPath != null)
        {
            endPoint.append(apiPath);
        }

        return endPoint.toString();
    }

    private static String _getPreBookingEndPoint(final TestContext criteria)
    {
        final StringBuilder endPoint = new StringBuilder(criteria.getHost());

        final String apiPath = criteria.getApiPath(RequestType.PRE_BOOKING);
        if (apiPath != null)
        {
            endPoint.append(apiPath);
        }

        return endPoint.toString();
    }

    private static String _getShoppingEndPoint(final TestContext criteria)
    {
        final StringBuilder endPoint = new StringBuilder(criteria.getHost());

        if (criteria.getVersion() != null)
        {
            endPoint.append("/");
            endPoint.append(criteria.getVersion());
        }

        final String apiPath = criteria.getApiPath(RequestType.SHOPPING);
        if (apiPath != null)
        {
            endPoint.append("/");
            endPoint.append(apiPath);
        }
        return endPoint.toString();
    }
}
