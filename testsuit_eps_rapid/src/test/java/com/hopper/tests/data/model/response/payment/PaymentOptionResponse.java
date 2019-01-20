package com.hopper.tests.data.model.response.payment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.Optional;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentOptionResponse
{
    @JsonProperty("affiliate_collect")
    private AffiliateCollect affiliateCollect;

    @JsonProperty("credit_card")
    private CreditCard creditCard;

    public PaymentOptionResponse() {}

    public AffiliateCollect getAffiliateCollect()
    {
        return affiliateCollect;
    }

    public Optional<CreditCard> getCreditCard()
    {
        return Optional.ofNullable(creditCard);
    }


    public static class AffiliateCollect
    {
        @JsonProperty("name")
        private String name;

        public AffiliateCollect() {}

        public String getName()
        {
            return name;
        }
    }

    public static class CreditCard
    {
        @JsonProperty("name")
        private String displayName;

        @JsonProperty("card_options")
        private List<CardOptions> cardOptions;

        public CreditCard() {}

        public String getDisplayName()
        {
            return displayName;
        }

        public List<CardOptions> getCardOptions()
        {
            return cardOptions != null ? cardOptions : ImmutableList.of();
        }
    }

    public static class CardOptions
    {
        @JsonProperty("name")
        private String name;

        @JsonProperty("card_type")
        private String cardType;

        @JsonProperty("processing_country")
        private String processingCountry;

        public CardOptions() {}

        public String getName()
        {
            return name;
        }

        public String getCardType()
        {
            return cardType;
        }

        public String getProcessingCountry()
        {
            return processingCountry;
        }
    }
}
