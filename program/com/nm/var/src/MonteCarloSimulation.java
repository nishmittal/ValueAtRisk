/**
 * 
 */
package com.nm.var.src;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.StatUtils;

/**
 * Implementation of the Monte Carlo Simulation model for computing VaR and pricing options.
 */
public class MonteCarloSimulation
{
    /** Final VaR computed using this model. */
    private double            monteCarloFinalVar;
    /** Maximum VaR experienced during the simulation. */
    private double            monteCarloMaximumVar;
    /**
     * Random number generator using to generate Gaussian (Normal) and regularly distributed random
     * numbers for simulating prices.
     */
    private Random            rng                 = new Random();
    /** List of investments in assets. */
    private ArrayList<Double> portfolioValues;
    /** List of historical stock price data, in same order as investments. */
    private ArrayList<File>   stockPriceDataFiles;
    /** Number of assets in the portfolio. */
    private int               numberOfStocks;
    /** The confidence level to compute VaR at. */
    private int               confidence;
    /** Number of days to simulate prices over and compute VaR for. */
    private int               timePeriod          = 10;
    /** Number of simulations of stock prices, for Monte Carlo methods. */
    private int               numberOfSimulations = 1000;
    /** The portfolio to compute VaR for. */
    private Portfolio         portfolio;
    /** The option pricing model selected by the user. */
    private String            optionPricingType;

    /**
     * Initialises a Monte Carlo simulation model using just a confidence level and a time period.
     * Assets and options can be added later.
     * 
     * @param confidence
     * @param timePeriod
     */
    public MonteCarloSimulation( int confidence, int timePeriod )
    {
        this.confidence = confidence;
        this.timePeriod = timePeriod;
    }

    /**
     * Initialises a Monte Carlo simulation model using some list of investments, historical price
     * data files, confidence level and a time period.
     * 
     * @param portfolioValues
     * @param stockPriceDataFiles
     * @param confidence
     * @param timePeriod
     */
    public MonteCarloSimulation( ArrayList<Double> portfolioValues,
                                 ArrayList<File> stockPriceDataFiles, int confidence, int timePeriod )
    {
        this.portfolioValues = portfolioValues;
        this.stockPriceDataFiles = stockPriceDataFiles;
        this.numberOfStocks = this.portfolioValues.size();
        this.confidence = confidence;
        this.timePeriod = timePeriod;
    }

    /**
     * Initialises a Monte Carlo simulation model using a portfolio, confidence and time period for
     * simulation.
     * 
     * @param portfolio
     * @param confidence
     * @param timePeriod
     */
    public MonteCarloSimulation( Portfolio portfolio, int confidence, int timePeriod )
    {
        this.stockPriceDataFiles = portfolio.getStockPriceDataFiles();
        this.portfolio = portfolio;
        this.portfolioValues = portfolio.getInvestments();
        this.confidence = confidence;
        this.numberOfStocks = portfolioValues.size();
        this.timePeriod = timePeriod;
    }

    /**
     * Empty constructor for testing.
     */
    public MonteCarloSimulation()
    {
    }

    /**
     * Computes VaR for portfolio using Monte Carlo simulation and user-defined option pricing
     * types.
     * 
     * @return an array containing final and max VaRs.
     */
    public double[] computeForPortfolio()
    {
        double initialPortFolioValue = 0.0;
        double finalPortfolioValue = 0.0;
        // take initial values of investments and options
        double initialOptionsValue = 0.0;

        ArrayList<Option> options = portfolio.getOptions();

        for( Option option : options )
        {
            initialOptionsValue += ( option.getInitialStockPrice() * option.getNumShares() );
        }

        initialPortFolioValue = VarUtils.sumOf( portfolio.getInvestments() ) + initialOptionsValue;

        double[] finalMinStockValues = computeForMultipleStocks( this.portfolioValues );

        double optionsFinalValue = 0.0, optionsMinValue = 0.0;
        for( Option o : options )
        {
            double[] finalMinPrices = new double[2];
            switch( optionPricingType )
            {
                case VarUtils.BS:
                    int type = VarUtils.convertToCallOrPut( o.getOptionType() );
                    if( type == VarUtils.ERROR_VAL )
                    {
                        break;
                    }
                    finalMinPrices = priceOptionUsingBlackScholes( o );
                    optionsFinalValue += finalMinPrices[0];
                    optionsMinValue += finalMinPrices[1];
                    break;
                case VarUtils.BT:
                    finalMinPrices = priceOptionUsingBinomialTree( o );
                    optionsFinalValue += finalMinPrices[0];
                    optionsMinValue += finalMinPrices[1];
                    break;
                case VarUtils.MC:
                    finalMinPrices = priceOptionUsingMonteCarlo( o );
                    optionsFinalValue += finalMinPrices[0];
                    optionsMinValue += finalMinPrices[1];
                    break;
            }
        }

        finalPortfolioValue = finalMinStockValues[0] + optionsFinalValue;
        double minPortfolioValue = finalMinStockValues[1] + optionsMinValue;
        double finalVaR = initialPortFolioValue - finalPortfolioValue;
        double maxVaR = initialPortFolioValue - minPortfolioValue;
        double[] finalMaxVaR = { Math.round( finalVaR ), Math.round( maxVaR ) };
        return finalMaxVaR;
    }

    /**
     * Computes value at risk for either one asset or multiple assets depending on the number of
     * assets in the portfolio.
     */
    public void computeValueAtRisk()
    {
        if( numberOfStocks == 1 )
        {
            double[] returnsFromFile = VarUtils
                                               .getReturnsFromFile( stockPriceDataFiles.get( 0 ) );
            double volatility = VarUtils
                                        .computeVolatility_EWMA( returnsFromFile );
            computeForOneStock( portfolioValues.get( 0 ), volatility );
        }
        else
        {
            computeForMultipleStocks( portfolioValues );
        }
    }

    /**
     * Computes the VaR for one stock using its value and volatility to run the Monte Carlo
     * simulation.
     * 
     * @param stockValue
     * @param volatility
     * @return array containing final and max vars
     */
    public double[] computeForOneStock( double stockValue, double volatility )
    {
        double[][] stockValues = simulatePrices( stockValue, volatility );

        double[] finalValues = new double[numberOfSimulations];
        double[] maximumLosses = new double[numberOfSimulations];
        for( int sim = 0 ; sim < numberOfSimulations ; sim++ )
        {
            // store stock price from last day of each simulation
            finalValues[sim] = stockValues[sim][timePeriod - 1];
            // sort the stock prices of each simulation for maximal VaR
            // calculation
            Arrays.sort( stockValues[sim] );
            maximumLosses[sim] = stockValues[sim][0];
        }

        // sort final losses for final VaR calculation
        Arrays.sort( finalValues );

        // VaR computation using final stock values
        double stockValueAtRequiredPercentile = VarUtils.getPercentile(
                                                                        finalValues, confidence );
        double finalVaR = stockValue - stockValueAtRequiredPercentile;
        /*
         * System.out.println( "Monte Carlo VaR simulated with "
         * + numberOfSimulations + " simulations of " + timePeriod
         * + " days each." );
         * System.out.println( "Monte Carlo VaR (1 stock - Final): "
         * + VarUtils.round( finalVaR ) );
         */
        this.monteCarloFinalVar = finalVaR;

        // maximum VaR during stock price path simulation
        Arrays.sort( maximumLosses );
        double maximumVaR = stockValue - maximumLosses[0];
        double[] estimations = { finalVaR, maximumVaR };
        /*
         * System.out.println( "Monte Carlo VaR (1 stock - Maximum): "
         * + VarUtils.round( maximumVaR ) );
         */
        this.monteCarloMaximumVar = maximumVaR;

        return estimations;
    }

    /**
     * Computes VaR for multiple stocks using some investments provided and getting the data files
     * from the constructors.
     * 
     * @param stockValues the investments made in the assets, in same order as the stock data files.
     * @return final and minimal values simulated for the portfolio to compute VaR from.
     */
    public double[] computeForMultipleStocks( ArrayList<Double> stockValues )
    {
        ArrayList<double[]> returnList = new ArrayList<double[]>();

        for( File stockFile : stockPriceDataFiles )
        {
            double[] returnsFromFile = VarUtils.getReturnsFromFile( stockFile );
            returnList.add( returnsFromFile );
        }
        double[][] covarianceMatrix = VarUtils.generateCovarianceMatrix(
                                                                         returnList, numberOfStocks );
        double[][] decomposedMatrix = VarUtils
                                              .decomposeMatrix( covarianceMatrix );

        double[][] finalDayPrices = new double[numberOfStocks][numberOfSimulations];
        double[][] maximumLosses = new double[numberOfStocks][numberOfSimulations];
        int iteration = 0;
        while( iteration < numberOfSimulations )
        {
            // need to do this 1000 times, and then record the final prices and
            // lowest prices (highest VaR)
            ArrayList<double[]> simulatedReturns = simulateReturns();

            double[] finalDayReturns = simulatedReturns.get( 0 );
            double[] minReturns = simulatedReturns.get( 1 );

            RealMatrix L = MatrixUtils.createRealMatrix( decomposedMatrix );

            double[] correlatedFinalReturns = L.operate( finalDayReturns );
            double[] correlatedMinReturns = L.operate( minReturns );

            for( int stock = 0 ; stock < numberOfStocks ; stock++ )
            {
                // price = e^(return) * stockValue
                finalDayPrices[stock][iteration] = Math
                                                       .exp( correlatedFinalReturns[stock] )
                                                   * stockValues.get( stock );
                maximumLosses[stock][iteration] = Math
                                                      .exp( correlatedMinReturns[stock] )
                                                  * stockValues.get( stock );
            }
            iteration++;
        }

        double[] portfolioFinalSimulatedValues = new double[numberOfSimulations];
        double[] portfolioMinSimulatedValues = new double[numberOfSimulations];

        for( int sim = 0 ; sim < numberOfSimulations ; sim++ )
        {
            double sumOfFinalStockValues = 0.0, sumOfMinStockValues = 0.0;
            for( int stock = 0 ; stock < numberOfStocks ; stock++ )
            {
                sumOfFinalStockValues += finalDayPrices[stock][sim];
                sumOfMinStockValues += maximumLosses[stock][sim];
            }
            portfolioFinalSimulatedValues[sim] = sumOfFinalStockValues;
            portfolioMinSimulatedValues[sim] = sumOfMinStockValues;
        }

        Arrays.sort( portfolioFinalSimulatedValues );

        double valueAtPercentile = VarUtils.getPercentile(
                                                           portfolioFinalSimulatedValues,
                                                           confidence );

        double portfolioValue = 0.0;

        for( double stockValue : stockValues )
        {
            portfolioValue += stockValue;
        }

        double finalVaR = portfolioValue - valueAtPercentile;

        this.monteCarloFinalVar = finalVaR;

        Arrays.sort( portfolioMinSimulatedValues );
        double maximumVaR = portfolioValue - portfolioMinSimulatedValues[0];
//        System.out.println( "Monte Carlo VaR (Portfolio - Maximum): "
//                            + VarUtils.round( maximumVaR ) );
        this.monteCarloMaximumVar = maximumVaR;

        double[] finalMinValues = { valueAtPercentile, portfolioFinalSimulatedValues[0] };

        return finalMinValues;

    }

    /**
     * Uses the Black-Scholes option pricing model alongside the Monte Carlo simulation model to
     * price an option.
     * 
     * @param option
     * @return final and minimum simulated option values for VaR computation.
     */
    public double[] priceOptionUsingBlackScholes( Option option )
    {
        double[] values = new double[2];
        double interest = option.getInterest(), strike = option.getStrike(), dailyVolatility = option
                                                                                                     .getDailyVolatility();
        double initialStockPrice = option.getInitialStockPrice();
        int timeToMaturity = option.getTimeToMaturity();
        int numShares = 1, numOptions = 1;
        int flag = VarUtils.convertToCallOrPut( option.getOptionType() );
        if( flag == VarUtils.ERROR_VAL )
        {
            return ( values );
        }

        BlackScholes bs = new BlackScholes();
        double[] finalDayPrices = new double[numberOfSimulations];
        double[] minPrices = new double[numberOfSimulations];
        double[][] optionPrices = new double[numberOfSimulations][timePeriod];

        for( int sim = 0 ; sim < numberOfSimulations ; sim++ )
        {
            double stockPrice = initialStockPrice;

            for( int day = 0 ; day < timePeriod ; day++ )
            {
                double optionPrice = 0;
                stockPrice = numShares
                             * ( stockPrice + ( dailyVolatility * stockPrice * rng
                                                                                  .nextGaussian() ) );
                optionPrice = numOptions
                              * bs.compute( flag, stockPrice, strike, timeToMaturity
                                                                      - day, interest,
                                            dailyVolatility );
                // TODO check time to maturity decreased by 1 every run
                optionPrices[sim][day] = optionPrice;
            }

            double[] simPrices = Arrays.copyOf( optionPrices[sim],
                                                optionPrices[sim].length );
            finalDayPrices[sim] = simPrices[timePeriod - 1];
            Arrays.sort( simPrices );
            minPrices[sim] = simPrices[0];
        }

        Arrays.sort( minPrices );
        int discountPeriod = option.getTimeToMaturity() - timePeriod;
        if( discountPeriod < 1 )
        {
            // for safety
            discountPeriod = option.getTimeToMaturity();
        }
        double meanFinalDayValue = StatUtils.mean( finalDayPrices );
        double meanMinValue = StatUtils.mean( minPrices );
        double discountedFinalValue = getDiscountedValue( meanFinalDayValue,
                                                          option.getInitialStockPrice(),
                                                          discountPeriod );
        double discountedMinValue = getDiscountedValue( meanMinValue, option.getInterest(),
                                                        discountPeriod );
        // double VaR = initialOptionPrice - finalDayValue;
        // double maxVaR = initialOptionPrice - minPrices[0];
        values[0] = discountedFinalValue;
        values[1] = discountedMinValue;
        return values;
    }

    /**
     * Uses the Monte-Carlo option pricing model alongside the Monte Carlo simulation model to price
     * an option.
     * 
     * @param option
     * @return final and minimum simulated option values for VaR computation.
     */
    public double[] priceOptionUsingMonteCarlo( Option option )
    {
        double[] finalMinValues = new double[2];
        // generate large number of random possible price paths
        double[][] prices = simulatePrices( option.getInitialStockPrice(),
                                            option.getDailyVolatility() );
        // calculate exercise value/payoff of option for each path
        // intrinsic value - Call option - Max[Sn-X, 0], Put option - Max[X-Sn,
        // 0]
        double[] exerciseValues = new double[numberOfSimulations];
        double currentPrice = 0.0;
        double exerciseValue = 0.0;
        for( int i = 0 ; i < numberOfSimulations ; i++ )
        {
            currentPrice = prices[i][timePeriod - 1];
            // TODO DOES THIS WORK FOR AMERICAN AND EUROPEAN OPTIONS?!
            if( VarUtils.convertToCallOrPut( option.getOptionType() ) == BlackScholes.CALL )
            {
                exerciseValue = Math
                                    .max( ( currentPrice - option.getStrike() ), 0 );
            }
            else
            {
                exerciseValue = Math
                                    .max( ( option.getStrike() - currentPrice ), 0 );
            }
            exerciseValues[i] = exerciseValue;
        }

        // take average of the payoffs
        double meanExerciseValue = StatUtils.mean( exerciseValues );

        int discountPeriod = option.getTimeToMaturity() - timePeriod;
        if( discountPeriod < 1 )
        {
            // for safety
            discountPeriod = option.getTimeToMaturity();
        }
        double finalValueOfOption = getDiscountedValue( meanExerciseValue, option.getInterest(),
                                                        option.getTimeToMaturity() - timePeriod );

        Arrays.sort( exerciseValues );

        double minExerciseValue = exerciseValues[0];

        double minValueOfOption = getDiscountedValue( minExerciseValue, option.getInterest(),
                                                      option.getTimeToMaturity() - timePeriod );

        finalMinValues[0] = finalValueOfOption;
        finalMinValues[1] = minValueOfOption;
        return finalMinValues;
    }

    /**
     * computes the discounted value of an option in relation to the following variables
     * 
     * @param meanValue avg exercise value of option
     * @param interest
     * @param timeToMaturity
     * @return discounted value
     */
    private double getDiscountedValue( double meanValue, double interest, double timeToMaturity )
    {
        // discount average to today = value of the option
        // PV = C / (1+interest)^numberofperiodsofinterest
        // e.g. 1000 in 5 years at 10%, PV = 1000 / (1+0.10)^5
        // if in days, convert to years
        timeToMaturity = timeToMaturity / VarUtils.DAYS_IN_YEAR;
        double denominator = Math.pow( ( 1 + interest ), timeToMaturity );
        double valueOfOption = meanValue / denominator;
        return valueOfOption;
    }

    /**
     * Uses the Binomial Tree option pricing model alongside the Monte Carlo simulation model to
     * price an option.
     * 
     * @param option
     * @return final and minimum simulated option values for VaR computation.
     */
    public double[] priceOptionUsingBinomialTree( Option option )
    {
        double[][] stockPrices = simulatePrices( option.getInitialStockPrice(),
                                                 option.getDailyVolatility() );
        double[][] optionPrices = new double[numberOfSimulations][timePeriod];
        double[] finalDayOptionPrices = new double[numberOfSimulations];
        double[] minOptionPrices = new double[numberOfSimulations];
        double[] finalMinPrices = new double[2];
        BinomialTree bt;
        for( int simulation = 0 ; simulation < numberOfSimulations ; simulation++ )
        {
            for( int day = 0 ; day < timePeriod ; day++ )
            {
                bt = new BinomialTree( stockPrices[simulation][day], option.getStrike(),
                                       option.getTimeToMaturity() - day,
                                       option.getDailyVolatility(), option.getInterest(),
                                       option.getOptionType() );
                // discount option price to today
                optionPrices[simulation][day] = getDiscountedValue( bt.getOptionPrice(),
                                                                    option.getInterest(),
                                                                    option.getTimeToMaturity()
                                                                            - day );
                bt = null;
            }
            finalDayOptionPrices[simulation] = optionPrices[simulation][timePeriod];
            Arrays.sort( optionPrices[simulation] );
            minOptionPrices[simulation] = optionPrices[simulation][0];
        }

        // get average of option prices
        double meanFinalPrice = StatUtils.mean( finalDayOptionPrices );
        double meanMinPrice = StatUtils.mean( minOptionPrices );

        int discountPeriod = option.getTimeToMaturity() - timePeriod;
        if( discountPeriod < 1 )
        {
            // for safety
            discountPeriod = option.getTimeToMaturity();
        }

        double discountedFinalValue = getDiscountedValue( meanFinalPrice, option.getInterest(),
                                                          discountPeriod );
        double discountedMinValue = getDiscountedValue( meanMinPrice, option.getInterest(),
                                                        discountPeriod );

        finalMinPrices[0] = discountedFinalValue;
        finalMinPrices[1] = discountedMinValue;

        return finalMinPrices;
    }

    /**
     * Simulates normally distributed returns for each asset over the specified time period using
     * the Monte Carlo simulation model.
     * 
     * @return list of returns containing simulated returns for each asset in the portfolio.
     */
    public ArrayList<double[]> simulateReturns()
    {
        double[][] simulatedReturns = new double[numberOfStocks][timePeriod];
        double[] minReturns = new double[numberOfStocks];
        double[] finalDayReturns = new double[numberOfStocks];
        ArrayList<double[]> minAndFinalReturns = new ArrayList<double[]>();

        // simulate returns
        for( int stock = 0 ; stock < numberOfStocks ; stock++ )
        {
            for( int day = 0 ; day < timePeriod ; day++ )
            {
                simulatedReturns[stock][day] = rng.nextGaussian();
            }
        }

        // record minimum and final day returns for each stock
        for( int i = 0 ; i < numberOfStocks ; i++ )
        {
            finalDayReturns[i] = simulatedReturns[i][timePeriod - 1];

            double[] returnsForStock = Arrays.copyOf( simulatedReturns[i],
                                                      timePeriod );
            Arrays.sort( returnsForStock );
            minReturns[i] = returnsForStock[0];
        }

        minAndFinalReturns.add( finalDayReturns );
        minAndFinalReturns.add( minReturns );

        return minAndFinalReturns;
    }

    /**
     * Simulated normally distributed prices for a stock's initial price and its volatility.
     * 
     * @param stockValue
     * @param volatility
     * @return 2D array containing prices over the time period for simulation.
     */
    private double[][] simulatePrices( double stockValue, double volatility )
    {
        double[][] stockValues = new double[numberOfSimulations][timePeriod];

        double possibleStockValue;

        for( int sim = 0 ; sim < numberOfSimulations ; sim++ )
        {
            for( int day = 0 ; day < timePeriod ; day++ )
            {
                if( day == 0 )
                {
                    possibleStockValue = stockValue
                                         + ( volatility * rng.nextGaussian() * stockValue );
                    stockValues[sim][day] = possibleStockValue;
                }
                else
                {
                    possibleStockValue = stockValues[sim][day - 1]
                                         + ( volatility * rng.nextGaussian() * stockValues[sim][day - 1] );
                    stockValues[sim][day] = possibleStockValue;
                }
            }
        }
        return stockValues;
    }

    /**
     * Estimates var for a certain number of days for backtesting.
     * 
     * @param numberOfDaysToTest number of days to estimate VaR over
     * @return array containing estimations of VaR for each day until the target number
     */
    public double[] estimateVaRForBacktesting_OneStock( int numberOfDaysToTest )
    {
        double[] estimations = new double[numberOfDaysToTest];
        double[] returns = VarUtils.getReturnsFromFile( stockPriceDataFiles.get( 0 ) );
        int numberOfReturnsToUse = returns.length - 1 - numberOfDaysToTest;

        for( int day = 0 ; day < numberOfDaysToTest ; day++ )
        {
            double[] returnsToUse = Arrays.copyOf( returns, numberOfReturnsToUse );
            double volatility = VarUtils.computeVolatility_GARCH( returnsToUse );
            double[] finalMaxVars = computeForOneStock( portfolioValues.get( 0 ), volatility );
            estimations[day] = finalMaxVars[0];
            numberOfReturnsToUse++;
        }
        return estimations;
    }

    /**
     * 
     * @return final var computed using this model.
     */
    public double getMonteCarloFinalVar()
    {
        return monteCarloFinalVar;
    }

    /**
     * 
     * @return max var simulated using this model.
     */
    public double getMonteCarloMaximumVar()
    {
        return monteCarloMaximumVar;
    }

    /**
     * @param portfolioValues
     *            the portfolioValues to set
     */
    public void setPortfolioValues( ArrayList<Double> portfolioValues )
    {
        this.portfolioValues = portfolioValues;
    }

    /**
     * @param numberOfStocks
     *            the numberOfStocks to set
     */
    public void setNumberOfStocks( int numberOfStocks )
    {
        this.numberOfStocks = numberOfStocks;
    }

    /**
     * @param timePeriod
     *            the timePeriod to set
     */
    public void setTimePeriod( int timePeriod )
    {
        this.timePeriod = timePeriod;
    }

    /**
     * @param numberOfSimulations
     *            the numberOfSimulations to set
     */
    public void setNumberOfSimulations( int numberOfSimulations )
    {
        this.numberOfSimulations = numberOfSimulations;
    }

    /**
     * @param optionPricingType the optionPricingType to set
     */
    public void setOptionPricingType( String optionPricingType )
    {
        this.optionPricingType = optionPricingType;
    }

}
