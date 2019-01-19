package com.hopper.tests.model.response.prebooking;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hopper.tests.model.response.Link;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PreBookingResponse
{
    @JsonProperty("status")
    private String status;

    @JsonProperty("links")
    private Map<String, Link> links;

    public PreBookingResponse(){}

    public String getStatus()
    {
        return status;
    }

    public Map<String, Link> getLinks()
    {
        return links;
    }
}
