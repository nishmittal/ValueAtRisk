/**
 * 
 */
package com.nm.var.src;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.math3.linear.CholeskyDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.correlation.Covariance;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

/**
 * Utility class containing methods commonly used within the Value at Risk
 * program.
 * 
 */
public abstract class VarUtils
{
    /** Types of options. */
    public static final int     AMERICAN_PUT     = 0, AMERICAN_CALL = 1, EUROPEAN_PUT = 2,
                                                 EUROPEAN_CALL = 3;
    /** Types of option pricing models and their action commands used in the GUI. */
    public static final String  BS               = "BS", MC = "MC", BT = "BT";
    /** Types of VaR models and their action commands used in the GUI. */
    public static final String  MB               = "MB", HS = "HS";
    public static final int     DAYS_IN_YEAR     = 252, DAYS_IN_MONTH = 21;
    /** Error value used to indicate something went wrong. */
    public static final int     ERROR_VAL        = -1;
    /** Used to split the stock price data file contents into a array of strings. */
    private static final String COMMA            = ",";
    /** The decay factor in the EWMA algorithm. */
    private static double       lambda           = 0.94;
    private static double       firstDayVariance = 0.01;
    private static double       firstDayReturn   = 0.02;
    /**
     * Parameters for the GARCH(1,1) estimation of volatility.
     */
    static double               gamma            = 0.05;
    static double               alpha            = 0.13;
    static double               beta             = 0.90;

    // TODO best backtest results for 99% confidence
    // need to get MLE done for each set of returns to optimise parameters
    /*
     * static double gamma = 0.01;
     * static double alpha = 0.02;
     * static double beta = 0.97;
     */
    static double               omega            = 0.000002;

    /**
     * Helper method
     * @param num
     * @return the square of num
     */
    public static double square( double num )
    {
        return Math.pow( num, 2 );
    }

    /**
     * Helper method
     * @param num
     * @return the square root of num
     */
    public static double root( double num )
    {
        return Math.sqrt( num );
    }

    /**
     * Helper method
     * @param num
     * @return num rounded to nearest whole number
     */
    public static double round( double num )
    {
        return Math.round( num );
    }

    /**
     * Uses {@code getReturnsFromFile(File file)} to iteratively extract files from a list and
     * generate a list of returns for respective files.
     * 
     * @param files List of csv files to parse for returns.
     * @return List of arrays containing returns for each input file.
     */
    public static ArrayList<double[]> getReturnsFromFiles( ArrayList<File> files )
    {
        ArrayList<double[]> returns = new ArrayList<double[]>();
        for( File stockData : files )
        {
            returns.add( getReturnsFromFile( stockData ) );
        }

        return returns;
    }

    /**
     * Reads through a csv file to extract the closing prices from each day of data.
     * 
     * @param file a csv file with historical price data.
     * @return corresponding returns in an array
     */
    public static double[] getReturnsFromFile( File file )
    {
        ArrayList<Double> closePrices = getClosingPrices( file );

        double[] returns = computeDailyReturns( closePrices );

        return returns;
    }
    
    /**
     * Saves the series of closing prices from the historical stock data into a list of prices.
     * @param file historical stock data
     * @return list of closing prices
     */
    public static ArrayList<Double> getClosingPrices( File file )
    {
        ArrayList<Double> closePrices = new ArrayList<Double>();
        try
        {
            BufferedReader reader = new BufferedReader( new FileReader( file ) );
            String nextLine;
            String comma = COMMA;
            int counter = 0; // skip storing column heading
            int closePriceColumn = 0;
            while( ( nextLine = reader.readLine() ) != null )
            {
                if( counter == 0 )
                {
                    String[] line = nextLine.split( comma );
                    for( int i = 0 ; i < line.length ; i++ )
                    {
                        if( line[i].contains( "Close" ) )
                        {
                            closePriceColumn = i;
                            break;
                        }
                    }
                }
                else
                {
                    String[] line = nextLine.split( comma );
                    closePrices.add( Double.parseDouble( line[closePriceColumn] ) );
                }
                counter++;
            }
            reader.close();
        }
        catch( IOException e )
        {
            System.out.println( "File" + file.getName() + " has a problem." );
        }
        return closePrices;
    }

    /**
     * Computes the daily returns from a list of closing prices using natural logs.
     * 
     * @param prices the series of closing prices from the stock data.
     * @return an array of returns
     */
    private static double[] computeDailyReturns( ArrayList<Double> prices )
    {
        int daysToCompute = prices.size() - 1;
        double[] returns = new double[daysToCompute];

        for( int i = 0 ; i < daysToCompute ; i++ )
        {
            // can't compute returns for last day in range
            if( ! ( i + 1 > daysToCompute ) )
            {
                double day2 = prices.get( i );
                double day1 = prices.get( i + 1 );
                returns[i] = Math.log( day2 / day1 );
            }
        }
        return returns;
    }

    /**
     * <ol>
     * <li>make new list with daily differences in stock price calculated using ln(Di/Di-1)</li>
     * <li>apply standard deviation formula to the daily differences to get daily volatility</li>
     * </ol>
     * 
     * @return
     */
    public static double computeVolatility_Standard( double[] returns )
    {
        DescriptiveStatistics stats = new DescriptiveStatistics();
        for( int i = 0 ; i < returns.length ; i++ )
        {
            stats.addValue( (double) returns[i] );
        }
        double dailyVolatility = stats.getStandardDeviation();
        return dailyVolatility;
    }
    
    /**
     * Computes volatilities for each set of returns passed
     * @param returnsForMultipleStocks
     * @return list of volatilities corresponding to the list of returns passed
     */
    public static ArrayList<Double> computeVolatilitiesFromReturns(
                                                                    ArrayList<double[]> returnsForMultipleStocks )
    {
        ArrayList<Double> volatilities = new ArrayList<Double>();
        for( double[] returns : returnsForMultipleStocks )
        {
            volatilities.add( computeVolatility_EWMA( returns ) );
        }
        return volatilities;
    }

    /**
     * Estimates the volatility of the stock using the closing prices provided.
     * EWMA works by placing higher weight on more recent data and maintaining a
     * running average of the variance of the data which can be used to
     * calculate the volatility of the stock.
     * 
     * @return estimated volatility of the stock as per the EWMA recursive
     *         calculation.
     */
    public static double computeVolatility_EWMA( double[] returns )
    {
        double varianceCurrentDay = getVariance_EWMA( 0, returns );
        double ewma = ( lambda * varianceCurrentDay )
                      + ( ( 1 - lambda ) * Math.pow( returns[0], 2 ) );
        double volatility = Math.sqrt( ewma );
        return volatility;
    }

    /**
     * Recursively computes the running variance using the EWMA formula.<br>
     * today's variance = yesterdaysVariance^2 * lambda + yesterdaysReturn^2 * (
     * 1 - lambda )
     * 
     * @param currentDay
     *            the day we are estimating the variance for.
     * @param returns
     * @return variance for the current day, based on yesterday's variance and
     *         yesterday's return
     */
    private static double getVariance_EWMA( int currentDay, double[] returns )
    {
        // day 0 is most recent day
        double variance = 0.0;
        // first day variance = lambda * firstDayVariance^2 + yesterdayReturn^2
        // * ( 1 - lambda )
        if( currentDay == returns.length - 1 )
        {
            double firstDayReturnSquared = Math.pow( firstDayReturn, 2 );
            double weight = ( 1 - lambda ) * Math.pow( lambda, currentDay );
            variance = ( lambda * firstDayVariance )
                       + ( weight * firstDayReturnSquared );
        }
        // today's variance = yesterdaysVariance^2 * lambda + yesterdaysReturn^2
        // * ( 1 - lambda )
        else if( currentDay >= 0 )
        {
            // recursively work out variance for prior days
            double yesterdayVariance = getVariance_EWMA( currentDay + 1, returns );
            double yesterdayReturnSquared = Math
                                                .pow( returns[currentDay + 1], 2 );
            double weight = ( 1 - lambda ) * Math.pow( lambda, currentDay );
            variance = ( lambda * yesterdayVariance )
                       + ( weight * yesterdayReturnSquared );
        }

        return variance;
    }

    @SuppressWarnings("unused")
    private double getCovariance_EWMA( int currentDay,
                                       ArrayList<Double> returns1, ArrayList<Double> returns2 )
    {
        ArrayList<Double> covariances_EWMA = new ArrayList<Double>();
        double covariance = 0;

        if( currentDay == returns1.size() - 1 )
        {
            double firstDayReturnsProduct = Math.pow( firstDayReturn, 2 );
            double weight = ( 1 - lambda ) * Math.pow( lambda, currentDay );
            covariance = ( lambda * firstDayVariance )
                         + ( weight * firstDayReturnsProduct );
            covariances_EWMA.set( currentDay, covariance );
        }
        else if( currentDay >= 0 )
        {
            double yesterdayCovariance = getCovariance_EWMA( currentDay + 1,
                                                             returns1, returns2 );
            double yesterdayReturnsProduct = returns1.get( currentDay + 1 )
                                             * returns2.get( currentDay + 1 );
            double weight = ( 1 - lambda ) * Math.pow( lambda, currentDay );
            covariance = ( lambda * yesterdayCovariance )
                         + ( weight * yesterdayReturnsProduct );
        }

        return covariance;
    }

    /**
     * Most popular method of estimating volatility. Limits influence of larger
     * fluctuations which could affect mean significantly. Results in estimates
     * which are closer to long-run variance.
     * 
     * @return estimated volatility of the stock.
     */
    public static double computeVolatility_GARCH( double[] returns )
    {
        /**
         * Accepted values for the weights of long run variance, previous return
         * and variance.
         */
        double longRunVariance = 0.0;
        DescriptiveStatistics stats = new DescriptiveStatistics();
        for( int i = 0 ; i < returns.length ; i++ )
        {
            stats.addValue( (double) returns[i] );
        }
        longRunVariance = stats.getVariance();
        // tune parameters for returns
        double variance = getVariance_GARCH( 0, longRunVariance, returns );
        // double variance = getVariance_GARCH2( 0, returns );
        double volatility = Math.sqrt( variance );
        return volatility;
    }

    // TODO using finmath to get optimal params
    @SuppressWarnings("unused")
    private static double getVariance_GARCH2( int day, double[] returns )
    {
        // values from Estimating Volatilities and Correlations, John Hull book.
        double variance = 0.0;
        if( day == returns.length - 1 )
        {
            double firstDayReturnSquared = Math.pow( firstDayReturn, 2 );
            variance = omega + ( alpha * firstDayReturnSquared )
                       + ( beta * firstDayVariance );
        }
        // variance = weightLRV*longRunVariance + weightPR*previousReturn +
        // weightVariance*previousVariance
        else if( day >= 0 )
        {
            // recursively work out variance for prior days
            double yesterdayVariance = getVariance_GARCH2( day + 1,
                                                           returns );
            double yesterdayReturnSquared = Math.pow( returns[day + 1], 2 );
            variance = omega + ( alpha * yesterdayReturnSquared )
                       + ( beta * yesterdayVariance );
        }
        return variance;
    }

    // TODO long run average variance, at each variance calculation, calculate average to day and
    // use that in subsequent calculations
    /**
     * Recursively computes the variance using the GARCH formula for daily
     * returns.<br>
     * variance = weightLRV*longRunVariance + weightPR*previousReturn +
     * weightVariance*previousVariance
     * 
     * @param day
     *            the day we are estimating the variance for
     * @param longRunVariance
     *            the overall variance of the whole series of returns.
     * @param returns
     * @return current day's variance.
     */
    private static double getVariance_GARCH( int day, double longRunVariance,
                                             double[] returns )
    {
        // values from Estimating Volatilities and Correlations, John Hull book.
        double variance = 0.0;
        if( day == returns.length - 1 )
        {
            double firstDayReturnSquared = Math.pow( firstDayReturn, 2 );
            variance = gamma * longRunVariance + alpha * firstDayReturnSquared
                       + beta * firstDayVariance;
        }
        // variance = weightLRV*longRunVariance + weightPR*previousReturn +
        // weightVariance*previousVariance
        else if( day >= 0 )
        {
            // recursively work out variance for prior days
            double yesterdayVariance = getVariance_GARCH( day + 1,
                                                          longRunVariance, returns );
            double yesterdayReturnSquared = Math.pow( returns[day + 1], 2 );
            variance = gamma * longRunVariance + alpha * yesterdayReturnSquared
                       + beta * yesterdayVariance;
        }
        return variance;
    }

    @SuppressWarnings("unused")
    private double getCovariance_GARCH( int day, double longRunVariance,
                                        ArrayList<Double> returns1, ArrayList<Double> returns2 )
    {
        // values from Estimating Volatilities and Correlations, John Hull book.
        double covariance = 0.0;
        if( day == returns1.size() - 1 )
        {
            double firstDayReturnProduct = Math.pow( firstDayReturn, 2 );
            covariance = gamma * longRunVariance + alpha
                         * firstDayReturnProduct + beta * firstDayVariance;
        }
        // variance = weightLRV*longRunVariance + weightPR*previousReturn +
        // weightVariance*previousVariance
        else if( day >= 0 )
        {
            // recursively work out variance for prior days
            double yesterdayCovariance = getCovariance_GARCH( day + 1,
                                                              longRunVariance, returns1, returns2 );
            double yesterdayReturnProduct = returns1.get( day + 1 )
                                            * returns2.get( day + 1 );
            covariance = gamma * longRunVariance + alpha
                         * yesterdayReturnProduct + beta * yesterdayCovariance;
        }
        return covariance;
    }

    /**
     * matrix of variances where the [x][y] location is the covariance between
     * stock x and stock y
     * 
     * @param returnList
     * @return
     */
    public static double[][] generateCovarianceMatrix(
                                                       ArrayList<double[]> returnList,
                                                       int numberOfStocks )
    {
        double[][] v = new double[numberOfStocks][numberOfStocks];

        for( int x = 0 ; x < v.length ; x++ )
        {
            for( int y = 0 ; y < v[0].length ; y++ )
            {
                if( x == y )
                {
                    v[x][y] = getCovariance( returnList.get( x ),
                                             returnList.get( x ) );
                }
                else
                {
                    v[x][y] = getCovariance( returnList.get( x ),
                                             returnList.get( y ) );
                }
            }
        }

        return v;
    }

    /**
     * Computes the covariance of two series of returns.
     * 
     * @param stock1Returns returns from stock
     * @param stock2Returns returns from stock
     * @return covariance of the two series of data
     */
    public static double getCovariance( double[] stock1Returns,
                                        double[] stock2Returns )
    {
        Covariance cov = new Covariance();
        double covariance = 0;

        // array lengths must match for covariance formula to work.
        int firstLength = stock1Returns.length;
        int secondLength = stock2Returns.length;

        if( firstLength == secondLength )
        {
            covariance = cov.covariance( stock1Returns, stock2Returns );
        }
        else
        {
            if( firstLength > secondLength )
            {
                double[] trimmedStock1Returns = Arrays.copyOf( stock1Returns,
                                                               secondLength );
                covariance = cov
                                .covariance( trimmedStock1Returns, stock2Returns );
            }
            else
            {
                double[] trimmedStock2Returns = Arrays.copyOf( stock2Returns,
                                                               firstLength );
                covariance = cov
                                .covariance( stock1Returns, trimmedStock2Returns );
            }
        }
        return covariance;
    }

    /**
     * Uses CholeskyDecomposition class from Apache Commons Math library to decompose a
     * positive-definite matrix into a triangular matrix.
     * 
     * @param originalMatrix Positive-Definite matrix of covariances.
     * @return Triangular decomposed matrix
     */
    public static double[][] decomposeMatrix( double[][] originalMatrix )
    {
        CholeskyDecomposition cd = new CholeskyDecomposition(
                                                              MatrixUtils.createRealMatrix( originalMatrix ) );
        RealMatrix decomposed = cd.getL();
        double[][] data = decomposed.getData();
        // check which one needed, lower/upper triangular?

        return data;
    }

    /**
     * This method works out the covariance of two series of data manually.
     * Covariance of two identical series of data will be the variance of such data.
     * 
     * @author Nishant
     * @param r1 Series of returns
     * @param r2 Series of returns
     * @return The covariance of the two series of data.
     */
    public static double getCovarianceManually( double[] r1, double[] r2 )
    {
        double covariance = 0.0;

        int firstLength = r1.length;
        int secondLength = r2.length;

        if( firstLength != secondLength )
        {
            if( firstLength > secondLength )
            {
                r1 = Arrays.copyOf( r1, secondLength );
            }
            else
            {
                r2 = Arrays.copyOf( r2, firstLength );
            }
        }
        double avg1 = StatUtils.mean( r1 );
        double avg2 = StatUtils.mean( r2 );
        int sampleSize = r1.length;
        double[] differences = new double[sampleSize];

        for( int i = 0 ; i < sampleSize ; i++ )
        {
            differences[i] = ( ( r1[i] - avg1 ) * ( r2[i] - avg2 ) );
        }

        double sumOfDifferences = StatUtils.sum( differences );
        covariance = sumOfDifferences / ( sampleSize - 1 );

        return covariance;
    }

    /**
     * Gets the percentile for the confidence level from the series of data provided.
     * 
     * @param data Series of data.
     * @param confidence The confidence level for which to get the percentile.
     * @return value at the desired percentile from the data
     */
    public static double getPercentile( double[] data, int confidence )
    {
        double percentile = StatUtils.percentile( data, 100 - confidence );
        return percentile;
    }

    /**
     * Return Call/Put flag, as defined in BlackScholes.java if type is a European Option.
     * Otherwise returns ERROR_VAL = -1.
     * 
     * @see BlackScholes
     * @param type The type of the Option.
     */
    public static int convertToCallOrPut( int type )
    {
        switch( type )
        {
            case EUROPEAN_CALL:
                return BlackScholes.CALL;
            case EUROPEAN_PUT:
                return BlackScholes.PUT;
            default:
                return ERROR_VAL;
        }
    }
    
    /**
     * @param values
     * @return sum of numbers contained in the list of values provided
     */
    public static double sumOf( ArrayList<Double> values )
    {
        double sum = 0.0;

        for( Double value : values )
        {
            sum += value;
        }

        return sum;
    }
    
    /**
     * Computes the 'sliding-window' of size varHorizon to compute n-day returns over the varHorizon
     * @param returns
     * @param varHorizon
     * @return array of returns covering the var horizon
     */
    public static double[] getReturnsOverVarHorizon( double[] returns, int varHorizon )
    {
        int numberOfReturns = returns.length - varHorizon + 1;
        double[] nDayHorizonReturns = new double[numberOfReturns];
        for( int i = 0 ; i < numberOfReturns ; i++ )
        {
            ArrayList<Double> windowReturns = new ArrayList<Double>();
            // assuming return over n days = sum of returns on each day from start to start +
            // horizon
            for( int day = i ; day < i + varHorizon ; day++ )
            {
                windowReturns.add( returns[day] );
            }
            nDayHorizonReturns[i] = VarUtils.sumOf( windowReturns );
        }

        return nDayHorizonReturns;
    }

    /**
     * Cumulative Normal Distribution Function <br>
     * Taken from CS3930 Moodle Website
     * 
     * @see <a href="http://moodle.rhul.ac.uk/mod/resource/view.php?id=123643">CNDF Function</a>
     * @param x
     *            The number for which to compute the value
     * @return
     */
    public static double CNDF( double x )
    {
        int neg = ( x < 0d ) ? 1 : 0;
        if( neg == 1 )
            x *= -1d;

        double k = ( 1d / ( 1d + 0.2316419 * x ) );
        double y = ( ( ( ( 1.330274429 * k - 1.821255978 ) * k + 1.781477937 ) * k - 0.356563782 )
                     * k + 0.319381530 )
                   * k;
        y = 1.0 - 0.398942280401 * Math.exp( -0.5 * x * x ) * y;

        return ( 1d - neg ) * y + neg * ( 1d - y );
    }

    public static double roundTwoDP( double n )
    {
        double rounded = Math.round( n * 100 );
        rounded = rounded / 100;
        return rounded;
    }

}
