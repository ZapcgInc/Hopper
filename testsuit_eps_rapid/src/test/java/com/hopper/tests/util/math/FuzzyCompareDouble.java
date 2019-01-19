package com.hopper.tests.util.math;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Compares Double values within a TOLERANCE
 */
public class FuzzyCompareDouble
{
    private static final double TOLERANCE = 0.01;

    public static boolean equal(Double d1, Double d2)
    {
        return Math.abs(_round(d1).compareTo(_round(d2))) <= TOLERANCE;
    }

    private static Double _round(Double d)
    {
        return new BigDecimal(d)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }
}
