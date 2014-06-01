package com.nm.var.test;

import org.junit.Before;
import org.junit.Test;

import com.nm.var.src.BackTesting;

public class BackTesting_UnitTest
{
    private BackTesting backTesting;

    @Before
    public void setUp() throws Exception
    {
    }

    @Test
    public void testRunTestForOneStock()
    {
        backTesting = new BackTesting();
        backTesting.backTest_ModelBuilding_OneStock();
        backTesting.backTest_HistoricalSimulation();
        backTesting.backTest_MonteCarlo();
        System.out.println(backTesting.getResult());
    }
    
}
