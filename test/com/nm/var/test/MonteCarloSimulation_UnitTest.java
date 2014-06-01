package com.nm.var.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import com.nm.var.src.MonteCarloSimulation;

public class MonteCarloSimulation_UnitTest
{
    MonteCarloSimulation      mc = new MonteCarloSimulation();
    private ArrayList<Double> values;
    private ArrayList<File>   stockFiles;

    @Before
    public void setUp() throws Exception
    {
        values = new ArrayList<Double>();
        stockFiles = new ArrayList<File>();
    }

    @Test
    public void shouldSimulateThreeFinalReturnsForThreeStocks()
    {
        mc.setNumberOfStocks( 3 );
        mc.setTimePeriod( 5 );
        ArrayList<double[]> simulatedReturns = mc.simulateReturns();
        assertEquals( 3, simulatedReturns.get( 0 ).length );
    }

    @Test
    public void shouldReturnMonteCarloSimulatedVaR()
    {
        values.clear();
        values.add( 1000000.0 );
        mc = new MonteCarloSimulation( values, null, 99, 10 );
        double monteCarloFinalVar = mc.getMonteCarloFinalVar();
        double monteCarloMaximumVar = mc.getMonteCarloMaximumVar();
        assertTrue( monteCarloMaximumVar >= monteCarloFinalVar );
    }

    @Test
    public void shouldReturnCorrectVARForTwoStocks()
    {
        stockFiles.clear();
        stockFiles.add( new File( "testing/APPLE.csv" ) );
        stockFiles.add( new File( "testing/MSFT_15082013_15112013.csv" ) );
        values.clear();
        values.add( 1000.0 );
        values.add( 2000.0 );
        MonteCarloSimulation sim = new MonteCarloSimulation( values, stockFiles, 99, 10 );
        sim.computeValueAtRisk();
        double monteCarloFinalVar = sim.getMonteCarloFinalVar();
        double monteCarloMaximumVar = sim.getMonteCarloMaximumVar();
        assertTrue( monteCarloMaximumVar >= monteCarloFinalVar );
        System.out.println( "Monte Carlo finished." );
    }

}
