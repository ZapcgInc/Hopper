package com.hopper.tests.data.model.response.retrieve_booking;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hopper.tests.data.model.response.CancelPolicies;
import com.hopper.tests.data.model.response.Link;
import com.hopper.tests.data.model.response.RoomPrice;
import com.hopper.tests.data.model.response.shopping.Amenities;
import com.hopper.tests.data.model.response.shopping.BedGroups;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Rate {
    @JsonProperty("id")
    private String id;

    @JsonProperty("refundable")
    private boolean refundable;


    @JsonProperty("merchant_of_record")
    private String merchantOfRecord;

//    @JsonProperty("links")
//    private Map<String, Link> links;

//    @JsonProperty("fees")
//    private Map<String,Fees> fees;
//
//    @JsonProperty("deposit_policies")
//    private List<Map<String,Object>> depositPolicies;

//    @JsonProperty("cancel_penalties")
//    private List<CancelPolicies> cancelPenalties;
//
//    @JsonProperty("amenities")
//    private List<Amenities> amenities;

//    @JsonProperty("nightly")
//    private List<List<Object>> nightlyRates;


    public Rate() {
    }

    public String getId() {
        return id;
    }

    public boolean isRefundable() {
        return refundable;
    }

    public String getMerchantOfRecord() {
        return merchantOfRecord;
    }

//    public Map<String, Link> getLinks() {
//        return links;
//    }
//
//    public List<CancelPolicies> getCancelPenalties() {
//        return cancelPenalties;
//    }
//
//    public List<Amenities> getAmenities() {
//        return amenities;
//    }
}
