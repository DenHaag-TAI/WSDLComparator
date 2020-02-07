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

import java.awt.Font;
import java.io.File;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.tree.DefaultTreeModel;

import nl.denhaag.tw.comparators.result.CompareConfiguration;
import nl.denhaag.tw.comparators.result.CompareResult;
import nl.denhaag.tw.comparators.result.CompareWsdl;
import nl.denhaag.tw.comparators.result.Compatibility;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class ComparisonThread extends Thread {
	private final static Logger LOGGER = LogManager.getLogger(ComparisonThread.class);
//	private final static Logger LOGGER = Logger.getLogger(ComparisonThread.class);

	private WsdlAnalyzer wsdlAnalyzer;
	public ComparisonThread( WsdlAnalyzer wsdlAnalyzer){
		this.wsdlAnalyzer = wsdlAnalyzer;
	}
	@Override
	public void run() {
		Font font = new Font(Font.SANS_SERIF, Font.BOLD, 12);
		Color color = null;
		String message = null;
		String icon = null;
		try {
			wsdlAnalyzer.getBtnCompare().setEnabled(false);
			wsdlAnalyzer.getChckbxCheckNamespaces().setEnabled(false);
			wsdlAnalyzer.getChckbxDisplayNamespaces().setEnabled(false);
			wsdlAnalyzer.getProgressLabel().setText("Comparison in progress...");
			wsdlAnalyzer.getProgressLabel().setIcon(null);
			wsdlAnalyzer.getProgressLabel().setForeground(Color.BLACK);
			String oldWsdlLocation = wsdlAnalyzer.getOldWsdlLocation().getText();
			String newWsdlLocation = wsdlAnalyzer.getNewWsdlLocation().getText();
			String reportDirectory = wsdlAnalyzer.getReportDirectory().getText();
			wsdlAnalyzer.getProperties().setProperty(WsdlAnalyzer.OLD_WSDLLOCATION, oldWsdlLocation);
			wsdlAnalyzer.getProperties().setProperty(WsdlAnalyzer.NEW_WSDLLOCATION, newWsdlLocation);
			wsdlAnalyzer.getProperties().setProperty(WsdlAnalyzer.REPORT_LOCATION, reportDirectory);
			WsdlAnalyzer.storeProperties(wsdlAnalyzer.getProperties(), WsdlAnalyzer.SETTINGS_PROPERTIES);
			CompareConfiguration.getInstance().init(wsdlAnalyzer.getChckbxCheckNamespaces().isSelected(),
					wsdlAnalyzer.getChckbxDisplayNamespaces().isSelected());
			File oldWsdl = new File(oldWsdlLocation);
			File newWsdl = new File(newWsdlLocation);
			CompareResult compareResult = CompareWsdl.compare(oldWsdl, newWsdl);
			File reportDir = new File( reportDirectory);
			if (Compatibility.NOCHANGE.equals(compareResult.getCompatibleAndChildren())) {
				icon = "ok";
				message = "No difference found";
				color = new Color(0,128,0);
			} else if (Compatibility.DOCUMENTATION.equals(compareResult.getCompatibleAndChildren())) {
				icon = "ok";
				message = "Only document is changed";
				color = Color.BLACK;
			} else if (Compatibility.NOTBREAKS.equals(compareResult.getCompatibleAndChildren())) {
				icon = "warning";
				message = "WSDL's are are different, but backwards compatible";				
				color = new Color(198,79,0);
			} else if (Compatibility.INVALID.equals(compareResult.getCompatibleAndChildren())) {
				icon = "invalid";
				message = "At least one of the WSDL's is not valid";				
				color = Color.RED;
			} else {
				icon = "breaks";
				message = "WSDL's are different and not backwards compatible";					
				color = Color.RED;
			}
			ReportWriter.writeReport(reportDir, icon, message, compareResult, oldWsdl, newWsdl);
			wsdlAnalyzer.getTree().setModel(new DefaultTreeModel(new CompareResultTreeNode(compareResult, null)));
			wsdlAnalyzer.getTree().setVisible(true);
			for (int i = 0; i < wsdlAnalyzer.getTree().getRowCount(); i++) {
				wsdlAnalyzer.getTree().expandRow(i);
			}
			wsdlAnalyzer.getTree().repaint();
		} catch (Throwable e) {
			icon = "invalid";
			color = Color.BLACK;
			font = new Font(Font.SANS_SERIF, Font.PLAIN, 11);
			message = "Unexpected error: " + e.getMessage();	
			LOGGER.error(e.getMessage(), e);
		}
		wsdlAnalyzer.getProgressLabel().setIcon(createImageIcon("/" + icon + ".png"));
		wsdlAnalyzer.getProgressLabel().setText(message);
		wsdlAnalyzer.getProgressLabel().setForeground(color);
		wsdlAnalyzer.getProgressLabel().setFont(font);
		wsdlAnalyzer.getBtnCompare().setEnabled(true);
		wsdlAnalyzer.getChckbxCheckNamespaces().setEnabled(true);
		wsdlAnalyzer.getChckbxDisplayNamespaces().setEnabled(true);
	}

	private ImageIcon createImageIcon(String path) {
		URL imgURL = getClass().getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL);
		} else {
			LOGGER.error("Couldn't find file: " + path);
			return null;
		}
	}
}
