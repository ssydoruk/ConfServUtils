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
import com.genesyslab.platform.applicationblocks.com.objects.CfgApplication;
import com.genesyslab.platform.applicationblocks.com.objects.CfgPerson;
import com.genesyslab.platform.applicationblocks.com.queries.CfgApplicationQuery;
import com.genesyslab.platform.applicationblocks.com.queries.CfgPersonQuery;


/**
 * Sample methods to show how configuration objects can be removed.
 *
 * @author <a href="mailto:makagon@genesyslab.com">Petr Makagon</a>
 * @author <a href="mailto:vladb@genesyslab.com">Vladislav Baranovsky</a>
 * @author <a href="mailto:afilatov@genesyslab.com">Alexander Filatov</a>
 * @author <a href="mailto:abrazhny@genesyslab.com">Anton Brazhnyk</a>
 * @author <a href="mailto:svolokh@genesyslab.com">Sergii Volokh</a>
 */
public class DeleteConfObjectSamples {
    public static CfgApplication deleteApplication(
            final IConfService service,
            final String appName)
                throws ConfigException {
        System.out.println("Deleting the application \"" + appName + "\"...");

        CfgApplication readApp = service.retrieveObject(CfgApplication.class,
                new CfgApplicationQuery(appName));

        if (readApp != null) {
            readApp.delete();
            System.out.println("    Application \"" + appName + "\" deleted successfuly.");
        } else {
            System.out.println("    There is no application named \"" + appName + "\".");
        }

        return readApp;
    }

    public static CfgPerson deletePerson(
            final IConfService service,
            final String userName)
                throws ConfigException {
        CfgPersonQuery personQuery = new CfgPersonQuery();

        personQuery.setUserName(userName);

        CfgPerson person = service.retrieveObject(CfgPerson.class, personQuery);

        if (person != null) {
            person.delete();
        }

        return person;
    }
}
