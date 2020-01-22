/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.airbnb.confservutils;

import static Utils.Swing.checkBoxSelection;
import com.genesyslab.platform.applicationblocks.com.CfgObject;
import com.genesyslab.platform.applicationblocks.com.objects.CfgAccessGroup;
import com.genesyslab.platform.commons.GEnum;
import com.genesyslab.platform.commons.collections.KeyValueCollection;
import com.genesyslab.platform.commons.collections.KeyValuePair;
import com.genesyslab.platform.commons.collections.ValueType;
import com.genesyslab.platform.configuration.protocol.types.CfgDNType;
import com.genesyslab.platform.configuration.protocol.types.CfgObjectType;
import com.genesyslab.platform.configuration.protocol.types.CfgScriptType;
import com.genesyslab.platform.configuration.protocol.types.CfgTransactionType;
import com.jidesoft.swing.CheckBoxList;
import com.jidesoft.swing.CheckBoxListSelectionModel;
import java.awt.ScrollPane;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author stepan_sydoruk
 */
public class AnnexReplace extends javax.swing.JPanel implements ISearchSettings, ISearchCommon {

    private final AppForm theForm;

    /**
     * Creates new form AppByDBID
     */
    public AnnexReplace(AppForm _theForm) {
        initComponents();
        theForm = _theForm;
        Utils.Swing.restrictHeight(tfObjectName);
        Utils.Swing.restrictHeight(tfOption);
        Utils.Swing.restrictHeight(tfOptionValue);
        Utils.Swing.restrictHeight(tfSection);
        clm = new DefaultListModel();
        clb = new CheckBoxList((ListModel) clm);
        jpObjectTypes.add(new JScrollPane(clb));
        Main.loadGenesysTypes(clb, CfgObjectType.values(), new GEnum[]{
            CfgObjectType.CFGNoObject,
            CfgObjectType.CFGMaxObjectType,
            CfgObjectType.CFGPersonLastLogin,
            CfgObjectType.CFGCallingList,
            CfgObjectType.CFGField,
            CfgObjectType.CFGFormat,
            CfgObjectType.CFGAppPrototype,
            CfgObjectType.CFGCampaign,
            CfgObjectType.CFGCampaignGroup,
            CfgObjectType.CFGTableAccess,
            CfgObjectType.CFGFilter

        });

        modelUncheck(clb.getCheckBoxListSelectionModel(), new GEnum[]{
            CfgObjectType.CFGPerson,
            CfgObjectType.CFGDN,
            CfgObjectType.CFGAccessGroup,
            CfgObjectType.CFGAgentLogin});

        ItemListener itemListener = new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {

                AbstractButton aButton = (AbstractButton) e.getSource();
                ButtonModel aModel = aButton.getModel();

                rbActionChanged(aButton.getModel().isSelected(), aButton);

            }
        };

        rbReplaceWith.addItemListener(itemListener);
        rbAddSection.addItemListener(itemListener);
        rbRemove.addItemListener(itemListener);
        rbRestoreFromBackup.addItemListener(itemListener);
        rbReplaceWith.setSelected(true);

    }

    public List<CfgObjectType> getSelectedObjectTypes() {
        ArrayList<CfgObjectType> ret = new ArrayList();
        for (Object checkBoxListSelectedValue : clb.getCheckBoxListSelectedValues()) {
            ret.add((CfgObjectType) ((CfgObjectTypeMenu) checkBoxListSelectedValue).getType());
        }
        return ret;

    }

    CheckBoxList clb;
    DefaultListModel clm;

    private void rbActionChanged(boolean isSelected, AbstractButton rbButton) {
        jpActions.setVisible(false);
        cbMakeBackup.setEnabled(rbAddSection.isSelected() || rbReplaceWith.isSelected());
        tfAddSection.setEnabled(rbAddSection.isSelected());
        tfAddKey.setEnabled(rbAddSection.isSelected());
        tfAddValue.setEnabled(rbAddSection.isSelected());
        lbKey.setEnabled(rbAddSection.isSelected());
        lbValue.setEnabled(rbAddSection.isSelected());
        tfReplaceWith.setEnabled(rbReplaceWith.isSelected());

        jpActions.setVisible(true);
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
        return false;
    }

    private GEnum cfgObjType(Object o) {
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
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup2 = new javax.swing.ButtonGroup();
        bgReplaceAction = new javax.swing.ButtonGroup();
        jPanel2 = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        jPanel12 = new javax.swing.JPanel();
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
        jpActions = new javax.swing.JPanel();
        jPanel13 = new javax.swing.JPanel();
        jPanel14 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel15 = new javax.swing.JPanel();
        jPanel16 = new javax.swing.JPanel();
        rbReplaceWith = new javax.swing.JRadioButton();
        tfReplaceWith = new javax.swing.JComboBox<>();
        cbMakeBackup = new javax.swing.JCheckBox();
        jPanel17 = new javax.swing.JPanel();
        rbAddSection = new javax.swing.JRadioButton();
        tfAddSection = new javax.swing.JComboBox<>();
        lbKey = new javax.swing.JLabel();
        tfAddKey = new javax.swing.JComboBox<>();
        lbValue = new javax.swing.JLabel();
        tfAddValue = new javax.swing.JComboBox<>();
        rbRemove = new javax.swing.JRadioButton();
        rbRestoreFromBackup = new javax.swing.JRadioButton();

        setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.PAGE_AXIS));

        jPanel2.setLayout(new javax.swing.BoxLayout(jPanel2, javax.swing.BoxLayout.PAGE_AXIS));

        jPanel10.setBorder(javax.swing.BorderFactory.createTitledBorder("Search range"));
        jPanel10.setLayout(new javax.swing.BoxLayout(jPanel10, javax.swing.BoxLayout.PAGE_AXIS));

        jPanel12.setLayout(new javax.swing.BoxLayout(jPanel12, javax.swing.BoxLayout.PAGE_AXIS));

        jPanel6.setLayout(new javax.swing.BoxLayout(jPanel6, javax.swing.BoxLayout.LINE_AXIS));

        lbObjectName.setText("Object name");
        jPanel6.add(lbObjectName);

        tfObjectName.setEditable(true);
        jPanel6.add(tfObjectName);

        jPanel12.add(jPanel6);

        jPanel3.setLayout(new javax.swing.BoxLayout(jPanel3, javax.swing.BoxLayout.LINE_AXIS));

        lbSection.setText("Section name");
        jPanel3.add(lbSection);

        tfSection.setEditable(true);
        jPanel3.add(tfSection);

        jPanel12.add(jPanel3);

        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.LINE_AXIS));

        lbOption.setText("Option name");
        jPanel1.add(lbOption);

        tfOption.setEditable(true);
        jPanel1.add(tfOption);

        jPanel12.add(jPanel1);

        jPanel5.setLayout(new javax.swing.BoxLayout(jPanel5, javax.swing.BoxLayout.LINE_AXIS));

        lbOptionValue.setText("Option Value");
        jPanel5.add(lbOptionValue);

        tfOptionValue.setEditable(true);
        jPanel5.add(tfOptionValue);

        jPanel12.add(jPanel5);

        jPanel10.add(jPanel12);

        jPanel2.add(jPanel10);

        jpObjectTypes.setBorder(javax.swing.BorderFactory.createTitledBorder("Object types"));
        jpObjectTypes.setLayout(new javax.swing.BoxLayout(jpObjectTypes, javax.swing.BoxLayout.LINE_AXIS));
        jPanel2.add(jpObjectTypes);

        jPanel4.setLayout(new javax.swing.BoxLayout(jPanel4, javax.swing.BoxLayout.LINE_AXIS));

        jPanel8.setLayout(new javax.swing.BoxLayout(jPanel8, javax.swing.BoxLayout.LINE_AXIS));

        cbIsRegex.setText("Regular expression");
        jPanel8.add(cbIsRegex);

        cbCaseSensitive.setSelected(true);
        cbCaseSensitive.setText("Case sensitive");
        jPanel8.add(cbCaseSensitive);

        jPanel4.add(jPanel8);

        jPanel2.add(jPanel4);

        add(jPanel2);

        jpActions.setBorder(javax.swing.BorderFactory.createTitledBorder("Replace parameters"));
        jpActions.setLayout(new javax.swing.BoxLayout(jpActions, javax.swing.BoxLayout.PAGE_AXIS));

        jPanel13.setLayout(new javax.swing.BoxLayout(jPanel13, javax.swing.BoxLayout.LINE_AXIS));

        jPanel14.setLayout(new javax.swing.BoxLayout(jPanel14, javax.swing.BoxLayout.LINE_AXIS));

        jLabel1.setText("Found value");
        jPanel14.add(jLabel1);

        jPanel13.add(jPanel14);

        jPanel15.setLayout(new java.awt.GridLayout(0, 1));

        jPanel16.setLayout(new javax.swing.BoxLayout(jPanel16, javax.swing.BoxLayout.LINE_AXIS));

        bgReplaceAction.add(rbReplaceWith);
        rbReplaceWith.setText("Replace value with");
        jPanel16.add(rbReplaceWith);

        tfReplaceWith.setEditable(true);
        jPanel16.add(tfReplaceWith);

        cbMakeBackup.setSelected(true);
        cbMakeBackup.setText("make backup");
        jPanel16.add(cbMakeBackup);

        jPanel15.add(jPanel16);

        jPanel17.setLayout(new javax.swing.BoxLayout(jPanel17, javax.swing.BoxLayout.LINE_AXIS));

        bgReplaceAction.add(rbAddSection);
        rbAddSection.setText("add section");
        jPanel17.add(rbAddSection);

        tfAddSection.setEditable(true);
        jPanel17.add(tfAddSection);

        lbKey.setText("key");
        jPanel17.add(lbKey);

        tfAddKey.setEditable(true);
        jPanel17.add(tfAddKey);

        lbValue.setText("value");
        jPanel17.add(lbValue);

        tfAddValue.setEditable(true);
        jPanel17.add(tfAddValue);

        jPanel15.add(jPanel17);

        bgReplaceAction.add(rbRemove);
        rbRemove.setText("Remove");
        jPanel15.add(rbRemove);

        bgReplaceAction.add(rbRestoreFromBackup);
        rbRestoreFromBackup.setText("restore from backup");
        jPanel15.add(rbRestoreFromBackup);

        jPanel13.add(jPanel15);

        jpActions.add(jPanel13);

        add(jpActions);
    }// </editor-fold>//GEN-END:initComponents

    @Override
    public String getSection() {
        return checkBoxSelection(tfSection);
    }

    @Override
    public String getObjName() {
        if (!isSearchAll()) {
            return checkBoxSelection(tfObjectName);
        } else {
            return null;
        }
    }

    @Override
    public String getOption() {
        return checkBoxSelection(tfOption);
    }

    @Override
    public String getValue() {
        return checkBoxSelection(tfOptionValue);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup bgReplaceAction;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JCheckBox cbCaseSensitive;
    private javax.swing.JCheckBox cbIsRegex;
    private javax.swing.JCheckBox cbMakeBackup;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jpActions;
    private javax.swing.JPanel jpObjectTypes;
    private javax.swing.JLabel lbKey;
    private javax.swing.JLabel lbObjectName;
    private javax.swing.JLabel lbOption;
    private javax.swing.JLabel lbOptionValue;
    private javax.swing.JLabel lbSection;
    private javax.swing.JLabel lbValue;
    private javax.swing.JRadioButton rbAddSection;
    private javax.swing.JRadioButton rbRemove;
    private javax.swing.JRadioButton rbReplaceWith;
    private javax.swing.JRadioButton rbRestoreFromBackup;
    private javax.swing.JComboBox<String> tfAddKey;
    private javax.swing.JComboBox<String> tfAddSection;
    private javax.swing.JComboBox<String> tfAddValue;
    private javax.swing.JComboBox<String> tfObjectName;
    private javax.swing.JComboBox<String> tfOption;
    private javax.swing.JComboBox<String> tfOptionValue;
    private javax.swing.JComboBox<String> tfReplaceWith;
    private javax.swing.JComboBox<String> tfSection;
    // End of variables declaration//GEN-END:variables

    @Override
    public boolean isSearchAll() {
        return false;
    }

    @Override
    public String getAllSearch() {
        return null;

    }

    public String getSearchSummary(int maxTypes) {
        StringBuilder buf = new StringBuilder();
        buf.append("Object by Annex;");
        buf.append(" types [\n");
        int num;

        num = (maxTypes < 0 || maxTypes > getSelectedObjectTypes().size() - 1)
                ? getSelectedObjectTypes().size()
                : maxTypes;

        buf.append(StringUtils.join(getSelectedObjectTypes().subList(0, num), ",\n\t"));
        if (maxTypes > 0 && num < getSelectedObjectTypes().size()) {
            buf.append("\n...(")
                    .append(getSelectedObjectTypes().size() - num)
                    .append(" more) ");
        }
        buf.append("]");

        if (isSearchAll()) {
            buf.append(" term \"")
                    .append(getAllSearch())
                    .append("\" in all fields, including object attributes");
        } else {
            if (getObjName() != null) {
                buf.append("\n\tname [")
                        .append(getObjName())
                        .append("]");
            }
            if (getSection() != null) {
                buf.append(" section [")
                        .append(getSection())
                        .append("]");
            }
            if (getOption() != null) {
                buf.append(" option [")
                        .append(getOption())
                        .append("]");
            }
            if (getValue() != null) {
                buf.append(" value [")
                        .append(getValue())
                        .append("]");
            }
        }
        buf.append(" rx[").append(isRegex() ? "yes" : "no").append("]");
        buf.append(" CaSe[").append(isCaseSensitive() ? "yes" : "no").append("]");
        buf.append("\naction: ").append(rbReplaceWith.isSelected() ? getReplaceWith()
                : rbAddSection.isSelected() ? getAddSection()
                : rbRemove.isSelected() ? rbRemove.getText()
                : rbRestoreFromBackup.isSelected() ? rbRestoreFromBackup.getText()
                : "");

        return buf.toString();
    }

    @Override
    public String getSearchSummary() {
        return getSearchSummary(-1);
    }

    @Override
    public void setChoices(Collection<String> choices) {

        Utils.Swing.setChoices(tfObjectName, choices);
        Utils.Swing.setChoices(tfOption, choices);
        Utils.Swing.setChoices(tfOptionValue, choices);
        Utils.Swing.setChoices(tfSection, choices);
    }

    @Override
    public Collection<String> getChoices() {

        return Utils.Swing.getChoices(tfObjectName,
                tfOption,
                tfOptionValue,
                tfSection);
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

    static final private String BACKUP_PREFIX = "###";

    String estimateUpdateObj(CfgObject obj, KeyValueCollection kv, ConfigServerManager configServerManager) {

        StringBuilder ret = new StringBuilder();

        if (rbAddSection.isSelected()) {
            ret.append("addiong option: [")
                    .append(checkBoxSelection(tfAddSection))
                    .append("]/\"")
                    .append(checkBoxSelection(tfAddKey))
                    .append("\"=\"")
                    .append(checkBoxSelection(tfAddValue))
                    .append("\"\n");
        } else if (rbRemove.isSelected()) {
            ret.append("deleting kvp ")
                    .append(kv)
                    .append("\n");
        } else if (rbReplaceWith.isSelected()) {
//        sectionsList = new KeyValueCollection();
//
//        sectionOptions = new KeyValueCollection();
//        sectionsList.addList("General", sectionOptions);
//        sectionOptions.addString("ServerDescription", "Main server of the IT department");
//        sectionOptions.addString("AdminAddress", "admin@somewhere.com");

            for (Object object : kv) {
                if (object instanceof KeyValuePair) {
                    KeyValuePair kvp = (KeyValuePair) object;
                    String section = kvp.getStringKey();
                    Object value = kvp.getValue();
                    ValueType valueType = kvp.getValueType();
                    if (valueType == ValueType.TKV_LIST) {
                        for (Object _kvInstance : (KeyValueCollection) value) {
                            KeyValuePair kvInstance = (KeyValuePair) _kvInstance;
//                            upd.addUpdateKey(section, kvInstance.getStringKey(), checkBoxSelection(tfReplaceWith));
                            ret.append("updating option value in [")
                                    .append(section)
                                    .append("]/\"")
                                    .append(kvInstance.getStringKey())
                                    .append("\" from \"")
                                    .append(kvInstance.getStringValue())
                                    .append("\" to \"")
                                    .append(checkBoxSelection(tfReplaceWith))
                                    .append("\"\n");
                            if (cbMakeBackup.isSelected()) {
                                String backupKey = BACKUP_PREFIX + kvInstance.getStringKey();
                                if (updateExisted(obj, section, backupKey, kvInstance.getStringValue())) {
//                                    upd.addUpdateKey(section, backupKey, kvInstance.getStringValue());
                                    ret.append("updating option value [")
                                            .append(section)
                                            .append("]/\"")
                                            .append(backupKey)
                                            .append("\" with value \"")
                                            .append(kvInstance.getStringValue())
                                            .append("\"\n");
                                } else {
//                                    upd.addAddKey(section, backupKey, kvInstance.getStringValue());
                                    ret.append("addiong option: [")
                                            .append(section)
                                            .append("]/\"")
                                            .append(backupKey)
                                            .append("\"=\"")
                                            .append(kvInstance.getStringValue())
                                            .append("\"\n");
                                }
                            }
                        }
                    } else {
                        logger.error("Unsupport value type: " + valueType + " obj: " + obj.toString());
                    }
                    logger.info(kvp);

                }

            }
//            upd.addUpdateKey(TOOL_TIP_TEXT_KEY, TOOL_TIP_TEXT_KEY, TOOL_TIP_TEXT_KEY);
        } else if (rbRestoreFromBackup.isSelected()) {
            ArrayList<UserProperties> allBackup = getAllBackup(obj);
            if (allBackup.isEmpty()) {
                theForm.requestOutput("No backup user properties");
            } else {
                for (UserProperties userProperties : allBackup) {
                    String origProperty = userProperties.key.substring(BACKUP_PREFIX.length());
                    setProperty(obj, ret, userProperties.section, origProperty, userProperties.value);
                    if (cbMakeBackup.isSelected()) {
                        String curValue = getCurValue(obj, userProperties.section, origProperty);
                        if (curValue != null) {
                            setProperty(obj, ret, userProperties.section, userProperties.key, curValue);
                        }
                    }
                }
            }
        }

//        upd.addAddKey("Caching", "CacheSizeLimit", "2044");
//        upd.addAddKey("TServer", "PrimaryRegion", "NA");
//        upd.addAddKey("us_urs_p", "event_arrive", "none");
//        upd.addDeleteKey("Caching", "CacheSizeLimit", "2044");
//        upd.commitUpdate();
        return ret.toString();
    }

    void updateObj(CfgObject obj, KeyValueCollection kv, ConfigServerManager configServerManager) {

        UpdateUserProperties upd = new UpdateUserProperties(configServerManager, obj.getObjectType(), obj.getObjectDbid());

        if (rbAddSection.isSelected()) {
            upd.addAddKey(checkBoxSelection(tfAddSection), checkBoxSelection(tfAddKey), checkBoxSelection(tfAddValue));
        } else if (rbRemove.isSelected()) {
            upd.addDeleteKey(kv);
        } else if (rbReplaceWith.isSelected()) {
//        sectionsList = new KeyValueCollection();
//
//        sectionOptions = new KeyValueCollection();
//        sectionsList.addList("General", sectionOptions);
//        sectionOptions.addString("ServerDescription", "Main server of the IT department");
//        sectionOptions.addString("AdminAddress", "admin@somewhere.com");

            for (Object object : kv) {
                if (object instanceof KeyValuePair) {
                    KeyValuePair kvp = (KeyValuePair) object;
                    String section = kvp.getStringKey();
                    Object value = kvp.getValue();
                    ValueType valueType = kvp.getValueType();
                    if (valueType == ValueType.TKV_LIST) {
                        for (Object _kvInstance : (KeyValueCollection) value) {
                            KeyValuePair kvInstance = (KeyValuePair) _kvInstance;
                            upd.addUpdateKey(section, kvInstance.getStringKey(), checkBoxSelection(tfReplaceWith));
                            if (cbMakeBackup.isSelected()) {
                                String backupKey = BACKUP_PREFIX + kvInstance.getStringKey();
                                if (updateExisted(obj, section, backupKey, kvInstance.getStringValue())) {
                                    upd.addUpdateKey(section, backupKey, kvInstance.getStringValue());
                                } else {
                                    upd.addAddKey(section, backupKey, kvInstance.getStringValue());
                                }
                            }
                        }
                    } else {
                        logger.error("Unsupport value type: " + valueType + " obj: " + obj.toString());
                    }
                    logger.info(kvp);

                }

            }
//            upd.addUpdateKey(TOOL_TIP_TEXT_KEY, TOOL_TIP_TEXT_KEY, TOOL_TIP_TEXT_KEY);
        } else if (rbRestoreFromBackup.isSelected()) {
            ArrayList<UserProperties> allBackup = getAllBackup(obj);
            if (allBackup.isEmpty()) {
                theForm.requestOutput("No backup user properties");
            } else {
                for (UserProperties userProperties : allBackup) {
                    String origProperty = userProperties.key.substring(BACKUP_PREFIX.length());
                    setProperty(obj, upd, userProperties.section, origProperty, userProperties.value);
                    if (cbMakeBackup.isSelected()) {
                        String curValue = getCurValue(obj, userProperties.section, origProperty);
                        if (curValue != null) {
                            setProperty(obj, upd, userProperties.section, userProperties.key, curValue);
                        }
                    }
                }
            }
        }

//        upd.addAddKey("Caching", "CacheSizeLimit", "2044");
//        upd.addAddKey("TServer", "PrimaryRegion", "NA");
//        upd.addAddKey("us_urs_p", "event_arrive", "none");
//        upd.addDeleteKey("Caching", "CacheSizeLimit", "2044");
        upd.commitUpdate();
    }

    private StringBuilder getReplaceWith() {
        return new StringBuilder()
                .append(rbReplaceWith.getText())
                .append(" (")
                .append(checkBoxSelection(tfReplaceWith))
                .append(") backup:")
                .append(cbMakeBackup.isSelected());
    }

    private StringBuilder getAddSection() {
        return new StringBuilder()
                .append(rbAddSection.getText())
                .append(" sect[")
                .append(checkBoxSelection(tfAddSection))
                .append("] key[:")
                .append(checkBoxSelection(tfAddKey))
                .append("] val[:")
                .append(checkBoxSelection(tfAddValue))
                .append("]");
    }

    private boolean updateExisted(CfgObject obj, String section, String stringKey, String stringValue) {
        KeyValueCollection property = (KeyValueCollection) obj.getProperty("userProperties");
        if (property != null && !property.isEmpty()) {
            for (Object object : property) {
                if (object instanceof KeyValuePair) {
                    KeyValuePair kvp = (KeyValuePair) object;
                    Object value = kvp.getValue();
                    ValueType valueType = kvp.getValueType();

                    if (kvp.getStringKey().equals(section)) {
                        if (valueType == ValueType.TKV_LIST) {
                            for (Object _kvInstance : (KeyValueCollection) value) {
                                KeyValuePair kvInstance = (KeyValuePair) _kvInstance;
                                if (kvInstance.getStringKey().equals(stringKey)) {
                                    return true;
                                }
                            }
                        } else {
                            theForm.showError("Unsupport value type: " + valueType + " obj: " + obj.toString());
                        }
                    }
                }
            }
        }
        return false;
    }

    private ArrayList<UserProperties> getAllBackup(CfgObject obj) {
        ArrayList<UserProperties> ret = new ArrayList<>();
        KeyValueCollection property = (KeyValueCollection) obj.getProperty("userProperties");
        if (property != null && !property.isEmpty()) {
            for (Object object : property) {
                if (object instanceof KeyValuePair) {
                    KeyValuePair kvp = (KeyValuePair) object;
                    Object value = kvp.getValue();
                    ValueType valueType = kvp.getValueType();

                    if (valueType == ValueType.TKV_LIST) {
                        for (Object _kvInstance : (KeyValueCollection) value) {
                            KeyValuePair kvInstance = (KeyValuePair) _kvInstance;
                            if (kvInstance.getStringKey().startsWith(BACKUP_PREFIX)) {
                                ret.add(new UserProperties(kvp.getStringKey(), kvInstance.getStringKey(), kvInstance.getStringValue()));
                            }
                        }
                    } else {
                        theForm.showError("Unsupport value type: " + valueType + " obj: " + obj.toString());
                    }
                }
            }
        }
        return ret;
    }

    private void setProperty(CfgObject obj, UpdateUserProperties upd, String section, String key, String value) {
        if (updateExisted(obj, section, key, value)) {
            upd.addUpdateKey(section, key, value);
        } else {
            upd.addAddKey(section, key, value);
        }
    }

    private void setProperty(CfgObject obj, StringBuilder buf, String section, String key, String value) {

        if (updateExisted(obj, section, key, value)) {
//            upd.addUpdateKey(section, key, value);
            buf.append("updating option value [")
                    .append(section)
                    .append("]/\"")
                    .append(key)
                    .append("\" with value \"")
                    .append(value)
                    .append("\"\n");
        } else {
//            upd.addAddKey(section, key, value);
            buf.append("adding option: [")
                    .append(section)
                    .append("]/\"")
                    .append(key)
                    .append("\"=\"")
                    .append(value)
                    .append("\"\n");
        }
    }

    private String getCurValue(CfgObject obj, String section, String origProperty) {
        KeyValueCollection property = (KeyValueCollection) obj.getProperty("userProperties");
        if (property != null && !property.isEmpty()) {
            for (Object object : property) {
                if (object instanceof KeyValuePair) {
                    KeyValuePair kvp = (KeyValuePair) object;
                    if (kvp.getStringKey().equals(section)) {
                        Object value = kvp.getValue();
                        ValueType valueType = kvp.getValueType();

                        if (valueType == ValueType.TKV_LIST) {
                            for (Object _kvInstance : (KeyValueCollection) value) {
                                KeyValuePair kvInstance = (KeyValuePair) _kvInstance;
                                if (kvInstance.getStringKey().equals(origProperty)) {
                                    return kvInstance.getStringValue();
                                }
                            }
                        } else {
                            theForm.showError("Unsupport value type: " + valueType + " obj: " + obj.toString());
                        }
                    }
                }
            }
        }
        return null;
    }

    boolean checkParameters() {
        if (rbAddSection.isSelected()) {
            if (StringUtils.isBlank(checkBoxSelection(tfAddSection))
                    || StringUtils.isBlank(checkBoxSelection(tfAddKey))
                    || StringUtils.isBlank(checkBoxSelection(tfAddValue))) {
                JOptionPane.showMessageDialog(theForm, "To create an option all of the section, key and value needs to be specified", "Cannot proceed", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } else if (rbReplaceWith.isSelected()) {
            if (StringUtils.isBlank(checkBoxSelection(tfReplaceWith))) {
                JOptionPane.showMessageDialog(theForm, "\"Replace with \" string cannot be blank", "Cannot proceed", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }
        return true;
    }

    String getSearchSummaryHTML() {
        StringBuilder ret = new StringBuilder();
        ret.append("<html>").append(getSearchSummary(2).replaceAll("\n", "<br>")).append("</html>");
        return ret.toString();

    }

    class UserProperties {

        private final String key;
        private final String section;
        private final String value;

        public UserProperties(String _section, String _key, String _value) {
            section = _section;
            key = _key;
            value = _value;

        }
    }

}
