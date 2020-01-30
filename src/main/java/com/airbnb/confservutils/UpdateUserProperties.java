/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.airbnb.confservutils;

import static Utils.Swing.checkBoxSelection;
import com.genesyslab.platform.applicationblocks.com.CfgObject;
import com.genesyslab.platform.applicationblocks.com.IConfService;
import com.genesyslab.platform.commons.collections.KeyValueCollection;
import com.genesyslab.platform.commons.collections.KeyValuePair;
import com.genesyslab.platform.commons.collections.ValueType;
import com.genesyslab.platform.configuration.protocol.confserver.requests.objects.RequestUpdateObject;
import com.genesyslab.platform.configuration.protocol.metadata.CfgMetadata;
import com.genesyslab.platform.configuration.protocol.obj.ConfObject;
import com.genesyslab.platform.configuration.protocol.obj.ConfObjectDelta;
import com.genesyslab.platform.configuration.protocol.types.CfgObjectType;
import java.util.ArrayList;
import java.util.HashMap;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author stepan_sydoruk
 */
public class UpdateUserProperties {

    static String uncommented(String currentValue) {
        if (currentValue.startsWith(BACKUP_PREFIX)) {
            return currentValue.substring(BACKUP_PREFIX.length());
        } else {
            return currentValue;
        }

    }

    private final int DBID;
    private final CfgObjectType objType;
    private final ConfigServerManager cfgManager;
    private final AppForm theForm;

    UpdateUserProperties(ConfigServerManager _configServerManager, CfgObjectType _objType, int _dbid, AppForm _theForm) {
        this.cfgManager = _configServerManager;
        this.objType = _objType;
        this.DBID = _dbid;
        theForm = _theForm;
    }
    KeyValueCollection updateSections = new KeyValueCollection();
    KeyValueCollection createSections = new KeyValueCollection();
    KeyValueCollection deleteSections = new KeyValueCollection();

    public static KeyValueCollection getSection(KeyValueCollection sections, String section) {
        KeyValueCollection list = sections.getList(section);
        if (list == null) {
            list = new KeyValueCollection();
            sections.addList(section, list);
        }
        return list;

    }

    void addAddKey(String section, String key, String val) {
        getSection(createSections, section).addString(key, val);
    }

    void addUpdateKey(String section, String key, String val) {
        getSection(updateSections, section).addString(key, val);
    }

    void addDeleteKey(String section, String key, String val) {
        getSection(deleteSections, section).addString(key, val);
    }

    private static final Logger logger = Main.getLogger();

    void commitUpdate() {
        if (!updateSections.isEmpty() || !createSections.isEmpty() || !deleteSections.isEmpty()) {
            IConfService service = cfgManager.getService();
            CfgMetadata metaData = service.getMetaData();
            ConfObjectDelta d = new ConfObjectDelta(metaData, objType);

            ConfObject obj1 = (ConfObject) d.getOrCreatePropertyValue(deltaByType.get(objType));

            obj1.setPropertyValue("DBID", DBID);              // - required

            if (!updateSections.isEmpty()) {
                d.setPropertyValue("changedUserProperties", updateSections);
            }

            if (!deleteSections.isEmpty()) {
                d.setPropertyValue("deletedUserProperties", deleteSections);
            }

            if (!createSections.isEmpty()) {

                obj1.setPropertyValue("userProperties", createSections);
            }

            RequestUpdateObject reqUpdate = RequestUpdateObject.create();
            logger.info("++" + d.toString());
            reqUpdate.setObjectDelta(d);

            cfgManager.execRequest(reqUpdate, objType);
        }

    }

    public static final HashMap<CfgObjectType, String> deltaByType = createDeltaByType();

    private static HashMap<CfgObjectType, String> createDeltaByType() {
        HashMap<CfgObjectType, String> ret = new HashMap<>();
        ret.put(CfgObjectType.CFGDN, "deltaDN");
        ret.put(CfgObjectType.CFGTransaction, "deltaTransaction");

        return ret;
    }

    void addDeleteKey(KeyValueCollection kv) {
        deleteSections = kv;
    }

    static final public String BACKUP_PREFIX = "#";

    public void updateObj(IUpdateSettings us, CfgObject obj, KeyValueCollection kv, ConfigServerManager configServerManager) {

        switch (us.getUpdateAction()) {
            case RENAME_SECTION:
                for (Object object : kv) {
                    if (object instanceof KeyValuePair) {
                        KeyValuePair kvp = (KeyValuePair) object;
                        String section = kvp.getStringKey();
                        Object value = kvp.getValue();
                        ValueType valueType = kvp.getValueType();
                        if (valueType == ValueType.TKV_LIST) {
                            for (Object _kvInstance : (KeyValueCollection) value) {
                                KeyValuePair kvInstance = (KeyValuePair) _kvInstance;
                                String newKey = us.getReplaceKey(kvInstance.getStringKey());
                                if (newKey.equals(kvInstance.getStringKey())) {
                                    theForm.requestOutput("\t!! skipping, no change in key\n");
                                } else {

                                    if (updateExisted(obj, section, newKey, kvInstance.getStringValue())) {
                                        addDeleteKey(section, newKey, kvInstance.getStringValue());
                                    }
                                    addAddKey(section, newKey, kvInstance.getStringValue());
                                    addDeleteKey(section, kvInstance.getStringKey(), kvInstance.getStringValue());
                                }
                            }
                        } else {
                            logger.error("Unsupport value type: " + valueType + " obj: " + obj.toString());
                        }
                        logger.info(kvp);
                    }
                }
                break;

            case ADD_SECTION:
                addAddKey(us.addSection(), us.addKey(), us.addValue());
                break;

            case REMOVE:
                addDeleteKey(kv);
                break;

            case REPLACE_WITH:
                for (Object object : kv) {
                    if (object instanceof KeyValuePair) {
                        KeyValuePair kvp = (KeyValuePair) object;
                        String section = kvp.getStringKey();
                        Object value = kvp.getValue();
                        ValueType valueType = kvp.getValueType();
                        if (valueType == ValueType.TKV_LIST) {
                            for (Object _kvInstance : (KeyValueCollection) value) {
                                KeyValuePair kvInstance = (KeyValuePair) _kvInstance;
                                addUpdateKey(section, kvInstance.getStringKey(), us.replaceWith(kvInstance.getStringValue()));
                                if (us.isMakeBackup()) {
                                    String backupKey = BACKUP_PREFIX + kvInstance.getStringKey();
                                    if (updateExisted(obj, section, backupKey, kvInstance.getStringValue())) {
                                        addUpdateKey(section, backupKey, kvInstance.getStringValue());
                                    } else {
                                        addAddKey(section, backupKey, kvInstance.getStringValue());
                                    }
                                }
                            }
                        } else {
                            logger.error("Unsupport value type: " + valueType + " obj: " + obj.toString());
                        }
                        logger.info(kvp);

                    }

                }
                break;

            case RESTORE_FROM_BACKUP:
                ArrayList<UserProperties> allBackup = getAllBackup(obj);
                if (allBackup.isEmpty()) {
                    theForm.requestOutput("No backup user properties");
                } else {
                    for (UserProperties userProperties : allBackup) {
                        String origProperty = userProperties.key.substring(BACKUP_PREFIX.length());
                        setProperty(obj, userProperties.section, origProperty, userProperties.value);
                        if (us.isMakeBackup()) {
                            String curValue = getCurValue(obj, userProperties.section, origProperty);
                            if (curValue != null) {
                                setProperty(obj, userProperties.section, userProperties.key, curValue);
                            }
                        }
                    }
                }
                break;
        }

        commitUpdate();
    }

    public static String getCommentedKey(String key) {
        return BACKUP_PREFIX + key;
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

    public String estimateUpdateObj(IUpdateSettings us, CfgObject obj, KeyValueCollection kv, ConfigServerManager configServerManager) {

        StringBuilder ret = new StringBuilder();

        switch (us.getUpdateAction()) {
            case RENAME_SECTION:
                for (Object object : kv) {
                    if (object instanceof KeyValuePair) {
                        KeyValuePair kvp = (KeyValuePair) object;
                        String section = kvp.getStringKey();
                        Object value = kvp.getValue();
                        ValueType valueType = kvp.getValueType();
                        if (valueType == ValueType.TKV_LIST) {
                            for (Object _kvInstance : (KeyValueCollection) value) {
                                KeyValuePair kvInstance = (KeyValuePair) _kvInstance;
                                String newKey = us.getReplaceKey(kvInstance.getStringKey());
                                ret.append(">> " + kvpToString(section, kvInstance.getStringKey(), kvInstance.getStringValue()) + "\n");
                                if (newKey.equals(kvInstance.getStringKey())) {
                                    ret.append("\t!! skipping, no change in key\n");
                                } else {
                                    if (updateExisted(obj, section, newKey, kvInstance.getStringValue())) {
                                        ret.append("\tdeleting1: " + kvpToString(section, newKey, kvInstance.getStringValue()) + "\n");

                                    }
                                    ret.append("\tadding: " + kvpToString(section, newKey, kvInstance.getStringValue()) + "\n");
                                    ret.append("\tdeleting: " + kvpToString(section, kvInstance.getStringKey(), kvInstance.getStringValue()) + "\n");
                                }
                            }
                        } else {
                            logger.error("Unsupport value type: " + valueType + " obj: " + obj.toString());
                        }
                        logger.info(kvp);
                    }
                }

                break;

            case ADD_SECTION:
                ret.append("adding option: [")
                        .append(us.addSection())
                        .append("]/\"")
                        .append(us.addKey())
                        .append("\"=\"")
                        .append(us.addValue())
                        .append("\"\n");
                break;

            case REMOVE:
                ret.append("deleting kvp ")
                        .append(kv)
                        .append("\n");
                break;

            case REPLACE_WITH:
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
                                        .append(us.replaceWith(kvInstance.getStringValue()))
                                        .append("\"\n");
                                if (us.isMakeBackup()) {
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
                                        ret.append("adding option: [")
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
                break;

            case RESTORE_FROM_BACKUP:
                ArrayList<UserProperties> allBackup = getAllBackup(obj);
                if (allBackup.isEmpty()) {
                    theForm.requestOutput("No backup user properties");
                } else {
                    for (UserProperties userProperties : allBackup) {
                        String origProperty = userProperties.key.substring(BACKUP_PREFIX.length());
                        setProperty(obj, ret, userProperties.section, origProperty, userProperties.value);
                        if (us.isMakeBackup()) {
                            String curValue = getCurValue(obj, userProperties.section, origProperty);
                            if (curValue != null) {
                                setProperty(obj, ret, userProperties.section, userProperties.key, curValue);
                            }
                        }
                    }
                }
                break;
        }

        return ret.toString();
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

    private void setProperty(CfgObject obj, String section, String key, String value) {
        if (updateExisted(obj, section, key, value)) {
            addUpdateKey(section, key, value);
        } else {
            addAddKey(section, key, value);
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

    private static String kvpToString(String _section, String _key, String _value) {
        return "[" + _section + "]/\"" + _key + "\"=\'" + _value + "\'";

    }

    class UserProperties {

        @Override
        public String toString() {
            return kvpToString(section, key, value);
        }

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
