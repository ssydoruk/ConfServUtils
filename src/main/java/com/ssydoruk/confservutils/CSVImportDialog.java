/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ssydoruk.confservutils;

import com.genesyslab.platform.applicationblocks.com.objects.CfgFolder;
import com.genesyslab.platform.configuration.protocol.types.CfgObjectType;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

/**
 *
 * @author stepan_sydoruk
 */
abstract class CSVImportDialog extends CSVGeneralImportDialog {

	static CSVImportDialog instance = null;

	public static CSVImportDialog getInstance(Window parent, ConfigServerManager cfg) {
		if (instance == null) {
			instance = new CSVImportDialog(parent, cfg) {
			};
		}
		return instance;
	}

	DefaultTableModel modelPlaceDN;
	JTable tabPlaceDN;
	JTable tabPlaceFolder;
	JTable tabDNFolders;

	Utils.swing.TableColumnAdjuster tcaPlaceDN;
	JPanel pPlaceFolder;
	JPanel dnFolders;
	JPanel topPan;
	private final ConfigServerManager cfg;

	public CSVImportDialog(Window parent, ConfigServerManager _cfg) throws HeadlessException {
		super(parent, _cfg);
		cfg = _cfg;

		modelPlaceDN = new DefaultTableModel() {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false; // To change body of generated methods, choose Tools | Templates.
			}
		};

		modelPlaceDN.addColumn("Place");
		modelPlaceDN.addColumn("DN");

		tabPlaceDN = new JTable(modelPlaceDN);
		tabPlaceDN.getTableHeader().setVisible(true);

		JScrollPane jp = new JScrollPane(tabPlaceDN);
		// jp.add(tab);

		Dimension preferredSize = new Dimension(600, 400);
		jp.setPreferredSize(preferredSize);
		jp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		topPan = new JPanel();

		topPan.setLayout(new BoxLayout(topPan, BoxLayout.PAGE_AXIS));
		topPan.add(jp);

		pPlaceFolder = new JPanel();
		pPlaceFolder.setLayout(new BoxLayout(pPlaceFolder, BoxLayout.PAGE_AXIS));
		pPlaceFolder.setBorder(new TitledBorder("Place folder"));
		tabPlaceFolder = new JTable();
		tabPlaceFolder.getTableHeader().setVisible(false);
		// tabPlaceFolder.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		tabPlaceFolder.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		pPlaceFolder.add(tabPlaceFolder);
		JButton btSelectPlaceFolder = new JButton(new AbstractAction("Select") {
			@Override
			public void actionPerformed(ActionEvent e) {
				ArrayList<CfgFolder> findFolders = findFolders(CfgObjectType.CFGPlace, theDialog, false);
				if (findFolders != null) {
					tabPlaceFolder.setModel(new FoldersModel(findFolders));
				}
			}
		});
		pPlaceFolder.add(btSelectPlaceFolder);
		dnFolders = new JPanel();
		dnFolders.setLayout(new BoxLayout(dnFolders, BoxLayout.PAGE_AXIS));
		dnFolders.setBorder(new TitledBorder("DN folders"));
		tabDNFolders = new JTable();
		tabDNFolders.getTableHeader().setVisible(false);
		// tabDNFolders.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		dnFolders.add(tabDNFolders);
		JButton btSelectDNFolder = new JButton(new AbstractAction("Select") {
			@Override
			public void actionPerformed(ActionEvent e) {
				ArrayList<CfgFolder> findFolders = findFolders(CfgObjectType.CFGDN, theDialog, true);
				if (findFolders != null) {
					tabDNFolders.setModel(new FoldersModel(findFolders));
				}
			}
		});
		dnFolders.add(btSelectDNFolder);
		topPan.add(pPlaceFolder);
		topPan.add(dnFolders);

	}

	private ArrayList<CfgFolder> lastSelectedFolders(JTable tab) {
		TableModel model = tab.getModel();
		if (model instanceof FoldersModel) {
			return ((FoldersModel) model).getAllFolders();
		}
		return null;
	}

	public ArrayList<CfgFolder> lastSelectedPlaceFolder() {
		return lastSelectedFolders(tabPlaceFolder);
	}

	public ArrayList<CfgFolder> lastSelectedDNFolders() {
		return lastSelectedFolders(tabDNFolders);

	}

	public boolean shouldImportCSV(final ArrayList<String[]> placeDN, boolean isImport) {

		modelPlaceDN.setRowCount(0);
		placeDN.stream().forEach(entry -> modelPlaceDN.addRow(new Object[] { entry[0], entry[1] }));

		pPlaceFolder.setVisible(isImport);
		dnFolders.setVisible(isImport);

		topPan.invalidate();

		StringBuilder title = new StringBuilder();
		if (isImport) {
			title.append("Do you want to check existense Place/DN (total ").append(placeDN.size()).append(")");
		} else {
			title.append("Do you want to import following Place/DN (total ").append(placeDN.size()).append(")");
		}

		Utils.ScreenInfo.CenterWindow(this);
		showModal(topPan, title.toString());

		return getDialogResult() == JOptionPane.OK_OPTION;

	}

}
