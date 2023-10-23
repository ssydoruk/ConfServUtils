/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ssydoruk.confservutils;

import Utils.Pair;
import com.genesyslab.platform.applicationblocks.com.CfgObject;
import com.genesyslab.platform.applicationblocks.com.IConfService;
import com.genesyslab.platform.applicationblocks.com.objects.*;
import com.genesyslab.platform.commons.collections.KeyValueCollection;
import com.genesyslab.platform.commons.collections.KeyValuePair;
import com.genesyslab.platform.commons.collections.ValueType;
import com.genesyslab.platform.commons.protocol.Message;
import com.genesyslab.platform.commons.protocol.ProtocolException;
import com.genesyslab.platform.configuration.protocol.confserver.events.EventObjectDeleted;
import com.genesyslab.platform.configuration.protocol.confserver.requests.objects.RequestDeleteObject;
import com.genesyslab.platform.configuration.protocol.confserver.requests.objects.RequestUpdateObject;
import com.genesyslab.platform.configuration.protocol.metadata.CfgDescriptionAttribute;
import com.genesyslab.platform.configuration.protocol.metadata.CfgMetadata;
import com.genesyslab.platform.configuration.protocol.metadata.CfgTypeMask;
import com.genesyslab.platform.configuration.protocol.obj.ConfObject;
import com.genesyslab.platform.configuration.protocol.obj.ConfObjectDelta;
import com.genesyslab.platform.configuration.protocol.types.CfgFlag;
import com.genesyslab.platform.configuration.protocol.types.CfgObjectType;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import static com.ssydoruk.confservutils.Misc.getSection;

/**
 * @author stepan_sydoruk
 */
public class UpdateCFGObjectProcessor {

    private static final Logger logger = Main.getLogger();
    public static final HashMap<CfgObjectType, String> deltaByType = createDeltaByType();
    static final public String BACKUP_PREFIX = "#";

    public static String uncommented(String currentValue) {

        return StringUtils.stripStart(currentValue, BACKUP_PREFIX);

    }


    private static String cleanString(String s) {
        return (s == null) ? "" : StringUtils.strip(StringUtils.trim(s));
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
    ArrayList<CfgObject> deleteObjects = new ArrayList<>();
    private boolean objectsUpdated;

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
        objectsUpdated = false;
    }

    private void prepareDelete() {
        deleteObjects.clear();
        objectsUpdated = false;

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

    String estimateUpdateObj(IUpdateSettings us, CfgObject obj, KeyValueCollection kv, CfgApplication appNew) {
        cfgObjproperties = appNew;
        return estimateUpdateObj(us, obj, kv);
    }

    private Message commitUpdateKVP(CfgObject obj) throws ProtocolException {
        if (!updateSections.isEmpty() || !createSections.isEmpty() || !deleteSections.isEmpty() || cfgObjproperties != null) {
            IConfService service = cfgManager.getService();
            CfgMetadata metaData = service.getMetaData();
            ConfObjectDelta d = new ConfObjectDelta(metaData, objType);

            String className = obj.getClass().getSimpleName();
            ConfObject obj1 = (ConfObject) d.getOrCreatePropertyValue(metaData.getCfgClass(className).getDelta().getClassDescription().getAttributeByName(className).getSchemaName());
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
            objectsUpdated = true;
            return ret;

        }
        return null;
    }

    public boolean isObjectsUpdated() {
        return objectsUpdated;
    }

    void addDeleteKey(KeyValueCollection kv) {
        kv.stream().forEach(object->deleteSections.add(object));
    }

    private void fillUpdateKVP(IUpdateSettings us, CfgObject obj, KeyValueCollection kv) {
        prepareUpdate();
        switch (us.getKVPUpdateAction()) {
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
                                addUpdateKey(section, kvInstance.getStringKey(), us.KVPreplaceWith(kvInstance.getStringValue()));
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

    public Message updateObj(IUpdateSettings us, CfgObject obj, KeyValueCollection kv, CfgObject newObjectProperties) throws ProtocolException {
        cfgObjproperties = newObjectProperties;
        return updateObj(us, obj, kv);
    }

    public Message updateObj(IUpdateSettings us, CfgObject obj, KeyValueCollection kv) throws ProtocolException {

        switch (us.getObjectUpdateAction()) {
            case KVP_CHANGE:
                fillUpdateKVP(us, obj, kv);
                return commitUpdateKVP(obj);

            case OBJECT_DELETE:
                fillUpdateDelete(us, obj, kv, false);
                commitUpdateDelete(obj);
                return null;
        }
        return null;
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

    public String estimateUpdateObjKVP(IUpdateSettings us, CfgObject obj, KeyValueCollection kv) {
        fillUpdateKVP(us, obj, kv);

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

        return ret.toString();
    }

    public String estimateUpdateObj(IUpdateSettings us, CfgObject obj, KeyValueCollection kv) {
        switch (us.getObjectUpdateAction()) {
            case KVP_CHANGE:
                return estimateUpdateObjKVP(us, obj, kv);

            case OBJECT_DELETE:
                return estimateUpdateObjDelete(us, obj, kv);

        }
        return null;

    }

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

    private void fillUpdateDelete(IUpdateSettings us, CfgObject obj, KeyValueCollection kv, boolean alwaysCheckDependent) {
        prepareDelete();
        try {
            CfgObject delObj = obj.clone();
            deleteObjects.add(delObj);
            if (alwaysCheckDependent || us.isDeleteDependendObjects()) {

                if (obj.getObjectType() == CfgObjectType.CFGPlace) {
                    CfgPlace pl = (CfgPlace) obj;
                    for (Integer dnDBID : pl.getDNDBIDs()) {
                        CfgDN dn = new CfgDN(cfgManager.getService());
                        dn.setDBID(dnDBID);
                        deleteObjects.add(dn);
                    }
                } else if (obj.getObjectType() == CfgObjectType.CFGPerson) {
                    CfgPerson ag = (CfgPerson) obj;
                    if (ag.getIsAgent() == CfgFlag.CFGTrue) {
                        CfgAgentInfo agentInfo = ag.getAgentInfo();
                        for (CfgAgentLoginInfo dnDBID : agentInfo.getAgentLogins()) {
                            CfgAgentLogin agentLogin = new CfgAgentLogin(cfgManager.getService());
                            agentLogin.setDBID(dnDBID.getAgentLogin().getDBID());
                            deleteObjects.add(agentLogin);
                        }
                        CfgPlace place = agentInfo.getPlace();
                        if (place != null) {
                            CfgPlace pl = new CfgPlace(cfgManager.getService());
                            pl.setDBID(place.getDBID());
                            deleteObjects.add(pl);
                            for (Integer dnDBID : place.getDNDBIDs()) {
                                CfgDN dn = new CfgDN(cfgManager.getService());
                                dn.setDBID(dnDBID);
                                deleteObjects.add(dn);
                            }
                        }
                    }
                } else {
                    theForm.requestOutput("!! No dependent search for type " + CfgObjectType.CFGPerson);
                }
            }
        } catch (CloneNotSupportedException ex) {
            logger.fatal(ex);
        }

    }

    private String getObjDescr(CfgObject obj) {
        return "Type: " + obj.getObjectType() + " DBID:" + obj.getObjectDbid();
    }

    private String estimateUpdateObjDelete(IUpdateSettings us, CfgObject obj, KeyValueCollection kv) {
        fillUpdateDelete(us, obj, kv, true);
        StringBuilder ret = new StringBuilder("Deleting object(s):-->\n");
        for (CfgObject deleteObject : deleteObjects) {
            ret.append(getObjDescr(deleteObject)).append("\n");
        }
        ret.append("<--\n");
        return ret.toString();
    }

    private void commitUpdateDelete(CfgObject obj) {
        for (CfgObject deleteObject : deleteObjects) {
            try {
                RequestDeleteObject reqDel = RequestDeleteObject.create();
                reqDel.setDbid(deleteObject.getObjectDbid());
                reqDel.setObjectType(deleteObject.getObjectType().asInteger());

                theForm.requestOutput("++ deleting object type:" + CfgObjectType.valueOf(reqDel.getObjectType()) + " dbid: " + reqDel.getDbid());

                Message ret = cfgManager.execRequest(reqDel, objType);
                if (ret instanceof EventObjectDeleted) {
                    EventObjectDeleted o = (EventObjectDeleted) ret;
                    theForm.requestOutput("Object type:" + CfgObjectType.valueOf(o.getObjectType()) + " dbid: " + o.getDbid() + " deleted!");
                } else if (ret != null) {
                    logger.info("++ ret: " + ret.toString());
                } else {
                    logger.error("++ ret is null! ");
                }
                objectsUpdated = true;

//                deleteObject.delete();
//                theForm.requestOutput(getObjDescr(deleteObject) + ((deleteObject.isSaved()) ? " deleted " : " not deleted"));
                objectsUpdated = true;
//            } catch (ConfigException ex) {
//                theForm.requestOutput("Not able to delete object " + getObjDescr(deleteObject)
//                        + ": " + ex.getMessage());
            } catch (ProtocolException ex) {
                theForm.requestOutput("Not able to delete object " + getObjDescr(deleteObject)
                        + ": " + ex.getMessage());
            }
        }

    }

    @FunctionalInterface
    public static interface ICustomKVP {

        public KeyValueCollection getCustomKVP(CfgObject obj);
    }

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
