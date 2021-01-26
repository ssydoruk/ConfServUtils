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

import com.genesyslab.platform.applicationblocks.com.*;
import com.genesyslab.platform.commons.protocol.*;
import java.util.*;
import org.apache.logging.log4j.*;


public class CreatePlaces {

    public static final Logger logger = LogManager.getLogger();

    public static void main(final String[] args)
            throws ConfigException, InterruptedException, ProtocolException, CloneNotSupportedException {
//        ResourceBundle properties = ResourceBundle.getBundle("quickstart");

//        String configServerHost="ESV1-C-PPE-46.ivr.airbnb.biz";
        logger.info("starting");

        String configServerHost = "esv1-c-mfwk-03t.airbnb.biz";
//        String configServerHost="esv1-c-ppe-46.ivr.airbnb.biz";
        int configServerPort = 2020;
        String configServerUser = "stepan.sydoruk@ext.airbnb.com.admin";
        String configServerPass = "QwErAsDf123";

        String tempAppName = "AppName4Test"; // Uniq name for temp app to be created,
        // changed and deleted.
        String tempAgentName = "AgentName4Test"; // Uniq name for temp agent to be created,
        // changed and deleted.

        CreatePlaces.logger.info("ComJavaQuickStart started execution.");

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

        ArrayList<SwitchObjectLocation> switches = new ArrayList<>();
        switches.add(new SwitchObjectLocation(service, "esv1_sipa1"));
//        switches.add(new SwitchLookup(service, "edn1_sipa1"));

        HashMap<SwitchObjectLocation, String> DNs = new HashMap<>();
        for (SwitchObjectLocation switche : switches) {
            DNs.put(switche, (String) null);

        }

        for (SwitchObjectLocation switchLookup : DNs.keySet()) {
            DNs.put(switchLookup, "90000");
        }
        AddPlace pl = new AddPlace(service, "cyara-90000", DNs, ObjectExistAction.RECREATE);

        pl.createAll();

//        EventsSubscriptionSamples.testEvents(service, tempAppName);
//        DeleteConfObjectSamples.deleteApplication(service, tempAppName);
        // Closes protocol connection and release ConfService instance:
        InitializationSamples.uninitializeConfigService(service);

        CreatePlaces.logger.info("ComJavaQuickStart finished execution.");
    }

}
