package com.nm.var.src;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
/**
 * Runs a backtesting routine on a user-defined setup.
 */
public class BackTesting
{
    /** Confidence level to compute VaR for testing. */
    private int       confidence         = 99;
    /** Time period to compute VaR for testing. */
    private int       timePeriod         = 1;
    /** Number of days to compute VaR for testing. */
    private int       numberOfDaysToTest = 100;
    /** The portfolio to run the backtest on. */
    private Portfolio portfolio;
    /** Result of the backtesting. */
    private String    output;
    /** Model to use to estimate VaR for backtesting. */
    private String    model;

    public BackTesting()
    {
        output = "";
        System.out.println( "Backtesting VaR Estimation over " + timePeriod + " day at "
                            + confidence + "%" );
    }

    public BackTesting( SimulationSetup setup )
    {
        this.portfolio = setup.getPortfolio();
        this.confidence = setup.getConfidenceLevel();
        this.model = setup.getModel();
        this.timePeriod = setup.getTimeHorizon();
        output = "";
    }

    public void backTest_ModelBuilding_OneStock()
    {
        // compute VaR for 100 days using past data
        Portfolio portfolioWithOneStock = getPortfolioWithOneStock();
        File prevStockData = portfolioWithOneStock.getAssets().get( 0 ).getData();
        ArrayList<File> stockPriceDataFiles = new ArrayList<File>();
        stockPriceDataFiles.add( prevStockData );
        ArrayList<Double> portfolioValues = new ArrayList<Double>();
        double value = portfolioWithOneStock.getAssets().get( 0 ).getInvestment();
        portfolioValues.add( value );

        ModelBuilding modelBuilding = new ModelBuilding( portfolioValues, stockPriceDataFiles,
                                                         confidence, timePeriod );
        double[] returns = VarUtils.getReturnsFromFile( prevStockData );
        int position = returns.length - 1 - numberOfDaysToTest;
        double[] estimations = modelBuilding.computeForBackTesting( returns, numberOfDaysToTest );
        output += "Backtesting Model Building:\n";
        compareEstimationsWithActualLosses_OneStock( estimations, returns, position, value );
    }

    public void backTest_HistoricalSimulation()
    {
        Portfolio portfolioWithOneStock = getPortfolioWithOneStock();
        HistoricalSimulation hs = new HistoricalSimulation( portfolioWithOneStock, confidence );
        double[] estimations = hs.estimateVaRForBackTestingOneStock( numberOfDaysToTest );
        double[] allReturns = VarUtils.getReturnsFromFile( portfolioWithOneStock.getStockPriceDataFiles()
                                                                                .get( 0 ) );
        int position = allReturns.length - 1 - numberOfDaysToTest;
        output += "Backtesting Historical Simulation:\n";
        compareEstimationsWithActualLosses_OneStock( estimations, allReturns, position,
                                                     portfolioWithOneStock.getAssetsValue() );

    }

    public void backTest_MonteCarlo()
    {
        Portfolio portfolioWithOneStock = getPortfolioWithOneStock();
        MonteCarloSimulation mc = new MonteCarloSimulation( portfolioWithOneStock, confidence, 10 );
        double[] allReturns = VarUtils.getReturnsFromFile( portfolioWithOneStock.getStockPriceDataFiles()
                                                                                .get( 0 ) );
        mc.setTimePeriod( 1 );
        double[] estimations = mc.estimateVaRForBacktesting_OneStock( numberOfDaysToTest );
        int position = allReturns.length - 1 - numberOfDaysToTest;
        output += "Backtesting Monte Carlo Simulation:\n";
        compareEstimationsWithActualLosses_OneStock( estimations, allReturns, position,
                                                     portfolioWithOneStock.getAssetsValue() );
    }

    public void backTestPortfolio()
    {
        switch( model )
        {
            case VarUtils.MB:
                backTestModelBuilding();
                break;
            case VarUtils.HS:
                backTestHistoricalSimulation();
                break;
            case VarUtils.MC:
                backTestMonteCarloSimulation();
                break;
        }
    }

    private void backTestModelBuilding()
    {
        output += "Backtesting Model Building:\n";
        ModelBuilding mb = new ModelBuilding( portfolio, confidence, timePeriod );
        File data = portfolio.getAssets().get( 0 ).getData();
        double value = portfolio.getAssets().get( 0 ).getInvestment();
        double[] returns = VarUtils.getReturnsFromFile( data );
        int position = returns.length - 1 - numberOfDaysToTest;
        double[] estimations = mb.computeForBackTesting( returns, numberOfDaysToTest );
        compareEstimationsWithActualLosses_OneStock( estimations, returns, position, value );
    }

    private void backTestHistoricalSimulation()
    {
        output += "Backtesting Historical Simulation:\n";

        HistoricalSimulation hs = new HistoricalSimulation( portfolio, confidence );
        hs.setVarHorizon( timePeriod );
        double[] estimations = hs.estimateVaRForBackTestingOneStock( numberOfDaysToTest );
        double[] allReturns = VarUtils.getReturnsFromFile( portfolio.getStockPriceDataFiles()
                                                                    .get( 0 ) );
        int position = allReturns.length - 1 - numberOfDaysToTest;
        compareEstimationsWithActualLosses_OneStock( estimations, allReturns, position,
                                                     portfolio.getAssetsValue() );

    }

    private void backTestMonteCarloSimulation()
    {
        output += "Backtesting Monte Carlo Simulation:\n";

        MonteCarloSimulation mc = new MonteCarloSimulation( portfolio, confidence, timePeriod );
        double[] allReturns = VarUtils.getReturnsFromFile( portfolio.getStockPriceDataFiles()
                                                                    .get( 0 ) );
        mc.setTimePeriod( 1 );
        double[] estimations = mc.estimateVaRForBacktesting_OneStock( numberOfDaysToTest );
        int position = allReturns.length - 1 - numberOfDaysToTest;
        compareEstimationsWithActualLosses_OneStock( estimations, allReturns, position,
                                                     portfolio.getAssetsValue() );

    }

    /**
     * Compares estimation of VaR with actual losses and updates the result.
     * @param estimations VaR estimations to test
     * @param allReturns complete series of returns of stock data
     * @param dayToStart day to start comparing VaR and actual losses from returns
     * @param initialValue of the asset
     */
    private void compareEstimationsWithActualLosses_OneStock( double[] estimations,
                                                              double[] allReturns, int dayToStart,
                                                              double initialValue )
    {
        int position = dayToStart;
        int numberOfExceptions = 0;
        double[] returnsToUse = Arrays.copyOfRange( allReturns, position, allReturns.length - 1 );
        double[] returnsOverVarHorizon = VarUtils.getReturnsOverVarHorizon( returnsToUse,
                                                                            timePeriod );
        double[] actualLosses = new double[numberOfDaysToTest];
        // reduce days as number of returns over time horizon decreases as time horizon grows 
        for( int day = 0 ; day < numberOfDaysToTest - (timePeriod - 1) ; day++ )
        {
            double actualLoss = initialValue
                                 - ( initialValue * Math.exp( returnsOverVarHorizon[day] ) );
            actualLosses[day] = actualLoss;
            if( actualLoss > estimations[day] )
            {
                numberOfExceptions++;
            }
            position++;
        }

        int acceptableExceptions = (int) Math.floor( numberOfDaysToTest
                                                     * ( 1 - ( ( (double) confidence ) / 100 ) ) );

        this.output += ( "\t Acceptable exceptions: " + acceptableExceptions
                         + " Number of exceptions = " + numberOfExceptions + "\n" );
    }
    
    /**
     * @return a pre-configured portfolio with one stock.
     */
    private Portfolio getPortfolioWithOneStock()
    {
        File prevStockData = new File( "testing/MSFT_Apr2012_Apr2013.csv" );
        ArrayList<File> stockPriceDataFiles = new ArrayList<File>();
        stockPriceDataFiles.add( prevStockData );
        ArrayList<Double> portfolioValues = new ArrayList<Double>();
        double msftInvestment = 1000000.0;
        portfolioValues.add( msftInvestment );
        Asset msft = new Asset( prevStockData, "MSFT", msftInvestment );
        Portfolio portfolio = new Portfolio();
        portfolio.addAsset( msft );
        return portfolio;
    }
    
    /**
     * @return the result of the backtesting
     */
    public String getResult()
    {
        return output;
    }

}
