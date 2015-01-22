package nl.denhaag.tw.comparators.gui;

/*
 * #%L
 * wsdl-comparator
 * %%
 * Copyright (C) 2012 - 2013 Team Webservices (Gemeente Den Haag)
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.apache.log4j.Logger;

public class WsdlAnalyzer {
	protected static final String OLD_WSDLLOCATION = "old-wsdllocation";
	protected static final String NEW_WSDLLOCATION = "new-wsdllocation";
	protected static final String REPORT_LOCATION = "report-location";
	protected static final String SETTINGS_PROPERTIES = "settings.properties";
	private static final String LABELS_PROPERTIES = "/labels.properties";
	private static final Logger LOGGER = Logger.getLogger(WsdlAnalyzer.class);
	private JFrame applicationFrame;
	private JScrollPane scrollPane;
	private JTextField oldWsdlLocation;
	private JTextField newWsdlLocation;
	private JTextField reportDirectory;
	private JButton oldWsdlBrowseButton;
	private JButton newWsdlBrowseButton;
	private JButton reportDirBrowseButton;
	private JTree tree;
	private JPanel inputPanel;

	private JCheckBox chckbxCheckNamespaces;
	private JCheckBox chckbxDisplayNamespaces;
	private Properties properties = new Properties();
	private Properties labelProperties = new Properties();

	private int height;
	private int width;
	private JButton compareButton;
	private JPanel progressPanel;
	private JLabel progressLabel;
	private static WsdlAnalyzer window;
	
	/**
	 * @return the chckbxCheckNamespaces
	 */
	protected JCheckBox getChckbxCheckNamespaces() {
		return chckbxCheckNamespaces;
	}

	/**
	 * @return the chckbxDisplayNamespaces
	 */
	protected JCheckBox getChckbxDisplayNamespaces() {
		return chckbxDisplayNamespaces;
	}
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					window = new WsdlAnalyzer();
					window.applicationFrame.setVisible(true);
				} catch (Exception e) {
					LOGGER.error(e.getMessage(),e);
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public WsdlAnalyzer() {
		initialize();
	}

	private String getTitle() {
		return getLabel("description") + " - " + getLabel("version") + " - " + getLabel("vendor");
	}

	private String getLabel(String propertyName) {
		return labelProperties.getProperty(propertyName, "???" + propertyName + "???");
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			LOGGER.error(e.getMessage(),e);
		}
		loadProperties(properties, SETTINGS_PROPERTIES);
		loadPropertiesFromClasspath(labelProperties, LABELS_PROPERTIES);
		applicationFrame = new JFrame(getTitle());
		applicationFrame.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				int newWidth = e.getComponent().getWidth();
				int newHeight = e.getComponent().getHeight();
				if (newWidth != width || newHeight != height) {
					int changedWidth = newWidth - width;
					int changedHeight = newHeight - height;
					if (scrollPane != null) {
						int x = scrollPane.getX();
						int y = scrollPane.getY();
						int scrollPaneWidth = scrollPane.getWidth();
						int scrollPaneHeight = scrollPane.getHeight();
						scrollPaneWidth +=	changedWidth ;
						scrollPaneHeight += changedHeight;
						scrollPane.setBounds(x, y, scrollPaneWidth, scrollPaneHeight);
						tree.setBounds(scrollPane.getBounds());
						// progress bar
						int progressPanelY = progressPanel.getY();
						int progressPanelWidth = progressPanel.getWidth();
						progressPanelWidth += changedWidth;
						progressPanelY+=changedHeight;
						progressPanel.setBounds(progressPanel.getX(), progressPanelY, progressPanelWidth, progressPanel.getHeight());
						
						inputPanel.setBounds( inputPanel.getX(), inputPanel.getY(), inputPanel.getWidth() +changedWidth, inputPanel.getHeight());
						oldWsdlLocation.setBounds( oldWsdlLocation.getX(), oldWsdlLocation.getY(), oldWsdlLocation.getWidth() +changedWidth, oldWsdlLocation.getHeight());
						newWsdlLocation.setBounds( newWsdlLocation.getX(), newWsdlLocation.getY(), newWsdlLocation.getWidth() +changedWidth, newWsdlLocation.getHeight());
						reportDirectory.setBounds( reportDirectory.getX(), reportDirectory.getY(), reportDirectory.getWidth() +changedWidth, reportDirectory.getHeight());
						oldWsdlBrowseButton.setBounds( oldWsdlBrowseButton.getX() +changedWidth, oldWsdlBrowseButton.getY(), oldWsdlBrowseButton.getWidth(), oldWsdlBrowseButton.getHeight());
						newWsdlBrowseButton.setBounds( newWsdlBrowseButton.getX() +changedWidth, newWsdlBrowseButton.getY(), newWsdlBrowseButton.getWidth(), newWsdlBrowseButton.getHeight());
						reportDirBrowseButton.setBounds( reportDirBrowseButton.getX() +changedWidth, reportDirBrowseButton.getY(), reportDirBrowseButton.getWidth(), reportDirBrowseButton.getHeight());
						inputPanel.revalidate();
						scrollPane.revalidate();
						progressPanel.revalidate();
					}
				}
				width = newWidth;
				height = newHeight;
			}
		});
		width = 800;
		height = 600;
		applicationFrame.setMinimumSize(new Dimension(width, height));
		applicationFrame.setTitle(getTitle());
		applicationFrame.setBounds(100, 100, width, height);
		applicationFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		scrollPane = new JScrollPane();
		scrollPane.setBounds(0, 145, 784, 380);
		tree = new JTree();
		tree.setBorder(null);

		tree.setRootVisible(false);

		tree.setCellRenderer(new DefaultTreeCellRenderer() {

			/**
									 * 
									 */
			private static final long serialVersionUID = 6375797035733759113L;

			@Override
			public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
					boolean leaf, int row, boolean hasFocus) {
				Component component = null;
				if (value != null && value instanceof CompareResultTreeNode) {
					CompareResultTreeNode node = ((CompareResultTreeNode) value);
					component = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
					component.setFont(node.getResult().getFont());
					component.setForeground(node.getResult().getColor());
				} else {
					component = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
				}
				return component;
			}

		});
		scrollPane.setViewportView(tree);
		DefaultTreeCellRenderer renderer = (DefaultTreeCellRenderer) tree.getCellRenderer();

		inputPanel = new JPanel();
		inputPanel.setBounds(0, 0, 784, 141);
		inputPanel.setLayout(null);

		JLabel lblOldWsdl = new JLabel("Old WSDL:");
		lblOldWsdl.setBounds(10, 27, 74, 14);
		inputPanel.add(lblOldWsdl);

		JLabel lblNewWsdl = new JLabel("New WSDL:");
		lblNewWsdl.setBounds(10, 57, 74, 14);
		inputPanel.add(lblNewWsdl);

		oldWsdlLocation = new JTextField();
		oldWsdlLocation.setBounds(102, 21, 563, 20);
		inputPanel.add(oldWsdlLocation);
		oldWsdlLocation.setText(properties.getProperty(OLD_WSDLLOCATION));
		oldWsdlLocation.setColumns(10);

		newWsdlLocation = new JTextField();
		newWsdlLocation.setBounds(102, 53, 563, 20);
		inputPanel.add(newWsdlLocation);
		newWsdlLocation.setText(properties.getProperty(NEW_WSDLLOCATION));
		newWsdlLocation.setColumns(10);

		compareButton = new JButton("Compare");
		compareButton.setBounds(263, 107, 89, 23);
		inputPanel.add(compareButton);
		compareButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				new ComparisonThread(window).start();

			}
		});
		applicationFrame.getContentPane().setLayout(null);
		applicationFrame.getContentPane().add(inputPanel);

		oldWsdlBrowseButton = new JButton("Browse");
		oldWsdlBrowseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				oldWsdlLocation.setText(openFileChooser(oldWsdlLocation.getText(), (JButton) e.getSource()));
			}
		});
		oldWsdlBrowseButton.setBounds(672, 20, 89, 23);
		inputPanel.add(oldWsdlBrowseButton);

		newWsdlBrowseButton = new JButton("Browse");
		newWsdlBrowseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				newWsdlLocation.setText(openFileChooser(newWsdlLocation.getText(), (JButton) e.getSource()));
			}
		});
		newWsdlBrowseButton.setBounds(672, 53, 89, 23);
		inputPanel.add(newWsdlBrowseButton);

		chckbxCheckNamespaces = new JCheckBox("Check namespaces");
		chckbxCheckNamespaces.setSelected(true);
		chckbxCheckNamespaces.setBounds(10, 107, 117, 23);
		inputPanel.add(chckbxCheckNamespaces);

		chckbxDisplayNamespaces = new JCheckBox("Display namespaces");
		chckbxDisplayNamespaces.setBounds(129, 107, 128, 23);
		inputPanel.add(chckbxDisplayNamespaces);

		JLabel lblReportDirectory = new JLabel("Report directory:");
		lblReportDirectory.setBounds(10, 86, 89, 14);
		inputPanel.add(lblReportDirectory);

		reportDirectory = new JTextField();
		reportDirectory.setBounds(102, 83, 563, 20);
		reportDirectory.setText(properties.getProperty(REPORT_LOCATION));
		inputPanel.add(reportDirectory);
		reportDirectory.setColumns(10);

		reportDirBrowseButton = new JButton("Browse");
		reportDirBrowseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				reportDirectory.setText(openDirChooser(reportDirectory.getText(), (JButton) e.getSource()));
			}
		});
		reportDirBrowseButton.setBounds(672, 87, 89, 23);
		inputPanel.add(reportDirBrowseButton);
		applicationFrame.getContentPane().add(scrollPane);
		
		progressPanel = new JPanel();
		progressPanel.setBounds(0, 530, 784, 32);
		applicationFrame.getContentPane().add(progressPanel);
		
		progressLabel = new JLabel("No comparison started");
		progressPanel.add(progressLabel);
		tree.setVisible(false);
		// renderer.setTextSelectionColor(Color.v);
		renderer.setBackgroundSelectionColor(Color.WHITE);
		renderer.setBorderSelectionColor(Color.WHITE);
	}

	public void expandAll(JTree tree, boolean expand) {
		TreeNode root = (TreeNode) tree.getModel().getRoot();

		// Traverse tree from root
		expandAll(tree, new TreePath(root), expand);
	}

	private void expandAll(JTree tree, TreePath parent, boolean expand) {
		// Traverse children
		TreeNode node = (TreeNode) parent.getLastPathComponent();
		if (node.getChildCount() >= 0) {
			for (int i = 0; i < node.getChildCount(); i++) {
				TreeNode n = node.getChildAt(i);
				TreePath path = parent.pathByAddingChild(n);
				expandAll(tree, path, expand);
			}
		}

		// Expansion or collapse must be done bottom-up
		if (expand) {
			tree.expandPath(parent);
		} else {
			tree.collapsePath(parent);
		}
	}



	private String openDirChooser(String selectedItem, Component source) {
		File selectedFile = new File(selectedItem);
		final JFileChooser fc = new JFileChooser(selectedFile.getParentFile());
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fc.setAcceptAllFileFilterUsed(false);
		// In response to a button click:
		int returnVal = fc.showOpenDialog(source);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			return file.getAbsolutePath();
		}
		return selectedItem;
	}

	private String openFileChooser(String selectedItem, Component source) {
		File selectedFile = new File(selectedItem);
		final JFileChooser fc = new JFileChooser(selectedFile.getParentFile());
		FileNameExtensionFilter filter = new FileNameExtensionFilter("WSDL files", "wsdl");
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setFileFilter(filter);

		fc.setAcceptAllFileFilterUsed(false);
		// In response to a button click:
		int returnVal = fc.showOpenDialog(source);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			return file.getAbsolutePath();
		}
		return selectedItem;
	}

	private static void loadProperties(Properties properties, String fileName) {
		try {
			properties.load(new FileInputStream(fileName));
		} catch (IOException ex) {
			LOGGER.warn(ex.getMessage());
		}
	}

	private static void loadPropertiesFromClasspath(Properties properties, String fileName) {
		try {
			properties.load(WsdlAnalyzer.class.getResourceAsStream(fileName));
		} catch (Exception ex) {
			LOGGER.warn(ex.getMessage());
		}
	}

	protected static void storeProperties(Properties properties, String fileName) {
		try {
			properties.store(new FileOutputStream(fileName), "Stored by synchronizer");
		} catch (IOException ex) {
			LOGGER.warn(ex.getMessage());
		}
	}

	/**
	 * @return the tree
	 */
	protected JTree getTree() {
		return tree;
	}

	/**
	 * @return the btnCompare
	 */
	protected JButton getBtnCompare() {
		return compareButton;
	}

	/**
	 * @return the progressLabel
	 */
	protected JLabel getProgressLabel() {
		return progressLabel;
	}

	/**
	 * @return the oldWsdlLocation
	 */
	protected JTextField getOldWsdlLocation() {
		return oldWsdlLocation;
	}

	/**
	 * @return the newWsdlLocation
	 */
	protected JTextField getNewWsdlLocation() {
		return newWsdlLocation;
	}

	/**
	 * @return the properties
	 */
	protected Properties getProperties() {
		return properties;
	}

	/**
	 * @return the reportDirectory
	 */
	protected JTextField getReportDirectory() {
		return reportDirectory;
	}

	/**
	 * @return the applicationFrame
	 */
	protected JFrame getApplicationFrame() {
		return applicationFrame;
	}
	
}