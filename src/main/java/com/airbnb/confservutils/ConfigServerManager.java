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
import com.genesyslab.platform.applicationblocks.com.ConfigServerException;
import com.genesyslab.platform.applicationblocks.com.ICfgObject;
import com.genesyslab.platform.applicationblocks.com.IConfService;
import com.genesyslab.platform.applicationblocks.com.WellKnownDBIDs;
import com.genesyslab.platform.applicationblocks.com.objects.CfgAccessGroup;
import com.genesyslab.platform.applicationblocks.com.objects.CfgActionCode;
import com.genesyslab.platform.applicationblocks.com.objects.CfgAgentGroup;
import com.genesyslab.platform.applicationblocks.com.objects.CfgAgentLogin;
import com.genesyslab.platform.applicationblocks.com.objects.CfgAlarmCondition;
import com.genesyslab.platform.applicationblocks.com.objects.CfgApplication;
import com.genesyslab.platform.applicationblocks.com.objects.CfgDN;
import com.genesyslab.platform.applicationblocks.com.objects.CfgDNGroup;
import com.genesyslab.platform.applicationblocks.com.objects.CfgEnumerator;
import com.genesyslab.platform.applicationblocks.com.objects.CfgEnumeratorValue;
import com.genesyslab.platform.applicationblocks.com.objects.CfgFolder;
import com.genesyslab.platform.applicationblocks.com.objects.CfgGVPIVRProfile;
import com.genesyslab.platform.applicationblocks.com.objects.CfgHost;
import com.genesyslab.platform.applicationblocks.com.objects.CfgIVR;
import com.genesyslab.platform.applicationblocks.com.objects.CfgIVRPort;
import com.genesyslab.platform.applicationblocks.com.objects.CfgObjectiveTable;
import com.genesyslab.platform.applicationblocks.com.objects.CfgPerson;
import com.genesyslab.platform.applicationblocks.com.objects.CfgPlace;
import com.genesyslab.platform.applicationblocks.com.objects.CfgPlaceGroup;
import com.genesyslab.platform.applicationblocks.com.objects.CfgScript;
import com.genesyslab.platform.applicationblocks.com.objects.CfgService;
import com.genesyslab.platform.applicationblocks.com.objects.CfgSkill;
import com.genesyslab.platform.applicationblocks.com.objects.CfgStatDay;
import com.genesyslab.platform.applicationblocks.com.objects.CfgStatTable;
import com.genesyslab.platform.applicationblocks.com.objects.CfgSwitch;
import com.genesyslab.platform.applicationblocks.com.objects.CfgTenant;
import com.genesyslab.platform.applicationblocks.com.objects.CfgTimeZone;
import com.genesyslab.platform.applicationblocks.com.objects.CfgTransaction;
import com.genesyslab.platform.applicationblocks.com.objects.CfgTreatment;
import com.genesyslab.platform.applicationblocks.com.objects.CfgVoicePrompt;
import com.genesyslab.platform.applicationblocks.com.queries.CfgAccessGroupQuery;
import com.genesyslab.platform.applicationblocks.com.queries.CfgActionCodeQuery;
import com.genesyslab.platform.applicationblocks.com.queries.CfgAgentGroupQuery;
import com.genesyslab.platform.applicationblocks.com.queries.CfgAgentLoginQuery;
import com.genesyslab.platform.applicationblocks.com.queries.CfgAlarmConditionQuery;
import com.genesyslab.platform.applicationblocks.com.queries.CfgApplicationQuery;
import com.genesyslab.platform.applicationblocks.com.queries.CfgDNGroupQuery;
import com.genesyslab.platform.applicationblocks.com.queries.CfgDNQuery;
import com.genesyslab.platform.applicationblocks.com.queries.CfgEnumeratorQuery;
import com.genesyslab.platform.applicationblocks.com.queries.CfgEnumeratorValueQuery;
import com.genesyslab.platform.applicationblocks.com.queries.CfgFolderQuery;
import com.genesyslab.platform.applicationblocks.com.queries.CfgGVPIVRProfileQuery;
import com.genesyslab.platform.applicationblocks.com.queries.CfgHostQuery;
import com.genesyslab.platform.applicationblocks.com.queries.CfgIVRPortQuery;
import com.genesyslab.platform.applicationblocks.com.queries.CfgIVRQuery;
import com.genesyslab.platform.applicationblocks.com.queries.CfgObjectiveTableQuery;
import com.genesyslab.platform.applicationblocks.com.queries.CfgPersonQuery;
import com.genesyslab.platform.applicationblocks.com.queries.CfgPlaceGroupQuery;
import com.genesyslab.platform.applicationblocks.com.queries.CfgPlaceQuery;
import com.genesyslab.platform.applicationblocks.com.queries.CfgScriptQuery;
import com.genesyslab.platform.applicationblocks.com.queries.CfgServiceQuery;
import com.genesyslab.platform.applicationblocks.com.queries.CfgSkillQuery;
import com.genesyslab.platform.applicationblocks.com.queries.CfgStatDayQuery;
import com.genesyslab.platform.applicationblocks.com.queries.CfgStatTableQuery;
import com.genesyslab.platform.applicationblocks.com.queries.CfgSwitchQuery;
import com.genesyslab.platform.applicationblocks.com.queries.CfgTenantQuery;
import com.genesyslab.platform.applicationblocks.com.queries.CfgTimeZoneQuery;
import com.genesyslab.platform.applicationblocks.com.queries.CfgTransactionQuery;
import com.genesyslab.platform.applicationblocks.com.queries.CfgTreatmentQuery;
import com.genesyslab.platform.applicationblocks.com.queries.CfgVoicePromptQuery;
import com.genesyslab.platform.commons.collections.KeyValueCollection;
import com.genesyslab.platform.commons.collections.KeyValuePair;
import com.genesyslab.platform.commons.protocol.ChannelState;
import com.genesyslab.platform.commons.protocol.Message;
import com.genesyslab.platform.commons.protocol.ProtocolException;
import com.genesyslab.platform.configuration.protocol.confserver.events.EventError;
import com.genesyslab.platform.configuration.protocol.confserver.events.EventObjectCreated;
import com.genesyslab.platform.configuration.protocol.confserver.events.EventObjectUpdated;
import com.genesyslab.platform.configuration.protocol.obj.ConfObject;
import com.genesyslab.platform.configuration.protocol.types.CfgDNType;
import com.genesyslab.platform.configuration.protocol.types.CfgErrorType;
import com.genesyslab.platform.configuration.protocol.types.CfgObjectType;
import com.genesyslab.platform.configuration.protocol.types.CfgRouteType;
import com.genesyslab.platform.configuration.protocol.utilities.CfgUtilities;
import confserverbatch.SwitchLookup;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author stepan_sydoruk
 */
public class ConfigServerManager {

    public static final Logger logger = (Logger) Main.logger;

    private IConfService service = null;
    private final AppForm parentForm;
    private final HashMap<String, Collection<? extends CfgObject>> prevQueries = new HashMap<>();
    private final HashSet<CfgObjectType> lastUpdatedObjects = new HashSet<>();

    ConfigServerManager(AppForm aThis) {
        parentForm = aThis;
    }

    public void clearCache() {
        prevQueries.clear();

    }

    void disconnect() throws ProtocolException, IllegalStateException, InterruptedException {
        if (isConnected()) {
            ConfigConnection.uninitializeConfigService(service);
            service = null;
            clearCache();
        }
    }

    IConfService connect(StoredSettings.ConfServer confServ, String user, String string) {

        try {
            disconnect();
        } catch (ProtocolException | IllegalStateException | InterruptedException ex) {
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

    public IConfService getService() {
        return service;
    }

    <T extends CfgObject> Collection<T> getResults(CfgQuery q, final Class< T> cls) throws ConfigException, InterruptedException {
        Main.logger.debug("query " + q + " for object type " + cls);
        String qToString = q.toString();
        checkQueryNeedsUpdate();

        Collection<T> cfgObjs;
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
                                if (value != null) {
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

    public Message execRequest(Message reqUpdate, CfgObjectType objType) throws ProtocolException {

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
            }
            return resp;

        } catch (ProtocolException | IllegalStateException ex) {
            logger.fatal(ex);
            throw ex;
        }

    }

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

    private CfgPlace doCreatePlace(IConfService service,
            String pl, ArrayList<CfgDN> cfgDNs) throws ConfigException {
        CfgPlace cfgPlace = new CfgPlace(service);

        cfgPlace.setDNs(cfgDNs);
        cfgPlace.setName(pl);
        cfgPlace.setTenantDBID(WellKnownDBIDs.EnvironmentDBID);
        parentForm.requestOutput("Creating Place [" + cfgPlace + "]");

        cfgPlace.save();
        if (cfgPlace.isSaved()) {
            parentForm.requestOutput("Created place DBID: " + cfgPlace.getDBID());
            return cfgPlace;
        } else {
            return null;
        }
    }

    private CfgPlace findPlace(
            final IConfService service,
            final String placeName, boolean mastExist
    ) throws ConfigException {
        CfgPlaceQuery cfgPlaceQuery = new CfgPlaceQuery();

        cfgPlaceQuery.setName(placeName);
        cfgPlaceQuery.setTenantDbid(WellKnownDBIDs.EnvironmentDBID);

        parentForm.requestOutput("searching " + "Place [" + placeName + "]");
        CfgPlace cfgPlace = service.retrieveObject(CfgPlace.class, cfgPlaceQuery);
        if (mastExist && cfgPlace == null) {
            throw new ConfigException("Place [" + placeName + "]");
        }
        parentForm.requestOutput("Found " + "Place [" + placeName + "] DBID=" + cfgPlace.getDBID());

        return cfgPlace;

    }

    private boolean placeDNsEqual(Collection<CfgDN> placeDNs, ArrayList<CfgDN> cfgDNs) {
        logger.info("placeDNsEqual: placeDNs: " + StringUtils.join(placeDNs, ",") + "; DNs: " + StringUtils.join(cfgDNs, ","));
        if (placeDNs.size() > 0) {
            if (placeDNs.size() == cfgDNs.size()) {
                for (CfgDN placeDN : placeDNs) {
                    int i = 0;
                    for (; i < cfgDNs.size(); i++) {
                        logger.debug("comparing " + placeDN.getDBID() + " vs " + cfgDNs.get(i).getDBID());
                        if (placeDN.getDBID().intValue() == cfgDNs.get(i).getDBID().intValue()) {
                            break;
                        }
                    }
                    if (i == cfgDNs.size()) {
                        return false;
                    }
                }
            } else {
                return false;
            }
        }
        return true;
    }

    private CfgPlace createPlace(String plName, ArrayList<CfgDN> cfgDNs, ExistingObjectDecider eod) throws ConfigException {
        try {
            return doCreatePlace(service, plName, cfgDNs);
        } catch (ConfigException ex) {
//            switch(objExistaction)
            if (ex instanceof ConfigServerException) {
                CfgPlace cfgPlace = findPlace(service, plName, true);
                parentForm.requestOutput("Place exists ");
                switch (eod.getCurrentAction(makeString(cfgPlace), makeString(getDependend(cfgPlace)))) {
                    case RECREATE:
                        parentForm.requestOutput("Recreating place");
                        cfgPlace.delete();
                        parentForm.requestOutput(cfgPlace.getName() + " deleted");
                        return doCreatePlace(service, plName, cfgDNs);

                    case REUSE:
                        parentForm.requestOutput("Reusing place " + cfgPlace.getName());
                        if (cfgPlace != null) {
                            if (!placeDNsEqual(cfgPlace.getDNs(), cfgDNs)) {
                                parentForm.requestOutput("Place found but DNs are different. Cannot reuse");
                                return null; //this will ensure exception thrown

                            }

                        }
                        return cfgPlace;

                    case FAIL:
                        return null;

                    default:
                        break;//fails
                }
            } else {
                logger.error("Unexpected exception " + ex);
            }
            throw ex;
        }

    }

    public CfgPlace createPlace(String pl, HashMap<SwitchLookup, String> theDNs, ExistingObjectDecider eod) throws ConfigException {
        ArrayList<CfgDN> cfgDNs = new ArrayList<>();
        for (Map.Entry<SwitchLookup, String> entry : theDNs.entrySet()) {
            CfgDN newDN = createExtDN(entry.getKey(), entry.getValue(), eod);
            if (newDN == null) {
                return null;
            }
            cfgDNs.add(newDN);
        }

        return createPlace(pl, cfgDNs, eod);
    }

    private CfgDN doCreateDN(IConfService service,
            String theNumber,
            String name,
            CfgDNType type,
            CfgSwitch sw) throws ConfigException {
        CfgDN dn = new CfgDN(service);

        parentForm.requestOutput("Creating DN [" + name + "] switch [" + sw.getName() + "]");
        dn.setName(name);
        dn.setSwitch(sw);
        dn.setType(type);
        dn.setNumber(theNumber);
        dn.setSwitchSpecificType(1);
        dn.setRouteType(CfgRouteType.CFGDefault);
        dn.save();
        if (dn.isSaved()) {
            parentForm.requestOutput("DN created, DBID: " + dn.getDBID());
            return dn;
        } else {
            return null;
        }
    }

    private CfgDN findDN(
            final IConfService service,
            final String dn, final CfgSwitch sw, boolean mastExist
    ) throws ConfigException {
        CfgDNQuery dnQuery = new CfgDNQuery();

        dnQuery.setName(dn);
        dnQuery.setSwitchDbid(sw.getDBID());

//        parentForm.requestOutput("searching " + "DN [" + dn + "] switch[" + sw.getName() + "]");
        CfgDN cfgDn = service.retrieveObject(CfgDN.class, dnQuery);
        if (mastExist && cfgDn == null) {
            throw new ConfigException("DN [" + dn + "] switch[" + sw.getName() + "]");
        }
        parentForm.requestOutput("found " + "DN [" + dn + "] switch[" + sw.getName() + "] DBID:" + cfgDn.getDBID());
        return cfgDn;

    }

    private CfgDN createExtDN(SwitchLookup key, String value, ExistingObjectDecider eod) throws ConfigException {
        String name = key.getSw().getName() + "_" + value;
        try {
            return doCreateDN(service, value, name, CfgDNType.CFGExtension, key.getSw());
        } catch (ConfigException ex) {
//            switch(objExistaction)
            if (ex instanceof ConfigServerException
                    && ((ConfigServerException) ex).getErrorType() == CfgErrorType.CFGUniquenessViolation) {
                CfgDN cfgDN = findDN(service, name, key.getSw(), true);
                parentForm.requestOutput("DN exists: " + cfgDN.getNumber());
                switch (eod.getCurrentAction(makeString(cfgDN), makeString(getDependend(cfgDN)))) {
                    case RECREATE:
                        parentForm.requestOutput("Recreating");
                        cfgDN.delete();
                        parentForm.requestOutput(cfgDN.getNumber() + " deleted");
                        return doCreateDN(service, cfgDN.getNumber(), cfgDN.getName(), cfgDN.getType(),
                                cfgDN.getSwitch());

                    case REUSE:
                        parentForm.requestOutput("Reusing DN " + cfgDN.getNumber());
                        return cfgDN;

                    case FAIL:
                        return null;
                }
            } else {

                parentForm.requestOutput(ex.getMessage());
            }
            throw ex;
        }
    }

    private CfgObject getDependend(CfgObject cfgObj) {
        if (cfgObj instanceof CfgDN) {
            CfgDN dn = (CfgDN) cfgObj;

            CfgPlaceQuery plQuery = new CfgPlaceQuery();
            plQuery.setDnDbid(dn.getDBID());

            try {
                //        parentForm.requestOutput("searching " + "DN [" + dn + "] switch[" + sw.getName() + "]");
                return service.retrieveObject(CfgPlace.class, plQuery);
            } catch (ConfigException ex) {
                logger.error(ex);
                return null;
            }
//        } else if (cfgObj instanceof CfgPlace) {
//            CfgPlace pl = (CfgPlace) cfgObj;
//
//            CfgPersonQuery personQuery = new CfgPersonQuery();
//            CfgAg
//            personQuery.set(pl.getDBID());
//
//            try {
//                //        parentForm.requestOutput("searching " + "DN [" + dn + "] switch[" + sw.getName() + "]");
//                return service.retrieveObject(CfgPlace.class, personQuery);
//            } catch (ConfigException ex) {
//                logger.error(ex);
//                return null;
//            }
        } else {
            return null;
        }
    }

    private String makeString(CfgObject cfgObj) {
        return (cfgObj == null) ? ""
                : cfgObj.getObjectPath() + "\n" + cfgObj.toString();
    }

    /**
     *
     * @param <T>
     * @param q
     * @param cls
     * @param props
     * @param ss
     * @param checkNames
     * @param foundProc
     * @return true if interrupted
     * @throws ConfigException
     * @throws InterruptedException
     */
    public <T extends CfgObject> boolean findObjects(final CfgQuery q, final Class<T> cls, final IKeyValueProperties props, final FindWorker ss, final boolean checkNames, final ICfgObjectFoundProc foundProc) throws ConfigException, InterruptedException {
        int cnt = 0;
        final HashMap<CfgObject, KeyValueCollection> matchedObjects = new HashMap<>();
        final StringBuilder buf = new StringBuilder();
        final Collection<T> cfgObjs = getResults(q, cls);
        if (cfgObjs != null && !cfgObjs.isEmpty()) {
            for (final CfgObject cfgObj : cfgObjs) {
                final KeyValueCollection kv = ss.matchConfigObject(cfgObj, props, checkNames);
                if (kv != null) {
                    cnt++;
                    if (foundProc == null) {
                        if (ss.isFullOutputSelected()) {
                            buf.append("----> path: ").append(cfgObj.getObjectPath()).append(" ").append(cfgObj.toString()).append("\n");
                        } else {
                            final Object[] names = props.getName(cfgObj).toArray();
                            buf.append("----> \"").append(names[0]).append("\"").append(" path: ").append(cfgObj.getObjectPath()).append(", type:").append(cfgObj.getObjectType()).append(", DBID: ").append(cfgObj.getObjectDbid());
                            if (names.length > 1) {
                                buf.append("\n\t");
                                int added = 1;
                                for (int i = 1; i < names.length; i++) {
                                    if (added > 1) {
                                        buf.append(", ");
                                    }
                                    final Object obj = names[i];
                                    if (obj != null) {
                                        final String s = obj.toString();
                                        if (StringUtils.isNotBlank(s)) {
                                            buf.append(s);
                                            added++;
                                        }
                                    }
                                }
                            }
                            buf.append("\n");
                            if (!kv.isEmpty()) {
                                buf.append("\t").append(kv.toString()).append("\n\n");
                            }
                        }
                    } else {
                        matchedObjects.put(cfgObj, kv);
                    }
                }
            }
            if (foundProc != null) {
                int i = 0;
                parentForm.requestOutput("Search done, located " + cnt + " objects type " + cls.getSimpleName() + "\n");
                for (final Map.Entry<CfgObject, KeyValueCollection> entry : matchedObjects.entrySet()) {
                    if (!foundProc.proc(entry.getKey(), entry.getValue(), ++i, matchedObjects.size())) {
                        return true;
                    }
                }
            } else if (cnt > 0) {
                parentForm.requestOutput("Search done, located " + cnt + " objects type " + cls.getSimpleName() + " -->\n" + buf + "<--\n");
            }
        }
        return false;
    }

    boolean doTheSearch(final CfgObjectType t, final ISearchSettings pn, final boolean warnNotFound, final boolean checkNames, final ICfgObjectFoundProc foundProc) throws ConfigException, InterruptedException {
        final IConfService service = parentForm.configServerManager.getService();
        // <editor-fold defaultstate="collapsed" desc="CfgObjectType.CFGDN">
        if (t == CfgObjectType.CFGDN) {
            final CfgDNQuery query = new CfgDNQuery();
            // String n = pn.getObjName();
            // if (pn.isCaseSensitive() && n != null) {
            // query.setDnNumber(n);
            //
            // }
            if (findObjects(query, CfgDN.class, new IKeyValueProperties() {
                @Override
                public KeyValueCollection getProperties(final CfgObject obj) {
                    return ((CfgDN) obj).getUserProperties();
                }

                @Override
                public Collection<String> getName(final CfgObject obj) {
                    final Collection<String> ret = new ArrayList<>();
                    ret.add(((CfgDN) obj).getNumber());
                    ret.add(((CfgDN) obj).getDNLoginID());
                    ret.add(((CfgDN) obj).getName());
                    ret.add(((CfgDN) obj).getOverride());
                    return ret;
                }
            }, new FindWorker(pn), checkNames, foundProc)) {
                return false;
            }
        } // </editor-fold>
        else if (t == CfgObjectType.CFGSwitch) {
            final CfgSwitchQuery query = new CfgSwitchQuery();
            // String n = pn.getObjName();
            // if (pn.isCaseSensitive() && n != null) {
            // query.setName(n);
            //
            // }
            // CfgSwitchType selectedObjSubType = (CfgSwitchType)
            // pn.getSelectedObjSubType();
            // if (selectedObjSubType != null) {
            // query.(selectedObjSubType);
            // }
            if (findObjects(query, CfgSwitch.class, new IKeyValueProperties() {
                @Override
                public KeyValueCollection getProperties(final CfgObject obj) {
                    return ((CfgSwitch) obj).getUserProperties();
                }

                @Override
                public Collection<String> getName(final CfgObject obj) {
                    final Collection<String> ret = new ArrayList<>();
                    ret.add(((CfgSwitch) obj).getName());
                    return ret;
                }
            }, new FindWorker(pn), checkNames, foundProc)) {
                return false;
            }
        } // </editor-fold>
        else if (t == CfgObjectType.CFGAgentLogin) {
            final CfgAgentLoginQuery query = new CfgAgentLoginQuery();
            // String n = pn.getObjName();
            // if (pn.isCaseSensitive() && n != null) {
            // query.setLoginCode(n);
            //
            // }
            // CfgSwitchType selectedObjSubType = (CfgSwitchType)
            // pn.getSelectedObjSubType();
            // if (selectedObjSubType != null) {
            // query.(selectedObjSubType);
            // }
            if (findObjects(query, CfgAgentLogin.class, new IKeyValueProperties() {
                @Override
                public KeyValueCollection getProperties(final CfgObject obj) {
                    return ((CfgAgentLogin) obj).getUserProperties();
                }

                @Override
                public Collection<String> getName(final CfgObject obj) {
                    final Collection<String> ret = new ArrayList<>();
                    ret.add(((CfgAgentLogin) obj).getLoginCode());
                    return ret;
                }
            }, new FindWorker(pn), checkNames, foundProc)) {
                return false;
            }
        } // </editor-fold>
        else if (t == CfgObjectType.CFGPlace) {
            final CfgPlaceQuery query = new CfgPlaceQuery();
            // String n = pn.getObjName();
            // if (pn.isCaseSensitive() && n != null) {
            // query.setName(n);
            //
            // }
            // CfgSwitchType selectedObjSubType = (CfgSwitchType)
            // pn.getSelectedObjSubType();
            // if (selectedObjSubType != null) {
            // query.(selectedObjSubType);
            // }
            if (findObjects(query, CfgPlace.class, new IKeyValueProperties() {
                @Override
                public KeyValueCollection getProperties(final CfgObject obj) {
                    return ((CfgPlace) obj).getUserProperties();
                }

                @Override
                public Collection<String> getName(final CfgObject obj) {
                    final Collection<String> ret = new ArrayList<>();
                    ret.add(((CfgPlace) obj).getName());
                    return ret;
                }
            }, new FindWorker(pn), checkNames, foundProc)) {
                return false;
            }
        } // </editor-fold>
        else if (t == CfgObjectType.CFGPerson) {
            final CfgPersonQuery query = new CfgPersonQuery();
            // String n = pn.getObjName();
            // if (pn.isCaseSensitive() && n != null) {
            // query.setUserName(n);
            //
            // }
            // CfgSwitchType selectedObjSubType = (CfgSwitchType)
            // pn.getSelectedObjSubType();
            // if (selectedObjSubType != null) {
            // query.(selectedObjSubType);
            // }
            if (findObjects(query, CfgPerson.class, new IKeyValueProperties() {
                @Override
                public KeyValueCollection getProperties(final CfgObject obj) {
                    return ((CfgPerson) obj).getUserProperties();
                }

                @Override
                public Collection<String> getName(final CfgObject obj) {
                    final Collection<String> ret = new ArrayList<>();
                    ret.add(((CfgPerson) obj).getUserName());
                    ret.add(((CfgPerson) obj).getEmailAddress());
                    ret.add(((CfgPerson) obj).getEmployeeID());
                    ret.add(((CfgPerson) obj).getExternalID());
                    ret.add(((CfgPerson) obj).getFirstName());
                    ret.add(((CfgPerson) obj).getLastName());
                    ret.add(((CfgPerson) obj).getPassword());
                    ret.add(((CfgPerson) obj).getUserName());
                    return ret;
                }
            }, new FindWorker(pn), checkNames, foundProc)) {
                return false;
            }
        } // </editor-fold>
        else if (t == CfgObjectType.CFGAgentGroup) {
            final CfgAgentGroupQuery query = new CfgAgentGroupQuery();
            // String n = pn.getObjName();
            // if (pn.isCaseSensitive() && n != null) {
            // query.setName(n);
            //
            // }
            if (findObjects(query, CfgAgentGroup.class, new IKeyValueProperties() {
                @Override
                public KeyValueCollection getProperties(final CfgObject obj) {
                    return ((CfgAgentGroup) obj).getGroupInfo().getUserProperties();
                }

                @Override
                public Collection<String> getName(final CfgObject obj) {
                    final Collection<String> ret = new ArrayList<>();
                    ret.add(((CfgAgentGroup) obj).getGroupInfo().getName());
                    return ret;
                }
            }, new FindWorker(pn), checkNames, foundProc)) {
                return false;
            }
        } // </editor-fold>
        else if (t == CfgObjectType.CFGDNGroup) {
            final CfgDNGroupQuery query = new CfgDNGroupQuery();
            // String n = pn.getObjName();
            // if (pn.isCaseSensitive() && n != null) {
            // query.setName(n);
            //
            // }
            if (findObjects(query, CfgDNGroup.class, new IKeyValueProperties() {
                @Override
                public KeyValueCollection getProperties(final CfgObject obj) {
                    return ((CfgDNGroup) obj).getGroupInfo().getUserProperties();
                }

                @Override
                public Collection<String> getName(final CfgObject obj) {
                    final Collection<String> ret = new ArrayList<>();
                    ret.add(((CfgDNGroup) obj).getGroupInfo().getName());
                    return ret;
                }
            }, new FindWorker(pn), checkNames, foundProc)) {
                return false;
            }
        } // </editor-fold>
        else if (t == CfgObjectType.CFGPlaceGroup) {
            final CfgPlaceGroupQuery query = new CfgPlaceGroupQuery();
            // String n = pn.getObjName();
            // if (pn.isCaseSensitive() && n != null) {
            // query.setName(n);
            //
            // }
            if (findObjects(query, CfgPlaceGroup.class, new IKeyValueProperties() {
                @Override
                public KeyValueCollection getProperties(final CfgObject obj) {
                    return ((CfgPlaceGroup) obj).getGroupInfo().getUserProperties();
                }

                @Override
                public Collection<String> getName(final CfgObject obj) {
                    final Collection<String> ret = new ArrayList<>();
                    ret.add(((CfgPlaceGroup) obj).getGroupInfo().getName());
                    return ret;
                }
            }, new FindWorker(pn), checkNames, foundProc)) {
                return false;
            }
        } // </editor-fold>
        else if (t == CfgObjectType.CFGScript) {
            final CfgScriptQuery query = new CfgScriptQuery();
            // String n = pn.getObjName();
            // if (pn.isCaseSensitive() && n != null) {
            // query.setName(n);
            //
            // }
            // CfgSwitchType selectedObjSubType = (CfgSwitchType)
            // pn.getSelectedObjSubType();
            // if (selectedObjSubType != null) {
            // query.(selectedObjSubType);
            // }
            if (findObjects(query, CfgScript.class, new IKeyValueProperties() {
                @Override
                public KeyValueCollection getProperties(final CfgObject obj) {
                    return ((CfgScript) obj).getUserProperties();
                }

                @Override
                public Collection<String> getName(final CfgObject obj) {
                    final Collection<String> ret = new ArrayList<>();
                    ret.add(((CfgScript) obj).getName());
                    return ret;
                }
            }, new FindWorker(pn), checkNames, foundProc)) {
                return false;
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="CfgObjectType.CFGTransaction">
        } else if (t == CfgObjectType.CFGTransaction) {
            final CfgTransactionQuery query = new CfgTransactionQuery();
            // String n = pn.getObjName();
            // if (pn.isCaseSensitive() && n != null) {
            // query.setName(n);
            //
            // }
            if (findObjects(query, CfgTransaction.class, new IKeyValueProperties() {
                @Override
                public KeyValueCollection getProperties(final CfgObject obj) {
                    return ((CfgTransaction) obj).getUserProperties();
                }

                @Override
                public Collection<String> getName(final CfgObject obj) {
                    final Collection<String> ret = new ArrayList<>();
                    ret.add(((CfgTransaction) obj).getName());
                    ret.add(((CfgTransaction) obj).getAlias());
                    ret.add(((CfgTransaction) obj).getDescription());
                    return ret;
                }
            }, new FindWorker(pn), checkNames, foundProc)) {
                return false;
            }
        } // </editor-fold>
        else if (t == CfgObjectType.CFGEnumerator) {
            final CfgEnumeratorQuery query = new CfgEnumeratorQuery();
            // String n = pn.getObjName();
            // if (pn.isCaseSensitive() && n != null) {
            // query.setName(n);
            //
            // }
            if (findObjects(query, CfgEnumerator.class, new IKeyValueProperties() {
                @Override
                public KeyValueCollection getProperties(final CfgObject obj) {
                    return ((CfgEnumerator) obj).getUserProperties();
                }

                @Override
                public Collection<String> getName(final CfgObject obj) {
                    final Collection<String> ret = new ArrayList<>();
                    ret.add(((CfgEnumerator) obj).getName());
                    ret.add(((CfgEnumerator) obj).getDescription());
                    ret.add(((CfgEnumerator) obj).getDisplayName());
                    return ret;
                }
            }, new FindWorker(pn), checkNames, foundProc)) {
                return false;
            }
        } // </editor-fold>
        else if (t == CfgObjectType.CFGEnumeratorValue) {
            final CfgEnumeratorValueQuery query = new CfgEnumeratorValueQuery();
            // String n = pn.getObjName();
            // if (pn.isCaseSensitive() && n != null) {
            // query.setName(n);
            //
            // }
            if (findObjects(query, CfgEnumeratorValue.class, new IKeyValueProperties() {
                @Override
                public KeyValueCollection getProperties(final CfgObject obj) {
                    return ((CfgEnumeratorValue) obj).getUserProperties();
                }

                @Override
                public Collection<String> getName(final CfgObject obj) {
                    final Collection<String> ret = new ArrayList<>();
                    ret.add(((CfgEnumeratorValue) obj).getName());
                    ret.add(((CfgEnumeratorValue) obj).getDescription());
                    ret.add(((CfgEnumeratorValue) obj).getDisplayName());
                    return ret;
                }
            }, new FindWorker(pn), checkNames, foundProc)) {
                return false;
            }
        } // </editor-fold>
        else if (t == CfgObjectType.CFGGVPIVRProfile) {
            final CfgGVPIVRProfileQuery query = new CfgGVPIVRProfileQuery();
            // String n = pn.getObjName();
            // if (pn.isCaseSensitive() && n != null) {
            // query.setName(n);
            //
            // }
            if (findObjects(query, CfgGVPIVRProfile.class, new IKeyValueProperties() {
                @Override
                public KeyValueCollection getProperties(final CfgObject obj) {
                    return ((CfgGVPIVRProfile) obj).getUserProperties();
                }

                @Override
                public Collection<String> getName(final CfgObject obj) {
                    final Collection<String> ret = new ArrayList<>();
                    ret.add(((CfgGVPIVRProfile) obj).getName());
                    ret.add(((CfgGVPIVRProfile) obj).getDescription());
                    ret.add(((CfgGVPIVRProfile) obj).getDisplayName());
                    ret.add(((CfgGVPIVRProfile) obj).getNotes());
                    ret.add(((CfgGVPIVRProfile) obj).getStatus());
                    ret.add(((CfgGVPIVRProfile) obj).getTfn());
                    return ret;
                }
            }, new FindWorker(pn), checkNames, foundProc)) {
                return false;
            }
        } // </editor-fold>
        else if (t == CfgObjectType.CFGAccessGroup) {
            final CfgAccessGroupQuery query = new CfgAccessGroupQuery();
            // String n = pn.getObjName();
            // if (pn.isCaseSensitive() && n != null) {
            // query.setName(n);
            //
            // }
            if (findObjects(query, CfgAccessGroup.class, new IKeyValueProperties() {
                @Override
                public KeyValueCollection getProperties(final CfgObject obj) {
                    return null;
                }

                @Override
                public Collection<String> getName(final CfgObject obj) {
                    final Collection<String> ret = new ArrayList<>();
                    return ret;
                }
            }, new FindWorker(pn), checkNames, foundProc)) {
                return false;
            }
        } // </editor-fold>
        else if (t == CfgObjectType.CFGActionCode) {
            final CfgActionCodeQuery query = new CfgActionCodeQuery();
            // String n = pn.getObjName();
            // if (pn.isCaseSensitive() && n != null) {
            // query.setName(n);
            //
            // }
            if (findObjects(query, CfgActionCode.class, new IKeyValueProperties() {
                @Override
                public KeyValueCollection getProperties(final CfgObject obj) {
                    return ((CfgActionCode) obj).getUserProperties();
                }

                @Override
                public Collection<String> getName(final CfgObject obj) {
                    final Collection<String> ret = new ArrayList<>();
                    ret.add(((CfgActionCode) obj).getName());
                    ret.add(((CfgActionCode) obj).getCode());
                    return ret;
                }
            }, new FindWorker(pn), checkNames, foundProc)) {
                return false;
            }
        } // </editor-fold>
        else if (t == CfgObjectType.CFGAlarmCondition) {
            final CfgAlarmConditionQuery query = new CfgAlarmConditionQuery();
            // String n = pn.getObjName();
            // if (pn.isCaseSensitive() && n != null) {
            // query.setName(n);
            //
            // }
            if (findObjects(query, CfgAlarmCondition.class, new IKeyValueProperties() {
                @Override
                public KeyValueCollection getProperties(final CfgObject obj) {
                    return ((CfgAlarmCondition) obj).getUserProperties();
                }

                @Override
                public Collection<String> getName(final CfgObject obj) {
                    final Collection<String> ret = new ArrayList<>();
                    ret.add(((CfgAlarmCondition) obj).getName());
                    ret.add(((CfgAlarmCondition) obj).getDescription());
                    return ret;
                }
            }, new FindWorker(pn), checkNames, foundProc)) {
                return false;
            }
        } // </editor-fold>
        else if (t == CfgObjectType.CFGApplication) {
            final CfgApplicationQuery query = new CfgApplicationQuery();
            // String n = pn.getObjName();
            // if (pn.isCaseSensitive() && n != null) {
            // query.setName(n);
            //
            // }
            if (findObjects(query, CfgApplication.class, new IKeyValueProperties() {
                @Override
                public KeyValueCollection getProperties(final CfgObject obj) {
                    return ((CfgApplication) obj).getUserProperties();
                }

                @Override
                public Collection<String> getName(final CfgObject obj) {
                    final Collection<String> ret = new ArrayList<>();
                    ret.add(((CfgApplication) obj).getName());
                    ret.add(((CfgApplication) obj).getCommandLine());
                    ret.add(((CfgApplication) obj).getCommandLineArguments());
                    ret.add(((CfgApplication) obj).getWorkDirectory());
                    ret.add(((CfgApplication) obj).getVersion());
                    return ret;
                }
            }, new FindWorker(pn), checkNames, foundProc)) {
                return false;
            }
        } // </editor-fold>
        else if (t == CfgObjectType.CFGFolder) {
            final CfgFolderQuery query = new CfgFolderQuery();
            // String n = pn.getObjName();
            // if (pn.isCaseSensitive() && n != null) {
            // query.setName(n);
            //
            // }
            if (findObjects(query, CfgFolder.class, new IKeyValueProperties() {
                @Override
                public KeyValueCollection getProperties(final CfgObject obj) {
                    return ((CfgFolder) obj).getUserProperties();
                }

                @Override
                public Collection<String> getName(final CfgObject obj) {
                    final Collection<String> ret = new ArrayList<>();
                    ret.add(((CfgFolder) obj).getName());
                    ret.add(((CfgFolder) obj).getDescription());
                    return ret;
                }
            }, new FindWorker(pn), checkNames, foundProc)) {
                return false;
            }
        } // </editor-fold>
        else if (t == CfgObjectType.CFGHost) {
            final CfgHostQuery query = new CfgHostQuery();
            // String n = pn.getObjName();
            // if (pn.isCaseSensitive() && n != null) {
            // query.setName(n);
            //
            // }
            if (findObjects(query, CfgHost.class, new IKeyValueProperties() {
                @Override
                public KeyValueCollection getProperties(final CfgObject obj) {
                    return ((CfgHost) obj).getUserProperties();
                }

                @Override
                public Collection<String> getName(final CfgObject obj) {
                    final Collection<String> ret = new ArrayList<>();
                    ret.add(((CfgHost) obj).getName());
                    ret.add(((CfgHost) obj).getIPaddress());
                    return ret;
                }
            }, new FindWorker(pn), checkNames, foundProc)) {
                return false;
            }
        } // </editor-fold>
        else if (t == CfgObjectType.CFGTenant) {
            final CfgTenantQuery query = new CfgTenantQuery();
            // String n = pn.getObjName();
            // if (pn.isCaseSensitive() && n != null) {
            // query.setName(n);
            //
            // }
            if (findObjects(query, CfgTenant.class, new IKeyValueProperties() {
                @Override
                public KeyValueCollection getProperties(final CfgObject obj) {
                    return ((CfgTenant) obj).getUserProperties();
                }

                @Override
                public Collection<String> getName(final CfgObject obj) {
                    final Collection<String> ret = new ArrayList<>();
                    ret.add(((CfgTenant) obj).getName());
                    ret.add(((CfgTenant) obj).getChargeableNumber());
                    return ret;
                }
            }, new FindWorker(pn), checkNames, foundProc)) {
                return false;
            }
        } // </editor-fold>
        else if (t == CfgObjectType.CFGIVRPort) {
            final CfgIVRPortQuery query = new CfgIVRPortQuery();
            // String n = pn.getObjName();
            // if (pn.isCaseSensitive() && n != null) {
            // query.setPortNumber(n);
            //
            // }
            if (findObjects(query, CfgIVRPort.class, new IKeyValueProperties() {
                @Override
                public KeyValueCollection getProperties(final CfgObject obj) {
                    return ((CfgIVRPort) obj).getUserProperties();
                }

                @Override
                public Collection<String> getName(final CfgObject obj) {
                    final Collection<String> ret = new ArrayList<>();
                    ret.add(((CfgIVRPort) obj).getDescription());
                    ret.add(((CfgIVRPort) obj).getPortNumber());
                    return ret;
                }
            }, new FindWorker(pn), checkNames, foundProc)) {
                return false;
            }
        } // </editor-fold>
        else if (t == CfgObjectType.CFGIVR) {
            final CfgIVRQuery query = new CfgIVRQuery();
            // String n = pn.getObjName();
            // if (pn.isCaseSensitive() && n != null) {
            // query.setName(n);
            //
            // }
            if (findObjects(query, CfgIVR.class, new IKeyValueProperties() {
                @Override
                public KeyValueCollection getProperties(final CfgObject obj) {
                    return ((CfgIVR) obj).getUserProperties();
                }

                @Override
                public Collection<String> getName(final CfgObject obj) {
                    final Collection<String> ret = new ArrayList<>();
                    ret.add(((CfgIVR) obj).getDescription());
                    ret.add(((CfgIVR) obj).getName());
                    return ret;
                }
            }, new FindWorker(pn), checkNames, foundProc)) {
                return false;
            }
        } // </editor-fold>
        else if (t == CfgObjectType.CFGObjectiveTable) {
            final CfgObjectiveTableQuery query = new CfgObjectiveTableQuery();
            // String n = pn.getObjName();
            // if (pn.isCaseSensitive() && n != null) {
            // query.setName(n);
            //
            // }
            if (findObjects(query, CfgObjectiveTable.class, new IKeyValueProperties() {
                @Override
                public KeyValueCollection getProperties(final CfgObject obj) {
                    return ((CfgObjectiveTable) obj).getUserProperties();
                }

                @Override
                public Collection<String> getName(final CfgObject obj) {
                    final Collection<String> ret = new ArrayList<>();
                    ret.add(((CfgObjectiveTable) obj).getDescription());
                    ret.add(((CfgObjectiveTable) obj).getName());
                    return ret;
                }
            }, new FindWorker(pn), checkNames, foundProc)) {
                return false;
            }
        } // </editor-fold>
        else if (t == CfgObjectType.CFGService) {
            final CfgServiceQuery query = new CfgServiceQuery();
            // String n = pn.getObjName();
            // if (pn.isCaseSensitive() && n != null) {
            // query.setName(n);
            //
            // }
            if (findObjects(query, CfgService.class, new IKeyValueProperties() {
                @Override
                public KeyValueCollection getProperties(final CfgObject obj) {
                    return ((CfgService) obj).getUserProperties();
                }

                @Override
                public Collection<String> getName(final CfgObject obj) {
                    final Collection<String> ret = new ArrayList<>();
                    ret.add(((CfgService) obj).getName());
                    ret.add(((CfgService) obj).getVersion());
                    return ret;
                }
            }, new FindWorker(pn), checkNames, foundProc)) {
                return false;
            }
        } // </editor-fold>
        else if (t == CfgObjectType.CFGSkill) {
            final CfgSkillQuery query = new CfgSkillQuery();
            // String n = pn.getObjName();
            // if (pn.isCaseSensitive() && n != null) {
            // query.setName(n);
            //
            // }
            if (findObjects(query, CfgSkill.class, new IKeyValueProperties() {
                @Override
                public KeyValueCollection getProperties(final CfgObject obj) {
                    return ((CfgSkill) obj).getUserProperties();
                }

                @Override
                public Collection<String> getName(final CfgObject obj) {
                    final Collection<String> ret = new ArrayList<>();
                    ret.add(((CfgSkill) obj).getName());
                    return ret;
                }
            }, new FindWorker(pn), checkNames, foundProc)) {
                return false;
            }
        } // </editor-fold>
        else if (t == CfgObjectType.CFGStatDay) {
            final CfgStatDayQuery query = new CfgStatDayQuery();
            // String n = pn.getObjName();
            // if (pn.isCaseSensitive() && n != null) {
            // query.setName(n);
            //
            // }
            if (findObjects(query, CfgStatDay.class, new IKeyValueProperties() {
                @Override
                public KeyValueCollection getProperties(final CfgObject obj) {
                    return ((CfgStatDay) obj).getUserProperties();
                }

                @Override
                public Collection<String> getName(final CfgObject obj) {
                    final Collection<String> ret = new ArrayList<>();
                    ret.add(((CfgStatDay) obj).getName());
                    return ret;
                }
            }, new FindWorker(pn), checkNames, foundProc)) {
                return false;
            }
        } // </editor-fold>
        else if (t == CfgObjectType.CFGStatTable) {
            final CfgStatTableQuery query = new CfgStatTableQuery();
            // String n = pn.getObjName();
            // if (pn.isCaseSensitive() && n != null) {
            // query.setName(n);
            //
            // }
            if (findObjects(query, CfgStatTable.class, new IKeyValueProperties() {
                @Override
                public KeyValueCollection getProperties(final CfgObject obj) {
                    return ((CfgStatTable) obj).getUserProperties();
                }

                @Override
                public Collection<String> getName(final CfgObject obj) {
                    final Collection<String> ret = new ArrayList<>();
                    ret.add(((CfgStatTable) obj).getName());
                    return ret;
                }
            }, new FindWorker(pn), checkNames, foundProc)) {
                return false;
            }
        } // </editor-fold>
        else if (t == CfgObjectType.CFGTimeZone) {
            final CfgTimeZoneQuery query = new CfgTimeZoneQuery();
            // String n = pn.getObjName();
            // if (pn.isCaseSensitive() && n != null) {
            // query.setName(n);
            //
            // }
            if (findObjects(query, CfgTimeZone.class, new IKeyValueProperties() {
                @Override
                public KeyValueCollection getProperties(final CfgObject obj) {
                    return ((CfgTimeZone) obj).getUserProperties();
                }

                @Override
                public Collection<String> getName(final CfgObject obj) {
                    final Collection<String> ret = new ArrayList<>();
                    ret.add(((CfgTimeZone) obj).getName());
                    ret.add(((CfgTimeZone) obj).getDescription());
                    ret.add(((CfgTimeZone) obj).getNameMSExplorer());
                    ret.add(((CfgTimeZone) obj).getNameNetscape());
                    return ret;
                }
            }, new FindWorker(pn), checkNames, foundProc)) {
                return false;
            }
        } // </editor-fold>
        else if (t == CfgObjectType.CFGTreatment) {
            final CfgTreatmentQuery query = new CfgTreatmentQuery();
            // String n = pn.getObjName();
            // if (pn.isCaseSensitive() && n != null) {
            // query.setName(n);
            //
            // }
            if (findObjects(query, CfgTreatment.class, new IKeyValueProperties() {
                @Override
                public KeyValueCollection getProperties(final CfgObject obj) {
                    return ((CfgTreatment) obj).getUserProperties();
                }

                @Override
                public Collection<String> getName(final CfgObject obj) {
                    final Collection<String> ret = new ArrayList<>();
                    ret.add(((CfgTreatment) obj).getName());
                    ret.add(((CfgTreatment) obj).getDescription());
                    return ret;
                }
            }, new FindWorker(pn), checkNames, foundProc)) {
                return false;
            }
        } // </editor-fold>
        else if (t == CfgObjectType.CFGVoicePrompt) {
            final CfgVoicePromptQuery query = new CfgVoicePromptQuery();
            // String n = pn.getObjName();
            // if (pn.isCaseSensitive() && n != null) {
            // query.setName(n);
            //
            // }
            if (findObjects(query, CfgVoicePrompt.class, new IKeyValueProperties() {
                @Override
                public KeyValueCollection getProperties(final CfgObject obj) {
                    return ((CfgVoicePrompt) obj).getUserProperties();
                }

                @Override
                public Collection<String> getName(final CfgObject obj) {
                    final Collection<String> ret = new ArrayList<>();
                    ret.add(((CfgVoicePrompt) obj).getName());
                    ret.add(((CfgVoicePrompt) obj).getDescription());
                    return ret;
                }
            }, new FindWorker(pn), checkNames, foundProc)) {
                return false;
            }
        } // </editor-fold>
        else {
            if (warnNotFound) {
                logger.info("Searching for type " + t + " not implemented yet");
            }
        }
        return true;
    }

    private void execQuery(final CfgQuery query, final ISearchNamedProperties objProperties, final BussAttr searchParams) throws ConfigException, InterruptedException {
        final Collection<CfgObject> cfgObjs = query.execute();
        final StringBuilder buf = new StringBuilder();
        if (cfgObjs == null || cfgObjs.isEmpty()) {
            logger.debug("no objects found\n", false);
        } else {
            logger.debug("retrieved " + cfgObjs.size() + " total objects");
            final int flags = ((searchParams.isRegex()) ? Pattern.LITERAL : 0) | ((searchParams.isCaseSensitive()) ? 0 : Pattern.CASE_INSENSITIVE);
            final String val = searchParams.getName();
            final Pattern ptVal = (val == null) ? null : Pattern.compile(val, flags);
            int cnt = 0;
            for (final CfgObject cfgObj : cfgObjs) {
                final String[] namedProperties = objProperties.getNamedProperties(cfgObj);
                boolean found = false;
                for (final String namedProperty : namedProperties) {
                    if (matching(ptVal, namedProperty)) {
                        found = true;
                        cnt++;
                        break;
                    }
                }
                if (found) {
                    if (searchParams.isFullOutputSelected()) {
                        buf.append(cfgObj.toString());
                    } else {
                        buf.append(objProperties.getShortPrint(cfgObj));
                    }
                    buf.append("\n");
                }
            }
            if (cnt > 0) {
                parentForm.requestOutput("Filtering done\n" + buf);
            }
        }
    }
}
