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
import com.genesyslab.platform.applicationblocks.com.objects.CfgAgentInfo;
import com.genesyslab.platform.applicationblocks.com.objects.CfgAgentLogin;
import com.genesyslab.platform.applicationblocks.com.objects.CfgAgentLoginInfo;
import com.genesyslab.platform.applicationblocks.com.objects.CfgPerson;
import com.genesyslab.platform.applicationblocks.com.objects.CfgPlace;
import com.genesyslab.platform.applicationblocks.com.objects.CfgTenant;
import com.genesyslab.platform.applicationblocks.com.queries.CfgAgentLoginQuery;
import com.genesyslab.platform.applicationblocks.com.queries.CfgPersonQuery;
import com.genesyslab.platform.applicationblocks.com.queries.CfgPlaceQuery;
import com.genesyslab.platform.applicationblocks.com.queries.CfgTenantQuery;
import com.genesyslab.platform.commons.GEnum;
import com.genesyslab.platform.configuration.protocol.types.CfgFlag;
import com.genesyslab.platform.configuration.protocol.types.CfgObjectType;
import com.genesyslab.platform.configuration.protocol.types.CfgPermissions;
import java.util.ArrayList;
import java.util.List;


/**
 * Sample methods to show some operations on agent related configuration information access.
 *
 * @author <a href="mailto:makagon@genesyslab.com">Petr Makagon</a>
 * @author <a href="mailto:vladb@genesyslab.com">Vladislav Baranovsky</a>
 * @author <a href="mailto:afilatov@genesyslab.com">Alexander Filatov</a>
 * @author <a href="mailto:abrazhny@genesyslab.com">Anton Brazhnyk</a>
 * @author <a href="mailto:svolokh@genesyslab.com">Sergii Volokh</a>
 */
public class AgentInformationSamples {
    /**
     * Create person description structure for storage in configuration server database.
     * This method creates some object with set of initialized fields but does not save it
     * to the configuration server.
     *
     * @param service configuration service instance
     * @param userName username of person to be created
     * @return detached confiduration object instance
     * @throws ConfigException in case of any problems in existing configuration
     *      in the configuration server
     * @see com.genesyslab.platform.applicationblocks.com.objects.CfgPerson#save()
     */
    public static CfgPerson createPersonRecord(
            final IConfService service,
            final String userName)
                throws ConfigException {
        CfgPerson newAgent = new CfgPerson(service);

        newAgent.setUserName(userName);

        newAgent.setFirstName("Ivan");
        newAgent.setLastName("Poddubnyi");
        newAgent.setEmployeeID("123567204434");

        newAgent.setIsAgent(CfgFlag.CFGTrue);

        newAgent.setTenantDBID(WellKnownDBIDs.EnvironmentDBID);

        return newAgent;
    }

    /**
     * Sample showing how agent specific information can be added to person configuration
     * object in the configuration server. This sample contains code to assign Place
     * and Agent Login to specified Person.
     * Note: This sample method does not save changes to the configuration server.
     *
     * @param service configuration service instance
     * @param newPerson name of person to modify
     * @return modified but not saved person
     * @throws ConfigException in case of any problems in existing configuration
     *      in the configuration server
     * @throws InterruptedException process is interrupted
     * @see com.genesyslab.platform.applicationblocks.com.objects.CfgPerson#save()
     */
    public static CfgPerson addSpecInfo2PersonRecord(
            final IConfService service,
            final String newPerson)
                throws ConfigException, InterruptedException {
        CfgPersonQuery personQuery = new CfgPersonQuery();
        personQuery.setUserName(newPerson);
        CfgPerson newAgent = service.retrieveObject(CfgPerson.class, personQuery);

        CfgAgentInfo agentInfo = new CfgAgentInfo(service, newAgent);

        CfgPlace place = service.retrieveObject(CfgPlace.class, new CfgPlaceQuery("test_place"));
        if (place != null) {
            agentInfo.setPlace(place);
        }

        CfgAgentLoginQuery queryAgentLogin = new CfgAgentLoginQuery();
        queryAgentLogin.setLoginCode("test_login");
        CfgAgentLogin agentLogin = service.retrieveMultipleObjects(CfgAgentLogin.class, queryAgentLogin)
                .iterator().next();
        if (agentLogin != null) {
            CfgAgentLoginInfo agentLoginInfo = new CfgAgentLoginInfo(service, newAgent);
            agentLoginInfo.setAgentLogin(agentLogin);

            List<CfgAgentLoginInfo> agentLoginInfos = new ArrayList<CfgAgentLoginInfo>();
            agentLoginInfos.add(agentLoginInfo);

            agentInfo.setAgentLogins(agentLoginInfos);
        }

        newAgent.setAgentInfo(agentInfo);

        return newAgent;
    }

    /**
     * Sample method for persons' permissions reading.
     *
     * @param service confiduration service instance
     * @param newAgent person object
     * @throws ConfigException in case of any problems while reading of the information
     */
    public static void readPersonPermissions(
            final IConfService service,
            final CfgPerson newAgent)
                throws ConfigException {
        // Read agents' permissions on particular configuration object (tenant):
        CfgTenant userTenant = newAgent.getTenant();
        if (userTenant == null) {
            userTenant = service.retrieveObject(CfgTenant.class,
                new CfgTenantQuery(WellKnownDBIDs.EnvironmentDBID));
        }

        int permissions = userTenant.retrieveAccountPermissions(newAgent);
        System.out.println("Agent has the following permissions on Tenant: " + permissions);

        // Read all agents' permissions:
        List<PermissionDescriptor> permissionList = newAgent.retrievePermissions();
        System.out.println("Agent has the following permissions: "
                    + permissionList.size() + " items\n{"
        );
        for (PermissionDescriptor perm : permissionList) {
            ICfgObject myObj = perm.retrieveObject(service);
            System.out.println("  "
                    + GEnum.getValue(CfgObjectType.class, perm.getObjectType()).name()
                    + ", dbid=" + perm.getObjectDbid()
                    + ", mask=" + perm.getAccessMask()
                    + ", " + myObj.getClass().getCanonicalName()
                );
        }
        System.out.println("}");
    }

    /**
     * Sample method for persons' permissions setting on particular configuration
     * object - tenant.
     *
     * @param service confiduration service instance
     * @param newAgent person object
     * @throws ConfigException in case of any problems while reading or writing of the information
     */
    public static void modifyPersonPermissions(
            final IConfService service,
            final CfgPerson newAgent)
                throws ConfigException {
        CfgTenant userTenant = newAgent.getTenant();
        if (userTenant == null) {
            userTenant = service.retrieveObject(CfgTenant.class,
                new CfgTenantQuery(WellKnownDBIDs.EnvironmentDBID));
        }

        userTenant.setAccountPermissions(
                newAgent,
                CfgPermissions.ReadExecuteAccess.asInteger()
        );
    }
}
