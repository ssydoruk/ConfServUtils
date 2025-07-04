/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ssydoruk.confservutils;

import Utils.swing.GridEditor;
import com.genesyslab.platform.commons.GEnum;
import com.genesyslab.platform.configuration.protocol.types.CfgAppType;
import com.jidesoft.swing.CheckBoxListSelectionModel;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import javax.swing.AbstractButton;
import javax.swing.DefaultListModel;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author stepan_sydoruk
 */
public class RestartServices extends javax.swing.JPanel {

	private final AppForm theForm;

	/**
	 * Creates new form AppByDBID
	 */
	public RestartServices(AppForm _theForm) {
		initComponents();
		theForm = _theForm;
		Utils.swing.Swing.restrictHeight(tfObjectName);
		Utils.swing.Swing.restrictHeight(cbApplicationType);

		Main.loadGenesysTypes(cbApplicationType, CfgAppType.values());

		ItemListener itemListener = new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {

				AbstractButton aButton = (AbstractButton) e.getSource();
				rbActionChanged(aButton.getModel().isSelected(), aButton);

			}
		};

	}

	public String getRemoteCommand() {
		return jtfRemoteCommand.getText();
	}

	public String getStatusScript() {
		return jtfStatusScript.getText();
	}

	/**
	 *
	 * @return application type if configured or null if all types
	 */
	public CfgAppType getSelectedAppType() {
		Object ret = cbApplicationType.getSelectedItem();
		if (ret instanceof CfgObjectTypeMenu) {
			return (CfgAppType) ((CfgObjectTypeMenu) ret).getType();
		} else {
			return null;
		}
	}

	private void rbActionChanged(boolean isSelected, AbstractButton rbButton) {
	}

	private static final Logger logger = Main.getLogger();

	public boolean isCaseSensitive() {
		return cbCaseSensitive.isSelected();
	}

	public boolean isRegex() {
		return cbIsRegex.isSelected();
	}

	public boolean isFullOutputSelected() {
		return false;
	}

	private final ArrayList<UserProperties> updateProperties = new ArrayList<>();

	private GEnum cfgObjType(Object o) {
		if (o == null || o instanceof String) {
			return null;
		} else {
			return ((CfgObjectTypeMenu) o).getType();
		}
	}

	private GridEditor kvpEditor;

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
	// <editor-fold defaultstate="collapsed" desc="Generated
	// Code">//GEN-BEGIN:initComponents
	private void initComponents() {

		buttonGroup2 = new javax.swing.ButtonGroup();
		bgReplaceAction = new javax.swing.ButtonGroup();
		jPanel2 = new javax.swing.JPanel();
		jPanel10 = new javax.swing.JPanel();
		jPanel12 = new javax.swing.JPanel();
		jPanel6 = new javax.swing.JPanel();
		lbObjectName = new javax.swing.JLabel();
		tfObjectName = new javax.swing.JComboBox<>();
		jPanel4 = new javax.swing.JPanel();
		jPanel8 = new javax.swing.JPanel();
		cbIsRegex = new javax.swing.JCheckBox();
		cbCaseSensitive = new javax.swing.JCheckBox();
		jPanel7 = new javax.swing.JPanel();
		jLabel2 = new javax.swing.JLabel();
		cbApplicationType = new javax.swing.JComboBox<>();
		jPanel11 = new javax.swing.JPanel();
		jPanel13 = new javax.swing.JPanel();
		jPanel29 = new javax.swing.JPanel();
		jLabel6 = new javax.swing.JLabel();
		jtfStatusScript = new javax.swing.JTextField();
		jbSelectScript1 = new javax.swing.JButton();
		jPanel30 = new javax.swing.JPanel();
		jLabel7 = new javax.swing.JLabel();
		jtfRemoteCommand = new javax.swing.JTextField();
		jPanel5 = new javax.swing.JPanel();

		setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.PAGE_AXIS));

		jPanel2.setLayout(new javax.swing.BoxLayout(jPanel2, javax.swing.BoxLayout.PAGE_AXIS));

		jPanel10.setBorder(javax.swing.BorderFactory.createTitledBorder("Search range"));
		jPanel10.setLayout(new javax.swing.BoxLayout(jPanel10, javax.swing.BoxLayout.PAGE_AXIS));

		jPanel12.setLayout(new javax.swing.BoxLayout(jPanel12, javax.swing.BoxLayout.PAGE_AXIS));

		jPanel6.setLayout(new javax.swing.BoxLayout(jPanel6, javax.swing.BoxLayout.LINE_AXIS));

		lbObjectName.setText("App name");
		jPanel6.add(lbObjectName);

		tfObjectName.setEditable(true);
		jPanel6.add(tfObjectName);

		jPanel12.add(jPanel6);

		jPanel10.add(jPanel12);

		jPanel4.setLayout(new javax.swing.BoxLayout(jPanel4, javax.swing.BoxLayout.LINE_AXIS));

		jPanel8.setLayout(new javax.swing.BoxLayout(jPanel8, javax.swing.BoxLayout.LINE_AXIS));

		cbIsRegex.setText("Regular expression");
		jPanel8.add(cbIsRegex);

		cbCaseSensitive.setText("Case sensitive");
		jPanel8.add(cbCaseSensitive);

		jPanel4.add(jPanel8);

		jPanel10.add(jPanel4);

		jPanel7.setLayout(new javax.swing.BoxLayout(jPanel7, javax.swing.BoxLayout.LINE_AXIS));

		jLabel2.setText("Application type");
		jPanel7.add(jLabel2);

		jPanel7.add(cbApplicationType);

		jPanel10.add(jPanel7);

		jPanel2.add(jPanel10);

		jPanel11.setBorder(javax.swing.BorderFactory.createTitledBorder("Actions"));
		jPanel11.setLayout(new javax.swing.BoxLayout(jPanel11, javax.swing.BoxLayout.PAGE_AXIS));

		jPanel13.setLayout(new javax.swing.BoxLayout(jPanel13, javax.swing.BoxLayout.PAGE_AXIS));

		jPanel29.setLayout(new javax.swing.BoxLayout(jPanel29, javax.swing.BoxLayout.LINE_AXIS));

		jLabel6.setText("App status script");
		jPanel29.add(jLabel6);

		jtfStatusScript.setText("/Users/stepan_sydoruk/bin/restartAppOnPPE");
		jPanel29.add(jtfStatusScript);

		jbSelectScript1.setText("...");
		jbSelectScript1.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jbSelectScript1ActionPerformed(evt);
			}
		});
		jPanel29.add(jbSelectScript1);

		jPanel13.add(jPanel29);

		jPanel30.setLayout(new javax.swing.BoxLayout(jPanel30, javax.swing.BoxLayout.LINE_AXIS));

		jLabel7.setText("Remote command");
		jPanel30.add(jLabel7);

		jtfRemoteCommand.setText("bash -c \"service sndmpd restart\"");
		jtfRemoteCommand.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jtfRemoteCommandActionPerformed(evt);
			}
		});
		jPanel30.add(jtfRemoteCommand);

		jPanel13.add(jPanel30);

		jPanel11.add(jPanel13);

		jPanel5.setLayout(new javax.swing.BoxLayout(jPanel5, javax.swing.BoxLayout.LINE_AXIS));
		jPanel11.add(jPanel5);

		jPanel2.add(jPanel11);

		add(jPanel2);
	}// </editor-fold>//GEN-END:initComponents

	private void jbSelectScript1ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jbSelectScript1ActionPerformed
		// TODO add your handling code here:
	}// GEN-LAST:event_jbSelectScript1ActionPerformed

	private void jtfRemoteCommandActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jtfRemoteCommandActionPerformed
		// TODO add your handling code here:
	}// GEN-LAST:event_jtfRemoteCommandActionPerformed

	public ArrayList<UserProperties> getUpdateProperties() {
		return updateProperties;
	}

	public String getSection() {
		return null;
	}

	public String getObjName() {
		if (!isSearchAll()) {
			return Utils.swing.Swing.checkBoxSelection(tfObjectName);
		} else {
			return null;
		}
	}

	public String getOption() {
		return null;
	}

	public String getValue() {
		return null;
	}

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.ButtonGroup bgReplaceAction;
	private javax.swing.ButtonGroup buttonGroup2;
	private javax.swing.JComboBox<String> cbApplicationType;
	private javax.swing.JCheckBox cbCaseSensitive;
	private javax.swing.JCheckBox cbIsRegex;
	private javax.swing.JLabel jLabel2;
	private javax.swing.JLabel jLabel6;
	private javax.swing.JLabel jLabel7;
	private javax.swing.JPanel jPanel10;
	private javax.swing.JPanel jPanel11;
	private javax.swing.JPanel jPanel12;
	private javax.swing.JPanel jPanel13;
	private javax.swing.JPanel jPanel2;
	private javax.swing.JPanel jPanel29;
	private javax.swing.JPanel jPanel30;
	private javax.swing.JPanel jPanel4;
	private javax.swing.JPanel jPanel5;
	private javax.swing.JPanel jPanel6;
	private javax.swing.JPanel jPanel7;
	private javax.swing.JPanel jPanel8;
	private javax.swing.JButton jbSelectScript1;
	private javax.swing.JTextField jtfRemoteCommand;
	private javax.swing.JTextField jtfStatusScript;
	private javax.swing.JLabel lbObjectName;
	private javax.swing.JComboBox<String> tfObjectName;
	// End of variables declaration//GEN-END:variables

	public boolean isSearchAll() {
		return false;
	}

	public String getAllSearch() {
		return null;

	}

	public String getSearchSummary(int maxTypes) {
		StringBuilder buf = new StringBuilder();
		buf.append("App by options;");

		if (isSearchAll()) {
			buf.append(" term \"").append(getAllSearch()).append("\" in all fields, including object attributes");
		} else {
			if (getObjName() != null) {
				buf.append("\n\tname [").append(getObjName()).append("]");
			}
			if (getSection() != null) {
				buf.append(" section [").append(getSection()).append("]");
			}
			if (getOption() != null) {
				buf.append(" option [").append(getOption()).append("]");
			}
			if (getValue() != null) {
				buf.append(" value [").append(getValue()).append("]");
			}
		}
		buf.append(" rx[").append(isRegex() ? "yes" : "no").append("]");
		buf.append(" CaSe[").append(isCaseSensitive() ? "yes" : "no").append("]");

		return buf.toString();
	}

	public String getSearchSummary() {
		return getSearchSummary(-1);
	}

	public void setChoices(Collection<String> choices) {

		Utils.swing.Swing.setChoices(tfObjectName, choices);

	}

	public Collection<String> getChoices() {

		return Utils.swing.Swing.getChoices(tfObjectName);
	}

	private void modelUncheck(CheckBoxListSelectionModel selectionModel, GEnum[] gEnum) {
		HashSet<GEnum> en = new HashSet<>(Arrays.asList(gEnum));

		DefaultListModel model = (DefaultListModel) selectionModel.getModel();
		selectionModel.clearSelection();
		for (int i = 0; i < model.size(); i++) {
			if (i != selectionModel.getAllEntryIndex()) {
				CfgObjectTypeMenu get = (CfgObjectTypeMenu) model.get(i);
				if (!en.contains(get.getType())) {
					selectionModel.addSelectionInterval(i, i);
				}
			}
		}

	}

	private StringBuilder getReplaceWith() {
		return null;
	}

	private StringBuilder getAddSection() {

		return null;
	}

	boolean checkParameters() {

		return true;
	}

	String getSearchSummaryHTML() {
		StringBuilder ret = new StringBuilder();
		ret.append("<html>").append(getSearchSummary(2).replaceAll("\n", "<br>")).append("</html>");
		return ret.toString();

	}

	public boolean isMakeBackup() {
		return false;
	}

	public String replaceWith(String currentValue) {
		return null;

	}

	public String getReplaceKey(String stringKey) {
		return stringKey;
	}

	public Collection<UserProperties> getAddedKVP() {
		return updateProperties;

	}

}
