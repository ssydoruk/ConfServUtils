/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ssydoruk.confservutils;

import static Utils.swing.Swing.checkBoxSelection;
import com.genesyslab.platform.commons.GEnum;
import com.genesyslab.platform.configuration.protocol.types.CfgObjectType;
import com.jidesoft.swing.CheckBoxList;
import com.jidesoft.swing.CheckBoxListSelectionModel;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.DefaultListModel;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author stepan_sydoruk
 */
public class ObjByAnnex extends javax.swing.JPanel implements ISearchSettings, ISearchCommon {

	/**
	 * Creates new form AppByDBID
	 */
	public ObjByAnnex() {
		initComponents();
		Utils.swing.Swing.restrictHeight(tfObjectName);
		Utils.swing.Swing.restrictHeight(tfOption);
		Utils.swing.Swing.restrictHeight(tfOptionValue);
		Utils.swing.Swing.restrictHeight(tfSearchString);
		Utils.swing.Swing.restrictHeight(tfSection);
		clm = new DefaultListModel();
		clb = new CheckBoxList((ListModel) clm);
		jpObjectTypes.add(new JScrollPane(clb));
		Main
			.loadGenesysTypes(clb, CfgObjectType.values(), new GEnum[] { CfgObjectType.CFGNoObject,
					CfgObjectType.CFGMaxObjectType, CfgObjectType.CFGPersonLastLogin, CfgObjectType.CFGAppPrototype,

			});

		modelUncheck(clb.getCheckBoxListSelectionModel(), new GEnum[] { CfgObjectType.CFGPerson, CfgObjectType.CFGDN,
				CfgObjectType.CFGAccessGroup, CfgObjectType.CFGAgentLogin });

		rbShortOutput.setSelected(true);
//        cbCaseSensitive.setSelected(false);

		jrbEverywhere.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(final ItemEvent e) {

				final AbstractButton aButton = (AbstractButton) e.getSource();
				final ButtonModel aModel = aButton.getModel();

				jrbEverywhereSelected(aButton.getModel().isSelected());

			}
		});
		jrbEverywhere.setSelected(true);

	}

	public List<CfgObjectType> getSelectedObjectTypes() {
		final ArrayList<CfgObjectType> ret = new ArrayList();
		for (final Object checkBoxListSelectedValue : clb.getCheckBoxListSelectedValues()) {
			ret.add((CfgObjectType) ((CfgObjectTypeMenu) checkBoxListSelectedValue).getType());
		}
		return ret;

	}

	CheckBoxList clb;
	DefaultListModel clm;

	private void jrbEverywhereSelected(final boolean isSelected) {
		setVisible(false);
		tfObjectName.setEnabled(!isSelected);
		lbObjectName.setEnabled(!isSelected);
		tfOption.setEnabled(!isSelected);
		lbOption.setEnabled(!isSelected);
		tfOptionValue.setEnabled(!isSelected);
		lbOptionValue.setEnabled(!isSelected);
		tfSection.setEnabled(!isSelected);
		lbSection.setEnabled(!isSelected);

		lbSearchString.setEnabled(isSelected);
		tfSearchString.setEnabled(isSelected);
		setVisible(true);
	}

	private static final Logger logger = Main.getLogger();

	@Override
	public boolean isCaseSensitive() {
		return cbCaseSensitive.isSelected();
	}

	@Override
	public boolean isRegex() {
		return cbIsRegex.isSelected();
	}

	@Override
	public boolean isFullOutputSelected() {
		return rbFullOutput.isSelected();
	}

	@Override
	public boolean isAllKVPsInOutput() {
		return cbAllKVPs.isSelected();
	}

	private GEnum cfgObjType(final Object o) {
		if (o == null || o instanceof String) {
			return null;
		} else {
			return ((CfgObjectTypeMenu) o).getType();
		}
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
	// <editor-fold defaultstate="collapsed" desc="Generated
	// <editor-fold defaultstate="collapsed" desc="Generated
	// Code">//GEN-BEGIN:initComponents
	private void initComponents() {

		buttonGroup1 = new javax.swing.ButtonGroup();
		buttonGroup2 = new javax.swing.ButtonGroup();
		jPanel10 = new javax.swing.JPanel();
		jPanel11 = new javax.swing.JPanel();
		jrbEverywhere = new javax.swing.JRadioButton();
		jrbOnlySelected = new javax.swing.JRadioButton();
		jPanel12 = new javax.swing.JPanel();
		jPanel13 = new javax.swing.JPanel();
		lbSearchString = new javax.swing.JLabel();
		tfSearchString = new javax.swing.JComboBox<>();
		jPanel6 = new javax.swing.JPanel();
		lbObjectName = new javax.swing.JLabel();
		tfObjectName = new javax.swing.JComboBox<>();
		jPanel3 = new javax.swing.JPanel();
		lbSection = new javax.swing.JLabel();
		tfSection = new javax.swing.JComboBox<>();
		jPanel1 = new javax.swing.JPanel();
		lbOption = new javax.swing.JLabel();
		tfOption = new javax.swing.JComboBox<>();
		jPanel5 = new javax.swing.JPanel();
		lbOptionValue = new javax.swing.JLabel();
		tfOptionValue = new javax.swing.JComboBox<>();
		jpObjectTypes = new javax.swing.JPanel();
		jPanel4 = new javax.swing.JPanel();
		jPanel8 = new javax.swing.JPanel();
		cbIsRegex = new javax.swing.JCheckBox();
		cbCaseSensitive = new javax.swing.JCheckBox();
		jPanel9 = new javax.swing.JPanel();
		jPanel7 = new javax.swing.JPanel();
		rbFullOutput = new javax.swing.JRadioButton();
		rbShortOutput = new javax.swing.JRadioButton();
		jPanel2 = new javax.swing.JPanel();
		cbAllKVPs = new javax.swing.JCheckBox();

		setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.PAGE_AXIS));

		jPanel10.setBorder(javax.swing.BorderFactory.createTitledBorder("Search range"));
		jPanel10.setLayout(new javax.swing.BoxLayout(jPanel10, javax.swing.BoxLayout.PAGE_AXIS));

		jPanel11.setLayout(new javax.swing.BoxLayout(jPanel11, javax.swing.BoxLayout.LINE_AXIS));

		buttonGroup2.add(jrbEverywhere);
		jrbEverywhere.setText("Everywhere");
		jPanel11.add(jrbEverywhere);

		buttonGroup2.add(jrbOnlySelected);
		jrbOnlySelected.setText("Only below attributes");
		jPanel11.add(jrbOnlySelected);

		jPanel10.add(jPanel11);

		jPanel12.setLayout(new javax.swing.BoxLayout(jPanel12, javax.swing.BoxLayout.PAGE_AXIS));

		jPanel13.setLayout(new javax.swing.BoxLayout(jPanel13, javax.swing.BoxLayout.LINE_AXIS));

		lbSearchString.setText("Search string");
		jPanel13.add(lbSearchString);

		tfSearchString.setEditable(true);
		tfSearchString.setMaximumSize(new java.awt.Dimension(32767, 22));
		tfSearchString.setName(""); // NOI18N
		jPanel13.add(tfSearchString);

		jPanel12.add(jPanel13);

		jPanel6.setLayout(new javax.swing.BoxLayout(jPanel6, javax.swing.BoxLayout.LINE_AXIS));

		lbObjectName.setText("Object name");
		jPanel6.add(lbObjectName);

		tfObjectName.setEditable(true);
		tfObjectName.setMaximumSize(new java.awt.Dimension(32767, 22));
		tfObjectName.setName(""); // NOI18N
		jPanel6.add(tfObjectName);

		jPanel12.add(jPanel6);

		jPanel3.setLayout(new javax.swing.BoxLayout(jPanel3, javax.swing.BoxLayout.LINE_AXIS));

		lbSection.setText("Section name");
		jPanel3.add(lbSection);

		tfSection.setEditable(true);
		tfSection.setMaximumSize(new java.awt.Dimension(32767, 22));
		tfSection.setName(""); // NOI18N
		jPanel3.add(tfSection);

		jPanel12.add(jPanel3);

		jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.LINE_AXIS));

		lbOption.setText("Option name");
		jPanel1.add(lbOption);

		tfOption.setEditable(true);
		tfOption.setMaximumSize(new java.awt.Dimension(32767, 22));
		tfOption.setName(""); // NOI18N
		jPanel1.add(tfOption);

		jPanel12.add(jPanel1);

		jPanel5.setLayout(new javax.swing.BoxLayout(jPanel5, javax.swing.BoxLayout.LINE_AXIS));

		lbOptionValue.setText("Option Value");
		jPanel5.add(lbOptionValue);

		tfOptionValue.setEditable(true);
		tfOptionValue.setMaximumSize(new java.awt.Dimension(32767, 22));
		tfOptionValue.setName(""); // NOI18N
		jPanel5.add(tfOptionValue);

		jPanel12.add(jPanel5);

		jPanel10.add(jPanel12);

		add(jPanel10);

		jpObjectTypes.setBorder(javax.swing.BorderFactory.createTitledBorder("Object types"));
		jpObjectTypes.setLayout(new javax.swing.BoxLayout(jpObjectTypes, javax.swing.BoxLayout.LINE_AXIS));
		add(jpObjectTypes);

		jPanel4.setLayout(new javax.swing.BoxLayout(jPanel4, javax.swing.BoxLayout.LINE_AXIS));

		jPanel8.setLayout(new javax.swing.BoxLayout(jPanel8, javax.swing.BoxLayout.PAGE_AXIS));

		cbIsRegex.setText("Regular expression");
		jPanel8.add(cbIsRegex);

		cbCaseSensitive.setText("Case sensitive");
		jPanel8.add(cbCaseSensitive);

		jPanel4.add(jPanel8);

		jPanel9.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
		jPanel9.setLayout(new javax.swing.BoxLayout(jPanel9, javax.swing.BoxLayout.LINE_AXIS));

		jPanel7.setLayout(new javax.swing.BoxLayout(jPanel7, javax.swing.BoxLayout.PAGE_AXIS));

		buttonGroup1.add(rbFullOutput);
		rbFullOutput.setText("Print full output");
		rbFullOutput.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				rbFullOutputActionPerformed(evt);
			}
		});
		jPanel7.add(rbFullOutput);

		buttonGroup1.add(rbShortOutput);
		rbShortOutput.setText("Print abbreviated output");
		rbShortOutput.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				rbFullOutputActionPerformed(evt);
			}
		});
		jPanel7.add(rbShortOutput);

		jPanel9.add(jPanel7);

		jPanel2.setLayout(new javax.swing.BoxLayout(jPanel2, javax.swing.BoxLayout.LINE_AXIS));

		cbAllKVPs.setText("all KVPs in section matched");
		jPanel2.add(cbAllKVPs);

		jPanel9.add(jPanel2);

		jPanel4.add(jPanel9);

		add(jPanel4);
	}// </editor-fold>//GEN-END:initComponents

	private void rbFullOutputActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_rbFullOutputActionPerformed
		cbAllKVPs.setEnabled(!isFullOutputSelected());
	}// GEN-LAST:event_rbFullOutputActionPerformed

	@Override
	public String getSection() {
		return Utils.swing.Swing.checkBoxSelection(tfSection);
	}

	@Override
	public String getObjName() {
		if (!isSearchAll()) {
			return Utils.swing.Swing.checkBoxSelection(tfObjectName);
		} else {
			return null;
		}
	}

	@Override
	public String getOption() {
		return Utils.swing.Swing.checkBoxSelection(tfOption);
	}

	@Override
	public String getValue() {
		return Utils.swing.Swing.checkBoxSelection(tfOptionValue);
	}

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.ButtonGroup buttonGroup1;
	private javax.swing.ButtonGroup buttonGroup2;
	private javax.swing.JCheckBox cbAllKVPs;
	private javax.swing.JCheckBox cbCaseSensitive;
	private javax.swing.JCheckBox cbIsRegex;
	private javax.swing.JPanel jPanel1;
	private javax.swing.JPanel jPanel10;
	private javax.swing.JPanel jPanel11;
	private javax.swing.JPanel jPanel12;
	private javax.swing.JPanel jPanel13;
	private javax.swing.JPanel jPanel2;
	private javax.swing.JPanel jPanel3;
	private javax.swing.JPanel jPanel4;
	private javax.swing.JPanel jPanel5;
	private javax.swing.JPanel jPanel6;
	private javax.swing.JPanel jPanel7;
	private javax.swing.JPanel jPanel8;
	private javax.swing.JPanel jPanel9;
	private javax.swing.JPanel jpObjectTypes;
	private javax.swing.JRadioButton jrbEverywhere;
	private javax.swing.JRadioButton jrbOnlySelected;
	private javax.swing.JLabel lbObjectName;
	private javax.swing.JLabel lbOption;
	private javax.swing.JLabel lbOptionValue;
	private javax.swing.JLabel lbSearchString;
	private javax.swing.JLabel lbSection;
	private javax.swing.JRadioButton rbFullOutput;
	private javax.swing.JRadioButton rbShortOutput;
	private javax.swing.JComboBox<String> tfObjectName;
	private javax.swing.JComboBox<String> tfOption;
	private javax.swing.JComboBox<String> tfOptionValue;
	private javax.swing.JComboBox<String> tfSearchString;
	private javax.swing.JComboBox<String> tfSection;
	// End of variables declaration//GEN-END:variables

	@Override
	public boolean isSearchAll() {
		return jrbEverywhere.isSelected();
	}

	@Override
	public String getAllSearch() {
		return Utils.swing.Swing.checkBoxSelection(tfSearchString);

	}

	@Override
	public String getSearchSummary() {
		final StringBuilder buf = new StringBuilder();
		buf.append("Object by Annex;");
		buf.append(" types [").append(StringUtils.join(getSelectedObjectTypes(), ",")).append("]");

		if (isSearchAll()) {
			buf
				.append(" term \"")
				.append(getAllSearch())
				.append("\" in all fields (also Options for Application), including object attributes");
		} else {
			if (getObjName() != null) {
				buf.append(" name [").append(getObjName()).append("]");
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
		buf.append("; ").append(isFullOutputSelected() ? "full" : "short").append(" output");
		buf.append("; ").append(isAllKVPsInOutput() ? "all KVPs in matched section" : "just matched KVPs/section");

		return buf.toString();
	}

	@Override
	public void setChoices(final Collection<String> choices) {
		Utils.swing.Swing.setChoices(tfSearchString, choices);
		Utils.swing.Swing.setChoices(tfObjectName, choices);
		Utils.swing.Swing.setChoices(tfOption, choices);
		Utils.swing.Swing.setChoices(tfOptionValue, choices);
		Utils.swing.Swing.setChoices(tfSection, choices);
	}

	@Override
	public Collection<String> getChoices() {

		return (isSearchAll()) ? Utils.swing.Swing.getChoices(tfSearchString)
				: Utils.swing.Swing.getChoices(tfObjectName, tfOption, tfOptionValue, tfSection);
	}

	private void modelUncheck(final CheckBoxListSelectionModel selectionModel, final GEnum[] gEnum) {
		final HashSet<GEnum> en = new HashSet<>(Arrays.asList(gEnum));

		final DefaultListModel model = (DefaultListModel) selectionModel.getModel();
		selectionModel.clearSelection();
		for (int i = 0; i < model.size(); i++) {
			if (i != selectionModel.getAllEntryIndex()) {
				final CfgObjectTypeMenu get = (CfgObjectTypeMenu) model.get(i);
				if (!en.contains(get.getType())) {
					selectionModel.addSelectionInterval(i, i);
				}
			}
		}

	}

}
