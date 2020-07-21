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

import com.genesyslab.platform.applicationblocks.com.AsyncRequestResult;
import com.genesyslab.platform.applicationblocks.com.ConfigException;
import com.genesyslab.platform.applicationblocks.com.IConfService;
import com.genesyslab.platform.applicationblocks.com.objects.CfgAppPrototype;
import com.genesyslab.platform.applicationblocks.com.objects.CfgApplication;
import com.genesyslab.platform.applicationblocks.com.objects.CfgConnInfo;
import com.genesyslab.platform.applicationblocks.com.objects.CfgHost;
import com.genesyslab.platform.applicationblocks.com.objects.CfgPerson;
import com.genesyslab.platform.applicationblocks.com.objects.CfgPortInfo;
import com.genesyslab.platform.applicationblocks.com.objects.CfgServer;
import com.genesyslab.platform.applicationblocks.com.queries.CfgApplicationQuery;
import com.genesyslab.platform.applicationblocks.com.queries.CfgPersonQuery;
import com.genesyslab.platform.applicationblocks.commons.Action;
import com.genesyslab.platform.commons.collections.KeyValueCollection;
import com.genesyslab.platform.commons.collections.KeyValuePair;
import java.util.Collection;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


/**
 * Sample methods for reading information from the configuration server.
 *
 * @author <a href="mailto:makagon@genesyslab.com">Petr Makagon</a>
 * @author <a href="mailto:vladb@genesyslab.com">Vladislav Baranovsky</a>
 * @author <a href="mailto:afilatov@genesyslab.com">Alexander Filatov</a>
 * @author <a href="mailto:abrazhny@genesyslab.com">Anton Brazhnyk</a>
 * @author <a href="mailto:svolokh@genesyslab.com">Sergii Volokh</a>
 */
public class ReadConfigurationSamples {
    /**
     * Sample method to print information from read application configuration object.
     *
     * @param cfgApp application object to print
     * @param printPrefix string prefix to be printed before each line
     */
    public static void printShortCfgApplication(
            final CfgApplication cfgApp,
            final String printPrefix) {
        System.out.println(printPrefix + "CfgApplication:");

        System.out.println(printPrefix + "    name               = " + cfgApp.getName());
        System.out.println(printPrefix + "    dbid               = " + cfgApp.getDBID());
        System.out.println(printPrefix + "    type               = " + cfgApp.getType());
        System.out.println(printPrefix + "    version            = " + cfgApp.getVersion());
        System.out.println(printPrefix + "    objectPath         = " + cfgApp.getObjectPath());
        System.out.println(printPrefix + "    state              = " + cfgApp.getState());

        CfgAppPrototype proto = cfgApp.getAppPrototype();
        if (proto != null) {
            System.out.println(printPrefix + "    prototype          = " + proto.getName());
        }

        System.out.println(printPrefix + "    password           = " + cfgApp.getPassword());

        CfgServer cfgSrv = cfgApp.getServerInfo();
        if (cfgSrv != null) {
            CfgHost cfgHost = cfgSrv.getHost();
            System.out.println(printPrefix + "    serverInfo         = "
                    + cfgHost.getName() + "("
                    + cfgHost.getOSinfo().getOStype().name() + ") : "
                    + cfgSrv.getPort());
        }

        Collection<CfgPortInfo> portinfos = cfgApp.getPortInfos();
        if (portinfos != null) {
            System.out.println(printPrefix + "    portInfos          = {");
            for (CfgPortInfo pi : portinfos) {
                System.out.println(printPrefix + "        "
                        + pi.getPort() + " - " + pi.getDescription());
            }
            System.out.println(printPrefix + "    }");
        }

        System.out.println(printPrefix + "    workingDirectory   = " + cfgApp.getWorkDirectory());
        System.out.println(printPrefix + "    commandLine        = " + cfgApp.getCommandLine());
        System.out.println(printPrefix + "    commandLineArgs    = " + cfgApp.getCommandLineArguments());

        System.out.println(printPrefix + "    isServer           = " + cfgApp.getIsServer());
        System.out.println(printPrefix + "    isPrimary          = " + cfgApp.getIsPrimary());
        System.out.println(printPrefix + "    autoRestart        = " + cfgApp.getAutoRestart());
        System.out.println(printPrefix + "    redundancyType     = " + cfgApp.getRedundancyType());

        System.out.println(printPrefix + "    startupType        = " + cfgApp.getStartupType());
        System.out.println(printPrefix + "    startupTimeout     = " + cfgApp.getStartupTimeout());
        System.out.println(printPrefix + "    shutdownTimeout    = " + cfgApp.getShutdownTimeout());
    }

    /**
     * Sample method to print extended information from read application configuration object.
     * This method also gets from the configuration server refered configuration application objects
     * and prints short information about that objects. This infromation is read on request
     * and we do not need to specify configuration service by itself or do something for this -
     * it is used from the main application object behind the scene.
     *
     * @param cfgApp application object to print
     */
    public static void printCfgApplication(
            final CfgApplication cfgApp) {
        printShortCfgApplication(cfgApp, "");

        // Print all options:
        System.out.println("    options            = {");
        KeyValueCollection appOptions = cfgApp.getOptions();
        for (Object sectionObj : appOptions) {
            KeyValuePair sectionKvp = (KeyValuePair) sectionObj;
            System.out.println("        Section \"" + sectionKvp.getStringKey() + "\" = {");
            for (Object recordObj : sectionKvp.getTKVValue()) {
                KeyValuePair recordKvp = (KeyValuePair) recordObj;
                System.out.println("            \""
                        + recordKvp.getStringKey() + "\" = \""
                        + recordKvp.getStringValue() + "\"");
            }
            System.out.println("        }");
        }
        System.out.println("    }");

        // Connected applications:
        Collection<CfgConnInfo> lConApps = cfgApp.getAppServers();
        System.out.print("    connectedApps      = ");
        if (lConApps != null) {
            System.out.println("{");
            for (CfgConnInfo conni : lConApps) {
                String descr = conni.getDescription();
                if (descr != null && !descr.equals("")) {
                    System.out.println("      \"" + descr + "\":");
                }
                printShortCfgApplication(conni.getAppServer(), "        ");
            }
            System.out.println("    }");
        } else {
            System.out.println("null");
        }
    }

    /**
     * Read application configuration object and print it.
     *
     * @param service configuration service instance
     * @param appName name of application to read and print
     * @throws ConfigException in case of exception while reading object
     */
    public static void readAndPrintCfgApplication(
            final IConfService service,
            final String appName)
                throws ConfigException {
        // Read configuration application object:
        CfgApplication readApp = service.retrieveObject(CfgApplication.class,
                new CfgApplicationQuery(appName));

        printCfgApplication(readApp);
    }

    /**
     * Read and print all person configuration objects.
     *
     * @param service configuration service instance
     * @throws ConfigException in case of exception while reading objects
     * @throws InterruptedException in case of interrupt signal
     */
    public static void readAndPrintAllCfgPersons(
            final IConfService service)
                throws ConfigException, InterruptedException {
        // Read configuration objects:
        Collection<CfgPerson> persons = service.retrieveMultipleObjects(CfgPerson.class,
                new CfgPersonQuery());

        System.out.println("Read " + persons.size() + " persons:");
        for (CfgPerson person : persons) {
            System.out.println("  " + person.getUserName() + " - "
                    + person.getFirstName() + " " + person.getLastName());
        }
    }

    /**
     * Read all application configuration objects asynchronously.
     *
     * @param service configuration service instance
     * @throws ConfigException in case of exception while reading objects
     * @throws InterruptedException in case of interrupt signal
     */
    public static void readAllCfgApplicationsAsync(
            final IConfService service)
                throws ConfigException, InterruptedException {
        // Start application configuration objects reading:
        AsyncRequestResult<CfgApplication> asyncRequest =
                service.beginRetrieveMultipleObjects(CfgApplication.class,
                        new CfgApplicationQuery(), null);

        int sleepDelta = 1;
        System.out.print("Applications reading: ");
        do {
            System.out.print(".");

            Thread.sleep(sleepDelta);
            // We can also use following method intead of sleep():
            //     asyncRequest.waitDone(sleepDelta, TimeUnit.MILLISECONDS);
            // This method will wait for read finish for not more than mentioned timeout.
            // So, by this way sleeping will be stopped immediately when data received.

            sleepDelta++;
        } while (!asyncRequest.isDone());
        System.out.println(" done");

        try {
            Collection<CfgApplication> applications = asyncRequest.get();
            System.out.println("Read " + applications.size() + " applications:");
            for (CfgApplication application : applications) {
                System.out.println("  \"" + application.getName() + "\" - "
                    + application.getType());
            }
        } catch (ExecutionException ee) {
            System.out.println("Error reading applications: " + ee);
            System.out.println("           inner exception: " + ee.getCause());
        }
    }

    /**
     * Read all application configuration objects asynchronously with callback usage.
     *
     * @param service configuration service instance
     * @throws ConfigException in case of exception while reading objects
     * @throws InterruptedException in case of interrupt signal
     */
    public static void readAllCfgApplicationsAsyncWCallback(
            final IConfService service)
                throws ConfigException, InterruptedException {
        // Create typified callback:
        Action<AsyncRequestResult<CfgApplication>> callback =
                new Action<AsyncRequestResult<CfgApplication>>() {
                    public void handle(final AsyncRequestResult<CfgApplication> obj) {
                        try {
                            Collection<CfgApplication> applications = obj.get();
                            System.out.println("\nApplications just read - " + applications.size());
                        } catch (Exception e) {
                            e.printStackTrace(System.out);
                        }
                    }
                };

        // Start application configuration objects reading:
        AsyncRequestResult<CfgApplication> asyncRequest =
                service.beginRetrieveMultipleObjects(CfgApplication.class,
                        new CfgApplicationQuery(), callback);

        int sleepDelta = 1;
        System.out.print("Applications reading: ");
        try {
            do {
                System.out.print(".");
                asyncRequest.get(sleepDelta++, TimeUnit.MILLISECONDS);
            } while (!asyncRequest.isDone());
        } catch (ExecutionException e) {
            e.printStackTrace(System.out);
        } catch (TimeoutException e) {
            e.printStackTrace(System.out);
        }
        System.out.println(" done");
    }
}
