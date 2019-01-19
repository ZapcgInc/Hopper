package com.hopper.tests.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RoomPrice
{
    @JsonProperty("nightly")
    private List<List<Price>> nightlyPrice;

    @JsonProperty("stay")
    private List<Price> stayPrice;

    public RoomPrice(){}

    public List<List<Price>> getNightlyPrice()
    {
        return nightlyPrice;
    }

    public List<Price> getStayPrice()
    {
        return stayPrice;
    }
}
