/**
 * 
 */
package mvc;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.nm.var.src.BackTesting;
import com.nm.var.src.SimulationSetup;
import com.nm.var.src.StressTester;

/**
 * MVC Design pattern
 * This class is the Controller which mediates the flow of data between the Model and the View (front-end).
 * Listens for user actions on the View and updates the view with the relevant data computed as a result of these actions.
 */
public class Controller
{
    private View  view;
    private Model model;

    public Controller( View view, Model model )
    {
        this.view = view;
        this.model = model;
        view.addComputeVaRListener( new ComputeVaRListener() );
        view.addBacktestListener( new RunBacktestListener() );
        view.addStressTestListener( new RunStressTestListener() );
    }

    /**
     * Attached to the 'Compute' button on the GUI, responsible for gathering the user-selected setup and updating the GUI with computed data.
     */
    class ComputeVaRListener implements ActionListener
    {
        @Override
        public void actionPerformed( ActionEvent arg0 )
        {
            computeVaRAndUpdateView();
        }

    }

    /**
     * Attached to the 'Run' button on the Backtest panel, responsible for gathering the user-selected setup and updating the GUI with computed data.
     */
    class RunBacktestListener implements ActionListener
    {
        @Override
        public void actionPerformed( ActionEvent arg0 )
        {
            runBacktestAndUpdateView();
        }

    }

    /**
     * Attached to the 'Run' button on the Stress test panel, responsible for gathering the user-selected setup and updating the GUI with computed data.
     */
    class RunStressTestListener implements ActionListener
    {
        @Override
        public void actionPerformed( ActionEvent arg0 )
        {
            runStressTestAndUpdateView();
        }

    }

    public static void main( String[] args )
    {
        View v = new View();
        Model m = Model.getInstance();
        @SuppressWarnings("unused")
        Controller c = new Controller( v, m );
    }

    /**
     * Gets the user-selected setup for stress testing and updates the view with the results from the Model.
     */
    public void runStressTestAndUpdateView()
    {
        StressTester st = new StressTester( view.getStressTestPortfolio() );
        st.run();
        view.setStressTestOutput( st.getResult() );
    }

    /**
     * Gets the user-selected setup for backtesting and updates the view with the results from the Model.
     */
    public void runBacktestAndUpdateView()
    {
        SimulationSetup backtestSetup = view.getBacktestSetup();
        if( backtestSetup != null )
        {
            BackTesting backtester = new BackTesting( backtestSetup );
            backtester.backTestPortfolio();
            view.setBacktestOutput( backtester.getResult() );
        }
    }

    /**
     * Gets the user-selected paramaters from the View.<br>
     * Gets the VaR calculation from the Model using the parameters.<br>
     * Updates view with the VaR calculation.
     * 
     * @return void (does nothing) if simulation setup is null
     */
    private void computeVaRAndUpdateView()
    {
        SimulationSetup simulationSetup = view.getSimulationSetup();
        if( simulationSetup == null )
        {
            return;
        }
        double[] estimations = model.getEstimation( simulationSetup );
        view.setEstimations( estimations );
    }

}
