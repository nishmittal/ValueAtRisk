/**
 * 
 */
package com.nm.var.src;

import java.io.File;
import java.util.ArrayList;

/**
 * Represent a portfolio (a collection of assets and options) in the program.
 */
public class Portfolio
{
    /** List of options held in this portfolio. */
    private ArrayList<Option> options;
    /** List of historical stock data for the assets in this portfolio. */
    private ArrayList<File>   stockPriceDataFiles;
    /** List of investments made in assets of this portfolio. */
    private ArrayList<Double> investments;
    /** List of assets held in this portfolio. */
    private ArrayList<Asset>  assets;
    /** Name of the portfolio for identification. */
    private String            name = "";
    
    
    /**
     * Initialises a blank portfolio. Assets and options can be added later.
     */
    public Portfolio()
    {
        assets = new ArrayList<Asset>();
        investments = new ArrayList<Double>();
        stockPriceDataFiles = new ArrayList<File>();
        options = new ArrayList<Option>();
    }

    /**
     * Initialise a portfolio with a list of assets and options.
     * 
     * @param assets list of assets to add to this portfolio
     * @param options list of options to add to this portfolio
     */
    public Portfolio( ArrayList<Asset> assets, ArrayList<Option> options )
    {
        this.options = options;
        this.assets = assets;
        updatePortfolioAssets();
    }

    /**
     * Initialise a portfolio using a list of options list of stock data and a list of investments.
     * 
     */
    public Portfolio( ArrayList<Option> options, ArrayList<File> stockPriceData,
                      ArrayList<Double> investments )
    {
        this.options = options;
        this.stockPriceDataFiles = stockPriceData;
        this.investments = investments;
    }

    /**
     * Updates the assets and historical stock price data held in this portfolio.
     */
    private void updatePortfolioAssets()
    {
        stockPriceDataFiles = new ArrayList<File>();
        investments = new ArrayList<Double>();

        for( Asset asset : assets )
        {
            stockPriceDataFiles.add( asset.getData() );
            investments.add( asset.getInvestment() );
        }
    }

    /**
     * @return
     *         options in this portfolio
     */
    public ArrayList<Option> getOptions()
    {
        return options;
    }

    /**
     * @param options the options to set
     */
    public void setOptions( ArrayList<Option> options )
    {
        this.options = options;
    }

    /**
     * Add an option to this portfolio.
     * @param option
     */
    public void addOption( Option option )
    {
        this.options.add( option );
    }

    /**
     * @return a list of stock data files from this portfolio
     */
    public ArrayList<File> getStockPriceDataFiles()
    {
        return stockPriceDataFiles;
    }

    /**
     * @param stockPriceDataFiles the stockPriceDataFiles to set
     */
    public void setStockPriceDataFiles( ArrayList<File> stockPriceDataFiles )
    {
        this.stockPriceDataFiles = stockPriceDataFiles;
    }

    /**
     * @return the investments made in this portfolio.
     */
    public ArrayList<Double> getInvestments()
    {
        return investments;
    }

    /**
     * @param investments the investments to set
     */
    public void setInvestments( ArrayList<Double> investments )
    {
        this.investments = investments;
    }

    /**
     * Add an asset to this portfolio.
     * @param asset
     */
    public void addAsset( Asset asset )
    {
        this.assets.add( asset );
        updatePortfolioAssets();
    }

    /**
     * @return list of assets in this portfolio.
     */
    public ArrayList<Asset> getAssets()
    {
        return assets;
    }

    /**
     * @param assets the assets to set
     */
    public void setAssets( ArrayList<Asset> assets )
    {
        this.assets = assets;
        updatePortfolioAssets();
    }

    /**
     * Returns the total value of the investments within the portfolio.
     */
    public double getAssetsValue()
    {
        double sum = 0.0;
        for( double value : investments )
        {
            sum += value;
        }

        return sum;
    }

    /**
     * @return the name of the portfolio
     */
    public String getName()
    {
        return name;
    }

    /**
     * Set name of this portfolio.
     * @param name the name to set
     */
    public void setName( String name )
    {
        this.name = name;
    }
}
