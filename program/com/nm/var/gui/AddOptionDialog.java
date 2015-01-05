package com.nm.var.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import com.nm.var.src.Asset;
import com.nm.var.src.Option;
import com.nm.var.src.OptionType;
import com.nm.var.src.VarUtils;

/**
 * This dialog allows the user to configure an option based on an existing asset in the portfolio
 * being created.
 */
public class AddOptionDialog extends JDialog
{

    private static final long     serialVersionUID = 1L;
    private final JPanel          contentPanel     = new JPanel();
    /**
     * A list of assets which are contained within the portfolio being created, options can use any
     * of these assets.
     */
    private ArrayList<Asset>      assets           = new ArrayList<Asset>();
    /** A drop-down menu for the currently available assets in the portfolio. */
    private JComboBox<String>     cboAssets;
    /** A text field to input the name of the option. */
    private JTextField            txtOptionName;
    private JLabel                lblName;
    /** The option being created. */
    private Option                option;
    /** Text field to input the start price of the option. */
    private JTextField            txtStartPrice;
    /** Text field to input the strike price of the option. */
    private JTextField            txtStrike;
    /** Text field to input the interest rate of the option. */
    private JTextField            txtInterest;
    /** Text field to input the time to maturity of the option. */
    private JTextField            txtTimeToMaturity;
    /** Text field to input the number of shares held of the asset in the option. */
    private JTextField            txtShares;
    /** Used to keep track of whether an option was successfully added using this dialog. */
    private boolean               optionAdded      = false;
    /** Drop-down menu of possible option types for an option. */
    private JComboBox<OptionType> cboOptionType;
    /**
     * Used as a model for the asset drop-down menu for when there are no assets configured in the
     * portfolio.
     */
    private String[]              noAssets         = { "No assets available" };

    /**
     * Create the dialog using initialise() method in a Runnable so that it executes on the
     * Event-Dispatch thread.
     * 
     * @param parent the component to set the location of this dialog relative to.
     * @param assets the list of assets available for this option to be based on.
     */
    public AddOptionDialog( final Component parent, final ArrayList<Asset> assets )
    {
        EventQueue.invokeLater( new Runnable()
        {
            public void run()
            {
                initialise( parent, assets );
            }
        } );

        this.setModalityType( ModalityType.APPLICATION_MODAL );
        setVisible( true );
    }

    /**
     * Creates the components of the dialog.
     * 
     * @param parent the component to set the location of this dialog relative to.
     * @param assets the list of assets available for this option to be based on.
     * @throws SecurityException
     */
    private void initialise( Component parent, ArrayList<Asset> assets ) throws SecurityException
    {
        setAlwaysOnTop( true );
        setResizable( false );
        setIconImage( Toolkit.getDefaultToolkit()
                             .getImage( AddOptionDialog.class.getResource( "/com/nm/var/gui/icons/application_add.png" ) ) );
        setTitle( "Add Option" );
        setBounds( 100, 100, 397, 231 );
        getContentPane().setLayout( new BorderLayout() );
        contentPanel.setBackground( new Color( 100, 149, 237 ) );
        contentPanel.setBorder( new EmptyBorder( 5, 5, 5, 5 ) );
        getContentPane().add( contentPanel, BorderLayout.CENTER );
        SpringLayout sl_contentPanel = new SpringLayout();
        contentPanel.setLayout( sl_contentPanel );

        JLabel lblTargetAsset = new JLabel( "Target asset:" );
        sl_contentPanel.putConstraint( SpringLayout.NORTH, lblTargetAsset, 10, SpringLayout.NORTH,
                                       contentPanel );
        sl_contentPanel.putConstraint( SpringLayout.EAST, lblTargetAsset, -132, SpringLayout.EAST,
                                       contentPanel );
        contentPanel.add( lblTargetAsset );

        cboAssets = new JComboBox<String>();
        sl_contentPanel.putConstraint( SpringLayout.NORTH, cboAssets, -3, SpringLayout.NORTH,
                                       lblTargetAsset );
        sl_contentPanel.putConstraint( SpringLayout.WEST, cboAssets, 6, SpringLayout.EAST,
                                       lblTargetAsset );
        sl_contentPanel.putConstraint( SpringLayout.EAST, cboAssets, -21, SpringLayout.EAST,
                                       contentPanel );
        cboAssets.setModel( new DefaultComboBoxModel<>( noAssets ) );
        contentPanel.add( cboAssets );
        {
            lblName = new JLabel( "Name:" );
            sl_contentPanel.putConstraint( SpringLayout.NORTH, lblName, 0, SpringLayout.NORTH,
                                           lblTargetAsset );
            contentPanel.add( lblName );
        }

        txtOptionName = new JTextField();
        txtOptionName.setForeground( Color.DARK_GRAY );
        sl_contentPanel.putConstraint( SpringLayout.WEST, txtOptionName, 66, SpringLayout.WEST,
                                       contentPanel );
        sl_contentPanel.putConstraint( SpringLayout.EAST, txtOptionName, -32, SpringLayout.WEST,
                                       lblTargetAsset );
        sl_contentPanel.putConstraint( SpringLayout.EAST, lblName, -6, SpringLayout.WEST,
                                       txtOptionName );
        sl_contentPanel.putConstraint( SpringLayout.NORTH, txtOptionName, -3, SpringLayout.NORTH,
                                       lblTargetAsset );
        contentPanel.add( txtOptionName );
        txtOptionName.setColumns( 10 );

        JPanel pnlSpecs = new JPanel();
        sl_contentPanel.putConstraint( SpringLayout.NORTH, pnlSpecs, 16, SpringLayout.SOUTH,
                                       cboAssets );
        sl_contentPanel.putConstraint( SpringLayout.WEST, pnlSpecs, -5, SpringLayout.WEST,
                                       contentPanel );
        sl_contentPanel.putConstraint( SpringLayout.SOUTH, pnlSpecs, -15, SpringLayout.SOUTH,
                                       contentPanel );
        sl_contentPanel.putConstraint( SpringLayout.EAST, pnlSpecs, 385, SpringLayout.WEST,
                                       contentPanel );
        pnlSpecs.setBorder( new LineBorder( new Color( 255, 255, 255 ), 2 ) );
        pnlSpecs.setBackground( new Color( 100, 149, 237 ) );
        contentPanel.add( pnlSpecs );
        SpringLayout sl_pnlSpecs = new SpringLayout();
        pnlSpecs.setLayout( sl_pnlSpecs );

        JLabel lblS0 = new JLabel( "Start price:" );
        sl_pnlSpecs.putConstraint( SpringLayout.NORTH, lblS0, 10, SpringLayout.NORTH, pnlSpecs );
        sl_pnlSpecs.putConstraint( SpringLayout.WEST, lblS0, 10, SpringLayout.WEST, pnlSpecs );
        pnlSpecs.add( lblS0 );

        JLabel lblX = new JLabel( "Strike:" );
        sl_pnlSpecs.putConstraint( SpringLayout.EAST, lblX, 0, SpringLayout.EAST, lblS0 );
        pnlSpecs.add( lblX );

        JLabel lblR = new JLabel( "Interest:" );
        sl_pnlSpecs.putConstraint( SpringLayout.SOUTH, lblX, -16, SpringLayout.NORTH, lblR );
        sl_pnlSpecs.putConstraint( SpringLayout.WEST, lblR, 20, SpringLayout.WEST, pnlSpecs );
        pnlSpecs.add( lblR );

        JLabel lblT = new JLabel( "Time to maturity:" );
        sl_pnlSpecs.putConstraint( SpringLayout.NORTH, lblT, 0, SpringLayout.NORTH, lblS0 );
        sl_pnlSpecs.putConstraint( SpringLayout.WEST, lblT, 104, SpringLayout.EAST, lblS0 );
        pnlSpecs.add( lblT );

        JLabel lblType = new JLabel( "Option type:" );
        sl_pnlSpecs.putConstraint( SpringLayout.NORTH, lblType, 0, SpringLayout.NORTH, lblX );
        sl_pnlSpecs.putConstraint( SpringLayout.EAST, lblType, 0, SpringLayout.EAST, lblT );
        pnlSpecs.add( lblType );

        cboOptionType = new JComboBox<OptionType>();
        sl_pnlSpecs.putConstraint( SpringLayout.NORTH, cboOptionType, -3, SpringLayout.NORTH, lblX );
        sl_pnlSpecs.putConstraint( SpringLayout.WEST, cboOptionType, 6, SpringLayout.EAST, lblType );
        sl_pnlSpecs.putConstraint( SpringLayout.EAST, cboOptionType, -12, SpringLayout.EAST,
                                   pnlSpecs );
        cboOptionType.setModel( new DefaultComboBoxModel<>( OptionType.values() ) );
        pnlSpecs.add( cboOptionType );

        JLabel lblNumShares = new JLabel( "# shares:" );
        sl_pnlSpecs.putConstraint( SpringLayout.NORTH, lblR, 0, SpringLayout.NORTH, lblNumShares );
        sl_pnlSpecs.putConstraint( SpringLayout.SOUTH, lblNumShares, -10, SpringLayout.SOUTH,
                                   pnlSpecs );
        sl_pnlSpecs.putConstraint( SpringLayout.EAST, lblNumShares, 0, SpringLayout.EAST, lblT );
        pnlSpecs.add( lblNumShares );

        txtStartPrice = new JTextField();
        txtStartPrice.setForeground( Color.DARK_GRAY );
        txtStartPrice.setToolTipText( "e.g. 50.0" );
        sl_pnlSpecs.putConstraint( SpringLayout.WEST, txtStartPrice, 6, SpringLayout.EAST, lblS0 );
        sl_pnlSpecs.putConstraint( SpringLayout.SOUTH, txtStartPrice, 0, SpringLayout.SOUTH, lblS0 );
        pnlSpecs.add( txtStartPrice );
        txtStartPrice.setColumns( 10 );

        txtStrike = new JTextField();
        txtStrike.setForeground( Color.DARK_GRAY );
        txtStrike.setToolTipText( "e.g. 80.0" );
        sl_pnlSpecs.putConstraint( SpringLayout.SOUTH, txtStrike, 0, SpringLayout.SOUTH, lblX );
        sl_pnlSpecs.putConstraint( SpringLayout.EAST, txtStrike, 0, SpringLayout.EAST,
                                   txtStartPrice );
        pnlSpecs.add( txtStrike );
        txtStrike.setColumns( 10 );

        txtInterest = new JTextField();
        txtInterest.setForeground( Color.DARK_GRAY );
        txtInterest.setToolTipText( "as decimal % e.g. 0.07" );
        sl_pnlSpecs.putConstraint( SpringLayout.SOUTH, txtInterest, 0, SpringLayout.SOUTH, lblR );
        sl_pnlSpecs.putConstraint( SpringLayout.EAST, txtInterest, 0, SpringLayout.EAST,
                                   txtStartPrice );
        pnlSpecs.add( txtInterest );
        txtInterest.setColumns( 10 );

        txtTimeToMaturity = new JTextField();
        txtTimeToMaturity.setForeground( Color.DARK_GRAY );
        sl_pnlSpecs.putConstraint( SpringLayout.WEST, txtTimeToMaturity, 6, SpringLayout.EAST, lblT );
        txtTimeToMaturity.setToolTipText( "in days" );
        sl_pnlSpecs.putConstraint( SpringLayout.SOUTH, txtTimeToMaturity, -9, SpringLayout.NORTH,
                                   cboOptionType );
        sl_pnlSpecs.putConstraint( SpringLayout.EAST, txtTimeToMaturity, 0, SpringLayout.EAST,
                                   cboOptionType );
        pnlSpecs.add( txtTimeToMaturity );
        txtTimeToMaturity.setColumns( 10 );

        txtShares = new JTextField();
        txtShares.setForeground( Color.DARK_GRAY );
        sl_pnlSpecs.putConstraint( SpringLayout.NORTH, txtShares, -3, SpringLayout.NORTH, lblR );
        sl_pnlSpecs.putConstraint( SpringLayout.WEST, txtShares, 0, SpringLayout.WEST,
                                   cboOptionType );
        sl_pnlSpecs.putConstraint( SpringLayout.EAST, txtShares, -2, SpringLayout.EAST,
                                   cboOptionType );
        pnlSpecs.add( txtShares );
        txtShares.setColumns( 10 );
        {
            JPanel buttonPane = new JPanel();
            buttonPane.setBackground( new Color( 100, 149, 237 ) );
            buttonPane.setLayout( new FlowLayout( FlowLayout.RIGHT ) );
            getContentPane().add( buttonPane, BorderLayout.SOUTH );
            {
                JButton okButton = new JButton( "OK" );
                okButton.addActionListener( new ActionListener()
                {
                    public void actionPerformed( ActionEvent e )
                    {
                        checkAndSaveOption();
                    }
                } );
                okButton.setIcon( new ImageIcon(
                                                 AddAssetDialog.class.getResource( "/com/nm/var/gui/icons/tick.png" ) ) );
                okButton.setActionCommand( "OK" );
                buttonPane.add( okButton );
                getRootPane().setDefaultButton( okButton );
            }
            {
                JButton cancelButton = new JButton( "Cancel" );
                cancelButton.addActionListener( new ActionListener()
                {
                    public void actionPerformed( ActionEvent e )
                    {
                        optionAdded = false;
                        close();
                    }
                } );
                cancelButton.setIcon( new ImageIcon(
                                                     AddAssetDialog.class.getResource( "/com/nm/var/gui/icons/cross.png" ) ) );
                cancelButton.setActionCommand( "Cancel" );
                buttonPane.add( cancelButton );
            }
        }
        setAssets( assets );
        setDefaultCloseOperation( JDialog.DISPOSE_ON_CLOSE );
        setLocationRelativeTo( parent );
    }

    /** Closes and disposes of all resources of this dialog. */
    protected void close()
    {
        this.dispose();
    }
    
    /**
     * When the OK button is clicked, verifies all required data has been provided and builds an
     * Option object using it.
     */
    protected void checkAndSaveOption() //TODO split into check then save where called.
    {
        boolean problem = false;
        double startPrice = 0, strike = 0, interest = 0, volatility = 0;
        int numShares = 0, timeToMaturity = 0, optionType = 0;
        String assetID = "";
        String name = "Option: " + txtOptionName.getText();
        File data = null;
        Asset optionAsset;
        try
        {
            if( !name.equals( "Option: " ) )
            {
                assetID = (String) cboAssets.getSelectedItem();
                startPrice = Double.parseDouble( txtStartPrice.getText() );
                strike = Double.parseDouble( txtStrike.getText() );
                interest = Double.parseDouble( txtInterest.getText() );
                numShares = Integer.parseInt( txtShares.getText() );
                timeToMaturity = Integer.parseInt( txtTimeToMaturity.getText() );
                optionAsset = assets.get( cboAssets.getSelectedIndex() );
                data = optionAsset.getData();
                volatility = VarUtils.computeVolatility_Standard( VarUtils.getReturnsFromFile( data ) );
            }
            else
            {
                problem = true;
            }
        }
        catch( Exception e )
        {
            if( e instanceof NegativeArraySizeException )
            {
                JOptionPane.showMessageDialog( this,
                                               "Please ensure the file contains stock price data.",
                                               "Error", JOptionPane.ERROR_MESSAGE );
            }
            problem = true;
        }

        if( problem == true )
        {
            JOptionPane.showMessageDialog( this, "Please enter a valid value for every field.",
                                           "Missing or Incorrect Data", JOptionPane.WARNING_MESSAGE );
        }
        else
        {
            option = new Option( startPrice, numShares, strike, interest, volatility,
                                 timeToMaturity, assetID, optionType, data, name );
            optionAdded = true;
            this.dispose();
        }
    }
    
    /**
     * Sets the list of usable assets for the option being created.
     * @param assets list of currently configured assets in the portfolio.
     */
    public void setAssets( ArrayList<Asset> assets )
    {
        this.assets = assets;
        if( this.assets.isEmpty() )
        {
            cboAssets.setModel( new DefaultComboBoxModel<>( noAssets ) );
        }
        else
        {
            String[] assetIDs = new String[this.assets.size()];
            for( int i = 0 ; i < assetIDs.length ; i++ )
            {
                assetIDs[i] = this.assets.get( i ).getID();
            }
            cboAssets.setModel( new DefaultComboBoxModel<>( assetIDs ) );
            cboAssets.setSelectedIndex( 0 );
        }
    }

    /**
     * @return the option created using this dialog if it was added successfully.
     */
    public Option getCreatedOption()
    {
        return option;
    }
    
    /**
     * @return whether an option was successfully added using this dialog.
     */
    public boolean optionAdded()
    {
        return optionAdded;
    }

}
