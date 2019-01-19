package com.hopper.tests.util.validations.model;

/**
 *
 * @param <T>
 */
public class Range<T extends Comparable<? super T>>
{
    public enum BoundType
    {
        INCLUSIVE,
        EXCLUSIVE,
        ;
    }

    private final T m_min;
    private final T m_max;
    private final BoundType m_boundType;

    public Range(T min, T max, BoundType boundType)
    {
        m_min = min;
        m_max = max;
        m_boundType = boundType;
    }

    public boolean isWithInBounds(T value)
    {
        if (m_boundType == BoundType.INCLUSIVE)
        {
            return value.compareTo(m_max) <= 0 && value.compareTo(m_min) >= 0;
        }

        return value.compareTo(m_max) < 0 && value.compareTo(m_min) > 0;
    }
}
