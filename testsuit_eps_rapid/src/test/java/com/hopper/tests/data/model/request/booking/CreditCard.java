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

@JsonIgnoreProperties(ignoreUnknown = true)
public class CreditCard
{
    private static final ObjectMapper YAML_OBJECT_MAPPER = new ObjectMapper(new YAMLFactory());

    @JsonProperty("type")
    private String type;

    @JsonProperty("card_type")
    private String cardType;

    @JsonProperty("number")
    private String number;

    @JsonProperty("security_code")
    private String securityCode;

    @JsonProperty("expiration_month")
    private String expirationMonth;

    @JsonProperty("expiration_year")
    private String expirationYear;

    @JsonProperty("billing_contact")
    private Contact contact;

    public String getType()
    {
        return type;
    }

    public String getCardType()
    {
        return cardType;
    }

    public String getNumber()
    {
        return number;
    }

    public String getSecurityCode()
    {
        return securityCode;
    }

    public String getExpirationMonth()
    {
        return expirationMonth;
    }

    public String getExpirationYear()
    {
        return expirationYear;
    }

    public Contact getContact()
    {
        return contact;
    }

    public void setType(String type)
    {
      this.type = type;
    }
    public void setCardType(String cardType)
    {
        this.cardType = cardType;
    }

   public void setNumber(String number)
   {
       this.number = number;
   }
   public void setSecurityCode(String securityCode){
        this.securityCode = securityCode;
   }

    public void setExpirationMonth(String expirationMonth) {
        this.expirationMonth = expirationMonth;
    }

    public void setExpirationYear(String expirationYear) {
        this.expirationYear = expirationYear;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public static class Contact
    {
        @JsonProperty("given_name")
        private String givenName;

        @JsonProperty("family_name")
        private String familyName;

        @JsonProperty("email")
        private String email;

        @JsonProperty("phone")
        private String phone;

        @JsonProperty("address")
        private Address address;

        public Contact(){}

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

        public Address getAddress()
        {
            return address;
        }

        public void setAddress(Address address)
        {
            this.address = address;
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

        @Override
        public String toString() {
            return "Contact{" +
                    "givenName='" + givenName + '\'' +
                    ", familyName='" + familyName + '\'' +
                    ", email='" + email + '\'' +
                    ", phone='" + phone + '\'' +
                    ", address=" + address +
                    '}';
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Address
    {
        @JsonProperty("line_1")
        private String line1;

        @JsonProperty("line_2")
        private String line2;

        @JsonProperty("line_3")
        private String line3;

        @JsonProperty("city")
        private String city;

        @JsonProperty("state_province_code")
        private String stateCode;

        @JsonProperty("postal_code")
        private String postalCode;

        @JsonProperty("country_code")
        private String countryCode;

        public Address(){}

        public String getLine1()
        {
            return line1;
        }

        public String getLine2()
        {
            return line2;
        }

        public String getLine3()
        {
            return line3;
        }

        public String getCity()
        {
            return city;
        }

        public String getStateCode()
        {
            return stateCode;
        }

        public String getPostalCode()
        {
            return postalCode;
        }

        public String getCountryCode()
        {
            return countryCode;
        }

        public void setCountryCode(String countryCode) {
            this.countryCode = countryCode;
        }

        public void setLine1(String line1) {
            this.line1 = line1;
        }

        public void setCity(String city) {
            this.city = city;
        }

        @Override
        public String toString() {
            return "Address{" +
                    "line1='" + line1 + '\'' +
                    ", line2='" + line2 + '\'' +
                    ", line3='" + line3 + '\'' +
                    ", city='" + city + '\'' +
                    ", stateCode='" + stateCode + '\'' +
                    ", postalCode='" + postalCode + '\'' +
                    ", countryCode='" + countryCode + '\'' +
                    '}';
        }
    }



    public static CreditCard create(final String pathToData)
    {
        try
        {
            Path resourceDirectory = Paths.get("src", "test", "resources", pathToData);
            return YAML_OBJECT_MAPPER.readValue(new File(resourceDirectory.toString()), CreditCard.class);
        }
        catch (IOException e)
        {
            LoggingUtil.log("Unable to parse Config file at location : " + pathToData);
            throw new RuntimeException("Unable to parse Config file at location : " + pathToData, e);
        }
    }

    public Map<String, Object> getAsMap()
    {
        final ImmutableMap.Builder<String, Object> addressMap = ImmutableMap.builder();
        addressMap.put("line_1", getContact().getAddress().getLine1());
        addressMap.put("line_2", getContact().getAddress().getLine2());
        addressMap.put("line_3", getContact().getAddress().getLine3());
        addressMap.put("city", getContact().getAddress().getCity());
        addressMap.put("state_province_code", getContact().getAddress().getStateCode());
        addressMap.put("postal_code", getContact().getAddress().getPostalCode());
        addressMap.put("country_code", getContact().getAddress().getCountryCode());

        final ImmutableMap.Builder<String, Object> billingContactMap = ImmutableMap.builder();
        billingContactMap.put("given_name", getContact().getGivenName());
        billingContactMap.put("family_name", getContact().getFamilyName());
        billingContactMap.put("email", getContact().getEmail());
        billingContactMap.put("phone", getContact().getPhone());
        billingContactMap.put("address", addressMap.build());


        final ImmutableMap.Builder<String, Object> builder = ImmutableMap.builder();
        builder.put("type", getType());
        builder.put("card_type", getCardType());
        builder.put("number", getNumber());
        builder.put("security_code", getSecurityCode());
        builder.put("expiration_month", getExpirationMonth());
        builder.put("expiration_year", getExpirationYear());
        builder.put("billing_contact", billingContactMap.build());

        return builder.build();
    }

    @Override
    public String toString() {
        return "CreditCard{" +
                "type='" + type + '\'' +
                ", cardType='" + cardType + '\'' +
                ", number='" + number + '\'' +
                ", securityCode='" + securityCode + '\'' +
                ", expirationMonth='" + expirationMonth + '\'' +
                ", expirationYear='" + expirationYear + '\'' +
                ", contact=" + contact +
                '}';
    }
}
