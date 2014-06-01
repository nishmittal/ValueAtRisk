package com.nm.var.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.nm.var.src.ConfidenceLevel;

public class ConfidenceLevel_UnitTest
{
    @Test
    public void shouldReturnCorrectDoubleForEnumEntry()
    {
        ConfidenceLevel test = ConfidenceLevel.NINETYNINE;
        int value = test.getValue();
        assertEquals( value, 99 );
    }

}
