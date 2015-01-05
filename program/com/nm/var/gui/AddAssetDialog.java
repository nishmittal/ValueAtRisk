package com.nm.var.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;

import com.nm.var.src.Asset;

import java.awt.Toolkit;
import java.awt.Color;

/**
 * This dialog allows the user to add an asset to a portfolio being created by specifying the
 * historical stock price data file, amount of investment made in the asset and an identifier (name)
 * for the asset.
 */
public class AddAssetDialog extends JDialog
{
    /**
     * The asset which is to be configured using this dialog.
     */
    private Asset             asset;
    /** Keeps track of whether an asset was successfully added using this dialog. */
    private Boolean           assetAdded       = false;
    private static final long serialVersionUID = 1L;
    private final JPanel      contentPanel     = new JPanel();
    private JLabel            lblStockData;
    private JLabel            lblId;
    private JLabel            lblInvest;
    /** Text field containing the file path for the historical price data file. */
    private JTextField        txtStockData;
    /** Text field containing the name/identifier of the asset. */
    private JTextField        txtID;
    /** Text field containing the amount of investment made in the asset. */
    private JTextField        txtInvestment;
    /** The historical stock price data file selected by the user for the asset. */
    private File              selectedFile;
    /** The FileChooser shown when the user needs to select a stock price data file. */
    private JFileChooser      chooser;
    /**
     * Used to keep track of the last directory used for a stock price data file so that the dialog
     * can open the same directory next time to make selection of files easier.
     */
    private File              lastDirectory    = new File( "" );

    /**
     * Create the dialog.
     */
    public AddAssetDialog( final Component parent, File lastDirectory )
    {
        this.lastDirectory = lastDirectory;
        EventQueue.invokeLater( new Runnable()
        {
            public void run()
            {
                initialise( parent );
            }
        } );
        setModalityType( ModalityType.APPLICATION_MODAL );
        setVisible( true );
    }

    /**
     * Initialises all the components of the dialog.
     * 
     * @param parent the component to set this dialog's location relative to.
     * @throws SecurityException
     */
    private void initialise( Component parent ) throws SecurityException
    {
        setAlwaysOnTop( true );
        setResizable( false );
        setIconImage( Toolkit.getDefaultToolkit()
                             .getImage( AddAssetDialog.class.getResource( "/com/nm/var/gui/icons/application_add.png" ) ) );
        setTitle( "Add Asset" );
        setBounds( 100, 100, 419, 161 );
        getContentPane().setLayout( new BorderLayout() );
        contentPanel.setBackground( new Color( 100, 149, 237 ) );
        contentPanel.setBorder( new EmptyBorder( 5, 5, 5, 5 ) );
        getContentPane().add( contentPanel, BorderLayout.CENTER );
        SpringLayout sl_contentPanel = new SpringLayout();
        contentPanel.setLayout( sl_contentPanel );
        {
            lblStockData = new JLabel( "Stock data:" );
            lblStockData.setHorizontalAlignment( SwingConstants.RIGHT );
            sl_contentPanel.putConstraint( SpringLayout.NORTH, lblStockData, 10, SpringLayout.NORTH,
                                           contentPanel );
            sl_contentPanel.putConstraint( SpringLayout.WEST, lblStockData, 10, SpringLayout.WEST,
                                           contentPanel );
            contentPanel.add( lblStockData );
        }
        {
            lblId = new JLabel( "ID:" );
            lblId.setHorizontalAlignment( SwingConstants.RIGHT );
            contentPanel.add( lblId );
        }
        {
            lblInvest = new JLabel( "Investment:" );
            sl_contentPanel.putConstraint( SpringLayout.NORTH, lblId, 0,
                                           SpringLayout.NORTH, lblInvest );
            sl_contentPanel.putConstraint( SpringLayout.NORTH, lblInvest, 21,
                                           SpringLayout.SOUTH, lblStockData );
            sl_contentPanel.putConstraint( SpringLayout.WEST, lblInvest, 0, SpringLayout.WEST,
                                           lblStockData );
            lblInvest.setHorizontalAlignment( SwingConstants.RIGHT );
            contentPanel.add( lblInvest );
        }
        {
            txtStockData = new JTextField();
            txtStockData.setForeground( new Color( 0, 0, 0 ) );
            sl_contentPanel.putConstraint( SpringLayout.NORTH, txtStockData, 9, SpringLayout.NORTH,
                                           contentPanel );
            sl_contentPanel.putConstraint( SpringLayout.WEST, txtStockData, 11, SpringLayout.EAST,
                                           lblStockData );
            contentPanel.add( txtStockData );
            txtStockData.setColumns( 10 );
        }
        {
            txtID = new JTextField();
            txtID.setForeground( new Color( 0, 0, 0 ) );
            sl_contentPanel.putConstraint( SpringLayout.NORTH, txtID, -3, SpringLayout.NORTH,
                                           lblId );
            sl_contentPanel.putConstraint( SpringLayout.WEST, txtID, 8, SpringLayout.EAST,
                                           lblId );
            sl_contentPanel.putConstraint( SpringLayout.EAST, txtID, -127, SpringLayout.EAST,
                                           contentPanel );
            contentPanel.add( txtID );
            txtID.setColumns( 10 );
        }
        {
            // TODO validate numeric input
            txtInvestment = new JTextField();
            txtInvestment.setForeground( new Color( 0, 0, 0 ) );
            sl_contentPanel.putConstraint( SpringLayout.WEST, lblId, 10, SpringLayout.EAST,
                                           txtInvestment );
            sl_contentPanel.putConstraint( SpringLayout.NORTH, txtInvestment, -3,
                                           SpringLayout.NORTH, lblInvest );
            sl_contentPanel.putConstraint( SpringLayout.WEST, txtInvestment, 6, SpringLayout.EAST,
                                           lblInvest );
            contentPanel.add( txtInvestment );
            txtInvestment.setColumns( 10 );
        }
        {
            JButton btnBrowse = new JButton( "Browse" );
            sl_contentPanel.putConstraint( SpringLayout.EAST, txtStockData, -6, SpringLayout.WEST,
                                           btnBrowse );
            sl_contentPanel.putConstraint( SpringLayout.NORTH, btnBrowse, -5, SpringLayout.NORTH,
                                           lblStockData );
            sl_contentPanel.putConstraint( SpringLayout.EAST, btnBrowse, 0, SpringLayout.EAST,
                                           contentPanel );
            btnBrowse.addActionListener( new ActionListener()
            {
                public void actionPerformed( ActionEvent e )
                {
                    showFileBrowser();
                }
            } );
            btnBrowse.setIcon( new ImageIcon(
                                              AddAssetDialog.class.getResource( "/com/nm/var/gui/icons/magnifier.png" ) ) );
            contentPanel.add( btnBrowse );
        }
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
                        checkAndSaveAsset();
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
                        close();
                    }
                } );
                cancelButton.setIcon( new ImageIcon(
                                                     AddAssetDialog.class.getResource( "/com/nm/var/gui/icons/cross.png" ) ) );
                cancelButton.setActionCommand( "Cancel" );
                buttonPane.add( cancelButton );
            }
        }
        setDefaultCloseOperation( JDialog.DISPOSE_ON_CLOSE );
        setLocationRelativeTo( parent );
    }

    /** Closes the dialog and disposes of all of its contents. */
    protected void close()
    {
        this.dispose();
    }

    /**
     * When the OK button is clicked, verifies all required data has been provided and builds an
     * Asset object using it.
     */
    protected void checkAndSaveAsset() //TODO split into if (checkAsset) saveAsset where called.
    {
        String stockData = txtStockData.getText();
        String id = "Asset: " + txtID.getText();
        String investment = txtInvestment.getText();
        if( stockData.equals( "" ) || id.equals( "" ) || investment.equals( "" ) )
        {
            JOptionPane.showMessageDialog( this, "Please enter a value for every field.",
                                           "Missing Data", JOptionPane.WARNING_MESSAGE );
        }
        else
        {
            File stockFile = chooser.getSelectedFile();
            createAsset( stockFile, id, Double.valueOf( investment ) );
            assetAdded = true;
            this.dispose();
        }
    }

    /**
     * Creates the asset using user-specified settings in the dialog once they have been validated.
     * 
     * @param data file containing historical stock prices
     * @param id identifier/name for the asset
     * @param investment amount of money invested int this asset
     */
    private void createAsset( File data, String id, Double investment )
    {
        asset = new Asset( data, id, investment );
    }

    /**
     * @return the asset created through this dialog.
     */
    public Asset getCreatedAsset()
    {
        return asset;
    }

    /**
     * Shows the file chooser for the user to select some file containing historical price data for
     * an asset.
     */
    private void showFileBrowser()
    {
        FileFilter filter = new FileFilter()
        {
            @Override
            public String getDescription()
            {
                return "CSV (containing historial stock prices)";
            }

            @Override
            public boolean accept( File f )
            {
                if( f.isDirectory() )
                {
                    return true;
                }

                String absolutePath = f.getAbsolutePath();
                if( absolutePath.endsWith( ".csv" ) )
                {
                    return true;
                }

                return false;
            }
        };
        chooser = new JFileChooser( lastDirectory );
        chooser.setFileFilter( filter );
        int action = chooser.showDialog( this, "Select" );
        switch( action )
        {
            case ( JFileChooser.APPROVE_OPTION ):
                selectedFile = chooser.getSelectedFile();
                txtStockData.setText( selectedFile.getPath() );
                break;
            default:
                break;
        }
    }

    /**
     * @return whether an asset was successfully added through this dialog.
     */
    public Boolean assetAdded()
    {
        return assetAdded;
    }

    /**
     * Used to keep track of the last directory used to select the historical stock price data file
     * so that it can be automatically shown when the user next selects a file for an asset.
     * 
     * @param dir the last directory used to specify the price data in creating an asset.
     */
    public void setLastDirectory( File dir )
    {
        this.lastDirectory = dir;
    }
}
