/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.airbnb.confservutils;

import com.genesyslab.platform.applicationblocks.com.IConfService;
import com.genesyslab.platform.commons.collections.KeyValueCollection;
import com.genesyslab.platform.configuration.protocol.confserver.requests.objects.RequestUpdateObject;
import com.genesyslab.platform.configuration.protocol.metadata.CfgMetadata;
import com.genesyslab.platform.configuration.protocol.obj.ConfObject;
import com.genesyslab.platform.configuration.protocol.obj.ConfObjectDelta;
import com.genesyslab.platform.configuration.protocol.types.CfgObjectType;
import java.util.HashMap;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author stepan_sydoruk
 */
public class UpdateUserProperties {

    private final int DBID;
    private final CfgObjectType objType;
    private final ConfigServerManager cfgManager;

    UpdateUserProperties(ConfigServerManager _configServerManager, CfgObjectType _objType, int _dbid) {
        this.cfgManager = _configServerManager;
        this.objType = _objType;
        this.DBID = _dbid;
    }
    KeyValueCollection updateSections = new KeyValueCollection();
    KeyValueCollection createSections = new KeyValueCollection();
    KeyValueCollection deleteSections = new KeyValueCollection();

    private KeyValueCollection getSection(KeyValueCollection sections, String section) {
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
        getSection(deleteSections, section).addString(key, null);
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

        return ret;
    }

    void addDeleteKey(KeyValueCollection kv) {
        deleteSections = kv;
    }

}
