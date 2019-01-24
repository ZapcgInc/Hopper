package com.hopper.tests.data.model.request.booking;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.common.collect.ImmutableMap;
import com.hopper.tests.util.logging.LoggingUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

/**
 * Simple Value Object to hold customer Info
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Customer
{
    private static final ObjectMapper YAML_OBJECT_MAPPER = new ObjectMapper(new YAMLFactory());

    @JsonProperty("title")
    private String title;

    @JsonProperty("given_name")
    private String givenName;

    @JsonProperty("family_name")
    private String familyName;

    @JsonProperty("email")
    private String email;

    @JsonProperty("phone")
    private String phone;

    @JsonProperty("smoking")
    private String smoking;

    @JsonProperty("special_request")
    private String specialRequest;

    public String getTitle()
    {
        return title;
    }

    public String getGivenName()
    {
        return givenName;
    }

    public String getFamilyName()
    {
        return familyName;
    }

    public String getEmail()
    {
        return email;
    }

    public String getPhone()
    {
        return phone;
    }

    public String getSmoking()
    {
        return smoking;
    }

    public String getSpecialRequest()
    {
        return specialRequest;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setSmoking(String smoking) {
        this.smoking = smoking;
    }

    public void setSpecialRequest(String specialRequest) {
        this.specialRequest = specialRequest;
    }

    public static Customer create(final String pathToData)
    {
        try
        {
            Path resourceDirectory = Paths.get("src", "test", "resources", pathToData);
            return YAML_OBJECT_MAPPER.readValue(new File(resourceDirectory.toString()), Customer.class);
        }
        catch (IOException e)
        {
            LoggingUtil.log("Unable to parse Config file at location : " + pathToData);
            throw new RuntimeException("Unable to parse Config file at location : " + pathToData, e);
        }
    }

    public Map<String, String> getAsMap()
    {
        final ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();

        builder.put("title", getTitle());
        builder.put("given_name", getGivenName());
        builder.put("family_name", getFamilyName());
        builder.put("email", getEmail());
        builder.put("phone", getPhone());
        builder.put("smoking", getSmoking());
        builder.put("special_request", getSpecialRequest());

        return builder.build();
    }

    @Override
    public String toString() {
        return "Customer{" +
                "title='" + title + '\'' +
                ", givenName='" + givenName + '\'' +
                ", familyName='" + familyName + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", smoking='" + smoking + '\'' +
                ", specialRequest='" + specialRequest + '\'' +
                '}';
    }
}
