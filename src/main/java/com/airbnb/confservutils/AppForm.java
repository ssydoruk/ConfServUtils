/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.airbnb.confservutils;

import Utils.InfoPanel;
import Utils.Pair;
import static Utils.StringUtils.matching;
import static Utils.Swing.checkBoxSelection;
import Utils.ValuesEditor;
import com.genesyslab.platform.applicationblocks.com.CfgObject;
import com.genesyslab.platform.applicationblocks.com.CfgQuery;
import com.genesyslab.platform.applicationblocks.com.ConfigException;
import com.genesyslab.platform.applicationblocks.com.ICfgObject;
import com.genesyslab.platform.applicationblocks.com.IConfService;
import com.genesyslab.platform.applicationblocks.com.objects.*;
import com.genesyslab.platform.applicationblocks.com.queries.CfgAccessGroupQuery;
import com.genesyslab.platform.applicationblocks.com.queries.CfgActionCodeQuery;
import com.genesyslab.platform.applicationblocks.com.queries.CfgAgentGroupQuery;
import com.genesyslab.platform.applicationblocks.com.queries.CfgAgentLoginQuery;
import com.genesyslab.platform.applicationblocks.com.queries.CfgAlarmConditionQuery;
import com.genesyslab.platform.applicationblocks.com.queries.CfgApplicationQuery;
import com.genesyslab.platform.applicationblocks.com.queries.CfgDNGroupQuery;
import com.genesyslab.platform.applicationblocks.com.queries.CfgDNQuery;
import com.genesyslab.platform.applicationblocks.com.queries.CfgEnumeratorQuery;
import com.genesyslab.platform.applicationblocks.com.queries.CfgEnumeratorValueQuery;
import com.genesyslab.platform.applicationblocks.com.queries.CfgFolderQuery;
import com.genesyslab.platform.applicationblocks.com.queries.CfgGVPIVRProfileQuery;
import com.genesyslab.platform.applicationblocks.com.queries.CfgHostQuery;
import com.genesyslab.platform.applicationblocks.com.queries.CfgIVRPortQuery;
import com.genesyslab.platform.applicationblocks.com.queries.CfgIVRQuery;
import com.genesyslab.platform.applicationblocks.com.queries.CfgObjectiveTableQuery;
import com.genesyslab.platform.applicationblocks.com.queries.CfgPersonQuery;
import com.genesyslab.platform.applicationblocks.com.queries.CfgPlaceGroupQuery;
import com.genesyslab.platform.applicationblocks.com.queries.CfgPlaceQuery;
import com.genesyslab.platform.applicationblocks.com.queries.CfgScriptQuery;
import com.genesyslab.platform.applicationblocks.com.queries.CfgServiceQuery;
import com.genesyslab.platform.applicationblocks.com.queries.CfgSkillQuery;
import com.genesyslab.platform.applicationblocks.com.queries.CfgStatDayQuery;
import com.genesyslab.platform.applicationblocks.com.queries.CfgStatTableQuery;
import com.genesyslab.platform.applicationblocks.com.queries.CfgSwitchQuery;
import com.genesyslab.platform.applicationblocks.com.queries.CfgTenantQuery;
import com.genesyslab.platform.applicationblocks.com.queries.CfgTimeZoneQuery;
import com.genesyslab.platform.applicationblocks.com.queries.CfgTransactionQuery;
import com.genesyslab.platform.applicationblocks.com.queries.CfgTreatmentQuery;
import com.genesyslab.platform.applicationblocks.com.queries.CfgVoicePromptQuery;
import com.genesyslab.platform.commons.collections.KeyValueCollection;
import com.genesyslab.platform.commons.collections.KeyValuePair;
import com.genesyslab.platform.commons.collections.ValueType;
import com.genesyslab.platform.commons.protocol.Message;
import com.genesyslab.platform.commons.protocol.ProtocolException;
import com.genesyslab.platform.configuration.protocol.confserver.events.EventObjectUpdated;
import com.genesyslab.platform.configuration.protocol.types.CfgAppType;
import com.genesyslab.platform.configuration.protocol.types.CfgObjectType;
import com.genesyslab.platform.configuration.protocol.types.CfgScriptType;
import com.genesyslab.platform.configuration.protocol.types.CfgTransactionType;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jidesoft.dialog.ButtonPanel;
import com.jidesoft.dialog.StandardDialog;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.logging.Level;
import java.util.regex.Pattern;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jasypt.util.text.StrongTextEncryptor;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.PTRRecord;
import org.xbill.DNS.Record;
import org.xbill.DNS.ReverseMap;

/**
 *
 * @author stepan_sydoruk
 */
public class AppForm extends javax.swing.JFrame {

    StoredSettings ds = null;
    private static final Logger logger = LogManager.getLogger();
    private final AppForm theForm;

    public void runGui() throws FileNotFoundException, IOException {
        loadConfig();
        setVisible(true);
    }

    private void loadConfig() throws FileNotFoundException, IOException {
        File f = new File(profile);
//                Gson gson = new Gson();
        if (f.exists()) {
            Gson gson = new GsonBuilder()
                    .enableComplexMapKeySerialization()
                    .serializeNulls()
                    .setDateFormat(DateFormat.LONG)
                    .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
                    .setPrettyPrinting()
                    .create();

            try (InputStreamReader reader = new InputStreamReader(new FileInputStream(f))) {
                ds = gson.fromJson(reader, StoredSettings.class);
            }
        } else {
            ds = new StoredSettings();
        }

//<editor-fold defaultstate="collapsed" desc="load users">
        cbUser.removeAllItems();
        for (String user : ds.getUsers()) {
            cbUser.addItem(user);
        }
        if (cbUser.getItemCount() > 0) {
            cbUser.setSelectedIndex(0);
        }
//</editor-fold>
        loadConfigServers();
        pfPassword.setText(getPassword());
        if (cbConfigServer.getItemCount() > 0) {
            cbConfigServer.setSelectedIndex(0);
        }

    }

    private void configServerChanged(ActionEvent e) {
        JComboBox cb = (JComboBox) e.getSource();
        setTitle("ConfigServer query - " + checkBoxSelection(cb));

    }

    private void loadConfigServers() {
//<editor-fold defaultstate="collapsed" desc="load configservers">
        ActionListener[] actionListeners = cbConfigServer.getActionListeners();
        for (ActionListener actionListener : actionListeners) {
            cbConfigServer.removeActionListener(actionListener);
        }
        cbConfigServer.removeAllItems();
        DefaultComboBoxModel mod = (DefaultComboBoxModel) cbConfigServer.getModel();
        for (StoredSettings.ConfServer cs : ds.getConfigServers()) {
            mod.addElement(cs);
        }
        if (mod.getSize() > 0) {
            cbConfigServer.setSelectedIndex(0);
        }

        cbConfigServer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                configServerChanged(e);
            }

        });
//</editor-fold>

    }

    private ValuesEditor confServEditor;

    private String profile;
    public static final int YES_TO_ALL = 100;

    /**
     * Creates new form AppForm
     */
    public AppForm() {
        initComponents();
        theForm = this;
        btCancel.setVisible(false);
        configServerManager = new ConfigServerManager(this);
        componentsEnabled = true;

        pfPassword.setMaximumSize(new Dimension((int) Math.ceil(pfPassword.getMaximumSize().getWidth()),
                (int) Math.ceil(pfPassword.getMinimumSize().getHeight())));

        cbUser.setEditable(true);

        cbUser.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                userEdited(e);
            }
        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                formClosing(e);
            }

        });

        Utils.ScreenInfo.CenterWindow(this);

        textEncryptor = new StrongTextEncryptor();
        textEncryptor.setPassword(ConfigConnection.class.getName());
        connectionStatusChanged();
        jmExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("-1-");
            }
        });

        jmExit.addMenuListener(new MenuListener() {
            @Override
            public void menuSelected(MenuEvent e) {
                saveConfig();
                System.exit(0);
            }

            @Override
            public void menuDeselected(MenuEvent e) {

            }

            @Override
            public void menuCanceled(MenuEvent e) {

            }
        });

    }

    ConfigServerManager configServerManager;

    StrongTextEncryptor textEncryptor;

    private int selectedIndex = -1;

    private void userEdited(ActionEvent e) {
        int index = cbUser.getSelectedIndex();
        if (index >= 0) {
            selectedIndex = index;
        } else if ("comboBoxEdited".equals(e.getActionCommand())) {
            DefaultComboBoxModel model = (DefaultComboBoxModel) cbUser.getModel();
            Object newValue = model.getSelectedItem();
            if (selectedIndex >= 0) {
                model.removeElementAt(selectedIndex);
            }
            model.addElement(newValue);
            cbUser.setSelectedItem(newValue);
            selectedIndex = model.getIndexOf(newValue);
            ds.updateUsers(model);
        }

    }

    private void formClosing(WindowEvent e) {
        saveConfig();
    }

    public void saveConfig() {
        ds.setPassword(textEncryptor.encrypt(new String(pfPassword.getPassword())));
        int selectedIndex1 = cbConfigServer.getSelectedIndex();
        ds.setLastUsedConfigServer(cbConfigServer.getSelectedIndex());

        Gson gson = new GsonBuilder()
                .enableComplexMapKeySerialization()
                .serializeNulls()
                .setDateFormat(DateFormat.LONG)
                .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
                .setPrettyPrinting()
                .setVersion(1.0)
                .create();

        try {
            OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(profile));
            gson.toJson(ds, writer);

            writer.close();
        } catch (FileNotFoundException ex) {
            logger.log(org.apache.logging.log4j.Level.FATAL, ex);
        } catch (IOException ex) {
            logger.log(org.apache.logging.log4j.Level.FATAL, ex);
        }

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton1 = new javax.swing.JButton();
        jpConfServ = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        cbConfigServer = new javax.swing.JComboBox<>();
        btEditConfgServ = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        cbUser = new javax.swing.JComboBox<>();
        jPanel7 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        pfPassword = new javax.swing.JPasswordField();
        jPanel2 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jButton2 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        btCancel = new javax.swing.JButton();
        btDisconnect = new javax.swing.JButton();
        btConnect = new javax.swing.JButton();
        btClearOutput = new javax.swing.JButton();
        jpOutput = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        taOutput = new javax.swing.JTextArea();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        miObjByDBID = new javax.swing.JMenuItem();
        miAppByIP = new javax.swing.JMenuItem();
        miAppByOption = new javax.swing.JMenuItem();
        miObjectByAnnex = new javax.swing.JMenuItem();
        miBusinessAttribute = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        miAnnexSearchReplace = new javax.swing.JMenuItem();
        miAppOptionsReplace = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        miOneORS = new javax.swing.JMenuItem();
        miAllORSs = new javax.swing.JMenuItem();
        jMenu4 = new javax.swing.JMenu();
        miBufferingOff = new javax.swing.JMenuItem();
        miBufferingOn = new javax.swing.JMenuItem();
        jmLoadStrategy = new javax.swing.JMenuItem();
        miRestartService = new javax.swing.JMenuItem();
        jmExit = new javax.swing.JMenu();

        jButton1.setText("jButton1");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Query ConfigServer");
        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.PAGE_AXIS));

        jpConfServ.setBorder(javax.swing.BorderFactory.createTitledBorder("Config Server"));
        jpConfServ.setLayout(new javax.swing.BoxLayout(jpConfServ, javax.swing.BoxLayout.PAGE_AXIS));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.PAGE_AXIS));

        jPanel4.setLayout(new javax.swing.BoxLayout(jPanel4, javax.swing.BoxLayout.LINE_AXIS));

        cbConfigServer.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cbConfigServer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbConfigServerActionPerformed(evt);
            }
        });
        jPanel4.add(cbConfigServer);

        btEditConfgServ.setText("...");
        btEditConfgServ.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btEditConfgServActionPerformed(evt);
            }
        });
        jPanel4.add(btEditConfgServ);

        jPanel1.add(jPanel4);

        jPanel5.setLayout(new javax.swing.BoxLayout(jPanel5, javax.swing.BoxLayout.PAGE_AXIS));

        jPanel6.setLayout(new javax.swing.BoxLayout(jPanel6, javax.swing.BoxLayout.LINE_AXIS));

        jLabel1.setText("CME user");
        jPanel6.add(jLabel1);

        cbUser.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jPanel6.add(cbUser);

        jPanel5.add(jPanel6);

        jPanel7.setLayout(new javax.swing.BoxLayout(jPanel7, javax.swing.BoxLayout.LINE_AXIS));

        jLabel2.setText("Password");
        jPanel7.add(jLabel2);

        pfPassword.setText("jPasswordField1");
        pfPassword.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pfPasswordActionPerformed(evt);
            }
        });
        jPanel7.add(pfPassword);

        jPanel5.add(jPanel7);

        jPanel1.add(jPanel5);

        jpConfServ.add(jPanel1);

        jPanel2.setLayout(new javax.swing.BoxLayout(jPanel2, javax.swing.BoxLayout.LINE_AXIS));

        jPanel8.setMaximumSize(new java.awt.Dimension(32767, 1));

        jButton2.setText("jButton2");

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addContainerGap(475, Short.MAX_VALUE)
                .addComponent(jButton2)
                .addGap(43, 43, 43))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton2)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.add(jPanel8);

        jPanel3.setLayout(new javax.swing.BoxLayout(jPanel3, javax.swing.BoxLayout.LINE_AXIS));

        btCancel.setText("Cancel");
        btCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btCancelActionPerformed(evt);
            }
        });
        jPanel3.add(btCancel);

        btDisconnect.setText("Disconnect");
        btDisconnect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btDisconnectActionPerformed(evt);
            }
        });
        jPanel3.add(btDisconnect);

        btConnect.setText("Connect");
        btConnect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btConnectActionPerformed(evt);
            }
        });
        jPanel3.add(btConnect);

        btClearOutput.setText("Clear output");
        btClearOutput.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btClearOutputActionPerformed(evt);
            }
        });
        jPanel3.add(btClearOutput);

        jPanel2.add(jPanel3);

        jpConfServ.add(jPanel2);

        getContentPane().add(jpConfServ);

        jpOutput.setBorder(javax.swing.BorderFactory.createTitledBorder("Output window"));
        jpOutput.setLayout(new java.awt.BorderLayout());

        taOutput.setEditable(false);
        taOutput.setColumns(20);
        taOutput.setRows(5);
        jScrollPane1.setViewportView(taOutput);

        jpOutput.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        getContentPane().add(jpOutput);

        jMenu1.setText("Query");

        miObjByDBID.setText("Object by DBID");
        miObjByDBID.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miObjByDBIDActionPerformed(evt);
            }
        });
        jMenu1.add(miObjByDBID);

        miAppByIP.setText("application by IP");
        miAppByIP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miAppByIPActionPerformed(evt);
            }
        });
        jMenu1.add(miAppByIP);

        miAppByOption.setText("Applications by option value");
        miAppByOption.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miAppByOptionActionPerformed(evt);
            }
        });
        jMenu1.add(miAppByOption);

        miObjectByAnnex.setText("Object by Annex option");
        miObjectByAnnex.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miObjectByAnnexActionPerformed(evt);
            }
        });
        jMenu1.add(miObjectByAnnex);

        miBusinessAttribute.setText("Business Attribute/Value");
        miBusinessAttribute.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miBusinessAttributeActionPerformed(evt);
            }
        });
        jMenu1.add(miBusinessAttribute);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Update");

        miAnnexSearchReplace.setText("Annex search and replace");
        miAnnexSearchReplace.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miAnnexSearchReplaceActionPerformed(evt);
            }
        });
        jMenu2.add(miAnnexSearchReplace);

        miAppOptionsReplace.setText("App options search and replace");
        miAppOptionsReplace.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miAppOptionsReplaceActionPerformed(evt);
            }
        });
        jMenu2.add(miAppOptionsReplace);

        jMenu3.setText("ORS Cluster");

        miOneORS.setText("Leave 1 ORS");
        miOneORS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miOneORSActionPerformed(evt);
            }
        });
        jMenu3.add(miOneORS);

        miAllORSs.setText("Restore all ORSs");
        miAllORSs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miAllORSsActionPerformed(evt);
            }
        });
        jMenu3.add(miAllORSs);

        jMenu2.add(jMenu3);

        jMenu4.setText("ORS scripts buffering");

        miBufferingOff.setText("Turn buffering off");
        miBufferingOff.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miBufferingOffActionPerformed(evt);
            }
        });
        jMenu4.add(miBufferingOff);

        miBufferingOn.setText("Turn buffering on");
        miBufferingOn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miBufferingOnActionPerformed(evt);
            }
        });
        jMenu4.add(miBufferingOn);

        jMenu2.add(jMenu4);

        jmLoadStrategy.setText("Load ORS strategy");
        jmLoadStrategy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmLoadStrategyActionPerformed(evt);
            }
        });
        jMenu2.add(jmLoadStrategy);

        miRestartService.setText("Restart service");
        miRestartService.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miRestartServiceActionPerformed(evt);
            }
        });
        jMenu2.add(miRestartService);

        jMenuBar1.add(jMenu2);

        jmExit.setText("Exit");
        jmExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmExitActionPerformed(evt);
            }
        });
        jMenuBar1.add(jmExit);

        setJMenuBar(jMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btEditConfgServActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btEditConfgServActionPerformed

        if (confServEditor == null) {
            confServEditor = new ValuesEditor((Window) this.getRootPane().getParent(), "Config Server profiles",
                    "Select %d profiles");

        }
        ArrayList<Object[]> values = new ArrayList<>();
        for (StoredSettings.ConfServer configServer : ds.getConfigServers()) {
            Object[] v = new Object[4];
            v[0] = configServer.getProfile();
            v[1] = configServer.getHost();
            v[2] = configServer.getPort();
            v[3] = configServer.getApp();
            values.add(v);
        }
//        for (DownloadSettings.LFMTHostInstance hi : ds.getLfmtHostInstances()) {
//            values.add(new Object[]{hi.getHost(), hi.getInstance(), hi.getBaseDir()});
//        }
        confServEditor.setData(new Object[]{"Profile", "CS host", "CS port", "CME application"},
                values
        );
        confServEditor.doShow();
        ds.loadConfServs(confServEditor.getData());
        loadConfigServers();
        saveConfig();

      }//GEN-LAST:event_btEditConfgServActionPerformed

    private void btConnectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btConnectActionPerformed
        connectToConfigServer();
        connectionStatusChanged();
    }//GEN-LAST:event_btConnectActionPerformed

    private void pfPasswordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pfPasswordActionPerformed
        System.out.println(pfPassword.getPassword());
    }//GEN-LAST:event_pfPasswordActionPerformed

    private void btDisconnectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btDisconnectActionPerformed
        try {
            configServerManager.disconnect();
        } catch (ProtocolException ex) {
            java.util.logging.Logger.getLogger(AppForm.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalStateException ex) {
            java.util.logging.Logger.getLogger(AppForm.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            java.util.logging.Logger.getLogger(AppForm.class.getName()).log(Level.SEVERE, null, ex);
        }
        connectionStatusChanged();
    }//GEN-LAST:event_btDisconnectActionPerformed

    RequestDialog objByDBID = null;

    private void miObjByDBIDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miObjByDBIDActionPerformed
        if (objByDBID == null) {
            objByDBID = new RequestDialog(this, new ObjByDBID(), (JMenuItem) evt.getSource());
        }
        if (objByDBID.doShow()) {

//                enableComponents(this, false);
            runInThread(new IThreadedFun() {
                @Override
                public void fun() {
                    if (connectToConfigServer()) {
                        ObjByDBID pn = (ObjByDBID) objByDBID.getContentPanel();
                        try {
                            requestOutput("Request: " + pn.getSearchSummary());
                            CfgObjectType t = pn.getSelectedItem();
                            int dbid = pn.getValue();
                            ICfgObject retrieveObject = configServerManager.retrieveObject(t, dbid);
                            if (retrieveObject != null) {
                                if (pn.isFullOutput()) {
                                    requestOutput(retrieveObject.toString());
                                } else {
                                    StringBuilder buf = new StringBuilder();

                                    buf.append("Object type:").append(retrieveObject.getObjectType()).append(" DBID:").append(retrieveObject.getObjectDbid()).append(" name: ").append(getObjName(retrieveObject));
                                    requestOutput(buf.toString());
                                }
                            } else {
                                requestOutput("Not found object DBID:" + dbid + " type:" + t);
                            }
                        } catch (ConfigException ex) {
                            showException("Error", ex);
                        }
                    }
                }

            });

        }
    }//GEN-LAST:event_miObjByDBIDActionPerformed

    private String getObjName(ICfgObject retrieveObject) {
        if (retrieveObject instanceof CfgAccessGroup) {
            return ((CfgAccessGroup) retrieveObject).getObjectPath();
        } else if (retrieveObject instanceof CfgAccessGroupBrief) {
            return ((CfgAccessGroupBrief) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgACE) {
            return ((CfgACE) retrieveObject).getID().toString();
        } else if (retrieveObject instanceof CfgACEID) {
            return ((CfgACEID) retrieveObject).toString();
        } else if (retrieveObject instanceof CfgACL) {
            return ((CfgACL) retrieveObject).toString();
        } else if (retrieveObject instanceof CfgACLID) {
            return ((CfgACLID) retrieveObject).toString();
        } else if (retrieveObject instanceof CfgActionCode) {
            return ((CfgActionCode) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgAddress) {
            return ((CfgAddress) retrieveObject).toString();
        } else if (retrieveObject instanceof CfgAgentGroup) {
            return ((CfgAgentGroup) retrieveObject).getGroupInfo().toString();
        } else if (retrieveObject instanceof CfgAgentInfo) {
            return ((CfgAgentInfo) retrieveObject).toString();
        } else if (retrieveObject instanceof CfgAgentLogin) {
            return ((CfgAgentLogin) retrieveObject).getLoginCode();
        } else if (retrieveObject instanceof CfgAgentLoginInfo) {
            return ((CfgAgentLoginInfo) retrieveObject).getAgentLogin().getLoginCode();
        } else if (retrieveObject instanceof CfgAlarmCondition) {
            return ((CfgAlarmCondition) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgApplication) {
            return ((CfgApplication) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgAppPrototype) {
            return ((CfgAppPrototype) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgAppRank) {
            return ((CfgAppRank) retrieveObject).getAppRank().toString();
        } else if (retrieveObject instanceof CfgAppServicePermission) {
            return ((CfgAppServicePermission) retrieveObject).toString();
        } else if (retrieveObject instanceof CfgCallingList) {
            return ((CfgCallingList) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgCallingListInfo) {
            return ((CfgCallingListInfo) retrieveObject).toString();
        } else if (retrieveObject instanceof CfgCampaign) {
            return ((CfgCampaign) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgCampaignGroup) {
            return ((CfgCampaignGroup) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgCampaignGroupInfo) {
            return ((CfgCampaignGroupInfo) retrieveObject).toString();
        } else if (retrieveObject instanceof CfgConnInfo) {
            return ((CfgConnInfo) retrieveObject).toString();
        } else if (retrieveObject instanceof CfgDelSwitchAccess) {
            return ((CfgDelSwitchAccess) retrieveObject).toString();
        } else if (retrieveObject instanceof CfgDeltaAccessGroup) {
            return ((CfgDeltaAccessGroup) retrieveObject).toString();
        } else if (retrieveObject instanceof CfgDeltaActionCode) {
            return ((CfgDeltaActionCode) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgDeltaAgentGroup) {
            return ((CfgDeltaAgentGroup) retrieveObject).toString();
        } else if (retrieveObject instanceof CfgDeltaAgentInfo) {
            return ((CfgDeltaAgentInfo) retrieveObject).toString();
        } else if (retrieveObject instanceof CfgDeltaAgentLogin) {
            return ((CfgDeltaAgentLogin) retrieveObject).toString();
        } else if (retrieveObject instanceof CfgDeltaAlarmCondition) {
            return ((CfgDeltaAlarmCondition) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgDeltaApplication) {
            return ((CfgDeltaApplication) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgDeltaAppPrototype) {
            return ((CfgDeltaAppPrototype) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgDeltaCallingList) {
            return ((CfgDeltaCallingList) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgDeltaCampaign) {
            return ((CfgDeltaCampaign) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgDeltaCampaignGroup) {
            return ((CfgDeltaCampaignGroup) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgDeltaDN) {
            return ((CfgDeltaDN) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgDeltaDNGroup) {
            return ((CfgDeltaDNGroup) retrieveObject).toString();
        } else if (retrieveObject instanceof CfgDeltaEnumerator) {
            return ((CfgDeltaEnumerator) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgDeltaEnumeratorValue) {
            return ((CfgDeltaEnumeratorValue) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgDeltaField) {
            return ((CfgDeltaField) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgDeltaFilter) {
            return ((CfgDeltaFilter) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgDeltaFolder) {
            return ((CfgDeltaFolder) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgDeltaFormat) {
            return ((CfgDeltaFormat) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgDeltaGroup) {
            return ((CfgDeltaGroup) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgDeltaGVPCustomer) {
            return ((CfgDeltaGVPCustomer) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgDeltaGVPIVRProfile) {
            return ((CfgDeltaGVPIVRProfile) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgDeltaGVPReseller) {
            return ((CfgDeltaGVPReseller) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgDeltaHost) {
            return ((CfgDeltaHost) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgDeltaIVR) {
            return ((CfgDeltaIVR) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgDeltaIVRPort) {
            return ((CfgDeltaIVRPort) retrieveObject).toString();
        } else if (retrieveObject instanceof CfgDeltaObjectiveTable) {
            return ((CfgDeltaObjectiveTable) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgDeltaPerson) {
            return ((CfgDeltaPerson) retrieveObject).toString();
        } else if (retrieveObject instanceof CfgDeltaPersonLastLogin) {
            return ((CfgDeltaPersonLastLogin) retrieveObject).toString();
        } else if (retrieveObject instanceof CfgDeltaPhysicalSwitch) {
            return ((CfgDeltaPhysicalSwitch) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgDeltaPlace) {
            return ((CfgDeltaPlace) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgDeltaPlaceGroup) {
            return ((CfgDeltaPlaceGroup) retrieveObject).toString();
        } else if (retrieveObject instanceof CfgDeltaRole) {
            return ((CfgDeltaRole) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgDeltaScheduledTask) {
            return ((CfgDeltaScheduledTask) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgDeltaScript) {
            return ((CfgDeltaScript) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgDeltaService) {
            return ((CfgDeltaService) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgDeltaSkill) {
            return ((CfgDeltaSkill) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgDeltaStatDay) {
            return ((CfgDeltaStatDay) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgDeltaStatTable) {
            return ((CfgDeltaStatTable) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgDeltaSwitch) {
            return ((CfgDeltaSwitch) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgDeltaTableAccess) {
            return ((CfgDeltaTableAccess) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgDeltaTenant) {
            return ((CfgDeltaTenant) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgDeltaTimeZone) {
            return ((CfgDeltaTimeZone) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgDeltaTransaction) {
            return ((CfgDeltaTransaction) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgDeltaTreatment) {
            return ((CfgDeltaTreatment) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgDeltaVoicePrompt) {
            return ((CfgDeltaVoicePrompt) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgDetectEvent) {
            return ((CfgDetectEvent) retrieveObject).toString();
        } else if (retrieveObject instanceof CfgDN) {
            return ((CfgDN) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgDNAccessNumber) {
            return ((CfgDNAccessNumber) retrieveObject).getNumber();
        } else if (retrieveObject instanceof CfgDNGroup) {
            return ((CfgDNGroup) retrieveObject).getType().toString();
        } else if (retrieveObject instanceof CfgDNInfo) {
            return ((CfgDNInfo) retrieveObject).toString();
        } else if (retrieveObject instanceof CfgEnumerator) {
            return ((CfgEnumerator) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgEnumeratorValue) {
            return ((CfgEnumeratorValue) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgField) {
            return ((CfgField) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgFilter) {
            return ((CfgFilter) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgFolder) {
            return ((CfgFolder) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgFormat) {
            return ((CfgFormat) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgGroup) {
            return ((CfgGroup) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgGVPCustomer) {
            return ((CfgGVPCustomer) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgGVPIVRProfile) {
            return ((CfgGVPIVRProfile) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgGVPReseller) {
            return ((CfgGVPReseller) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgHost) {
            return ((CfgHost) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgID) {
            return ((CfgID) retrieveObject).toString();
        } else if (retrieveObject instanceof CfgIVR) {
            return ((CfgIVR) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgIVRPort) {
            return ((CfgIVRPort) retrieveObject).getPortNumber();
        } else if (retrieveObject instanceof CfgMemberID) {
            return ((CfgMemberID) retrieveObject).toString();
        } else if (retrieveObject instanceof CfgObjectID) {
            return ((CfgObjectID) retrieveObject).toString();
        } else if (retrieveObject instanceof CfgObjectiveTable) {
            return ((CfgObjectiveTable) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgObjectiveTableRecord) {
            return ((CfgObjectiveTableRecord) retrieveObject).toString();
        } else if (retrieveObject instanceof CfgObjectResource) {
            return ((CfgObjectResource) retrieveObject).toString();
        } else if (retrieveObject instanceof CfgOS) {
            return ((CfgOS) retrieveObject).getOStype().toString();
        } else if (retrieveObject instanceof CfgOwnerID) {
            return ((CfgOwnerID) retrieveObject).toString();
        } else if (retrieveObject instanceof CfgParentID) {
            return ((CfgParentID) retrieveObject).toString();
        } else if (retrieveObject instanceof CfgPerson) {
            return ((CfgPerson) retrieveObject).getUserName();
        } else if (retrieveObject instanceof CfgPersonBrief) {
            return ((CfgPersonBrief) retrieveObject).toString();
        } else if (retrieveObject instanceof CfgPersonLastLogin) {
            return ((CfgPersonLastLogin) retrieveObject).toString();
        } else if (retrieveObject instanceof CfgPhones) {
            return ((CfgPhones) retrieveObject).toString();
        } else if (retrieveObject instanceof CfgPhysicalSwitch) {
            return ((CfgPhysicalSwitch) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgPlace) {
            return ((CfgPlace) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgPlaceGroup) {
            return ((CfgPlaceGroup) retrieveObject).toString();
        } else if (retrieveObject instanceof CfgPortInfo) {
            return ((CfgPortInfo) retrieveObject).toString();
        } else if (retrieveObject instanceof CfgRemovalEvent) {
            return ((CfgRemovalEvent) retrieveObject).toString();
        } else if (retrieveObject instanceof CfgResourceID) {
            return ((CfgResourceID) retrieveObject).toString();
        } else if (retrieveObject instanceof CfgRole) {
            return ((CfgRole) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgRoleMember) {
            return ((CfgRoleMember) retrieveObject).toString();
        } else if (retrieveObject instanceof CfgScheduledTask) {
            return ((CfgScheduledTask) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgScript) {
            return ((CfgScript) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgServer) {
            return ((CfgServer) retrieveObject).toString();
        } else if (retrieveObject instanceof CfgServerHostID) {
            return ((CfgServerHostID) retrieveObject).toString();
        } else if (retrieveObject instanceof CfgServerVersion) {
            return ((CfgServerVersion) retrieveObject).toString();
        } else if (retrieveObject instanceof CfgService) {
            return ((CfgService) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgServiceInfo) {
            return ((CfgServiceInfo) retrieveObject).toString();
        } else if (retrieveObject instanceof CfgSkill) {
            return ((CfgSkill) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgSkillLevel) {
            return ((CfgSkillLevel) retrieveObject).toString();
        } else if (retrieveObject instanceof CfgSolutionComponent) {
            return ((CfgSolutionComponent) retrieveObject).toString();
        } else if (retrieveObject instanceof CfgSolutionComponentDefinition) {
            return ((CfgSolutionComponentDefinition) retrieveObject).toString();
        } else if (retrieveObject instanceof CfgStatDay) {
            return ((CfgStatDay) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgStatInterval) {
            return ((CfgStatInterval) retrieveObject).toString();
        } else if (retrieveObject instanceof CfgStatTable) {
            return ((CfgStatTable) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgSubcode) {
            return ((CfgSubcode) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgSwitch) {
            return ((CfgSwitch) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgSwitchAccessCode) {
            return ((CfgSwitchAccessCode) retrieveObject).toString();
        } else if (retrieveObject instanceof CfgTableAccess) {
            return ((CfgTableAccess) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgTenant) {
            return ((CfgTenant) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgTenantBrief) {
            return ((CfgTenantBrief) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgTimeZone) {
            return ((CfgTimeZone) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgTransaction) {
            return ((CfgTransaction) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgTreatment) {
            return ((CfgTreatment) retrieveObject).getName();
        } else if (retrieveObject instanceof CfgUpdatePackageRecord) {
            return ((CfgUpdatePackageRecord) retrieveObject).toString();
        } else if (retrieveObject instanceof CfgVoicePrompt) {
            return ((CfgVoicePrompt) retrieveObject).getName();
        }
        return null;
    }

    RequestDialog appByIP = null;

    private boolean componentsEnabled;
    private HashMap<Component, Boolean> savedEnabled = null;

    public void enableComponentsRecource(Container container, boolean enable) {
        Component[] components = container.getComponents();
        for (Component component : components) {
            savedEnabled.put(component, component.isEnabled());
            component.setVisible(enable);
            if (component instanceof Container) {
                enableComponentsRecource((Container) component, enable);
            }
        }
    }

    public void enableComponents(Container container, boolean enable) {
        if (enable != componentsEnabled) {
            logger.info("enableComponents: " + enable);
            if (savedEnabled != null) {
                for (Map.Entry<Component, Boolean> entry : savedEnabled.entrySet()) {
                    entry.getKey().setVisible(entry.getValue());
                }
                savedEnabled = null;
            } else {
                savedEnabled = new HashMap<>();
                enableComponentsRecource(container, enable);
            }
            componentsEnabled = enable;
//            container.invalidate();
        }
    }

    private void miAppByIPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miAppByIPActionPerformed
        if (appByIP == null) {
            appByIP = new RequestDialog(this, new AppByIP(), (JMenuItem) evt.getSource());
        }
        if (appByIP.doShow()) {
            runAppByIPActionPerformed(evt);
        }
    }//GEN-LAST:event_miAppByIPActionPerformed

    private void btClearOutputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btClearOutputActionPerformed
        taOutput.setText("");
    }//GEN-LAST:event_btClearOutputActionPerformed

    private void jmExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmExitActionPerformed
        // TODO add your handling code here:
        logger.info("jMenu3ActionPerformed pressed " + evt.getActionCommand());
//        theForm.dispose();
        System.exit(0);
    }//GEN-LAST:event_jmExitActionPerformed

    RequestDialog appByOption;

    private void miAppByOptionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miAppByOptionActionPerformed
        if (appByOption == null) {
            appByOption = new RequestDialog(this, new AppByOptions(), (JMenuItem) evt.getSource());
        }
        if (appByOption.doShow()) {
            appByOptionThread((AppByOptions) appByOption.getContentPanel());

        }
    }//GEN-LAST:event_miAppByOptionActionPerformed

    RequestDialog objByAnnex;

    private void miObjectByAnnexActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miObjectByAnnexActionPerformed
        if (objByAnnex == null) {
            objByAnnex = new RequestDialog(this, new ObjByAnnex(), (JMenuItem) evt.getSource());
        }
        JFrame f = this;
        if (objByAnnex.doShow()) {
            runObjByAnnexThread(evt);

        }
    }//GEN-LAST:event_miObjectByAnnexActionPerformed

    RequestDialog bussAttr;

    private void runBussAttrPerformed(ActionEvent evt) {
        if (connectToConfigServer()) {

            BussAttr pn = (BussAttr) bussAttr.getContentPanel();
            try {
                requestOutput("Request: " + pn.getSearchSummary());

//<editor-fold defaultstate="collapsed" desc="iSearchSettings">
                ISearchSettings seearchSettings = new ISearchSettings() {
                    @Override
                    public boolean isCaseSensitive() {
                        return pn.isCaseSensitive();
                    }

                    @Override
                    public boolean isRegex() {
                        return pn.isRegex();
                    }

                    @Override
                    public boolean isFullOutputSelected() {
                        return pn.isFullOutputSelected();
                    }

                    @Override
                    public boolean isSearchAll() {
                        return false;
                    }

                    @Override
                    public String getAllSearch() {
                        return null;
                    }

                    @Override
                    public String getSection() {
                        return null;
                    }

                    @Override
                    public String getObjName() {
                        return (pn.getName() == null) ? null : pn.getName();
                    }

                    @Override
                    public String getOption() {
                        return null;
                    }

                    @Override
                    public String getValue() {
                        return null;
                    }

                };
//</editor-fold>

                if (pn.iscbAttrSelected()) {
                    CfgEnumeratorQuery query = new CfgEnumeratorQuery(configServerManager.getService());

                    findObjects(
                            query,
                            CfgEnumerator.class,
                            new IKeyValueProperties() {
                        @Override
                        public KeyValueCollection getProperties(CfgObject obj) {
                            return ((CfgEnumerator) obj).getUserProperties();
                        }

                        @Override
                        public Collection<String> getName(CfgObject obj) {
                            Collection<String> ret = new ArrayList<>();
                            ret.add(((CfgEnumerator) obj).getName());
                            ret.add(((CfgEnumerator) obj).getDescription());
                            ret.add(((CfgEnumerator) obj).getDisplayName());
                            return ret;
                        }
                    },
                            new FindWorker(seearchSettings), true, null);

                }
                if (pn.iscbAttrValueSelected()) {
                    CfgEnumeratorValueQuery query = new CfgEnumeratorValueQuery(configServerManager.getService());

                    findObjects(
                            query,
                            CfgEnumeratorValue.class,
                            new IKeyValueProperties() {
                        @Override
                        public KeyValueCollection getProperties(CfgObject obj) {
                            return ((CfgEnumeratorValue) obj).getUserProperties();
                        }

                        @Override
                        public Collection<String> getName(CfgObject obj) {
                            Collection<String> ret = new ArrayList<>();
                            ret.add(((CfgEnumeratorValue) obj).getName());
                            ret.add(((CfgEnumeratorValue) obj).getDescription());
                            ret.add(((CfgEnumeratorValue) obj).getDisplayName());
                            return ret;
                        }
                    },
                            new FindWorker(seearchSettings), true, null);

                }

            } catch (ConfigException ex) {
                showException("Error", ex);

            } catch (InterruptedException ex) {
                java.util.logging.Logger.getLogger(AppForm.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        }

        logger.info(
                "affirm");
    }

    private void miBusinessAttributeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miBusinessAttributeActionPerformed
        if (bussAttr == null) {
            bussAttr = new RequestDialog(this, new BussAttr(), (JMenuItem) evt.getSource());
        }
        JFrame f = this;
        if (bussAttr.doShow()) {
            runInThread(new IThreadedFun() {
                @Override
                public void fun() {
                    runBussAttrPerformed(evt);

                }
            });

        }        // TODO add your handling code here:
    }//GEN-LAST:event_miBusinessAttributeActionPerformed

    private void btCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btCancelActionPerformed
        if (worker != null && !worker.isDone()) {
            worker.cancel(true);
        }
    }//GEN-LAST:event_btCancelActionPerformed

    private void cbConfigServerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbConfigServerActionPerformed
        // TODO add your handling code here:

    }//GEN-LAST:event_cbConfigServerActionPerformed

    RequestDialog annexReplace;
    AnnexReplace panelAnnexReplace;

    RequestDialog appOptionsChange;
    AppOptionsChange panelAppOptionsChange;

    RequestDialog appRestartServices;
    RestartServices panelRestartServices;

    InfoPanel infoDialog = null;
    ObjectFound pn1 = null;

    public int showYesNoPanel(String infoMsg, String msg) {

        if (infoDialog == null) {
            pn1 = new ObjectFound();

            infoDialog = new InfoPanel(theForm, "Please choose", pn1, JOptionPane.YES_NO_CANCEL_OPTION);
            infoDialog.addButton("Yes to all", YES_TO_ALL);
        }
        pn1.setInfoMsg(infoMsg);
        pn1.setText(msg);

        infoDialog.showModal();

        return infoDialog.getDialogResult();
    }
    boolean yesToAll;
    UpdateCFGObjectProcessor upd = null;

    private KeyValueCollection getAllValuesInSection(CfgObject obj, ISearchSettings seearchSettings) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void miAnnexSearchReplaceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miAnnexSearchReplaceActionPerformed
        yesToAll = false;
        upd = null;

        if (annexReplace == null) {
            panelAnnexReplace = new AnnexReplace(this);
            annexReplace = new RequestDialog(this, panelAnnexReplace, (JMenuItem) evt.getSource());
        }

        if (annexReplace.doShow()) {

//                showYesNoPanel(pn1.getSearchSummaryHTML(), "something" + "\n kv: " + "something"+"\nsomething" + "\n kv: " + "something"+"\nsomething" + "\n kv: " + "something"+"\nsomething" + "\n kv: " + "something"+"\nsomething" + "\n kv: " + "something"+"\nsomething" + "\n kv: " + "something"+"\nsomething" + "\n kv: " + "something"+"\nsomething" + "\n kv: " + "something");
//                if(0==1)
//                    return;
            if (!panelAnnexReplace.checkParameters()) {
                return;
            }
            if (connectToConfigServer()) {

                AnnexReplace pn = (AnnexReplace) annexReplace.getContentPanel();
//                showYesNoPanel(pn.getSearchSummary(2), "something" + "\n kv: " + "something");
//                if(0==1)
//                    return;
//                requestOutput("Request: " + pn.getSearchSummary());

//                showYesNoPanel(pn.getSearchSummary());
                for (CfgObjectType value : pn.getSelectedObjectTypes()) {
                    try {
                        if (!doTheSearch(value, pn, false, true, new ICfgObjectFoundProc() {
                            @Override
                            public boolean proc(CfgObject obj, KeyValueCollection kv, int current, int total) {
                                logger.info("found " + obj.toString() + "\n kv: " + kv.toString());
//                                int showYesNoPanel = showYesNoPanel(pn.getSearchSummary(), obj.toString() + "\n kv: " + kv.toString());

                                try {
                                    if (yesToAll) {
                                        if (upd != null) {
                                            upd.updateObj(pn, obj, kv, configServerManager);
                                        }

                                    } else {
                                        upd = new UpdateCFGObjectProcessor(configServerManager, obj.getObjectType(), theForm);
                                        String estimateUpdateObj = upd.estimateUpdateObj(pn, obj, kv, configServerManager);
                                        switch (showYesNoPanel(pn.getSearchSummaryHTML(), "Object " + current + " of matched " + total
                                                + "\ntoUpdate: \n----------------------\n" + estimateUpdateObj
                                                + "\n-->\n" + obj.toString() + "\n\t kv: " + kv.toString()
                                        )) {
                                            case YES_TO_ALL:
                                                if (JOptionPane.showConfirmDialog(theForm,
                                                        "Are you sure you want to modify this and all following found objects?",
                                                        "Please confirm",
                                                        JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {

                                                    yesToAll = true;
                                                    upd.updateObj(pn, obj, kv, configServerManager);
                                                    break;
                                                }
                                                break;

                                            case JOptionPane.YES_OPTION:
                                                upd.updateObj(pn, obj, kv, configServerManager);
                                                break;

                                            case JOptionPane.NO_OPTION:
                                                break;

                                            case JOptionPane.CANCEL_OPTION:
                                                return false;
                                        }
                                    }
                                } catch (ProtocolException protocolException) {
                                    showError("Exception while updating: " + protocolException.getMessage());
                                } catch (HeadlessException headlessException) {
                                    showError("Exception while updating: " + headlessException.getMessage());
                                }

                                return true;
                            }
                        })) {

                            break;
                        }
                    } catch (ConfigException ex) {
                        java.util.logging.Logger.getLogger(AppForm.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (InterruptedException ex) {
                        java.util.logging.Logger.getLogger(AppForm.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

            }

        }
    }//GEN-LAST:event_miAnnexSearchReplaceActionPerformed

    private void miAllORSsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miAllORSsActionPerformed
        modifyCluster(false);
    }//GEN-LAST:event_miAllORSsActionPerformed

    private void modifyCluster(boolean oneORS) {
        upd = null;
        yesToAll = false;

        FindObject objName = getObjName(CfgTransaction.class.getSimpleName() + " type " + CfgTransactionType.CFGTRTList);

        if (objName == null) {
            return;
        }

        if (connectToConfigServer()) {

            ISearchSettings seearchSettings = new ISearchSettings() {
                @Override
                public boolean isCaseSensitive() {
                    return objName.isCaseSensitive();
                }

                @Override
                public boolean isRegex() {
                    return objName.isRegex();
                }

                @Override
                public boolean isFullOutputSelected() {
                    return false;
                }

                @Override
                public boolean isSearchAll() {
                    return false;
                }

                @Override
                public String getAllSearch() {
                    return null;
                }

                @Override
                public String getSection() {
                    if (objName.isRegex()) {
                        return "^cluster$";
                    } else {
                        return "cluster";
                    }
                }

                @Override
                public String getObjName() {
                    return objName.getName();
                }

                @Override
                public String getOption() {
                    return null;
                }

                @Override
                public String getValue() {
                    return null;
                }

            };

            class AUpdateSettings implements IUpdateSettings {

                private boolean oneActive = false;

                public AUpdateSettings() {
                }

                @Override
                public boolean isMakeBackup() {
                    return true;
                }

                @Override
                public IUpdateSettings.UpdateAction getUpdateAction() {
                    return IUpdateSettings.UpdateAction.RENAME_SECTION;
                }

                @Override
                public String replaceWith(String currentValue) {
                    if (oneORS) {
                        if (!oneActive) {
                            oneActive = true;
                            if (currentValue.startsWith(UpdateCFGObjectProcessor.BACKUP_PREFIX)) {
                                return currentValue.substring(UpdateCFGObjectProcessor.BACKUP_PREFIX.length());
                            } else {
                                return currentValue;
                            }
                        }
                        return currentValue;
                    } else {
                        return UpdateCFGObjectProcessor.uncommented(currentValue);
                    }

                }

                public void setOneActive(boolean oneActive) {
                    this.oneActive = oneActive;
                }

                @Override
                public String getReplaceKey(String currentValue) {
                    if (oneORS) {
                        if (!oneActive) {
                            oneActive = true;
                            if (currentValue.startsWith(UpdateCFGObjectProcessor.BACKUP_PREFIX)) {
                                return currentValue.substring(UpdateCFGObjectProcessor.BACKUP_PREFIX.length());
                            } else {
                                return currentValue;
                            }
                        }
                        return UpdateCFGObjectProcessor.getCommentedKey(currentValue);
                    } else {
                        return UpdateCFGObjectProcessor.uncommented(currentValue);
                    }
                }

                @Override
                public Collection<UserProperties> getAddedKVP() {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }
            }

            AUpdateSettings us = new AUpdateSettings();

            ICfgObjectFoundProc foundProc = new ICfgObjectFoundProc() {
                @Override
                public boolean proc(CfgObject obj, KeyValueCollection kv, int current, int total) {
//                    kv = getAllValuesInSection(obj, seearchSettings);
                    kv = new KeyValueCollection();
                    kv.addList(seearchSettings.getSection(), ((CfgTransaction) obj).getUserProperties().getList(seearchSettings.getSection()));
//                            ((CfgTransaction) obj).getUserProperties().getList(seearchSettings.getSection());
                    logger.info("found " + obj.toString() + "\n kv: " + kv.toString());

//                                int showYesNoPanel = showYesNoPanel(pn.getSearchSummary(), obj.toString() + "\n kv: " + kv.toString());
                    try {
                        if (yesToAll) {
                            us.setOneActive(false);
                            upd.updateObj(us, obj, kv, configServerManager);
                        } else {
                            us.setOneActive(false);
                            upd = new UpdateCFGObjectProcessor(configServerManager, obj.getObjectType(), theForm);
                            String estimateUpdateObj = upd.estimateUpdateObj(us, obj, kv, configServerManager);
                            switch (showYesNoPanel(seearchSettings.toString(), "Object " + current + " of matched " + total
                                    + "\ntoUpdate: \n----------------------\n" + estimateUpdateObj
                                    + "\n-->\n" + obj.toString() + "\n\t kv: " + kv.toString()
                            )) {
                                case YES_TO_ALL:
                                    if (JOptionPane.showConfirmDialog(theForm,
                                            "Are you sure you want to modify this and all following found objects?",
                                            "Please confirm",
                                            JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {

                                        yesToAll = true;
                                        us.setOneActive(false);
                                        upd.updateObj(us, obj, kv, configServerManager);
                                        break;
                                    }
                                    break;

                                case JOptionPane.YES_OPTION:
                                    us.setOneActive(false);
                                    upd.updateObj(us, obj, kv, configServerManager);
                                    break;

                                case JOptionPane.NO_OPTION:
                                    break;

                                case JOptionPane.CANCEL_OPTION:
                                    return false;
                            }
                        }
                    } catch (ProtocolException protocolException) {
                        showError("Exception while updating: " + protocolException.getMessage());
                    } catch (HeadlessException headlessException) {
                        showError("Exception while updating: " + headlessException.getMessage());
                    }

                    return true;
                }

            };

            CfgObjectType value = CfgObjectType.CFGTransaction;

            try {

                CfgTransactionQuery query = new CfgTransactionQuery();
//                setQueryNameFilter(query, objName.getName(), objName.isRegex());
                query.setObjectType(CfgTransactionType.CFGTRTList);

                if (findObjects(
                        query,
                        CfgTransaction.class,
                        new IKeyValueProperties() {
                    @Override
                    public KeyValueCollection getProperties(CfgObject obj) {
                        return ((CfgTransaction) obj).getUserProperties();
                    }

                    @Override
                    public Collection<String> getName(CfgObject obj) {
                        Collection<String> ret = new ArrayList<>();
                        ret.add(((CfgTransaction) obj).getName());
                        return ret;
                    }
                },
                        new FindWorker(seearchSettings), true, foundProc)) {

                }

            } catch (ConfigException ex) {
                java.util.logging.Logger.getLogger(AppForm.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InterruptedException ex) {
                java.util.logging.Logger.getLogger(AppForm.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

    private void miOneORSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miOneORSActionPerformed
        modifyCluster(true);
    }//GEN-LAST:event_miOneORSActionPerformed

    private void miBufferingOffActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miBufferingOffActionPerformed
        strategyBuffering(false, evt);
    }//GEN-LAST:event_miBufferingOffActionPerformed

    private void miBufferingOnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miBufferingOnActionPerformed
        strategyBuffering(true, evt);

    }//GEN-LAST:event_miBufferingOnActionPerformed

    private void miAppOptionsReplaceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miAppOptionsReplaceActionPerformed
        yesToAll = false;
        upd = null;

        if (appOptionsChange == null) {
            panelAppOptionsChange = new AppOptionsChange(this);
            appOptionsChange = new RequestDialog(this, panelAppOptionsChange, (JMenuItem) evt.getSource());
        }

        if (appOptionsChange.doShow()) {
//                showYesNoPanel(pn1.getSearchSummaryHTML(), "something" + "\n kv: " + "something"+"\nsomething" + "\n kv: " + "something"+"\nsomething" + "\n kv: " + "something"+"\nsomething" + "\n kv: " + "something"+"\nsomething" + "\n kv: " + "something"+"\nsomething" + "\n kv: " + "something"+"\nsomething" + "\n kv: " + "something"+"\nsomething" + "\n kv: " + "something");
//                if(0==1)
//                    return;
            if (!panelAppOptionsChange.checkParameters()) {
                return;
            }
            if (connectToConfigServer()) {

                ICfgObjectFoundProc foundProc = new ICfgObjectFoundProc() {
                    @Override
                    public boolean proc(CfgObject obj, KeyValueCollection kv, int current, int total) {
//                    kv = getAllValuesInSection(obj, seearchSettings);
//                    kv = new KeyValueCollection();
//                    kv.addList(seearchSettings.getSection(), ((CfgScript) obj).getUserProperties().getList(seearchSettings.getSection()));
//                            ((CfgTransaction) obj).getUserProperties().getList(seearchSettings.getSection());
                        logger.info("found obj " + getObjName(obj) + " type " + obj.getObjectType() + " DBID:" + obj.getObjectDbid() + " at " + obj.getObjectPath());

//                                int showYesNoPanel = showYesNoPanel(pn.getSearchSummary(), obj.toString() + "\n kv: " + kv.toString());
                        try {
                            if (yesToAll) {
                                upd.updateObj(panelAppOptionsChange, obj, kv, configServerManager);
                            } else {
                                upd = new UpdateCFGObjectProcessor(configServerManager, obj.getObjectType(), theForm);
                                upd.setCustomKVPProc(new UpdateCFGObjectProcessor.ICustomKVP() {
                                    @Override
                                    public KeyValueCollection getCustomKVP(CfgObject _obj) {
                                        return ((CfgApplication) _obj).getOptions();
                                    }
                                });
                                upd.setPropKeys("changedOptions", "deletedOptions", "options");
                                String estimateUpdateObj = upd.estimateUpdateObj(panelAppOptionsChange, obj, kv, configServerManager);
                                if (estimateUpdateObj != null) //
                                {
                                    switch (showYesNoPanel(panelAppOptionsChange.getSearchSummaryHTML(), "Object " + current + " of matched " + total
                                            + "\ntoUpdate: \n----------------------\n" + estimateUpdateObj
                                            + "\n-->\n" + obj.toString() + "\n\t kv: " + kv.toString()
                                    )) {
                                        case YES_TO_ALL:
                                            if (JOptionPane.showConfirmDialog(theForm,
                                                    "Are you sure you want to modify this and all following found objects?",
                                                    "Please confirm",
                                                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {

                                                yesToAll = true;
                                                upd.updateObj(panelAppOptionsChange, obj, kv, configServerManager);
                                                break;
                                            }
                                            break;

                                        case JOptionPane.YES_OPTION:
                                            upd.updateObj(panelAppOptionsChange, obj, kv, configServerManager);
                                            break;

                                        case JOptionPane.NO_OPTION:
                                            break;

                                        case JOptionPane.CANCEL_OPTION:
                                            return false;
                                    }
                                }
                            }
                        } catch (ProtocolException protocolException) {
                            showError("Exception while updating: " + protocolException.getMessage());
                        } catch (HeadlessException headlessException) {
                            showError("Exception while updating: " + headlessException.getMessage());
                        }

                        return true;
                    }

                };

                try {
                    CfgApplicationQuery query = new CfgApplicationQuery();

                    CfgAppType selectedAppType = panelAppOptionsChange.getSelectedAppType();
                    if (selectedAppType != null) {
                        query.setAppType(selectedAppType);
                    }
//                    String n = panelAppOptionsChange.getObjName();
//                    if (panelAppOptionsChange.isCaseSensitive() && n != null) {
//                        query.setName(n);
//                    }

                    if (findObjects(
                            query,
                            CfgApplication.class,
                            new IKeyValueProperties() {
                        @Override
                        public KeyValueCollection getProperties(CfgObject obj) {
                            return ((CfgApplication) obj).getOptions();
                        }

                        @Override
                        public Collection<String> getName(CfgObject obj) {
                            Collection<String> ret = new ArrayList<>();
                            ret.add(((CfgApplication) obj).getName());
                            return ret;
                        }
                    },
                            new FindWorker(panelAppOptionsChange), true, foundProc)) {

                    }

                } catch (ConfigException ex) {
                    java.util.logging.Logger.getLogger(AppForm.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InterruptedException ex) {
                    java.util.logging.Logger.getLogger(AppForm.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        }
    }//GEN-LAST:event_miAppOptionsReplaceActionPerformed

    RequestDialog loadORSStrategy;
    LoadORSStrategy panelLoadORSStrategy;

    private void jmLoadStrategyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmLoadStrategyActionPerformed
        yesToAll = false;
        upd = null;

        if (loadORSStrategy == null) {
            panelLoadORSStrategy = new LoadORSStrategy(this);
            loadORSStrategy = new RequestDialog(this, panelLoadORSStrategy, (JMenuItem) evt.getSource());
        }

        if (connectToConfigServer()) {
            if (loadORSStrategy.doShow("Load ORS strategies on routing points", panelLoadORSStrategy)) {
                panelLoadORSStrategy.doUpdate(configServerManager);
            }
        }
    }//GEN-LAST:event_jmLoadStrategyActionPerformed

    private void miRestartServiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miRestartServiceActionPerformed
        yesToAll = false;
        upd = null;

        if (appRestartServices == null) {
            panelRestartServices = new RestartServices(this);
            appRestartServices = new RequestDialog(this, panelRestartServices, (JMenuItem) evt.getSource());
        }

        if (appRestartServices.doShow()) {
//                showYesNoPanel(pn1.getSearchSummaryHTML(), "something" + "\n kv: " + "something"+"\nsomething" + "\n kv: " + "something"+"\nsomething" + "\n kv: " + "something"+"\nsomething" + "\n kv: " + "something"+"\nsomething" + "\n kv: " + "something"+"\nsomething" + "\n kv: " + "something"+"\nsomething" + "\n kv: " + "something"+"\nsomething" + "\n kv: " + "something");
//                if(0==1)
//                    return;
            if (!panelRestartServices.checkParameters()) {
                return;
            }

            if (connectToConfigServer()) {
                CfgApplication appNew = new CfgApplication(configServerManager.getService());
                String remoteCommand = panelRestartServices.getRemoteCommand();

                if (StringUtils.isBlank(remoteCommand)) {
                    JOptionPane.showMessageDialog(theForm, "Remote command cannot be empty", "Cannot proceed", JOptionPane.ERROR_MESSAGE);
                    return;

                } else {
                    String[] split = StringUtils.split(remoteCommand);
                    if (split.length > 0) {
                        appNew.setCommandLine(split[0]);
                        appNew.setCommandLineArguments((split.length > 1) ? StringUtils.join(ArrayUtils.subarray(split, 1, split.length), " ") : ".");

                    }
                }

                String remoteRestartScript = panelRestartServices.getStatusScript();
                if (StringUtils.isBlank(remoteRestartScript)) {
                    JOptionPane.showMessageDialog(theForm, "Status script parameter cannot be empty", "Cannot proceed", JOptionPane.ERROR_MESSAGE);
                    return;

                }

                ISearchSettings seearchSettings = new ISearchSettings() {
                    @Override
                    public boolean isCaseSensitive() {
                        return panelRestartServices.isCaseSensitive();
                    }

                    @Override
                    public boolean isRegex() {
                        return panelRestartServices.isRegex();
                    }

                    @Override
                    public boolean isFullOutputSelected() {
                        return false;
                    }

                    @Override
                    public boolean isSearchAll() {
                        return false;
                    }

                    @Override
                    public String getAllSearch() {
                        return null;
                    }

                    @Override
                    public String getSection() {
                        return null;
                    }

                    @Override
                    public String getObjName() {
//                        return panelRestartServices.getName();
                        return "esv1_wbagent_ors6_01";
                    }

                    @Override
                    public String getOption() {
                        return "start_command";
                    }

                    @Override
                    public String getValue() {
                        return null;
                    }

                };

                IUpdateSettings us = new IUpdateSettings() {

                    @Override
                    public boolean isMakeBackup() {
                        return true;
                    }

                    @Override
                    public IUpdateSettings.UpdateAction getUpdateAction() {
                        return IUpdateSettings.UpdateAction.RENAME_SECTION;
                    }

                    @Override
                    public String replaceWith(String currentValue) {
                        return currentValue + "1";
                    }

                    @Override
                    public String getReplaceKey(String currentValue) {
                        return UpdateCFGObjectProcessor.getCommentedKey(currentValue);
                    }

                    @Override
                    public Collection<UserProperties> getAddedKVP() {
                        Collection<UserProperties> ret = new ArrayList<>();
                        ret.add(new UserProperties("a", "b", "c"));
                        return ret;
                    }
                };

                class RestoreSettings implements IUpdateSettings {

                    private UserProperties up;

                    @Override
                    public boolean isMakeBackup() {
                        return true;
                    }

                    @Override
                    public IUpdateSettings.UpdateAction getUpdateAction() {
                        return IUpdateSettings.UpdateAction.ADD_OPTION_FORCE;
                    }

                    @Override
                    public String replaceWith(String currentValue) {
                        return UpdateCFGObjectProcessor.uncommented(currentValue);
                    }

                    @Override
                    public String getReplaceKey(String currentValue) {
                        return UpdateCFGObjectProcessor.uncommented(currentValue);
                    }

                    @Override
                    public Collection<UserProperties> getAddedKVP() {
//                        ret.add(new UserProperties(kv., profile, profile))s
                        if (up != null) {
                            Collection<UserProperties> ret = new ArrayList<>();
                            ret.add(up);
                            return ret;
                        } else {
                            return null;
                        }
                    }

                    public void replaceKVP(KeyValueCollection _kv) {
                        up = null;
                        for (Object object : _kv) {
                            if (object instanceof KeyValuePair) {
                                KeyValuePair kvp = (KeyValuePair) object;
                                String section = kvp.getStringKey();
                                if (section.equals("start_stop")) {
                                    Object value = kvp.getValue();
                                    ValueType valueType = kvp.getValueType();
                                    if (valueType == ValueType.TKV_LIST) {
                                        for (Object _kvInstance : (KeyValueCollection) value) {
                                            KeyValuePair kvInstance = (KeyValuePair) _kvInstance;
                                            if (kvInstance.getStringKey().equals("start_command")) {
                                                up = new UserProperties(section, kvInstance.getStringKey(), kvInstance.getValueAsString());
                                                return;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                ;

                RestoreSettings usRestore = new RestoreSettings();

                CfgApplication appSaved = new CfgApplication(configServerManager.getService());

                upd = new UpdateCFGObjectProcessor(configServerManager, CfgObjectType.CFGApplication, theForm);

//                            logger.debug((new Gson()).toJson(app));
//                upd.setPropKeys("changedOptions", "deletedOptions", "options");
                ICfgObjectFoundProc foundProc = new ICfgObjectFoundProc() {
                    @Override
                    public boolean proc(CfgObject obj, KeyValueCollection kv, int current, int total) {
//                    kv = getAllValuesInSection(obj, seearchSettings);
//                    kv = new KeyValueCollection();
//                    kv.addList(seearchSettings.getSection(), ((CfgScript) obj).getUserProperties().getList(seearchSettings.getSection()));
//                            ((CfgTransaction) obj).getUserProperties().getList(seearchSettings.getSection());
                        logger.info("found obj " + getObjName(obj) + " type " + obj.getObjectType() + " DBID:" + obj.getObjectDbid() + " at " + obj.getObjectPath());

//                                int showYesNoPanel = showYesNoPanel(pn.getSearchSummary(), obj.toString() + "\n kv: " + kv.toString());
                        try {
                            if (yesToAll) {
                                upd.updateObj(us, obj, kv, configServerManager, appNew);
                            } else {

                                String estimateUpdateObj = upd.estimateUpdateObj(us, obj, kv, configServerManager, appNew);
                                if (estimateUpdateObj != null) //
                                {
                                    switch (showYesNoPanel(panelRestartServices.getSearchSummaryHTML(), "Object " + current + " of matched " + total
                                            + "\ntoUpdate: \n----------------------\n" + estimateUpdateObj
                                            + "\n-->\n" + obj.toString() + "\n\t kv: " + kv.toString()
                                    )) {
                                        case YES_TO_ALL:
                                            if (JOptionPane.showConfirmDialog(theForm,
                                                    "Are you sure you want to modify this and all following found objects?",
                                                    "Please confirm",
                                                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {

                                                yesToAll = true;
                                                upd.updateObj(us, obj, kv, configServerManager, appNew);
                                                break;
                                            }
                                            break;

                                        case JOptionPane.YES_OPTION:
                                            appSaved.setCommandLine(((CfgApplication) obj).getCommandLine());
                                            appSaved.setCommandLineArguments(((CfgApplication) obj).getCommandLineArguments());
                                            usRestore.replaceKVP(kv);
                                            Message updateObj = upd.updateObj(us, obj, kv, configServerManager, appNew);
                                            if (updateObj != null && updateObj.messageId() == EventObjectUpdated.ID) {
                                                logger.debug("object updated");

                                                try {
                                                    String remoteCmd = remoteRestartScript + " " + ((CfgApplication) obj).getName();
                                                    requestOutput("Executing: " + remoteRestartScript);
                                                    Pair<ArrayList<String>, ArrayList<String>> executeCommand = Utils.UnixProcess.ExtProcess.executeCommand(remoteCmd, true, true);
                                                    if (executeCommand != null) {
                                                        ArrayList<String> lines;
                                                        if ((lines = executeCommand.getKey()) != null) {
                                                            requestOutput("Stdout: " + StringUtils.join(lines));
                                                        }
                                                        if ((lines = executeCommand.getValue()) != null) {
                                                            requestOutput("Stderr: " + StringUtils.join(lines));
                                                        }
                                                    }
                                                } catch (IOException ex) {
                                                    java.util.logging.Logger.getLogger(AppForm.class.getName()).log(Level.SEVERE, null, ex);
                                                } catch (InterruptedException ex) {
                                                    java.util.logging.Logger.getLogger(AppForm.class.getName()).log(Level.SEVERE, null, ex);
                                                }
                                                updateObj = upd.updateObj(usRestore, obj, kv, configServerManager, appSaved);
                                                if (updateObj != null
                                                        && updateObj.messageId() == EventObjectUpdated.ID) {
                                                    logger.debug("object restored");
                                                } else {
                                                    showError("Failed to restore object: " + ((updateObj != null) ? updateObj : null));
                                                    return false;
                                                }
                                            } else {
                                                showError("Failed to update object: " + ((updateObj != null) ? updateObj : null));
                                                return false;
                                            }
                                            break;

                                        case JOptionPane.NO_OPTION:
                                            break;

                                        case JOptionPane.CANCEL_OPTION:
                                            return false;
                                    }
                                }
                            }
                        } catch (ProtocolException protocolException) {
                            showError("Exception while updating: " + protocolException.getMessage());
                            return false;
                        } catch (HeadlessException headlessException) {
                            showError("Exception while updating: " + headlessException.getMessage());
                            return false;
                        }

                        return true;
                    }

                };

                try {
                    CfgApplicationQuery query = new CfgApplicationQuery();

                    CfgAppType selectedAppType = panelRestartServices.getSelectedAppType();
                    if (selectedAppType != null) {
                        query.setAppType(selectedAppType);
                    }
//                    String n = panelRestartServices.getObjName();
//                    if (panelRestartServices.isCaseSensitive() && n != null) {
//                        query.setName(n);
//                    }

                    if (findObjects(
                            query,
                            CfgApplication.class,
                            new IKeyValueProperties() {
                        @Override
                        public KeyValueCollection getProperties(CfgObject obj) {
                            return ((CfgApplication) obj).getUserProperties();
                        }

                        @Override
                        public Collection<String> getName(CfgObject obj) {
                            Collection<String> ret = new ArrayList<>();
                            ret.add(((CfgApplication) obj).getName());
                            return ret;
                        }
                    },
                            new FindWorker(seearchSettings), true, foundProc)) {

                    }

                } catch (ConfigException ex) {
                    java.util.logging.Logger.getLogger(AppForm.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InterruptedException ex) {
                    java.util.logging.Logger.getLogger(AppForm.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        }
    }//GEN-LAST:event_miRestartServiceActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btCancel;
    private javax.swing.JButton btClearOutput;
    private javax.swing.JButton btConnect;
    private javax.swing.JButton btDisconnect;
    private javax.swing.JButton btEditConfgServ;
    private javax.swing.JComboBox<String> cbConfigServer;
    private javax.swing.JComboBox<String> cbUser;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JMenu jmExit;
    private javax.swing.JMenuItem jmLoadStrategy;
    private javax.swing.JPanel jpConfServ;
    private javax.swing.JPanel jpOutput;
    private javax.swing.JMenuItem miAllORSs;
    private javax.swing.JMenuItem miAnnexSearchReplace;
    private javax.swing.JMenuItem miAppByIP;
    private javax.swing.JMenuItem miAppByOption;
    private javax.swing.JMenuItem miAppOptionsReplace;
    private javax.swing.JMenuItem miBufferingOff;
    private javax.swing.JMenuItem miBufferingOn;
    private javax.swing.JMenuItem miBusinessAttribute;
    private javax.swing.JMenuItem miObjByDBID;
    private javax.swing.JMenuItem miObjectByAnnex;
    private javax.swing.JMenuItem miOneORS;
    private javax.swing.JMenuItem miRestartService;
    private javax.swing.JPasswordField pfPassword;
    private javax.swing.JTextArea taOutput;
    // End of variables declaration//GEN-END:variables

    public void setProfile(String sGUIProfile) {
        this.profile = sGUIProfile;
    }

    private boolean connectToConfigServer() {
        /*
            String configServerHost = "10.61.6.55";
//        String configServerHost="esv1-c-ppe-46.ivr.airbnb.biz";
            int configServerPort = 2025;
            String configServerUser = "stepan.sydoruk@ext.airbnb.com.admin";
            String configServerPass = "CCbljher72~pAOk6NiP";

            String tempAppName = "AppName4Test"; // Uniq name for temp app to be created,
            // changed and deleted.
            String tempAgentName = "AgentName4Test"; // Uniq name for temp agent to be created,
            // changed and deleted.

            logger.info("ComJavaQuickStart started execution.");

            String someAppName = "default";
//        if (someAppName == null || someAppName.equals("")) {
//            someAppName = "default";
//        }
//        configServerHost = properties.getString("ConfServerHost");
//        configServerPort = Integer.parseInt(properties.getString("ConfServerPort"));
//        configServerUser = properties.getString("ConfServerUser");
//        configServerPass = properties.getString("ConfServerPassword");
         */
        if (configServerManager.isConnected()) {
            return true;
        } else {
            IConfService ret = null;
            StoredSettings.ConfServer confServ = (StoredSettings.ConfServer) cbConfigServer.getSelectedItem();
            String user = (String) cbUser.getSelectedItem();
            if (confServ != null && user != null) {
                ret = configServerManager.connect(confServ,
                        user, new String(pfPassword.getPassword()));

            }
            connectionStatusChanged();
            return configServerManager.isConnected();
        }
    }

    private String getPassword() {
        return textEncryptor.decrypt(ds.getPassword());
    }

    public void requestOutput(String toString, boolean printBlock) {
        logger.info(toString);
        if (printBlock) {
            taOutput.append("------------------------------------\n");
        }
        taOutput.append(toString);
        taOutput.append("\n");
        taOutput.setCaretPosition(taOutput.getDocument().getLength());

        logger.debug(toString);
    }

    public void requestOutput(String toString) {
        requestOutput(toString, true);

    }

    public void showError(String msg) {
        logger.error(msg);
        requestOutput("!!! Error: " + msg, false);
    }

    public void showException(String cannot_connect_to_ConfigServer, Exception ex) {
        logger.error(cannot_connect_to_ConfigServer, ex);
        StringBuilder buf = new StringBuilder();
        buf.append("!!!Exception!!! = ").append(cannot_connect_to_ConfigServer).append("\n");
        for (StackTraceElement stackTraceElement : ex.getStackTrace()) {
            buf.append("\t").append(stackTraceElement.toString()).append("\n");
        }
        requestOutput(buf.toString(), false);
    }

    private void connectionStatusChanged() {
        boolean isConnected = configServerManager.isConnected();
        btDisconnect.setEnabled(isConnected);
        btConnect.setEnabled(!isConnected);
        cbConfigServer.setEnabled(!isConnected);
        cbUser.setEnabled(!isConnected);
        pfPassword.setEnabled(!isConnected);
    }

    /**
     *
     * @param <T>
     * @param q
     * @param cls
     * @param props
     * @param ss
     * @param checkNames
     * @param foundProc
     * @return true if interrupted
     * @throws ConfigException
     * @throws InterruptedException
     */
    public <T extends CfgObject> boolean findObjects(
            CfgQuery q,
            Class< T> cls,
            IKeyValueProperties props,
            FindWorker ss,
            boolean checkNames,
            ICfgObjectFoundProc foundProc
    ) throws ConfigException, InterruptedException {
        int cnt = 0;
        HashMap<CfgObject, KeyValueCollection> matchedObjects = new HashMap<>();

        StringBuilder buf = new StringBuilder();

        Collection<T> cfgObjs = configServerManager.getResults(q, cls);

        if (cfgObjs != null && !cfgObjs.isEmpty()) {

            for (CfgObject cfgObj : cfgObjs) {

                KeyValueCollection kv = ss.matchConfigObject(cfgObj, props, checkNames);

                if (kv != null) {
                    cnt++;
                    if (foundProc == null) {
                        if (ss.isFullOutputSelected()) {
                            buf.append("----> path: ").append(cfgObj.getObjectPath()).append(" ").append(cfgObj.toString()).append("\n");
                        } else {
                            Object[] names = props.getName(cfgObj).toArray();
                            buf.append("----> \"").append(names[0]).append("\"").append(" path: ").append(cfgObj.getObjectPath()).append(", type:").append(cfgObj.getObjectType()).append(", DBID: ").append(cfgObj.getObjectDbid());
                            if (names.length > 1) {
                                buf.append("\n\t");
                                int added = 1;
                                for (int i = 1; i < names.length; i++) {
                                    if (added > 1) {
                                        buf.append(", ");
                                    }
                                    Object obj = names[i];
                                    if (obj != null) {
                                        String s = obj.toString();
                                        if (StringUtils.isNotBlank(s)) {
                                            buf.append(s);
                                            added++;
                                        }
                                    }
                                }
                            }
                            buf.append("\n");
                            if (!kv.isEmpty()) {
                                buf.append("\t").append(kv.toString()).append("\n\n");
                            }
                        }
                    } else {
                        matchedObjects.put(cfgObj, kv);
                    }
                }

            }
            if (foundProc != null) {
                int i = 0;
                requestOutput("Search done, located " + cnt + " objects type " + cls.getSimpleName() + "\n");

                for (Map.Entry<CfgObject, KeyValueCollection> entry : matchedObjects.entrySet()) {
                    if (!foundProc.proc(entry.getKey(), entry.getValue(), ++i, matchedObjects.size())) {
                        return true;
                    }
                }

            } else if (cnt > 0) {
                requestOutput("Search done, located " + cnt + " objects type " + cls.getSimpleName() + " -->\n" + buf + "<--\n");
            }
        }
        return false;
    }

    private void runAppByIPActionPerformed(ActionEvent evt) {
        runInThread(new IThreadedFun() {
            @Override
            public void fun() {
                StringBuilder buf = new StringBuilder();

                AppByIP pn1 = (AppByIP) appByIP.getContentPanel();
                String ip1 = pn1.getText();
                requestOutput("Request: " + pn1.getSearchSummary());

                try {
                    try {
//                enableComponents(this, false);
                        ArrayList<Record> hostNames = new ArrayList<>();

                        for (int t1 : new int[]{org.xbill.DNS.Type.PTR, org.xbill.DNS.Type.A}) {
                            Lookup l = new Lookup(ReverseMap.fromAddress(ip1), t1);
                            Record[] hosts = l.run();
                            if (ArrayUtils.isNotEmpty(hosts)) {
                                hostNames.addAll(Arrays.asList(hosts));
                            }

                        }
                        if (hostNames.isEmpty()) {
                            buf.append("IP [").append(ip1).append("] not resolved\n");
                        } else {
                            if ((connectToConfigServer())) {
                                CfgHostQuery hq = new CfgHostQuery(configServerManager.getService());
                                CfgApplicationQuery aq = new CfgApplicationQuery(configServerManager.getService());
                                buf.append("resolved IP[").append(ip1).append("] to ");
                                for (Record hostName : hostNames) {
                                    PTRRecord r = (PTRRecord) hostName;
                                    buf.append(r.getTarget().toString(true)).append("\n");
//                                    String mask = StringUtils.strip(StringUtils.trim(r.getTarget().getLabelString(0))) + "*";
                                    String mask = r.getTarget().getLabelString(0) + "*";
                                    hq.setName(mask);
                                    Collection<CfgHost> hostsFound = configServerManager.getResults(hq, CfgHost.class
                                    );
                                    if (hostsFound != null) {
                                        for (CfgHost cfgHost : hostsFound) {
                                            buf.append("Found host: ").append(cfgHost.getName()).append(" DBID:")
                                                    .append(cfgHost.getDBID()).append(" type: ").append(cfgHost.getType()).append(" os: ").append(cfgHost.getOSinfo().getOStype()).append("\n");
                                            aq.setHostDbid(cfgHost.getDBID());
                                            Collection<CfgApplication> appsFound = aq.execute();
                                            buf.append("\tapplications on the host:\n");
                                            if (appsFound == null) {
                                                buf.append("**** no apps found!!! ");
                                            } else {
                                                for (CfgApplication cfgApplication : appsFound) {
                                                    buf.append("\t\t\"").append(cfgApplication.getName()).append("\"").append(" (type:").append(cfgApplication.getType()).append(", DBID:").append(cfgApplication.getDBID()).append(")\n");
                                                }
                                                if (pn1.isFullOutput()) {
                                                    for (CfgApplication cfgApplication : appsFound) {
                                                        buf.append("\t\t\"").append(cfgApplication.toString()).append("\n<<<<<\n");
                                                    }
                                                }
                                            }
//                                try {
//                                    l = new Lookup(cfgHost.getName());
//                                    Record[] run = l.run();
//                                    if (run == null) {
//                                        logger.info("Not resolved name [" + cfgHost.getName() + "]");
//                                    } else {
//                                        buf.append("resolved [" + cfgHost.getName() + "]: ");
//                                        for (Record record : run) {
//                                            buf.append(" ").append(record.getName().toString(false));
//
//                                        }
//                                        buf.append("\n");
//                                    }
//                                } catch (TextParseException ex) {
//                                    java.util.logging.Logger.getLogger(AppForm.class.getName()).log(Level.SEVERE, null, ex);
//                                }
//                            InetAddress[] allByName = Address.getAllByName(cfgHost.getName());
//                            cfgHost.getName();
                                        }
                                    } else {
                                        buf.append("host ").append(r.getTarget().toString(true)).append(" search mask:[").append(mask).append("] not found in CME!\n");
                                    }

//                            r.getTarget().getLabelString(0);
                                }

//                        hq.setName("esv1*");
//                        Collection<CfgHost> execute = hq.execute();
//                        for (CfgHost cfgHost : execute) {
//                            try {
//                                l = new Lookup(cfgHost.getName());
//                                Record[] run = l.run();
//                                if (run == null) {
//                                    logger.info("Not resolved name [" + cfgHost.getName() + "]");
//                                } else {
//                                    buf.append("resolved [" + cfgHost.getName() + "]: ");
//                                    for (Record record : run) {
//                                        buf.append(" ").append(record.getName().toString(false));
//
//                                    }
//                                    buf.append("\n");
//                                }
//                            } catch (TextParseException ex) {
//                                java.util.logging.Logger.getLogger(AppForm.class.getName()).log(Level.SEVERE, null, ex);
//                            }
////                            InetAddress[] allByName = Address.getAllByName(cfgHost.getName());
////                            cfgHost.getName();
//                        }
//                        logger.info(execute);
//                        CfgApplicationQuery q = new CfgApplicationQuery(service);
////                    ICfgObject retrieveObject = service.retrieveObject(t, dbid);
////                    requestOutput(retrieveObject.toString());
//                } catch (ConfigException ex) {
//                    java.util.logging.Logger.getLogger(AppForm.class.getName()).log(Level.SEVERE, null, ex);
//                }
                            }
                        }
                    } catch (UnknownHostException ex) {
                        StringBuilder append = buf.append(ex.getMessage()).append("\n");
                        java.util.logging.Logger.getLogger(Main.class
                                .getName()).log(java.util.logging.Level.SEVERE, null, ex);

                    } catch (ConfigException ex) {
                        java.util.logging.Logger.getLogger(AppForm.class
                                .getName()).log(Level.SEVERE, null, ex);

                    } catch (InterruptedException ex) {
                        java.util.logging.Logger.getLogger(AppForm.class
                                .getName()).log(Level.SEVERE, null, ex);
                    }
                    requestOutput(buf.toString());
                } finally {
//            enableComponents(this, true);
                }
            }
        });

    }

    private void appByOptionThread(AppByOptions par) {
        runInThread(new IThreadedFun() {
            @Override
            public void fun() {
                if (connectToConfigServer()) {

                    AppByOptions pn = (AppByOptions) appByOption.getContentPanel();
                    requestOutput("Request: " + pn.getSearchSummary());

                    try {

                        CfgAppType t = pn.getSelectedAppType();
                        CfgApplicationQuery q = new CfgApplicationQuery();
                        if (t != null) {
                            q.setAppType(t);

                        }
                        findObjects(
                                q,
                                CfgApplication.class,
                                new IKeyValueProperties() {
                            @Override
                            public KeyValueCollection getProperties(CfgObject obj) {
                                return ((CfgApplication) obj).getOptions();
                            }

                            @Override
                            public Collection<String> getName(CfgObject obj) {
                                Collection<String> ret = new ArrayList<>();
                                ret.add(((CfgApplication) obj).getName());
                                return ret;

                            }
                        },
                                new FindWorker(pn),
                                true,
                                null);

                    } catch (ConfigException ex) {
                        showException("Error", ex);

                    } catch (InterruptedException ex) {
                        java.util.logging.Logger.getLogger(AppForm.class
                                .getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });

    }

    SwingWorker worker = null;

    private void workStarted(boolean isStarted) {
        btCancel.setVisible(isStarted);
        btClearOutput.setVisible(!isStarted);
        btDisconnect.setVisible(!isStarted);
        btConnect.setVisible(!isStarted);
        btEditConfgServ.setVisible(!isStarted);

    }

    private void runInThread(IThreadedFun fun) {
        SwingWorker theWorker = new SwingWorker() {
            private Exception ex = null;

            @Override
            protected Object doInBackground() throws Exception {
                worker = this;
                workStarted(true);
                try {
                    fun.fun();
                } catch (ConfigException | InterruptedException e) {
                    ex = e;
                }
                return null;
            }

            ;

            @Override
            protected void done() {
                if (ex != null) {
                    if (ex instanceof InterruptedException) {
                        requestOutput("Interrupted", false);
                    } else {
                        requestOutput("Exception processing: " + ex.toString() + "\n" + StringUtils.join(ex.getStackTrace(), "\n"), false);
                    }
                } else {
                    requestOutput("All done", false);
                }
                workStarted(false);
                worker = null;
            }

        };
        theWorker.execute();
    }

    private void runObjByAnnexThread(ActionEvent evt) {
        runInThread(new IThreadedFun() {
            @Override
            public void fun() throws ConfigException, InterruptedException {
                if (connectToConfigServer()) {

                    ObjByAnnex pn = (ObjByAnnex) objByAnnex.getContentPanel();
                    requestOutput("Request: " + pn.getSearchSummary());

                    for (CfgObjectType value : pn.getSelectedObjectTypes()) {
                        doTheSearch(value, pn, false, true, null);
                    }

                }
            }
        });

    }

    private void runAnnexReplaceThread(ActionEvent evt) {
        runInThread(new IThreadedFun() {
            @Override
            public void fun() throws ConfigException, InterruptedException {
                if (connectToConfigServer()) {
//                    updateObj();
                    ObjByAnnex pn = (ObjByAnnex) objByAnnex.getContentPanel();
                    requestOutput("Request: " + pn.getSearchSummary());

                    for (CfgObjectType value : pn.getSelectedObjectTypes()) {
                        doTheSearch(value, pn, false, true, null);
                    }

                }
            }
        });

    }

    private boolean doTheSearch(
            CfgObjectType t,
            ISearchSettings pn,
            boolean warnNotFound,
            boolean checkNames,
            ICfgObjectFoundProc foundProc) throws ConfigException, InterruptedException {
        IConfService service = configServerManager.getService();
        //<editor-fold defaultstate="collapsed" desc="CfgObjectType.CFGDN">
        if (t == CfgObjectType.CFGDN) {
            CfgDNQuery query = new CfgDNQuery();
//            String n = pn.getObjName();
//            if (pn.isCaseSensitive() && n != null) {
//                query.setDnNumber(n);
//
//            }

            if (findObjects(
                    query,
                    CfgDN.class,
                    new IKeyValueProperties() {
                @Override
                public KeyValueCollection getProperties(CfgObject obj) {
                    return ((CfgDN) obj).getUserProperties();
                }

                @Override
                public Collection<String> getName(CfgObject obj) {
                    Collection<String> ret = new ArrayList<>();
                    ret.add(((CfgDN) obj).getNumber());
                    ret.add(((CfgDN) obj).getDNLoginID());
                    ret.add(((CfgDN) obj).getName());
                    ret.add(((CfgDN) obj).getOverride());

                    return ret;
                }
            },
                    new FindWorker(pn),
                    checkNames,
                    foundProc)) {
                return false;
            }
        } //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="CfgObjectType.CFGSwitch">
        else if (t == CfgObjectType.CFGSwitch) {
            CfgSwitchQuery query = new CfgSwitchQuery();
//            String n = pn.getObjName();
//            if (pn.isCaseSensitive() && n != null) {
//                query.setName(n);
//
//            }
//                            CfgSwitchType selectedObjSubType = (CfgSwitchType) pn.getSelectedObjSubType();
//                            if (selectedObjSubType != null) {
//                                query.(selectedObjSubType);
//                            }

            if (findObjects(
                    query,
                    CfgSwitch.class,
                    new IKeyValueProperties() {
                @Override
                public KeyValueCollection getProperties(CfgObject obj) {
                    return ((CfgSwitch) obj).getUserProperties();
                }

                @Override
                public Collection<String> getName(CfgObject obj) {
                    Collection<String> ret = new ArrayList<>();
                    ret.add(((CfgSwitch) obj).getName());
                    return ret;
                }
            },
                    new FindWorker(pn),
                    checkNames,
                    foundProc)) {
                return false;
            }
        } //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="CfgObjectType.CFGAgentLogin">
        else if (t == CfgObjectType.CFGAgentLogin) {
            CfgAgentLoginQuery query = new CfgAgentLoginQuery();
//            String n = pn.getObjName();
//            if (pn.isCaseSensitive() && n != null) {
//                query.setLoginCode(n);
//
//            }
//                            CfgSwitchType selectedObjSubType = (CfgSwitchType) pn.getSelectedObjSubType();
//                            if (selectedObjSubType != null) {
//                                query.(selectedObjSubType);
//                            }

            if (findObjects(
                    query,
                    CfgAgentLogin.class,
                    new IKeyValueProperties() {
                @Override
                public KeyValueCollection getProperties(CfgObject obj) {
                    return ((CfgAgentLogin) obj).getUserProperties();
                }

                @Override
                public Collection<String> getName(CfgObject obj) {
                    Collection<String> ret = new ArrayList<>();
                    ret.add(((CfgAgentLogin) obj).getLoginCode());
                    return ret;
                }
            },
                    new FindWorker(pn),
                    checkNames,
                    foundProc)) {
                return false;
            }
        } //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="CfgObjectType.CFGPlace">
        else if (t == CfgObjectType.CFGPlace) {
            CfgPlaceQuery query = new CfgPlaceQuery();
//            String n = pn.getObjName();
//            if (pn.isCaseSensitive() && n != null) {
//                query.setName(n);
//
//            }
//                            CfgSwitchType selectedObjSubType = (CfgSwitchType) pn.getSelectedObjSubType();
//                            if (selectedObjSubType != null) {
//                                query.(selectedObjSubType);
//                            }

            if (findObjects(
                    query,
                    CfgPlace.class,
                    new IKeyValueProperties() {
                @Override
                public KeyValueCollection getProperties(CfgObject obj) {
                    return ((CfgPlace) obj).getUserProperties();
                }

                @Override
                public Collection<String> getName(CfgObject obj) {
                    Collection<String> ret = new ArrayList<>();
                    ret.add(((CfgPlace) obj).getName());
                    return ret;
                }
            },
                    new FindWorker(pn), checkNames, foundProc)) {
                return false;
            }
        } //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="CfgObjectType.CFGPerson">
        else if (t == CfgObjectType.CFGPerson) {
            CfgPersonQuery query = new CfgPersonQuery();
//            String n = pn.getObjName();
//            if (pn.isCaseSensitive() && n != null) {
//                query.setUserName(n);
//
//            }
//                            CfgSwitchType selectedObjSubType = (CfgSwitchType) pn.getSelectedObjSubType();
//                            if (selectedObjSubType != null) {
//                                query.(selectedObjSubType);
//                            }

            if (findObjects(
                    query,
                    CfgPerson.class,
                    new IKeyValueProperties() {
                @Override
                public KeyValueCollection getProperties(CfgObject obj) {
                    return ((CfgPerson) obj).getUserProperties();
                }

                @Override
                public Collection<String> getName(CfgObject obj) {
                    Collection<String> ret = new ArrayList<>();
                    ret.add(((CfgPerson) obj).getUserName());
                    ret.add(((CfgPerson) obj).getEmailAddress());
                    ret.add(((CfgPerson) obj).getEmployeeID());
                    ret.add(((CfgPerson) obj).getExternalID());
                    ret.add(((CfgPerson) obj).getFirstName());
                    ret.add(((CfgPerson) obj).getLastName());
                    ret.add(((CfgPerson) obj).getPassword());
                    ret.add(((CfgPerson) obj).getUserName());

                    return ret;
                }
            },
                    new FindWorker(pn), checkNames, foundProc)) {
                return false;
            }
        } //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="CfgObjectType.CFGAgentGroup">
        else if (t == CfgObjectType.CFGAgentGroup) {
            CfgAgentGroupQuery query = new CfgAgentGroupQuery();
//            String n = pn.getObjName();
//            if (pn.isCaseSensitive() && n != null) {
//                query.setName(n);
//
//            }

            if (findObjects(
                    query,
                    CfgAgentGroup.class,
                    new IKeyValueProperties() {
                @Override
                public KeyValueCollection getProperties(CfgObject obj) {
                    return ((CfgAgentGroup) obj).getGroupInfo().getUserProperties();
                }

                @Override
                public Collection<String> getName(CfgObject obj) {
                    Collection<String> ret = new ArrayList<>();
                    ret.add(((CfgAgentGroup) obj).getGroupInfo().getName());
                    return ret;
                }
            },
                    new FindWorker(pn), checkNames, foundProc)) {
                return false;
            }
        } //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="CfgObjectType.CFGDNGroup">
        else if (t == CfgObjectType.CFGDNGroup) {
            CfgDNGroupQuery query = new CfgDNGroupQuery();
//            String n = pn.getObjName();
//            if (pn.isCaseSensitive() && n != null) {
//                query.setName(n);
//
//            }

            if (findObjects(
                    query,
                    CfgDNGroup.class,
                    new IKeyValueProperties() {
                @Override
                public KeyValueCollection getProperties(CfgObject obj) {
                    return ((CfgDNGroup) obj).getGroupInfo().getUserProperties();
                }

                @Override
                public Collection<String> getName(CfgObject obj) {
                    Collection<String> ret = new ArrayList<>();
                    ret.add(((CfgDNGroup) obj).getGroupInfo().getName());
                    return ret;
                }
            },
                    new FindWorker(pn), checkNames, foundProc)) {
                return false;
            }
        } //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="CfgObjectType.CFGPlaceGroup">
        else if (t == CfgObjectType.CFGPlaceGroup) {
            CfgPlaceGroupQuery query = new CfgPlaceGroupQuery();
//            String n = pn.getObjName();
//            if (pn.isCaseSensitive() && n != null) {
//                query.setName(n);
//
//            }

            if (findObjects(
                    query,
                    CfgPlaceGroup.class,
                    new IKeyValueProperties() {
                @Override
                public KeyValueCollection getProperties(CfgObject obj) {
                    return ((CfgPlaceGroup) obj).getGroupInfo().getUserProperties();
                }

                @Override
                public Collection<String> getName(CfgObject obj) {
                    Collection<String> ret = new ArrayList<>();
                    ret.add(((CfgPlaceGroup) obj).getGroupInfo().getName());
                    return ret;
                }
            },
                    new FindWorker(pn), checkNames, foundProc)) {
                return false;
            }
        } //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="CfgObjectType.CFGScript">
        else if (t == CfgObjectType.CFGScript) {
            CfgScriptQuery query = new CfgScriptQuery();
//            String n = pn.getObjName();
//            if (pn.isCaseSensitive() && n != null) {
//                query.setName(n);
//
//            }
//                            CfgSwitchType selectedObjSubType = (CfgSwitchType) pn.getSelectedObjSubType();
//                            if (selectedObjSubType != null) {
//                                query.(selectedObjSubType);
//                            }

            if (findObjects(
                    query,
                    CfgScript.class,
                    new IKeyValueProperties() {
                @Override
                public KeyValueCollection getProperties(CfgObject obj) {
                    return ((CfgScript) obj).getUserProperties();
                }

                @Override
                public Collection<String> getName(CfgObject obj) {
                    Collection<String> ret = new ArrayList<>();
                    ret.add(((CfgScript) obj).getName());
                    return ret;
                }
            },
                    new FindWorker(pn), checkNames, foundProc)) {
                return false;
            }
//</editor-fold>
            //<editor-fold defaultstate="collapsed" desc="CfgObjectType.CFGTransaction">
        } else if (t == CfgObjectType.CFGTransaction) {
            CfgTransactionQuery query = new CfgTransactionQuery();
//            String n = pn.getObjName();
//            if (pn.isCaseSensitive() && n != null) {
//                query.setName(n);
//
//            }

            if (findObjects(
                    query,
                    CfgTransaction.class,
                    new IKeyValueProperties() {
                @Override
                public KeyValueCollection getProperties(CfgObject obj) {
                    return ((CfgTransaction) obj).getUserProperties();
                }

                @Override
                public Collection<String> getName(CfgObject obj) {
                    Collection<String> ret = new ArrayList<>();
                    ret.add(((CfgTransaction) obj).getName());
                    ret.add(((CfgTransaction) obj).getAlias());
                    ret.add(((CfgTransaction) obj).getDescription());
                    return ret;
                }
            },
                    new FindWorker(pn), checkNames, foundProc)) {
                return false;
            }
        } //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="CfgObjectType.CFGEnumerator">
        else if (t == CfgObjectType.CFGEnumerator) {
            CfgEnumeratorQuery query = new CfgEnumeratorQuery();
//            String n = pn.getObjName();
//            if (pn.isCaseSensitive() && n != null) {
//                query.setName(n);
//
//            }

            if (findObjects(
                    query,
                    CfgEnumerator.class,
                    new IKeyValueProperties() {
                @Override
                public KeyValueCollection getProperties(CfgObject obj) {
                    return ((CfgEnumerator) obj).getUserProperties();
                }

                @Override
                public Collection<String> getName(CfgObject obj) {
                    Collection<String> ret = new ArrayList<>();
                    ret.add(((CfgEnumerator) obj).getName());
                    ret.add(((CfgEnumerator) obj).getDescription());
                    ret.add(((CfgEnumerator) obj).getDisplayName());
                    return ret;
                }
            },
                    new FindWorker(pn), checkNames, foundProc)) {
                return false;
            }
        } //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="CfgObjectType.CFGEnumeratorValue">
        else if (t == CfgObjectType.CFGEnumeratorValue) {
            CfgEnumeratorValueQuery query = new CfgEnumeratorValueQuery();
//            String n = pn.getObjName();
//            if (pn.isCaseSensitive() && n != null) {
//                query.setName(n);
//
//            }

            if (findObjects(
                    query,
                    CfgEnumeratorValue.class,
                    new IKeyValueProperties() {
                @Override
                public KeyValueCollection getProperties(CfgObject obj) {
                    return ((CfgEnumeratorValue) obj).getUserProperties();
                }

                @Override
                public Collection<String> getName(CfgObject obj) {
                    Collection<String> ret = new ArrayList<>();
                    ret.add(((CfgEnumeratorValue) obj).getName());
                    ret.add(((CfgEnumeratorValue) obj).getDescription());
                    ret.add(((CfgEnumeratorValue) obj).getDisplayName());
                    return ret;
                }
            },
                    new FindWorker(pn), checkNames, foundProc)) {
                return false;
            }

        } //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="CfgObjectType.CFGGVPIVRProfile">
        else if (t == CfgObjectType.CFGGVPIVRProfile) {
            CfgGVPIVRProfileQuery query = new CfgGVPIVRProfileQuery();
//            String n = pn.getObjName();
//            if (pn.isCaseSensitive() && n != null) {
//                query.setName(n);
//
//            }

            if (findObjects(
                    query,
                    CfgGVPIVRProfile.class,
                    new IKeyValueProperties() {
                @Override
                public KeyValueCollection getProperties(CfgObject obj) {
                    return ((CfgGVPIVRProfile) obj).getUserProperties();
                }

                @Override
                public Collection<String> getName(CfgObject obj) {
                    Collection<String> ret = new ArrayList<>();
                    ret.add(((CfgGVPIVRProfile) obj).getName());
                    ret.add(((CfgGVPIVRProfile) obj).getDescription());
                    ret.add(((CfgGVPIVRProfile) obj).getDisplayName());
                    ret.add(((CfgGVPIVRProfile) obj).getNotes());
                    ret.add(((CfgGVPIVRProfile) obj).getStatus());
                    ret.add(((CfgGVPIVRProfile) obj).getTfn());
                    return ret;
                }
            },
                    new FindWorker(pn), checkNames, foundProc)) {
                return false;
            }

        } //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="CfgObjectType.CFGAccessGroup">
        else if (t == CfgObjectType.CFGAccessGroup) {
            CfgAccessGroupQuery query = new CfgAccessGroupQuery();
//            String n = pn.getObjName();
//            if (pn.isCaseSensitive() && n != null) {
//                query.setName(n);
//
//            }

            if (findObjects(
                    query,
                    CfgAccessGroup.class,
                    new IKeyValueProperties() {
                @Override
                public KeyValueCollection getProperties(CfgObject obj) {
                    return null;
                }

                @Override
                public Collection<String> getName(CfgObject obj) {
                    Collection<String> ret = new ArrayList<>();

                    return ret;
                }
            },
                    new FindWorker(pn), checkNames, foundProc)) {
                return false;
            }

        } //</editor-fold>        
        //<editor-fold defaultstate="collapsed" desc="CfgObjectType.CFGActionCode">
        else if (t == CfgObjectType.CFGActionCode) {
            CfgActionCodeQuery query = new CfgActionCodeQuery();
//            String n = pn.getObjName();
//            if (pn.isCaseSensitive() && n != null) {
//                query.setName(n);
//
//            }

            if (findObjects(
                    query,
                    CfgActionCode.class,
                    new IKeyValueProperties() {
                @Override
                public KeyValueCollection getProperties(CfgObject obj) {
                    return ((CfgActionCode) obj).getUserProperties();

                }

                @Override
                public Collection<String> getName(CfgObject obj) {
                    Collection<String> ret = new ArrayList<>();
                    ret.add(((CfgActionCode) obj).getName());
                    ret.add(((CfgActionCode) obj).getCode());
                    return ret;
                }
            },
                    new FindWorker(pn), checkNames, foundProc)) {
                return false;
            }

        } //</editor-fold>   
        //<editor-fold defaultstate="collapsed" desc="CfgObjectType.CFGAlarmCondition">
        else if (t == CfgObjectType.CFGAlarmCondition) {
            CfgAlarmConditionQuery query = new CfgAlarmConditionQuery();
//            String n = pn.getObjName();
//            if (pn.isCaseSensitive() && n != null) {
//                query.setName(n);
//
//            }

            if (findObjects(
                    query,
                    CfgAlarmCondition.class,
                    new IKeyValueProperties() {
                @Override
                public KeyValueCollection getProperties(CfgObject obj) {
                    return ((CfgAlarmCondition) obj).getUserProperties();
                }

                @Override
                public Collection<String> getName(CfgObject obj) {
                    Collection<String> ret = new ArrayList<>();
                    ret.add(((CfgAlarmCondition) obj).getName());
                    ret.add(((CfgAlarmCondition) obj).getDescription());
                    return ret;
                }
            },
                    new FindWorker(pn), checkNames, foundProc)) {
                return false;
            }

        } //</editor-fold>   
        //<editor-fold defaultstate="collapsed" desc="CfgObjectType.CFGApplication">
        else if (t == CfgObjectType.CFGApplication) {
            CfgApplicationQuery query = new CfgApplicationQuery();
//            String n = pn.getObjName();
//            if (pn.isCaseSensitive() && n != null) {
//                query.setName(n);
//
//            }

            if (findObjects(
                    query,
                    CfgApplication.class,
                    new IKeyValueProperties() {
                @Override
                public KeyValueCollection getProperties(CfgObject obj) {
                    return ((CfgApplication) obj).getUserProperties();
                }

                @Override
                public Collection<String> getName(CfgObject obj) {
                    Collection<String> ret = new ArrayList<>();
                    ret.add(((CfgApplication) obj).getName());
                    ret.add(((CfgApplication) obj).getCommandLine());
                    ret.add(((CfgApplication) obj).getCommandLineArguments());
                    ret.add(((CfgApplication) obj).getWorkDirectory());
                    ret.add(((CfgApplication) obj).getVersion());
                    return ret;
                }
            },
                    new FindWorker(pn), checkNames, foundProc)) {
                return false;
            }

        } //</editor-fold>   
        //<editor-fold defaultstate="collapsed" desc="CfgObjectType.CFGFolder">
        else if (t == CfgObjectType.CFGFolder) {
            CfgFolderQuery query = new CfgFolderQuery();
//            String n = pn.getObjName();
//            if (pn.isCaseSensitive() && n != null) {
//                query.setName(n);
//
//            }

            if (findObjects(
                    query,
                    CfgFolder.class,
                    new IKeyValueProperties() {
                @Override
                public KeyValueCollection getProperties(CfgObject obj) {
                    return ((CfgFolder) obj).getUserProperties();
                }

                @Override
                public Collection<String> getName(CfgObject obj) {
                    Collection<String> ret = new ArrayList<>();
                    ret.add(((CfgFolder) obj).getName());
                    ret.add(((CfgFolder) obj).getDescription());

                    return ret;
                }
            },
                    new FindWorker(pn), checkNames, foundProc)) {
                return false;
            }

        } //</editor-fold>   
        //<editor-fold defaultstate="collapsed" desc="CfgObjectType.CFGHost">
        else if (t == CfgObjectType.CFGHost) {
            CfgHostQuery query = new CfgHostQuery();
//            String n = pn.getObjName();
//            if (pn.isCaseSensitive() && n != null) {
//                query.setName(n);
//
//            }

            if (findObjects(
                    query,
                    CfgHost.class,
                    new IKeyValueProperties() {
                @Override
                public KeyValueCollection getProperties(CfgObject obj) {
                    return ((CfgHost) obj).getUserProperties();
                }

                @Override
                public Collection<String> getName(CfgObject obj) {
                    Collection<String> ret = new ArrayList<>();
                    ret.add(((CfgHost) obj).getName());
                    ret.add(((CfgHost) obj).getIPaddress());
                    return ret;
                }
            },
                    new FindWorker(pn), checkNames, foundProc)) {
                return false;
            }

        } //</editor-fold>   
        //<editor-fold defaultstate="collapsed" desc="CfgObjectType.CFGTenant">
        else if (t == CfgObjectType.CFGTenant) {
            CfgTenantQuery query = new CfgTenantQuery();
//            String n = pn.getObjName();
//            if (pn.isCaseSensitive() && n != null) {
//                query.setName(n);
//
//            }

            if (findObjects(
                    query,
                    CfgTenant.class,
                    new IKeyValueProperties() {
                @Override
                public KeyValueCollection getProperties(CfgObject obj) {
                    return ((CfgTenant) obj).getUserProperties();
                }

                @Override
                public Collection<String> getName(CfgObject obj) {
                    Collection<String> ret = new ArrayList<>();
                    ret.add(((CfgTenant) obj).getName());
                    ret.add(((CfgTenant) obj).getChargeableNumber());
                    return ret;
                }
            },
                    new FindWorker(pn), checkNames, foundProc)) {
                return false;
            }

        } //</editor-fold>   
        //<editor-fold defaultstate="collapsed" desc="CfgObjectType.CFGIVRPort">
        else if (t == CfgObjectType.CFGIVRPort) {
            CfgIVRPortQuery query = new CfgIVRPortQuery();
//            String n = pn.getObjName();
//            if (pn.isCaseSensitive() && n != null) {
//                query.setPortNumber(n);
//
//            }

            if (findObjects(
                    query,
                    CfgIVRPort.class,
                    new IKeyValueProperties() {
                @Override
                public KeyValueCollection getProperties(CfgObject obj) {
                    return ((CfgIVRPort) obj).getUserProperties();
                }

                @Override
                public Collection<String> getName(CfgObject obj) {
                    Collection<String> ret = new ArrayList<>();
                    ret.add(((CfgIVRPort) obj).getDescription());
                    ret.add(((CfgIVRPort) obj).getPortNumber());
                    return ret;
                }
            },
                    new FindWorker(pn), checkNames, foundProc)) {
                return false;
            }

        } //</editor-fold>   
        //<editor-fold defaultstate="collapsed" desc="CfgObjectType.CFGIVR">
        else if (t == CfgObjectType.CFGIVR) {
            CfgIVRQuery query = new CfgIVRQuery();
//            String n = pn.getObjName();
//            if (pn.isCaseSensitive() && n != null) {
//                query.setName(n);
//
//            }

            if (findObjects(
                    query,
                    CfgIVR.class,
                    new IKeyValueProperties() {
                @Override
                public KeyValueCollection getProperties(CfgObject obj) {
                    return ((CfgIVR) obj).getUserProperties();
                }

                @Override
                public Collection<String> getName(CfgObject obj) {
                    Collection<String> ret = new ArrayList<>();
                    ret.add(((CfgIVR) obj).getDescription());
                    ret.add(((CfgIVR) obj).getName());
                    return ret;
                }
            },
                    new FindWorker(pn), checkNames, foundProc)) {
                return false;
            }

        } //</editor-fold>   
        //<editor-fold defaultstate="collapsed" desc="CfgObjectType.CFGObjectiveTable">
        else if (t == CfgObjectType.CFGObjectiveTable) {
            CfgObjectiveTableQuery query = new CfgObjectiveTableQuery();
//            String n = pn.getObjName();
//            if (pn.isCaseSensitive() && n != null) {
//                query.setName(n);
//
//            }

            if (findObjects(
                    query,
                    CfgObjectiveTable.class,
                    new IKeyValueProperties() {
                @Override
                public KeyValueCollection getProperties(CfgObject obj) {
                    return ((CfgObjectiveTable) obj).getUserProperties();
                }

                @Override
                public Collection<String> getName(CfgObject obj) {
                    Collection<String> ret = new ArrayList<>();
                    ret.add(((CfgObjectiveTable) obj).getDescription());
                    ret.add(((CfgObjectiveTable) obj).getName());
                    return ret;
                }
            },
                    new FindWorker(pn), checkNames, foundProc)) {
                return false;
            }

        } //</editor-fold>   
        //<editor-fold defaultstate="collapsed" desc="CfgObjectType.CFGService">
        else if (t == CfgObjectType.CFGService) {
            CfgServiceQuery query = new CfgServiceQuery();
//            String n = pn.getObjName();
//            if (pn.isCaseSensitive() && n != null) {
//                query.setName(n);
//
//            }

            if (findObjects(
                    query,
                    CfgService.class,
                    new IKeyValueProperties() {
                @Override
                public KeyValueCollection getProperties(CfgObject obj) {
                    return ((CfgService) obj).getUserProperties();
                }

                @Override
                public Collection<String> getName(CfgObject obj) {
                    Collection<String> ret = new ArrayList<>();
                    ret.add(((CfgService) obj).getName());
                    ret.add(((CfgService) obj).getVersion());
                    return ret;
                }
            },
                    new FindWorker(pn), checkNames, foundProc)) {
                return false;
            }

        } //</editor-fold>   
        //<editor-fold defaultstate="collapsed" desc="CfgObjectType.CFGSkill">
        else if (t == CfgObjectType.CFGSkill) {
            CfgSkillQuery query = new CfgSkillQuery();
//            String n = pn.getObjName();
//            if (pn.isCaseSensitive() && n != null) {
//                query.setName(n);
//
//            }

            if (findObjects(
                    query,
                    CfgSkill.class,
                    new IKeyValueProperties() {
                @Override
                public KeyValueCollection getProperties(CfgObject obj) {
                    return ((CfgSkill) obj).getUserProperties();
                }

                @Override
                public Collection<String> getName(CfgObject obj) {
                    Collection<String> ret = new ArrayList<>();
                    ret.add(((CfgSkill) obj).getName());
                    return ret;
                }
            },
                    new FindWorker(pn), checkNames, foundProc)) {
                return false;
            }

        } //</editor-fold>   
        //<editor-fold defaultstate="collapsed" desc="CfgObjectType.CFGStatDay">
        else if (t == CfgObjectType.CFGStatDay) {
            CfgStatDayQuery query = new CfgStatDayQuery();
//            String n = pn.getObjName();
//            if (pn.isCaseSensitive() && n != null) {
//                query.setName(n);
//
//            }

            if (findObjects(
                    query,
                    CfgStatDay.class,
                    new IKeyValueProperties() {
                @Override
                public KeyValueCollection getProperties(CfgObject obj) {
                    return ((CfgStatDay) obj).getUserProperties();
                }

                @Override
                public Collection<String> getName(CfgObject obj) {
                    Collection<String> ret = new ArrayList<>();
                    ret.add(((CfgStatDay) obj).getName());
                    return ret;
                }
            },
                    new FindWorker(pn), checkNames, foundProc)) {
                return false;
            }

        } //</editor-fold>   
        //<editor-fold defaultstate="collapsed" desc="CfgObjectType.CFGStatTable">
        else if (t == CfgObjectType.CFGStatTable) {
            CfgStatTableQuery query = new CfgStatTableQuery();
//            String n = pn.getObjName();
//            if (pn.isCaseSensitive() && n != null) {
//                query.setName(n);
//
//            }

            if (findObjects(
                    query,
                    CfgStatTable.class,
                    new IKeyValueProperties() {
                @Override
                public KeyValueCollection getProperties(CfgObject obj) {
                    return ((CfgStatTable) obj).getUserProperties();
                }

                @Override
                public Collection<String> getName(CfgObject obj) {
                    Collection<String> ret = new ArrayList<>();
                    ret.add(((CfgStatTable) obj).getName());
                    return ret;
                }
            },
                    new FindWorker(pn), checkNames, foundProc)) {
                return false;
            }

        } //</editor-fold>   
        //<editor-fold defaultstate="collapsed" desc="CfgObjectType.CFGTimeZone">
        else if (t == CfgObjectType.CFGTimeZone) {
            CfgTimeZoneQuery query = new CfgTimeZoneQuery();
//            String n = pn.getObjName();
//            if (pn.isCaseSensitive() && n != null) {
//                query.setName(n);
//
//            }

            if (findObjects(
                    query,
                    CfgTimeZone.class,
                    new IKeyValueProperties() {
                @Override
                public KeyValueCollection getProperties(CfgObject obj) {
                    return ((CfgTimeZone) obj).getUserProperties();
                }

                @Override
                public Collection<String> getName(CfgObject obj) {
                    Collection<String> ret = new ArrayList<>();
                    ret.add(((CfgTimeZone) obj).getName());
                    ret.add(((CfgTimeZone) obj).getDescription());
                    ret.add(((CfgTimeZone) obj).getNameMSExplorer());
                    ret.add(((CfgTimeZone) obj).getNameNetscape());
                    return ret;
                }
            },
                    new FindWorker(pn), checkNames, foundProc)) {
                return false;
            }

        } //</editor-fold>   
        //<editor-fold defaultstate="collapsed" desc="CfgObjectType.CFGTreatment">
        else if (t == CfgObjectType.CFGTreatment) {
            CfgTreatmentQuery query = new CfgTreatmentQuery();
//            String n = pn.getObjName();
//            if (pn.isCaseSensitive() && n != null) {
//                query.setName(n);
//
//            }

            if (findObjects(
                    query,
                    CfgTreatment.class,
                    new IKeyValueProperties() {
                @Override
                public KeyValueCollection getProperties(CfgObject obj) {
                    return ((CfgTreatment) obj).getUserProperties();
                }

                @Override
                public Collection<String> getName(CfgObject obj) {
                    Collection<String> ret = new ArrayList<>();
                    ret.add(((CfgTreatment) obj).getName());
                    ret.add(((CfgTreatment) obj).getDescription());

                    return ret;
                }
            },
                    new FindWorker(pn), checkNames, foundProc)) {
                return false;
            }

        } //</editor-fold>   
        //<editor-fold defaultstate="collapsed" desc="CfgObjectType.CFGVoicePrompt">
        else if (t == CfgObjectType.CFGVoicePrompt) {
            CfgVoicePromptQuery query = new CfgVoicePromptQuery();
//            String n = pn.getObjName();
//            if (pn.isCaseSensitive() && n != null) {
//                query.setName(n);
//
//            }

            if (findObjects(
                    query,
                    CfgVoicePrompt.class,
                    new IKeyValueProperties() {
                @Override
                public KeyValueCollection getProperties(CfgObject obj) {
                    return ((CfgVoicePrompt) obj).getUserProperties();
                }

                @Override
                public Collection<String> getName(CfgObject obj) {
                    Collection<String> ret = new ArrayList<>();
                    ret.add(((CfgVoicePrompt) obj).getName());
                    ret.add(((CfgVoicePrompt) obj).getDescription());

                    return ret;
                }
            },
                    new FindWorker(pn), checkNames, foundProc)) {
                return false;
            }

        } //</editor-fold>   
        else {
            if (warnNotFound) {
                logger.info("Searching for type " + t + " not implemented yet");
            }

        }
        return true;
    }

    private void execQuery(CfgQuery query,
            ISearchNamedProperties objProperties,
            BussAttr searchParams
    ) throws ConfigException, InterruptedException {
        Collection<CfgObject> cfgObjs = query.execute();
        StringBuilder buf = new StringBuilder();

        if (cfgObjs == null || cfgObjs.isEmpty()) {
            logger.debug("no objects found\n", false);
        } else {
            logger.debug("retrieved " + cfgObjs.size() + " total objects");
            int flags = ((searchParams.isRegex()) ? Pattern.LITERAL : 0) | ((searchParams.isCaseSensitive()) ? 0 : Pattern.CASE_INSENSITIVE);
            String val = searchParams.getName();
            Pattern ptVal = (val == null) ? null : Pattern.compile(val, flags);
            int cnt = 0;
            for (CfgObject cfgObj : cfgObjs) {
                String[] namedProperties = objProperties.getNamedProperties(cfgObj);
                boolean found = false;
                for (String namedProperty : namedProperties) {
                    if (matching(ptVal, namedProperty)) {
                        found = true;
                        cnt++;
                        break;
                    }
                }
                if (found) {
                    if (searchParams.isFullOutputSelected()) {
                        buf.append(cfgObj.toString());
                    } else {
                        buf.append(objProperties.getShortPrint(cfgObj));
                    }
                    buf.append("\n");
                }
            }
            if (cnt > 0) {
                requestOutput("Filtering done\n" + buf);

            }
        }
    }

    HashSet<String> searchValues = new HashSet<>();

    public static final HashMap<String, Integer> cacheOptions = createCacheOptions();

    private static HashMap<String, Integer> createCacheOptions() {
        HashMap<String, Integer> ret = new HashMap<>();
        ret.put("assembled-cache-reload-threshold", 0);
        ret.put("max-age", 0);
        ret.put("max-assembled-cache-age", 0);

        return ret;
    }

    public FindObject getObjName(String objClass) {
        if (getObjName == null) {
            findObj = new FindObject();
            getObjName = new RequestDialog(this, findObj);
        }
        findObj.setCaseSensitive(false);
        findObj.setLabel("Specify name for " + objClass);
        if (!getObjName.doShow("Edit ")) {
            return null;
        } else {
            return findObj;
        }
    }

    private static final String appSection = "application";
    RequestDialog getObjName = null;
    FindObject findObj = null;

    /**
     *
     * @param turnOn - turn on buffering; if true, means add cache parameters,
     * remove them
     */
    private void strategyBuffering(boolean turnOn, ActionEvent evt) {
        upd = null;
        yesToAll = false;

        FindObject objName = getObjName(CfgScript.class.getSimpleName() + " type " + CfgScriptType.CFGEnhancedRouting);

        if (objName == null) {
            return;
        }

        if (connectToConfigServer()) {
            ISearchSettings searchSettings;
            IUpdateSettings us;
            if (turnOn) {
                //<editor-fold defaultstate="collapsed" desc="turnOn">
                searchSettings = new ISearchSettings() {

                    @Override
                    public boolean isCaseSensitive() {
                        return objName.isCaseSensitive();
                    }

                    @Override
                    public boolean isRegex() {
                        return objName.isRegex();
                    }

                    @Override
                    public boolean isFullOutputSelected() {
                        return false;
                    }

                    @Override
                    public boolean isSearchAll() {
                        return false;
                    }

                    @Override
                    public String getAllSearch() {
                        return null;
                    }

                    @Override
                    public String getSection() {
                        if (objName.isRegex()) {
                            return "^" + appSection + "$";
                        } else {
                            return appSection;
                        }
                    }

                    @Override
                    public String getObjName() {
                        return objName.getName();
                    }

                    @Override
                    public String getOption() {
                        return null;

                    }

                    @Override
                    public String getValue() {
                        return null;
                    }

                };

                us = new IUpdateSettings() {

                    @Override
                    public boolean isMakeBackup() {
                        return false;
                    }

                    @Override
                    public IUpdateSettings.UpdateAction getUpdateAction() {
                        return IUpdateSettings.UpdateAction.ADD_SECTION;
                    }

                    @Override
                    public String replaceWith(String currentValue) {

                        return null;

                    }

                    @Override
                    public String getReplaceKey(String currentValue) {
                        return null;
                    }

                    @Override
                    public Collection<UserProperties> getAddedKVP() {
                        Collection<UserProperties> ret = new ArrayList<>();
                        for (Map.Entry<String, Integer> entry : cacheOptions.entrySet()) {
                            ret.add(new UserProperties(appSection, entry.getKey(), entry.getValue().toString()));

                        }
                        return ret;
                    }
                };
//</editor-fold>

            } else {
                //<editor-fold defaultstate="collapsed" desc="turnOff">
                searchSettings = new ISearchSettings() {

                    @Override
                    public boolean isCaseSensitive() {
                        return objName.isCaseSensitive();
                    }

                    @Override
                    public boolean isRegex() {
                        return true;
                    }

                    @Override
                    public boolean isFullOutputSelected() {
                        return false;
                    }

                    @Override
                    public boolean isSearchAll() {
                        return false;
                    }

                    @Override
                    public String getAllSearch() {
                        return null;
                    }

                    @Override
                    public String getSection() {
                        return "^" + appSection + "$";
                    }

                    @Override
                    public String getObjName() {
                        return objName.getName();
                    }

                    @Override
                    public String getOption() {
                        return StringUtils.join(cacheOptions.keySet(), "|");

                    }

                    @Override
                    public String getValue() {
                        return null;
                    }

                };

                us = new IUpdateSettings() {

                    @Override
                    public boolean isMakeBackup() {
                        return false;
                    }

                    @Override
                    public IUpdateSettings.UpdateAction getUpdateAction() {
                        return IUpdateSettings.UpdateAction.REMOVE;
                    }

                    @Override
                    public String replaceWith(String currentValue) {
                        return null;
                    }

                    @Override
                    public String getReplaceKey(String currentValue) {
                        return null;
                    }

                    @Override
                    public Collection<UserProperties> getAddedKVP() {
                        Collection<UserProperties> ret = new ArrayList<>();
                        for (Map.Entry<String, Integer> entry : cacheOptions.entrySet()) {
                            ret.add(new UserProperties(appSection, entry.getKey(), entry.getValue().toString()));

                        }
                        return ret;
                    }
                };
//</editor-fold>

            }

            ICfgObjectFoundProc foundProc = new ICfgObjectFoundProc() {
                @Override
                public boolean proc(CfgObject obj, KeyValueCollection kv, int current, int total) {
//                    kv = getAllValuesInSection(obj, seearchSettings);
//                    kv = new KeyValueCollection();
//                    kv.addList(seearchSettings.getSection(), ((CfgScript) obj).getUserProperties().getList(seearchSettings.getSection()));
//                            ((CfgTransaction) obj).getUserProperties().getList(seearchSettings.getSection());
                    logger.info("found " + obj.toString() + "\n kv: " + kv.toString());

//                                int showYesNoPanel = showYesNoPanel(pn.getSearchSummary(), obj.toString() + "\n kv: " + kv.toString());
                    try {
                        if (yesToAll) {
                            upd.updateObj(us, obj, kv, configServerManager);
                        } else {
                            upd = new UpdateCFGObjectProcessor(configServerManager, obj.getObjectType(), theForm);
                            String estimateUpdateObj = upd.estimateUpdateObj(us, obj, kv, configServerManager);
                            if (estimateUpdateObj != null) //
                            {
                                switch (showYesNoPanel(searchSettings.toString(), "Object " + current + " of matched " + total
                                        + "\ntoUpdate: \n----------------------\n" + estimateUpdateObj
                                        + "\n-->\n" + obj.toString() + "\n\t kv: " + kv.toString()
                                )) {
                                    case YES_TO_ALL:
                                        if (JOptionPane.showConfirmDialog(theForm,
                                                "Are you sure you want to modify this and all following found objects?",
                                                "Please confirm",
                                                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {

                                            yesToAll = true;
                                            upd.updateObj(us, obj, kv, configServerManager);
                                            break;
                                        }
                                        break;

                                    case JOptionPane.YES_OPTION:
                                        upd.updateObj(us, obj, kv, configServerManager);
                                        break;

                                    case JOptionPane.NO_OPTION:
                                        break;

                                    case JOptionPane.CANCEL_OPTION:
                                        return false;
                                }
                            }
                        }
                    } catch (ProtocolException protocolException) {
                        showError("Exception while updating: " + protocolException.getMessage());
                    } catch (HeadlessException headlessException) {
                        showError("Exception while updating: " + headlessException.getMessage());
                    }

                    return true;
                }

            };

            try {

                CfgScriptQuery query = new CfgScriptQuery();
                query.setScriptType(CfgScriptType.CFGEnhancedRouting);

                if (findObjects(
                        query,
                        CfgScript.class,
                        new IKeyValueProperties() {
                    @Override
                    public KeyValueCollection getProperties(CfgObject obj) {
                        return ((CfgScript) obj).getUserProperties();
                    }

                    @Override
                    public Collection<String> getName(CfgObject obj) {
                        Collection<String> ret = new ArrayList<>();
                        ret.add(((CfgScript) obj).getName());
                        return ret;
                    }
                },
                        new FindWorker(searchSettings), true, foundProc)) {

                }

            } catch (ConfigException ex) {
                java.util.logging.Logger.getLogger(AppForm.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InterruptedException ex) {
                java.util.logging.Logger.getLogger(AppForm.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        logger.info("Done buffering update");
    }

    private void setQueryNameFilter(CfgQuery query, String name, boolean regex) {
        if (StringUtils.isNotEmpty(name)) {
            if (query instanceof CfgTransactionQuery) {
                ((CfgTransactionQuery) query).setName(getNamePattern(name, regex));
            } else if (query instanceof CfgScriptQuery) {
                ((CfgScriptQuery) query).setName(getNamePattern(name, regex));
            } else {
                theForm.requestOutput("!!! not supported filter for " + query);
            }
        }
    }

    private String getNamePattern(String name, boolean regex) {
        if (regex) {
            return name;
        } else {
            return "*" + name + "*";
        }
    }

    class RequestDialog extends StandardDialog {

        private JPanel contentPanel;

        private RequestDialog(Window parent, JPanel contentPanel, JMenuItem mi) {
            this(parent, contentPanel);
            setTitle(mi.getText() + " parameters");

        }

        private RequestDialog(Window parent, JPanel contentPanel) {
            super(parent);
            this.contentPanel = contentPanel;
            setTitle("Enter request parameters");
        }

        @Override
        public JPanel getContentPanel() {
            return contentPanel;
        }

        @Override
        public JComponent createBannerPanel() {
            return null;
        }

        @Override
        public JComponent createContentPanel() {
            JPanel content = new JPanel();
            content.setLayout(new BoxLayout(content, BoxLayout.PAGE_AXIS));
            content.add(contentPanel);

            return content;
        }

        @Override
        public ButtonPanel createButtonPanel() {
            ButtonPanel buttonPanel = new ButtonPanel();
            JButton cancelButton = new JButton();
            buttonPanel.addButton(cancelButton);

            cancelButton.setAction(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    setDialogResult(RESULT_CANCELLED);
                    setVisible(false);
                    dispose();
                }
            });
            cancelButton.setText("Close");

            JButton jbOK = new JButton("OK");
            buttonPanel.addButton(jbOK);

//            listPane.add(jbFilter);
            jbOK.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    setDialogResult(RESULT_AFFIRMED);
                    dispose();
                }
            });

            String act = "OK";

            setDefaultCancelAction(cancelButton.getAction());
            setDefaultAction(jbOK.getAction());
            getRootPane().setDefaultButton(jbOK);

            buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            buttonPanel.setSizeConstraint(ButtonPanel.NO_LESS_THAN); // since the checkbox is quite wide, we don't want all of them have the same size.
            return buttonPanel;
        }

        public boolean doShow(String title, IConfigPanel onShow) {
            if (onShow != null) {
                onShow.showProc();
            }
            return doShow(title);
        }

        public boolean doShow(String Title) {
            setTitle(Title);
            return doShow();
        }

        public boolean doShow() {

//            setModal(true);
            pack();
            if (contentPanel instanceof ISearchCommon) {
                ((ISearchCommon) contentPanel).setChoices(searchValues);
            }

//            ScreenInfo.CenterWindow(this);
            setLocationRelativeTo(getParent());
//            setVisible(true);
            setAlwaysOnTop(true);
            java.awt.EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    toFront();

                }
            });
//            setVisible(false);
            setVisible(true);

            if (getDialogResult() == StandardDialog.RESULT_AFFIRMED) {
                if (contentPanel instanceof ISearchCommon) {
                    Collection<String> choices = ((ISearchCommon) contentPanel).getChoices();
                    if (choices != null && !choices.isEmpty()) {
                        for (String choice : choices) {
                            searchValues.add(choice);
                        }
                    }
                }
                return true;
            } else {
                return false;
            }

        }
    }

}
