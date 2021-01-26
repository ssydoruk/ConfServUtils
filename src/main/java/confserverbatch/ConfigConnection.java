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
import com.genesyslab.platform.commons.collections.*;
import com.genesyslab.platform.commons.connection.configuration.*;
import com.genesyslab.platform.commons.protocol.*;
import com.genesyslab.platform.configuration.protocol.*;
import com.genesyslab.platform.configuration.protocol.types.*;
import org.apache.logging.log4j.*;


public class ConfigConnection {

    public static IConfService initializeConfigService(
            final String cfgsrvEndpointName,
            final String cfgsrvHost,
            final int cfgsrvPort,
            final String username,
            final String password)
            throws ConfigException, InterruptedException, ProtocolException {

        CfgAppType clientType = CfgAppType.CFGSCE;
        String clientName = "default";

        LogManager.getLogger().info("initializeConfigService ");
        return initializeConfigService(cfgsrvEndpointName,
                cfgsrvHost, cfgsrvPort,
                clientType, clientName,
                username, password);
    }

    public static IConfService initializeConfigService(
            final String cfgsrvEndpointName,
            final String cfgsrvHost,
            final int cfgsrvPort,
            final CfgAppType clientType,
            final String clientName,
            final String username,
            final String password)
            throws ConfigException, InterruptedException, ProtocolException {

        KeyValueConfiguration cp = new KeyValueConfiguration(new KeyValueCollection());
//        cp.setTLSEnabled(true);

        Endpoint cfgServerEndpoint
                = new Endpoint(cfgsrvEndpointName, cfgsrvHost, cfgsrvPort);

        ConfServerProtocol protocol = new ConfServerProtocol(cfgServerEndpoint);
        protocol.setClientName(clientName);
        protocol.setClientApplicationType(clientType.ordinal());
        protocol.setUserName(username);
        protocol.setUserPassword(password);
        protocol.setUseLocalization(false);

        IConfService service = ConfServiceFactory.createConfService(protocol);

        protocol.open();

        return service;
    }

    public static void uninitializeConfigService(
            final IConfService service) throws ProtocolException, IllegalStateException, InterruptedException {
        if (service.getProtocol().getState() != ChannelState.Closed) {
            service.getProtocol().close();
        }
        ConfServiceFactory.releaseConfService(service);
    }
}
