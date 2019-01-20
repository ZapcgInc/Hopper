package com.hopper.tests.data.model.response.booking;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hopper.tests.data.model.response.Link;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Room
{
    @JsonProperty("id")
    private String id;

    @JsonProperty("links")
    private Map<String, Link> links;

    public Room(){}

    public String getId()
    {
        return id;
    }

    public Map<String, Link> getLinks()
    {
        return links;
    }
}
