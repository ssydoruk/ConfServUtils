//===============================================================================
// Any authorized distribution of any copy of this code (including any related
// documentation) must reproduce the following restrictions, disclaimer and copyright
// notice:
// The Genesys name, trademarks and/or logo(s) of Genesys shall not be used to name
// (even as a part of another name), endorse and/or promote products derived from
// this code without prior written permission from Genesys Telecommunications
// Laboratories, Inc.
// The use, copy, and/or distribution of this code is subject to the terms of the Genesys
// Developer License Agreement.  This code shall not be used, copied, and/or
// distributed under any other license agreement.
// THIS CODE IS PROVIDED BY GENESYS TELECOMMUNICATIONS LABORATORIES, INC.
// ("GENESYS") "AS IS" WITHOUT ANY WARRANTY OF ANY KIND. GENESYS HEREBY
// DISCLAIMS ALL EXPRESS, IMPLIED, OR STATUTORY CONDITIONS, REPRESENTATIONS AND
// WARRANTIES WITH RESPECT TO THIS CODE (OR ANY PART THEREOF), INCLUDING, BUT
// NOT LIMITED TO, IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
// PARTICULAR PURPOSE OR NON-INFRINGEMENT. GENESYS AND ITS SUPPLIERS SHALL
// NOT BE LIABLE FOR ANY DAMAGE SUFFERED AS A RESULT OF USING THIS CODE. IN NO
// EVENT SHALL GENESYS AND ITS SUPPLIERS BE LIABLE FOR ANY DIRECT, INDIRECT,
// CONSEQUENTIAL, ECONOMIC, INCIDENTAL, OR SPECIAL DAMAGES (INCLUDING, BUT
// NOT LIMITED TO, ANY LOST REVENUES OR PROFITS).
// Copyright (c) 2006 - 2017 Genesys Telecommunications Laboratories, Inc. All rights reserved.
//===============================================================================
package confserverbatch;

import Utils.Pair;
import com.genesyslab.platform.applicationblocks.com.CfgObject;
import com.genesyslab.platform.applicationblocks.com.CfgQuery;
import com.genesyslab.platform.applicationblocks.com.ConfigException;
import com.genesyslab.platform.applicationblocks.com.IConfService;
import com.genesyslab.platform.applicationblocks.com.objects.CfgAgentInfo;
import com.genesyslab.platform.applicationblocks.com.objects.CfgAgentLogin;
import com.genesyslab.platform.applicationblocks.com.objects.CfgAgentLoginInfo;
import com.genesyslab.platform.applicationblocks.com.objects.CfgPerson;
import com.genesyslab.platform.applicationblocks.com.objects.CfgPlace;
import com.genesyslab.platform.applicationblocks.com.queries.CfgAgentLoginQuery;
import com.genesyslab.platform.applicationblocks.com.queries.CfgPersonQuery;
import com.genesyslab.platform.applicationblocks.com.queries.CfgPlaceQuery;
import com.genesyslab.platform.commons.collections.KeyValueCollection;
import com.genesyslab.platform.commons.protocol.Message;
import com.genesyslab.platform.commons.protocol.ProtocolException;
import com.genesyslab.platform.configuration.protocol.confserver.events.EventError;
import com.genesyslab.platform.configuration.protocol.confserver.events.EventObjectCreated;
import com.genesyslab.platform.configuration.protocol.confserver.events.EventObjectUpdated;
import com.genesyslab.platform.configuration.protocol.confserver.requests.objects.RequestCreateObject;
import com.genesyslab.platform.configuration.protocol.confserver.requests.objects.RequestUpdateObject;
import com.genesyslab.platform.configuration.protocol.metadata.CfgMetadata;
import com.genesyslab.platform.configuration.protocol.obj.ConfObject;
import com.genesyslab.platform.configuration.protocol.obj.ConfObjectDelta;
import com.genesyslab.platform.configuration.protocol.obj.ConfStructure;
import com.genesyslab.platform.configuration.protocol.obj.ConfStructureCollection;
import com.genesyslab.platform.configuration.protocol.types.CfgFlag;
import com.genesyslab.platform.configuration.protocol.types.CfgObjectType;
import com.genesyslab.platform.configuration.protocol.types.CfgStructureType;
import com.genesyslab.platform.configuration.protocol.utilities.CfgUtilities;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Small test application to execute some of described samples in test mode to
 * ensure that provided code is working.
 *
 * @author <a href="mailto:makagon@genesyslab.com">Petr Makagon</a>
 * @author <a href="mailto:vladb@genesyslab.com">Vladislav Baranovsky</a>
 * @author <a href="mailto:afilatov@genesyslab.com">Alexander Filatov</a>
 * @author <a href="mailto:abrazhny@genesyslab.com">Anton Brazhnyk</a>
 * @author <a href="mailto:svolokh@genesyslab.com">Sergii Volokh</a>
 */
public class RenameDomain {

    /**
     * Small function to be executed to check that main Configuration Service
     * operations are working. Some kind of test. Can be used for automatic
     * tests with dependance on Genesys environment.
     *
     * @param args programm commandline arguments
     * @throws ConfigException in case of any configuration service exception
     * @throws ProtocolException in case of any configuration protocol exception
     * @throws InterruptedException if process was interrupted
     */
    public static final Logger logger = LogManager.getLogger();


    public static void main(final String[] args)
            throws ConfigException, InterruptedException, ProtocolException, CloneNotSupportedException {
//        ResourceBundle properties = ResourceBundle.getBundle("quickstart");

//        String configServerHost="ESV1-C-PPE-46.ivr.airbnb.biz";
        RenameDomain app = new RenameDomain();
        app.run();

        RenameDomain.logger.info("ComJavaQuickStart finished execution.");
    }
    ConfigServerManager configServerManager;

    boolean testRun = true;


    private final Pattern ptSearchDomain = Pattern.compile("(.*)ext.airbnb(.*)");
    private final String sReplaceString = "$1nobnb.biz$2";
    public RenameDomain() {
        
        configServerManager = new ConfigServerManager();
        
    }

    private void run() throws ProtocolException, IllegalStateException, InterruptedException, ConfigException {
        logger.info("starting in " + (testRun ? "test" : "production") + " mode");

        configServerManager.connect("default", "esv1-c-mfwk-03t.airbnb.biz", 2020, "stepan.sydoruk@ext.airbnb.com.admin", "QwErAsDf123");
        if (configServerManager.isConnected()) {
            doUpdate();
            configServerManager.disconnect();
        }

        logger.info("done");
    }

    private void doUpdate() throws ConfigException, InterruptedException, InterruptedException, InterruptedException {
        CfgPersonQuery query = new CfgPersonQuery();
        query.setIsAgent(CfgFlag.CFGTrue.ordinal());
        logger.info("********* updating agents");
//        query.setEmployeeId("stepan_sydoruk");

//                            CfgSwitchType selectedObjSubType = (CfgSwitchType) pn.getSelectedObjSubType();
//                            if (selectedObjSubType != null) {
//                                query.(selectedObjSubType);
//                            }
//<editor-fold defaultstate="collapsed" desc="CfgPerson">
        if (1 == 1) {
            processConfigObjects(
                    query,
                    CfgPerson.class,
                    new IObjectUpdateProc() {
                @Override
                public void updateProc(CfgObject obj) {
                    CfgPerson p = (CfgPerson) obj;
                    logger.debug("Checking  " + p.getObjectType() + ": " + p.getUserName());

                    CfgAgentInfo agentInfo = p.getAgentInfo();

//                    logger.info("agentInfo: " + agentInfo.getAgentLogins());
                    Collection<CfgAgentLoginInfo> agentLogins = agentInfo.getAgentLogins();
                    ArrayList<Pair<Integer, Integer>> newDBIDs = new ArrayList<>(agentLogins.size());
                    for (CfgAgentLoginInfo agentLogin : agentLogins) {
//                        logger.info("login: " + agentLogin);
                        Integer newID = processLogin(agentLogin, p);
                        if (newID != null) {
                            newDBIDs.add(new Pair(newID, agentLogin.getWrapupTime()));
                        }
                    }

                    Matcher mEmailAddress = (StringUtils.isBlank(p.getEmailAddress())) ? null : ptSearchDomain.matcher(p.getEmailAddress());
                    Matcher mUserName = (StringUtils.isBlank(p.getUserName())) ? null : ptSearchDomain.matcher(p.getUserName());

                    if ((mEmailAddress != null && mEmailAddress.find()) || (mUserName != null && mUserName.find()) || !newDBIDs.isEmpty()) {
                        logger.info("Updating person: " + p.getEmployeeID() + " email: " + p.getEmailAddress() + " " + p.getUserName());
                        if (testRun) {
                            infoTestRun("updated email: " + (mEmailAddress != null ? mEmailAddress.replaceAll(sReplaceString) : "<empty>") + " updated username: " + (mUserName != null ? mUserName.replaceAll(sReplaceString) : "<empty>")
                                    + " updated loginDBIDs:" + (!newDBIDs.isEmpty() ? newDBIDs.toString() : "none"));

                        } else {
                            IConfService service = configServerManager.getService();
                            CfgMetadata metaData = service.getMetaData();
                            ConfObjectDelta d = new ConfObjectDelta(metaData, CfgObjectType.CFGPerson);

                            ConfObject obj1 = (ConfObject) d.getOrCreatePropertyValue("deltaPerson");

                            obj1.setPropertyValue("DBID", p.getDBID());              // - required
                            obj1.setPropertyValue("emailAddress", (mEmailAddress != null ? mEmailAddress.replaceAll(sReplaceString) : "<empty>"));      // - to set new host name (if needed)
                            obj1.setPropertyValue("userName", (mUserName != null ? mUserName.replaceAll(sReplaceString) : "<empty>"));      // - to set new host name (if needed)
                            ConfStructure obj2 = (ConfStructure) obj1.getOrCreatePropertyValue("agentInfo");
                            for (Pair<Integer, Integer> newDBID : newDBIDs) {
                                ConfStructureCollection obj3 = (ConfStructureCollection) obj2.getOrCreatePropertyValue("agentLogins");
                                ConfStructure cs = new ConfStructure(metaData, CfgStructureType.CFGAgentLoginInfo);
                                cs.setPropertyValue("agentLoginDBID", newDBID.getKey());
                                cs.setPropertyValue("wrapupTime", newDBID.getValue());
                                obj3.add(cs);

                            }

                            RequestUpdateObject reqUpdate = RequestUpdateObject.create();
                            logger.info("++" + d.toString());
                            reqUpdate.setObjectDelta(d);

                            execRequest(reqUpdate, service);

                            logger.info("----- updating loginids -----");
                        }
                    }
                }

            });
        }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="CfgPlace">
        CfgPlaceQuery query2 = new CfgPlaceQuery();
        logger.info("********* updating places");

//        query2.setName("stepan.sydoruk@ext.airbnb.com");
        if (1 == 1) {
            processConfigObjects(
                    query2,
                    CfgPlace.class,
                    new IObjectUpdateProc() {
                @Override
                public void updateProc(CfgObject obj) {
                    CfgPlace p = (CfgPlace) obj;
                    logger.debug("Checking  " + p.getObjectType() + ": " + p.getName());

                    Matcher mPlaceName = (StringUtils.isBlank(p.getName())) ? null : ptSearchDomain.matcher(p.getName());

                    if (mPlaceName != null && mPlaceName.find()) {
                        logger.info("Updating place: " + p.getName());

                        if (testRun) {
                            infoTestRun("updated place name: " + mPlaceName.replaceAll(sReplaceString));
                        } else {
                            IConfService service = configServerManager.getService();
                            CfgMetadata metaData = service.getMetaData();
                            CfgPlace np = new CfgPlace(configServerManager.getService());
                            ConfObjectDelta d = new ConfObjectDelta(metaData, CfgObjectType.CFGPlace);

                            ConfObject obj1 = (ConfObject) d.getOrCreatePropertyValue("deltaPlace");
                            obj1.setPropertyValue("DBID", p.getDBID());              // - required
                            obj1.setPropertyValue("name", mPlaceName.replaceAll(sReplaceString));      // - to set new host name (if needed)

                            RequestUpdateObject reqUpdate = RequestUpdateObject.create();
                            reqUpdate.setObjectDelta(d);

                            try {
                                Message resp = service.getProtocol().request(reqUpdate);

                                if (resp instanceof EventObjectUpdated) {
                                    logger.info("!!Object updated");
                                } else if (resp instanceof EventError) {
                                    logger.error("Error on object update: "
                                            + CfgUtilities.getErrorCode(((EventError) resp).getErrorCode())
                                            + "\tDescription: " + ((EventError) resp).getDescription());
                                }

                            } catch (ProtocolException ex) {
                                java.util.logging.Logger.getLogger(RenameDomain.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (IllegalStateException ex) {
                                java.util.logging.Logger.getLogger(RenameDomain.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }

                }
            });
        }
//</editor-fold>
    }

    private <T extends CfgObject> void processConfigObjects(
            CfgQuery q,
            Class< T> cls,
            IObjectUpdateProc proc
    ) throws ConfigException, InterruptedException {

        Collection<T> cfgObjs = configServerManager.getResults(q, cls);

        if (cfgObjs == null || cfgObjs.isEmpty()) {
            logger.debug("no objects found\n", false);
        } else {
            logger.debug("retrieved " + cfgObjs.size() + " total objects type " + cls.getSimpleName());

            int cnt = 0;
            for (CfgObject cfgObj : cfgObjs) {
                proc.updateProc(cfgObj);
                cnt++;

            }
            if (cnt > 0) {
                logger.info("Search done, located " + cnt + " objects type " + cls.getSimpleName());
            }
        }
    }

    private Message execRequest(Message reqUpdate, IConfService service) {
        try {
            Message resp = service.getProtocol().request(reqUpdate);

            if (resp instanceof EventObjectUpdated) {
                logger.info("!!Object updated");
            } else if (resp instanceof EventError) {
                logger.error("Error on object update: "
                        + CfgUtilities.getErrorCode(((EventError) resp).getErrorCode())
                        + "\tDescription: " + ((EventError) resp).getDescription());
            } else if (resp instanceof EventObjectCreated) {
                EventObjectCreated oc = (EventObjectCreated) resp;
                ConfObject object = oc.getObject();
                logger.info("new object DBID: " + object.getObjectDbid());
                return resp;
            }

        } catch (ProtocolException ex) {
            java.util.logging.Logger.getLogger(RenameDomain.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalStateException ex) {
            java.util.logging.Logger.getLogger(RenameDomain.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private Integer processLogin(CfgAgentLoginInfo agentLogin, CfgPerson p) {

        CfgAgentLoginQuery query1 = new CfgAgentLoginQuery();
        query1.setDbid(agentLogin.getAgentLoginDBID());

        try {

            Collection<CfgAgentLogin> cfgObjs = configServerManager.getResults(query1, CfgAgentLogin.class);

            if (cfgObjs == null || cfgObjs.isEmpty()) {
                logger.debug("no agent login found\n", false);
            } else {

                CfgAgentLogin al = (CfgAgentLogin) cfgObjs.toArray()[0];
                logger.debug("retrieved LoginID at " + al.getObjectPath() + " obj: " + al);

                Matcher mLoginCode = (StringUtils.isBlank(al.getLoginCode())) ? null : ptSearchDomain.matcher(al.getLoginCode());

                if (mLoginCode != null && mLoginCode.find()) {
                    String newLoginCode = mLoginCode.replaceAll(sReplaceString);
                    if (testRun) {
                        infoTestRun("new LoginCode:" + newLoginCode);
                        return al.getDBID();
                    } else {
                        IConfService service = configServerManager.getService();
                        CfgMetadata metaData = service.getMetaData();

                        CfgAgentLogin newAl = new CfgAgentLogin(service);
                        newAl.setSwitchDBID(al.getSwitchDBID());
                        newAl.setFolderId(al.getFolderId());
                        newAl.setLoginCode(newLoginCode);
                        newAl.setState(al.getState());
                        newAl.setTenantDBID(al.getTenantDBID());
                        newAl.setSwitchSpecificType(al.getSwitchSpecificType());
                        Object o;

                        if ((o = al.getUserProperties()) != null) {
                            newAl.setUserProperties((KeyValueCollection) o);
                        }
                        if ((o = al.getOverride()) != null) {
                            newAl.setOverride((String) o);
                        }
                        if ((o = al.getPassword()) != null) {
                            newAl.setPassword((String) o);
                        }

                        RequestCreateObject cr = RequestCreateObject.create();
                        cr.setObject((ConfObject) newAl.getRawObjectData());
                        Message resp = execRequest(cr, service);

                        if (resp != null && resp instanceof EventObjectCreated) {
                            return ((EventObjectCreated) resp).getObject().getObjectDbid();
                        }
                    }
                }
            }

        } catch (ConfigException ex) {
            java.util.logging.Logger.getLogger(RenameDomain.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            java.util.logging.Logger.getLogger(RenameDomain.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private void infoTestRun(String string) {
        logger.info("!!! test run: " + string);
    }

}
