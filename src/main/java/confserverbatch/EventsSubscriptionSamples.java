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
import com.genesyslab.platform.applicationblocks.com.objects.CfgApplication;
import com.genesyslab.platform.applicationblocks.com.objects.CfgDeltaApplication;
import com.genesyslab.platform.applicationblocks.com.queries.CfgApplicationQuery;
import com.genesyslab.platform.applicationblocks.commons.Predicate;
import com.genesyslab.platform.applicationblocks.commons.broker.Subscriber;


/**
 * Sample methods for subscriptions and working with configuration server events.
 *
 * @author <a href="mailto:makagon@genesyslab.com">Petr Makagon</a>
 * @author <a href="mailto:vladb@genesyslab.com">Vladislav Baranovsky</a>
 * @author <a href="mailto:afilatov@genesyslab.com">Alexander Filatov</a>
 * @author <a href="mailto:abrazhny@genesyslab.com">Anton Brazhnyk</a>
 * @author <a href="mailto:svolokh@genesyslab.com">Sergii Volokh</a>
 */
public class EventsSubscriptionSamples {


    /**
     * Sample code showing how to setup and close subscription and
     * handler registration in configuration service for particular
     * application configuration object.
     * This method is useless by itself - it closes all initiated steps without
     * any tasks/operations done.
     * It is designed to show initialization and deinitialization steps
     * with required components (veriables, interfaces) in clear static context
     * without external dependencies except method input parameters.
     *
     * @param confService configuration service instance
     * @param cfgApplication some configuration application object
     * @throws ConfigException exception working with Configuration
     */
    public static void eventsStartAndStop4CfgApplication(
            final IConfService confService,
            final CfgApplication cfgApplication)
                throws ConfigException {
        // Create notification query:
        NotificationQuery notificationQuery = new NotificationQuery();
        notificationQuery.setObjectType(cfgApplication.getObjectType());
        notificationQuery.setObjectDbid(cfgApplication.getDBID());

        // Create notification mFilter:
        NotificationFilter notificationFilter = new NotificationFilter(notificationQuery);

        // Create subscriber:
        SampleSubscriber mySubscriber = new SampleSubscriber(notificationFilter);

        // Register subscriber in local configuration service message broker:
        confService.register(mySubscriber);

        // Subscribe for events on configuration server:
        // (null will be returned in case of error while subscription handling)
        Subscription mySubscription = confService.subscribe(notificationQuery);

        // Initialization is done.
        // Here we can run main working cycle.
        // mySubscriber will get notification events in case of changes on cfgApplication.
        // ...

        // Uninitialize registration and subscription:
        if (mySubscription != null) {
            confService.unsubscribe(mySubscription);
        }
        confService.unregister(mySubscriber);
    }

    /**
     * This method registers handler for all incoming configuration information changes
     * and subscribes for all such events in the configuration server.
     *
     * @param confService configuration service instance
     * @throws ConfigException COM AB exception
     */
    public static void eventsStartAndStop4All(
            final IConfService confService)
                throws ConfigException {
        // Create subscriber with null mFilter:
        SampleSubscriber mySubscriber = new SampleSubscriber(null);

        // Register subscriber in local configuration service message broker:
        confService.register(mySubscriber);

        // Subscribe for events on configuration server:
        // (null will be returned in case of error while subscription handling)
        Subscription mySubscription = confService.subscribe(new NotificationQuery());

        // Initialization is done.
        // Here we can run main working cycle.
        // mySubscriber will get notification events.
        // ...

        // Uninitialize registration and subscription:
        if (mySubscription != null) {
            confService.unsubscribe(mySubscription);
        }
        confService.unregister(mySubscriber);
    }


    static void testEvents(
            final IConfService confService, final String appName)
                throws ConfigException {
        System.out.println("Event test started. Waiting for the events.");

        CfgApplication app = confService.retrieveObject(CfgApplication.class,
                new CfgApplicationQuery(appName));

        NotificationQuery notificationQuery = new NotificationQuery();
        notificationQuery.setObjectType(app.getObjectType());
        notificationQuery.setObjectDbid(app.getDBID());
        //notificationQuery.setTenantDbid(WellKnownDBIDs.EnvironmentDBID);

        NotificationFilter filter = new NotificationFilter(notificationQuery);

        SampleSubscriber sampleSubscriber = new SampleSubscriber(filter);

        confService.register(sampleSubscriber);

        Subscription subscr = confService.subscribe(notificationQuery);

        try {
            for (int i = 0; i < 3600; i++) {
                System.out.print(".");
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            System.out.println("\nInterrupt signal received...");
        }

        confService.unregister(sampleSubscriber);

        System.out.println("\nEvent test part 1 finished.\n");

        confService.unsubscribe(subscr);
    }
    /**
     * Sample implementation of Subscriber on ConfigurationEvent.
     * This object will be called in case of subscribed incoming events.
     */
    private static class SampleSubscriber
            implements Subscriber<ConfEvent> {
        private Predicate<ConfEvent> mFilter;
        
        public SampleSubscriber(
                final Predicate<ConfEvent> filter) {
            this.mFilter = filter;
        }
        
        /**
         * Simple implementation of event handling interface.
         * It just prints some information to the STDOUT stream about incoming events.
         *
         * @param cfgEvent configuration service event
         */
        public void handle(
                final ConfEvent cfgEvent) {
            System.out.println(
                    "\nSampleSubscriber - Event received: EventType = "
                            + cfgEvent.getEventType()
                            + ", Object Type = "
                            + cfgEvent.getObjectType()
                            + ", Object Id = "
                            + cfgEvent.getObjectId()
                            + "!"
            );
            
            if (cfgEvent.getEventType() == ConfEvent.EventType.ObjectUpdated) {
                if (cfgEvent.getCfgObject() instanceof CfgDeltaApplication) {
                    CfgDeltaApplication deltaApp =
                            (CfgDeltaApplication) cfgEvent.getCfgObject();
                    
                    if (deltaApp.getName() != null) {
                        System.out.println("Application name changed to " + deltaApp.getName());
                        // do something...
                    }
                    if (deltaApp.getCommandLineArguments() != null) {
                        System.out.println("New Application commandline opts: "
                                + deltaApp.getCommandLineArguments()
                        );
                        // do something...
                    }
                }
            }
        }
        
        public Predicate<ConfEvent> getFilter() {
            return mFilter;
        }
    }
}
