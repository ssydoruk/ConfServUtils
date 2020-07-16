/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.airbnb.confservutils;

import Utils.Pair;
import com.genesyslab.platform.applicationblocks.com.CfgObject;
import com.genesyslab.platform.applicationblocks.com.IConfService;
import com.genesyslab.platform.applicationblocks.com.objects.CfgApplication;
import com.genesyslab.platform.commons.collections.KeyValueCollection;
import com.genesyslab.platform.commons.collections.KeyValuePair;
import com.genesyslab.platform.commons.collections.ValueType;
import com.genesyslab.platform.commons.protocol.Message;
import com.genesyslab.platform.commons.protocol.ProtocolException;
import com.genesyslab.platform.configuration.protocol.confserver.requests.objects.RequestUpdateObject;
import com.genesyslab.platform.configuration.protocol.metadata.CfgDescriptionAttribute;
import com.genesyslab.platform.configuration.protocol.metadata.CfgMetadata;
import com.genesyslab.platform.configuration.protocol.metadata.CfgTypeMask;
import com.genesyslab.platform.configuration.protocol.obj.ConfObject;
import com.genesyslab.platform.configuration.protocol.obj.ConfObjectDelta;
import com.genesyslab.platform.configuration.protocol.types.CfgObjectType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import javax.swing.JOptionPane;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author stepan_sydoruk
 */
public class UpdateCFGObjectProcessor {

    private static final Logger logger = Main.getLogger();
    public static final HashMap<CfgObjectType, String> deltaByType = createDeltaByType();
    static final public String BACKUP_PREFIX = "#";

    public static String uncommented(String currentValue) {

        return StringUtils.stripStart(currentValue, BACKUP_PREFIX);

    }

    public static KeyValueCollection getSection(KeyValueCollection sections, String section) {
        KeyValueCollection list = sections.getList(section);
        if (list == null) {
            list = new KeyValueCollection();
            sections.addList(section, list);
        }
        return list;

    }

    private static String cleanString(String s) {
        return StringUtils.strip(StringUtils.trim(s));
    }

    private static HashMap<CfgObjectType, String> createDeltaByType() {
        HashMap<CfgObjectType, String> ret = new HashMap<>();
        ret.put(CfgObjectType.CFGDN, "deltaDN");
        ret.put(CfgObjectType.CFGTransaction, "deltaTransaction");
        ret.put(CfgObjectType.CFGScript, "deltaScript");
        ret.put(CfgObjectType.CFGApplication, "deltaApplication");
        ret.put(CfgObjectType.CFGAgentLogin, "deltaAgentLogin");

        return ret;
    }

    public static String getCommentedKey(String key) {
        return BACKUP_PREFIX + key;
    }

    private final CfgObjectType objType;
    private final ConfigServerManager cfgManager;
    private final AppForm theForm;
    private CfgObject cfgObjproperties;
    private final KVPUpdater annexUpdates;

    KeyValueCollection updateSections = new KeyValueCollection();
    KeyValueCollection createSections = new KeyValueCollection();
    KeyValueCollection deleteSections = new KeyValueCollection();
    private String changedPropsKey = "changedUserProperties";
    private String deletedPropsKey = "deletedUserProperties";
    private String createdPropsKey = "userProperties";
    private ICustomKVP customKVPProc = null;

    UpdateCFGObjectProcessor(ConfigServerManager _configServerManager, CfgObjectType _objType, AppForm _theForm) {
        this.cfgObjproperties = null;
        this.cfgManager = _configServerManager;
        this.objType = _objType;
        theForm = _theForm;
        annexUpdates = new KVPUpdater(true);
    }

    private void prepareUpdate() {
        updateSections.clear();
        createSections.clear();
        deleteSections.clear();
    }

    void addAddKey(String section, String key, String val) {
        getSection(createSections, section).addString(cleanString(key), cleanString(val));
    }

    void addUpdateKey(String section, String key, String val) {
        getSection(updateSections, section).addString(cleanString(key), cleanString(val));
    }

    void addAddKeyForce(String section, String key, String val, CfgObject obj) {
        getSection(createSections, section).addString(cleanString(key), cleanString(val));
    }

    void addAddKey(String section, String key, String val, CfgObject obj) {
        Pair<String, String> existing = updateExisted(obj, section, key);

        if (StringUtils.isNotEmpty(existing.getKey())) {
            String newSection = existing.getKey();
            String newKey = existing.getValue();
            if (StringUtils.isBlank(newKey)) {
                getSection(createSections, newSection).addString(cleanString(key), cleanString(val));

            } else {
                addUpdateKey(newSection, newKey, val);
            }

        } else {
            getSection(createSections, section).addString(cleanString(key), cleanString(val));
        }
    }

    void addDeleteKey(String section, String key, String val, CfgObject obj) {
        Pair<String, String> existing = updateExisted(obj, section, key);
        if (StringUtils.isNotEmpty(existing.getKey())) {
            if ((StringUtils.isNotEmpty(key) && StringUtils.isNotEmpty(existing.getValue()))) {
                getSection(deleteSections, existing.getKey()).addString(existing.getValue(), cleanString(val));
            }

            if ((StringUtils.isEmpty(key) && StringUtils.isEmpty(existing.getValue()))) {
                getSection(deleteSections, existing.getKey()).addString("", "");
            }

        }

    }

    public void setPropKeys(String _changedPropsKey, String _deletedPropsKey, String _createdPropsKey) {
        changedPropsKey = _changedPropsKey;
        deletedPropsKey = _deletedPropsKey;
        createdPropsKey = _createdPropsKey;
    }

    String estimateUpdateObj(IUpdateSettings us, CfgObject obj, KeyValueCollection kv, ConfigServerManager configServerManager, CfgApplication appNew) {
        cfgObjproperties = appNew;
        return estimateUpdateObj(us, obj, kv, configServerManager);
    }

    Message commitUpdate(CfgObject obj) throws ProtocolException {
        if (!updateSections.isEmpty() || !createSections.isEmpty() || !deleteSections.isEmpty() || cfgObjproperties != null) {
            IConfService service = cfgManager.getService();
            CfgMetadata metaData = service.getMetaData();
            ConfObjectDelta d = new ConfObjectDelta(metaData, objType);

            String dByType = deltaByType.get(objType);
            if (StringUtils.isAllEmpty(dByType)) {
                JOptionPane.showConfirmDialog(theForm, "Cannot find delta by type!!!", "Update failed", JOptionPane.WARNING_MESSAGE, JOptionPane.OK_OPTION);
            } else {

                ConfObject obj1 = (ConfObject) d.getOrCreatePropertyValue(dByType);

                obj1.setPropertyValue("DBID", obj.getObjectDbid());              // - required

                if (!updateSections.isEmpty()) {
                    d.setPropertyValue(changedPropsKey, updateSections);
                }

                if (!deleteSections.isEmpty()) {
                    d.setPropertyValue(deletedPropsKey, deleteSections);
                }

                if (!createSections.isEmpty()) {
                    obj1.setPropertyValue(createdPropsKey, createSections);
                }

                if (cfgObjproperties != null) {
                    int cfgPrimitiveInt = CfgTypeMask.Primitive.getCfgType();
                    for (CfgDescriptionAttribute attr : cfgObjproperties.getMetaData().getAttributes()) {
                        if (!attr.isKey() && (attr.getTypeBitMask() & cfgPrimitiveInt) != 0) {
                            String name = attr.getName();
                            Object prop = cfgObjproperties.getProperty(name);
                            if (prop != null) {
                                obj1.setPropertyValue(name, prop);
                            }
                        }
                    }
                }
//                    CfgApplication app = new CfgApplication(service);
//                    app.setCommandLine("line");
//                    app.setCommandLineArguments("args");
//                    CfgDescriptionClass metaData1 = app.getMetaData();
//                    ICfgClassOperationalInfo delta = metaData1.getDelta();
//                    Collection<CfgDescriptionAttribute> keys = metaData1.getKeys();
//                    Collection<CfgDescriptionAttribute> attributes = metaData1.getAttributes();
//                    CfgDescriptionObject attributeInfo = obj1.getClassInfo();
//                app.getp 
//                obj1.setPropertyValue("commandLine", "aaa");

                RequestUpdateObject reqUpdate = RequestUpdateObject.create();
                logger.info("++" + d.toString());
                reqUpdate.setObjectDelta(d);
                logger.info("++ req: " + reqUpdate);

                Message ret = cfgManager.execRequest(reqUpdate, objType);
                logger.info("++ ret: " + ret.toString());

                return ret;

            }

        }
        return null;
    }

    void addDeleteKey(KeyValueCollection kv) {
        for (Object object : kv) {
            deleteSections.add(object);
        }
    }

    public void fillUpdate(IUpdateSettings us, CfgObject obj, KeyValueCollection kv, ConfigServerManager configServerManager) {
        prepareUpdate();
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

                                    addDeleteKey(section, newKey, kvInstance.getStringValue(), obj);
                                    addAddKey(section, newKey, kvInstance.getStringValue());
                                    addDeleteKey(section, kvInstance.getStringKey(), kvInstance.getStringValue(), obj);
                                }
                            }
                        } else {
                            logger.error("Unsupport value type: " + valueType + " obj: " + obj.toString());
                        }
                        logger.info(kvp);
                    }
                }
                break;

            case ADD_SECTION: {
                Collection<UserProperties> addedKVP = us.getAddedKVP();
                if (addedKVP != null) {
                    for (UserProperties userProperties : addedKVP) {
                        addAddKey(userProperties.getSection(), userProperties.getKey(), userProperties.getValue(), obj);

                    }
                }
            }
            break;

            case ADD_OPTION_FORCE: {
                Collection<UserProperties> addedKVP = us.getAddedKVP();
                if (addedKVP != null) {
                    for (UserProperties userProperties : addedKVP) {
                        addAddKeyForce(userProperties.getSection(), userProperties.getKey(), userProperties.getValue(), obj);

                    }
                }
            }
            break;

            case REMOVE: {
                if (kv != null && !kv.isEmpty()) {
                    addDeleteKey(kv);
                } else {
//                    Collection<UserProperties> addedKVP = us.getAddedKVP();
//                    if (addedKVP != null) {
//                        for (UserProperties userProperties : addedKVP) {
//                            addDeleteKey(userProperties.getSection(), userProperties.getKey(), userProperties.getValue(), obj);
//
//                        }
//                    }
                }
            }
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
                        String origProperty = userProperties.getKey().substring(BACKUP_PREFIX.length());
                        setProperty(obj, userProperties.getSection(), origProperty, userProperties.getValue());
                        if (us.isMakeBackup()) {
                            String curValue = getCurValue(obj, userProperties.getSection(), origProperty);
                            if (curValue != null) {
                                setProperty(obj, userProperties.getSection(), userProperties.getKey(), curValue);
                            }
                        }
                    }
                }
                break;
        }

    }

    public Message updateObj(IUpdateSettings us, CfgObject obj, KeyValueCollection kv, ConfigServerManager configServerManager, CfgObject newObjectProperties) throws ProtocolException {
        cfgObjproperties = newObjectProperties;
        return updateObj(us, obj, kv, configServerManager);
    }

    public Message updateObj(IUpdateSettings us, CfgObject obj, KeyValueCollection kv, ConfigServerManager configServerManager) throws ProtocolException {

        fillUpdate(us, obj, kv, configServerManager);

        return commitUpdate(obj);
    }

    private String getCurValue(CfgObject obj, String section, String origProperty) {
        KeyValueCollection property = (customKVPProc == null) ? (KeyValueCollection) obj.getProperty("userProperties") : customKVPProc.getCustomKVP(obj);

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

    /**
     *
     * @param us
     * @param obj
     * @param kv
     * @param configServerManager
     * @return null if there is nothing to update or string with all updates
     */
    public String estimateUpdateObj(IUpdateSettings us, CfgObject obj, KeyValueCollection kv, ConfigServerManager configServerManager) {
        fillUpdate(us, obj, kv, configServerManager);

        StringBuilder ret = new StringBuilder();
        if (!updateSections.isEmpty() || !createSections.isEmpty() || !deleteSections.isEmpty() || cfgObjproperties != null) {
            if (!updateSections.isEmpty()) {
                for (Object object : updateSections) {
                    if (object instanceof KeyValuePair) {
                        KeyValuePair kvp = (KeyValuePair) object;
                        Object value = kvp.getValue();
                        ValueType valueType = kvp.getValueType();
                        if (valueType == ValueType.TKV_LIST) {
                            for (Object _kvInstance : (KeyValueCollection) value) {
                                KeyValuePair kvInstance = (KeyValuePair) _kvInstance;
                                ret.append("update: [").append(kvp.getStringKey()).append("]/\"").append(kvInstance.getStringKey()).append("\"=\'").append(kvInstance.getStringValue()).append("\'\n");
                            }
                        } else {
                            theForm.showError("Unsupport value type: " + valueType + " obj: " + obj.toString());
                        }
                    }
                }
            }

            if (!createSections.isEmpty()) {
                for (Object object : createSections) {
                    if (object instanceof KeyValuePair) {
                        KeyValuePair kvp = (KeyValuePair) object;
                        Object value = kvp.getValue();
                        ValueType valueType = kvp.getValueType();
                        if (valueType == ValueType.TKV_LIST) {
                            for (Object _kvInstance : (KeyValueCollection) value) {
                                KeyValuePair kvInstance = (KeyValuePair) _kvInstance;
                                ret.append("create: [").append(kvp.getStringKey()).append("]/\"").append(kvInstance.getStringKey()).append("\"=\'").append(kvInstance.getStringValue()).append("\'\n");
                            }
                        } else {
                            theForm.showError("Unsupport value type: " + valueType + " obj: " + obj.toString());
                        }
                    }
                }
            }

            if (!deleteSections.isEmpty()) {
                for (Object object : deleteSections) {
                    if (object instanceof KeyValuePair) {
                        KeyValuePair kvp = (KeyValuePair) object;
                        Object value = kvp.getValue();
                        ValueType valueType = kvp.getValueType();
                        if (valueType == ValueType.TKV_LIST) {
                            for (Object _kvInstance : (KeyValueCollection) value) {
                                KeyValuePair kvInstance = (KeyValuePair) _kvInstance;
                                ret.append("deletes: [").append(kvp.getStringKey()).append("]/\"").append(kvInstance.getStringKey()).append("\"=\'").append(kvInstance.getStringValue()).append("\'\n");
                            }
                        } else {
                            theForm.showError("Unsupport value type: " + valueType + " obj: " + obj.toString());
                        }
                    }
                }
            }

            if (cfgObjproperties != null) {
                ret.append("Properties change:\n");
                int cfgPrimitiveInt = CfgTypeMask.Primitive.getCfgType();
                for (CfgDescriptionAttribute attr : cfgObjproperties.getMetaData().getAttributes()) {
                    if (!attr.isKey() && (attr.getTypeBitMask() & cfgPrimitiveInt) != 0) {
                        String name = attr.getName();
                        Object prop = cfgObjproperties.getProperty(name);
                        if (prop != null) {
                            ret.append("\t[").append(name).append("]:[").append(prop).append("]\n");
                        }
                    }
                }
            }
        } else {
            return null;
        }
        /*
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
                Collection<UserProperties> addedKVP = us.getAddedKVP();
                if (addedKVP != null) {
                    for (UserProperties userProperties : addedKVP) {
//                        addAddKey(userProperties.getSection(), userProperties.getKey(), userProperties.getValue());
                        ret.append("adding option: [")
                                .append(userProperties.getSection())
                                .append("]/\"")
                                .append(userProperties.getKey())
                                .append("\"=\"")
                                .append(userProperties.getValue())
                                .append("\"\n");

                    }
                }
                break;

            case REMOVE: {
                addedKVP = us.getAddedKVP();
                if (addedKVP != null) {
                    for (UserProperties userProperties : addedKVP) {

                        ret.append("deleting kvp ")
                                .append(userProperties.toString())
                                .append("\n");

                    }
                }

                break;
            }

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
                        String origProperty = userProperties.getKey().substring(BACKUP_PREFIX.length());
                        setProperty(obj, ret, userProperties.getSection(), origProperty, userProperties.getValue());
                        if (us.isMakeBackup()) {
                            String curValue = getCurValue(obj, userProperties.getSection(), origProperty);
                            if (curValue != null) {
                                setProperty(obj, ret, userProperties.getSection(), userProperties.getKey(), curValue);
                            }
                        }
                    }
                }
                break;
        }
         */

        return ret.toString();
    }

    /**
     * searches for current section and key and if found, returns current
     * values. Comparison is done case insensitive
     *
     * @param obj
     * @param section
     * @param stringKey
     * @return
     */
    private Pair<String, String> updateExisted(CfgObject obj, String section, String stringKey) {
        Pair<String, String> ret = new Pair<>(null, null);

        KeyValueCollection property = (customKVPProc == null) ? (KeyValueCollection) obj.getProperty("userProperties") : customKVPProc.getCustomKVP(obj);
        if (property != null && !property.isEmpty()) {
            for (Object object : property) {
                if (object instanceof KeyValuePair) {
                    KeyValuePair kvp = (KeyValuePair) object;
                    Object value = kvp.getValue();
                    ValueType valueType = kvp.getValueType();

                    if (kvp.getStringKey().equalsIgnoreCase(section)) {
                        ret.setKey(kvp.getStringKey());
                        if (StringUtils.isEmpty(stringKey)) {
                            break;
                        } else if (valueType == ValueType.TKV_LIST) {
                            for (Object _kvInstance : (KeyValueCollection) value) {
                                KeyValuePair kvInstance = (KeyValuePair) _kvInstance;
                                if (kvInstance.getStringKey().equalsIgnoreCase(stringKey)) {
                                    ret.setValue(kvInstance.getStringKey());
                                    break;
                                }
                            }
                        } else {
                            theForm.showError("Unsupport value type: " + valueType + " obj: " + obj.toString());
                        }
                    }
                }
            }
        }
        return ret;
    }

    private boolean updateExisted(CfgObject obj, String section, String stringKey, String stringValue) {
        KeyValueCollection property = (customKVPProc == null) ? (KeyValueCollection) obj.getProperty("userProperties") : customKVPProc.getCustomKVP(obj);

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
        KeyValueCollection property = (customKVPProc == null) ? (KeyValueCollection) obj.getProperty("userProperties") : customKVPProc.getCustomKVP(obj);

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

    public void setCustomKVPProc(ICustomKVP customKVPProc) {
        this.customKVPProc = customKVPProc;
    }

    public static interface ICustomKVP {

        public KeyValueCollection getCustomKVP(CfgObject obj);
    };

    class KVPUpdater {

        private final KeyValueCollection updateSections = new KeyValueCollection();
        private final KeyValueCollection createSections = new KeyValueCollection();
        private final KeyValueCollection deleteSections = new KeyValueCollection();

        private String changedPropsKey;
        private String deletedPropsKey;
        private String createdPropsKey;

        public KVPUpdater(boolean isUserProperties) {
            if (isUserProperties) {
                initKeys("changedUserProperties", "deletedUserProperties", "userProperties");
            } else {
                initKeys("changedOptions", "deletedOptions", "options");
            }

        }

        private void initKeys(String changedPropsKey, String deletedPropsKey, String createdPropsKey) {
            this.changedPropsKey = changedPropsKey;
            this.deletedPropsKey = deletedPropsKey;
            this.createdPropsKey = createdPropsKey;
        }

    }

}
