package mvc;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import com.nm.var.gui.AddPortfolioDialog;
import com.nm.var.src.Asset;
import com.nm.var.src.ConfidenceLevel;
import com.nm.var.src.Option;
import com.nm.var.src.Portfolio;
import com.nm.var.src.SimulationSetup;
import com.nm.var.src.VarUtils;

/**
 * MVC Design Pattern
 * View - the main user interface of the program. Updated by the Controller with data from the
 * Model.
 * 
 */
public class View
{
    /**
     * The main frame for the user interface.
     * 
     * All icons courtesy of <a href="http://www.famfamfam.com/lab/icons/silk">mjames at
     * famfamfam.com</a>
     */

    private JFrame               frmApp;
    private final Action         closeAction = new CloseAction();
    private final Action         aboutAction = new AboutAction();
    private JComboBox<String>    cbPortfolios;
    private ArrayList<Portfolio> portfolios  = new ArrayList<Portfolio>();
    private JScrollPane          scrollPaneContents;
    private ButtonGroup          optionPricingType;
    private ButtonGroup          modelType;
    private JTextField           txtTimeHorizon;
    private JButton              btnDelPortfolio;
    private JList<String>        listPortfolioContents;
    private JRadioButton         rdbtnMB;
    private JRadioButton         rdbtnBS;
    private JRadioButton         rdbtnBT;
    private JRadioButton         rdbtnOMC;
    private JLabel               lblOptionPricingInfo;
    private Portfolio            selectedPortfolio;
    private JComboBox<String>    cboConfidenceLevel;
    private SimulationSetup      simulationSetup;
    private JButton              btnCompute;
    private JTextField           txtVaR;
    private JTextField           txtMaxVaR;
    private JTextField           txtBacktestTimeHorizon;
    private JButton              btnRunBacktest;
    private JTextPane            txtBacktestOutput;
    private JComboBox<String>    cboBacktestConfidence;
    private JRadioButton         rdbtnBacktestMB;
    private JRadioButton         rdbtnBacktestHS;
    private JRadioButton         rdbtnBacktestMC;
    private JTextPane            txtStresstestOutput;
    private JComboBox<String>    cboStressTestPortfolio;
    private JButton              btnRunStressTest;
    private ButtonGroup          backtestGroup;
    private JComboBox<String>    cboBacktestPortfolio;

    /**
     * Create the application on the Event-Dispatch Thread.
     */
    public View()
    {
        try
        {
            UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
        }
        catch( ClassNotFoundException | InstantiationException | IllegalAccessException
                | UnsupportedLookAndFeelException e )
        {
            e.printStackTrace();
        }
        initialize();
        createSamplePortfolio();
        updatePortfoliosHeld();
        EventQueue.invokeLater( new Runnable()
        {
            public void run()
            {
                frmApp.setVisible( true );
            }
        } );
    }

    /**
     * Creates a sample portfolio upon application startup.
     */
    private void createSamplePortfolio()
    {
        Portfolio p = new Portfolio();
        File data = new File( "testing/MSFT_Apr2012_Apr2013.csv" );
        Asset a = new Asset( data, "MSFT", 1000.0 );
        p.addAsset( a );
        p.setName( "Sample portfolio" );
        this.portfolios.add( p );
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize()
    {
        frmApp = new JFrame();
        frmApp.setResizable( false );
        frmApp.getContentPane().setBackground( new Color( 100, 149, 237 ) );
        frmApp.setBackground( new Color( 47, 79, 79 ) );
        frmApp.setFont( new Font( "Segoe UI", Font.PLAIN, 12 ) );
        frmApp.setForeground( new Color( 47, 79, 79 ) );
        frmApp.setTitle( "Value at Risk Calculator" );
        frmApp.setBounds( 100, 100, 523, 523 );
        frmApp.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        frmApp.setIconImage( Toolkit.getDefaultToolkit()
                                    .getImage( View.class.getResource( "/com/nm/var/gui/icons/icon.PNG" ) ) );
        frmApp.getContentPane().setLayout( null );

        JMenuBar menuBar = new JMenuBar();
        menuBar.setFont( new Font( "Segoe UI", Font.PLAIN, 12 ) );
        menuBar.setBounds( 0, 0, 517, 21 );
        menuBar.setBorder( null );
        menuBar.setForeground( Color.WHITE );
        menuBar.setBorderPainted( false );
        frmApp.getContentPane().add( menuBar );

        JMenu mnFile = new JMenu( "File" );
        menuBar.add( mnFile );

        JMenuItem mntmExit = new JMenuItem( "Exit" );
        mntmExit.setAction( closeAction );
        mntmExit.setIcon( new ImageIcon(
                                         View.class.getResource( "/com/nm/var/gui/icons/cross.png" ) ) );
        mnFile.add( mntmExit );

        JMenu mnHelp = new JMenu( "Help" );
        menuBar.add( mnHelp );

        JMenuItem mntmAbout = new JMenuItem( "About" );
        mntmAbout.setAction( aboutAction );
        mntmAbout.setIcon( new ImageIcon(
                                          View.class.getResource( "/com/nm/var/gui/icons/information.png" ) ) );
        mnHelp.add( mntmAbout );

        JTabbedPane tabbedPane = new JTabbedPane( JTabbedPane.TOP );
        tabbedPane.setBounds( 10, 32, 497, 451 );
        tabbedPane.setBorder( null );
        frmApp.getContentPane().add( tabbedPane );

        JPanel pnlSetup = new JPanel();
        pnlSetup.setBorder( null );
        pnlSetup.setBackground( Color.WHITE );
        tabbedPane.addTab( "VaR", getIcon( "wrench_orange" ), pnlSetup, null );
        SpringLayout sl_pnlSetup = new SpringLayout();
        pnlSetup.setLayout( sl_pnlSetup );

        JPanel pnlPortfolio = new JPanel();
        sl_pnlSetup.putConstraint( SpringLayout.NORTH, pnlPortfolio, 0, SpringLayout.NORTH,
                                   pnlSetup );
        sl_pnlSetup.putConstraint( SpringLayout.WEST, pnlPortfolio, 0, SpringLayout.WEST, pnlSetup );
        sl_pnlSetup.putConstraint( SpringLayout.SOUTH, pnlPortfolio, 423, SpringLayout.NORTH,
                                   pnlSetup );
        sl_pnlSetup.putConstraint( SpringLayout.EAST, pnlPortfolio, 0, SpringLayout.EAST, pnlSetup );
        pnlPortfolio.setBackground( Color.GRAY );
        pnlPortfolio.setBorder( null );
        pnlSetup.add( pnlPortfolio );
        pnlPortfolio.setLayout( null );

        JLabel lblSelect = new JLabel( "Portfolio:" );
        lblSelect.setFont( new Font( "Tahoma", Font.BOLD, 12 ) );
        lblSelect.setForeground( Color.WHITE );
        lblSelect.setBackground( Color.GRAY );
        lblSelect.setBounds( 10, 14, 59, 14 );
        pnlPortfolio.add( lblSelect );

        cbPortfolios = new JComboBox<String>();
        cbPortfolios.setFont( new Font( "Tahoma", Font.PLAIN, 12 ) );
        cbPortfolios.setBounds( 70, 9, 154, 25 );
        cbPortfolios.setToolTipText( "Select a pre-defined portfolio." );
        cbPortfolios.setMaximumRowCount( 99 );
        cbPortfolios.addItemListener( new ItemListener()
        {

            @Override
            public void itemStateChanged( ItemEvent arg0 )
            {
                updateContentsList();
            }
        } );
        pnlPortfolio.add( cbPortfolios );

        JButton btnNewPortfolio = new JButton( "Create" );
        btnNewPortfolio.setForeground( Color.BLACK );
        btnNewPortfolio.setBackground( new Color( 60, 179, 113 ) );
        btnNewPortfolio.setFont( new Font( "Tahoma", Font.BOLD, 12 ) );
        btnNewPortfolio.setBounds( 20, 39, 96, 25 );
        btnNewPortfolio.addActionListener( new ActionListener()
        {
            public void actionPerformed( ActionEvent arg0 )
            {
                addNewPortfolio();
            }
        } );
        btnNewPortfolio.setToolTipText( "Create new portfolio" );
        btnNewPortfolio.setIcon( new ImageIcon(
                                                View.class.getResource( "/com/nm/var/gui/icons/add.png" ) ) );
        pnlPortfolio.add( btnNewPortfolio );

        btnDelPortfolio = new JButton( "Delete" );
        btnDelPortfolio.setBackground( Color.RED );
        btnDelPortfolio.setFont( new Font( "Tahoma", Font.BOLD, 12 ) );
        btnDelPortfolio.setBounds( 126, 39, 98, 25 );
        btnDelPortfolio.addActionListener( new ActionListener()
        {

            @Override
            public void actionPerformed( ActionEvent e )
            {
                deletePortfolio();
            }
        } );
        btnDelPortfolio.setToolTipText( "Remove portfolio" );
        btnDelPortfolio.setIcon( new ImageIcon(
                                                View.class.getResource( "/com/nm/var/gui/icons/delete.png" ) ) );
        btnDelPortfolio.setEnabled( false );
        pnlPortfolio.add( btnDelPortfolio );

        scrollPaneContents = new JScrollPane();
        scrollPaneContents.setToolTipText( "Contents of the portfolio" );
        scrollPaneContents.setBounds( 15, 71, 197, 162 );
        scrollPaneContents.setBackground( Color.GRAY );
        scrollPaneContents.setBorder( new TitledBorder(
                                                        new LineBorder( new Color( 255, 255, 255 ) ),
                                                        "Contents", TitledBorder.LEADING,
                                                        TitledBorder.TOP, null, Color.WHITE ) );
        pnlPortfolio.add( scrollPaneContents );

        listPortfolioContents = new JList<String>();
        listPortfolioContents.setFont( new Font( "Tahoma", Font.PLAIN, 12 ) );
        listPortfolioContents.setBackground( Color.GRAY );
        listPortfolioContents.setForeground( Color.WHITE );
        scrollPaneContents.setViewportView( listPortfolioContents );

        JPanel panel = new JPanel();
        panel.setBackground( Color.GRAY );
        panel.setBorder( new TitledBorder( new LineBorder( new Color( 255, 255, 255 ) ),
                                           "VaR Setup", TitledBorder.LEADING, TitledBorder.TOP,
                                           null, Color.WHITE ) );
        panel.setBounds( 10, 244, 472, 168 );
        pnlPortfolio.add( panel );
        panel.setLayout( null );

        JLabel lblModel = new JLabel( "Model:" );
        lblModel.setFont( new Font( "Tahoma", Font.BOLD, 12 ) );
        lblModel.setForeground( Color.WHITE );
        lblModel.setBounds( 10, 24, 52, 14 );
        panel.add( lblModel );

        JLabel lblOptionPricing = new JLabel( "Option pricing:" );
        lblOptionPricing.setFont( new Font( "Tahoma", Font.BOLD, 12 ) );
        lblOptionPricing.setForeground( Color.WHITE );
        lblOptionPricing.setBounds( 10, 49, 100, 14 );
        panel.add( lblOptionPricing );

        JLabel lblTimeHorizon = new JLabel( "Time horizon:" );
        lblTimeHorizon.setFont( new Font( "Tahoma", Font.BOLD, 12 ) );
        lblTimeHorizon.setForeground( Color.WHITE );
        lblTimeHorizon.setBounds( 10, 74, 91, 14 );
        panel.add( lblTimeHorizon );

        JLabel lblConfidence = new JLabel( "Confidence level:" );
        lblConfidence.setFont( new Font( "Tahoma", Font.BOLD, 12 ) );
        lblConfidence.setForeground( Color.WHITE );
        lblConfidence.setBounds( 10, 99, 111, 14 );
        panel.add( lblConfidence );

        rdbtnMB = new JRadioButton( "Model Building" );
        rdbtnMB.addActionListener( new ActionListener()
        {
            public void actionPerformed( ActionEvent arg0 )
            {
                disableOptionPricing();
            }
        } );
        rdbtnMB.setForeground( Color.WHITE );
        rdbtnMB.setBackground( Color.GRAY );
        rdbtnMB.setBounds( 114, 21, 93, 23 );
        rdbtnMB.setActionCommand( VarUtils.MB );
        panel.add( rdbtnMB );

        JRadioButton rdbtnHS = new JRadioButton( "Historical Simulation" );
        rdbtnHS.addActionListener( new ActionListener()
        {
            public void actionPerformed( ActionEvent arg0 )
            {
                disableOptionPricing();
            }
        } );
        rdbtnHS.setForeground( Color.WHITE );
        rdbtnHS.setBackground( Color.GRAY );
        rdbtnHS.setBounds( 210, 21, 119, 23 );
        rdbtnHS.setActionCommand( VarUtils.HS );
        panel.add( rdbtnHS );

        JRadioButton rdbtnMC = new JRadioButton( "Monte Carlo Simulation" );
        rdbtnMC.addActionListener( new ActionListener()
        {
            public void actionPerformed( ActionEvent e )
            {
                disableOptionPricing();
            }
        } );
        rdbtnMC.setForeground( Color.WHITE );
        rdbtnMC.setBackground( Color.GRAY );
        rdbtnMC.setBounds( 331, 21, 135, 23 );
        rdbtnMC.setActionCommand( VarUtils.MC );
        panel.add( rdbtnMC );

        modelType = new ButtonGroup();
        modelType.add( rdbtnMB );
        modelType.add( rdbtnHS );
        modelType.add( rdbtnMC );

        rdbtnBS = new JRadioButton( "Black Scholes" );
        rdbtnBS.setForeground( Color.WHITE );
        rdbtnBS.setBackground( Color.GRAY );
        rdbtnBS.setBounds( 114, 46, 88, 23 );
        rdbtnBS.setActionCommand( VarUtils.BS );
        panel.add( rdbtnBS );

        rdbtnBT = new JRadioButton( "Binomial Tree" );
        rdbtnBT.setForeground( Color.WHITE );
        rdbtnBT.setBackground( Color.GRAY );
        rdbtnBT.setBounds( 210, 46, 119, 23 );
        rdbtnBT.setActionCommand( VarUtils.BT );
        panel.add( rdbtnBT );

        rdbtnOMC = new JRadioButton( "Monte Carlo Simulation" );
        rdbtnOMC.setForeground( Color.WHITE );
        rdbtnOMC.setBackground( Color.GRAY );
        rdbtnOMC.setBounds( 331, 46, 135, 23 );
        rdbtnOMC.setActionCommand( VarUtils.MC );
        panel.add( rdbtnOMC );

        optionPricingType = new ButtonGroup();
        optionPricingType.add( rdbtnBS );
        optionPricingType.add( rdbtnBT );
        optionPricingType.add( rdbtnOMC );

        txtTimeHorizon = new JTextField();
        txtTimeHorizon.setForeground( new Color( 100, 149, 237 ) );
        txtTimeHorizon.setToolTipText( "number of days over which to estimate VaR" );
        txtTimeHorizon.setBounds( 111, 72, 86, 20 );
        panel.add( txtTimeHorizon );
        txtTimeHorizon.setColumns( 10 );

        cboConfidenceLevel = new JComboBox<String>();
        cboConfidenceLevel.setFont( new Font( "Tahoma", Font.PLAIN, 12 ) );
        cboConfidenceLevel.setForeground( new Color( 0, 0, 0 ) );
        cboConfidenceLevel.setBackground( new Color( 255, 255, 255 ) );
        cboConfidenceLevel.setBounds( 121, 97, 76, 20 );
        cboConfidenceLevel.setModel( new DefaultComboBoxModel<>( ConfidenceLevel.getStringValues() ) );
        panel.add( cboConfidenceLevel );

        lblOptionPricingInfo = new JLabel( "Option pricing not available for Model Building." );
        lblOptionPricingInfo.setHorizontalAlignment( SwingConstants.RIGHT );
        lblOptionPricingInfo.setForeground( Color.GREEN );
        lblOptionPricingInfo.setBounds( 234, 70, 228, 25 );
        lblOptionPricingInfo.setVisible( false );
        panel.add( lblOptionPricingInfo );

        btnCompute = new JButton( "Compute" );
        btnCompute.setIcon( new ImageIcon(
                                           View.class.getResource( "/com/nm/var/gui/icons/control_play_blue.png" ) ) );
        btnCompute.setFont( new Font( "Tahoma", Font.BOLD, 12 ) );
        btnCompute.setBackground( Color.BLUE );
        btnCompute.setToolTipText( "Compute VaR for current setup." );
        btnCompute.setBounds( 351, 126, 111, 31 );
        btnCompute.setEnabled( false );
        panel.add( btnCompute );

        JPanel pnlResults = new JPanel();
        pnlResults.setBorder( new TitledBorder( new LineBorder( new Color( 255, 255, 255 ) ),
                                                "Results", TitledBorder.LEADING, TitledBorder.TOP,
                                                null, new Color( 255, 255, 255 ) ) );
        pnlResults.setBackground( Color.GRAY );
        pnlResults.setBounds( 234, 14, 248, 224 );
        pnlPortfolio.add( pnlResults );
        pnlResults.setLayout( null );

        JLabel lblFinalVar = new JLabel( "VaR:" );
        lblFinalVar.setForeground( Color.WHITE );
        lblFinalVar.setFont( new Font( "Tahoma", Font.BOLD, 12 ) );
        lblFinalVar.setBounds( 10, 69, 36, 14 );
        pnlResults.add( lblFinalVar );

        JLabel lblMaxVar = new JLabel( "Max VaR:" );
        lblMaxVar.setForeground( Color.WHITE );
        lblMaxVar.setFont( new Font( "Tahoma", Font.BOLD, 12 ) );
        lblMaxVar.setBounds( 10, 101, 62, 14 );
        pnlResults.add( lblMaxVar );

        txtVaR = new JTextField();
        txtVaR.setToolTipText( "The standard VaR for the portfolio." );
        txtVaR.setEditable( false );
        txtVaR.setForeground( new Color( 255, 140, 0 ) );
        txtVaR.setFont( new Font( "Tahoma", Font.BOLD, 12 ) );
        txtVaR.setBounds( 71, 67, 129, 20 );
        pnlResults.add( txtVaR );
        txtVaR.setColumns( 10 );

        txtMaxVaR = new JTextField();
        txtMaxVaR.setToolTipText( "The maximum VaR estimated during the time period (for Monte Carlo Simulation)." );
        txtMaxVaR.setForeground( Color.RED );
        txtMaxVaR.setFont( new Font( "Tahoma", Font.BOLD, 12 ) );
        txtMaxVaR.setEditable( false );
        txtMaxVaR.setColumns( 10 );
        txtMaxVaR.setBounds( 71, 99, 129, 20 );
        pnlResults.add( txtMaxVaR );

        JPanel pnlModelTest = new JPanel();
        pnlModelTest.setBackground( Color.GRAY );
        tabbedPane.addTab( "Model Testing", getIcon( "tick" ), pnlModelTest, null );
        pnlModelTest.setLayout( null );

        JPanel pnlBacktesting = new JPanel();
        pnlBacktesting.setBorder( new TitledBorder( new LineBorder( new Color( 255, 255, 255 ) ),
                                                    "Backtesting", TitledBorder.LEADING,
                                                    TitledBorder.TOP, null, Color.WHITE ) );
        pnlBacktesting.setBackground( Color.GRAY );
        pnlBacktesting.setBounds( 10, 11, 472, 227 );
        pnlModelTest.add( pnlBacktesting );
        pnlBacktesting.setLayout( null );

        JLabel label = new JLabel( "Portfolio:" );
        label.setBounds( 10, 26, 59, 14 );
        label.setForeground( Color.WHITE );
        label.setFont( new Font( "Tahoma", Font.BOLD, 12 ) );
        label.setBackground( Color.GRAY );
        pnlBacktesting.add( label );

        cboBacktestPortfolio = new JComboBox<String>();
        cboBacktestPortfolio.setBounds( 70, 21, 154, 25 );
        cboBacktestPortfolio.setToolTipText( "Select a pre-defined portfolio." );
        cboBacktestPortfolio.setMaximumRowCount( 99 );
        cboBacktestPortfolio.setFont( new Font( "Tahoma", Font.PLAIN, 12 ) );
        pnlBacktesting.add( cboBacktestPortfolio );

        JLabel label_1 = new JLabel( "Model:" );
        label_1.setBounds( 10, 85, 52, 14 );
        label_1.setForeground( Color.WHITE );
        label_1.setFont( new Font( "Tahoma", Font.BOLD, 12 ) );
        pnlBacktesting.add( label_1 );

        rdbtnBacktestMB = new JRadioButton( "Model Building" );
        rdbtnBacktestMB.setBounds( 114, 82, 93, 23 );
        rdbtnBacktestMB.setForeground( Color.WHITE );
        rdbtnBacktestMB.setBackground( Color.GRAY );
        rdbtnBacktestMB.setActionCommand( "MB" );
        pnlBacktesting.add( rdbtnBacktestMB );

        rdbtnBacktestHS = new JRadioButton( "Historical Simulation" );
        rdbtnBacktestHS.setBounds( 210, 82, 119, 23 );
        rdbtnBacktestHS.setForeground( Color.WHITE );
        rdbtnBacktestHS.setBackground( Color.GRAY );
        rdbtnBacktestHS.setActionCommand( "HS" );
        pnlBacktesting.add( rdbtnBacktestHS );

        rdbtnBacktestMC = new JRadioButton( "Monte Carlo Simulation" );
        rdbtnBacktestMC.setBounds( 331, 82, 135, 23 );
        rdbtnBacktestMC.setForeground( Color.WHITE );
        rdbtnBacktestMC.setBackground( Color.GRAY );
        rdbtnBacktestMC.setActionCommand( "MC" );
        pnlBacktesting.add( rdbtnBacktestMC );

        backtestGroup = new ButtonGroup();
        backtestGroup.add( rdbtnBacktestMB );
        backtestGroup.add( rdbtnBacktestHS );
        backtestGroup.add( rdbtnBacktestMC );

        JLabel label_2 = new JLabel( "Confidence level:" );
        label_2.setBounds( 275, 65, 111, 14 );
        label_2.setForeground( Color.WHITE );
        label_2.setFont( new Font( "Tahoma", Font.BOLD, 12 ) );
        pnlBacktesting.add( label_2 );

        cboBacktestConfidence = new JComboBox<String>();
        cboBacktestConfidence.setBounds( 386, 63, 76, 20 );
        cboBacktestConfidence.setForeground( Color.BLACK );
        cboBacktestConfidence.setFont( new Font( "Tahoma", Font.PLAIN, 12 ) );
        cboBacktestConfidence.setBackground( Color.WHITE );
        cboBacktestConfidence.setModel( new DefaultComboBoxModel<>( ConfidenceLevel.getStringValues() ) );
        pnlBacktesting.add( cboBacktestConfidence );

        JLabel label_3 = new JLabel( "Time horizon:" );
        label_3.setBounds( 10, 63, 91, 14 );
        label_3.setForeground( Color.WHITE );
        label_3.setFont( new Font( "Tahoma", Font.BOLD, 12 ) );
        pnlBacktesting.add( label_3 );

        txtBacktestTimeHorizon = new JTextField();
        txtBacktestTimeHorizon.setBounds( 111, 61, 86, 20 );
        txtBacktestTimeHorizon.setToolTipText( "number of days over which to estimate VaR" );
        txtBacktestTimeHorizon.setForeground( new Color( 100, 149, 237 ) );
        txtBacktestTimeHorizon.setColumns( 10 );
        pnlBacktesting.add( txtBacktestTimeHorizon );

        btnRunBacktest = new JButton( "Run" );
        btnRunBacktest.setBounds( 369, 23, 93, 25 );
        btnRunBacktest.setIcon( new ImageIcon(
                                               View.class.getResource( "/com/nm/var/gui/icons/control_play_blue.png" ) ) );
        btnRunBacktest.setToolTipText( "Backtest selected model with selected parameters" );
        btnRunBacktest.setFont( new Font( "Tahoma", Font.BOLD, 12 ) );
        btnRunBacktest.setBackground( new Color( 50, 205, 50 ) );
        pnlBacktesting.add( btnRunBacktest );

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds( 10, 110, 452, 106 );
        pnlBacktesting.add( scrollPane );

        txtBacktestOutput = new JTextPane();
        txtBacktestOutput.setForeground( new Color( 100, 149, 237 ) );
        scrollPane.setViewportView( txtBacktestOutput );

        JPanel pnlStressTesting = new JPanel();
        pnlStressTesting.setBorder( new TitledBorder( new LineBorder( new Color( 255, 255, 255 ) ),
                                                      "Stress Testing", TitledBorder.LEADING,
                                                      TitledBorder.TOP, null, Color.WHITE ) );
        pnlStressTesting.setBackground( Color.GRAY );
        pnlStressTesting.setBounds( 10, 246, 472, 166 );
        pnlModelTest.add( pnlStressTesting );
        pnlStressTesting.setLayout( null );

        JLabel label_4 = new JLabel( "Portfolio:" );
        label_4.setForeground( Color.WHITE );
        label_4.setFont( new Font( "Tahoma", Font.BOLD, 12 ) );
        label_4.setBackground( Color.GRAY );
        label_4.setBounds( 10, 27, 59, 14 );
        pnlStressTesting.add( label_4 );

        cboStressTestPortfolio = new JComboBox<String>();
        cboStressTestPortfolio.setToolTipText( "Select a pre-defined portfolio." );
        cboStressTestPortfolio.setMaximumRowCount( 99 );
        cboStressTestPortfolio.setFont( new Font( "Tahoma", Font.PLAIN, 12 ) );
        cboStressTestPortfolio.setBounds( 70, 22, 154, 25 );
        pnlStressTesting.add( cboStressTestPortfolio );

        btnRunStressTest = new JButton( "Run" );
        btnRunStressTest.setIcon( new ImageIcon(
                                                 View.class.getResource( "/com/nm/var/gui/icons/control_play_blue.png" ) ) );
        btnRunStressTest.setToolTipText( "Backtest selected model with selected parameters" );
        btnRunStressTest.setFont( new Font( "Tahoma", Font.BOLD, 12 ) );
        btnRunStressTest.setBackground( new Color( 50, 205, 50 ) );
        btnRunStressTest.setBounds( 369, 22, 93, 25 );
        pnlStressTesting.add( btnRunStressTest );

        JScrollPane scrollPane_1 = new JScrollPane();
        scrollPane_1.setBounds( 10, 51, 452, 104 );
        pnlStressTesting.add( scrollPane_1 );

        txtStresstestOutput = new JTextPane();
        txtStresstestOutput.setForeground( new Color( 100, 149, 237 ) );
        scrollPane_1.setViewportView( txtStresstestOutput );
    }

    /**
     * Disables option pricing when Model Building VaR model is selected and informs the user.
     */
    protected void disableOptionPricing()
    {
        boolean enabled;
        if( rdbtnMB.isSelected() )
        {
            enabled = false;
            optionPricingType.clearSelection();
            lblOptionPricingInfo.setVisible( true );
        }
        else
        {
            enabled = true;
            lblOptionPricingInfo.setVisible( false );
        }
        rdbtnBS.setEnabled( enabled );
        rdbtnBT.setEnabled( enabled );
        rdbtnOMC.setEnabled( enabled );
    }

    /**
     * Removes the selected portfolio from the program as per the user action.
     */
    protected void deletePortfolio()
    {
        String selectedItem = (String) cbPortfolios.getSelectedItem();
        String question = "Remove portfolio " + selectedItem + " ?";
        int action = JOptionPane.showConfirmDialog( frmApp, question, "Confirm Portfolio Change",
                                                    JOptionPane.OK_CANCEL_OPTION,
                                                    JOptionPane.QUESTION_MESSAGE );
        if( action == JOptionPane.OK_OPTION )
        {
            Portfolio portfolioToRemove = null;
            for( Portfolio p : portfolios )
            {
                if( p.getName().equals( selectedItem ) )
                {
                    portfolioToRemove = p;
                }
            }
            portfolios.remove( portfolioToRemove );
            updatePortfoliosHeld();
            updateDeleteButton();
        }
    }

    /**
     * Updates the portfolio contents lists with data for the contents of the selected portfolio.
     */
    protected void updateContentsList()
    {
        String selectedPortfolioName = (String) cbPortfolios.getSelectedItem();

        for( Portfolio p : portfolios )
        {
            if( p.getName().equals( selectedPortfolioName ) )
            {
                this.selectedPortfolio = p;
                ArrayList<String> contents = new ArrayList<String>();
                for( Asset a : p.getAssets() )
                {
                    contents.add( a.getID() );
                }
                for( Option a : p.getOptions() )
                {
                    contents.add( a.getName() );
                }

                DefaultListModel<String> model = new DefaultListModel<String>();
                for( String s : contents )
                {
                    model.addElement( s );
                }
                listPortfolioContents.setModel( model );
                return;
            }
        }

    }

    /**
     * Updates the drop-down menu with the list of portfolios held in the program.
     */
    private void updatePortfoliosHeld()
    {
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<String>();
        if( portfolios.isEmpty() )
        {
            model.addElement( "No portfolios available." );
            btnCompute.setEnabled( false );
        }
        else
        {
            for( Portfolio p : portfolios )
            {
                model.addElement( p.getName() );
            }
            btnCompute.setEnabled( true );
        }
        cbPortfolios.setModel( model );
        cboBacktestPortfolio.setModel( model );
        cboStressTestPortfolio.setModel( model );
        updateContentsList();
    }

    /**
     * Gets the specified icon from the icons classpath resource.
     * 
     * @param name of the icon
     * @return Icon corresponding to the name.
     */
    private ImageIcon getIcon( String name )
    {
        return new ImageIcon(
                              View.class.getResource( "/com/nm/var/gui/icons/" + name + ".png" ) );
    }

    /**
     * Shows the Add Portfolio dialog in the Event-Dispatch Thread to allow the user to create a new
     * portfolio.
     */
    private void addNewPortfolio()
    {
        EventQueue.invokeLater( new Runnable()
        {
            @Override
            public void run()
            {
                showAddPortfolioDialog();
            }
        } );
    }

    /**
     * Shows the Add Portfolio Dialog.
     */
    private void showAddPortfolioDialog()
    {

        AddPortfolioDialog dlg = new AddPortfolioDialog( frmApp );
        if( dlg.portfolioAdded() )
        {
            portfolios.add( dlg.getCreatedPortfolio() );
        }
        updatePortfoliosHeld();
        updateDeleteButton();
    }

    /**
     * Disables the delete button if no portfolios are in the program.
     */
    private void updateDeleteButton()
    {
        btnDelPortfolio.setEnabled( !portfolios.isEmpty() );
    }

    /**
     * @return the simulationSetup created by the user for VaR computation
     */
    public SimulationSetup getSimulationSetup()
    {
        checkAndBuildSimulationSetup();
        return simulationSetup;
    }

    /**
     * @return the simulationSetup created by the user for backtesting
     */
    public SimulationSetup getBacktestSetup()
    {
        String selectedPortfolioName = (String) cboBacktestPortfolio.getSelectedItem();
        Portfolio selectedPortfolio = new Portfolio();
        for( Portfolio p : portfolios )
        {
            if( p.getName().equals( selectedPortfolioName ) )
            {
                selectedPortfolio = p;
            }
        }
        try
        {
            String model = backtestGroup.getSelection().getActionCommand();
            int conf = ConfidenceLevel.values()[cboBacktestConfidence.getSelectedIndex()].getValue();
            int timeHorizon = Integer.valueOf( txtBacktestTimeHorizon.getText() );
            SimulationSetup setup = new SimulationSetup( selectedPortfolio, model, "", conf,
                                                         timeHorizon );
            return setup;
        }
        catch( Exception e )
        {
            showInvalidDataMessage();
            return null;
        }
    }

    /**
     * @return the portfolio selected for stress testing
     */
    public Portfolio getStressTestPortfolio()
    {
        String selectedPortfolioName = (String) cboStressTestPortfolio.getSelectedItem();
        Portfolio selectedPortfolio = new Portfolio();
        for( Portfolio p : portfolios )
        {
            if( p.getName().equals( selectedPortfolioName ) )
            {
                selectedPortfolio = p;
            }
        }

        return selectedPortfolio;
    }

    /**
     * Handles the closure of the application after confirmation.
     */
    private class CloseAction extends AbstractAction
    {
        private static final long serialVersionUID = 1L;

        public CloseAction()
        {
            putValue( NAME, "Exit" );
            putValue( SHORT_DESCRIPTION, "Closes the app" );
        }

        public void actionPerformed( ActionEvent e )
        {
            int optionSelected = JOptionPane.showConfirmDialog( frmApp, "Close the app?",
                                                                "Confirm app closure",
                                                                JOptionPane.YES_NO_OPTION );
            if( optionSelected == 0 )
            {
                frmApp.dispose();
            }
        }
    }

    /**
     * Shows information about the development of the program.
     */
    private class AboutAction extends AbstractAction
    {

        public AboutAction()
        {
            putValue( NAME, "About" );
            putValue( SHORT_DESCRIPTION, "Information about the app" );
        }

        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed( ActionEvent arg0 )
        {
            String message = "Developed by Nishant Mittal\nFinal Year Project 2013-14";
            JOptionPane.showMessageDialog( frmApp, message,
                                           "About Value at Risk Calculator",
                                           JOptionPane.INFORMATION_MESSAGE );
        }

    }

    private void checkAndBuildSimulationSetup()
    {
        try
        {
            String selectedModel = modelType.getSelection().getActionCommand();
            String selectedOptionPricingType = "";
            if( !selectedModel.equals( VarUtils.MB ) )
            {
                selectedOptionPricingType = optionPricingType.getSelection()
                                                             .getActionCommand();
            }
            int selectedIndex = cboConfidenceLevel.getSelectedIndex();
            int confidenceLevel = ConfidenceLevel.values()[selectedIndex].getValue();
            int timeHorizon = Integer.parseInt( txtTimeHorizon.getText() );
            simulationSetup = new SimulationSetup( selectedPortfolio, selectedModel,
                                                   selectedOptionPricingType,
                                                   confidenceLevel, timeHorizon );
        }
        catch( Exception e )
        {
            showInvalidDataMessage();
        }
    }

    /**
     * Shows a message informing the user of incorrect type of data entered in a dialog.
     * 
     * @throws HeadlessException
     */
    private void showInvalidDataMessage() throws HeadlessException
    {
        JOptionPane.showMessageDialog( frmApp,
                                       "Please enter a valid value for each parameter.",
                                       "Invalid Data",
                                       JOptionPane.ERROR_MESSAGE );
    }

    /**
     * Attach the compute var listener to the Compute button.
     * 
     * @param listener
     */
    public void addComputeVaRListener( ActionListener listener )
    {
        btnCompute.addActionListener( listener );
    }

    /**
     * Attach the run backtest listener to the Run button on the backtest panel.
     * 
     * @param l the listener
     */
    public void addBacktestListener( ActionListener l )
    {
        btnRunBacktest.addActionListener( l );
    }

    /**
     * Attach the run stress test listener to the Run button on the stress testing panel.
     * 
     * @param l the listener
     */
    public void addStressTestListener( ActionListener l )
    {
        btnRunStressTest.addActionListener( l );
    }

    /**
     * Updates the view with the final and max var estimations.
     * 
     * @param estimations array containing final and max VaRs from the model
     */
    public void setEstimations( double[] estimations )
    {
        txtVaR.setText( String.valueOf( estimations[0] ) );
        if( estimations[1] == 0 )
        {
            txtMaxVaR.setText( "Not available" );
            return;
        }
        txtMaxVaR.setText( String.valueOf( estimations[1] ) );
    }

    /**
     * Updates the backtesting panel with results of the back test.
     * 
     * @param output the result of the backtest from the model
     */
    public void setBacktestOutput( String output )
    {
        txtBacktestOutput.setText( output );
    }

    /**
     * Updates the stress testing panel with results of the stress test.
     * 
     * @param output the result of the stress test from the model
     */
    public void setStressTestOutput( String output )
    {
        txtStresstestOutput.setText( output );
    }
}
