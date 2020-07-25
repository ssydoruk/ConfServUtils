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
package confserverbatch.gui;

import com.genesyslab.platform.applicationblocks.com.ConfEvent;
import com.genesyslab.platform.applicationblocks.com.ConfigException;
import com.genesyslab.platform.applicationblocks.com.ICfgDelta;
import com.genesyslab.platform.applicationblocks.com.objects.CfgApplication;
import com.genesyslab.platform.applicationblocks.com.objects.CfgConnInfo;
import com.genesyslab.platform.configuration.protocol.types.CfgObjectType;
import java.awt.*;
import java.awt.event.*;
import java.net.URI;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;


public class FormQuickStart extends JFrame {

    private JPanel panelRoot;
    private JTextField jtxtfldAppNameQuery;
    private JButton jbtnRetrieve;
    private JPanel panelApplicationPropertiesRoot;
    private JButton jbtnSubscribe;
    private JButton jbtnUnsubscribe;
    private JButton jbtnApply;
    private JTextField jtxtfldAppType;
    private JTextField jtxtfldAppName;
    private JTextField jtxtfldAppVersion;
    private JList jlistConnections;
    private KVListOptionsTree jtreeOptions;
    private JTextField jtxtfldStatus;

    private ConfigurationAccessor configAccessor;

    private CfgApplication currentApplication = null;


    FormQuickStart() {
        super("COM Quick Start");

        initUIComponents();

        jtxtfldAppNameQuery.setText("COMCfgApplication");

        jtxtfldAppType.setEditable(false);

        setContentPane(panelRoot);
        setPreferredSize(new Dimension(353, 413));
        setResizable(false);

        jbtnRetrieve.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onRetrieve();
            }
        });
        jbtnSubscribe.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onSubscribe();
            }
        });
        jbtnUnsubscribe.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onUnsubscribe();
            }
        });
        jbtnApply.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onApply();
            }
        });

        // call onExit() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(final WindowEvent e) {
                onLoad();
            }

            @Override
            public void windowClosing(WindowEvent e) {
                onExit();
            }
        });

        initConfigurationAccessor();
    }


    private void initUIComponents() {
        panelRoot = new JPanel();
        JPanel panel1 = new JPanel();
        jtxtfldAppNameQuery = new JTextField();
        jbtnRetrieve = new JButton("Retrieve");
        panelApplicationPropertiesRoot = new JPanel();
        JPanel panel2 = new JPanel();
        JPanel panel3 = new JPanel();
        JPanel panel4 = new JPanel();
        JPanel panel5 = new JPanel();
        JPanel panel6 = new JPanel();
        jbtnSubscribe = new JButton("Subscribe");
        jbtnUnsubscribe = new JButton("Unsubscribe");
        jbtnApply = new JButton("Apply");
        jtxtfldAppType = new JTextField();
        jtxtfldAppName = new JTextField();
        jtxtfldAppVersion = new JTextField();
        jtxtfldStatus = new JTextField();
        jlistConnections = new JList(new DefaultListModel());
        jtreeOptions = new KVListOptionsTree();

        panelRoot.setLayout(new BorderLayout(0, 0));

        panelRoot.add(panel1, BorderLayout.NORTH);
        panel1.setBorder(new TitledBorder("Application Query"));
        panel1.setLayout(new BorderLayout(3, 0));
        panel1.add(new JLabel("Name:"), BorderLayout.WEST);
        panel1.add(jtxtfldAppNameQuery, BorderLayout.CENTER);
        panel1.add(jbtnRetrieve, BorderLayout.EAST);

        panelRoot.add(panelApplicationPropertiesRoot, BorderLayout.CENTER);
        panelApplicationPropertiesRoot.setBorder(new TitledBorder("Application Properties"));
        panelApplicationPropertiesRoot.setLayout(new BorderLayout());

        panelRoot.add(jtxtfldStatus, BorderLayout.SOUTH);
        jtxtfldStatus.setBorder(new EmptyBorder(0, 0, 0, 0));
        jtxtfldStatus.setEditable(false);

        panelApplicationPropertiesRoot.add(panel2, BorderLayout.NORTH);
        GridBagLayout gbl1 = new GridBagLayout();
        GridBagConstraints gbc1 = new GridBagConstraints();
        gbc1.fill = GridBagConstraints.BOTH;
        gbc1.weightx = 1.0;
        panel2.setLayout(gbl1);
        JLabel lbl1 = new JLabel("Name:");
        gbl1.setConstraints(lbl1, gbc1);
        panel2.add(lbl1);
        gbc1.gridwidth = GridBagConstraints.REMAINDER; //end row
        gbc1.weightx = 3.0;
        gbl1.setConstraints(jtxtfldAppName, gbc1);
        panel2.add(jtxtfldAppName);
        JLabel lbl2 = new JLabel("Type:");
        gbc1.gridwidth = 1;
        gbc1.weightx = 1.0;
        gbl1.setConstraints(lbl2, gbc1);
        panel2.add(lbl2);
        gbc1.gridwidth = GridBagConstraints.REMAINDER; //end row
        gbc1.weightx = 3.0;
        gbl1.setConstraints(jtxtfldAppType, gbc1);
        panel2.add(jtxtfldAppType);
        JLabel lbl3 = new JLabel("Version:");
        gbc1.gridwidth = 1;
        gbc1.weightx = 1.0;
        gbl1.setConstraints(lbl3, gbc1);
        panel2.add(lbl3);
        gbc1.gridwidth = GridBagConstraints.REMAINDER; //end row
        gbc1.weightx = 3.0;
        gbl1.setConstraints(jtxtfldAppVersion, gbc1);
        panel2.add(jtxtfldAppVersion);

        panelApplicationPropertiesRoot.add(panel3, BorderLayout.CENTER);
        GridBagLayout gbl2 = new GridBagLayout();
        GridBagConstraints gbc2 = new GridBagConstraints();
        panel3.setLayout(gbl2);
        gbc2.fill = GridBagConstraints.BOTH;
        gbc2.weightx = 1.0;
        JLabel lbl4 = new JLabel("Connections:");
        gbl2.setConstraints(lbl4, gbc2);
        panel3.add(lbl4);
        JLabel lbl5 = new JLabel("Options:");
        gbc2.gridwidth = GridBagConstraints.REMAINDER; //end row
        gbl2.setConstraints(lbl5, gbc2);
        panel3.add(lbl5);
        gbc2.gridwidth = 1;
        gbc2.weightx = 0;
        gbc2.weighty = 1;
        gbl2.setConstraints(panel4, gbc2);
        panel3.add(panel4);
        panel4.setBorder(BorderFactory.createEtchedBorder());
        panel4.setLayout(new BorderLayout());
        panel4.add(new JScrollPane(jlistConnections), BorderLayout.CENTER);
        gbc2.weightx = 2;
        gbc2.gridwidth = GridBagConstraints.REMAINDER; //end row
        gbl2.setConstraints(panel5, gbc2);
        panel3.add(panel5);
        panel5.setBorder(BorderFactory.createEtchedBorder());
        panel5.setLayout(new BorderLayout());
        panel5.add(new JScrollPane(jtreeOptions), BorderLayout.CENTER);

        panelApplicationPropertiesRoot.add(panel6, BorderLayout.SOUTH);
        FlowLayout fll = new FlowLayout();
        fll.setAlignment(FlowLayout.RIGHT);
        panel6.setLayout(fll);
        panel6.add(jbtnSubscribe);
        panel6.add(jbtnUnsubscribe);
        panel6.add(jbtnApply);
    }

    /**
     * We initialize the configuration accessor and connect to
     * Configuration Server from form constructor. The configuration information
     * for Configuration server is specified in the quickstart.properties file.
     */
    private void initConfigurationAccessor() {
        try {
            updateUI();
            configAccessor = new ConfigurationAccessor();
            configAccessor.initialize();

            updateStatusBar("Openning connection...");
            configAccessor.connect();

            updateStatusBar(configAccessor.getChannelState().toString());
            updateUI();
        } catch (Exception ex) {
            updateStatusBar(ex.toString());
            messageBox("Exception in connection initialization", ex);
            configAccessor = null;
        }
    }


    protected void updateStatusBar(final String status) {
        String prefix;
        URI confUri = null;
        if (configAccessor != null) {
            confUri = configAccessor.getUri();
        }
        if (confUri != null) {
            prefix = "[tcp://" + confUri.getHost()
                    + ":" + confUri.getPort() + "/]: ";
        } else {
            prefix = "[no connection]: ";
        }
        jtxtfldStatus.setText(prefix + status);
    }

    protected void messageBox(final String message, final Exception ex) {
        MessageDialog dialog = new MessageDialog(this, message, ex);
        dialog.pack();
        Dimension screenSize =
                Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = dialog.getSize();
        int x = (screenSize.width - frameSize.width) / 2;
        int y = (screenSize.height - frameSize.height) / 2;
        dialog.setLocation(x, y);
        dialog.setVisible(true);
    }


    private void updateUI() {
        boolean isConnectionAvailable = (configAccessor != null);
        boolean isAppAvailable = (currentApplication != null);

        jtxtfldAppNameQuery.setEnabled(isConnectionAvailable);
        jbtnRetrieve.setEnabled(isConnectionAvailable);

        jtxtfldAppName.setEnabled(isAppAvailable);
        jtxtfldAppVersion.setEnabled(false);
        jbtnSubscribe.setEnabled(isAppAvailable
                && !configAccessor.isSubscribedForAppEvents(currentApplication.getObjectDbid()));
        jbtnUnsubscribe.setEnabled(isAppAvailable && !jbtnSubscribe.isEnabled());
        jlistConnections.setEnabled(isAppAvailable);
        jtreeOptions.setEnabled(isAppAvailable);

        if (isAppAvailable) {
            setPropertiesTitle("\"" + currentApplication.getName()
                    + "\" (dbid=" + currentApplication.getDBID() + ") Properties",
                    Color.BLACK);

            jtxtfldAppName.setText(currentApplication.getName());
            jtxtfldAppType.setText(currentApplication.getType().toString());
            jtxtfldAppVersion.setText(currentApplication.getVersion());

            fillConnectionsList(currentApplication);
            jtreeOptions.setKVListData(currentApplication.getOptions());
        } else {
            setPropertiesTitle("No application loaded", Color.GRAY);

            jtxtfldAppName.setText("");
            jtxtfldAppType.setText("");
            jtxtfldAppVersion.setText("");

            fillConnectionsList(null);
            jtreeOptions.setKVListData(null);
        }

        jbtnApply.setEnabled(isAppAvailable);
    }

    private void fillConnectionsList(final CfgApplication app) {
        ((DefaultListModel) jlistConnections.getModel()).clear();
        if (app != null && app.getAppServers() != null) {
            for (CfgConnInfo connInfo : app.getAppServers()) {
                CfgApplication lapp = connInfo.getAppServer();
                String val = lapp.getName() + " [" + lapp.getType() + "]";
                ((DefaultListModel) jlistConnections.getModel()).addElement(val);
                        connInfo.getAppServer().getName();
            }
        }
    }

    protected void setPropertiesTitle(
            final String title, final Color color) {
        TitledBorder border = (TitledBorder) panelApplicationPropertiesRoot.getBorder();
        border.setTitle(title);
        border.setTitleColor(color);
        panelApplicationPropertiesRoot.repaint();
    }


    private void onLoad() {
    }

    private void onExit() {
        if (configAccessor != null) {
            try {
                configAccessor.unsubscribe();
            } catch (Exception e) {
                messageBox("Exception on unsubscribe", e);
            }
            try {
                configAccessor.disconnect();
            } catch (Exception e) {
                updateStatusBar("Exception on disconnect: " + e.toString());
            }
        }
        dispose();
    }

    private void onRetrieve() {
        String appName = jtxtfldAppNameQuery.getText();
        if (!appName.equals("")) {
            try {
                configAccessor.unsubscribe();
            } catch (ConfigException e) {
                e.printStackTrace();
            }
            currentApplication = null;
            updateUI();

            try {
                currentApplication = configAccessor.retrieveApplication(appName);
                if (currentApplication == null) {
                    messageBox("Specified application is not found! ('" + appName + "')", null);
                } else {
                    updateStatusBar("Retrieved application " + currentApplication.getName());
                    updateUI();
                }
            } catch (Exception ex) {
                messageBox("Can't retrieve application '" + appName + "'!", ex);
            }
        }
    }

    private void onSubscribe() {
        if (currentApplication != null) {
            try {
                configAccessor.subscribe(
                        new SubscriptionEventHandler(),
                        CfgObjectType.CFGApplication,
                        currentApplication.getObjectDbid());
                updateStatusBar("Subscribed for "
                        + currentApplication.getName() + " events.");
                updateUI();
            } catch (Exception ex) {
                messageBox("Can't subscribe for object updates", ex);
            }
        }
    }

    private void onUnsubscribe() {
        if (currentApplication != null) {
            if (configAccessor.isSubscribedForAppEvents(
                    currentApplication.getObjectDbid())) {
                try {
                    configAccessor.unsubscribe();
                    updateStatusBar("Unsubscribed from "
                            + currentApplication.getName() + " events.");
                    updateUI();
                } catch (Exception ex) {
                    messageBox("Can't unsubscribe from object updates", ex);
                }
            }
        }
    }

    private void onApply() {
        if (currentApplication != null) {
            currentApplication.setName(jtxtfldAppName.getText());
            currentApplication.setOptions(jtreeOptions.getKVListData());
            try {
                currentApplication.save();
                updateStatusBar("Applied changes to "
                        + currentApplication.getName());
            } catch (Exception ex) {
                messageBox("Can't update application data on server", ex);
            }
        }
    }


    private class SubscriptionEventHandler
            implements com.genesyslab.platform.applicationblocks.commons.Action<ConfEvent> {
        public void handle(final ConfEvent obj) {
            if (obj == null || currentApplication == null
                    || obj.getObjectType() != CfgObjectType.CFGApplication
                    || obj.getObjectId() != currentApplication.getObjectDbid()) {
                return;
            }

            if (obj.getCfgObject() instanceof ICfgDelta) {
                currentApplication.update((ICfgDelta) obj.getCfgObject());
            }

            updateUI();

            updateStatusBar("Application " + currentApplication.getName() + " has been updated.");
            messageBox("Received configuration update!", null);
        }
    }
}
