package com.hopper.tests.data.model.response.booking;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableMap;
import com.hopper.tests.data.model.response.Link;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BookingResponse
{
    @JsonProperty("itinerary_id")
    private String itineraryId;

    @JsonProperty("links")
    private Map<String, Link> links;

    public BookingResponse(){}

    public String getItineraryId()
    {
        return itineraryId;
    }

    public Map<String, Link> getLinks()
    {
        return links != null ? links : ImmutableMap.of();
    }
}
