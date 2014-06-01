/**
 * 
 */
package mvc;

import com.nm.var.src.HistoricalSimulation;
import com.nm.var.src.ModelBuilding;
import com.nm.var.src.MonteCarloSimulation;
import com.nm.var.src.SimulationSetup;
import com.nm.var.src.VarUtils;

/**
 * MVC Design Pattern.
 * Singleton Model Class which is called upon by the Controller to execute instructions as per user
 * actions on the
 * View.
 */
public class Model
{
    /**
     * Single instance of the Model used by the Controller to compute results for a user-selected
     * setup.
     */
    private static final Model instance = new Model();

    /** Empty constructor. */
    private Model()
    {
    }

    /**
     * @return the singleton instance of the Model.
     */
    public static Model getInstance()
    {
        return instance;
    }

    /**
     * Responsible for construction required VaR and option pricing models for VaR computation using
     * the simulationSetup.
     * 
     * @param simulationSetup the setup selected by the user on the View
     * @return estimations of the final and max VaRs from the result of the computations.
     */
    public double[] getEstimation( SimulationSetup simulationSetup )
    {
        // using passed parameters create relevant objects and get estimation
        double[] finalMaxVars = new double[2];

        switch( simulationSetup.getModel() )
        {
            case VarUtils.MB:
                finalMaxVars = getEstimationsUsingModelBuilding( simulationSetup );
                break;
            case VarUtils.HS:
                finalMaxVars = getEstimationsUsingHistoricalSimulation( simulationSetup );
                break;
            case VarUtils.MC:
                finalMaxVars = getEstimationsUsingMonteCarloSimulation( simulationSetup );
                break;
        }
        simulationSetup.getPortfolio();
        simulationSetup.getOptionPricingType();
        simulationSetup.getTimeHorizon();
        simulationSetup.getConfidenceLevel();

        return finalMaxVars;
    }

    /**
     * Responsible for using the Model-Building VaR model to obtain the final VaR for the
     * user-defined simulationSetup.
     * 
     * @param simulationSetup
     * @return array containing the final VaR computed from the Model-Building VaR model.
     */
    private double[] getEstimationsUsingModelBuilding( SimulationSetup simulationSetup )
    {
        ModelBuilding mb = new ModelBuilding( simulationSetup.getPortfolio(),
                                              simulationSetup.getConfidenceLevel(),
                                              simulationSetup.getTimeHorizon() );
        return mb.computeValueAtRisk();
    }

    /**
     * Responsible for using the Historical Simulation VaR model to obtain the final VaR for the
     * user-defined simulationSetup.
     * 
     * @param simulationSetup
     * @return array containing the final VaR computed from the Historical Simulation VaR model.
     */
    private double[] getEstimationsUsingHistoricalSimulation( SimulationSetup simulationSetup )
    {
        HistoricalSimulation hs = new HistoricalSimulation( simulationSetup.getPortfolio(),
                                                            simulationSetup.getConfidenceLevel() );
        hs.setOptionPricingType( simulationSetup.getOptionPricingType() );
        return hs.computeValueAtRiskForPortfolio();

    }

    /**
     * Responsible for using the Monte Carlo VaR model to obtain the final VaR for the user-defined
     * simulationSetup.
     * 
     * @param simulationSetup
     * @return array containing the final VaR computed from the Monte Carlo VaR model.
     */
    private double[] getEstimationsUsingMonteCarloSimulation( SimulationSetup simulationSetup )
    {
        MonteCarloSimulation mc = new MonteCarloSimulation( simulationSetup.getPortfolio(),
                                                            simulationSetup.getConfidenceLevel(),
                                                            simulationSetup.getTimeHorizon() );
        mc.setOptionPricingType( simulationSetup.getOptionPricingType() );

        return mc.computeForPortfolio();

    }
}
