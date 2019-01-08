package com.hopper.tests.model;

/**
 * Simple Value Object for Shopping Response.
 */
public class ShoppingResponse
{
    private final String m_propertyId;
    private final String m_roomId;
    private final String m_rateId;
    private final String m_priceCheckEndPoint;

    private ShoppingResponse(Builder builder)
    {
        m_propertyId = builder.m_propertyId;
        m_roomId = builder.m_roomId;
        m_rateId = builder.m_rateId;
        m_priceCheckEndPoint = builder.m_priceCheckEndPoint;
    }

    public String getPropertyId()
    {
        return m_propertyId;
    }

    public String getRoomId()
    {
        return m_roomId;
    }

    public String getRateId()
    {
        return m_rateId;
    }

    public String getPriceCheckEndPoint()
    {
        return m_priceCheckEndPoint;
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public static class Builder
    {
        private String m_propertyId;
        private String m_roomId;
        private String m_rateId;
        private String m_priceCheckEndPoint;

        public Builder withPropertyId(String propertyId)
        {
            m_propertyId = propertyId;
            return this;
        }

        public Builder withRoomId(String roomId)
        {
            m_roomId = roomId;
            return this;
        }

        public Builder withRateId(String rateId)
        {
            m_rateId = rateId;
            return this;
        }

        public Builder withPriceCheckEndPoint(String priceCheckEndPoint)
        {
            m_priceCheckEndPoint = priceCheckEndPoint;
            return this;
        }

        public ShoppingResponse build()
        {
            return new ShoppingResponse(this);
        }
    }
}
