package com.hopper.tests.model.response.prebooking;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableMap;
import com.hopper.tests.model.response.Link;
import com.hopper.tests.model.response.RoomPrice;
import org.apache.commons.lang.StringUtils;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PreBookingResponse
{
    @JsonProperty("status")
    private String status;

    @JsonProperty("links")
    private Map<String, Link> links;

    @JsonProperty("occupancies")
    private Map<String, RoomPrice> roomPriceByOccupany;

    public PreBookingResponse(){}

    public String getStatus()
    {
        return status != null ? status : StringUtils.EMPTY;
    }

    public Map<String, Link> getLinks()
    {
        return links;
    }

    public Map<String, RoomPrice> getRoomPriceByOccupany()
    {
        return roomPriceByOccupany != null ? roomPriceByOccupany : ImmutableMap.of();
    }
}
