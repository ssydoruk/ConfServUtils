/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.airbnb.confservutils;

import static com.airbnb.confservutils.ConfigServerManager.logger;
import com.genesyslab.platform.applicationblocks.com.*;
import com.genesyslab.platform.commons.*;
import com.genesyslab.platform.commons.collections.*;
import com.genesyslab.platform.commons.protocol.*;
import com.genesyslab.platform.configuration.protocol.confserver.requests.objects.*;
import com.genesyslab.platform.configuration.protocol.metadata.*;
import com.genesyslab.platform.configuration.protocol.obj.*;
import com.genesyslab.platform.configuration.protocol.types.*;
import com.google.gson.*;
import com.opencsv.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.*;
import org.graalvm.polyglot.*;

/**
 * Interface to Java code from javascript code
 * @author stepan_sydoruk
 */
public class CStoJS {

    private static CStoJS csToJS = null;

    static CStoJS getInstance(ConfigServerManager csManager) {
        if (csToJS == null) {
            csToJS = new CStoJS();
        }
        csToJS.setConfigManager(csManager);
        return csToJS;
    }

    public Collection findObjects(String objectType) throws Exception {
        return findObjects(objectType, 0, false);
    }

    @HostAccess.Export
    public Collection findObjects(String objectType, boolean refresh) throws Exception {
        return findObjects(objectType, 0, refresh);
    }

    private ConfigServerManager csManager;

    /**
     *
     * @param objectType
     * @param max
     * @param refresh
     * @return Collection of CfgObject
     * @throws Exception
     */
    @HostAccess.Export
    public Collection findObjects(String objectType, int max, boolean refresh) throws Exception {
        return csManager.findObjects(objectType, max, refresh);

    }

    public String showException() throws Exception {
        throw new Exception("exception 1");
    }

    @HostAccess.Export
    public String objToJson(CfgObject obj) {
        return cfgObjectToJson(obj).toString();
    }

    @HostAccess.Export
    public String getAttribute(CfgObject obj, String attr) {
        return obj.getRawObjectData().getPropertyValue(attr).toString();
    }

    @HostAccess.Export
    public ConfObjectBase getAttributes(CfgObject obj) {
        return obj.getRawObjectData();
    }

    @HostAccess.Export
    public String enumToString(String enumName, int num) throws ClassNotFoundException {
        return GEnum.getValue((Class<GEnum>) Class.forName("com.genesyslab.platform.configuration.protocol.types." + enumName), num).toString();
    }

    @HostAccess.Export
    public String getObjectPath(CfgObject obj) {
        return obj.getObjectPath();
    }

    @HostAccess.Export
    public int enumToNum(String enumName, String val) throws ClassNotFoundException {
        return GEnum.getValue((Class<GEnum>) Class.forName("com.genesyslab.platform.configuration.protocol.types." + enumName), val).ordinal();
    }

    HashMap<CfgObjectType, ConfObjectsCollection> allObjectsBuffer = new HashMap<>();

    private ConfObjectsCollection readObjects(CfgObjectType objectType) throws ProtocolException, InterruptedException {

        ConfObjectsCollection ret = allObjectsBuffer.get(objectType);
        if (ret == null) {
            class Val {

                public ConfObjectsCollection result = null;
            }

            int intPerson = objectType.asInteger();
            KeyValueCollection filterKey = new KeyValueCollection();
            filterKey.addObject("DBID", 247);

            RequestReadObjects requestReadObjects
                    = RequestReadObjects.create(
                            intPerson,
                            null
                    //                        ,                        filterKey
                    );
            Val v = new Val();

            CountDownLatch latch = new CountDownLatch(1);
            csManager.getService().getProtocol().request(requestReadObjects);
//            csManager.getService().getProtocol().requestAsync(requestReadObjects, v, new CompletionHandler<Message, Val>() {
//                @Override
//                public void completed(Message msg, Val a) {
//                    System.out.println("-2-"+msg.messageName());
//                    switch (msg.messageId()) {
//                        case EventObjectsRead.ID:
//
//                            EventObjectsRead objectsRead
//                                    = (EventObjectsRead) msg;
//                            System.out.println("-1-"+msg.messageName());
//                            System.out.println("There are total "
//                                    + objectsRead.getObjectTotalCount() + ", in this batch:" + objectsRead.getObjectCount());
//                            a.result = objectsRead.getObjects();
//                            break;
//
//                        case EventError.ID:
//                            System.out.println("-1-"+msg.messageName());
//
//                            break;
//
//                        case EventObjectsSent.ID:
//                            System.out.println("-1-"+msg.messageName());
//                            latch.countDown();
//                            break;
//
//                        default:
//                            System.out.println("-1-"+msg.messageName());
//                            break;
//
//                    }
//
//                }
//
//                @Override
//                public void failed(Throwable thrwbl, Val a) {
//                    System.out.println("failed");
//                }
//            });
//            latch.await();
            if (v.result != null) {
                allObjectsBuffer.put(objectType, v.result);
                ret = v.result;
            }
        }
        logger.info("hello");
        return ret;
    }

//    @HostAccess.Export
//    public ConfObjectsCollection getObject(String objectType) throws Exception {
//        return readObjects(CfgObjectType.valueOf(objectType));
//    }
    @HostAccess.Export
    public String findObject(String objectType, String attrName, Object attrValue) throws Exception {
        CfgObjectType _objectType = CfgObjectType.valueOf(objectType);

        for (Iterator it = findObjects(objectType).iterator(); it.hasNext();) {
            CfgObject cfgObj = (CfgObject) it.next();
            logger.debug(cfgObj.toString());
            ConfObjectBase rod = cfgObj.getRawObjectData();
            Object propertyValue = rod.getPropertyValue(attrName);
            if (propertyValue != null && ((String) attrValue).contains(propertyValue.toString())) {
                return cfgObjectToJson(cfgObj).toString();
            }
        }
        csManager.getParentForm().requestOutput("findObject  type:" + _objectType + " attr:" + attrName + " value:" + attrValue);
        return "{}";
    }

    Gson gson = new Gson();

    @HostAccess.Export
    public boolean deleteObject(String objectType, String DBID) {
        return deleteObject(objectType, Integer.parseInt(DBID));
    }

    @HostAccess.Export
    public boolean deleteObject(String objectType, int DBID) {
        CfgObjectType _objectType = CfgObjectType.valueOf(objectType);
        csManager.getParentForm().requestOutput("deleteObject  type:" + _objectType + " dbid:" + DBID);

        try {
            return csManager.deleteObject(_objectType.asInteger(), DBID);
        } catch (Exception ex) {
            Logger.getLogger(CStoJS.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    @HostAccess.Export
    public Integer createObject(String objectType, String createObjProperties) {
        CfgObjectType _objectType = CfgObjectType.valueOf(objectType);
        csManager.getParentForm().requestOutput("createObject  type:" + _objectType + " createObjProperties:" + createObjProperties);

        CfgMetadata metaData = csManager.getService().getMetaData();

        ConfObject newObj = new ConfObject(metaData, _objectType);

//      newObj.setPropertyValue("name",      "new-host-name");
//      newObj.setPropertyValue("IPaddress", "19.19.19.19");
//
//      newObj.setPropertyValue("type",  1);
//      newObj.setPropertyValue("state", 1);
//
//      newObj.setPropertyValue("LCAPort", "4999");
//
//      ConfStructure osInfo = (ConfStructure) newObj.getOrCreatePropertyValue("OSinfo");
//      osInfo.setPropertyValue("OStype",    8);
//      osInfo.setPropertyValue("OSversion", "7");
        JsonObject convertedObject = gson.fromJson(createObjProperties, JsonObject.class);

        boolean sendRequest = false;
        for (Map.Entry<String, JsonElement> entry : convertedObject.entrySet()) {

            if (entry.getValue().isJsonPrimitive()) {
                JsonPrimitive val = entry.getValue().getAsJsonPrimitive();
                sendRequest = true;
                if (val.isNumber()) {
                    newObj.setPropertyValue(entry.getKey(), entry.getValue().getAsInt());
                } else if (val.isString()) {
                    newObj.setPropertyValue(entry.getKey(), entry.getValue().getAsString());
                }

            } else {
//                if (_objectType == CfgObjectType.CFGPerson) {
//                    if (entry.getKey().equals("agentInfo")) {
//                        JsonObject skillLevels = entry.getValue().getAsJsonObject().getAsJsonObject("skillLevels");
//                        if (skillLevels != null) {
//                            addedSkillLevels(_objectType, s, DBID, skillLevels.getAsJsonObject("added"));
//                            updateSkillLevels(_objectType, s, DBID, skillLevels.getAsJsonObject("changed"));
//                            deleteSkillLevels(_objectType, s, DBID, skillLevels.getAsJsonArray("deleted"));
//                        }
//
//                        switch (entry.getKey()) {
//
//                            case "agentLogins":
//                                break;
//                        }
//                    }
//                }
                logger.info(entry.getValue());
            }
        }

        ConfObject ret = null;
        try {
            ret = csManager.createObject(newObj);
        } catch (Exception exception) {
            logger.error("", exception);
        }
        return (ret == null) ? -1 : ret.getObjectDbid();
    }

    /**
     *
     * @param objectType - CfgPerson, etc
     * @param DBID
     * @param updateObjProperties - JSON with parameters to update
     * @return
     * @throws ProtocolException
     *
     *
     * example of updateObjProperties for person var updateObj = { // userName:
     * "stepan.sydoruk@ext.airbnb.com", agentInfo: { skillLevels: { changed: {
     * 103: 2, 238: 3 }, deleted: [238], added: { 103: 2, 238: 3 }, },
     * agentLogins: { changed: {}, deleted: {}, }, }, };
     *
     */
    @HostAccess.Export
    public String updateObject(String objectType, String _DBID, String updateObjProperties) throws ProtocolException {
        Integer DBID = Integer.parseInt(_DBID);
        CfgObjectType _objectType = CfgObjectType.valueOf(objectType);
        csManager.getParentForm().requestOutput("findObject  type:" + _objectType + " updateObjProperties:" + updateObjProperties);

        CfgMetadata metaData = csManager.getService().getMetaData();

        ConfObjectDelta objectDelta = new ConfObjectDelta(metaData, _objectType);

        String s = metaData.getCfgClass(objectType).getDelta().getClassDescription().getAttributeByName(objectType).getSchemaName();
        ConfObject deltaPerson = (ConfObject) objectDelta.getOrCreatePropertyValue(s);

        deltaPerson.setPropertyValue("DBID", DBID);              // - required
        JsonObject convertedObject = gson.fromJson(updateObjProperties, JsonObject.class
        );

        boolean sendRequest = false;
        for (Map.Entry<String, JsonElement> entry : convertedObject.entrySet()) {

            if (entry.getValue().isJsonPrimitive()) {
                JsonPrimitive val = entry.getValue().getAsJsonPrimitive();
                sendRequest = true;
                if (val.isNumber()) {
                    deltaPerson.setPropertyValue(entry.getKey(), entry.getValue().getAsNumber());
                } else if (val.isString()) {
                    deltaPerson.setPropertyValue(entry.getKey(), entry.getValue().getAsString());
                }

            } else {
                if (_objectType == CfgObjectType.CFGPerson) {
                    if (entry.getKey().equals("agentInfo")) {
                        JsonObject skillLevels = entry.getValue().getAsJsonObject().getAsJsonObject("skillLevels");
                        if (skillLevels != null) {
                            addedSkillLevels(_objectType, s, DBID, skillLevels.getAsJsonObject("added"));
                            updateSkillLevels(_objectType, s, DBID, skillLevels.getAsJsonObject("changed"));
                            deleteSkillLevels(_objectType, s, DBID, skillLevels.getAsJsonArray("deleted"));
                        }

                        switch (entry.getKey()) {

                            case "agentLogins":
                                break;
                        }
                    }

                }
                logger.info(entry.getValue());
            }
        }
        if (sendRequest) {
            RequestUpdateObject reqUpdate = RequestUpdateObject.create();
            reqUpdate.setObjectDelta(objectDelta);

            logger.info("++ req: " + reqUpdate);
            Message ret = csManager.execRequest(reqUpdate, _objectType);
            logger.info("++ ret: " + ret.toString());
            return ret.toString();
        }
        return "<Request not sent>";
    }

    private HashMap<CfgDescriptionClass, String> objTypeProperties = null;

    @HostAccess.Export
    public String getObjectAttributes(String objectType) throws Exception {
        if (objTypeProperties == null) {
            objTypeProperties = new HashMap<>();
        }
        CfgDescriptionClass cfgClass = csManager.getService().getMetaData().getCfgClass(objectType);
        if (cfgClass == null) {
            throw new Exception("Incorrect object type [" + objectType + "]");
        }
        String props = objTypeProperties.get(cfgClass);
        if (props == null) {
            ArrayList<CfgDescriptionAttributePrimitive> ret = new ArrayList<>();
            for (CfgDescriptionAttribute a : csManager.getService().getMetaData().getCfgClass(objectType).getAttributes()) {
//            logger.debug(CfgDescriptionJson.toJson(a));
                if (a instanceof CfgDescriptionAttributePrimitive && !a.isKey()) {
                    if (a instanceof CfgDescriptionAttributeEnumItem) {
                        ret.add((CfgDescriptionAttributeEnumItem) a);
                    } else {
                        ret.add((CfgDescriptionAttributePrimitive) a);
                    }
                }
            }
            props = CfgDescriptionJson.toJson(ret);
            objTypeProperties.put(cfgClass, props);
        }
        return props;
    }

    private JsonObject cfgObjectToJson(CfgObject person) {
        CfgDescriptionClass metaData = person.getMetaData();
        ConfObjectBase rawObjectData = person.getRawObjectData();

        JsonObject result = new JsonObject();
        result.addProperty("type", person.getObjectType().toString());
        result.addProperty("path", person.getObjectPath());
        result.addProperty("FolderDBID", person.getFolderId());

        JsonObject props = new JsonObject();
        for (CfgDescriptionAttribute a : metaData.getAttributes()) {
            String attrName = a.getSchemaName();
            Object val = rawObjectData.getPropertyValue(attrName);
            if (val == null) {
                props.addProperty(attrName,
                        (String) null
                );
            } else {
                if (val instanceof Integer) {
                    props.addProperty(attrName, (Integer) val);
                } else if (val instanceof String) {
                    props.addProperty(attrName, (String) val);
                } else if (val instanceof KeyValueCollection) {
                    props.add(attrName, kvpJson((KeyValueCollection) val));
                } else if (val instanceof ConfStructure) {
                    props.add(attrName, kvpConfStructure((ConfStructure) val));
                } else if (val instanceof ConfDataCollection) {
                    props.add(attrName, kvpConfDataCollection((ConfDataCollection) val));
                } else {
                    props.addProperty("***" + attrName, val.toString());
                }
            }
        }
        result.add("attributes", props);

        return result;
    }

    private JsonElement kvpJson(KVList val) {
        JsonObject ret = new JsonObject();
        for (Iterator iterator = val.iterator(); iterator.hasNext();) {
            Object nextElement = iterator.next();
            if (nextElement instanceof KeyValuePair) {
                String key = ((KeyValuePair) nextElement).getStringKey();
                Object newVal = ((KeyValuePair) nextElement).getValue();
                if ((newVal instanceof KeyValueCollection)) {
                    ret.add(key, kvpJson((KVList) newVal));
                } else if ((newVal instanceof String)) {
                    ret.addProperty(key, (String) newVal);
                } else if ((newVal instanceof Integer)) {
                    ret.addProperty(key, (Integer) newVal);
                } else {
                    ret.addProperty(key, newVal.toString());
                }
            }

        }

        return ret;
    }

    private JsonElement kvpConfDataCollection(ConfDataCollection confDataCollection) {
        JsonArray ret = new JsonArray();
        Iterator iterator;
        for (iterator = confDataCollection.iterator(); iterator.hasNext();) {
            Object attr = iterator.next();
            ret.add(new JsonPrimitive(attr.toString()));
        }
        return ret;
    }

    private JsonElement kvpConfStructure(ConfStructure confStructure) {
        JsonObject ret = new JsonObject();
        CfgDescriptionStructure classInfo = confStructure.getClassInfo();

        for (CfgDescriptionAttribute attr : classInfo.getAttributes()) {
            Object propertyValue = confStructure.getPropertyValue(attr.getIndex());
            if (attr instanceof CfgDescriptionAttributeReferenceLink) {
                if (propertyValue instanceof Integer) {
                    ret.addProperty(attr.getName(), (Integer) propertyValue);
                } else {
                    ret.addProperty(attr.getName(), (String) propertyValue);
                }
            } else if (attr instanceof CfgDescriptionAttributeReferenceClassList) {
                ret.add(attr.getName(), jsonConfStructureCollection((ConfStructureCollection) propertyValue));

//                CfgDescriptionAttributeReferenceClassList aa = (CfgDescriptionAttributeReferenceClassList) a;
//                Object propertyValue = confStructure.getPropertyValue(aa.getItemName());
            }
        }
        return ret;
    }

    private JsonElement jsonConfStructureCollection(ConfStructureCollection confStructureCollection) {
        if (confStructureCollection == null) {
            return null;
        }
        JsonArray ret = new JsonArray();
        for (Iterator<ConfStructure> iterator = confStructureCollection.iterator(); iterator.hasNext();) {
            ConfStructure attr = iterator.next();
            Iterator<CfgDescriptionAttribute> iterator1;
            JsonObject obj = new JsonObject();
            for (iterator1 = attr.getClassInfo().getAttributes().iterator(); iterator1.hasNext();) {
                CfgDescriptionAttribute descrAttr = iterator1.next();
                Object propertyValue = attr.getPropertyValue(descrAttr.getIndex());
                if (propertyValue instanceof Integer) {
                    obj.addProperty(descrAttr.getName(), (Integer) propertyValue);
                } else {
                    if (propertyValue != null) {
                        obj.addProperty(descrAttr.getName(), propertyValue.toString());
                    }
                }
            }
            ret.add(obj);
        }
        return ret;
    }

    private void deleteSkillLevels(CfgObjectType _objectType, String s, int DBID, JsonArray deleted) throws ProtocolException {
        if (deleted != null && deleted.size() > 0) {
            CfgMetadata metaData = csManager.getService().getMetaData();

            ConfObjectDelta objectDelta = new ConfObjectDelta(metaData, _objectType);

            ConfObject deltaPerson = (ConfObject) objectDelta.getOrCreatePropertyValue(s);
            ConfObjectBase deltaAgentInfo = (ConfStructure) objectDelta.getOrCreatePropertyValue("deltaAgentInfo");

            deltaPerson.setPropertyValue("DBID", DBID);              // - required

            for (JsonElement jsonElement : deleted) {
                ConfIntegerCollection col = (ConfIntegerCollection) deltaAgentInfo.getOrCreatePropertyValue("deletedSkillDBIDs");
                col.add(jsonElement.getAsInt());
            }
            executeUpdate(objectDelta, _objectType);
        }
    }

    private boolean emptyJsonObj(JsonObject obj) {
        return (obj == null || obj.isJsonNull() || obj.size() <= 0);
    }

    private void updateSkillLevels(CfgObjectType _objectType, String s, int DBID, JsonObject changed) throws ProtocolException {
        if (!emptyJsonObj(changed)) {
            CfgMetadata metaData = csManager.getService().getMetaData();

            ConfObjectDelta objectDelta = new ConfObjectDelta(metaData, _objectType);

            ConfObject deltaPerson = (ConfObject) objectDelta.getOrCreatePropertyValue(s);
            ConfObjectBase deltaAgentInfo = (ConfStructure) objectDelta.getOrCreatePropertyValue("deltaAgentInfo");

            ConfStructureCollection changedSkillLevels;

            deltaPerson.setPropertyValue("DBID", DBID);              // - required

            for (Map.Entry<String, JsonElement> changedEntry : changed.getAsJsonObject().entrySet()) {
                changedSkillLevels = (ConfStructureCollection) deltaAgentInfo.getOrCreatePropertyValue("changedSkillLevels");
                ConfStructure createStructure = changedSkillLevels.createStructure();
                createStructure.setPropertyValue("skillDBID", Integer.parseInt(changedEntry.getKey()));
                createStructure.setPropertyValue("level", changedEntry.getValue().getAsInt());
                changedSkillLevels.add(createStructure);
            }
            executeUpdate(objectDelta, _objectType);
        }
    }

    private void addedSkillLevels(CfgObjectType _objectType, String s, int DBID, JsonObject added) throws ProtocolException {
        if (!emptyJsonObj(added)) {
            CfgMetadata metaData = csManager.getService().getMetaData();

            ConfObjectDelta objectDelta = new ConfObjectDelta(metaData, _objectType);

            ConfObject deltaPerson = (ConfObject) objectDelta.getOrCreatePropertyValue(s);
            ConfObject deltaPerson1 = (ConfObject) objectDelta.getOrCreatePropertyValue("deltaPerson");
            ConfStructure deltaAgentInfo = (ConfStructure) deltaPerson1.getOrCreatePropertyValue("agentInfo");

            ConfStructureCollection changedSkillLevels;

            deltaPerson.setPropertyValue("DBID", DBID);              // - required

            for (Map.Entry<String, JsonElement> changedEntry : added.getAsJsonObject().entrySet()) {
                changedSkillLevels = (ConfStructureCollection) deltaAgentInfo.getOrCreatePropertyValue("skillLevels");
                ConfStructure createStructure = changedSkillLevels.createStructure();
                createStructure.setPropertyValue("skillDBID", Integer.parseInt(changedEntry.getKey()));
                createStructure.setPropertyValue("level", changedEntry.getValue().getAsInt());
                changedSkillLevels.add(createStructure);

            }
            executeUpdate(objectDelta, _objectType);
        }
    }

    private void executeUpdate(ConfObjectDelta objectDelta, CfgObjectType _objectType) throws ProtocolException {
        RequestUpdateObject reqUpdate = RequestUpdateObject.create();
        reqUpdate.setObjectDelta(objectDelta);

        logger.info("++ req: " + reqUpdate);
        Message ret = csManager.execRequest(reqUpdate, _objectType);
        logger.info("++ ret: " + ret.toString());
    }

    /**
     * Establishes connection to the configuration server. 
     * @return true if connected; false otherwise
     * @throws Exception 
     */
    @HostAccess.Export
    public boolean connectToConfigServer() throws Exception {
        return csManager.connectToConfigServer();
    }

    private void setConfigManager(ConfigServerManager _csManager) {
        this.csManager = _csManager;
    }

    @HostAccess.Export
    public List<String[]> readCSV(String fileName) throws IOException {
        return readCSV(fileName, 0);
    }

    @HostAccess.Export
    public List<String[]> readCSV(String fileName, int skipLines) throws IOException {
        try {

            final CSVReader reader
                    = new CSVReaderBuilder(new FileReader(fileName))
                            .withSkipLines(skipLines)
                            .withCSVParser(new CSVParserBuilder()
                                    .withSeparator('\t')
                                    .withSeparator(',')
                                    .withIgnoreQuotations(true)
                                    .build())
                            .build();

            List<String[]> ret = reader.readAll();
            return ret;

        } catch (FileNotFoundException ex) {
            Logger.getLogger(CStoJS.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

    }

}
