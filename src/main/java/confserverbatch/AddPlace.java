/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package confserverbatch;

import com.ssydoruk.confservutils.ConfigServerManager;
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
import com.genesyslab.platform.applicationblocks.com.queries.CfgDNQuery;
import com.genesyslab.platform.applicationblocks.com.queries.CfgPlaceQuery;
import com.genesyslab.platform.configuration.protocol.obj.ConfObject;
import com.genesyslab.platform.configuration.protocol.types.CfgDNType;
import com.genesyslab.platform.configuration.protocol.types.CfgErrorType;
import com.genesyslab.platform.configuration.protocol.types.CfgObjectType;
import com.genesyslab.platform.configuration.protocol.types.CfgRouteType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author stepan_sydoruk
 */
public class AddPlace {

    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger();

    private ObjectExistAction objExistAction;
    private IConfService service;
    String pl = null;
    HashMap<SwitchObjectLocation, String> theDNs = new HashMap<>();

    public AddPlace(IConfService service, String place, HashMap<SwitchObjectLocation, String> DNs, ObjectExistAction objectExistAction) {
        this.service = service;
        setObjExistAction(objectExistAction);
        pl = place;
        for (Map.Entry<SwitchObjectLocation, String> entry : DNs.entrySet()) {
            theDNs.put(entry.getKey(), entry.getValue());

        }
    }

    public AddPlace(ConfigServerManager configServerManager, String string, HashMap<SwitchObjectLocation, String> DNs, ObjectExistAction objectExistAction) {
        this(configServerManager.getService(), string, DNs, objectExistAction);
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
                    if (Objects.equals(dn.getDBID(), cfgDNs.get(i).getDBID())) {
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
            Logger.getLogger(AddPlace.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void createAll() throws ConfigException, CloneNotSupportedException {
        ArrayList<CfgDN> cfgDNs = new ArrayList<>();
        for (Map.Entry<SwitchObjectLocation, String> entry : theDNs.entrySet()) {
            cfgDNs.add(createDN(entry.getKey(),
                    entry.getValue()));
        }

        CfgPlace cfgPlace = createPlace(pl, cfgDNs);

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

        cfgPlace.setDNs(cfgDNs);
        cfgPlace.setName(pl);
        cfgPlace.setTenantDBID(WellKnownDBIDs.EnvironmentDBID);
        logger.info("Creating Place [" + cfgPlace + "]");

        cfgPlace.save();
        return cfgPlace;
    }

    private CfgPlace createPlace(String pl, ArrayList<CfgDN> cfgDNs) throws ConfigException {
        try {
            return doCreatePlace(service, pl, cfgDNs);
        } catch (ConfigException ex) {
//            switch(objExistaction)
            logger.info("DN exists; " + objExistAction);
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
    private void finalizeAgent(CfgPerson ag, CfgPlace pl, ArrayList<CfgAgentLogin> agentLogins) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
