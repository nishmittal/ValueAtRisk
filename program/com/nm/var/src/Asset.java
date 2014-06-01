/**
 * 
 */
package com.nm.var.src;

import java.io.File;

/**
 * Representation of an asset.
 * Holds parameters for historical stock price data location, an identifier and the investment.
 */
public class Asset
{
    /** Historical stock price data file for asset. (csv) */
    private File   data;
    /** Identifier for asset. */
    private String ID;
    /** Amount of money invested in the asset. */
    private double investment;

    /** Constructor to initialise an asset. */
    public Asset( File data, String iD, double investment )
    {
        this.data = data;
        ID = iD;
        this.investment = investment;
    }


    /**
     * Black constructor.
     */
    public Asset()
    {
    }

    /**
     * @return the historical price data for this asset
     */
    public File getData()
    {
        return data;
    }

    /**
     * @param data the historical price data to set
     */
    public void setData( File data )
    {
        this.data = data;
    }

    /**
     * @return the identifier of this asset
     */
    public String getID()
    {
        return ID;
    }

    /**
     * @param id the id to set
     */
    public void setID( String id )
    {
        ID = id;
    }

    /**
     * @return the investment in this asset
     */
    public double getInvestment()
    {
        return investment;
    }

    /**
     * @param investment the investment to set
     */
    public void setInvestment( double investment )
    {
        this.investment = investment;
    }

}
