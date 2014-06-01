/**
 * 
 */
package com.nm.var.src;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Implementation of the Historical Simulation VaR model.
 */
public class HistoricalSimulation
{
    /** List of investments for a portfolio. */
    private ArrayList<Double> portfolioValues;
    /** List of stock data files for a portfolio. */
    private ArrayList<File>   stockPriceDataFiles;
    /** The confidence level to compute VaR for. */
    private int               confidence;
    /** The number of assets in this portfolio. */
    private int               numberOfStocks;
    /** The portfolio being used for VaR computation. */
    private Portfolio         portfolio;
    /** The option pricing model being used to price options in this model. */
    private String            optionPricingType;
    /** The number of days to compute VaR for. */
    private int               varHorizon   = 1;
    /** An array to store the compute final and maximum VaRs from computation. */
    private double[]          finalMaxVars = new double[2];

    /**
     * Constructor to initialise a Historical Simulation model using a list of investments, list of stock price data files and a confidence level.
     * @param confidence
     * @param stockPriceDataFiles
     * @param portfolioValues the investments corresponding the the list of stock data files
     * 
     */
    public HistoricalSimulation( ArrayList<Double> portfolioValues,
                                 ArrayList<File> stockPriceDataFiles, int confidence )
    {
        this.portfolioValues = portfolioValues;
        this.stockPriceDataFiles = stockPriceDataFiles;
        this.confidence = confidence;
        this.numberOfStocks = portfolioValues.size();
    }
    
    /**
     * Constructor to initialise a Historical Simulation model using a portfolio and a confidence level.
     * @param portfolio the portfolio to compute VaR for.
     * @param confidence the confidence level at which to compute VaR.
     */
    public HistoricalSimulation( Portfolio portfolio, int confidence )
    {
        this.portfolio = portfolio;
        this.stockPriceDataFiles = portfolio.getStockPriceDataFiles();
        this.portfolioValues = portfolio.getInvestments();
        this.confidence = confidence;
        this.numberOfStocks = portfolioValues.size();
    }

    /** Blank constructor. */
    public HistoricalSimulation()
    {
    }
    /**
     * Computes value at risk.
     */
    public void computeValueAtRisk()
    {
        if( numberOfStocks == 1 )
        {
            System.out.println( "Historical Simulation VaR (" + numberOfStocks
                                + " stocks): " + Math.round( computeValueAtRisk_OneStock() ) );
        }
        else
        {

            ArrayList<double[]> portfolioReturns = VarUtils.getReturnsFromFiles( stockPriceDataFiles );

            /*
             * System.out.println( "Historical Simulation VaR (" + numberOfStocks
             * + " stocks): "
             * + Math.round( computeForMultipleStocks( portfolioReturns ) ) );
             */
            computeForMultipleStocks( portfolioReturns );
        }

    }
    
    /**
     * Computes value at risk.
     * @return array containing final and max VaRs.
     */
    public double[] computeValueAtRiskForPortfolio()
    {
        return computeForPortfolio();
    }

    /**
     * Initial implementation of the historical simulation method of computing
     * VaR. Strategy used:
     * <ol>
     * <li>get sorted list of periodic returns</li>
     * <li>compute desired percentile from requested confidence level</li>
     * <li>lookup this percentile in the sorted list of returns and get the return at this location</li>
     * <li>multiply the portfolio value by the selected return as a measure of VaR</li>
     * </ol>
     * 
     * @return
     */
    public double computeValueAtRisk_OneStock()
    {
        double[] returns = VarUtils.getReturnsFromFile( stockPriceDataFiles
                                                                           .get( 0 ) );
        return getVaROneStock( returns );
    }
    
    /**
     * Get the VaR estimate from a percentile of the given returns.
     * @param returns historical returns of the asset
     * @return VaR at the percentile relevant to the confidence level from the returns.
     */
    private double getVaROneStock( double[] returns )
    {
        Arrays.sort( returns );
        double percentile = VarUtils.getPercentile( returns, confidence );
        double var = portfolioValues.get( 0 )
                     - ( portfolioValues.get( 0 ) * Math.exp( percentile ) );
        return var;
    }

    /**
     * Computes VaR for multiple stocks.
     * @param confidence
     * @param portfolioReturns
     * @param investments
     * @return final VaR
     */
    public double computeForMultipleStocks( ArrayList<double[]> portfolioReturns )
    {
        double var = 0.0;

        int numberOfStocks = portfolioReturns.size();

        // should be the smallest of the lengths of the array of returns.
        int[] lengthsOfReturns = new int[numberOfStocks];
        for( double[] arr : portfolioReturns )
        {
            lengthsOfReturns[portfolioReturns.indexOf( arr )] = arr.length;
        }
        Arrays.sort( lengthsOfReturns );
        int numberOfReturns = lengthsOfReturns[0];

        System.out.println( "\t Days of data from returns: " + numberOfReturns );

        double[] possiblePortfolioValues = new double[numberOfReturns];

        // calculate overall value for each previous return
        for( int i = 0 ; i < numberOfReturns ; i++ )
        {
            double possibleChange = 0.0;
            for( int n = 0 ; n < numberOfStocks ; n++ )
            {
                possibleChange += portfolioValues.get( n )
                                  * Math.exp( portfolioReturns.get( n )[i] );
            }
            possiblePortfolioValues[i] = possibleChange;
        }

        Arrays.sort( possiblePortfolioValues );

        double valueAtPercentile = VarUtils.getPercentile( possiblePortfolioValues, confidence );
        double portfolioValue = 0.0;
        for( double value : portfolioValues )
        {
            portfolioValue += value;
        }
        valueAtPercentile = portfolioValue - valueAtPercentile;
        var = Math.round( Math.abs( valueAtPercentile ) );
        finalMaxVars[0] = var;
        double maxVar = portfolioValue - possiblePortfolioValues[0];
        finalMaxVars[1] = maxVar;
        return var;
    }

    /** Computes VaR for portfolio. */
    private double[] computeForPortfolio()
    {
        double initialPortFolioValue = 0.0;
        double finalPortfolioValue = 0.0;

        ArrayList<Double> investments = portfolio.getInvestments();

        double initialOptionsValue = 0.0;

        ArrayList<Option> options = portfolio.getOptions();

        for( Option option : options )
        {
            initialOptionsValue += ( option.getInitialStockPrice() * option.getNumShares() );
        }

        initialPortFolioValue = VarUtils.sumOf( investments ) + initialOptionsValue;

        /***************** STOCKS *********************/
        // use previous functionality to compute final prices of the portfolio and options
        ArrayList<double[]> portfolioReturns = VarUtils.getReturnsFromFiles( portfolio.getStockPriceDataFiles() );
        int numberOfStocks = portfolioReturns.size();

        // should be the smallest of the lengths of the array of returns.
        int[] lengthsOfReturns = new int[numberOfStocks];
        for( double[] arr : portfolioReturns )
        {
            lengthsOfReturns[portfolioReturns.indexOf( arr )] = arr.length;
        }
        Arrays.sort( lengthsOfReturns );
        int numberOfReturns = lengthsOfReturns[0];

        double[] possiblePortfolioValues = new double[numberOfReturns];

        // calculate overall value for each previous return
        for( int i = 0 ; i < numberOfReturns ; i++ )
        {
            double possibleChange = 0.0;
            for( int n = 0 ; n < numberOfStocks ; n++ )
            {
                possibleChange += portfolioValues.get( n )
                                  * Math.exp( portfolioReturns.get( n )[i] );
            }
            possiblePortfolioValues[i] = possibleChange;
        }

        Arrays.sort( possiblePortfolioValues );

        double stocksValueAtPercentile = VarUtils.getPercentile( possiblePortfolioValues,
                                                                 confidence );

        /***************** OPTIONS *********************/
        double optionsFinalValue = 0.0, optionsMinValue = 0.0;
        for( Option option : options )
        {
            double[] priceOption = priceOption( option );
            optionsFinalValue += priceOption[0];
            optionsMinValue += priceOption[1];
        }

        finalPortfolioValue = stocksValueAtPercentile + optionsFinalValue;

        double portfolioFinalVaR = initialPortFolioValue - finalPortfolioValue;
        double portfolioMinValue = possiblePortfolioValues[0] + optionsMinValue;
        double portfolioMaxVaR = initialPortFolioValue - portfolioMinValue;

        finalMaxVars[0] = Math.round( portfolioFinalVaR );
        finalMaxVars[1] = Math.round( portfolioMaxVaR );

        return finalMaxVars;

    }
    
    /**
     * Prices an option using a pre-defined option pricing type.
     * @param option the option to value
     * @return final and minimum option prices during simulation for VaR computation.
     */
    public double[] priceOption( Option option )
    {
        double[] finalMinOptionsValue = new double[2];
        // get returns from file
        double[] returns = VarUtils.getReturnsFromFile( option.getPriceData() );
        int numberOfReturns = returns.length;
        ArrayList<Double> possibleOptionValues = new ArrayList<Double>();
        // compute possible value change for each return in data
        double historicalValue = 0.0;
        double stockPrice = 0.0;
        int initialTimeToMaturity = option.getTimeToMaturity();
        for( int i = 0 ; i < numberOfReturns ; i++ )
        {
            // TODO need to check if this is the right price used
            int currentTimeToMaturity = initialTimeToMaturity - i;
            stockPrice = Math.exp( returns[i] ) * option.getInitialStockPrice();
            if( currentTimeToMaturity >= 0 )
            {
                switch( optionPricingType )
                {
                    case VarUtils.BS:
                        int type = VarUtils.convertToCallOrPut( option.getOptionType() );
                        if( type == VarUtils.ERROR_VAL )
                        {
                            return finalMinOptionsValue;
                        }
                        BlackScholes bs = new BlackScholes();
                        historicalValue = bs.compute( type, stockPrice, option.getStrike(),
                                                      currentTimeToMaturity,
                                                      option.getInterest(),
                                                      option.getDailyVolatility() );
                        break;
                    case VarUtils.BT:
                        // update the initial price and maturity to today's value
                        option.setInitialStockPrice( stockPrice );
                        option.setTimeToMaturity( currentTimeToMaturity );
                        BinomialTree bt = new BinomialTree( option );
                        historicalValue = bt.getOptionPrice();
                        break;
                    case VarUtils.MC:
                        option.setInitialStockPrice( stockPrice );
                        option.setTimeToMaturity( currentTimeToMaturity );
                        MonteCarloSimulation mc = new MonteCarloSimulation();
                        historicalValue = mc.priceOptionUsingMonteCarlo( option )[0];
                        break;
                }

                possibleOptionValues.add( historicalValue );
            }
            else
            {
                break;
            }
        }
        double[] values = new double[possibleOptionValues.size()];
        for( int i = 0 ; i < possibleOptionValues.size() ; i++ )
        {
            values[i] = possibleOptionValues.get( i );
        }
        // sort possible values
        Arrays.sort( values );

        // select value from percentile
        double valueAtPercentile = VarUtils.getPercentile( values, confidence );
        double minValue = values[0];
        finalMinOptionsValue = new double[] { valueAtPercentile, minValue };
        return finalMinOptionsValue;
    }

    /**
     * @param optionPricingType the optionPricingType to set
     */
    public void setOptionPricingType( String optionPricingType )
    {
        this.optionPricingType = optionPricingType;
    }

    /**
     * @param varHorizon the varHorizon to set
     */
    public void setVarHorizon( int varHorizon )
    {
        this.varHorizon = varHorizon;
    }
    
    /**
     * Estimates VaR for backtesting purposes.
     * @param numberOfDaysToTest the number of days to estimate VaR for
     * @return estimations of VaR over the next number of days specified
     */
    public double[] estimateVaRForBackTestingOneStock( int numberOfDaysToTest )
    {
        double[] returns = VarUtils.getReturnsFromFile( stockPriceDataFiles.get( 0 ) );
        int numberOfReturnsToUse = returns.length - 1 - numberOfDaysToTest;
        double[] estimations = new double[numberOfDaysToTest];

        for( int day = 0 ; day < numberOfDaysToTest ; day++ )
        {
            double[] returnsToUse = Arrays.copyOf( returns, numberOfReturnsToUse );
            double[] returnsOverVarHorizon = VarUtils.getReturnsOverVarHorizon( returnsToUse, varHorizon );
            estimations[day] = getVaROneStock( returnsOverVarHorizon );
            numberOfReturnsToUse++;
        }

        return estimations;
    }

    /**
     * @return an array containing the final and maximum VaRs computed using this model.
     */
    public double[] getFinalMaxVars()
    {
        return finalMaxVars;
    }
}
