/**
 * 
 */
package com.nm.var.src;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * This class implements the Model-Building Value at Risk model. 
 */
public class ModelBuilding
{
    /** List of investments made. */
    private ArrayList<Double> portfolioValues;
    /** List of historical price data files for assets in same order to investments. */
    private ArrayList<File>   stockPriceDataFiles;
    /** The confidence at which to compute VaR. */
    private int               confidence;
    /** The time period to compute VaR over. */
    private int               timePeriod;
    /** The number of standard deviations our risk is unlikely to exceed, affected by the confidence level. */
    private double            zDelta;
    /** The number of assets currently held in this model. */
    private int               numberOfStocks;

    /**
     * Initialises a Model-Building VaR model using a portfolio.
     * @param p portfolio to compute VaR for
     * @param confidence
     * @param timePeriod
     */
    public ModelBuilding( Portfolio p, int confidence, int timePeriod )
    {
        this.confidence = confidence;
        this.timePeriod = timePeriod;
        this.portfolioValues = p.getInvestments();
        this.numberOfStocks = portfolioValues.size();
        stockPriceDataFiles = p.getStockPriceDataFiles();
        computeZDelta();
    }

    /**
     * Initialises a Model-Building VaR model using separate lists of assets and file.
     * @param portfolioValues the investments made for assets in the portfolio
     * @param confidence
     * @param timePeriod
     */
    public ModelBuilding( ArrayList<Double> portfolioValues,
                          ArrayList<File> stockPriceDataFiles, int confidence, int timePeriod )
    {
        this.portfolioValues = portfolioValues;
        this.stockPriceDataFiles = stockPriceDataFiles;
        this.confidence = confidence;
        this.timePeriod = timePeriod;
        this.numberOfStocks = portfolioValues.size();
        computeZDelta();
    }

    /**
     * Start computing the final VaR using this model.
     * @return an array containing just the final VaR computed from this model.
     */
    public double[] computeValueAtRisk()
    {
        double[] finalMaxVars = new double[2];
        finalMaxVars[0] = Math.round( computeForMultipleStocks() );
        return finalMaxVars;
    }
    
    /**
     * Method used to compute VaR for multiple stocks by calculating the variance of the portfolio.
     * @return VaR
     */
    public double computeForMultipleStocks()
    {
        ArrayList<double[]> returnList = new ArrayList<double[]>();

        for( File stockFile : stockPriceDataFiles )
        {
            double[] returnsFromFile = VarUtils.getReturnsFromFile( stockFile );
            returnList.add( returnsFromFile );
        }

        double portfolioVariance = getPortfolioVariance( returnList );

        // VaR = stdDevUpperBound * volatilityOfPortfolio *
        // Math.sqrt(numberOfDays);
        double VaR = zDelta * VarUtils.root( portfolioVariance )
                     * VarUtils.root( timePeriod );

        return VaR;
    }

    /**
     * Computes the variance of a portfolio of assets by generating a covariance matrix.
     * @param returnList a list of series of returns from stock file data
     * @return total variance of the portfolio.
     */
    private double getPortfolioVariance( ArrayList<double[]> returnList )
    {
        double[][] covarianceMatrix = VarUtils.generateCovarianceMatrix(
                                                                         returnList, numberOfStocks );

        double portfolioVariance = 0.0;

        for( int i = 0 ; i < covarianceMatrix.length ; i++ )
        {
            for( int j = 0 ; j < covarianceMatrix[0].length ; j++ )
            {
                // value_stock1 * value_stock2 * covariance_stock1stock2
                double x = portfolioValues.get( i ) * portfolioValues.get( j )
                           * covarianceMatrix[i][j];
                portfolioVariance += x;
            }
        }
        return portfolioVariance;
    }

    /**
     * Calculates VaR by:
     * <ol>
     * <li>computing standard deviation of daily changes in the value of the portfolio</li>
     * <li>computing one day VaR</li>
     * <li>computing VaR over requested time period.</li>
     * </ol>
     * 
     * @return The Value at Risk over the specified time period.
     */
    public double computeForOneStock()
    {
        double volatility = VarUtils.computeVolatility_Standard( VarUtils.getReturnsFromFile( stockPriceDataFiles.get( 0 ) ) );
        double VaR = Math.round( getVaR( volatility, portfolioValues.get( 0 ) ) );
        return VaR;
    }

    /**
     * For one stock in portfolio.
     * Use the returns until current day to estimate volatility and thus the VaR for next day.
     * @param returns
     * @param numberOfDaysToTest
     * @return
     */
    public double[] computeForBackTesting( double[] returns, int numberOfDaysToTest )
    {
        int numberOfReturnsToUse = returns.length - 1 - numberOfDaysToTest;
        double portfolioValue = portfolioValues.get( 0 );
        double[] estimations = new double[numberOfDaysToTest];
        // calculate one-day VaR for day+1 -> numberOfDaysToTest from returns to date
        for( int day = 0 ; day < numberOfDaysToTest ; day++ )
        {
            double[] returnsToUse = Arrays.copyOf( returns, numberOfReturnsToUse );
            double volatility = VarUtils.computeVolatility_GARCH( returnsToUse );
            estimations[day] = getVaR( volatility, portfolioValue );
            numberOfReturnsToUse++;
        }

        return estimations;
    }
    
    /**
     * Computes VaR using the volatility and value of the portfolio.
     * @param volatility of the portfolio
     * @param portfolioValue total value of the portfolio
     * @return VaR estimate using these parameters
     */
    private double getVaR( double volatility, double portfolioValue )
    {
        double VaR = 0.0;
        double stdDevDailyValueChange = volatility * portfolioValue;
        double oneDayVaR = zDelta * stdDevDailyValueChange;
        VaR = oneDayVaR * Math.sqrt( timePeriod );
        return VaR;
    }

    /**
     * Computes Value at Risk for two stocks using pre-defined volatilities and
     * covariances.
     * 
     * @param value1
     * @param value2
     * @param dailyVolatility1
     * @param dailyVolatility2
     * @param covariance
     * @return
     */
    public double computeForTwoStocks( double dailyVolatility1,
                                       double dailyVolatility2, double covariance )
    {
        double stdDevChange1 = portfolioValues.get( 0 ) * dailyVolatility1;
        double stdDevChange2 = portfolioValues.get( 1 ) * dailyVolatility2;

        double VaR = VarUtils.root( VarUtils.square( stdDevChange1 )
                                    + VarUtils.square( stdDevChange2 ) + 2 * covariance
                                    * stdDevChange1 * stdDevChange2 )
                     * zDelta * VarUtils.root( timePeriod );
        return VaR;
    }
    
    /**
     * Computes the zDelta for the confidence level provided.
     */
    private void computeZDelta()
    {
        // 0 is default initialisation value of a Java int
        if( confidence == 0 )
        {
            System.out.println( "Confidence level not set." );
            return;
        }

        switch( confidence )
        {
            case 99:
                zDelta = 2.33;
                break;
            case 98:
                zDelta = 2.05;
                break;
            case 97:
                zDelta = 1.88;
                break;
            case 96:
                zDelta = 1.75;
                break;
            case 95:
                zDelta = 1.65;
                break;
            case 90:
                zDelta = 1.29;
                break;
            case 85:
                zDelta = 1.04;
                break;
            case 80:
                zDelta = 0.84;
                break;
            case 75:
                zDelta = 0.68;
                break;
            default:
                System.out.println( "zDelta not available" );
                break;
        }

    }
   
}
