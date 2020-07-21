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
// Copyright (c) 2007 - 2017 Genesys Telecommunications Laboratories, Inc. All rights reserved.
//===============================================================================
package confserverbatch.gui;

import com.genesyslab.platform.applicationblocks.com.*;
import com.genesyslab.platform.applicationblocks.com.queries.CfgApplicationQuery;
import com.genesyslab.platform.applicationblocks.com.objects.CfgApplication;

import com.genesyslab.platform.applicationblocks.commons.Action;

import com.genesyslab.platform.configuration.protocol.types.CfgAppType;
import com.genesyslab.platform.configuration.protocol.types.CfgObjectType;
import com.genesyslab.platform.configuration.protocol.ConfServerProtocol;

import com.genesyslab.platform.commons.protocol.Endpoint;
import com.genesyslab.platform.commons.protocol.ChannelState;
import com.genesyslab.platform.commons.protocol.ProtocolException;
import com.genesyslab.platform.commons.protocol.Protocol;

import com.genesyslab.platform.commons.GEnum;
import com.genesyslab.platform.commons.collections.KeyValueCollection;
import com.genesyslab.platform.commons.connection.configuration.KeyValueConfiguration;

import java.net.URI;

import java.util.ResourceBundle;
import java.util.PropertyResourceBundle;

import java.io.FileInputStream;

/**
 * This class is used to access configuration server data.
 */
class ConfigurationAccessor {

    private String host;
    private int port;
    private String userName;
    private String password;
    private String clientName;
    private CfgAppType clientType;

    private IConfService confService;

    private Action<ConfEvent> currentHandler;
    private Subscription currentSubscription;
    private int subscribedAppDBID = -1;

    ConfigurationAccessor() {
    }

    /**
     * Creates a new instance of the Configuration Server protocol and sets all
     * of the attributes required for its use.
     */
    private ConfServerProtocol createProtocol() {
        KeyValueConfiguration cp = new KeyValueConfiguration(new KeyValueCollection());
        cp.setTLSEnabled(true);
        Endpoint desc = new Endpoint("ConfAcc#" + hashCode(), host, port, cp);

        ConfServerProtocol protocol = new ConfServerProtocol(desc);

        protocol.setUserName(userName);
        protocol.setUserPassword(password);
        protocol.setClientName(clientName);
        protocol.setClientApplicationType(clientType.ordinal());

        return protocol;
    }

    /**
     * Returns the current state of the configuration server channel. Should be
     * used to determine whether a connection is opened.
     */
    public ChannelState getChannelState() {
        if (confService != null) {
            Protocol protocol = confService.getProtocol();
            if (protocol != null) {
                return protocol.getState();
            }
        }
        return ChannelState.Closed;
    }

    /**
     * The URI of the current configuration server.
     */
    public URI getUri() {
        if (confService != null) {
            Protocol protocol = confService.getProtocol();
            if (protocol != null && protocol.getEndpoint() != null) {
                return protocol.getEndpoint().getUri();
            }
        }
        return null;
    }

    /**
     * Used to determine whether we're subscribed for events relating to the
     * application specified by the passed dbid.
     */
    public boolean isSubscribedForAppEvents(final int dbid) {
        return (subscribedAppDBID == dbid);
    }

    /**
     * Used to open the connection to the Configuration Server specified in the
     * App.config file for this application.
     */
    public void connect()
            throws ConfigException, ProtocolException, InterruptedException {
        ConfServerProtocol protocol = createProtocol();
        confService = ConfServiceFactory.createConfService(protocol);

        try {
            protocol.open();
        } catch (Exception ex) {
            ConfServiceFactory.releaseConfService(confService);
            confService = null;
            throw new ConfigRuntimeException(
                    "Cannot connect to the configuration server (" + ex.getMessage() + ").");
        }
    }

    /**
     * Should be called before working with Configuration Server. This method
     * reads the necessary configuration information from the App.config and
     * sets the appropriate properties.
     */
    public void initialize() {
        host = ConfigurationManager.getAppOption("ConfServerHost");
        port = Integer.parseInt(ConfigurationManager.getAppOption("ConfServerPort"));

        try {
            new URI("tcp://" + host + ":" + port);
        } catch (Exception e) {
            throw new RuntimeException("Invalid configuration server URI: "
                    + e.toString());
        }

        userName = ConfigurationManager.getAppOption("ConfServerUser");
        password = ConfigurationManager.getAppOption("ConfServerPassword");
        clientName = ConfigurationManager.getAppOption("ConfServerClientName");

        clientType = null;
        String clntType = ConfigurationManager.getAppOption("ConfServerClientType");
        if (clntType != null && !clntType.equals("")) {
            String type = clntType;
            if (!type.startsWith("CFG")) {
                type = "CFG" + type;
            }
            clientType = (CfgAppType) GEnum.getValue(CfgAppType.class, type);
            if (clientType == null) {
                throw new RuntimeException(
                        "Invalid client type '" + clntType
                        + "'. See CfgAppType enum for valid values.");
            }
        }
    }

    /**
     * Closes connection with configuration server.
     */
    void disconnect()
            throws ProtocolException, InterruptedException {
        if (confService != null) {
            Protocol protocol = confService.getProtocol();
            if (protocol != null && protocol.getState() != ChannelState.Closed) {
                protocol.close();
            }
            ConfServiceFactory.releaseConfService(confService);
            confService = null;
        }
    }

    /**
     * Retrieves an application by name.
     *
     * @param appName application name as seen in configuration server
     * @return CfgApplication object describing the retrieved application, or
     * null if not found
     */
    public CfgApplication retrieveApplication(final String appName)
            throws ConfigException {
        return confService.retrieveObject(CfgApplication.class,
                new CfgApplicationQuery(appName));
    }

    /**
     * Unsubscribes from Configuration server notifications about the current
     * "subscribed" application.
     */
    public void unsubscribe() throws ConfigException {
        if (currentHandler != null && currentSubscription != null) {
            confService.unregister(currentHandler);
            confService.unsubscribe(currentSubscription);
        }

        currentHandler = null;
        currentSubscription = null;

        subscribedAppDBID = -1;
    }

    /**
     * Subscribes to events about an object of the specified type, and with the
     * specified dbid.
     *
     * @param myAction a delegate which will be called when events are received
     * @param objectType the type of subscribed object
     * @param dbid the dbid of subscribed object
     */
    public void subscribe(
            final Action<ConfEvent> myAction,
            final CfgObjectType objectType,
            final int dbid)
            throws ConfigException {

        if (currentHandler != null && currentSubscription != null) {
            unsubscribe();
        }

        NotificationQuery notificationQuery = new NotificationQuery();
        notificationQuery.setObjectType(objectType);
        notificationQuery.setObjectDbid(dbid);

        NotificationFilter filter = new NotificationFilter(notificationQuery);

        confService.register(myAction, filter);

        currentSubscription = confService.subscribe(notificationQuery);
        currentHandler = myAction;

        subscribedAppDBID = dbid;
    }

    private static class ConfigurationManager {

        private static ResourceBundle properties;

        static {
            try {
                properties = new PropertyResourceBundle(
                        new FileInputStream("quickstart.properties"));
            } catch (Exception ex) {
                throw new RuntimeException(
                        "Can't read application configuration", ex);
            }
        }

        static String getAppOption(final String optName) {
            String val = properties.getString(optName);
            if (val == null) {
                throw new RuntimeException(
                        "Application configuration option '" + optName + "' is not defined");
            }
            return val;
        }
    }
}
