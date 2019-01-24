package com.hopper.tests.data.model.response.retrieve_booking;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hopper.tests.data.model.request.booking.CreditCard;
import com.hopper.tests.data.model.response.Link;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Room {
    @JsonProperty("id")
    private String id;

    @JsonProperty("property_confirmation_id")
    private String propertyConfirmationId;

    @JsonProperty("bed_group_id")
    private String bedGroupId;

    @JsonProperty("checkin")
    private String checkin;

    @JsonProperty("checkout")
    private String checkout;

    @JsonProperty("number_of_adults")
    private int numberOfAdults;

    @JsonProperty("child_ages")
    private List<Integer> childAges;

    @JsonProperty("given_name")
    private String givenName;

    @JsonProperty("family_name")
    private String familyName;

    @JsonProperty("status")
    private String status;

    @JsonProperty("special_request")
    private String specialRequest;

    @JsonProperty("phone")
    private String phone;

    @JsonProperty("smoking")
    private boolean smoking;

    @JsonProperty("rate")
    private Rate rate;

    @JsonProperty("links")
    private Map<String, Link> links;

    public Room() {
    }

    public String getId() {
        return id;
    }

    public Map<String, Link> getLinks() {
        return links;
    }

    public String getPropertyConfirmationId() {
        return propertyConfirmationId;
    }

    public String getBedGroupId() {
        return bedGroupId;
    }

    public int getNumberOfAdults() {
        return numberOfAdults;
    }

    public List<Integer> getChildAges() {
        return childAges;
    }

    public String getGivenName() {
        return givenName;
    }

    public String getFamilyName() {
        return familyName;
    }

    public String getStatus() {
        return status;
    }

    public String getSpecialRequest() {
        return specialRequest;
    }

    public boolean isSmoking() {
        return smoking;
    }

    public Rate getRate() {
        return rate;
    }

    public String getCheckin() {
        return checkin;
    }

    public String getCheckout() {
        return checkout;
    }

    public String getPhone() {
        return phone;
    }
}
