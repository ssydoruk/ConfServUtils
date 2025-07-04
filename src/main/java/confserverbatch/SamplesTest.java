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

import com.genesyslab.platform.applicationblocks.com.ConfigException;
import com.genesyslab.platform.applicationblocks.com.IConfService;
import com.genesyslab.platform.commons.protocol.ProtocolException;
import java.util.ArrayList;
import java.util.HashMap;
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
public class SamplesTest {

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

//        String configServerHost="host.com";
        logger.info("starting");

        String configServerHost = "host1.com";
//        String configServerHost="host.com";
        int configServerPort = 2020;
        String configServerUser = "ssydoruk@gmail.com.admin";
        String configServerPass = "QwErAsDf123";

        String tempAppName = "AppName4Test"; // Uniq name for temp app to be created,
        // changed and deleted.
        String tempAgentName = "AgentName4Test"; // Uniq name for temp agent to be created,
        // changed and deleted.

        SamplesTest.logger.info("ComJavaQuickStart started execution.");

        String someAppName = "default";
//        if (someAppName == null || someAppName.equals("")) {
//            someAppName = "default";
//        }
//        configServerHost = properties.getString("ConfServerHost");
//        configServerPort = Integer.parseInt(properties.getString("ConfServerPort"));
//        configServerUser = properties.getString("ConfServerUser");
//        configServerPass = properties.getString("ConfServerPassword");

        IConfService service = InitializationSamples.initializeConfigService(
                tempAppName, configServerHost, configServerPort,
                configServerUser, configServerPass
        );

//        ReadConfigurationSamples.readAndPrintCfgApplication(service, someAppName);
//
//        ReadConfigurationSamples.readAllCfgApplicationsAsync(service);
//
//        // Create application test (remove if it is already created before):
//        DeleteConfObjectSamples.deleteApplication(service, tempAppName);
//        CfgApplication app = CreateApplicationSamples.createSrvApplicationExt(service, tempAppName);
//        // SAVE the application - the sample method does not save it:
//        app.save();
        // Some tests with CfgPerson:
        // Delete person if already exists and then recreate:
//        DeleteConfObjectSamples.deletePerson(service, tempAgentName);
//        CfgPerson agent = AgentInformationSamples.createPersonRecord(service, tempAgentName);
//        SamplesTest.logger.info("dbid: " + agent.getDBID());
//        agent.save();
//        SamplesTest.logger.info("dbid: " + agent.getDBID());
////        AgentInformationSamples.readPersonPermissions(service, agent);
////        AgentInformationSamples.modifyPersonPermissions(service, agent);
//        CfgAgentInfo ai = new CfgAgentInfo(service, null);
////        agent.setAgentInfo(ai);
//        agent.delete();
        ArrayList<SwitchObjectLocation> switches = new ArrayList<>();
        switches.add(new SwitchObjectLocation(service, "esv1_sipa1"));
        switches.add(new SwitchObjectLocation(service, "edn1_sipa1"));

        HashMap<SwitchObjectLocation, String> DNs = new HashMap<>();
        for (SwitchObjectLocation switche : switches) {
            DNs.put(switche, (String) null);

        }

        FullAgent ag = new FullAgent("Cyara_90000",
                "Cyara",
                "90000",
                "Cyara_90000",
                "");

        ag.setObjExistAction(ObjectExistAction.RECREATE);

        for (SwitchObjectLocation switche : switches) {
            ag.addLoginID(switche, "90000");

        }
        for (SwitchObjectLocation switchLookup : DNs.keySet()) {
            DNs.put(switchLookup, "90000");
        }

        ag.addPlace("cyara-90000", DNs);
        ag.setService(service);
        ag.createAll();

//        EventsSubscriptionSamples.testEvents(service, tempAppName);
//        DeleteConfObjectSamples.deleteApplication(service, tempAppName);
        // Closes protocol connection and release ConfService instance:
        InitializationSamples.uninitializeConfigService(service);

        SamplesTest.logger.info("ComJavaQuickStart finished execution.");
    }

}
