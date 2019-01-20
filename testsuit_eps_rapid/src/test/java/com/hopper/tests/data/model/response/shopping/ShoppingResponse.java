package com.hopper.tests.data.model.response.shopping;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.collect.ImmutableList;
import com.hopper.tests.data.model.response.ErrorResponse;

import java.util.Arrays;
import java.util.List;

/**
 * Simple Value Object for Shopping Response.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ShoppingResponse
{
    private final List<Property> m_properties;
    private ErrorResponse errorResponse;

    public ShoppingResponse()
    {
        m_properties = null;
    }

    public ShoppingResponse(Property[] properties)
    {
        m_properties = Arrays.asList(properties);
    }

    public List<Property> getProperties()
    {
        return m_properties != null ? m_properties : ImmutableList.of();
    }
}
