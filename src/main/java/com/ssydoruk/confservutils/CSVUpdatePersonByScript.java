/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ssydoruk.confservutils;

import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.Window;
import java.util.ArrayList;
import javax.swing.BoxLayout;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author stepan_sydoruk
 */
abstract class CSVUpdatePersonByScript extends CSVGeneralImportDialog {

	static CSVUpdatePersonByScript instance = null;

	public static CSVUpdatePersonByScript getInstance(Window parent, ConfigServerManager cfg) {
		if (instance == null) {
			instance = new CSVUpdatePersonByScript(parent, cfg) {
			};
		}
		return instance;
	}

	DefaultTableModel modelPersons;
	JTable tabPersons;

	Utils.swing.TableColumnAdjuster tcaPersons;
	JPanel topPan;
	JSEditPanel jsEditPanel;
	private final ConfigServerManager cfg;

	public CSVUpdatePersonByScript(Window parent, ConfigServerManager _cfg) throws HeadlessException {
		super(parent, _cfg);
		cfg = _cfg;

		modelPersons = new DefaultTableModel() {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false; // To change body of generated methods, choose Tools | Templates.
			}
		};

		modelPersons.addColumn("Person");

		tabPersons = new JTable(modelPersons);
		tabPersons.getTableHeader().setVisible(true);

		JScrollPane jp = new JScrollPane(tabPersons);
		// jp.add(tab);

		Dimension preferredSize = new Dimension(600, 400);
		jp.setPreferredSize(preferredSize);
		jp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		topPan = new JPanel();

		topPan.setLayout(new BoxLayout(topPan, BoxLayout.PAGE_AXIS));
		topPan.add(jp);

		jsEditPanel = new JSEditPanel();
		topPan.add(jsEditPanel);

	}

	public boolean shouldProceed(final ArrayList<String[]> persons) {

		modelPersons.setRowCount(0);

		for (String[] entry : persons) {
			modelPersons.addRow(new Object[] { entry[0] });
		}
		topPan.invalidate();

		StringBuilder title = new StringBuilder();
		title.append("Do you want to process following persons (total ").append(persons.size()).append(")");

		Utils.ScreenInfo.CenterWindow(this);
		showModal(topPan, title.toString());

		return getDialogResult() == JOptionPane.OK_OPTION;

	}

	String getScript() {
		return jsEditPanel.getText();
	}

}
