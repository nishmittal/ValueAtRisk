package com.nm.var.src;

import java.util.Arrays;
import java.util.Random;

/**
 * Implements the stress testing technique to see what affect a large decline in stock prices has on
 * the value of the portfolio.
 */
public class StressTester
{
    /** The factor by which stock values must decline in the event of a crash. */
    private double crashFactor = 0.50;
    /** Array of portfolio values simulated before and after the crassh. */
    double[]       simulatedValues;
    /** Array of losses occurring after portfolio values are simulated during the stress test. */
    double[]       losses;
    /** The portfolio to stress test. */
    Portfolio      portfolio;
    /** The number of days to conduct the stress test over. */
    int            totalDays;
    /** The day at which the crash should happen. */
    int            crashDay;
    /** Random number generator used to simulate changes in portfolio values from day to day. */
    private Random rng;
    /** The result of the stress test. */
    private String output;

    /**
     * Initialise a stress tester using a portfolio.
     * 
     * @param pf
     */
    public StressTester( Portfolio pf )
    {
        this.portfolio = pf;
        rng = new Random();
    }

    /**
     * Blank constructor.
     */
    public StressTester()
    {
    }

    /**
     * Run the stress test by simulating the portfolio values before and after the crash and
     * building the result from that data.
     */
    public void run()
    {
        simulateCrashAndRecordLosses();
        buildOutput();
    }

    /**
     * Builds up a results from the results of the stress test.
     */
    private void buildOutput()
    {
        output = "Original portfolio value: " + portfolio.getAssetsValue() + "\n";
        Arrays.sort( simulatedValues );
        Arrays.sort( losses );
        double minValue = simulatedValues[0];
        double maxValue = simulatedValues[simulatedValues.length - 1];
        output += ( "Min | Max portfolio value = " + minValue + " | " + maxValue );
        output += "\n";
        double minLoss = losses[0];
        double maxLoss = losses[losses.length - 1];
        output += ( "Min | Max portfolio loss  = " + minLoss + " | " + maxLoss );
        output += ( "\n \n NOTE: Negative loss indicates a profit." );
    }

    /**
     * Simulates stock price movement on either side of the crash as a function of the previous
     * stock price multiplied by some random return.
     * On the day of the crash, the portfolio value declines by the crash factor defined earlier.
     */
    private void simulateCrashAndRecordLosses()
    {
        double initialValue = VarUtils.sumOf( portfolio.getInvestments() );
        totalDays = 100;
        crashDay = totalDays / 4;
        simulatedValues = new double[totalDays];
        losses = new double[totalDays];
        simulatedValues[0] = initialValue;
        losses[0] = 0.0;
        for( int day = 1 ; day < totalDays ; day++ )
        {
            // value today = value yday + (value yday * some random number)
            if( day == crashDay - 1 )
            {
                double valueAtCrash = simulatedValues[day - 1] * crashFactor;
                simulatedValues[day] = valueAtCrash;

            }
            else
            {
                int n = rng.nextInt( 5 );
                double upOrDown = ( (double) n ) / 100;
                if( rng.nextInt( 2 ) == 0 )
                {
                    upOrDown = upOrDown * -1;
                }
                double valueOnDay = simulatedValues[day - 1]
                                    + ( simulatedValues[day - 1] * upOrDown );
                simulatedValues[day] = VarUtils.roundTwoDP( valueOnDay );
            }

            losses[day] = VarUtils.roundTwoDP( initialValue - simulatedValues[day] );
        }
    }

    /**
     * @return the losses experienced by the portfoliod during the stress test.
     */
    public double[] getLosses()
    {
        return losses;
    }

    /**
     * 
     * @return the maximum loss experienced by the portfolio during the stress test.
     */
    public double getMaxLoss()
    {
        return losses[losses.length - 1];
    }

    /**
     * set the portfolio to stress test
     * 
     * @param pf the portfolio to use
     */
    public void setPortfolio( Portfolio pf )
    {
        this.portfolio = pf;
    }

    /**
     * @param crashFactor the crashFactor to set
     */
    public void setCrashFactor( double crashFactor )
    {
        this.crashFactor = crashFactor;
    }

    /**
     * @param totalDays the totalDays to set
     */
    public void setTotalDays( int totalDays )
    {
        this.totalDays = totalDays;
    }

    /**
     * @param crashDay the crashDay to set
     */
    public void setCrashDay( int crashDay )
    {
        this.crashDay = crashDay;
    }

    /**
     * @return the simulatedValues
     */
    public double[] getSimulatedValues()
    {
        return simulatedValues;
    }

    /**
     * @return the result of the stress test
     */
    public String getResult()
    {
        return output;
    }

}
