package com.nm.var.src;

/**
 * Combines the user-selected VaR model, option pricing model, confidence level, time horizon and
 * user-selected portfolio to use when computing VaR.
 */
public class SimulationSetup
{
    /** The model to use for computing VaR. */
    private String    model;
    /** The model to use for pricing options. */
    private String    optionPricingType;
    /** The confidence level to compute VaR at. */
    private int       confidenceLevel;
    /** The time horizon to compute VaR over. */
    private int       timeHorizon;
    /** The portfolio to compute VaR for. */
    private Portfolio portfolio;

    /**
     * @return user-selected portfolio
     */
    public Portfolio getPortfolio()
    {
        return portfolio;
    }

    /** Blank constructor. Setup info can be added later. */
    public SimulationSetup()
    {
        // blank constructor
    }

    /**
     * Creates a simulation setup using the user-selected VaR model, option pricing model,
     * confidence level, time horizon and user-selected portfolio to use.
     * 
     * @param selectedPortfolio
     * @param selectedModel
     * @param selectedOptionPricingType
     * @param confidenceLevel
     * @param timeHorizon
     */
    public SimulationSetup( Portfolio selectedPortfolio, String selectedModel, String selectedOptionPricingType,
                            int confidenceLevel,
                            int timeHorizon )
    {
        this.portfolio = selectedPortfolio;
        this.model = selectedModel;
        this.optionPricingType = selectedOptionPricingType;
        this.confidenceLevel = confidenceLevel;
        this.timeHorizon = timeHorizon;
    }

    /**
     * @return the user-selected VaR model
     */
    public String getModel()
    {
        return model;
    }

    /**
     * @return the optionPricingType selected by the user
     */
    public String getOptionPricingType()
    {
        return optionPricingType;
    }

    /**
     * @return the confidenceLevel to compute VaR at.
     */
    public int getConfidenceLevel()
    {
        return confidenceLevel;
    }

    /**
     * @return the timeHorizon to compute VaR over.
     */
    public int getTimeHorizon()
    {
        return timeHorizon;
    }

}
