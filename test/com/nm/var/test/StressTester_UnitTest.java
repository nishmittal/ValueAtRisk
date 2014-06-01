package com.nm.var.test;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import com.nm.var.src.Asset;
import com.nm.var.src.Portfolio;
import com.nm.var.src.StressTester;

public class StressTester_UnitTest
{
    StressTester st;

    @Before
    public void setUp() throws Exception
    {
        st = new StressTester( getPortfolioWithOneStock() );
    }

    @Test
    public void testStressReturnsForOneStock()
    {
        st.run();
        compareValues( st );
    }
    
    @Test
    public void testStressReturnsForMultipleStocks()
    {
        Portfolio p = getPortfolioWithOneStock();
        Asset a = new Asset( new File( "testing/APPLE.csv" ), "APPL", 2000.0 );
        p.addAsset( a );
        st = new StressTester( p );
        st.run();
        compareValues( st );
    }

    /**
     * @param simulatedValues
     */
    private void compareValues( StressTester st)
    {
        double[] simulatedValues = st.getSimulatedValues();
        double[] losses = st.getLosses();
        Arrays.sort( simulatedValues );
        Arrays.sort( losses );
        double minValue = simulatedValues[0];
        double maxValue = simulatedValues[simulatedValues.length - 1];
        System.out.println( "Min|Max portfolio value = " + minValue + " | " + maxValue );
        double minLoss = losses[0];
        double maxLoss = losses[losses.length - 1];
        System.out.println( "Min|Max portfolio loss  = " + minLoss + " | " + maxLoss );
        
    }


    private Portfolio getPortfolioWithOneStock()
    {
        File prevStockData = new File( "testing/MSFT_Apr2012_Apr2013.csv" );
        ArrayList<File> stockPriceDataFiles = new ArrayList<File>();
        stockPriceDataFiles.add( prevStockData );
        ArrayList<Double> portfolioValues = new ArrayList<Double>();
        double msftInvestment = 1000.0;
        portfolioValues.add( msftInvestment );
        Asset msft = new Asset( prevStockData, "MSFT", msftInvestment );
        Portfolio portfolio = new Portfolio();
        portfolio.addAsset( msft );
        return portfolio;
    }

}
