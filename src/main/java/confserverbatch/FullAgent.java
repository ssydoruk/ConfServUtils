/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package confserverbatch;

import com.genesyslab.platform.applicationblocks.com.ConfigException;
import com.genesyslab.platform.applicationblocks.com.ConfigServerException;
import com.genesyslab.platform.applicationblocks.com.IConfService;
import com.genesyslab.platform.applicationblocks.com.WellKnownDBIDs;
import com.genesyslab.platform.applicationblocks.com.objects.CfgAgentInfo;
import com.genesyslab.platform.applicationblocks.com.objects.CfgAgentLogin;
import com.genesyslab.platform.applicationblocks.com.objects.CfgAgentLoginInfo;
import com.genesyslab.platform.applicationblocks.com.objects.CfgDN;
import com.genesyslab.platform.applicationblocks.com.objects.CfgDeltaPerson;
import com.genesyslab.platform.applicationblocks.com.objects.CfgPerson;
import com.genesyslab.platform.applicationblocks.com.objects.CfgPlace;
import com.genesyslab.platform.applicationblocks.com.objects.CfgSwitch;
import com.genesyslab.platform.applicationblocks.com.queries.CfgAgentLoginQuery;
import com.genesyslab.platform.applicationblocks.com.queries.CfgDNQuery;
import com.genesyslab.platform.applicationblocks.com.queries.CfgPersonQuery;
import com.genesyslab.platform.applicationblocks.com.queries.CfgPlaceQuery;
import com.genesyslab.platform.configuration.protocol.obj.ConfObject;
import com.genesyslab.platform.configuration.protocol.types.CfgDNType;
import com.genesyslab.platform.configuration.protocol.types.CfgErrorType;
import com.genesyslab.platform.configuration.protocol.types.CfgFlag;
import com.genesyslab.platform.configuration.protocol.types.CfgObjectType;
import com.genesyslab.platform.configuration.protocol.types.CfgRouteType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author stepan_sydoruk
 */
public class FullAgent {

    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger();


    private ObjectExistAction objExistAction;

 

    private String userName;
    private String firstName;
    private String lastName;
    private String employeeID;
    private String externalUserID;
    private IConfService service;
    HashMap<SwitchObjectLocation, String> loginIDs = new HashMap();
    String pl = null;
    HashMap<SwitchObjectLocation, String> theDNs = new HashMap<>();

    public FullAgent(String string) {
    }

    FullAgent(String userName, String firstName,
            String lastName, String employeeID, String externalUserID) {
        this.userName = userName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.employeeID = employeeID;
        this.externalUserID = externalUserID;

    }
    void setObjExistAction(ObjectExistAction objectExistAction) {
        objExistAction = objectExistAction;
    }
    private CfgDN reCreateDN(IConfService service, CfgDN findDN) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    private boolean placeDNsEqual(Collection<CfgDN> placeDNs, ArrayList<CfgDN> cfgDNs) {
        if (placeDNs.size() == cfgDNs.size() && placeDNs.size() > 0) {
            for (CfgDN dn : placeDNs) {
                int i = 0;
                for (; i < cfgDNs.size(); i++) {
                    if (dn.getDBID() == cfgDNs.get(i).getDBID()) {
                        break;
                    }
                }
                if (i == cfgDNs.size()) {
                    return false;
                }
            }
        }
        return true;
    }
    private void updateLoginIDs(IConfService service, CfgPerson newAgent, ArrayList<CfgAgentLogin> loginIDs1, CfgPlace cfgPlace) {
        try {
            CfgAgentInfo cfgAI = new CfgAgentInfo(service, newAgent);
            cfgAI.setPlace(cfgPlace);
            ArrayList<CfgAgentLoginInfo> alis = new ArrayList<>(loginIDs1.size());
            for (CfgAgentLogin agLogin : loginIDs1) {
                CfgAgentLoginInfo ali = new CfgAgentLoginInfo(service, null);
                ali.setAgentLogin(agLogin);
                ali.setWrapupTime(3000);
                alis.add(ali);

            }
            cfgAI.setAgentLogins(alis);

            ConfObject inDelta = new ConfObject(service.getMetaData(), CfgObjectType.CFGPerson);
            inDelta.setPropertyValue("DBID", newAgent.getDBID());

            CfgDeltaPerson dp = new CfgDeltaPerson(service);
            dp.setProperty("deltaAgentInfo", inDelta);
            dp.setProperty("DBID", newAgent.getDBID());

            logger.info(dp.toXml());

            newAgent.update(dp);

//            RequestUpdateObject reqUpdate = RequestUpdateObject.create();
//            reqUpdate.setObjectDelta(createDelta);
//
//            Message resp = service.getProtocol().request(reqUpdate);
//            if (resp == null) {
//                // timeout
//            } else if (resp instanceof EventObjectUpdated) {
//                // the object has been updated
//            } else if (resp instanceof EventError) {
//                // fail((EventError) resp);
//            } else {
//                // unexpected server response
//            }
        } catch (IllegalStateException ex) {
            Logger.getLogger(FullAgent.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    void createAll() throws ConfigException, CloneNotSupportedException {
        ArrayList<CfgDN> cfgDNs = new ArrayList<>();
        for (Map.Entry<SwitchObjectLocation, String> entry : theDNs.entrySet()) {
            cfgDNs.add(createDN(entry.getKey(),
                    entry.getValue()));
        }

        ArrayList<CfgAgentLogin> agentLogins = new ArrayList<>();
        for (Map.Entry<SwitchObjectLocation, String> entry : loginIDs.entrySet()) {
            agentLogins.add(createAgentLogin(entry.getKey(),
                    entry.getValue()));
        }
        CfgPlace cfgPlace = createPlace(pl, cfgDNs);
        CfgPerson ag = createPerson(userName, employeeID, externalUserID, firstName, lastName,
                agentLogins, cfgPlace);

    }


    void addLoginID(SwitchObjectLocation switche, String string) {
        loginIDs.put(switche, string);
    }


    void addPlace(String place, HashMap<SwitchObjectLocation, String> DNs) {
        pl = place;
        for (Map.Entry<SwitchObjectLocation, String> entry : DNs.entrySet()) {
            theDNs.put(entry.getKey(), entry.getValue());

        }
    }

    void setService(IConfService service) {
        this.service = service;
    }

    //<editor-fold defaultstate="collapsed" desc="cfgDN utility">
    private CfgDN createDN(SwitchObjectLocation key, String value) throws ConfigException {
        String name = key.getSw().getName() + "_" + value;
        try {
            return doCreateDN(service, value, name, CfgDNType.CFGExtension, key.getSw());
        } catch (ConfigException ex) {
//            switch(objExistaction)
            SamplesTest.logger.info("DN exists; " + objExistAction);
            if (ex instanceof ConfigServerException
                    && ((ConfigServerException) ex).getErrorType() == CfgErrorType.CFGUniquenessViolation) {
                switch (objExistAction) {
                    case RECREATE:
                        CfgDN cfgDN = findDN(service, name, key.getSw(), true);
                        cfgDN.delete();
                        return doCreateDN(service, cfgDN.getNumber(), cfgDN.getName(), cfgDN.getType(),
                                cfgDN.getSwitch());

                    case REUSE:
                        return findDN(service, name, key.getSw(), true);

                    default:
                        break;//fails
                }
            }
            throw ex;
        }
    }

    private CfgDN findDN(
            final IConfService service,
            final String dn, final CfgSwitch sw, boolean mastExist
    ) throws ConfigException {
        CfgDNQuery dnQuery = new CfgDNQuery();

        dnQuery.setName(dn);
        dnQuery.setSwitchDbid(sw.getDBID());

        SamplesTest.logger.info("searching " + "DN [" + dn + "] switch[" + sw.getName() + "]");
        CfgDN cfgDn = service.retrieveObject(CfgDN.class, dnQuery);
        if (mastExist && cfgDn == null) {
            throw new ConfigException("DN [" + dn + "] switch[" + sw.getName() + "]");
        }
        SamplesTest.logger.info("found " + "DN [" + dn + "] switch[" + sw.getName() + "] DBID:" + cfgDn.getDBID());
        return cfgDn;

    }

    private CfgDN deleteDN(
            final IConfService service,
            final String dn, final CfgSwitch sw)
            throws ConfigException {

        CfgDN cfgDN = findDN(service, dn, sw, true);
        cfgDN.delete();
        return cfgDN;

    }

    private CfgDN doCreateDN(IConfService service,
            String Number,
            String name,
            CfgDNType type,
            CfgSwitch sw) throws ConfigException {
        CfgDN dn = new CfgDN(service);

        SamplesTest.logger.info("Creating DN [" + name + "] switch [" + sw.getName() + "]");
        dn.setName(name);
        dn.setSwitch(sw);
        dn.setType(type);
        dn.setNumber(Number);
        dn.setRouteType(CfgRouteType.CFGDefault);
        dn.save();
        return dn;
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="cfgLoginID utility">
    private CfgAgentLogin findAgentLogin(
            final IConfService service,
            final String loginCode, final CfgSwitch sw, boolean mastExist
    ) throws ConfigException {
        CfgAgentLoginQuery agentLoginQuery = new CfgAgentLoginQuery();

        agentLoginQuery.setLoginCode(loginCode);
        agentLoginQuery.setSwitchDbid(sw.getDBID());

        SamplesTest.logger.info("searching " + "AgentLogin [" + loginCode + "] switch[" + sw.getName() + "]");
        CfgAgentLogin cfgLoginCode = service.retrieveObject(CfgAgentLogin.class, agentLoginQuery);
        if (mastExist && cfgLoginCode == null) {
            throw new ConfigException("AgentLogin [" + loginCode + "] switch[" + sw.getName() + "]");
        }
        return cfgLoginCode;

    }

    private CfgAgentLogin deleteAgentLogin(
            final IConfService service,
            final String loginCode, final CfgSwitch sw)
            throws ConfigException {

        CfgAgentLogin cfgAgentLogin = findAgentLogin(service, loginCode, sw, true);
        cfgAgentLogin.delete();
        return cfgAgentLogin;

    }

    private CfgAgentLogin doCreateAgentLogin(IConfService service,
            String loginCode,
            CfgSwitch sw) throws ConfigException {
        CfgAgentLogin agentLogin = new CfgAgentLogin(service);

        SamplesTest.logger.info("Creating AgentLogin [" + loginCode + "] switch [" + sw.getName() + "]");
        agentLogin.setLoginCode(loginCode);
        agentLogin.setSwitch(sw);
        agentLogin.setSwitchSpecificType(1);
        agentLogin.save();
        return agentLogin;
    }

    private CfgAgentLogin createAgentLogin(SwitchObjectLocation key, String value) throws ConfigException {
        try {
            return doCreateAgentLogin(service, value, key.getSw());
        } catch (ConfigException ex) {
//            switch(objExistaction)
            SamplesTest.logger.info("DN exists; " + objExistAction);
            if (ex instanceof ConfigServerException
                    && ((ConfigServerException) ex).getErrorType() == CfgErrorType.CFGUniquenessViolation) {
                switch (objExistAction) {
                    case RECREATE:
                        CfgAgentLogin cfgAgentLogin = findAgentLogin(service, value, key.getSw(), true);
                        cfgAgentLogin.delete();
                        return doCreateAgentLogin(service, value,
                                cfgAgentLogin.getSwitch());

                    case REUSE:
                        return findAgentLogin(service, value, key.getSw(), true);

                    default:
                        break;//fails
                }
            }
            throw ex;
        }

    }

//</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="cfgPlace utility">
    private CfgPlace findPlace(
            final IConfService service,
            final String placeName, boolean mastExist
    ) throws ConfigException {
        CfgPlaceQuery cfgPlaceQuery = new CfgPlaceQuery();

        cfgPlaceQuery.setName(placeName);
        cfgPlaceQuery.setTenantDbid(WellKnownDBIDs.EnvironmentDBID);

        SamplesTest.logger.info("searching " + "Place [" + placeName + "]");
        CfgPlace cfgPlace = service.retrieveObject(CfgPlace.class, cfgPlaceQuery);
        if (mastExist && cfgPlace == null) {
            throw new ConfigException("Place [" + placeName + "]");
        }
        SamplesTest.logger.info("Found " + "Place [" + placeName + "] DBID=" + cfgPlace.getDBID());

        return cfgPlace;

    }

    private CfgPlace deletePlace(
            final IConfService service,
            final String placeName)
            throws ConfigException {

        CfgPlace cfgPlace = findPlace(service, placeName, true);
        cfgPlace.delete();
        return cfgPlace;

    }

    private CfgPlace doCreatePlace(IConfService service,
            String pl, ArrayList<CfgDN> cfgDNs) throws ConfigException {
        CfgPlace cfgPlace = new CfgPlace(service);

        SamplesTest.logger.info("Creating Place [" + pl + "] DNs [" + cfgDNs + "]");

        cfgPlace.setDNs(cfgDNs);
        cfgPlace.setName(pl);
        cfgPlace.setTenantDBID(WellKnownDBIDs.EnvironmentDBID);
        return cfgPlace;
    }

    private CfgPlace createPlace(String pl, ArrayList<CfgDN> cfgDNs) throws ConfigException {
        try {
            return doCreatePlace(service, pl, cfgDNs);
        } catch (ConfigException ex) {
//            switch(objExistaction)
            SamplesTest.logger.info("DN exists; " + objExistAction);
            if (ex instanceof ConfigServerException
                    && ((ConfigServerException) ex).getErrorType() == CfgErrorType.CFGUniquenessViolation) {
                switch (objExistAction) {
                    case RECREATE:
                        CfgPlace cfgPlace = findPlace(service, pl, true);
                        cfgPlace.delete();
                        return doCreatePlace(service, pl, cfgDNs);

                    case REUSE:
                        CfgPlace ret = findPlace(service, pl, true);
                        if (ret != null) {
                            if (!placeDNsEqual(ret.getDNs(), cfgDNs)) {
                                SamplesTest.logger.error("Place found but DNs are different. Cannot reuse");
                                break; //this will ensure exception thrown

                            }

                        }
                        return ret;

                    default:
                        break;//fails
                }
            }
            throw ex;
        }

    }

//</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="cfgPerson utility">
    private CfgPerson findPerson(
            final IConfService service,
            final String personName, boolean mastExist
    ) throws ConfigException {
        CfgPersonQuery cfgPersonQuery = new CfgPersonQuery();

        cfgPersonQuery.setUserName(personName);
        cfgPersonQuery.setTenantDbid(WellKnownDBIDs.EnvironmentDBID);

        SamplesTest.logger.info("searching " + "person [" + personName + "]");
        CfgPerson cfgPerson = service.retrieveObject(CfgPerson.class, cfgPersonQuery);
        if (mastExist && cfgPerson == null) {
            throw new ConfigException("Person [" + personName + "]");
        }
        SamplesTest.logger.info("Found " + "Person [" + personName + "] DBID=" + cfgPerson.getDBID());

        return cfgPerson;

    }

    private CfgPerson deletePerson(
            final IConfService service,
            final String placeName)
            throws ConfigException {

        CfgPerson cfgPerson = findPerson(service, placeName, true);
        cfgPerson.delete();
        return cfgPerson;

    }

    private CfgPerson doCreatePerson(IConfService service, String userName1,
            String employeeID, String extUserID, String firstName1, String lastName1,
            ArrayList<CfgAgentLogin> loginIDs1, CfgPlace cfgPlace) throws ConfigException, CloneNotSupportedException {

        SamplesTest.logger.info("Creating Person [" + employeeID + "] loginIDs [" + loginIDs1 + "]");
        CfgPerson newAgent = new CfgPerson(service);

        newAgent.setUserName(userName1);

        newAgent.setFirstName(firstName1);
        newAgent.setLastName(lastName1);
        newAgent.setEmployeeID(employeeID);
        newAgent.setExternalID(extUserID);

        newAgent.setIsAgent(CfgFlag.CFGTrue);

        newAgent.setTenantDBID(WellKnownDBIDs.EnvironmentDBID);

        CfgAgentInfo cfgAI = new CfgAgentInfo(service, newAgent);
        cfgAI.setPlace(cfgPlace);
        newAgent.setAgentInfo(cfgAI);
        newAgent.save();

        return newAgent;
    }

    private CfgPerson createPerson(String userName, String employeeID,
            String extUserID, String firstName1, String lastName1,
            ArrayList<CfgAgentLogin> loginIDs1, CfgPlace cfgPlace) throws ConfigException, CloneNotSupportedException {
        try {
            return doCreatePerson(service, userName, employeeID, extUserID,
                    firstName1, lastName1,
                    loginIDs1, cfgPlace);
        } catch (ConfigException ex) {
//            switch(objExistaction)
            SamplesTest.logger.info("DN exists; " + objExistAction);
            if (ex instanceof ConfigServerException
                    && ((ConfigServerException) ex).getErrorType() == CfgErrorType.CFGUniquenessViolation) {
                switch (objExistAction) {
                    case RECREATE:
                        CfgPerson cfgPerson = findPerson(service, userName, true);
                        cfgPerson.delete();
                        return doCreatePerson(service, userName, employeeID, extUserID,
                                firstName1, lastName1,
                                loginIDs1, cfgPlace);

                    case REUSE:
                        CfgPerson ret = findPerson(service, userName, true);
                        updateLoginIDs(service, ret, loginIDs1, cfgPlace);

//                        if (ret != null) {
//                            CfgPlace pl1 = ret.getAgentInfo().getPlace();
//                            if ((pl1 == null && cfgPlace == null)
//                                    || (placeDNsEqual(pl1.getDNs(), (ArrayList<CfgDN>) cfgPlace.getDNs()))) {
//
//                            }
//                            if (ret == null) {
//                                SamplesTest.logger.error("Person found but DNs are different. Cannot reuse");
//                                break; //this will ensure exception thrown
//                            }
//                        }
                        return ret;

                    default:
                        break;//fails
                }
            }
            throw ex;
        }

    }

//</editor-fold>
    private void finalizeAgent(CfgPerson ag, CfgPlace pl, ArrayList<CfgAgentLogin> agentLogins) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
