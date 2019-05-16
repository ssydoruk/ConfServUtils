/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.airbnb.confservutils;

import com.genesyslab.platform.applicationblocks.com.CfgObject;
import com.genesyslab.platform.applicationblocks.com.CfgQuery;
import com.genesyslab.platform.applicationblocks.com.ConfigException;
import com.genesyslab.platform.applicationblocks.com.ICfgObject;
import com.genesyslab.platform.applicationblocks.com.IConfService;
import com.genesyslab.platform.commons.protocol.ChannelState;
import com.genesyslab.platform.commons.protocol.ProtocolException;
import com.genesyslab.platform.configuration.protocol.types.CfgObjectType;
import java.util.Collection;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author stepan_sydoruk
 */
public class ConfigServerManager {

    private IConfService service = null;
    private final AppForm parentForm;

    ConfigServerManager(AppForm aThis) {
        parentForm = aThis;
    }

    void disconnect() throws ProtocolException, IllegalStateException, InterruptedException {
        if (isConnected()) {
            ConfigConnection.uninitializeConfigService(service);
            service = null;
            prevQueries.clear();
        }
    }

    IConfService connect(StoredSettings.ConfServer confServ, String user, String string) {

        try {
            disconnect();
        } catch (ProtocolException ex) {
            Logger.getLogger(ConfigServerManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalStateException ex) {
            Logger.getLogger(ConfigServerManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(ConfigServerManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        parentForm.requestOutput("connecting...\n", false);

        try {
            /*
            String configServerHost = "10.61.6.55";
//        String configServerHost="esv1-c-ppe-46.ivr.airbnb.biz";
            int configServerPort = 2025;
            String configServerUser = "stepan.sydoruk@ext.airbnb.com.admin";
            String configServerPass = "CCbljher72~pAOk6NiP";

            String tempAppName = "AppName4Test"; // Uniq name for temp app to be created,
            // changed and deleted.
            String tempAgentName = "AgentName4Test"; // Uniq name for temp agent to be created,
            // changed and deleted.

            logger.info("ComJavaQuickStart started execution.");

            String someAppName = "default";
//        if (someAppName == null || someAppName.equals("")) {
//            someAppName = "default";
//        }
//        configServerHost = properties.getString("ConfServerHost");
//        configServerPort = Integer.parseInt(properties.getString("ConfServerPort"));
//        configServerUser = properties.getString("ConfServerUser");
//        configServerPass = properties.getString("ConfServerPassword");
             */
            service = ConfigConnection.initializeConfigService(
                    confServ.getApp(), confServ.getHost(), confServ.getPortInt(),
                    user, string);

            if (service != null) {
                parentForm.requestOutput("connected to ConfigServer " + confServ, false);
            }

        } catch (ConfigException | InterruptedException | ProtocolException ex) {
            service = null;
            parentForm.showException("Cannot connect to ConfigServer", ex);

        }
        return service;

    }

    boolean isConnected() {
        return (service != null && service.getProtocol().getState() != ChannelState.Closed);
    }

    ICfgObject retrieveObject(CfgObjectType t, int dbid) throws ConfigException {
        if (isConnected()) {
            return service.retrieveObject(t, dbid);
        } else {
            return null;
        }
    }

    public IConfService getService() {
        return service;
    }
    private final HashMap<String, Collection<? extends CfgObject>> prevQueries = new HashMap<>();

    <T extends CfgObject> Collection<T> getResults(CfgQuery q, final Class< T> cls) throws ConfigException, InterruptedException {
        Main.logger.debug("query " + q + " for object type " + cls);
        String qToString = q.toString();

        Collection<T> cfgObjs = null;
        if (prevQueries.containsKey(qToString)) {
            cfgObjs = (Collection<T>) prevQueries.get(qToString);
        } else {
            Main.logger.info("executing the request " + q);
            cfgObjs = service.retrieveMultipleObjects(cls, q);
            Main.logger.info("received " + ((cfgObjs == null) ? 0 : cfgObjs.size()) + " objects");
            prevQueries.put(qToString, cfgObjs);
        }
        return cfgObjs;
    }

}
