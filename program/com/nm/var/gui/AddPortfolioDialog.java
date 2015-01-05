package com.nm.var.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import com.nm.var.src.Asset;
import com.nm.var.src.Option;
import com.nm.var.src.Portfolio;

/**
 * This dialog allows the user to create a new portfolio by adding assets and options to it.
 * Also provides removal of assets and options from the portfolio being created.
 * 
 */
public class AddPortfolioDialog extends JDialog
{

    //TODO fix portfolio contents deletion, use one list?
    private static final long serialVersionUID = 1L;

    private final JPanel      contentPanel     = new JPanel();
    /** Text field to input the name of the portfolio. */
    private JTextField        txtPortfolioName;
    private JLabel            lblNewLabel;
    /** Button to show the Add Asset Dialog. */
    private JButton           btnAddAsset;
    /** Button to show the Add Option Dialog. */
    private JButton           btnAddOption;
    private JPanel            pnlContents;
    /** The portfolio being configured using this dialog. */
    private Portfolio         portfolio        = new Portfolio();
    /** List of assets in this portfolio. */
    private ArrayList<Asset>  assets           = new ArrayList<Asset>();
    /** List of options in this portfolio. */
    private ArrayList<Option> options          = new ArrayList<Option>();
    /** List of strings used to show the assets and options configured in this portfolio. */
    private JList<String>     listPortfolioContents;
    /** Used to keep track of whether a portfolio was added successfully using this dialog. */
    private boolean           portfolioAdded   = false;
    /** Used to keep track of the last directory where an asset price file was added from. */
    private File              lastDirectory    = new File( "" );
    /** Button to handle the deletion of items in the portfolio. */
    private JButton           btnDeleteListItem;
    /** Used to set the location of this dialog relative to this component. */
    private Component         parentDialog;

    /**
     * Create the dialog.
     */
    public AddPortfolioDialog( Component parent )
    {
        this.parentDialog = parent;
        setAlwaysOnTop( true );
        setIconImage( Toolkit.getDefaultToolkit()
                             .getImage( AddPortfolioDialog.class.getResource( "/com/nm/var/gui/icons/application_add.png" ) ) );
        setResizable( false );
        setType( Type.POPUP );
        setTitle( "Add Portfolio" );
        setBounds( 100, 100, 444, 254 );
        getContentPane().setLayout( new BorderLayout() );
        contentPanel.setBackground( new Color( 100, 149, 237 ) );
        contentPanel.setBorder( new EmptyBorder( 5, 5, 5, 5 ) );
        getContentPane().add( contentPanel, BorderLayout.CENTER );
        SpringLayout sl_contentPanel = new SpringLayout();
        contentPanel.setLayout( sl_contentPanel );
        {
            lblNewLabel = new JLabel( "Name:" );
            lblNewLabel.setForeground( Color.WHITE );
            sl_contentPanel.putConstraint( SpringLayout.EAST, lblNewLabel, 51, SpringLayout.WEST,
                                           contentPanel );
            lblNewLabel.setFont( new Font( "Tahoma", Font.BOLD, 12 ) );
            sl_contentPanel.putConstraint( SpringLayout.NORTH, lblNewLabel, 10, SpringLayout.NORTH,
                                           contentPanel );
            sl_contentPanel.putConstraint( SpringLayout.WEST, lblNewLabel, 10, SpringLayout.WEST,
                                           contentPanel );
            contentPanel.add( lblNewLabel );
        }
        {
            txtPortfolioName = new JTextField();
            sl_contentPanel.putConstraint( SpringLayout.NORTH, txtPortfolioName, -2,
                                           SpringLayout.NORTH, lblNewLabel );
            sl_contentPanel.putConstraint( SpringLayout.WEST, txtPortfolioName, 6,
                                           SpringLayout.EAST, lblNewLabel );
            txtPortfolioName.setForeground( new Color( 0, 0, 0 ) );
            contentPanel.add( txtPortfolioName );
            txtPortfolioName.setColumns( 10 );
        }
        {
            btnAddAsset = new JButton( "Asset" );
            sl_contentPanel.putConstraint( SpringLayout.EAST, txtPortfolioName, -37,
                                           SpringLayout.WEST, btnAddAsset );
            sl_contentPanel.putConstraint( SpringLayout.NORTH, btnAddAsset, 10, SpringLayout.NORTH,
                                           contentPanel );
            sl_contentPanel.putConstraint( SpringLayout.WEST, btnAddAsset, 240, SpringLayout.WEST,
                                           contentPanel );
            btnAddAsset.addActionListener( new ActionListener()
            {
                public void actionPerformed( ActionEvent e )
                {
                    addAssetToPortfolio();
                }
            } );
            btnAddAsset.setHorizontalAlignment( SwingConstants.LEFT );
            btnAddAsset.setMnemonic( '+' );
            btnAddAsset.setIcon( new ImageIcon(
                                                AddPortfolioDialog.class.getResource( "/com/nm/var/gui/icons/add.png" ) ) );
            contentPanel.add( btnAddAsset );
        }
        {
            btnAddOption = new JButton( "Option" );
            btnAddOption.addActionListener( new ActionListener()
            {
                public void actionPerformed( ActionEvent arg0 )
                {
                    addOptionToPortfolio();
                }
            } );
            btnAddOption.setHorizontalAlignment( SwingConstants.LEFT );
            sl_contentPanel.putConstraint( SpringLayout.EAST, btnAddAsset, -6, SpringLayout.WEST,
                                           btnAddOption );
            sl_contentPanel.putConstraint( SpringLayout.NORTH, btnAddOption, 10,
                                           SpringLayout.NORTH, contentPanel );
            sl_contentPanel.putConstraint( SpringLayout.EAST, btnAddOption, -10, SpringLayout.EAST,
                                           contentPanel );
            btnAddOption.setMnemonic( '+' );
            btnAddOption.setIcon( new ImageIcon(
                                                 AddPortfolioDialog.class.getResource( "/com/nm/var/gui/icons/add.png" ) ) );
            contentPanel.add( btnAddOption );
        }
        {
            pnlContents = new JPanel();
            sl_contentPanel.putConstraint( SpringLayout.NORTH, pnlContents, 5, SpringLayout.SOUTH,
                                           btnAddAsset );
            sl_contentPanel.putConstraint( SpringLayout.WEST, pnlContents, 10, SpringLayout.WEST,
                                           contentPanel );
            sl_contentPanel.putConstraint( SpringLayout.SOUTH, pnlContents, -3, SpringLayout.SOUTH,
                                           contentPanel );
            pnlContents.setBorder( new TitledBorder( new LineBorder( new Color( 255, 255, 255 ) ),
                                                     "Portfolio Contents", TitledBorder.LEADING,
                                                     TitledBorder.TOP, null, new Color( 255, 255,
                                                                                        255 ) ) );
            pnlContents.setBackground( new Color( 100, 149, 237 ) );
            contentPanel.add( pnlContents );
            pnlContents.setLayout( null );
            {
                listPortfolioContents = new JList<String>();
                listPortfolioContents.setBounds( 6, 16, 303, 110 );
                listPortfolioContents.setForeground( Color.WHITE );
                listPortfolioContents.setBackground( Color.GRAY );
                listPortfolioContents.setBorder( null );
                listPortfolioContents.setValueIsAdjusting( true );
                listPortfolioContents.setToolTipText( "Contents of the portfolio" );
                pnlContents.add( listPortfolioContents );
            }
        }
        {
            btnDeleteListItem = new JButton( "Delete" );
            sl_contentPanel.putConstraint( SpringLayout.EAST, pnlContents, -4, SpringLayout.WEST,
                                           btnDeleteListItem );
            sl_contentPanel.putConstraint( SpringLayout.NORTH, btnDeleteListItem, 16,
                                           SpringLayout.SOUTH, btnAddOption );
            sl_contentPanel.putConstraint( SpringLayout.WEST, btnDeleteListItem, 0,
                                           SpringLayout.WEST, btnAddOption );
            btnDeleteListItem.addActionListener( new ActionListener()
            {
                public void actionPerformed( ActionEvent e )
                {
                    confirmAndDeleteSelection();
                    updateDeleteButton();
                }
            } );
            btnDeleteListItem.setToolTipText( "Remove selected item from portfolio" );
            btnDeleteListItem.setMnemonic( '-' );
            btnDeleteListItem.setIcon( new ImageIcon(
                                                      AddPortfolioDialog.class.getResource( "/com/nm/var/gui/icons/delete.png" ) ) );
            btnDeleteListItem.setEnabled( false );
            contentPanel.add( btnDeleteListItem );
        }
        {
            JPanel buttonPane = new JPanel();
            buttonPane.setBackground( new Color( 100, 149, 237 ) );
            buttonPane.setLayout( new FlowLayout( FlowLayout.RIGHT ) );
            getContentPane().add( buttonPane, BorderLayout.SOUTH );
            {
                JButton okButton = new JButton( "OK" );
                okButton.setIcon( new ImageIcon(
                                                 AddPortfolioDialog.class.getResource( "/com/nm/var/gui/icons/tick.png" ) ) );
                okButton.setActionCommand( "OK" );
                buttonPane.add( okButton );
                getRootPane().setDefaultButton( okButton );
                okButton.addActionListener( new ActionListener()
                {
                    @Override
                    public void actionPerformed( ActionEvent e )
                    {
                        if( save() )
                        {
                            close();
                        }
                    }
                } );
            }
            {
                JButton cancelButton = new JButton( "Cancel" );
                cancelButton.setIcon( new ImageIcon(
                                                     AddPortfolioDialog.class.getResource( "/com/nm/var/gui/icons/cross.png" ) ) );
                cancelButton.addActionListener( new ActionListener()
                {
                    public void actionPerformed( ActionEvent arg0 )
                    {
                        close();
                    }
                } );
                cancelButton.setActionCommand( "Cancel" );
                buttonPane.add( cancelButton );
            }
        }
        this.setDefaultCloseOperation( JDialog.DISPOSE_ON_CLOSE );
        setLocationRelativeTo( parent );
        this.setModalityType( ModalityType.APPLICATION_MODAL );
        this.setVisible( true );
        // pack();
        updatePortfolioContents();
        updateDeleteButton();
    }

    /**
     * Confirms and deletes the selected item from the portfolio and updates the contents of the
     * portfolio.
     */
    protected void confirmAndDeleteSelection()
    {
        // get selected item name
        String selectedValue = listPortfolioContents.getSelectedValue();

        // confirm
        String question = "Remove " + selectedValue
                          + " from portfolio?\nRemoving an asset will also remove associated options.";
        int action = JOptionPane.showConfirmDialog( this, question, "Confirm Portfolio Change",
                                                    JOptionPane.OK_CANCEL_OPTION,
                                                    JOptionPane.QUESTION_MESSAGE );
        if( action == JOptionPane.OK_OPTION )
        {
            // remove selected item from portfolio
            if( selectedValue.startsWith( "Option" ) )
            {
                Option optionToRemove = null;
                for( Option o : options )
                {
                    if( o.getName().equals( selectedValue ) )
                    {
                        optionToRemove = o;
                    }
                }
                options.remove( optionToRemove );
            }
            else
            {
                Asset assetToRemove = null;
                for( Asset a : assets )
                {
                    if( a.getID().equals( selectedValue ) )
                    {
                        assetToRemove = a;
                    }
                }
                Option optionToRemove = null;
                for( Option o : options )
                {
                    if( o.getStockID().equals( assetToRemove.getID() ) )
                    {
                        optionToRemove = o;
                    }
                }
                assets.remove( assetToRemove );
                options.remove( optionToRemove );
             // TODO FIX PORTFOLIO ITEMS NOT BEING REMOVED PROPERLY - USE ONE LIST ONLY
            }
            // update portfolio
            portfolio = new Portfolio( assets, options );
            // update list model
            updatePortfolioContents();
        }
    }
    
    /**
     * When the OK button is clicked, verifies all required data has been provided and builds a
     * Portfolio object using it.
     */
    private boolean save()
    {
        // check portfolio has a name and list of assets and options is not empty
        String name = txtPortfolioName.getText();

        if( name.isEmpty() )
        {
            JOptionPane.showMessageDialog( this, "Please enter a name for the portfolio",
                                           "Missing Portfolio Name", JOptionPane.WARNING_MESSAGE );
            return false;
        }

        if( assets.isEmpty() )
        {
            JOptionPane.showMessageDialog( this,
                                           "Please add some assets to the portfolio.",
                                           "Missing Data", JOptionPane.WARNING_MESSAGE );
            return false;
        }

        if( assets.isEmpty() && options.isEmpty() )
        {
            JOptionPane.showMessageDialog( this,
                                           "Please add some assets or options to the portfolio.",
                                           "Missing Data", JOptionPane.WARNING_MESSAGE );
            return false;
        }

        portfolio.setName( txtPortfolioName.getText() );
        portfolioAdded = true;
        return true;
    }
    
    /**
     * Closes the dialog, but confirms if the user is in the process of creating a portfolio.
     */
    private void close()
    {
        if( portfolioAdded )
        {
            this.dispose();
        }
        else
        {
            int response = JOptionPane.showConfirmDialog( this,
                                                          "Are you sure you wish to cancel creating a portfolio?",
                                                          "Cancel Portfolio Creation",
                                                          JOptionPane.YES_NO_OPTION );
            if( response == JOptionPane.YES_OPTION )
            {
                this.dispose();
            }
        }
    }
    /**
     * Updates the availability of the Delete button.
     */
    private void updateDeleteButton()
    {
        if( assets.isEmpty() && options.isEmpty() )
        {
            btnDeleteListItem.setEnabled( false );
        }
        else
        {
            btnDeleteListItem.setEnabled( true );
        }
    }
    /**
     * Adds an asset to the portfolio using the Add Asset dialog.
     * Updates the portfolio contents.
     */
    public void addAssetToPortfolio()
    {
        EventQueue.invokeLater( new Runnable()
        {
            public void run()
            {
                AddAssetDialog dlg = new AddAssetDialog( parentDialog, lastDirectory );
                if( dlg.assetAdded() )
                {
                    Asset createdAsset = dlg.getCreatedAsset();
                    lastDirectory = createdAsset.getData().getParentFile();
                    assets.add( createdAsset );
                    portfolio.addAsset( createdAsset );
                    updatePortfolioContents();
                }
            }
        } );

    }
    /**
     * Adds an option to the portfolio using the Add Option dialog.
     * Updates the portfolio contents.
     */
    private void addOptionToPortfolio()
    {
        SwingWorker<Option, Void> worker = new SwingWorker<Option, Void>()
        {
            @Override
            protected Option doInBackground() throws Exception
            {
                AddOptionDialog dlg = new AddOptionDialog( parentDialog, assets );
                if( dlg.optionAdded() )
                {
                    Option createdOption = dlg.getCreatedOption();
                    options.add( createdOption );
                    portfolio.addOption( dlg.getCreatedOption() );
                    updatePortfolioContents();
                    return createdOption;
                }
                else
                {
                    return null;
                }
            }
        };
        worker.run();
    }
    /**
     * @return the portfolio created using this dialog.
     */
    public Portfolio getCreatedPortfolio()
    {
        return portfolio;
    }
    /**
     * Updates the portfolio contents with the names of the assets and options inside it.
     */
    private void updatePortfolioContents()
    {
        ArrayList<String> contents = new ArrayList<String>();
        for( Asset a : assets )
        {
            contents.add( a.getID() );
        }
        for( Option a : options )
        {
            contents.add( a.getName() );
        }

        DefaultListModel<String> model = new DefaultListModel<String>();
        for( String s : contents )
        {
            model.addElement( s );
        }

        listPortfolioContents.setModel( model );
        updateDeleteButton();
    }
    /**
     * @return whether a portfolio has been successfully added using this dialog.
     */
    public boolean portfolioAdded()
    {
        return portfolioAdded;
    }
}
