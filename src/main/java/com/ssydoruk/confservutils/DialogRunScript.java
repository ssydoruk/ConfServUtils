/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ssydoruk.confservutils;

import com.jidesoft.dialog.*;

import static com.jidesoft.dialog.StandardDialog.RESULT_CANCELLED;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;

import org.apache.commons.lang3.*;

/**
 * @author stepan_sydoruk
 */
final class DialogRunScript extends StandardDialog {

	private PRunScript pRunScript;
	private final JLabel lbFile;
	private String fileName;
	private final JButton btSave;
	private final JButton btOK;
	private final JToggleButton btDebug;
	private final JButton btCancel;
	final ButtonPanel buttonPanel;
	private boolean forceFile = false;
	private final JButton btClear;

	public DialogRunScript(final Window parent) {
		super(parent);
		setTitle("Enter request parameters");
		lbFile = new JLabel();
		pRunScript = new PRunScript();
		JSRunner.getInstance().setDebugPort(Main.isDebug() ? "9229" : (String) null);
		addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent windowEvent) {
				pRunScript.windowClosing();
			}
		});

		buttonPanel = new ButtonPanel();
		buttonPanel.addButton(btCancel = new JButton(new AbstractAction("Close") {
			@Override
			public void actionPerformed(final ActionEvent e) {
				setDialogResult(RESULT_CANCELLED);
				setVisible(false);
				dispose();
			}
		}));

		buttonPanel.addButton(btOK = new JButton(new AbstractAction("Run") {
			@Override
			public void actionPerformed(ActionEvent e) {
				runSelected();
			}
		}));

		buttonPanel.addButton(btDebug = new JToggleButton(new AbstractAction("Debug") {
			@Override
			public void actionPerformed(ActionEvent e) {
				setDebug(((AbstractButton) e.getSource()).getModel().isSelected());
			}

		}));

		buttonPanel.addButton(btClear = new JButton(new AbstractAction("Clear log") {
			@Override
			public void actionPerformed(ActionEvent e) {
				pRunScript.clearLog();
			}
		}));

		buttonPanel.addButton(btSave = new JButton(new AbstractAction("Save") {
			@Override
			public void actionPerformed(ActionEvent e) {
				pRunScript.saveScript();
				changeDocStatus(DOC_STATE.SAVED);
			}
		}));

		setDefaultCancelAction(btCancel.getAction());
		setDefaultAction(btOK.getAction());
		getRootPane().setDefaultButton(btOK);

		buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		buttonPanel.setSizeConstraint(ButtonPanel.NO_LESS_THAN); // since the checkbox is quite wide, we don't want

	}

	@Override
	public JComponent createBannerPanel() {
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.LINE_AXIS));
		p.add(new JLabel("File: "));
		p.add(lbFile);

		return p;
	}

	@Override
	public JComponent createContentPanel() {

		return pRunScript;
	}

	@Override
	public ButtonPanel createButtonPanel() {

		// all of them have the same size.
		return buttonPanel;
	}

	private void runSelected() {
		scriptExecuted(true);
		pRunScript.runScript(forceFile);
		forceFile = false;
		scriptExecuted(false);
	}

	private void setDebug(boolean debugOn) {
		String msg = null;
		if (debugOn) {
			String portEntered = JOptionPane.showInputDialog(this, "Enter debug port", 9229);
			if (portEntered == null) {
				btDebug.setSelected(false);

				return;
			} else {
				ArrayList<String> initMessages = JSRunner.getInstance().setDebugPort(portEntered);
				if (initMessages != null) {
					msg = StringUtils.join(initMessages, "\n");
				}

			}
		} else {
			JSRunner.getInstance().setDebugPort(null);
		}
		showDebugMessage((msg != null) ? msg : "Debugging is turned off");
	}

	private DebugInfoDialog debugDialog = null;

	private void showDebugMessage(String msg) {
		if (debugDialog == null) {
			debugDialog = new DebugInfoDialog(this);
		}
		debugDialog.showAnn("Javascript debug", msg);
	}

	private void changeDocStatus(DOC_STATE docState) {
		switch (docState) {
		case EXTERNALLY_MODIFIED:
			lbFile.setText(fileName + " (externally edited)");
			forceFile = true;
			break;
		case EDITED:
			lbFile.setText(fileName + " (modified)");
			break;
		case CLEAR:
			lbFile.setText(fileName);
			break;

		case SAVED:
			lbFile.setText(fileName);
			forceFile = true;
			break;
		}
		btSave.setEnabled(docState == DOC_STATE.EDITED);
	}

	void doShow(File scriptFile, ConfigServerManager configServerManager) {

		// setModal(true);
		this.fileName = scriptFile.getAbsolutePath();
		pRunScript.setParams(scriptFile, configServerManager, docState -> changeDocStatus(docState));
		pack();
		changeDocStatus(DOC_STATE.CLEAR);

		setLocationRelativeTo(getParent());
		// setVisible(true);
//        setAlwaysOnTop(true);
		java.awt.EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				toFront();

			}
		});
		// setVisible(false);
		setVisible(true);

	}

	private void scriptExecuted(boolean b) {
//        btSave.setEnabled(!b);
//        btClear.setEnabled(!b);
//        btOK.setEnabled(!b);
//        btCancel.setEnabled(!b);
	}

	public enum DOC_STATE {
		CLEAR, EDITED, SAVED, EXTERNALLY_MODIFIED,
	}

	public interface IDocUpdated {

		void docUpdated(DOC_STATE docState);
	}

}
