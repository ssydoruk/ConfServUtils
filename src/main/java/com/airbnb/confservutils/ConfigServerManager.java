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
import com.genesyslab.platform.applicationblocks.com.objects.CfgACE;
import com.genesyslab.platform.applicationblocks.com.objects.CfgACEID;
import com.genesyslab.platform.applicationblocks.com.objects.CfgACL;
import com.genesyslab.platform.applicationblocks.com.objects.CfgACLID;
import com.genesyslab.platform.applicationblocks.com.objects.CfgAccessGroup;
import com.genesyslab.platform.applicationblocks.com.objects.CfgAccessGroupBrief;
import com.genesyslab.platform.applicationblocks.com.objects.CfgActionCode;
import com.genesyslab.platform.applicationblocks.com.objects.CfgAddress;
import com.genesyslab.platform.applicationblocks.com.objects.CfgAgentGroup;
import com.genesyslab.platform.applicationblocks.com.objects.CfgAgentInfo;
import com.genesyslab.platform.applicationblocks.com.objects.CfgAgentLogin;
import com.genesyslab.platform.applicationblocks.com.objects.CfgAgentLoginInfo;
import com.genesyslab.platform.applicationblocks.com.objects.CfgAlarmCondition;
import com.genesyslab.platform.applicationblocks.com.objects.CfgAppPrototype;
import com.genesyslab.platform.applicationblocks.com.objects.CfgAppRank;
import com.genesyslab.platform.applicationblocks.com.objects.CfgAppServicePermission;
import com.genesyslab.platform.applicationblocks.com.objects.CfgApplication;
import com.genesyslab.platform.applicationblocks.com.objects.CfgCallingList;
import com.genesyslab.platform.applicationblocks.com.objects.CfgCallingListInfo;
import com.genesyslab.platform.applicationblocks.com.objects.CfgCampaign;
import com.genesyslab.platform.applicationblocks.com.objects.CfgCampaignGroup;
import com.genesyslab.platform.applicationblocks.com.objects.CfgCampaignGroupInfo;
import com.genesyslab.platform.applicationblocks.com.objects.CfgConnInfo;
import com.genesyslab.platform.applicationblocks.com.objects.CfgDN;
import com.genesyslab.platform.applicationblocks.com.objects.CfgDNAccessNumber;
import com.genesyslab.platform.applicationblocks.com.objects.CfgDNGroup;
import com.genesyslab.platform.applicationblocks.com.objects.CfgDNInfo;
import com.genesyslab.platform.applicationblocks.com.objects.CfgDelSwitchAccess;
import com.genesyslab.platform.applicationblocks.com.objects.CfgDeltaAccessGroup;
import com.genesyslab.platform.applicationblocks.com.objects.CfgDeltaActionCode;
import com.genesyslab.platform.applicationblocks.com.objects.CfgDeltaAgentGroup;
import com.genesyslab.platform.applicationblocks.com.objects.CfgDeltaAgentInfo;
import com.genesyslab.platform.applicationblocks.com.objects.CfgDeltaAgentLogin;
import com.genesyslab.platform.applicationblocks.com.objects.CfgDeltaAlarmCondition;
import com.genesyslab.platform.applicationblocks.com.objects.CfgDeltaAppPrototype;
import com.genesyslab.platform.applicationblocks.com.objects.CfgDeltaApplication;
import com.genesyslab.platform.applicationblocks.com.objects.CfgDeltaCallingList;
import com.genesyslab.platform.applicationblocks.com.objects.CfgDeltaCampaign;
import com.genesyslab.platform.applicationblocks.com.objects.CfgDeltaCampaignGroup;
import com.genesyslab.platform.applicationblocks.com.objects.CfgDeltaDN;
import com.genesyslab.platform.applicationblocks.com.objects.CfgDeltaDNGroup;
import com.genesyslab.platform.applicationblocks.com.objects.CfgDeltaEnumerator;
import com.genesyslab.platform.applicationblocks.com.objects.CfgDeltaEnumeratorValue;
import com.genesyslab.platform.applicationblocks.com.objects.CfgDeltaField;
import com.genesyslab.platform.applicationblocks.com.objects.CfgDeltaFilter;
import com.genesyslab.platform.applicationblocks.com.objects.CfgDeltaFolder;
import com.genesyslab.platform.applicationblocks.com.objects.CfgDeltaFormat;
import com.genesyslab.platform.applicationblocks.com.objects.CfgDeltaGVPCustomer;
import com.genesyslab.platform.applicationblocks.com.objects.CfgDeltaGVPIVRProfile;
import com.genesyslab.platform.applicationblocks.com.objects.CfgDeltaGVPReseller;
import com.genesyslab.platform.applicationblocks.com.objects.CfgDeltaGroup;
import com.genesyslab.platform.applicationblocks.com.objects.CfgDeltaHost;
import com.genesyslab.platform.applicationblocks.com.objects.CfgDeltaIVR;
import com.genesyslab.platform.applicationblocks.com.objects.CfgDeltaIVRPort;
import com.genesyslab.platform.applicationblocks.com.objects.CfgDeltaObjectiveTable;
import com.genesyslab.platform.applicationblocks.com.objects.CfgDeltaPerson;
import com.genesyslab.platform.applicationblocks.com.objects.CfgDeltaPersonLastLogin;
import com.genesyslab.platform.applicationblocks.com.objects.CfgDeltaPhysicalSwitch;
import com.genesyslab.platform.applicationblocks.com.objects.CfgDeltaPlace;
import com.genesyslab.platform.applicationblocks.com.objects.CfgDeltaPlaceGroup;
import com.genesyslab.platform.applicationblocks.com.objects.CfgDeltaRole;
import com.genesyslab.platform.applicationblocks.com.objects.CfgDeltaScheduledTask;
import com.genesyslab.platform.applicationblocks.com.objects.CfgDeltaScript;
import com.genesyslab.platform.applicationblocks.com.objects.CfgDeltaService;
import com.genesyslab.platform.applicationblocks.com.objects.CfgDeltaSkill;
import com.genesyslab.platform.applicationblocks.com.objects.CfgDeltaStatDay;
import com.genesyslab.platform.applicationblocks.com.objects.CfgDeltaStatTable;
import com.genesyslab.platform.applicationblocks.com.objects.CfgDeltaSwitch;
import com.genesyslab.platform.applicationblocks.com.objects.CfgDeltaTableAccess;
import com.genesyslab.platform.applicationblocks.com.objects.CfgDeltaTenant;
import com.genesyslab.platform.applicationblocks.com.objects.CfgDeltaTimeZone;
import com.genesyslab.platform.applicationblocks.com.objects.CfgDeltaTransaction;
import com.genesyslab.platform.applicationblocks.com.objects.CfgDeltaTreatment;
import com.genesyslab.platform.applicationblocks.com.objects.CfgDeltaVoicePrompt;
import com.genesyslab.platform.applicationblocks.com.objects.CfgDetectEvent;
import com.genesyslab.platform.applicationblocks.com.objects.CfgEnumerator;
import com.genesyslab.platform.applicationblocks.com.objects.CfgEnumeratorValue;
import com.genesyslab.platform.applicationblocks.com.objects.CfgField;
import com.genesyslab.platform.applicationblocks.com.objects.CfgFilter;
import com.genesyslab.platform.applicationblocks.com.objects.CfgFolder;
import com.genesyslab.platform.applicationblocks.com.objects.CfgFormat;
import com.genesyslab.platform.applicationblocks.com.objects.CfgGVPCustomer;
import com.genesyslab.platform.applicationblocks.com.objects.CfgGVPIVRProfile;
import com.genesyslab.platform.applicationblocks.com.objects.CfgGVPReseller;
import com.genesyslab.platform.applicationblocks.com.objects.CfgGroup;
import com.genesyslab.platform.applicationblocks.com.objects.CfgHost;
import com.genesyslab.platform.applicationblocks.com.objects.CfgID;
import com.genesyslab.platform.applicationblocks.com.objects.CfgIVR;
import com.genesyslab.platform.applicationblocks.com.objects.CfgIVRPort;
import com.genesyslab.platform.applicationblocks.com.objects.CfgMemberID;
import com.genesyslab.platform.applicationblocks.com.objects.CfgOS;
import com.genesyslab.platform.applicationblocks.com.objects.CfgObjectID;
import com.genesyslab.platform.applicationblocks.com.objects.CfgObjectResource;
import com.genesyslab.platform.applicationblocks.com.objects.CfgObjectiveTable;
import com.genesyslab.platform.applicationblocks.com.objects.CfgObjectiveTableRecord;
import com.genesyslab.platform.applicationblocks.com.objects.CfgOwnerID;
import com.genesyslab.platform.applicationblocks.com.objects.CfgParentID;
import com.genesyslab.platform.applicationblocks.com.objects.CfgPerson;
import com.genesyslab.platform.applicationblocks.com.objects.CfgPersonBrief;
import com.genesyslab.platform.applicationblocks.com.objects.CfgPersonLastLogin;
import com.genesyslab.platform.applicationblocks.com.objects.CfgPhones;
import com.genesyslab.platform.applicationblocks.com.objects.CfgPhysicalSwitch;
import com.genesyslab.platform.applicationblocks.com.objects.CfgPlace;
import com.genesyslab.platform.applicationblocks.com.objects.CfgPlaceGroup;
import com.genesyslab.platform.applicationblocks.com.objects.CfgPortInfo;
import com.genesyslab.platform.applicationblocks.com.objects.CfgRemovalEvent;
import com.genesyslab.platform.applicationblocks.com.objects.CfgResourceID;
import com.genesyslab.platform.applicationblocks.com.objects.CfgRole;
import com.genesyslab.platform.applicationblocks.com.objects.CfgRoleMember;
import com.genesyslab.platform.applicationblocks.com.objects.CfgScheduledTask;
import com.genesyslab.platform.applicationblocks.com.objects.CfgScript;
import com.genesyslab.platform.applicationblocks.com.objects.CfgServer;
import com.genesyslab.platform.applicationblocks.com.objects.CfgServerHostID;
import com.genesyslab.platform.applicationblocks.com.objects.CfgServerVersion;
import com.genesyslab.platform.applicationblocks.com.objects.CfgService;
import com.genesyslab.platform.applicationblocks.com.objects.CfgServiceInfo;
import com.genesyslab.platform.applicationblocks.com.objects.CfgSkill;
import com.genesyslab.platform.applicationblocks.com.objects.CfgSkillLevel;
import com.genesyslab.platform.applicationblocks.com.objects.CfgSolutionComponent;
import com.genesyslab.platform.applicationblocks.com.objects.CfgSolutionComponentDefinition;
import com.genesyslab.platform.applicationblocks.com.objects.CfgStatDay;
import com.genesyslab.platform.applicationblocks.com.objects.CfgStatInterval;
import com.genesyslab.platform.applicationblocks.com.objects.CfgStatTable;
import com.genesyslab.platform.applicationblocks.com.objects.CfgSubcode;
import com.genesyslab.platform.applicationblocks.com.objects.CfgSwitch;
import com.genesyslab.platform.applicationblocks.com.objects.CfgSwitchAccessCode;
import com.genesyslab.platform.applicationblocks.com.objects.CfgTableAccess;
import com.genesyslab.platform.applicationblocks.com.objects.CfgTenant;
import com.genesyslab.platform.applicationblocks.com.objects.CfgTenantBrief;
import com.genesyslab.platform.applicationblocks.com.objects.CfgTimeZone;
import com.genesyslab.platform.applicationblocks.com.objects.CfgTransaction;
import com.genesyslab.platform.applicationblocks.com.objects.CfgTreatment;
import com.genesyslab.platform.applicationblocks.com.objects.CfgUpdatePackageRecord;
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
import com.genesyslab.platform.configuration.protocol.types.CfgFlag;
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
import java.util.logging.Level;
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

        logger.info("searching " + "Place [" + placeName + "]");
        CfgPlace cfgPlace = service.retrieveObject(CfgPlace.class, cfgPlaceQuery);
        if (cfgPlace == null) {
            if (mastExist) {
                throw new ConfigException("Place [" + placeName + "]");
            }
        } else {
            logger.info("Found " + "Place [" + placeName + "] DBID=" + cfgPlace.getDBID());
        }

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

    private String getDNs(Collection<Integer> dns) {
        StringBuilder ret = new StringBuilder();
        if (dns != null) {
            for (Integer dbid : dns) {
                final ICfgObject retrieveObject;
                try {
                    retrieveObject = retrieveObject(CfgObjectType.CFGDN, dbid);
                    if (ret.length() > 0) {
                        ret.append("; ");
                    }
                    if (retrieveObject != null) {
                        ret.append(nameDBID(retrieveObject));
                    }

                } catch (ConfigException ex) {
                    java.util.logging.Logger.getLogger(ConfigServerManager.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return ret.toString();
    }

    private String nameDBID(ICfgObject obj) {
        if (obj != null) {
            String s = getObjName(obj);
            return s + "(DBID:" + obj.getObjectDbid() + ")";
        } else {
            return "";
        }

    }

    public void checkPlace(String pl, HashMap<SwitchLookup, String> theDNs) throws ConfigException {

        parentForm.requestOutput("*** Checking for place [" + pl + "]" + " DNs: " + dnsList(theDNs));
        for (Map.Entry<SwitchLookup, String> entry : theDNs.entrySet()) {
            CfgDN newDN = findDN(service, entry.getValue(), entry.getKey().getSw(), false);
            if (newDN != null) {
                String dn = nameDBID(newDN);
                CfgObject dependend = getDependend(newDN);
                if (dependend == null) {
                    parentForm.requestOutput("Found DN " + dn + " no place ");
                } else {
                    parentForm.requestOutput("Found " + dn + " place: " + nameDBID(dependend) + "DNs: " + getDNs(((CfgPlace) dependend).getDNDBIDs()));
                }

            } else {
                parentForm.requestOutput("! not found: " + dnEntry(entry));
            }
        }
        CfgPlace cfgPlace = findPlace(service, pl, false);
        if (cfgPlace != null) {
            parentForm.requestOutput("Found place : " + nameDBID(cfgPlace) + ") DNs:" + getDNs(cfgPlace.getDNDBIDs()));

        } else {
            parentForm.requestOutput("! not found place");
        }
    }

    public static String getObjName(final ICfgObject retrieveObject) {
        if (retrieveObject == null) {
            return null;
        } else if (retrieveObject instanceof CfgDetectEvent) {
            return ((CfgDetectEvent) retrieveObject).toString();
        } else if (retrieveObject instanceof CfgDN) {
            return ((CfgDN) retrieveObject).getNumber();
        } else if (retrieveObject instanceof CfgDNAccessNumber) {
            return ((CfgDNAccessNumber) retrieveObject).getNumber();
        } else if (retrieveObject instanceof CfgDNGroup) {
            return ((CfgDNGroup) retrieveObject).getType().toString();
        } else if (retrieveObject instanceof CfgDNInfo) {
            return ((CfgDNInfo) retrieveObject).toString();
        } else if (retrieveObject instanceof CfgEnumerator) {
            return ((CfgEnumerator) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgEnumeratorValue) {
            return ((CfgEnumeratorValue) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgField) {
            return ((CfgField) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgFilter) {
            return ((CfgFilter) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgFolder) {
            return ((CfgFolder) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgFormat) {
            return ((CfgFormat) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgGroup) {
            return ((CfgGroup) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgGVPCustomer) {
            return ((CfgGVPCustomer) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgGVPIVRProfile) {
            return ((CfgGVPIVRProfile) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgGVPReseller) {
            return ((CfgGVPReseller) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgHost) {
            return ((CfgHost) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgID) {
            return ((CfgID) retrieveObject).toString();
        } else if (retrieveObject instanceof CfgIVR) {
            return ((CfgIVR) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgIVRPort) {
            return ((CfgIVRPort) retrieveObject).getPortNumber();
        } else if (retrieveObject instanceof CfgMemberID) {
            return ((CfgMemberID) retrieveObject).toString();
        } else if (retrieveObject instanceof CfgObjectID) {
            return ((CfgObjectID) retrieveObject).toString();
        } else if (retrieveObject instanceof CfgObjectiveTable) {
            return ((CfgObjectiveTable) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgObjectiveTableRecord) {
            return ((CfgObjectiveTableRecord) retrieveObject).toString();
        } else if (retrieveObject instanceof CfgObjectResource) {
            return ((CfgObjectResource) retrieveObject).toString();
        } else if (retrieveObject instanceof CfgOS) {
            return ((CfgOS) retrieveObject).getOStype().toString();
        } else if (retrieveObject instanceof CfgOwnerID) {
            return ((CfgOwnerID) retrieveObject).toString();
        } else if (retrieveObject instanceof CfgParentID) {
            return ((CfgParentID) retrieveObject).toString();
        } else if (retrieveObject instanceof CfgPerson) {
            return ((CfgPerson) retrieveObject).getUserName();
        } else if (retrieveObject instanceof CfgPersonBrief) {
            return ((CfgPersonBrief) retrieveObject).toString();
        } else if (retrieveObject instanceof CfgPersonLastLogin) {
            return ((CfgPersonLastLogin) retrieveObject).toString();
        } else if (retrieveObject instanceof CfgPhones) {
            return ((CfgPhones) retrieveObject).toString();
        } else if (retrieveObject instanceof CfgPhysicalSwitch) {
            return ((CfgPhysicalSwitch) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgPlace) {
            return ((CfgPlace) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgPlaceGroup) {
            return ((CfgPlaceGroup) retrieveObject).toString();
        } else if (retrieveObject instanceof CfgPortInfo) {
            return ((CfgPortInfo) retrieveObject).toString();
        } else if (retrieveObject instanceof CfgRemovalEvent) {
            return ((CfgRemovalEvent) retrieveObject).toString();
        } else if (retrieveObject instanceof CfgResourceID) {
            return ((CfgResourceID) retrieveObject).toString();
        } else if (retrieveObject instanceof CfgRole) {
            return ((CfgRole) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgRoleMember) {
            return ((CfgRoleMember) retrieveObject).toString();
        } else if (retrieveObject instanceof CfgScheduledTask) {
            return ((CfgScheduledTask) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgScript) {
            return ((CfgScript) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgServer) {
            return ((CfgServer) retrieveObject).toString();
        } else if (retrieveObject instanceof CfgServerHostID) {
            return ((CfgServerHostID) retrieveObject).toString();
        } else if (retrieveObject instanceof CfgServerVersion) {
            return ((CfgServerVersion) retrieveObject).toString();
        } else if (retrieveObject instanceof CfgService) {
            return ((CfgService) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgServiceInfo) {
            return ((CfgServiceInfo) retrieveObject).toString();
        } else if (retrieveObject instanceof CfgSkill) {
            return ((CfgSkill) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgSkillLevel) {
            return ((CfgSkillLevel) retrieveObject).toString();
        } else if (retrieveObject instanceof CfgSolutionComponent) {
            return ((CfgSolutionComponent) retrieveObject).toString();
        } else if (retrieveObject instanceof CfgSolutionComponentDefinition) {
            return ((CfgSolutionComponentDefinition) retrieveObject).toString();
        } else if (retrieveObject instanceof CfgStatDay) {
            return ((CfgStatDay) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgStatInterval) {
            return ((CfgStatInterval) retrieveObject).toString();
        } else if (retrieveObject instanceof CfgStatTable) {
            return ((CfgStatTable) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgSubcode) {
            return ((CfgSubcode) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgSwitch) {
            return ((CfgSwitch) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgSwitchAccessCode) {
            return ((CfgSwitchAccessCode) retrieveObject).toString();
        } else if (retrieveObject instanceof CfgTableAccess) {
            return ((CfgTableAccess) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgTenant) {
            return ((CfgTenant) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgTenantBrief) {
            return ((CfgTenantBrief) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgTimeZone) {
            return ((CfgTimeZone) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgTransaction) {
            return ((CfgTransaction) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgTreatment) {
            return ((CfgTreatment) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgUpdatePackageRecord) {
            return ((CfgUpdatePackageRecord) retrieveObject).toString();
        } else if (retrieveObject instanceof CfgVoicePrompt) {
            return ((CfgVoicePrompt) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgAccessGroup) {
            return ((CfgAccessGroup) retrieveObject).getObjectPath();
        } else if (retrieveObject instanceof CfgAccessGroupBrief) {
            return ((CfgAccessGroupBrief) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgACE) {
            return ((CfgACE) retrieveObject).getID().toString();
        } else if (retrieveObject instanceof CfgACEID) {
            return ((CfgACEID) retrieveObject).toString();
        } else if (retrieveObject instanceof CfgACL) {
            return ((CfgACL) retrieveObject).toString();
        } else if (retrieveObject instanceof CfgACLID) {
            return ((CfgACLID) retrieveObject).toString();
        } else if (retrieveObject instanceof CfgActionCode) {
            return ((CfgActionCode) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgAddress) {
            return ((CfgAddress) retrieveObject).toString();
        } else if (retrieveObject instanceof CfgAgentGroup) {
            return ((CfgAgentGroup) retrieveObject).getGroupInfo().toString();
        } else if (retrieveObject instanceof CfgAgentInfo) {
            return ((CfgAgentInfo) retrieveObject).toString();
        } else if (retrieveObject instanceof CfgAgentLogin) {
            return ((CfgAgentLogin) retrieveObject).getLoginCode();
        } else if (retrieveObject instanceof CfgAgentLoginInfo) {
            return ((CfgAgentLoginInfo) retrieveObject).getAgentLogin().getLoginCode();
        } else if (retrieveObject instanceof CfgAlarmCondition) {
            return ((CfgAlarmCondition) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgApplication) {
            return ((CfgApplication) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgAppPrototype) {
            return ((CfgAppPrototype) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgAppRank) {
            return ((CfgAppRank) retrieveObject).getAppRank().toString();
        } else if (retrieveObject instanceof CfgAppServicePermission) {
            return ((CfgAppServicePermission) retrieveObject).toString();
        } else if (retrieveObject instanceof CfgCallingList) {
            return ((CfgCallingList) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgCallingListInfo) {
            return ((CfgCallingListInfo) retrieveObject).toString();
        } else if (retrieveObject instanceof CfgCampaign) {
            return ((CfgCampaign) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgCampaignGroup) {
            return ((CfgCampaignGroup) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgCampaignGroupInfo) {
            return ((CfgCampaignGroupInfo) retrieveObject).toString();
        } else if (retrieveObject instanceof CfgConnInfo) {
            return ((CfgConnInfo) retrieveObject).toString();
        } else if (retrieveObject instanceof CfgDelSwitchAccess) {
            return ((CfgDelSwitchAccess) retrieveObject).toString();
        } else if (retrieveObject instanceof CfgDeltaAccessGroup) {
            return ((CfgDeltaAccessGroup) retrieveObject).toString();
        } else if (retrieveObject instanceof CfgDeltaActionCode) {
            return ((CfgDeltaActionCode) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgDeltaAgentGroup) {
            return ((CfgDeltaAgentGroup) retrieveObject).toString();
        } else if (retrieveObject instanceof CfgDeltaAgentInfo) {
            return ((CfgDeltaAgentInfo) retrieveObject).toString();
        } else if (retrieveObject instanceof CfgDeltaAgentLogin) {
            return ((CfgDeltaAgentLogin) retrieveObject).toString();
        } else if (retrieveObject instanceof CfgDeltaAlarmCondition) {
            return ((CfgDeltaAlarmCondition) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgDeltaApplication) {
            return ((CfgDeltaApplication) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgDeltaAppPrototype) {
            return ((CfgDeltaAppPrototype) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgDeltaCallingList) {
            return ((CfgDeltaCallingList) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgDeltaCampaign) {
            return ((CfgDeltaCampaign) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgDeltaCampaignGroup) {
            return ((CfgDeltaCampaignGroup) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgDeltaDN) {
            return ((CfgDeltaDN) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgDeltaDNGroup) {
            return ((CfgDeltaDNGroup) retrieveObject).toString();
        } else if (retrieveObject instanceof CfgDeltaEnumerator) {
            return ((CfgDeltaEnumerator) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgDeltaEnumeratorValue) {
            return ((CfgDeltaEnumeratorValue) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgDeltaField) {
            return ((CfgDeltaField) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgDeltaFilter) {
            return ((CfgDeltaFilter) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgDeltaFolder) {
            return ((CfgDeltaFolder) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgDeltaFormat) {
            return ((CfgDeltaFormat) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgDeltaGroup) {
            return ((CfgDeltaGroup) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgDeltaGVPCustomer) {
            return ((CfgDeltaGVPCustomer) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgDeltaGVPIVRProfile) {
            return ((CfgDeltaGVPIVRProfile) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgDeltaGVPReseller) {
            return ((CfgDeltaGVPReseller) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgDeltaHost) {
            return ((CfgDeltaHost) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgDeltaIVR) {
            return ((CfgDeltaIVR) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgDeltaIVRPort) {
            return ((CfgDeltaIVRPort) retrieveObject).toString();
        } else if (retrieveObject instanceof CfgDeltaObjectiveTable) {
            return ((CfgDeltaObjectiveTable) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgDeltaPerson) {
            return ((CfgDeltaPerson) retrieveObject).toString();
        } else if (retrieveObject instanceof CfgDeltaPersonLastLogin) {
            return ((CfgDeltaPersonLastLogin) retrieveObject).toString();
        } else if (retrieveObject instanceof CfgDeltaPhysicalSwitch) {
            return ((CfgDeltaPhysicalSwitch) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgDeltaPlace) {
            return ((CfgDeltaPlace) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgDeltaPlaceGroup) {
            return ((CfgDeltaPlaceGroup) retrieveObject).toString();
        } else if (retrieveObject instanceof CfgDeltaRole) {
            return ((CfgDeltaRole) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgDeltaScheduledTask) {
            return ((CfgDeltaScheduledTask) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgDeltaScript) {
            return ((CfgDeltaScript) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgDeltaService) {
            return ((CfgDeltaService) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgDeltaSkill) {
            return ((CfgDeltaSkill) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgDeltaStatDay) {
            return ((CfgDeltaStatDay) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgDeltaStatTable) {
            return ((CfgDeltaStatTable) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgDeltaSwitch) {
            return ((CfgDeltaSwitch) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgDeltaTableAccess) {
            return ((CfgDeltaTableAccess) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgDeltaTenant) {
            return ((CfgDeltaTenant) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgDeltaTimeZone) {
            return ((CfgDeltaTimeZone) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgDeltaTransaction) {
            return ((CfgDeltaTransaction) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgDeltaTreatment) {
            return ((CfgDeltaTreatment) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgDeltaVoicePrompt) {
            return ((CfgDeltaVoicePrompt) retrieveObject).getName();

        } else {
            return null;
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
            final String dn, final CfgSwitch sw, boolean mustExist
    ) throws ConfigException {
        CfgDNQuery dnQuery = new CfgDNQuery();

        dnQuery.setDnNumber(dn);
        dnQuery.setSwitchDbid(sw.getDBID());

        logger.info("searching " + "DN [" + dn + "] switch[" + sw.getName() + "]");
        CfgDN cfgDn = service.retrieveObject(CfgDN.class, dnQuery);
        if (cfgDn == null) {
            if (mustExist) {
                throw new ConfigException("DN [" + dn + "] switch[" + sw.getName() + "]");
            }
        } else {
            logger.info("found " + "DN [" + dn + "] switch[" + sw.getName() + "] DBID:" + cfgDn.getDBID());
        }
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
                CfgDN cfgDN = findDN(service, value, key.getSw(), true);
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

    private String dnEntry(Map.Entry<SwitchLookup, String> entry) {
        StringBuilder ret = new StringBuilder();
        SwitchLookup key = entry.getKey();
        String val = entry.getValue();

        ret.append("[").append(key.getSw().getName()).append("]").append("/").append(val);
        return ret.toString();
    }

    private String dnsList(HashMap<SwitchLookup, String> theDNs) {
        StringBuilder ret = new StringBuilder();
        for (Map.Entry<SwitchLookup, String> entry : theDNs.entrySet()) {
            if (ret.length() > 0) {
                ret.append(" ;");
            }
            ret.append(dnEntry(entry));
        }
        return ret.toString();
    }

    public HashMap<Integer, CfgObject> getAllDBID_AgentLogin() {
        HashMap<Integer, CfgObject> ret = new HashMap<>();
        for (CfgObject obj : getAllAgentLogins()) {
            ret.put(obj.getObjectDbid(), obj);

        }
        return ret;

    }

    public ArrayList<CfgObject> getAllAgentLogins() {
        ArrayList<CfgObject> agentLogins = new ArrayList<>();

        final ICfgObjectFoundProc foundProc = new ICfgObjectFoundProc() {
            @Override
            public boolean proc(final CfgObject obj, KeyValueCollection kv, final int current,
                    final int total) {
                agentLogins.add(obj);
                return true;
            }
        };
        try {
            final CfgAgentLoginQuery query = new CfgAgentLoginQuery();

            if (findObjects(query, CfgAgentLogin.class, new IKeyValueProperties() {
                @Override
                public KeyValueCollection getProperties(final CfgObject obj) {
                    return null;
                }

                @Override
                public Collection<String> getName(final CfgObject obj) {

                    return null;
                }
            }, new FindWorker(FIND_ALL), true, foundProc)) {

            }

        } catch (final ConfigException | InterruptedException ex) {
            java.util.logging.Logger.getLogger(AppForm.class.getName()).log(Level.SEVERE, null, ex);
        }
        return agentLogins;

    }

    public HashMap<Integer, CfgObject> getAllExtDBID_Extension() {
        ArrayList<CfgDN> allExtensions = getAllExtensions();
        HashMap<Integer, CfgObject> ret = new HashMap<>();
        for (CfgDN ext : allExtensions) {
            if (ext.getType() == CfgDNType.CFGExtension) {
                ret.put(ext.getDBID(), ext);
            }
        }
        return ret;
    }

    private <T extends CfgObject> ArrayList<T> getAll(final CfgQuery q, final Class<T> cls) {
        ArrayList< T> exts = new ArrayList<>();

        final ICfgObjectFoundProc foundProc = new ICfgObjectFoundProc() {
            @Override
            public boolean proc(final CfgObject obj, KeyValueCollection kv, final int current,
                    final int total) {
                exts.add((T) obj);
                return true;
            }
        };
        try {

            if (findObjects(q, cls, new IKeyValueProperties() {
                @Override
                public KeyValueCollection getProperties(final CfgObject obj) {
                    return null;
                }

                @Override
                public Collection<String> getName(final CfgObject obj) {
                    return null;
                }
            }, new FindWorker(FIND_ALL), true, foundProc)) {

            }

        } catch (final ConfigException | InterruptedException ex) {
            java.util.logging.Logger.getLogger(AppForm.class.getName()).log(Level.SEVERE, null, ex);
        }
        return exts;
    }

    public ArrayList< CfgDN> getAllExtensions() {
        final CfgDNQuery query = new CfgDNQuery();
        query.setDnType(CfgDNType.CFGExtension);
        ArrayList<CfgDN> all = getAll(query, CfgDN.class);
        return all;
    }

    public HashMap<Integer, CfgObject> getAllExtDBID_Place() {
        HashMap<Integer, CfgObject> extPlace = new HashMap<>();

        for (CfgPlace pl : getAll(new CfgPlaceQuery(), CfgPlace.class)) {
            Collection<Integer> dndbiDs = pl.getDNDBIDs();
            if (dndbiDs != null) {
                for (Integer dnDBID : dndbiDs) {
                    extPlace.put(dnDBID, pl);
                }
            }
        }

        return extPlace;

    }

    public HashMap<Integer, CfgObject> getAllLoginID_Agents() {
        HashMap<Integer, CfgObject> agent = new HashMap<>();
        for (CfgObject obj : getAllAgents()) {
            if (obj instanceof CfgPerson) {
                CfgPerson o = (CfgPerson) obj;
                if (o.getIsAgent() == CfgFlag.CFGTrue) {
                    CfgAgentInfo agentInfo = o.getAgentInfo();
                    for (CfgAgentLoginInfo ali : agentInfo.getAgentLogins()) {
                        if (agent.containsKey(ali.getAgentLoginDBID())) {
                            parentForm.showError("loginID assigned to more than one agent: " + ali.getAgentLoginDBID());
                        } else {
                            agent.put(ali.getAgentLoginDBID(), obj);
                        }
                    }
                }
            }
        }
        return agent;
    }

    private ArrayList< CfgPerson> getAllAgents() {
        final CfgPersonQuery query = new CfgPersonQuery();
        query.setIsAgent(CfgFlag.CFGTrue.asInteger());
        return getAll(query, CfgPerson.class);

    }

    private final static ISearchSettings FIND_ALL = new ISearchSettings() {
        @Override
        public boolean isCaseSensitive() {
            return false;
        }

        @Override
        public boolean isRegex() {
            return false;
        }

        @Override
        public boolean isFullOutputSelected() {
            return false;
        }

        @Override
        public boolean isSearchAll() {
            return false;
        }

        @Override
        public String getAllSearch() {
            return null;
        }

        @Override
        public String getSection() {
            return null;
        }

        @Override
        public String getObjName() {
            return null;
        }

        @Override
        public String getOption() {
            return null;
        }

        @Override
        public String getValue() {
            return null;
        }

    };
}
