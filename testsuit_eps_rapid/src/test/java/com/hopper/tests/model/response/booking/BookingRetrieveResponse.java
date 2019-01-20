package com.hopper.tests.model.response.booking;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hopper.tests.model.response.Link;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BookingRetrieveResponse
{
    @JsonProperty("itinerary_id")
    private String itineraryId;

    @JsonProperty("property_id")
    private String propertyId;

    @JsonProperty("links")
    private Map<String, Link> links;

    public BookingRetrieveResponse(){}

    public String getItineraryId()
    {
        return itineraryId;
    }

    public String getPropertyId()
    {
        return propertyId;
    }

    public Map<String, Link> getLinks()
    {
        return links;
    }
}
