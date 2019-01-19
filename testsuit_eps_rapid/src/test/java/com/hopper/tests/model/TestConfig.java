package com.hopper.tests.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableMap;
import com.hopper.tests.constants.SupportedPartners;

import java.util.HashMap;
import java.util.Map;

/**
 * Value Object to hold Test Configuration.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TestConfig
{
    @JsonProperty("partner")
    private SupportedPartners partner;

    @JsonProperty("api")
    private String api;

    @JsonProperty("version")
    private String version;

    @JsonProperty("headers")
    private Map<String, String> headers = new HashMap<>();

    @JsonProperty("auth_params")
    private Map<String, String> authParams = new HashMap<>();

    @JsonProperty("shopping_end_point")
    private String shoppingEndPoint;

    public TestConfig(){}

    public SupportedPartners getPartner()
    {
        return partner;
    }

    public String getAPI()
    {
        return api;
    }

    public String getVersion()
    {
        return version;
    }

    public Map<String, String> getHeaders()
    {
        return headers != null ? headers : ImmutableMap.of();
    }

    public Map<String, String> getAuthParams()
    {
        return authParams != null ? authParams : ImmutableMap.of();
    }

    public String getShoppingEndPoint()
    {
        return shoppingEndPoint;
    }
}
