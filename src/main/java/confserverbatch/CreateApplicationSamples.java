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
import com.genesyslab.platform.applicationblocks.com.objects.CfgAppPrototype;
import com.genesyslab.platform.applicationblocks.com.objects.CfgApplication;
import com.genesyslab.platform.applicationblocks.com.objects.CfgConnInfo;
import com.genesyslab.platform.applicationblocks.com.objects.CfgFolder;
import com.genesyslab.platform.applicationblocks.com.objects.CfgHost;
import com.genesyslab.platform.applicationblocks.com.objects.CfgServer;
import com.genesyslab.platform.applicationblocks.com.objects.CfgTenant;
import com.genesyslab.platform.applicationblocks.com.queries.CfgAppPrototypeQuery;
import com.genesyslab.platform.applicationblocks.com.queries.CfgApplicationQuery;
import com.genesyslab.platform.applicationblocks.com.queries.CfgFolderQuery;
import com.genesyslab.platform.applicationblocks.com.queries.CfgHostQuery;
import com.genesyslab.platform.applicationblocks.com.queries.CfgTenantQuery;
import com.genesyslab.platform.commons.collections.KeyValueCollection;
import com.genesyslab.platform.configuration.protocol.types.CfgAppType;
import com.genesyslab.platform.configuration.protocol.types.CfgFlag;
import com.genesyslab.platform.configuration.protocol.types.CfgHAType;
import com.genesyslab.platform.configuration.protocol.types.CfgObjectState;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Sample methods to show how a configuration application object can be created.
 *
 * @author <a href="mailto:makagon@genesyslab.com">Petr Makagon</a>
 * @author <a href="mailto:vladb@genesyslab.com">Vladislav Baranovsky</a>
 * @author <a href="mailto:afilatov@genesyslab.com">Alexander Filatov</a>
 * @author <a href="mailto:abrazhny@genesyslab.com">Anton Brazhnyk</a>
 * @author <a href="mailto:svolokh@genesyslab.com">Sergii Volokh</a>
 */
public class CreateApplicationSamples {

    /**
     * Create detached configuration object with minimal set of options
     * initialized to be ready for save in the configuration server.
     *
     * @param service configuration service instance
     * @param appName name of the application to be created
     * @return detached configuration object
     * @throws ConfigException in case of any problems in existing configuration
     * in the configuration server
     * @throws InterruptedException process is interrupted
     * @see
     * com.genesyslab.platform.applicationblocks.com.objects.CfgApplication#save()
     */
    public static CfgApplication createSrvApplicationMinimal(
            final IConfService service,
            final String appName)
            throws ConfigException, InterruptedException {
        // Create detached object:
        CfgApplication app = new CfgApplication(service);

        app.setName(appName);
        app.setType(CfgAppType.CFGThirdPartyServer);
        app.setVersion("7.6.000.01");

        app.setState(CfgObjectState.CFGEnabled);

        app.setWorkDirectory(".");
        app.setCommandLine(".");

        // Read all configuration applications templates of ThirdPartyServer type
        // and choose first one for our new application:
        CfgAppPrototypeQuery prototypeQuery = new CfgAppPrototypeQuery();
        prototypeQuery.setAppType(app.getType());
        Collection<CfgAppPrototype> prototypes = service
                .retrieveMultipleObjects(CfgAppPrototype.class, prototypeQuery);
        if (prototypes.size() == 0) {
            throw new ConfigException(
                    "Please, add a prototype of type ThirdPartyServer to the configuration server");
        }
        app.setAppPrototype(prototypes.iterator().next());

        // Read all host objects configured in the configuration server
        // and use first one for our new application:
        CfgServer serverInfo = new CfgServer(service, app);
        Collection<CfgHost> hosts = service
                .retrieveMultipleObjects(CfgHost.class, new CfgHostQuery());
        if (hosts.size() == 0) {
            throw new ConfigException(
                    "Please, add a host configuration object to the configuration server");
        }
        serverInfo.setHost(hosts.iterator().next());
        serverInfo.setPort("2024");
        app.setServerInfo(serverInfo);

        return app;
    }

    /**
     * Create detached configuration application object of server type including
     * several complex properties/fields.
     *
     * @param service configuration service instance
     * @param appName name of the application to be created
     * @return detached configuration object
     * @throws ConfigException in case of any problems in existing configuration
     * in the configuration server
     * @throws InterruptedException process is interrupted
     * @see
     * com.genesyslab.platform.applicationblocks.com.objects.CfgApplication#save()
     */
    public static CfgApplication createSrvApplicationExt(
            final IConfService service,
            final String appName)
            throws ConfigException, InterruptedException {
        // Create detached object with minimal required options initialized:
        CfgApplication app = createSrvApplicationMinimal(service, appName);

        app.setAutoRestart(CfgFlag.CFGTrue);
        app.setStartupTimeout(90);
        app.setRedundancyType(CfgHAType.CFGHTHotStanby);
        app.setIsPrimary(CfgFlag.CFGTrue);

        CfgFolder baseFolder = service.retrieveObject(
                CfgFolder.class, new CfgFolderQuery("Boris"));
        if ((baseFolder != null) && (baseFolder.getDBID() != null)) {
            app.setFolderId(baseFolder.getDBID());
        }

        // Read all existing applications configured in the configuration server
        // and set last one as a "connected server":
        Collection<CfgApplication> connApps = service
                .retrieveMultipleObjects(CfgApplication.class,
                        new CfgApplicationQuery(/*"SomeAppName"*/));

        if (connApps.size() > 0) {
            List<CfgConnInfo> connAppInfos = new ArrayList<CfgConnInfo>();

            CfgConnInfo info = new CfgConnInfo(service, app);
            info.setAppServer((CfgApplication) connApps.toArray()[connApps.size() - 1]);
            info.setDescription("My Connection");
            connAppInfos.add(info);

            app.setAppServers(connAppInfos);
        }

        // Read list of all configured tenants and assign it to the application:
        Collection<CfgTenant> tenants = service
                .retrieveMultipleObjects(CfgTenant.class, new CfgTenantQuery());
        if (tenants != null) {
            app.setTenants(tenants);
        }

        // Create "Options" sections:
        KeyValueCollection sectionsList;
        KeyValueCollection sectionOptions;

        sectionsList = new KeyValueCollection();

        sectionOptions = new KeyValueCollection();
        sectionsList.addList("General", sectionOptions);
        sectionOptions.addString("ServerDescription", "Main server of the IT department");
        sectionOptions.addString("AdminAddress", "admin@somewhere.com");

        sectionOptions = new KeyValueCollection();
        sectionsList.addList("Caching", sectionOptions);
        sectionOptions.addString("CacheDir", "C:\\SomeAppCache");
        sectionOptions.addString("CacheSizeLimit", "1024");

        app.setOptions(sectionsList);

        return app;
    }
}
