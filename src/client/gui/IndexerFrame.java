/**
 * 
 */
package client.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import client.communication.*;
import client.gui.batchstate.BatchState;
import client.gui.batchstate.BatchStateListener;
import client.gui.batchstate.Cell;
import client.gui.dialog.*;
import client.gui.panel.*;

/**
 * @author tchambs
 * 
 */
@SuppressWarnings("serial")
public class IndexerFrame extends JFrame implements BatchStateListener{

	
	public int port = 39460;
	public String hostname = "localhost";
	
	BatchState batchState;
	
	private LoginDialog loginDialog;
	private DownloadBatchDialog downloadBatchDialog;
	
	JMenuItem exitMenuItem;
	JMenuItem downloadBatchMenuItem;
	JMenuItem logoutMenuItem;
	
	ButtonPanel buttonPanel;
	ImagePanel imagePanel;
	EntryPanel BLentryPanel;
	EntryPanel BRentryPanel;
	TableEntryPanel tableEntryPanel;
	FormEntryPanel formEntryPanel;
	FieldHelpPanel fieldHelpPanel;
	ImageNavigatorPanel imageNavigatorPanel;
	
	JSplitPane horizontalSplit;
	JSplitPane verticalSplit;
	
	IndexerFrame(String title, String[] args) {
		super(title);
		
		this.hostname = args[0];
		this.port = Integer.parseInt(args[1]);
	
		this.addWindowListener(windowAdapter);
		
		batchState = new BatchState(hostname, port);
 	   	batchState.addListener(this);
		
		loginDialog = new LoginDialog(batchState, this);
		
		createComponents();
	}
	
	private void createComponents() {
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu menu = new JMenu("File");
		menu.setMnemonic('c');
		menuBar.add(menu);
		
		downloadBatchMenuItem = new JMenuItem("Download Batch");
		downloadBatchMenuItem.addActionListener(actionListener);
		menu.add(downloadBatchMenuItem);
		
		logoutMenuItem = new JMenuItem("Logout");
		logoutMenuItem.addActionListener(actionListener);
		menu.add(logoutMenuItem);
		
		exitMenuItem = new JMenuItem("Exit");
		exitMenuItem.addActionListener(actionListener);
		menu.add(exitMenuItem);
		
		buttonPanel = new ButtonPanel(this);
		
		imagePanel = new ImagePanel();
		imagePanel.setPreferredSize(new Dimension(800, 500));
		imagePanel.setMinimumSize(new Dimension(300, 250));
		
		tableEntryPanel = new TableEntryPanel();
		formEntryPanel = new FormEntryPanel();
		fieldHelpPanel = new FieldHelpPanel();
		imageNavigatorPanel = new ImageNavigatorPanel();
		
		BLentryPanel = new EntryPanel();
		BLentryPanel.setMinimumSize(new Dimension(300,100));
		BLentryPanel.setPreferredSize(new Dimension(400,200));
		BLentryPanel.addTab("Table Entry", tableEntryPanel);
		BLentryPanel.addTab("Form Entry", formEntryPanel);
		
		BRentryPanel = new EntryPanel();
		BRentryPanel.setMinimumSize(new Dimension(200,100));
		BRentryPanel.setPreferredSize(new Dimension(400,200));
		BRentryPanel.addTab("Field Help", fieldHelpPanel);
		BRentryPanel.addTab("Image Navigator", imageNavigatorPanel);
		
		horizontalSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, BLentryPanel, BRentryPanel);
		horizontalSplit.setPreferredSize(new Dimension(100,100));
		
		verticalSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, imagePanel, horizontalSplit);
		
		
		JPanel rootPanel = new JPanel();
		rootPanel.setLayout(new BorderLayout());
		
		rootPanel.add(buttonPanel, BorderLayout.NORTH);
		rootPanel.add(verticalSplit, BorderLayout.CENTER);
		
		this.add(rootPanel);
	}
	
	private ActionListener actionListener = new ActionListener() {

		public void actionPerformed(ActionEvent e) {

			if (e.getSource() == logoutMenuItem) {
				logout();
			} else if (e.getSource() == exitMenuItem) {
				System.exit(0);
			} else if (e.getSource() == downloadBatchMenuItem) {
				downloadBatch();
			}
		}

	};
	
	private WindowAdapter windowAdapter = new WindowAdapter() {

		public void windowClosing(WindowEvent e) {
			System.exit(0);
		}
	};
	
	private void logout() {
		
		//save state
		
		//return to login dialog
		
		this.setVisible(false);
		loginDialog = new LoginDialog(batchState, this);
	}
	
	private void downloadBatch() {
		downloadBatchDialog = new DownloadBatchDialog(batchState);
	}
	
	public static void main(final String[] args) {

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				
				IndexerFrame frame;
				if (args.length == 2) {
					frame = new IndexerFrame("Record Indexer", args);
				} else {
					String[] defaultArgs = {"localhost", Integer.toString(39640)};
					frame = new IndexerFrame("Record Indexer", defaultArgs);
				}
				
				//frame.pack();
				frame.setSize(800,800);
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
			}
		});
	}

	/**
	 * @return
	 */
	public BatchState getBatchState() {
		return batchState;
	}

	/* (non-Javadoc)
	 * @see client.gui.batchstate.BatchStateListener#valueChanged(client.gui.batchstate.Cell, java.lang.String)
	 */
	@Override
	public void valueChanged(Cell cell, String newValue) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see client.gui.batchstate.BatchStateListener#selectedCellChanged(client.gui.batchstate.Cell)
	 */
	@Override
	public void selectedCellChanged(Cell newSelectedCell) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see client.gui.batchstate.BatchStateListener#invertImageChanged()
	 */
	@Override
	public void invertImageChanged() {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see client.gui.batchstate.BatchStateListener#highlightsVisibleChanged()
	 */
	@Override
	public void highlightsVisibleChanged() {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see client.gui.batchstate.BatchStateListener#zoomChanged()
	 */
	@Override
	public void zoomChanged() {
		// TODO Auto-generated method stub
		
	}
}