package com.hopper.tests.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hopper.tests.model.PriceWithCurrency;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TotalPrice
{
    @JsonProperty("inclusive")
    private PriceWithCurrency inclusive;

    @JsonProperty("exclusive")
    private PriceWithCurrency exclusive;

    @JsonProperty("strikethrough")
    private PriceWithCurrency strikeThrough;

    @JsonProperty("marketing_fee")
    private PriceWithCurrency marketingFee;

    @JsonProperty("minimum_selling_price")
    private PriceWithCurrency minSellingPrice;

    public PriceWithCurrency getInclusive()
    {
        return inclusive;
    }

    public PriceWithCurrency getExclusive()
    {
        return exclusive;
    }

    public PriceWithCurrency getStrikeThrough()
    {
        return strikeThrough;
    }

    public PriceWithCurrency getMarketingFee()
    {
        return marketingFee;
    }

    public PriceWithCurrency getMinSellingPrice()
    {
        return minSellingPrice;
    }
}
