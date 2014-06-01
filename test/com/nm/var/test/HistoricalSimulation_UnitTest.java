package com.nm.var.test;

import java.io.File;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import com.nm.var.src.HistoricalSimulation;

public class HistoricalSimulation_UnitTest
{

    private HistoricalSimulation hs;
    private ArrayList<Double>    stockValues;
    private ArrayList<File>      stockDataFiles;
    private int                  confidence;
    private double               calculatedVaR;

    @Before
    public void setUp() throws Exception
    {
        stockValues = new ArrayList<Double>();
        stockDataFiles = new ArrayList<File>();
    }

    @Test
    public void shouldComputeCorrectVaRUsingHistoricalSimulation()
    {
        stockValues.clear();
        stockValues.add( 1000.0 );
        confidence = 99;
        setupWithLargeStockDataFile( stockValues, confidence );
        calculatedVaR = hs.computeValueAtRisk_OneStock();
        System.out.println( "Historical Simulation VaR (1 stock): "
                            + calculatedVaR );
    }

    @Test
    public void shouldComputeVaRUsingHistoricalSimulationForTwoStocks()
    {
        stockValues.clear();
        stockValues.add( 1000.0 );
        stockValues.add( 2000.0 );
        stockDataFiles.clear();
        stockDataFiles.add( new File( "testing/GOOG_Tester.csv" ) );
        stockDataFiles.add( new File( "testing/MSFT_15082013_15112013.csv" ) );
        confidence = 99;
        hs = new HistoricalSimulation( stockValues, stockDataFiles, confidence );
        hs.computeValueAtRisk();
    }

    @Test
    public void shouldComputeVaRUsingHistoricalSimulationForThreeStocks()
    {
        stockValues.clear();
        stockValues.add( 1000.0 );
        stockValues.add( 1500.0 );
        stockValues.add( 2000.0 );
        stockDataFiles.clear();
        stockDataFiles.add( new File( "testing/GOOG_Tester.csv" ) );
        stockDataFiles.add( new File( "testing/GOOG_190913_181013.csv" ) );
        stockDataFiles.add( new File( "testing/MSFT_15082013_15112013.csv" ) );
        confidence = 99;
        hs = new HistoricalSimulation( stockValues, stockDataFiles, confidence );
        hs.computeValueAtRisk();
    }

    private void setupWithLargeStockDataFile(
                                              final ArrayList<Double> portfolioValues,
                                              final int confidence )
    {
        stockDataFiles.clear();
        stockDataFiles.add( new File( "testing/GOOG_Tester.csv" ) );
        hs = new HistoricalSimulation( portfolioValues, stockDataFiles,
                                       confidence );
    }

}
