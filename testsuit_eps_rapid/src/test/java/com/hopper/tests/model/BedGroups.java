package com.hopper.tests.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BedGroups
{
    @JsonProperty("links")
    private Map<String, Link> links;

    public BedGroups(){}

    public Map<String, Link> getLinks()
    {
        return links;
    }
}
