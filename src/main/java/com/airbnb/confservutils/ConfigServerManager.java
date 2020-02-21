/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.airbnb.confservutils;

import static Utils.StringUtils.matching;
import com.genesyslab.platform.applicationblocks.com.CfgObject;
import com.genesyslab.platform.applicationblocks.com.CfgQuery;
import com.genesyslab.platform.applicationblocks.com.ConfigException;
import com.genesyslab.platform.applicationblocks.com.ICfgObject;
import com.genesyslab.platform.applicationblocks.com.IConfService;
import com.genesyslab.platform.commons.collections.KeyValueCollection;
import com.genesyslab.platform.commons.collections.KeyValuePair;
import com.genesyslab.platform.commons.protocol.ChannelState;
import com.genesyslab.platform.commons.protocol.Message;
import com.genesyslab.platform.commons.protocol.ProtocolException;
import com.genesyslab.platform.configuration.protocol.confserver.events.EventError;
import com.genesyslab.platform.configuration.protocol.confserver.events.EventObjectCreated;
import com.genesyslab.platform.configuration.protocol.confserver.events.EventObjectUpdated;
import com.genesyslab.platform.configuration.protocol.obj.ConfObject;
import com.genesyslab.platform.configuration.protocol.types.CfgObjectType;
import com.genesyslab.platform.configuration.protocol.utilities.CfgUtilities;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.regex.Pattern;
import javax.swing.SwingWorker;
import org.apache.logging.log4j.Logger;

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
            logger.fatal(ex);
        } catch (IllegalStateException ex) {
            logger.fatal(ex);
        } catch (InterruptedException ex) {
            logger.fatal(ex);
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

    public static final Logger logger = (Logger) Main.logger;

    public IConfService getService() {
        return service;
    }
    private final HashMap<String, Collection<? extends CfgObject>> prevQueries = new HashMap<>();

    <T extends CfgObject> Collection<T> getResults(CfgQuery q, final Class< T> cls) throws ConfigException, InterruptedException {
        Main.logger.debug("query " + q + " for object type " + cls);
        String qToString = q.toString();
        checkQueryNeedsUpdate();

        Collection<T> cfgObjs = null;
        if (prevQueries.containsKey(qToString) && prevQueries.get(qToString) != null) {
            cfgObjs = (Collection<T>) prevQueries.get(qToString);
        } else {
            Main.logger.debug("executing the request " + q);
            cfgObjs = service.retrieveMultipleObjects(cls, q);
            Main.logger.debug("retrieved " + ((cfgObjs == null) ? 0 : cfgObjs.size()) + " objects");
            prevQueries.put(qToString, cfgObjs);
        }
        return cfgObjs;
    }

    private <T extends CfgObject> void findApps(
            CfgQuery q,
            Class< T> cls,
            IKeyValueProperties props,
            ISearchSettings ss
    ) throws ConfigException, InterruptedException {

        StringBuilder buf = new StringBuilder();

        Collection<T> cfgObjs = getResults(q, cls);

        if (cfgObjs == null || cfgObjs.isEmpty()) {
            logger.debug("no objects found\n", false);
        } else {
            logger.debug("retrieved " + cfgObjs.size() + " total objects type " + cls.getSimpleName());
            int flags = ((ss.isRegex()) ? Pattern.LITERAL : 0) | ((ss.isCaseSensitive()) ? 0 : Pattern.CASE_INSENSITIVE);
            Pattern ptAll = null;
            Pattern ptSection = null;
            Pattern ptOption = null;
            Pattern ptVal = null;
            String section = ss.getSection();
            String option = ss.getOption();
            String val = ss.getValue();

            if (ss.isSearchAll()) {
                ptAll = (ss.isSearchAll() && ss.getAllSearch() != null ? Pattern.compile(ss.getAllSearch(), flags) : null);
            } else {
                ptSection = (section == null) ? null : Pattern.compile(section, flags);
                ptOption = (option == null) ? null : Pattern.compile(option, flags);
                ptVal = (val == null) ? null : Pattern.compile(val, flags);
            }
            KeyValueCollection kv = new KeyValueCollection();

            boolean checkForSectionOrOption = (ptAll != null || option != null || section != null || val != null);
            int cnt = 0;
            for (CfgObject cfgObj : cfgObjs) {
                boolean shouldInclude = false;
                if (checkForSectionOrOption) {
                    KeyValueCollection options;
                    options = props.getProperties(cfgObj);
                    kv.clear();
                    String sectionFound = null;
                    if (ptAll != null) { // if we got here and we are searching for all, it means name is already matched
                        for (String string : props.getName(cfgObj)) {
                            if (matching(ptAll, string)) {
                                shouldInclude = true;
                                break;
                            }
                        }

                    }
                    if (options != null) {
                        Enumeration<KeyValuePair> enumeration = options.getEnumeration();
                        KeyValuePair el;

                        while (enumeration.hasMoreElements()) {
                            el = enumeration.nextElement();

                            if (ptAll != null) {
                                if (matching(ptAll, el.getStringKey())) {
                                    sectionFound = el.getStringKey();
                                    shouldInclude = true;
                                }
                            } else if (ptSection != null) {
                                if (!matching(ptSection, el.getStringKey())) {
                                    continue;
                                } else {
                                    sectionFound = el.getStringKey();
                                }
                            }

                            if (ptVal == null && ptOption == null && ptAll == null) {
                                kv.addObject(el.getStringKey(), new KeyValueCollection());
                                shouldInclude = true;
                            } else {
                                KeyValueCollection addedValues = new KeyValueCollection();
                                Object value = el.getValue();
                                if (value instanceof KeyValueCollection) {
                                    KeyValueCollection sectionValues = (KeyValueCollection) value;
                                    Enumeration<KeyValuePair> optVal = sectionValues.getEnumeration();
                                    KeyValuePair theOpt;
                                    while (optVal.hasMoreElements()) {
                                        theOpt = optVal.nextElement();
                                        boolean isOptFound = false;
                                        boolean isValFound = false;

                                        if (ptAll != null) {
                                            if (matching(ptAll, theOpt.getStringKey())) {
                                                isOptFound = true;
                                            }
                                            if (matching(ptAll, theOpt.getStringValue())) {
                                                isValFound = true;
                                            }
                                        } else {
                                            if (ptOption != null) {
                                                if (matching(ptOption, theOpt.getStringKey())) {
                                                    isOptFound = true;
                                                }
                                            }
                                            if (ptVal != null) {
                                                if (matching(ptVal, theOpt.getStringValue())) {
                                                    isValFound = true;
                                                }
                                            }
                                        }
                                        if (isOptFound || isValFound) {
                                            addedValues.addPair(theOpt);

                                        }
                                    }
                                } else {
                                    logger.debug("value [" + value + "] is of type " + value.getClass() + " obj: " + cfgObj);
                                    if (ptVal != null) {
                                        if (matching(ptVal, value.toString())) {
                                            addedValues.addPair(el);

                                        }
                                    }
                                }
                                if (!addedValues.isEmpty() || sectionFound != null) {
                                    String sect = (sectionFound != null) ? sectionFound : el.getStringKey();
                                    KeyValueCollection list = kv.getList(sect);
                                    if (list == null) {
                                        list = new KeyValueCollection();
                                        kv.addList(sect, list);
                                    }
                                    list.addAll(Arrays.asList(addedValues.toArray()));
//                                    kv.addObject(el.getStringKey(), addedValues);
                                    shouldInclude = true;
                                }

                            }

                        }
                    }

                } else {
                    shouldInclude = true;
                }
                if (shouldInclude) {
                    if (ss.isFullOutputSelected()) {
                        buf.append(cfgObj.toString()).append("\n");
                    } else {
                        Object[] names = props.getName(cfgObj).toArray();
                        buf.append("\"").append(names[0]).append("\"").append(" path: ").append(cfgObj.getObjectPath()).append(", type:").append(cfgObj.getObjectType()).append(", DBID: ").append(cfgObj.getObjectDbid());
                        buf.append("\n");
                        if (names.length > 1) {
                            buf.append('\t');
                            for (int i = 1; i < names.length; i++) {
                                if (i > 1) {
                                    buf.append(", ");
                                }
                                buf.append(names[i]);
                            }
                        }
                        if (checkForSectionOrOption) {
                            buf.append("   ");
                            buf.append(kv);
                        }
                        buf.append("\n");
                    }
                    cnt++;
                }

            }
            if (cnt > 0) {
//                requestOutput("Search done, located " + cnt + " objects type " + cls.getSimpleName() + " -->\n" + buf + "<--\n");
            }
        }
    }

    public Message execRequest(Message reqUpdate, CfgObjectType objType) {

        try {
            // Call the SwingWorker from within the Swing thread
            parentForm.requestOutput("Executing update: " + reqUpdate.toString());
            Message resp = service.getProtocol().request(reqUpdate);

            if (resp instanceof EventObjectUpdated) {
                parentForm.requestOutput("!!Object updated dbid:" + ((EventObjectUpdated) resp).toString());
                objectUpdated(objType);
            } else if (resp instanceof EventError) {
                parentForm.requestOutput("Error on object update: "
                        + CfgUtilities.getErrorCode(((EventError) resp).getErrorCode())
                        + "\tDescription: " + ((EventError) resp).getDescription());
            } else if (resp instanceof EventObjectCreated) {
                EventObjectCreated oc = (EventObjectCreated) resp;
                ConfObject object = oc.getObject();
                parentForm.requestOutput("new object DBID: " + object.getObjectDbid());
                return resp;
            }

        } catch (ProtocolException ex) {
            logger.fatal(ex);
        } catch (IllegalStateException ex) {
            logger.fatal(ex);
        }
        return null;

    }

    private HashSet<CfgObjectType> lastUpdatedObjects = new HashSet<CfgObjectType>();

    private void checkQueryNeedsUpdate() {
        for (CfgObjectType lastUpdatedObject : lastUpdatedObjects) {
            for (Map.Entry<String, Collection<? extends CfgObject>> entry : prevQueries.entrySet()) {
                String key = entry.getKey();
                Collection<? extends CfgObject> value = entry.getValue();
                if (value != null) {
                    for (CfgObject cfgObject : value) {
                        if (lastUpdatedObject.equals(cfgObject.getObjectType())) {
                            logger.info("removing updated type " + cfgObject.getObjectType() + " from buffer");
                            prevQueries.put(key, null);
                            break;
                        }
                    }
                }
            }
        }
        lastUpdatedObjects.clear();

    }

    private void objectUpdated(CfgObjectType objType) {
        lastUpdatedObjects.add(objType);
    }

}
