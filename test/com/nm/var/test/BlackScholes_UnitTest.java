package com.nm.var.test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.nm.var.src.BlackScholes;

public class BlackScholes_UnitTest
{
    BlackScholes bs;

    @Before
    public void setUp() throws Exception
    {
        bs = new BlackScholes();
    }

    @Test
    public void shouldReturnCorrectCNDFValue()
    {

    }

    @Test
    public void shouldReturnCorrectCallPrice()
    {
        double callPrice = bs.compute( 0, 80, 100, 0.5, 0.07, 0.03 * Math.sqrt( 252 ) );
        assertEquals( 5.29, callPrice, 0.01 );
    }

    @Test
    public void shouldReturnCorrectPutPrice()
    {
        double putPrice = bs.compute( 1, 80, 100, 0.5, 0.07, 0.03 * Math.sqrt( 252 ) );
        assertEquals( 21.85, putPrice, 0.01 );
    }

}
