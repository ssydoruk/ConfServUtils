/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ssydoruk.confservutils;

import Utils.Pair;

import static Utils.StringUtils.matching;
import static java.util.Collections.*;

import com.ssydoruk.confservutils.ConfigConnection;
import com.ssydoruk.confservutils.ConfigServerManager;
import com.genesyslab.platform.applicationblocks.com.*;
import com.genesyslab.platform.applicationblocks.com.objects.*;
import com.genesyslab.platform.applicationblocks.com.queries.*;
import com.genesyslab.platform.commons.collections.*;
import com.genesyslab.platform.commons.protocol.*;
import com.genesyslab.platform.configuration.protocol.confserver.events.*;
import com.genesyslab.platform.configuration.protocol.confserver.requests.objects.*;
import com.genesyslab.platform.configuration.protocol.obj.*;
import com.genesyslab.platform.configuration.protocol.types.*;
import com.genesyslab.platform.configuration.protocol.utilities.*;
import confserverbatch.*;

import java.util.*;
import java.util.logging.Level;
import java.util.regex.*;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;

/**
 * @author stepan_sydoruk
 */
public class ConfigServerManager {

	public AppForm getParentForm() {
		return parentForm;
	}

	public static final Logger logger = (Logger) Main.logger;
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
		public boolean isAllKVPsInOutput() {
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

	static String getFolderFullName(CfgFolder fld) {
		return fld.getObjectPath() + "\\[" + fld.getName() + "]";

	}

	private IConfService service = null;
	private final AppForm parentForm;
	private final HashMap<String, Collection<? extends CfgObject>> prevQueries = new HashMap<>();
	private final HashSet<CfgObjectType> lastUpdatedObjects = new HashSet<>();

	ConfigServerManager(AppForm aThis) {
		parentForm = aThis;
		theCfgObjComparator = new CfgObjComparator();
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

	IConfService connect(StoredSettings.ConfServer confServ, String user, String string)
			throws ConfigException, InterruptedException, ProtocolException {

		try {
			disconnect();
		} catch (ProtocolException | IllegalStateException | InterruptedException ex) {
			logger.fatal(ex);
		}
		parentForm.requestOutput("connecting...\n", false);

		return service = ConfigConnection
			.initializeConfigService(confServ.getApp(), confServ.getHost(), confServ.getPortInt(), user, string);

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

	private class CfgObjComparator implements Comparator<ICfgObject> {

		@Override
		public int compare(ICfgObject o1, ICfgObject o2) {
			return 0;
		}
	}

	private final CfgObjComparator theCfgObjComparator;

	public <T extends CfgObject> Collection<T> getResults(CfgQuery q, final Class<T> cls, boolean refresh)
			throws ConfigException, InterruptedException {
		Main.logger.debug("query " + q + " for object type " + cls);
		String qToString = q.toString();
		checkQueryNeedsUpdate();

		Collection<T> cfgObjs;
		if (!refresh && (prevQueries.containsKey(qToString) && prevQueries.get(qToString) != null)) {
			cfgObjs = (Collection<T>) prevQueries.get(qToString);
		} else {
			Main.logger.debug("executing the request " + q);
			cfgObjs = service.retrieveMultipleObjects(cls, q);
			Main.logger.debug("retrieved " + ((cfgObjs == null) ? 0 : cfgObjs.size()) + " objects");
//            Collections.sort(cfgObjs);
			prevQueries.put(qToString, cfgObjs);
		}
		return cfgObjs;
	}

	public <T extends CfgObject> Collection<T> getResults(CfgQuery q, final Class<T> cls)
			throws ConfigException, InterruptedException {
		return getResults(q, cls, false);
	}

	private <T extends CfgObject> void findApps(CfgQuery q, Class<T> cls, IKeyValueProperties props, ISearchSettings ss)
			throws ConfigException, InterruptedException {

		StringBuilder buf = new StringBuilder();

		Collection<T> cfgObjs = getResults(q, cls);

		if (cfgObjs == null || cfgObjs.isEmpty()) {
			logger.debug("no objects found\n", false);
		} else {
			logger.debug("retrieved " + cfgObjs.size() + " total objects type " + cls.getSimpleName());
			int flags = ((ss.isRegex()) ? Pattern.LITERAL : 0)
					| ((ss.isCaseSensitive()) ? 0 : Pattern.CASE_INSENSITIVE);
			Pattern ptAll = null;
			Pattern ptSection = null;
			Pattern ptOption = null;
			Pattern ptVal = null;
			String section = ss.getSection();
			String option = ss.getOption();
			String val = ss.getValue();

			if (ss.isSearchAll()) {
				ptAll = (ss.isSearchAll() && ss.getAllSearch() != null ? Pattern.compile(ss.getAllSearch(), flags)
						: null);
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
					if (ptAll != null) { // if we got here and we are searching for all, it means name is already
											// matched
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
										logger
											.debug("value [" + value + "] is of type " + value
												.getClass() + " obj: " + cfgObj);
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
						buf
							.append("\"")
							.append(names[0])
							.append("\"")
							.append(" path: ")
							.append(cfgObj.getObjectPath())
							.append(", type:")
							.append(cfgObj.getObjectType())
							.append(", DBID: ")
							.append(cfgObj.getObjectDbid());
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
				parentForm
					.requestOutput("Error on object update: " + CfgUtilities
						.getErrorCode(((EventError) resp).getErrorCode()) + "\tDescription: " + ((EventError) resp)
							.getDescription());
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

	private boolean doCreatePlace(IConfService service, String pl, ArrayList<CfgDN> cfgDNs, Integer folderDBID)
			throws ConfigException {
		CfgPlace cfgPlace = new CfgPlace(service);

		cfgPlace.setDNs(cfgDNs);
		cfgPlace.setName(pl);
		if (folderDBID != null) {
			cfgPlace.setFolderId(folderDBID);
		}
		cfgPlace.setTenantDBID(WellKnownDBIDs.EnvironmentDBID);
		parentForm.requestOutput("Creating Place [" + cfgPlace + "]");

		cfgPlace.save();
		if (cfgPlace.isSaved()) {
			parentForm.requestOutput("Created place DBID: " + cfgPlace.getDBID());
		}
		return true;
	}

	private CfgPlace findPlace(final IConfService service, final String placeName, boolean mastExist)
			throws ConfigException {
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

	public AbstractCollection<CfgFolder> findFolders(FindObject objName, CfgObjectType type) {
		// <editor-fold defaultstate="collapsed" desc="iSearchSettings">
		ISearchSettings seearchSettings = new ISearchSettings() {
			@Override
			public boolean isCaseSensitive() {
				return (objName == null) ? false : objName.isCaseSensitive();
			}

			@Override
			public boolean isRegex() {
				return (objName == null) ? false : objName.isRegex();
			}

			@Override
			public boolean isFullOutputSelected() {
				return false;
			}

			@Override
			public boolean isAllKVPsInOutput() {
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
				return (objName == null) ? null : objName.getName();
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
		// </editor-fold>
		ArrayList<CfgFolder> ret = new ArrayList<>();
		ICfgObjectFoundProc foundProc = (CfgObject obj, KeyValueCollection kv, int current, int total) -> {
			ret.add((CfgFolder) obj);
			return true;
		};
		try {
			CfgFolderQuery q = new CfgFolderQuery();
			q.setType(type.ordinal());
			q.setObjectType(CfgObjectType.CFGFolder.ordinal());

			if (findObjects(q, CfgFolder.class, new IKeyValueProperties() {
				@Override
				public KeyValueCollection getProperties(CfgObject obj) {
					return null;
				}

				@Override
				public Collection<String> getName(CfgObject obj) {
					Collection<String> ret = new ArrayList<>();
					ret.add(((CfgFolder) obj).getName());
					return ret;
				}
			}, new FindWorker(seearchSettings), true, foundProc)) {

			}
			sort(ret, (o1, o2) -> {
				return getFolderFullName(((CfgFolder) o1)).compareToIgnoreCase(getFolderFullName((CfgFolder) o2));
			});
			if (logger.isDebugEnabled()) {
				StringBuilder b = new StringBuilder();
				for (CfgFolder result : ret) {
					b.append("[").append(result.getName()).append("] at ").append(result.getObjectPath()).append("\n");
				}
				logger.debug("Found total folders: " + b);
			}
			return ret;

		} catch (ConfigException | InterruptedException ex) {
			java.util.logging.Logger.getLogger(AppForm.class.getName()).log(Level.SEVERE, null, ex);
		}
		return null;
	}

	private boolean placeDNsEqual(Collection<CfgDN> placeDNs, ArrayList<CfgDN> cfgDNs) {
		logger
			.info("placeDNsEqual: placeDNs: " + StringUtils.join(placeDNs, ",") + "; DNs: " + StringUtils
				.join(cfgDNs, ","));
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

	private boolean createPlace(String plName, ArrayList<CfgDN> cfgDNs, ExistingObjectDecider eod, Integer folderDBID)
			throws ConfigException {
		try {
			return doCreatePlace(service, plName, cfgDNs, folderDBID);
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
					return doCreatePlace(service, plName, cfgDNs, folderDBID);

				case REUSE:
					parentForm.requestOutput("Reusing place " + cfgPlace.getName());
					if (cfgPlace != null) {
						if (!placeDNsEqual(cfgPlace.getDNs(), cfgDNs)) {
							parentForm.requestOutput("Place found but DNs are different. Cannot reuse");
							return false; // this will ensure exception thrown

						}

					}
					return true;

				case SKIP:
					return true;

				case FAIL:
					return false;

				default:
					break;// fails
				}
			} else {
				logger.error("Unexpected exception " + ex);
			}
			throw ex;
		}
	}

	private String getDNs(Collection<Integer> dns, String delim) {
		StringBuilder ret = new StringBuilder();
		if (dns != null) {
			for (Integer dbid : dns) {
				final ICfgObject retrieveObject;
				try {
					retrieveObject = retrieveObject(CfgObjectType.CFGDN, dbid);
					if (ret.length() > 0) {
						ret.append(delim);
					}
					if (retrieveObject != null) {
						ret.append(objectBasicInfo((CfgObject) retrieveObject));
					}

				} catch (ConfigException ex) {
					java.util.logging.Logger.getLogger(ConfigServerManager.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
		}
		return ret.toString();
	}

	private String objectBasicInfo(CfgObject obj) {
		if (obj != null) {
			String s = getObjName(obj);
			return s + "(DBID:" + obj.getObjectDbid() + ") at [" + obj.getObjectPath() + "]";
		} else {
			return "";
		}

	}

	public void checkLoginID(String loginID) throws ConfigException, InterruptedException {

		parentForm.requestOutput("*** Checking for loginID [" + loginID + "]");

		Collection<CfgAgentLogin> findLoginIDs = findLoginIDs(service, loginID);
		if (findLoginIDs != null && !findLoginIDs.isEmpty()) {
			for (CfgAgentLogin newAgentLogin : findLoginIDs) {
				String agentLogin = objectBasicInfo(newAgentLogin);
				CfgObject dependend = getDependend(newAgentLogin);
				if (dependend == null) {
					parentForm.requestOutput("Found LoginID " + agentLogin + " no person ");
				} else {
					parentForm.requestOutput("Found " + agentLogin + "\n\tPerson: " + objectBasicInfo(dependend));
				}
			}
		} else {
			parentForm.requestOutput("! not found [" + loginID + "]");
		}
	}

	public void checkPlace(String pl, String theDN) throws ConfigException, InterruptedException {

		parentForm.requestOutput("*** Checking for place [" + pl + "]" + " DN: " + theDN);

		Collection<CfgDN> findDNs = findDNs(service, theDN);
		if (findDNs != null && !findDNs.isEmpty()) {
			for (CfgDN newDN : findDNs) {
				String dn = objectBasicInfo(newDN);
				CfgObject dependend = getDependend(newDN);
				if (dependend == null) {
					parentForm.requestOutput("Found DN " + dn + " no place ");
				} else {
					parentForm
						.requestOutput("Found " + dn + "\n\tplace: " + objectBasicInfo(dependend) + "\n\t\tDNs: " + getDNs(((CfgPlace) dependend)
							.getDNDBIDs(), "\n\t\t"));
				}
			}
		} else {
			parentForm.requestOutput("! not found [" + theDN + "]");
		}
		CfgPlace cfgPlace = findPlace(service, pl, false);
		if (cfgPlace != null) {
			parentForm
				.requestOutput("Found place : " + objectBasicInfo(cfgPlace) + ") DNs:\n\t" + getDNs(cfgPlace
					.getDNDBIDs(), "\n\t"));

		} else {
			parentForm.requestOutput("! not found place");
		}
	}

	/**
	 * Creates place with DNs
	 *
	 * @param thePlace
	 * @param DNs
	 * @param eod
	 * @param lastSelectedPlaceFolder
	 * @return false if user selects to stop process; true otherwise
	 * @throws ConfigException
	 */
	public boolean createPlace(String thePlace, HashMap<SwitchObjectLocation, String> DNs, ExistingObjectDecider eod,
			ArrayList<CfgFolder> lastSelectedPlaceFolder) throws ConfigException {
		if (lastSelectedPlaceFolder != null && !lastSelectedPlaceFolder.isEmpty()) {
			return createPlace(thePlace, DNs, eod, lastSelectedPlaceFolder.get(0).getDBID());
		} else {
			return createPlace(thePlace, DNs, eod, (Integer) null);
		}
	}

	public boolean createPersonFromAgent(CfgPerson cfgPerson, ExistingObjectDecider eod)
			throws ConfigException, InterruptedException {
		CfgPerson admin = new CfgPerson(service);

		admin.setIsAgent(CfgFlag.CFGFalse);
		admin.setEmployeeID(cfgPerson.getEmployeeID() + "_admin");
		admin.setExternalID(cfgPerson.getEmployeeID());
		admin.setEmailAddress(cfgPerson.getEmailAddress());
		admin.setUserName(cfgPerson.getUserName() + ".admin");
		admin.setIsExternalAuth(CfgFlag.CFGTrue);
		admin.setFirstName(cfgPerson.getFirstName());
		admin.setLastName(cfgPerson.getLastName());
		admin.setTenantDBID(cfgPerson.getTenantDBID());

		parentForm.requestOutput("Creating " + admin.toString());

		try {
			admin.save();
		} catch (ConfigException e) {
			parentForm.requestOutput("failed creation: " + e.toString());
			return false;
		}

//        ConfObject am = new ConfObject(admin.getMetaData().);
//        CfgMetadata metadata = service.getMetaData();
//
//        ConfObject obj0 = new ConfObject(metadata, CfgObjectType.CFGPerson);
//        CfgPerson()
//
//        RequestCreateObject req = RequestCreateObject.create(am);
//        RequestUpdateObject reqUpdate = RequestUpdateObject.create();
//        logger.info("++" + d.toString());
//        reqUpdate.setObjectDelta(d);
//        logger.info("++ req: " + reqUpdate);
//
//        Message ret = cfgManager.execRequest(reqUpdate, objType);
//        logger.info("++ ret: " + ret.toString());
//        objectsUpdated = true;
		return true;
	}

	public boolean createLoginIDs(HashMap<SwitchObjectLocation, String> thelLoginIDs, ExistingObjectDecider eod)
			throws ConfigException, InterruptedException {
		for (Map.Entry<SwitchObjectLocation, String> entry : thelLoginIDs.entrySet()) {
			Pair<ObjectExistAction, CfgObject> newLoginID = createLoginID(entry.getKey(), entry.getValue(), eod);
			if (newLoginID == null || newLoginID.getKey() == ObjectExistAction.FAIL) {
				return false;
			}
			if (newLoginID.getKey() == ObjectExistAction.SKIP) {
				return true;
			}
		}
		return true;
	}

	public boolean createPlace(String pl, HashMap<SwitchObjectLocation, String> theDNs, ExistingObjectDecider eod,
			Integer folderDBID) throws ConfigException {
		ArrayList<CfgDN> cfgDNs = new ArrayList<>();
		for (Map.Entry<SwitchObjectLocation, String> entry : theDNs.entrySet()) {
			Pair<ObjectExistAction, CfgObject> createExtDN = createExtDN(entry.getKey(), entry.getValue(), eod);
			if (createExtDN == null || createExtDN.getKey() == ObjectExistAction.FAIL) {
				return false;
			}
			if (createExtDN.getKey() == ObjectExistAction.SKIP) {
				return true;
			}
			cfgDNs.add((CfgDN) createExtDN.getValue());
		}
		return createPlace(pl, cfgDNs, eod, folderDBID);
	}

	private Pair<ObjectExistAction, CfgObject> doCreateDN(IConfService service, String theNumber, String name,
			CfgDNType type, CfgSwitch sw, Integer folderDBID) throws ConfigException {
		CfgDN dn = new CfgDN(service);

		parentForm.requestOutput("Creating DN [" + name + "] switch [" + sw.getName() + "]");
		dn.setName(name);
		dn.setSwitch(sw);
		dn.setType(type);
		dn.setNumber(theNumber);
		dn.setSwitchSpecificType(1);
		if (folderDBID != null) {
			dn.setFolderId(folderDBID);
		}
		dn.setRouteType(CfgRouteType.CFGDefault);
		dn.save();
		if (dn.isSaved()) {
			parentForm.requestOutput("DN created, DBID: " + dn.getDBID());
			return new Pair(ObjectExistAction.UNKNOWN, dn);
		} else {
			return null;
		}
	}

	private Pair<ObjectExistAction, CfgObject> doCreateLoginID(IConfService service, String name, CfgSwitch sw,
			Integer folderDBID) throws ConfigException {
		CfgAgentLogin login = new CfgAgentLogin(service);

		parentForm.requestOutput("Creating loginID [" + name + "] switch [" + sw.getName() + "]");
		login.setLoginCode(name);
		login.setSwitch(sw);
		login.setUseOverride(CfgFlag.CFGTrue);
		login.setSwitchSpecificType(1);
		if (folderDBID != null) {
			login.setFolderId(folderDBID);
		}
		login.save();
		if (login.isSaved()) {
			parentForm.requestOutput("loginID created, DBID: " + login.getDBID());
			return new Pair(ObjectExistAction.UNKNOWN, login);
		} else {
			return null;
		}
	}

	private Collection<CfgDN> findDNs(final IConfService service, final String dn)
			throws ConfigException, InterruptedException {
		CfgDNQuery dnQuery = new CfgDNQuery();

		dnQuery.setDnNumber(dn);

		logger.info("searching " + "DN [" + dn + "]");
		return getResults(dnQuery, CfgDN.class);

	}

	private Collection<CfgAgentLogin> findLoginIDs(final IConfService service, final String loginID)
			throws ConfigException, InterruptedException {
		return findLoginIDs(service, loginID, null, false);
	}

	private CfgAgentLogin findLoginID(final IConfService service, final String loginid, final CfgSwitch sw,
			boolean mustExist) throws ConfigException, InterruptedException {
		Collection<CfgAgentLogin> ret = findLoginIDs(service, loginid, sw, mustExist);
		if (ret != null && !ret.isEmpty()) {
			return (CfgAgentLogin) ret.toArray()[0];
		} else {
			return null;
		}

	}

	private Collection<CfgAgentLogin> findLoginIDs(final IConfService service, final String loginid, final CfgSwitch sw,
			boolean mustExist) throws ConfigException, InterruptedException {
		CfgAgentLoginQuery dnQuery = new CfgAgentLoginQuery(service);

		dnQuery.setLoginCode(loginid);
		if (sw != null) {
			dnQuery.setSwitchDbid(sw.getDBID());
		}

		logger.info("searching " + "loginid [" + loginid + "]" + ((sw != null) ? " switch[" + sw.getName() + "]" : ""));
		Collection<CfgAgentLogin> loginIDs = service.retrieveMultipleObjects(CfgAgentLogin.class, dnQuery);

		if (loginIDs == null || loginIDs.isEmpty()) {
			if (mustExist) {
				throw new ConfigException("loginid [" + loginid + "] switch[" + sw.getName() + "]");
			}
		}

		logger.info("found " + ((loginIDs != null && loginIDs.isEmpty()) ? loginIDs.size() : 0) + " entries");

		return loginIDs;

	}

	private CfgDN findDN(final IConfService service, final String dn, final CfgSwitch sw, boolean mustExist)
			throws ConfigException {
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

	private Pair<ObjectExistAction, CfgObject> createExtDN(SwitchObjectLocation key, String value,
			ExistingObjectDecider eod) throws ConfigException {
		String name = key.getSw().getName() + "_" + value;
		try {
			return doCreateDN(service, value, name, CfgDNType.CFGExtension, key.getSw(), key.getFolderDBID());
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
					return doCreateDN(service, cfgDN.getNumber(), cfgDN.getName(), cfgDN.getType(), cfgDN
						.getSwitch(), cfgDN.getFolderId());

				case REUSE:
					parentForm.requestOutput("Reusing DN " + cfgDN.getNumber());
					return new Pair(ObjectExistAction.REUSE, cfgDN);

				case SKIP:
					parentForm.requestOutput("skipping " + cfgDN.getNumber());
					return new Pair(ObjectExistAction.SKIP, cfgDN);

				case FAIL:
					return null;
				}
			} else {

				parentForm.requestOutput(ex.getMessage());
			}
			throw ex;
		}
	}

	private Pair<ObjectExistAction, CfgObject> createLoginID(SwitchObjectLocation switchFolder, String loginIDName,
			ExistingObjectDecider eod) throws ConfigException, InterruptedException {
		try {
			return doCreateLoginID(service, loginIDName, switchFolder.getSw(), switchFolder.getFolderDBID());
		} catch (ConfigException ex) {
//            switch(objExistaction)
			if (ex instanceof ConfigServerException
					&& ((ConfigServerException) ex).getErrorType() == CfgErrorType.CFGUniquenessViolation) {
				CfgAgentLogin cfgLoginID = findLoginID(service, loginIDName, switchFolder.getSw(), true);
				parentForm.requestOutput("LoginID exists: " + cfgLoginID.getLoginCode());
				switch (eod.getCurrentAction(makeString(cfgLoginID), makeString(getDependend(cfgLoginID)))) {
				case RECREATE:
					parentForm.requestOutput("Recreating");
					cfgLoginID.delete();
					parentForm.requestOutput(cfgLoginID.getLoginCode() + " deleted");
					return doCreateLoginID(service, loginIDName, cfgLoginID.getSwitch(), cfgLoginID.getFolderId());

				case REUSE:
					parentForm.requestOutput("Reusing loginID " + cfgLoginID.getLoginCode());
					return new Pair(ObjectExistAction.REUSE, cfgLoginID);

				case SKIP:
					parentForm.requestOutput("skipping " + cfgLoginID.getLoginCode());
					return new Pair(ObjectExistAction.SKIP, cfgLoginID);

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
				// parentForm.requestOutput("searching " + "DN [" + dn + "] switch[" +
				// sw.getName() + "]");
				return service.retrieveObject(CfgPlace.class, plQuery);
			} catch (ConfigException ex) {
				logger.error(ex);
				return null;
			}

		} else if (cfgObj instanceof CfgAgentLogin) {
			CfgAgentLogin loginID = (CfgAgentLogin) cfgObj;

			CfgPersonQuery personQuery = new CfgPersonQuery(service);
			personQuery.setLoginDbid(loginID.getDBID());

			try {
				// parentForm.requestOutput("searching " + "DN [" + dn + "] switch[" +
				// sw.getName() + "]");
				return service.retrieveObject(CfgPerson.class, personQuery);
			} catch (ConfigException ex) {
				logger.error(ex);
				return null;
			}

		} else {
			return null;
		}
	}

	private String makeString(CfgObject cfgObj) {
		return (cfgObj == null) ? "" : cfgObj.getObjectPath() + "\n" + cfgObj.toString();
	}

	/**
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
	public <T extends CfgObject> boolean findObjects(final CfgQuery q, final Class<T> cls,
			final IKeyValueProperties props, final FindWorker ss, final boolean checkNames,
			final ICfgObjectFoundProc foundProc) throws ConfigException, InterruptedException {
		int cnt = 0;
		final HashMap<CfgObject, KeyValueCollection> matchedObjects = new HashMap<>();
		final StringBuilder buf = new StringBuilder();
		final Collection<T> cfgObjs = getResults(q, cls);
		if (cfgObjs != null && !cfgObjs.isEmpty()) {
			for (final CfgObject cfgObj : cfgObjs) {
				MatchedKVPs matched = ss.matchConfigObject(cfgObj, props, checkNames);
				KeyValueCollection kv;
				if (matched != null && (kv = matched.getMatchedKVPs()) != null) {
					cnt++;
					if (foundProc == null) {
						if (ss.isFullOutputSelected()) {
							buf
								.append("----> path: ")
								.append(cfgObj.getObjectPath())
								.append(" ")
								.append(cfgObj.toString())
								.append("\n");
						} else {
							final Object[] names = props.getName(cfgObj).toArray();
							buf
								.append("----> \"")
								.append(names[0])
								.append("\"")
								.append(" path: ")
								.append(cfgObj.getObjectPath())
								.append(", type:")
								.append(cfgObj.getObjectType())
								.append(", DBID: ")
								.append(cfgObj.getObjectDbid());
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
								if (ss.isAllKVPOutput())
									buf.append("\t").append(matched.getMatchedSections().toString()).append("\n\n");
								else {
									buf.append("\t").append(kv.toString()).append("\n\n");
								}
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
				parentForm
					.requestOutput("Search done, located " + cnt + " objects type " + cls
						.getSimpleName() + " -->\n" + buf + "<--\n");
			}
		}
		return false;
	}

	boolean doTheSearch(final CfgObjectType t, final ISearchSettings pn, final boolean warnNotFound,
			final boolean checkNames, final ICfgObjectFoundProc foundProc)
			throws ConfigException, InterruptedException {
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
					KeyValueCollection ret = new KeyValueCollection();
					ret.addAll((((CfgApplication) obj).getUserProperties()));
					ret.addAll(((CfgApplication) obj).getOptions());
					return ret;
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
		else if (t == CfgObjectType.CFGCampaignGroup) {
			final CfgCampaignGroupQuery query = new CfgCampaignGroupQuery();
			// String n = pn.getObjName();
			// if (pn.isCaseSensitive() && n != null) {
			// query.setName(n);
			//
			// }
			if (findObjects(query, CfgCampaignGroup.class, new IKeyValueProperties() {
				@Override
				public KeyValueCollection getProperties(final CfgObject obj) {
					KeyValueCollection ret;
					ret = (((CfgCampaignGroup) obj).getUserProperties());
					return ret;
				}

				@Override
				public Collection<String> getName(final CfgObject obj) {
					final Collection<String> ret = new ArrayList<>();
					ret.add(((CfgCampaignGroup) obj).getName());
					ret.add(((CfgCampaignGroup) obj).getDescription());
					return ret;
				}
			}, new FindWorker(pn), checkNames, foundProc)) {
				return false;
			}
		} // </editor-fold>
		else if (t == CfgObjectType.CFGCampaign) {
			final CfgCampaignQuery query = new CfgCampaignQuery();
			// String n = pn.getObjName();
			// if (pn.isCaseSensitive() && n != null) {
			// query.setName(n);
			//
			// }
			if (findObjects(query, CfgCampaign.class, new IKeyValueProperties() {
				@Override
				public KeyValueCollection getProperties(final CfgObject obj) {
					KeyValueCollection ret;
					ret = (((CfgCampaign) obj).getUserProperties());
					return ret;
				}

				@Override
				public Collection<String> getName(final CfgObject obj) {
					final Collection<String> ret = new ArrayList<>();
					ret.add(((CfgCampaign) obj).getName());
					ret.add(((CfgCampaign) obj).getDescription());
					return ret;
				}
			}, new FindWorker(pn), checkNames, foundProc)) {
				return false;
			}
		} // </editor-fold>
		else if (t == CfgObjectType.CFGCallingList) {
			final CfgCallingListQuery query = new CfgCallingListQuery();
			// String n = pn.getObjName();
			// if (pn.isCaseSensitive() && n != null) {
			// query.setName(n);
			//
			// }
			if (findObjects(query, CfgCallingList.class, new IKeyValueProperties() {
				@Override
				public KeyValueCollection getProperties(final CfgObject obj) {
					KeyValueCollection ret;
					ret = (((CfgCallingList) obj).getUserProperties());
					return ret;
				}

				@Override
				public Collection<String> getName(final CfgObject obj) {
					final Collection<String> ret = new ArrayList<>();
					ret.add(((CfgCallingList) obj).getName());
					ret.add(((CfgCallingList) obj).getDescription());
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

	private void execQuery(final CfgQuery query, final ISearchNamedProperties objProperties,
			final BussAttr searchParams) throws ConfigException, InterruptedException {
		final Collection<CfgObject> cfgObjs = query.execute();
		final StringBuilder buf = new StringBuilder();
		if (cfgObjs == null || cfgObjs.isEmpty()) {
			logger.debug("no objects found\n", false);
		} else {
			logger.debug("retrieved " + cfgObjs.size() + " total objects");
			final int flags = ((searchParams.isRegex()) ? Pattern.LITERAL : 0)
					| ((searchParams.isCaseSensitive()) ? 0 : Pattern.CASE_INSENSITIVE);
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

	private String dnEntry(Map.Entry<SwitchObjectLocation, String> entry) {
		StringBuilder ret = new StringBuilder();
		SwitchObjectLocation key = entry.getKey();
		String val = entry.getValue();

		ret.append("[").append(key.getSw().getName()).append("]").append("/").append(val);
		return ret.toString();
	}

	private String dnsList(HashMap<SwitchObjectLocation, String> theDNs) {
		StringBuilder ret = new StringBuilder();
		for (Map.Entry<SwitchObjectLocation, String> entry : theDNs.entrySet()) {
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

		final ICfgObjectFoundProc foundProc = (final CfgObject obj, KeyValueCollection kv, final int current,
				final int total) -> {
			agentLogins.add(obj);
			return true;
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
		ArrayList<T> exts = new ArrayList<>();

		final ICfgObjectFoundProc foundProc = (final CfgObject obj, KeyValueCollection kv, final int current,
				final int total) -> {
			exts.add((T) obj);
			return true;
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

	public ArrayList<CfgDN> getAllExtensions() {
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

	public HashMap<Integer, CfgObject> getAllLoginID_Agents() throws ConfigException, InterruptedException {
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

	private Collection<CfgPerson> getAllAgents() throws ConfigException, InterruptedException {
		final CfgPersonQuery query = new CfgPersonQuery();
		query.setIsAgent(CfgFlag.CFGTrue.asInteger());
		return getResults(query, CfgPerson.class);

	}

	public Collection<CfgPerson> getAllPersons() throws ConfigException, InterruptedException {
		return getResults(new CfgPersonQuery(), CfgPerson.class);

	}

	public boolean connectToConfigServer() throws Exception {

		if (isConnected()) {
			return true;
		} else {
			IConfService ret = null;
			final StoredSettings.ConfServer confServ = (StoredSettings.ConfServer) getParentForm()
				.cbConfigServergetSelectedItem();
			final String user = (String) getParentForm().cbUsergetSelectedItem();
			if (confServ != null && user != null) {
				try {
					ret = connect(confServ, user, getParentForm().pfPasswordgetPassword());

				} catch (ProtocolException | IllegalStateException | InterruptedException ex) {
					getParentForm().showException("Failed to connect to ConfigServer: " + ex.getMessage(), ex);
				}

			}
			getParentForm().connectionStatusChanged();

			return ret != null;
		}
	}

	public ConfObject createObject(ConfObject newObj) throws Exception {
		RequestCreateObject reqCreate = RequestCreateObject.create();
		reqCreate.setObject(newObj);

		Message resp = service.getProtocol().request(reqCreate);

		if (resp instanceof EventObjectCreated) {
			return ((EventObjectCreated) resp).getObject();
		} else if (resp instanceof EventError) {
			String err = "Error on object create: " + CfgUtilities
				.getErrorCode(((EventError) resp).getErrorCode()) + "\tDescription: " + ((EventError) resp)
					.getDescription();
			logger.error(err);
			throw new Exception(err);

		}
		return null;
	}

	Collection findObjects(String objectType, int max, boolean refresh) throws ConfigException, InterruptedException {
		CfgObjectType t = CfgObjectType.valueOf(objectType);
		CfgFilterBasedQuery q = new CfgFilterBasedQuery(t);

		Collection allObjects = null;
		if (t == CfgObjectType.CFGPerson) {
			allObjects = getResults(q, CfgPerson.class, refresh);
		} else if (t == CfgObjectType.CFGPlace) {
			allObjects = getResults(q, CfgPlace.class, refresh);
		} else if (t == CfgObjectType.CFGDN) {
			allObjects = getResults(q, CfgDN.class, refresh);
		} else if (t == CfgObjectType.CFGAgentLogin) {
			allObjects = getResults(q, CfgAgentLogin.class, refresh);
		} else if (t == CfgObjectType.CFGAgentGroup) {
			allObjects = getResults(q, CfgAgentGroup.class, refresh);
		} else if (t == CfgObjectType.CFGApplication) {
			allObjects = getResults(q, CfgApplication.class, refresh);
		} else if (t == CfgObjectType.CFGSwitch) {
			allObjects = getResults(q, CfgSwitch.class, refresh);
		} else if (t == CfgObjectType.CFGFolder) {
			allObjects = getResults(q, CfgFolder.class, refresh);
		} else if (t == CfgObjectType.CFGDNGroup) {
			allObjects = getResults(q, CfgDNGroup.class, refresh);

		} else if (t == CfgObjectType.CFGPlaceGroup) {
			allObjects = getResults(q, CfgPlaceGroup.class, refresh);

		} else if (t == CfgObjectType.CFGScript) {
			allObjects = getResults(q, CfgScript.class, refresh);

		} else if (t == CfgObjectType.CFGTransaction) {
			allObjects = getResults(q, CfgTransaction.class, refresh);

		} else if (t == CfgObjectType.CFGEnumerator) {
			allObjects = getResults(q, CfgEnumerator.class, refresh);

		} else if (t == CfgObjectType.CFGEnumeratorValue) {
			allObjects = getResults(q, CfgEnumeratorValue.class, refresh);

		} else if (t == CfgObjectType.CFGGVPIVRProfile) {
			allObjects = getResults(q, CfgGVPIVRProfile.class, refresh);

		} else if (t == CfgObjectType.CFGAccessGroup) {
			allObjects = getResults(q, CfgAccessGroup.class, refresh);

		} else if (t == CfgObjectType.CFGActionCode) {
			allObjects = getResults(q, CfgActionCode.class, refresh);

		} else if (t == CfgObjectType.CFGAlarmCondition) {
			allObjects = getResults(q, CfgAlarmCondition.class, refresh);

		} else if (t == CfgObjectType.CFGApplication) {
			allObjects = getResults(q, CfgApplication.class, refresh);

		} else if (t == CfgObjectType.CFGHost) {
			allObjects = getResults(q, CfgHost.class, refresh);

		} else if (t == CfgObjectType.CFGTenant) {
			allObjects = getResults(q, CfgTenant.class, refresh);

		} else if (t == CfgObjectType.CFGIVRPort) {
			allObjects = getResults(q, CfgIVRPort.class, refresh);

		} else if (t == CfgObjectType.CFGIVR) {
			allObjects = getResults(q, CfgIVR.class, refresh);

		} else if (t == CfgObjectType.CFGObjectiveTable) {
			allObjects = getResults(q, CfgObjectiveTable.class, refresh);

		} else if (t == CfgObjectType.CFGService) {
			allObjects = getResults(q, CfgService.class, refresh);

		} else if (t == CfgObjectType.CFGSkill) {
			allObjects = getResults(q, CfgSkill.class, refresh);

		} else if (t == CfgObjectType.CFGStatDay) {
			allObjects = getResults(q, CfgStatDay.class, refresh);

		} else if (t == CfgObjectType.CFGStatTable) {
			allObjects = getResults(q, CfgStatTable.class, refresh);

		} else if (t == CfgObjectType.CFGTimeZone) {
			allObjects = getResults(q, CfgTimeZone.class, refresh);

		} else if (t == CfgObjectType.CFGTreatment) {
			allObjects = getResults(q, CfgTreatment.class, refresh);

		} else if (t == CfgObjectType.CFGVoicePrompt) {
			allObjects = getResults(q, CfgVoicePrompt.class, refresh);

		}

		if (max > 0) {
			ArrayList<CfgObject> ret = new ArrayList();
			int i = 1;
			for (Iterator it = allObjects.iterator(); it.hasNext();) {
				CfgObject obj = (CfgObject) it.next();
				logger.debug(obj);
				ret.add(obj);
				if (max > 0 && i++ >= max) {
					break;
				}
			}
			return ret;
		} else {
			return allObjects;
		}
	}

	public boolean deleteObject(Integer objTypeInt, int DBID) throws ProtocolException, Exception {
		RequestDeleteObject reqDelete = RequestDeleteObject.create(objTypeInt, DBID);

		Message resp = service.getProtocol().request(reqDelete);

		logger.error(resp);
		if (resp instanceof EventObjectDeleted) {
			parentForm
				.requestOutput("Deleted object DBID:\n" + ((EventObjectDeleted) resp)
					.getDbid() + " type: " + CfgObjectType.valueOf(((EventObjectDeleted) resp).getObjectType()));
			return true;
		} else if (resp instanceof EventError) {
			String err = "Error on object create: " + CfgUtilities
				.getErrorCode(((EventError) resp).getErrorCode()) + "\tDescription: " + ((EventError) resp)
					.getDescription();
			logger.error(err);
			throw new Exception(err);

		}
		return true;
	}
}
