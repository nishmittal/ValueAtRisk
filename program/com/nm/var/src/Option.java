package com.nm.var.src;

import java.io.File;

/**
 * Represents an option in the program.
 * Holds all possible parameters for VaR computation of this option.
 */
public class Option
{
    /** Option parameters. */
    // TODO daily or annual volatility, annual = daily * sqrt(252)
    private double interest, stockPrice, strike, dailyVolatility;
    private int    timeToMaturity, optionType;
    /** Four letter code for the stock on which this option is held. */
    private String stockID;
    /** Number of shares of the underlying asset held undert his option. */
    private int    numShares;
    /** Historical price data of the option's underlying asset. */
    private File   priceData;
    /** Name for the option. */
    private String name;

    /**
     * Creates an option using standard option parameters
     * 
     * @param stockPrice
     * @param strike
     * @param interest annual?
     * @param dailyVolatility
     * @param timeToMaturity in days
     * @param stockID of the underlying asset.
     * @param optionType MUST BE ONE OF VARUTILS.AMERICAN/EUROPEAN-CALL/PUT
     * @param priceData of the underlying asset.
     */
    public Option( double stockPrice, int numShares, double strike, double interest, double dailyVolatility,
                   int timeToMaturity, String stockID, int optionType, File priceData, String name )
    {
        this.stockPrice = stockPrice;
        this.strike = strike;
        this.interest = interest;
        this.dailyVolatility = dailyVolatility;
        this.timeToMaturity = timeToMaturity;
        this.stockID = stockID;
        this.optionType = optionType;
        this.priceData = priceData;
        this.numShares = numShares;
        this.name = name;
    }

    /**
     * 
     * @return name of the option.
     */
    public String getName()
    {
        return name;
    }

    /**
     * set the name of the option.
     * 
     * @param name
     */
    public void setName( String name )
    {
        this.name = name;
    }

    /**
     * @return the interest
     */
    public double getInterest()
    {
        return interest;
    }

    /**
     * @param interest the interest to set
     */
    public void setInterest( double interest )
    {
        this.interest = interest;
    }

    /**
     * @return the strike
     */
    public double getStrike()
    {
        return strike;
    }

    /**
     * @param strike the strike to set
     */
    public void setStrike( double strike )
    {
        this.strike = strike;
    }

    /**
     * @return the dailyVolatility
     */
    public double getDailyVolatility()
    {
        return dailyVolatility;
    }

    /**
     * @param dailyVolatility the dailyVolatility to set
     */
    public void setDailyVolatility( double dailyVolatility )
    {
        this.dailyVolatility = dailyVolatility;
    }

    /**
     * @return the initialStockPrice
     */
    public double getInitialStockPrice()
    {
        return stockPrice;
    }

    /**
     * @param initialStockPrice the initialStockPrice to set
     */
    public void setInitialStockPrice( double initialStockPrice )
    {
        this.stockPrice = initialStockPrice;
    }

    /**
     * @return the timeToMaturity in days
     */
    public int getTimeToMaturity()
    {
        return timeToMaturity;
    }

    /**
     * @param timeToMaturity the time to maturity of the Option in days
     */
    public void setTimeToMaturity( int timeToMaturity )
    {
        this.timeToMaturity = timeToMaturity;
    }

    /**
     * @return the stockID
     */
    public String getStockID()
    {
        return stockID;
    }

    /**
     * @param stockID the stockID to set
     */
    public void setStockID( String stockID )
    {
        this.stockID = stockID;
    }

    /**
     * @return the optionType
     */
    public int getOptionType()
    {
        return optionType;
    }

    /**
     * @param optionType the optionType to set
     */
    public void setOptionType( int optionType )
    {
        this.optionType = optionType;
    }

    /**
     * @return the priceData
     */
    public File getPriceData()
    {
        return priceData;
    }

    /**
     * @param priceData the priceData to set
     */
    public void setPriceData( File priceData )
    {
        this.priceData = priceData;
    }

    /**
     * @return the numShares
     */
    public int getNumShares()
    {
        return numShares;
    }

}
