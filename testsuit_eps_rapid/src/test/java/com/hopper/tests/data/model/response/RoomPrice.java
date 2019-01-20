package com.hopper.tests.data.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RoomPrice
{
    @JsonProperty("nightly")
    private List<List<Price>> nightlyPrice;

    @JsonProperty("stay")
    private List<Price> stayPrice;

    @JsonProperty("fees")
    private List<Price> fees;

    @JsonProperty("totals")
    private TotalPrice totals;

    public RoomPrice() {}

    public List<List<Price>> getNightlyPrice()
    {
        return nightlyPrice != null ? nightlyPrice : ImmutableList.of();
    }

    public List<Price> getStayPrice()
    {
        return stayPrice != null ? stayPrice : ImmutableList.of();
    }

    public List<Price> getFees()
    {
        return fees != null ? fees : ImmutableList.of();
    }

    public TotalPrice getTotals()
    {
        return totals;
    }
}
