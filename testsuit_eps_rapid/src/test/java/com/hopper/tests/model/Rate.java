package com.hopper.tests.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Rate
{
    @JsonProperty("id")
    private String id;

    @JsonProperty("available_rooms")
    private int availableRooms;

    @JsonProperty("refundable")
    private boolean refundable;

    @JsonProperty("deposit_required")
    private boolean depositRequired;

    @JsonProperty("fenced_deal")
    private boolean fencedDeal;

    @JsonProperty("fenced_deal_available")
    private boolean fencedDealAvailable;

    @JsonProperty("merchant_of_record")
    private String merchantOfRecord;

    @JsonProperty("links")
    private Map<String, Link> links;

    @JsonProperty("bed_groups")
    private List<BedGroups> bedGroups;

    public Rate(){}

    public String getId()
    {
        return id;
    }

    public int getAvailableRooms()
    {
        return availableRooms;
    }

    public boolean isRefundable()
    {
        return refundable;
    }

    public boolean isDepositRequired()
    {
        return depositRequired;
    }

    public boolean isFencedDeal()
    {
        return fencedDeal;
    }

    public boolean isFencedDealAvailable()
    {
        return fencedDealAvailable;
    }

    public String getMerchantOfRecord()
    {
        return merchantOfRecord;
    }

    public Map<String, Link> getLinks()
    {
        return links;
    }

    public List<BedGroups> getBedGroups()
    {
        return bedGroups;
    }
}
