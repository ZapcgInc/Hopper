package com.hopper.tests.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Arrays;
import java.util.List;

/**
 * Simple Value Object for Shopping Response.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ShoppingResponse
{
    private final List<Property> m_properties;

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
        return m_properties;
    }
}
