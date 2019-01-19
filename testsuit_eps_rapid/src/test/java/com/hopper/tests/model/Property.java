package com.hopper.tests.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Property
{
    @JsonProperty("property_id")
    private String propertyId;

    @JsonProperty("rooms")
    private List<Room> rooms;

    @JsonProperty("score")
    private String score;

    @JsonProperty("links")
    private Map<String, Link> links;

    public Property(){}

    public String getPropertyId()
    {
        return propertyId;
    }

    public List<Room> getRooms()
    {
        return rooms;
    }

    public String getScore()
    {
        return score;
    }

    public Map<String, Link> getLinks()
    {
        return links;
    }
}
