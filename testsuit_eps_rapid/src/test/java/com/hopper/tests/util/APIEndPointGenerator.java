package com.hopper.tests.util;

import com.hopper.tests.constants.RequestType;
import com.hopper.tests.model.TestContext;

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
            default:
            {
                throw new UnsupportedOperationException("Request type :" + requestType.name() + "is currently unsupported");
            }
        }
    }

    private static String _getPreBookingEndPoint(final TestContext criteria)
    {
        final StringBuilder endPoint = new StringBuilder(criteria.getHost());

        if (criteria.getShoppingResponse() != null && criteria.getShoppingResponse().getPriceCheckEndPoint() != null)
        {
            endPoint.append(criteria.getShoppingResponse().getPriceCheckEndPoint());
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
