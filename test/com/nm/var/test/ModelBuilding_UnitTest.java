package com.nm.var.test;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import com.nm.var.src.ModelBuilding;

public class ModelBuilding_UnitTest
{

    private ModelBuilding     modelBuilding;
    private double            delta = 1.0;
    private ArrayList<Double> stockValues;
    private ArrayList<File>   stockDataFiles;
    private int               confidence;
    private int               timePeriod;
    private double            expectedVaR, calculatedVaR;

    @Before
    public void setUp() throws Exception
    {
        stockValues = new ArrayList<Double>();
        stockDataFiles = new ArrayList<File>();
    }

    @Test
    public void shouldReturnCorrectTenDayVaRForGoogleStocks()
    {
        stockValues.clear();
        stockValues.add( 1000000.0 );
        stockDataFiles.clear();
        stockDataFiles.add( new File( "testing/GOOG_190913_181013.csv" ) );
        confidence = 99;
        timePeriod = 10;
        setCalculationParameters( stockValues, timePeriod, confidence,
                                  stockDataFiles );
        calculatedVaR = modelBuilding.computeForOneStock();
        expectedVaR = 220276.0;
        assertEquals( expectedVaR, calculatedVaR, delta );
    }

    @Test
    public void shouldComputeCorrectVaRForTwoStocks()
    {
        stockValues.clear();
        stockValues.add( 10000000.0 );
        stockValues.add( 5000000.0 );
        confidence = 99;
        timePeriod = 10;
        setCalculationParameters( stockValues, timePeriod, confidence, null );
        calculatedVaR = modelBuilding.computeForTwoStocks( 0.02, 0.01, 0.3 );
        expectedVaR = 1622657.0;
        assertEquals( expectedVaR, calculatedVaR, delta );
    }

    @Test
    public void shouldComputeCorrectVaRForThreeStocks()
    {
        stockDataFiles.clear();
        stockDataFiles.add( new File( "testing/GOOG_190913_181013.csv" ) );
        stockDataFiles.add( new File( "testing/GOOG_Tester.csv" ) );
        stockDataFiles.add( new File( "testing/MSFT_15082013_15112013.csv" ) );
        stockValues.clear();
        stockValues.add( 1000000.0 );
        stockValues.add( 1500000.0 );
        stockValues.add( 2000000.0 );
        confidence = 99;
        timePeriod = 10;
        setCalculationParameters( stockValues, timePeriod, confidence,
                                  stockDataFiles );
        calculatedVaR = modelBuilding.computeForMultipleStocks();
        expectedVaR = 413122.0;
        assertEquals( expectedVaR, calculatedVaR, delta );
    }

    private void setCalculationParameters(
                                           final ArrayList<Double> portfolioValues, final int timePeriod,
                                           final int confidence, final ArrayList<File> stockPriceDataFiles )
    {
        modelBuilding = new ModelBuilding( portfolioValues, stockPriceDataFiles,
                                           confidence, timePeriod );
    }
}
