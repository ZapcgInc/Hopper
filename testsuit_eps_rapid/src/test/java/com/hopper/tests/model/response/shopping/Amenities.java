package com.hopper.tests.model.response.shopping;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Amenities
{
    @JsonProperty("id")
    private String id;

    @JsonProperty("name")
    private String name;

    public Amenities(){}

    public String getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }
}
