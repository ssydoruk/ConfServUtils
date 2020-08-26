/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.airbnb.confservutils;

import Utils.InfoPanel;
import Utils.Pair;
import static Utils.Swing.checkBoxSelection;
import Utils.TableColumnAdjuster;
import Utils.ValuesEditor;
import com.genesyslab.platform.applicationblocks.com.CfgObject;
import com.genesyslab.platform.applicationblocks.com.CfgQuery;
import com.genesyslab.platform.applicationblocks.com.ConfigException;
import com.genesyslab.platform.applicationblocks.com.ICfgObject;
import com.genesyslab.platform.applicationblocks.com.IConfService;
import com.genesyslab.platform.applicationblocks.com.objects.*;
import com.genesyslab.platform.applicationblocks.com.queries.CfgApplicationQuery;
import com.genesyslab.platform.applicationblocks.com.queries.CfgEnumeratorQuery;
import com.genesyslab.platform.applicationblocks.com.queries.CfgEnumeratorValueQuery;
import com.genesyslab.platform.applicationblocks.com.queries.CfgHostQuery;
import com.genesyslab.platform.applicationblocks.com.queries.CfgScriptQuery;
import com.genesyslab.platform.applicationblocks.com.queries.CfgTransactionQuery;
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
import confserverbatch.ObjectExistAction;
import confserverbatch.SwitchLookup;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.logging.Level;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.border.TitledBorder;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
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
public final class AppForm extends javax.swing.JFrame {

    StoredSettings ds = null;
    private static final Logger logger = LogManager.getLogger();
    private final AppForm theForm;

    public void runGui() throws FileNotFoundException, IOException {
        loadConfig();
        setVisible(true);
    }

    private void loadConfig() throws FileNotFoundException, IOException {
        final File f = new File(profile);
        // Gson gson = new Gson();
        if (f.exists()) {
            final Gson gson = new GsonBuilder().enableComplexMapKeySerialization().serializeNulls()
                    .setDateFormat(DateFormat.LONG).setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
                    .setPrettyPrinting().create();

            try (InputStreamReader reader = new InputStreamReader(new FileInputStream(f))) {
                ds = gson.fromJson(reader, StoredSettings.class);
            }
        } else {
            ds = new StoredSettings();
        }

        // <editor-fold defaultstate="collapsed" desc="load users">
        cbUser.removeAllItems();
        for (final String user : ds.getUsers()) {
            cbUser.addItem(user);
        }
        if (cbUser.getItemCount() > 0) {
            cbUser.setSelectedIndex(0);
        }
        // </editor-fold>
        loadConfigServers();
        pfPassword.setText(getPassword());
        if (cbConfigServer.getItemCount() > 0) {
            cbConfigServer.setSelectedIndex(0);
        }

    }

    private void configServerChanged(final ActionEvent e) {
        final JComboBox cb = (JComboBox) e.getSource();
        setTitle("ConfigServer query - " + checkBoxSelection(cb));

    }

    private void loadConfigServers() {
        // <editor-fold defaultstate="collapsed" desc="load configservers">
        final ActionListener[] actionListeners = cbConfigServer.getActionListeners();
        for (final ActionListener actionListener : actionListeners) {
            cbConfigServer.removeActionListener(actionListener);
        }
        cbConfigServer.removeAllItems();
        final DefaultComboBoxModel mod = (DefaultComboBoxModel) cbConfigServer.getModel();
        for (final StoredSettings.ConfServer cs : ds.getConfigServers()) {
            mod.addElement(cs);
        }
        if (mod.getSize() > 0) {
            cbConfigServer.setSelectedIndex(0);
        }

        cbConfigServer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                configServerChanged(e);
            }

        });
        // </editor-fold>

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

        cbUser.addActionListener((final ActionEvent e) -> {
            userEdited(e);
        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent e) {
                formClosing(e);
            }

        });

        Utils.ScreenInfo.CenterWindow(this);

        textEncryptor = new StrongTextEncryptor();
        textEncryptor.setPassword(ConfigConnection.class.getName());
        connectionStatusChanged();
        jmExit.addActionListener((final ActionEvent e) -> {
            System.out.println("-1-");
        });

        jmExit.addMenuListener(new MenuListener() {
            @Override
            public void menuSelected(final MenuEvent e) {
                saveConfig();
                System.exit(0);
            }

            @Override
            public void menuDeselected(final MenuEvent e) {

            }

            @Override
            public void menuCanceled(final MenuEvent e) {

            }
        });

    }

    ConfigServerManager configServerManager;

    StrongTextEncryptor textEncryptor;

    private int selectedIndex = -1;

    private void userEdited(final ActionEvent e) {
        final int index = cbUser.getSelectedIndex();
        if (index >= 0) {
            selectedIndex = index;
        } else if ("comboBoxEdited".equals(e.getActionCommand())) {
            final DefaultComboBoxModel model = (DefaultComboBoxModel) cbUser.getModel();
            final Object newValue = model.getSelectedItem();
            if (selectedIndex >= 0) {
                model.removeElementAt(selectedIndex);
            }
            model.addElement(newValue);
            cbUser.setSelectedItem(newValue);
            selectedIndex = model.getIndexOf(newValue);
            ds.updateUsers(model);
        }

    }

    private void formClosing(final WindowEvent e) {
        saveConfig();
    }

    public void saveConfig() {
        ds.setPassword(textEncryptor.encrypt(new String(pfPassword.getPassword())));
        final int selectedIndex1 = cbConfigServer.getSelectedIndex();
        ds.setLastUsedConfigServer(cbConfigServer.getSelectedIndex());

        final Gson gson = new GsonBuilder().enableComplexMapKeySerialization().serializeNulls()
                .setDateFormat(DateFormat.LONG).setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
                .setPrettyPrinting().setVersion(1.0).create();

        try {
            try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(profile))) {
                gson.toJson(ds, writer);
            }
        } catch (final FileNotFoundException ex) {
            logger.log(org.apache.logging.log4j.Level.FATAL, ex);
        } catch (final IOException ex) {
            logger.log(org.apache.logging.log4j.Level.FATAL, ex);
        }

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated
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
        spOutputScroll = new javax.swing.JScrollPane();
        taOutput = new javax.swing.JTextArea();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        miObjByDBID = new javax.swing.JMenuItem();
        miAppByIP = new javax.swing.JMenuItem();
        miAppByOption = new javax.swing.JMenuItem();
        miObjectByAnnex = new javax.swing.JMenuItem();
        miBusinessAttribute = new javax.swing.JMenuItem();
        jMenu6 = new javax.swing.JMenu();
        miLoginsWithoutAgent = new javax.swing.JMenuItem();
        miExtensionWithoutPlace = new javax.swing.JMenuItem();
        jMenu7 = new javax.swing.JMenu();
        miCheckDNPlaceExists = new javax.swing.JMenuItem();
        miFindLDAPs = new javax.swing.JMenuItem();
        miFindLDAPsForUsers = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        miAnnexSearchReplace = new javax.swing.JMenuItem();
        miAppOptionsReplace = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        miOneORS = new javax.swing.JMenuItem();
        miAllORSs = new javax.swing.JMenuItem();
        jMenu4 = new javax.swing.JMenu();
        miBufferingOff = new javax.swing.JMenuItem();
        miBufferingOn = new javax.swing.JMenuItem();
        miLoadStrategy = new javax.swing.JMenuItem();
        miRestartService = new javax.swing.JMenuItem();
        jMenu5 = new javax.swing.JMenu();
        miImportCSV = new javax.swing.JMenuItem();
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
        spOutputScroll.setViewportView(taOutput);

        jpOutput.add(spOutputScroll, java.awt.BorderLayout.CENTER);

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

        jMenu6.setText("ConfigDB verification");

        miLoginsWithoutAgent.setText("Login IDs without agent");
        miLoginsWithoutAgent.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miLoginsWithoutAgentActionPerformed(evt);
            }
        });
        jMenu6.add(miLoginsWithoutAgent);

        miExtensionWithoutPlace.setText("Extension without link to place");
        miExtensionWithoutPlace.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miExtensionWithoutPlaceActionPerformed(evt);
            }
        });
        jMenu6.add(miExtensionWithoutPlace);

        jMenu1.add(jMenu6);

        jMenu7.setText("CSV operations");

        miCheckDNPlaceExists.setText("Check if Place /DN exists");
        miCheckDNPlaceExists.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miCheckDNPlaceExistsActionPerformed(evt);
            }
        });
        jMenu7.add(miCheckDNPlaceExists);

        miFindLDAPs.setText("Find users with LDAPs");
        miFindLDAPs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miFindLDAPsActionPerformed(evt);
            }
        });
        jMenu7.add(miFindLDAPs);

        miFindLDAPsForUsers.setText("Find LDAP for userNames");
        miFindLDAPsForUsers.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miFindLDAPsForUsersActionPerformed(evt);
            }
        });
        jMenu7.add(miFindLDAPsForUsers);

        jMenu1.add(jMenu7);

        jMenuItem1.setText("folderSearch");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Update");

        miAnnexSearchReplace.setText("Object find and process");
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

        miLoadStrategy.setText("Load ORS strategy");
        miLoadStrategy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miLoadStrategyActionPerformed(evt);
            }
        });
        jMenu2.add(miLoadStrategy);

        miRestartService.setText("Restart service");
        miRestartService.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miRestartServiceActionPerformed(evt);
            }
        });
        jMenu2.add(miRestartService);

        jMenu5.setText("CSV operations");

        miImportCSV.setText("Import Place / DN");
        miImportCSV.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miImportCSVActionPerformed(evt);
            }
        });
        jMenu5.add(miImportCSV);

        jMenu2.add(jMenu5);

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

    private void miFindLDAPsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miFindLDAPsActionPerformed
        verifyLDAP();
    }//GEN-LAST:event_miFindLDAPsActionPerformed

    private void miFindLDAPsForUsersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miFindLDAPsForUsersActionPerformed
        verifyUserNameLDAP();
    }//GEN-LAST:event_miFindLDAPsForUsersActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        folderSearch();

    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void miCheckDNPlaceExistsActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_miCheckDNPlaceExistsActionPerformed
        verifyCSV();
    }// GEN-LAST:event_miCheckDNPlaceExistsActionPerformed

    private void miLoginsWithoutAgentActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_miLoginsWithoutAgentActionPerformed
        getLoginsWithoutAgent();
    }// GEN-LAST:event_miLoginsWithoutAgentActionPerformed

    private void miExtensionWithoutPlaceActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_miExtensionWithoutPlaceActionPerformed
        getExtensionsWithoutPlace();
    }// GEN-LAST:event_miExtensionWithoutPlaceActionPerformed

    private void btEditConfgServActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btEditConfgServActionPerformed

        if (confServEditor == null) {
            confServEditor = new ValuesEditor((Window) this.getRootPane().getParent(), "Config Server profiles",
                    "Select %d profiles");

        }
        final ArrayList<Object[]> values = new ArrayList<>();
        for (final StoredSettings.ConfServer configServer : ds.getConfigServers()) {
            final Object[] v = new Object[4];
            v[0] = configServer.getProfile();
            v[1] = configServer.getHost();
            v[2] = configServer.getPort();
            v[3] = configServer.getApp();
            values.add(v);
        }
        // for (DownloadSettings.LFMTHostInstance hi : ds.getLfmtHostInstances()) {
        // values.add(new Object[]{hi.getHost(), hi.getInstance(), hi.getBaseDir()});
        // }
        confServEditor.setData(new Object[]{"Profile", "CS host", "CS port", "CME application"}, values);
        confServEditor.doShow();
        ds.loadConfServs(confServEditor.getData());
        loadConfigServers();
        saveConfig();

    }// GEN-LAST:event_btEditConfgServActionPerformed

    private void btConnectActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btConnectActionPerformed
        connectToConfigServer();
        connectionStatusChanged();
    }// GEN-LAST:event_btConnectActionPerformed

    private void pfPasswordActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_pfPasswordActionPerformed
        System.out.println(pfPassword.getPassword());
    }// GEN-LAST:event_pfPasswordActionPerformed

    private void btDisconnectActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btDisconnectActionPerformed
        try {
            configServerManager.disconnect();
        } catch (final ProtocolException | IllegalStateException | InterruptedException ex) {
            java.util.logging.Logger.getLogger(AppForm.class.getName()).log(Level.SEVERE, null, ex);
        }
        connectionStatusChanged();
    }// GEN-LAST:event_btDisconnectActionPerformed

    RequestDialog objByDBID = null;

    private void miObjByDBIDActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_miObjByDBIDActionPerformed
        if (objByDBID == null) {
            objByDBID = new RequestDialog(this, new ObjByDBID(), (JMenuItem) evt.getSource());
        }
        if (objByDBID.doShow()) {

            // enableComponents(this, false);
            runInThread(() -> {
                if (connectToConfigServer()) {
                    final ObjByDBID pn = (ObjByDBID) objByDBID.getContentPanel();
                    try {
                        requestOutput("Request: " + pn.getSearchSummary());
                        final CfgObjectType t = pn.getSelectedItem();
                        final int dbid = pn.getValue();
                        final ICfgObject retrieveObject = configServerManager.retrieveObject(t, dbid);
                        if (retrieveObject != null) {
                            if (pn.isFullOutput()) {
                                requestOutput(retrieveObject.toString());
                            } else {
                                final StringBuilder buf = new StringBuilder();

                                buf.append("Object type:").append(retrieveObject.getObjectType()).append(" DBID:")
                                        .append(retrieveObject.getObjectDbid()).append(" name: ")
                                        .append(ConfigServerManager.getObjName(retrieveObject));
                                requestOutput(buf.toString());
                            }
                        } else {
                            requestOutput("Not found object DBID:" + dbid + " type:" + t);
                        }
                    } catch (final ConfigException ex) {
                        showException("Error", ex);
                    }
                }
            });

        }
    }// GEN-LAST:event_miObjByDBIDActionPerformed

    RequestDialog appByIP = null;

    private boolean componentsEnabled;
    private HashMap<Component, Boolean> savedEnabled = null;

    public void enableComponentsRecource(final Container container, final boolean enable) {
        final Component[] components = container.getComponents();
        for (final Component component : components) {
            savedEnabled.put(component, component.isEnabled());
            component.setVisible(enable);
            if (component instanceof Container) {
                enableComponentsRecource((Container) component, enable);
            }
        }
    }

    public void enableComponents(final Container container, final boolean enable) {
        if (enable != componentsEnabled) {
            logger.info("enableComponents: " + enable);
            if (savedEnabled != null) {
                for (final Map.Entry<Component, Boolean> entry : savedEnabled.entrySet()) {
                    entry.getKey().setVisible(entry.getValue());
                }
                savedEnabled = null;
            } else {
                savedEnabled = new HashMap<>();
                enableComponentsRecource(container, enable);
            }
            componentsEnabled = enable;
            // container.invalidate();
        }
    }

    private void miAppByIPActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_miAppByIPActionPerformed
        if (appByIP == null) {
            appByIP = new RequestDialog(this, new AppByIP(), (JMenuItem) evt.getSource());
        }
        if (appByIP.doShow()) {
            runAppByIPActionPerformed(evt);
        }
    }// GEN-LAST:event_miAppByIPActionPerformed

    private void btClearOutputActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btClearOutputActionPerformed
        taOutput.setText("");
    }// GEN-LAST:event_btClearOutputActionPerformed

    private void jmExitActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jmExitActionPerformed
        // TODO add your handling code here:
        logger.info("jMenu3ActionPerformed pressed " + evt.getActionCommand());
        // theForm.dispose();
        System.exit(0);
    }// GEN-LAST:event_jmExitActionPerformed

    RequestDialog appByOption;

    private void miAppByOptionActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_miAppByOptionActionPerformed
        if (appByOption == null) {
            appByOption = new RequestDialog(this, new AppByOptions(), (JMenuItem) evt.getSource());
        }
        if (appByOption.doShow()) {
            appByOptionThread((AppByOptions) appByOption.getContentPanel());

        }
    }// GEN-LAST:event_miAppByOptionActionPerformed

    RequestDialog objByAnnex;

    private void miObjectByAnnexActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_miObjectByAnnexActionPerformed
        if (objByAnnex == null) {
            objByAnnex = new RequestDialog(this, new ObjByAnnex(), (JMenuItem) evt.getSource());
        }
        final JFrame f = this;
        if (objByAnnex.doShow()) {
            runObjByAnnexThread(evt);

        }
    }// GEN-LAST:event_miObjectByAnnexActionPerformed

    RequestDialog bussAttr;

    private void runBussAttrPerformed(final ActionEvent evt) {
        if (connectToConfigServer()) {

            final BussAttr pn = (BussAttr) bussAttr.getContentPanel();
            try {
                requestOutput("Request: " + pn.getSearchSummary());

                // <editor-fold defaultstate="collapsed" desc="iSearchSettings">
                final ISearchSettings seearchSettings = new ISearchSettings() {
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
                // </editor-fold>

                if (pn.iscbAttrSelected()) {
                    final CfgEnumeratorQuery query = new CfgEnumeratorQuery(configServerManager.getService());

                    configServerManager.findObjects(query, CfgEnumerator.class, new IKeyValueProperties() {
                        @Override
                        public KeyValueCollection getProperties(final CfgObject obj) {
                            return ((CfgEnumerator) obj).getUserProperties();
                        }

                        @Override
                        public Collection<String> getName(final CfgObject obj) {
                            final Collection<String> ret = new ArrayList<>();
                            ret.add(((CfgEnumerator) obj).getName());
                            ret.add(((CfgEnumerator) obj).getDescription());
                            ret.add(((CfgEnumerator) obj).getDisplayName());
                            return ret;
                        }
                    }, new FindWorker(seearchSettings), true, null);

                }
                if (pn.iscbAttrValueSelected()) {
                    final CfgEnumeratorValueQuery query = new CfgEnumeratorValueQuery(configServerManager.getService());

                    configServerManager.findObjects(query, CfgEnumeratorValue.class, new IKeyValueProperties() {
                        @Override
                        public KeyValueCollection getProperties(final CfgObject obj) {
                            return ((CfgEnumeratorValue) obj).getUserProperties();
                        }

                        @Override
                        public Collection<String> getName(final CfgObject obj) {
                            final Collection<String> ret = new ArrayList<>();
                            ret.add(((CfgEnumeratorValue) obj).getName());
                            ret.add(((CfgEnumeratorValue) obj).getDescription());
                            ret.add(((CfgEnumeratorValue) obj).getDisplayName());
                            return ret;
                        }
                    }, new FindWorker(seearchSettings), true, null);

                }

            } catch (final ConfigException ex) {
                showException("Error", ex);

            } catch (final InterruptedException ex) {
                java.util.logging.Logger.getLogger(AppForm.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        logger.info("affirm");
    }

    private void miBusinessAttributeActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_miBusinessAttributeActionPerformed
        if (bussAttr == null) {
            bussAttr = new RequestDialog(this, new BussAttr(), (JMenuItem) evt.getSource());
        }
        final JFrame f = this;
        if (bussAttr.doShow()) {
            runInThread(new IThreadedFun() {
                @Override
                public void fun() {
                    runBussAttrPerformed(evt);

                }
            });

        } // TODO add your handling code here:
    }// GEN-LAST:event_miBusinessAttributeActionPerformed

    private void btCancelActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btCancelActionPerformed
        if (worker != null && !worker.isDone()) {
            worker.cancel(true);
        }
    }// GEN-LAST:event_btCancelActionPerformed

    private void cbConfigServerActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_cbConfigServerActionPerformed
        // TODO add your handling code here:

    }// GEN-LAST:event_cbConfigServerActionPerformed

    RequestDialog annexReplace;
    AnnexReplace panelAnnexReplace;

    RequestDialog appOptionsChange;
    AppOptionsChange panelAppOptionsChange;

    RequestDialog appRestartServices;
    RestartServices panelRestartServices;

    InfoPanel infoDialog = null;
    ObjectFound pn1 = null;

    public int showYesNoPanel(final String infoMsg, final String msg) {

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

    private KeyValueCollection getAllValuesInSection(final CfgObject obj, final ISearchSettings seearchSettings) {
        throw new UnsupportedOperationException("Not supported yet."); // To change body of generated methods, choose
        // Tools | Templates.
    }

    private void miAnnexSearchReplaceActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_miAnnexSearchReplaceActionPerformed
        yesToAll = false;
        upd = null;

        if (annexReplace == null) {
            panelAnnexReplace = new AnnexReplace(this);
            annexReplace = new RequestDialog(this, panelAnnexReplace, (JMenuItem) evt.getSource());
        }

        if (annexReplace.doShow()) {

            if (!panelAnnexReplace.checkParameters()) {
                return;
            }
            if (connectToConfigServer()) {

                final AnnexReplace pn = (AnnexReplace) annexReplace.getContentPanel();

                ICfgObjectFoundProc foundProc;
                if (pn.isActionUpdateKVP()) {
                    foundProc = (final CfgObject obj, final KeyValueCollection kv, final int current,
                            final int total) -> {
                        requestOutput("found obj #" + current + "(" + total + ") - " + obj.toString() + "\n kv: "
                                + kv.toString());
                        // int showYesNoPanel = showYesNoPanel(pn.getSearchSummary(), obj.toString() +
                        // "\n kv: " + kv.toString());

                        try {
                            if (yesToAll) {
                                if (upd != null) {
                                    upd.updateObj(pn, obj, kv);
                                }

                            } else {
                                upd = new UpdateCFGObjectProcessor(configServerManager, obj.getObjectType(), theForm);
                                final String estimateUpdateObj = upd.estimateUpdateObj(pn, obj, kv);
                                switch (showYesNoPanel(pn.getSearchSummaryHTML(),
                                        "Object " + current + " of matched " + total
                                        + "\ntoUpdate: \n----------------------\n" + estimateUpdateObj
                                        + "\n-->\n" + obj.toString() + "\n\t kv: " + kv.toString())) {
                                    case YES_TO_ALL:
                                        if (JOptionPane.showConfirmDialog(theForm,
                                                "Are you sure you want to modify this and all following found objects?",
                                                "Please confirm", JOptionPane.YES_NO_OPTION,
                                                JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {

                                            yesToAll = true;
                                            upd.updateObj(pn, obj, kv);
                                            break;
                                        }
                                        break;

                                    case JOptionPane.YES_OPTION:
                                        upd.updateObj(pn, obj, kv);
                                        break;

                                    case JOptionPane.NO_OPTION:
                                        break;

                                    case JOptionPane.CANCEL_OPTION:
                                        return false;
                                }
                            }
                        } catch (final ProtocolException | HeadlessException protocolException) {
                            showError("Exception while updating: " + protocolException.getMessage());
                        }

                        return true;
                    };
                } else { // delete object
                    foundProc = (final CfgObject obj, final KeyValueCollection kv, final int current,
                            final int total) -> {
                        requestOutput("found obj #" + current + "(" + total + ") - " + obj.toString() + "\n kv: "
                                + kv.toString());
                        // int showYesNoPanel = showYesNoPanel(pn.getSearchSummary(), obj.toString() +
                        // "\n kv: " + kv.toString());

                        try {
                            if (yesToAll) {
                                if (upd != null) {
                                    upd.updateObj(pn, obj, kv);
                                }

                            } else {
                                upd = new UpdateCFGObjectProcessor(configServerManager, obj.getObjectType(), theForm);
                                final String estimateUpdateObj = upd.estimateUpdateObj(pn, obj, kv);
                                switch (showYesNoPanel(pn.getSearchSummaryHTML(),
                                        "Object " + current + " of matched " + total + "\n-->\n" + obj.toString()
                                        + "\n\t kv: " + kv.toString() + "\ntoUpdate: \n----------------------\n"
                                        + estimateUpdateObj)) {
                                    case YES_TO_ALL:
                                        if (JOptionPane.showConfirmDialog(theForm,
                                                "Are you sure you want to modify this and all following found objects?",
                                                "Please confirm", JOptionPane.YES_NO_OPTION,
                                                JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {

                                            yesToAll = true;
                                            upd.updateObj(pn, obj, kv);
                                            break;
                                        }
                                        break;

                                    case JOptionPane.YES_OPTION:
                                        upd.updateObj(pn, obj, kv);
                                        break;

                                    case JOptionPane.NO_OPTION:
                                        break;

                                    case JOptionPane.CANCEL_OPTION:
                                        return false;
                                }
                            }
                        } catch (final ProtocolException | HeadlessException protocolException) {
                            showError("Exception while updating: " + protocolException.getMessage());
                        }
                        return true;
                    };
                }

                runInThread(() -> {
                    for (final CfgObjectType value : pn.getSelectedObjectTypes()) {
                        try {
                            if (!configServerManager.doTheSearch(value, pn, false, true, foundProc)) {
                                break;
                            }
                        } catch (final ConfigException | InterruptedException ex) {
                            java.util.logging.Logger.getLogger(AppForm.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                });

                if (upd != null && upd.isObjectsUpdated()) {
                    configServerManager.clearCache();
                }
            }

        }
    }// GEN-LAST:event_miAnnexSearchReplaceActionPerformed

    private void miAllORSsActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_miAllORSsActionPerformed
        modifyCluster(false);
    }// GEN-LAST:event_miAllORSsActionPerformed

    private void modifyCluster(final boolean oneORS) {
        upd = null;
        yesToAll = false;

        final FindObject objName = getObjName(
                CfgTransaction.class.getSimpleName() + " type " + CfgTransactionType.CFGTRTList);

        if (objName == null) {
            return;
        }

        if (connectToConfigServer()) {

            final ISearchSettings seearchSettings = new ISearchSettings() {
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
                public IUpdateSettings.KVPUpdateAction getKVPUpdateAction() {
                    return IUpdateSettings.KVPUpdateAction.RENAME_SECTION;
                }

                @Override
                public String KVPreplaceWith(final String currentValue) {
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

                public void setOneActive(final boolean oneActive) {
                    this.oneActive = oneActive;
                }

                @Override
                public String getReplaceKey(final String currentValue) {
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
                    throw new UnsupportedOperationException("Not supported yet."); // To change body of generated
                    // methods, choose Tools | Templates.
                }

                @Override
                public ObjectUpdateAction getObjectUpdateAction() {
                    return ObjectUpdateAction.KVP_CHANGE;
                }

                @Override
                public boolean isDeleteDependendObjects() {
                    throw new UnsupportedOperationException("Not supported yet."); // To change body of generated
                    // methods, choose Tools | Templates.
                }
            }

            final AUpdateSettings us = new AUpdateSettings();

            final ICfgObjectFoundProc foundProc = new ICfgObjectFoundProc() {
                @Override
                public boolean proc(final CfgObject obj, KeyValueCollection kv, final int current, final int total) {
                    // kv = getAllValuesInSection(obj, seearchSettings);
                    kv = new KeyValueCollection();
                    kv.addList(seearchSettings.getSection(),
                            ((CfgTransaction) obj).getUserProperties().getList(seearchSettings.getSection()));
                    // ((CfgTransaction)
                    // obj).getUserProperties().getList(seearchSettings.getSection());
                    logger.info("found " + obj.toString() + "\n kv: " + kv.toString());

                    // int showYesNoPanel = showYesNoPanel(pn.getSearchSummary(), obj.toString() +
                    // "\n kv: " + kv.toString());
                    try {
                        if (yesToAll) {
                            us.setOneActive(false);
                            upd.updateObj(us, obj, kv);
                        } else {
                            us.setOneActive(false);
                            upd = new UpdateCFGObjectProcessor(configServerManager, obj.getObjectType(), theForm);
                            final String estimateUpdateObj = upd.estimateUpdateObj(us, obj, kv);
                            switch (showYesNoPanel(seearchSettings.toString(),
                                    "Object " + current + " of matched " + total
                                    + "\ntoUpdate: \n----------------------\n" + estimateUpdateObj + "\n-->\n"
                                    + obj.toString() + "\n\t kv: " + kv.toString())) {
                                case YES_TO_ALL:
                                    if (JOptionPane.showConfirmDialog(theForm,
                                            "Are you sure you want to modify this and all following found objects?",
                                            "Please confirm", JOptionPane.YES_NO_OPTION,
                                            JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {

                                        yesToAll = true;
                                        us.setOneActive(false);
                                        upd.updateObj(us, obj, kv);
                                        break;
                                    }
                                    break;

                                case JOptionPane.YES_OPTION:
                                    us.setOneActive(false);
                                    upd.updateObj(us, obj, kv);
                                    break;

                                case JOptionPane.NO_OPTION:
                                    break;

                                case JOptionPane.CANCEL_OPTION:
                                    return false;
                            }
                        }
                    } catch (final ProtocolException | HeadlessException protocolException) {
                        showError("Exception while updating: " + protocolException.getMessage());
                    }

                    return true;
                }

            };

            try {

                final CfgTransactionQuery query = new CfgTransactionQuery();
                // setQueryNameFilter(query, objName.getName(), objName.isRegex());
                query.setObjectType(CfgTransactionType.CFGTRTList);

                if (configServerManager.findObjects(query, CfgTransaction.class, new IKeyValueProperties() {
                    @Override
                    public KeyValueCollection getProperties(final CfgObject obj) {
                        return ((CfgTransaction) obj).getUserProperties();
                    }

                    @Override
                    public Collection<String> getName(final CfgObject obj) {
                        final Collection<String> ret = new ArrayList<>();
                        ret.add(((CfgTransaction) obj).getName());
                        return ret;
                    }
                }, new FindWorker(seearchSettings), true, foundProc)) {

                }

            } catch (final ConfigException | InterruptedException ex) {
                java.util.logging.Logger.getLogger(AppForm.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (upd != null && upd.isObjectsUpdated()) {
                configServerManager.clearCache();
            }
        }
    }

    private void miOneORSActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_miOneORSActionPerformed
        modifyCluster(true);
    }// GEN-LAST:event_miOneORSActionPerformed

    private void miBufferingOffActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_miBufferingOffActionPerformed
        strategyBuffering(false, evt);
    }// GEN-LAST:event_miBufferingOffActionPerformed

    private void miBufferingOnActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_miBufferingOnActionPerformed
        strategyBuffering(true, evt);

    }// GEN-LAST:event_miBufferingOnActionPerformed

    private void miAppOptionsReplaceActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_miAppOptionsReplaceActionPerformed
        yesToAll = false;
        upd = null;

        if (appOptionsChange == null) {
            panelAppOptionsChange = new AppOptionsChange(this);
            appOptionsChange = new RequestDialog(this, panelAppOptionsChange, (JMenuItem) evt.getSource());
        }

        if (appOptionsChange.doShow()) {
            // showYesNoPanel(pn1.getSearchSummaryHTML(), "something" + "\n kv: " +
            // "something"+"\nsomething" + "\n kv: " + "something"+"\nsomething" + "\n kv: "
            // + "something"+"\nsomething" + "\n kv: " + "something"+"\nsomething" + "\n kv:
            // " + "something"+"\nsomething" + "\n kv: " + "something"+"\nsomething" + "\n
            // kv: " + "something"+"\nsomething" + "\n kv: " + "something");
            // if(0==1)
            // return;
            if (!panelAppOptionsChange.checkParameters()) {
                return;
            }
            if (connectToConfigServer()) {

                final ICfgObjectFoundProc foundProc = new ICfgObjectFoundProc() {
                    @Override
                    public boolean proc(final CfgObject obj, final KeyValueCollection kv, final int current,
                            final int total) {
                        // kv = getAllValuesInSection(obj, seearchSettings);
                        // kv = new KeyValueCollection();
                        // kv.addList(seearchSettings.getSection(), ((CfgScript)
                        // obj).getUserProperties().getList(seearchSettings.getSection()));
                        // ((CfgTransaction)
                        // obj).getUserProperties().getList(seearchSettings.getSection());
                        logger.info("found obj " + ConfigServerManager.getObjName(obj) + " type " + obj.getObjectType()
                                + " DBID:" + obj.getObjectDbid() + " at " + obj.getObjectPath());

                        // int showYesNoPanel = showYesNoPanel(pn.getSearchSummary(), obj.toString() +
                        // "\n kv: " + kv.toString());
                        try {
                            if (yesToAll) {
                                upd.updateObj(panelAppOptionsChange, obj, kv);
                            } else {
                                upd = new UpdateCFGObjectProcessor(configServerManager, obj.getObjectType(), theForm);
                                upd.setCustomKVPProc(new UpdateCFGObjectProcessor.ICustomKVP() {
                                    @Override
                                    public KeyValueCollection getCustomKVP(final CfgObject _obj) {
                                        return ((CfgApplication) _obj).getOptions();
                                    }
                                });
                                upd.setPropKeys("changedOptions", "deletedOptions", "options");
                                final String estimateUpdateObj = upd.estimateUpdateObj(panelAppOptionsChange, obj, kv);
                                if (estimateUpdateObj != null) //
                                {
                                    switch (showYesNoPanel(panelAppOptionsChange.getSearchSummaryHTML(),
                                            "Object " + current + " of matched " + total
                                            + "\ntoUpdate: \n----------------------\n" + estimateUpdateObj
                                            + "\n-->\n" + obj.toString() + "\n\t kv: " + kv.toString())) {
                                        case YES_TO_ALL:
                                            if (JOptionPane.showConfirmDialog(theForm,
                                                    "Are you sure you want to modify this and all following found objects?",
                                                    "Please confirm", JOptionPane.YES_NO_OPTION,
                                                    JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {

                                                yesToAll = true;
                                                upd.updateObj(panelAppOptionsChange, obj, kv);
                                                break;
                                            }
                                            break;

                                        case JOptionPane.YES_OPTION:
                                            upd.updateObj(panelAppOptionsChange, obj, kv);
                                            break;

                                        case JOptionPane.NO_OPTION:
                                            break;

                                        case JOptionPane.CANCEL_OPTION:
                                            return false;
                                    }
                                }
                            }
                        } catch (final ProtocolException | HeadlessException protocolException) {
                            showError("Exception while updating: " + protocolException.getMessage());
                        }

                        return true;
                    }

                };

                try {
                    final CfgApplicationQuery query = new CfgApplicationQuery();

                    final CfgAppType selectedAppType = panelAppOptionsChange.getSelectedAppType();
                    if (selectedAppType != null) {
                        query.setAppType(selectedAppType);
                    }
                    // String n = panelAppOptionsChange.getObjName();
                    // if (panelAppOptionsChange.isCaseSensitive() && n != null) {
                    // query.setName(n);
                    // }

                    if (configServerManager.findObjects(query, CfgApplication.class, new IKeyValueProperties() {
                        @Override
                        public KeyValueCollection getProperties(final CfgObject obj) {
                            return ((CfgApplication) obj).getOptions();
                        }

                        @Override
                        public Collection<String> getName(final CfgObject obj) {
                            final Collection<String> ret = new ArrayList<>();
                            ret.add(((CfgApplication) obj).getName());
                            return ret;
                        }
                    }, new FindWorker(panelAppOptionsChange), true, foundProc)) {

                    }

                } catch (final ConfigException | InterruptedException ex) {
                    java.util.logging.Logger.getLogger(AppForm.class.getName()).log(Level.SEVERE, null, ex);
                }
                if (upd != null && upd.isObjectsUpdated()) {
                    configServerManager.clearCache();
                }
            }
        }
    }// GEN-LAST:event_miAppOptionsReplaceActionPerformed

    RequestDialog loadORSStrategy;
    LoadORSStrategy panelLoadORSStrategy;

    private void miLoadStrategyActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jmLoadStrategyActionPerformed
        yesToAll = false;
        upd = null;

        if (loadORSStrategy == null) {
            panelLoadORSStrategy = new LoadORSStrategy(this, configServerManager);
            loadORSStrategy = new RequestDialog(this, panelLoadORSStrategy, (JMenuItem) evt.getSource());
        }

        if (connectToConfigServer()) {
            if (loadORSStrategy.doShow("Load ORS strategies on routing points", panelLoadORSStrategy)) {
                panelLoadORSStrategy.doUpdate();
            }
        }
    }// GEN-LAST:event_jmLoadStrategyActionPerformed

    private void miRestartServiceActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_miRestartServiceActionPerformed
        yesToAll = false;
        upd = null;

        if (appRestartServices == null) {
            panelRestartServices = new RestartServices(this);
            appRestartServices = new RequestDialog(this, panelRestartServices, (JMenuItem) evt.getSource());
        }

        if (appRestartServices.doShow()) {
            // showYesNoPanel(pn1.getSearchSummaryHTML(), "something" + "\n kv: " +
            // "something"+"\nsomething" + "\n kv: " + "something"+"\nsomething" + "\n kv: "
            // + "something"+"\nsomething" + "\n kv: " + "something"+"\nsomething" + "\n kv:
            // " + "something"+"\nsomething" + "\n kv: " + "something"+"\nsomething" + "\n
            // kv: " + "something"+"\nsomething" + "\n kv: " + "something");
            // if(0==1)
            // return;
            if (!panelRestartServices.checkParameters()) {
                return;
            }

            if (connectToConfigServer()) {
                final CfgApplication appNew = new CfgApplication(configServerManager.getService());
                final String remoteCommand = panelRestartServices.getRemoteCommand();

                if (StringUtils.isBlank(remoteCommand)) {
                    JOptionPane.showMessageDialog(theForm, "Remote command cannot be empty", "Cannot proceed",
                            JOptionPane.ERROR_MESSAGE);
                    return;

                } else {
                    final String[] split = StringUtils.split(remoteCommand);
                    if (split.length > 0) {
                        appNew.setCommandLine(split[0]);
                        appNew.setCommandLineArguments(
                                (split.length > 1) ? StringUtils.join(ArrayUtils.subarray(split, 1, split.length), " ")
                                        : ".");

                    }
                }

                final String remoteRestartScript = panelRestartServices.getStatusScript();
                if (StringUtils.isBlank(remoteRestartScript)) {
                    JOptionPane.showMessageDialog(theForm, "Status script parameter cannot be empty", "Cannot proceed",
                            JOptionPane.ERROR_MESSAGE);
                    return;

                }

                final ISearchSettings seearchSettings = new ISearchSettings() {
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
                        // return panelRestartServices.getName();
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

                final IUpdateSettings us = new IUpdateSettings() {

                    @Override
                    public boolean isMakeBackup() {
                        return true;
                    }

                    @Override
                    public IUpdateSettings.KVPUpdateAction getKVPUpdateAction() {
                        return IUpdateSettings.KVPUpdateAction.RENAME_SECTION;
                    }

                    @Override
                    public String KVPreplaceWith(final String currentValue) {
                        return currentValue + "1";
                    }

                    @Override
                    public String getReplaceKey(final String currentValue) {
                        return UpdateCFGObjectProcessor.getCommentedKey(currentValue);
                    }

                    @Override
                    public Collection<UserProperties> getAddedKVP() {
                        final Collection<UserProperties> ret = new ArrayList<>();
                        ret.add(new UserProperties("a", "b", "c"));
                        return ret;
                    }

                    @Override
                    public IUpdateSettings.ObjectUpdateAction getObjectUpdateAction() {
                        return ObjectUpdateAction.KVP_CHANGE;
                    }

                    @Override
                    public boolean isDeleteDependendObjects() {
                        throw new UnsupportedOperationException("Not supported yet."); // To change body of generated
                        // methods, choose Tools |
                        // Templates.
                    }
                };

                class RestoreSettings implements IUpdateSettings {

                    private UserProperties up;

                    @Override
                    public boolean isMakeBackup() {
                        return true;
                    }

                    @Override
                    public IUpdateSettings.KVPUpdateAction getKVPUpdateAction() {
                        return IUpdateSettings.KVPUpdateAction.ADD_OPTION_FORCE;
                    }

                    @Override
                    public String KVPreplaceWith(final String currentValue) {
                        return UpdateCFGObjectProcessor.uncommented(currentValue);
                    }

                    @Override
                    public String getReplaceKey(final String currentValue) {
                        return UpdateCFGObjectProcessor.uncommented(currentValue);
                    }

                    @Override
                    public Collection<UserProperties> getAddedKVP() {
                        // ret.add(new UserProperties(kv., profile, profile))s
                        if (up != null) {
                            final Collection<UserProperties> ret = new ArrayList<>();
                            ret.add(up);
                            return ret;
                        } else {
                            return null;
                        }
                    }

                    public void replaceKVP(final KeyValueCollection _kv) {
                        up = null;
                        for (final Object object : _kv) {
                            if (object instanceof KeyValuePair) {
                                final KeyValuePair kvp = (KeyValuePair) object;
                                final String section = kvp.getStringKey();
                                if (section.equals("start_stop")) {
                                    final Object value = kvp.getValue();
                                    final ValueType valueType = kvp.getValueType();
                                    if (valueType == ValueType.TKV_LIST) {
                                        for (final Object _kvInstance : (KeyValueCollection) value) {
                                            final KeyValuePair kvInstance = (KeyValuePair) _kvInstance;
                                            if (kvInstance.getStringKey().equals("start_command")) {
                                                up = new UserProperties(section, kvInstance.getStringKey(),
                                                        kvInstance.getValueAsString());
                                                return;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public ObjectUpdateAction getObjectUpdateAction() {
                        return ObjectUpdateAction.KVP_CHANGE;
                    }

                    @Override
                    public boolean isDeleteDependendObjects() {
                        throw new UnsupportedOperationException("Not supported yet."); // To change body of generated
                        // methods, choose Tools |
                        // Templates.
                    }
                }
                ;

                final RestoreSettings usRestore = new RestoreSettings();

                final CfgApplication appSaved = new CfgApplication(configServerManager.getService());

                upd = new UpdateCFGObjectProcessor(configServerManager, CfgObjectType.CFGApplication, theForm);

                // logger.debug((new Gson()).toJson(app));
                // upd.setPropKeys("changedOptions", "deletedOptions", "options");
                final ICfgObjectFoundProc foundProc = new ICfgObjectFoundProc() {
                    @Override
                    public boolean proc(final CfgObject obj, final KeyValueCollection kv, final int current,
                            final int total) {
                        // kv = getAllValuesInSection(obj, seearchSettings);
                        // kv = new KeyValueCollection();
                        // kv.addList(seearchSettings.getSection(), ((CfgScript)
                        // obj).getUserProperties().getList(seearchSettings.getSection()));
                        // ((CfgTransaction)
                        // obj).getUserProperties().getList(seearchSettings.getSection());
                        logger.info("found obj " + ConfigServerManager.getObjName(obj) + " type " + obj.getObjectType()
                                + " DBID:" + obj.getObjectDbid() + " at " + obj.getObjectPath());

                        // int showYesNoPanel = showYesNoPanel(pn.getSearchSummary(), obj.toString() +
                        // "\n kv: " + kv.toString());
                        try {
                            if (yesToAll) {
                                upd.updateObj(us, obj, kv, appNew);
                            } else {

                                final String estimateUpdateObj = upd.estimateUpdateObj(us, obj, kv, appNew);
                                if (estimateUpdateObj != null) //
                                {
                                    switch (showYesNoPanel(panelRestartServices.getSearchSummaryHTML(),
                                            "Object " + current + " of matched " + total
                                            + "\ntoUpdate: \n----------------------\n" + estimateUpdateObj
                                            + "\n-->\n" + obj.toString() + "\n\t kv: " + kv.toString())) {
                                        case YES_TO_ALL:
                                            if (JOptionPane.showConfirmDialog(theForm,
                                                    "Are you sure you want to modify this and all following found objects?",
                                                    "Please confirm", JOptionPane.YES_NO_OPTION,
                                                    JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {

                                                yesToAll = true;
                                                upd.updateObj(us, obj, kv, appNew);
                                                break;
                                            }
                                            break;

                                        case JOptionPane.YES_OPTION:
                                            appSaved.setCommandLine(((CfgApplication) obj).getCommandLine());
                                            appSaved.setCommandLineArguments(
                                                    ((CfgApplication) obj).getCommandLineArguments());
                                            usRestore.replaceKVP(kv);
                                            Message updateObj = upd.updateObj(us, obj, kv, appNew);
                                            if (updateObj != null && updateObj.messageId() == EventObjectUpdated.ID) {
                                                logger.debug("object updated");

                                                try {
                                                    final String remoteCmd = remoteRestartScript + " "
                                                            + ((CfgApplication) obj).getName();
                                                    requestOutput("Executing: " + remoteRestartScript);
                                                    final Pair<ArrayList<String>, ArrayList<String>> executeCommand = Utils.UnixProcess.ExtProcess
                                                            .executeCommand(remoteCmd, true, true);
                                                    if (executeCommand != null) {
                                                        ArrayList<String> lines;
                                                        if ((lines = executeCommand.getKey()) != null) {
                                                            requestOutput("Stdout: " + StringUtils.join(lines));
                                                        }
                                                        if ((lines = executeCommand.getValue()) != null) {
                                                            requestOutput("Stderr: " + StringUtils.join(lines));
                                                        }
                                                    }
                                                } catch (final IOException | InterruptedException ex) {
                                                    java.util.logging.Logger.getLogger(AppForm.class.getName())
                                                            .log(Level.SEVERE, null, ex);
                                                }
                                                updateObj = upd.updateObj(usRestore, obj, kv, appSaved);
                                                if (updateObj != null
                                                        && updateObj.messageId() == EventObjectUpdated.ID) {
                                                    logger.debug("object restored");
                                                } else {
                                                    showError("Failed to restore object: "
                                                            + ((updateObj != null) ? updateObj : null));
                                                    return false;
                                                }
                                            } else {
                                                showError("Failed to update object: "
                                                        + ((updateObj != null) ? updateObj : null));
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
                        } catch (final ProtocolException | HeadlessException protocolException) {
                            showError("Exception while updating: " + protocolException.getMessage());
                            return false;
                        }

                        return true;
                    }

                };

                try {
                    final CfgApplicationQuery query = new CfgApplicationQuery();

                    final CfgAppType selectedAppType = panelRestartServices.getSelectedAppType();
                    if (selectedAppType != null) {
                        query.setAppType(selectedAppType);
                    }
                    // String n = panelRestartServices.getObjName();
                    // if (panelRestartServices.isCaseSensitive() && n != null) {
                    // query.setName(n);
                    // }

                    if (configServerManager.findObjects(query, CfgApplication.class, new IKeyValueProperties() {
                        @Override
                        public KeyValueCollection getProperties(final CfgObject obj) {
                            return ((CfgApplication) obj).getUserProperties();
                        }

                        @Override
                        public Collection<String> getName(final CfgObject obj) {
                            final Collection<String> ret = new ArrayList<>();
                            ret.add(((CfgApplication) obj).getName());
                            return ret;
                        }
                    }, new FindWorker(seearchSettings), true, foundProc)) {

                    }

                } catch (final ConfigException | InterruptedException ex) {
                    java.util.logging.Logger.getLogger(AppForm.class.getName()).log(Level.SEVERE, null, ex);
                }
                if (upd != null && upd.isObjectsUpdated()) {
                    configServerManager.clearCache();
                }
            }
        }
    }// GEN-LAST:event_miRestartServiceActionPerformed

    private void miCreateDNPlacesActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_miCreateDNPlacesActionPerformed
        pastePlaceDNs();
    }// GEN-LAST:event_miCreateDNPlacesActionPerformed

    private void miImportCSVActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_miImportCSVActionPerformed
        importCSV();
    }// GEN-LAST:event_miImportCSVActionPerformed

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
    private javax.swing.JMenu jMenu5;
    private javax.swing.JMenu jMenu6;
    private javax.swing.JMenu jMenu7;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JMenu jmExit;
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
    private javax.swing.JMenuItem miCheckDNPlaceExists;
    private javax.swing.JMenuItem miExtensionWithoutPlace;
    private javax.swing.JMenuItem miFindLDAPs;
    private javax.swing.JMenuItem miFindLDAPsForUsers;
    private javax.swing.JMenuItem miImportCSV;
    private javax.swing.JMenuItem miLoadStrategy;
    private javax.swing.JMenuItem miLoginsWithoutAgent;
    private javax.swing.JMenuItem miObjByDBID;
    private javax.swing.JMenuItem miObjectByAnnex;
    private javax.swing.JMenuItem miOneORS;
    private javax.swing.JMenuItem miRestartService;
    private javax.swing.JPasswordField pfPassword;
    private javax.swing.JScrollPane spOutputScroll;
    private javax.swing.JTextArea taOutput;
    // End of variables declaration//GEN-END:variables

    public void setProfile(final String sGUIProfile) {
        this.profile = sGUIProfile;
    }

    private boolean connectToConfigServer() {

        if (configServerManager.isConnected()) {
            return true;
        } else {
            runInThread(() -> {
                IConfService ret = null;
                final StoredSettings.ConfServer confServ = (StoredSettings.ConfServer) cbConfigServer.getSelectedItem();
                final String user = (String) cbUser.getSelectedItem();
                if (confServ != null && user != null) {
                    ret = configServerManager.connect(confServ, user, new String(pfPassword.getPassword()));

                }
                connectionStatusChanged();

            });
            return configServerManager.isConnected();
        }
    }

    private String getPassword() {
        return textEncryptor.decrypt(ds.getPassword());
    }

    public void requestOutput(final String toString, final boolean printBlock) {
        boolean shouldChangeCaret = lastLineVisible();
        logger.info(toString);
        if (printBlock) {
            taOutput.append("------------------------------------\n");
        }
        taOutput.append(toString);
        taOutput.append("\n");
        if (shouldChangeCaret) {
            taOutput.setCaretPosition(taOutput.getDocument().getLength());
        }

        // logger.debug(toString);
    }

    public void requestOutput(final String toString) {
        requestOutput(toString, false);

    }

    public void showError(final String msg) {
        logger.error(msg);
        requestOutput("!!! Error: " + msg, true);
    }

    public void showException(final String cannot_connect_to_ConfigServer, final Exception ex) {
        logger.error(cannot_connect_to_ConfigServer, ex);
        final StringBuilder buf = new StringBuilder();
        buf.append("!!!Exception!!! = ").append(cannot_connect_to_ConfigServer).append("\n");
        for (final StackTraceElement stackTraceElement : ex.getStackTrace()) {
            buf.append("\t").append(stackTraceElement.toString()).append("\n");
        }
        requestOutput(buf.toString(), false);
    }

    private void connectionStatusChanged() {
        final boolean isConnected = configServerManager.isConnected();
        btDisconnect.setEnabled(isConnected);
        btConnect.setEnabled(!isConnected);
        cbConfigServer.setEnabled(!isConnected);
        cbUser.setEnabled(!isConnected);
        pfPassword.setEnabled(!isConnected);
    }

    private void runAppByIPActionPerformed(final ActionEvent evt) {
        runInThread(new IThreadedFun() {
            @Override
            public void fun() {
                final StringBuilder buf = new StringBuilder();

                final AppByIP pn1 = (AppByIP) appByIP.getContentPanel();
                final String ip1 = pn1.getText();
                requestOutput("Request: " + pn1.getSearchSummary());

                try {
                    try {
                        // enableComponents(this, false);
                        final ArrayList<Record> hostNames = new ArrayList<>();

                        for (final int t1 : new int[]{org.xbill.DNS.Type.PTR, org.xbill.DNS.Type.A}) {
                            final Lookup l = new Lookup(ReverseMap.fromAddress(ip1), t1);
                            final Record[] hosts = l.run();
                            if (ArrayUtils.isNotEmpty(hosts)) {
                                hostNames.addAll(Arrays.asList(hosts));
                            }

                        }
                        if (hostNames.isEmpty()) {
                            buf.append("IP [").append(ip1).append("] not resolved\n");
                        } else {
                            if ((connectToConfigServer())) {
                                final CfgHostQuery hq = new CfgHostQuery(configServerManager.getService());
                                final CfgApplicationQuery aq = new CfgApplicationQuery(
                                        configServerManager.getService());
                                buf.append("resolved IP[").append(ip1).append("] to ");
                                for (final Record hostName : hostNames) {
                                    final PTRRecord r = (PTRRecord) hostName;
                                    buf.append(r.getTarget().toString(true)).append("\n");
                                    // String mask =
                                    // StringUtils.strip(StringUtils.trim(r.getTarget().getLabelString(0))) + "*";
                                    final String mask = r.getTarget().getLabelString(0) + "*";
                                    hq.setName(mask);
                                    final Collection<CfgHost> hostsFound = configServerManager.getResults(hq,
                                            CfgHost.class);
                                    if (hostsFound != null) {
                                        for (final CfgHost cfgHost : hostsFound) {
                                            buf.append("Found host: ").append(cfgHost.getName()).append(" DBID:")
                                                    .append(cfgHost.getDBID()).append(" type: ")
                                                    .append(cfgHost.getType()).append(" os: ")
                                                    .append(cfgHost.getOSinfo().getOStype()).append("\n");
                                            aq.setHostDbid(cfgHost.getDBID());
                                            final Collection<CfgApplication> appsFound = aq.execute();
                                            buf.append("\tapplications on the host:\n");
                                            if (appsFound == null) {
                                                buf.append("**** no apps found!!! ");
                                            } else {
                                                for (final CfgApplication cfgApplication : appsFound) {
                                                    buf.append("\t\t\"").append(cfgApplication.getName()).append("\"")
                                                            .append(" (type:").append(cfgApplication.getType())
                                                            .append(", DBID:").append(cfgApplication.getDBID())
                                                            .append(")\n");
                                                }
                                                if (pn1.isFullOutput()) {
                                                    for (final CfgApplication cfgApplication : appsFound) {
                                                        buf.append("\t\t\"").append(cfgApplication.toString())
                                                                .append("\n<<<<<\n");
                                                    }
                                                }
                                            }
                                            // try {
                                            // l = new Lookup(cfgHost.getName());
                                            // Record[] run = l.run();
                                            // if (run == null) {
                                            // logger.info("Not resolved name [" + cfgHost.getName() + "]");
                                            // } else {
                                            // buf.append("resolved [" + cfgHost.getName() + "]: ");
                                            // for (Record record : run) {
                                            // buf.append(" ").append(record.getName().toString(false));
                                            //
                                            // }
                                            // buf.append("\n");
                                            // }
                                            // } catch (TextParseException ex) {
                                            // java.util.logging.Logger.getLogger(AppForm.class.getName()).log(Level.SEVERE,
                                            // null, ex);
                                            // }
                                            // InetAddress[] allByName = Address.getAllByName(cfgHost.getName());
                                            // cfgHost.getName();
                                        }
                                    } else {
                                        buf.append("host ").append(r.getTarget().toString(true))
                                                .append(" search mask:[").append(mask).append("] not found in CME!\n");
                                    }

                                    // r.getTarget().getLabelString(0);
                                }

                                // hq.setName("esv1*");
                                // Collection<CfgHost> execute = hq.execute();
                                // for (CfgHost cfgHost : execute) {
                                // try {
                                // l = new Lookup(cfgHost.getName());
                                // Record[] run = l.run();
                                // if (run == null) {
                                // logger.info("Not resolved name [" + cfgHost.getName() + "]");
                                // } else {
                                // buf.append("resolved [" + cfgHost.getName() + "]: ");
                                // for (Record record : run) {
                                // buf.append(" ").append(record.getName().toString(false));
                                //
                                // }
                                // buf.append("\n");
                                // }
                                // } catch (TextParseException ex) {
                                // java.util.logging.Logger.getLogger(AppForm.class.getName()).log(Level.SEVERE,
                                // null, ex);
                                // }
                                //// InetAddress[] allByName = Address.getAllByName(cfgHost.getName());
                                //// cfgHost.getName();
                                // }
                                // logger.info(execute);
                                // CfgApplicationQuery q = new CfgApplicationQuery(service);
                                //// ICfgObject retrieveObject = service.retrieveObject(t, dbid);
                                //// requestOutput(retrieveObject.toString());
                                // } catch (ConfigException ex) {
                                // java.util.logging.Logger.getLogger(AppForm.class.getName()).log(Level.SEVERE,
                                // null, ex);
                                // }
                            }
                        }
                    } catch (final UnknownHostException ex) {
                        buf.append(ex.getMessage()).append("\n");
                        java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE,
                                null, ex);

                    } catch (final ConfigException | InterruptedException ex) {
                        java.util.logging.Logger.getLogger(AppForm.class.getName()).log(Level.SEVERE, null, ex);

                    }
                    requestOutput(buf.toString());
                } finally {
                    // enableComponents(this, true);
                }
            }
        });

    }

    private void appByOptionThread(final AppByOptions par) {
        runInThread(new IThreadedFun() {
            @Override
            public void fun() {
                if (connectToConfigServer()) {

                    final AppByOptions pn = (AppByOptions) appByOption.getContentPanel();
                    requestOutput("Request: " + pn.getSearchSummary());

                    try {

                        final CfgAppType t = pn.getSelectedAppType();
                        final CfgApplicationQuery q = new CfgApplicationQuery();
                        if (t != null) {
                            q.setAppType(t);

                        }
                        configServerManager.findObjects(q, CfgApplication.class, new IKeyValueProperties() {
                            @Override
                            public KeyValueCollection getProperties(final CfgObject obj) {
                                return ((CfgApplication) obj).getOptions();
                            }

                            @Override
                            public Collection<String> getName(final CfgObject obj) {
                                final Collection<String> ret = new ArrayList<>();
                                ret.add(((CfgApplication) obj).getName());
                                return ret;

                            }
                        }, new FindWorker(pn), true, null);

                    } catch (final ConfigException ex) {
                        showException("Error", ex);

                    } catch (final InterruptedException ex) {
                        java.util.logging.Logger.getLogger(AppForm.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });

    }

    SwingWorker worker = null;

    private void workStarted(final boolean isStarted) {
        btCancel.setVisible(isStarted);
        btClearOutput.setVisible(!isStarted);
        btDisconnect.setVisible(!isStarted);
        btConnect.setVisible(!isStarted);
        btEditConfgServ.setVisible(!isStarted);

    }

    private void runInThread(final IThreadedFun fun) {
        runInThread(fun, null);
    }

    private void runInThread(final IThreadedFun fun, final IThreadedFun endFun) {
        final SwingWorker theWorker = new SwingWorker() {
            private Exception ex = null;

            @Override
            protected Object doInBackground() throws Exception {
                worker = this;
                workStarted(true);
                try {
                    fun.fun();
                } catch (Exception e) {
                    requestOutput("----Exception while in thread: " + e.getMessage() + "\n\t"
                            + StringUtils.join(e.getStackTrace(), "\n\t") + "\n-------\n");
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
                    }
                } else {
                    if (endFun != null) {
                        try {
                            endFun.fun();
                        } catch (Exception ex) {
                            requestOutput("Exception running endfun: " + ex.getMessage());
                        }
                    }
                    requestOutput("All done", false);
                }
                workStarted(false);
                worker = null;
            }

        };
        theWorker.execute();
    }

    private void runObjByAnnexThread(final ActionEvent evt) {
        runInThread(new IThreadedFun() {
            @Override
            public void fun() throws ConfigException, InterruptedException {
                if (connectToConfigServer()) {

                    final ObjByAnnex pn = (ObjByAnnex) objByAnnex.getContentPanel();
                    requestOutput("Request: " + pn.getSearchSummary());

                    for (final CfgObjectType value : pn.getSelectedObjectTypes()) {
                        configServerManager.doTheSearch(value, pn, false, true, null);
                    }

                }
            }
        });

    }

    private void runAnnexReplaceThread(final ActionEvent evt) {
        runInThread(new IThreadedFun() {
            @Override
            public void fun() throws ConfigException, InterruptedException {
                if (connectToConfigServer()) {
                    // updateObj();
                    final ObjByAnnex pn = (ObjByAnnex) objByAnnex.getContentPanel();
                    requestOutput("Request: " + pn.getSearchSummary());

                    for (final CfgObjectType value : pn.getSelectedObjectTypes()) {
                        configServerManager.doTheSearch(value, pn, false, true, null);
                    }

                }
            }
        });

    }

    HashSet<String> searchValues = new HashSet<>();

    public static final HashMap<String, Integer> cacheOptions = createCacheOptions();

    private static HashMap<String, Integer> createCacheOptions() {
        final HashMap<String, Integer> ret = new HashMap<>();
        ret.put("assembled-cache-reload-threshold", 0);
        ret.put("max-age", 0);
        ret.put("max-assembled-cache-age", 0);

        return ret;
    }

    public FindObject getObjName(final String objClass) {
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
    private void strategyBuffering(final boolean turnOn, final ActionEvent evt) {
        upd = null;
        yesToAll = false;

        final FindObject objName = getObjName(
                CfgScript.class.getSimpleName() + " type " + CfgScriptType.CFGEnhancedRouting);

        if (objName == null) {
            return;
        }

        if (connectToConfigServer()) {
            ISearchSettings searchSettings;
            IUpdateSettings us;
            if (turnOn) {
                // <editor-fold defaultstate="collapsed" desc="turnOn">
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
                    public IUpdateSettings.KVPUpdateAction getKVPUpdateAction() {
                        return IUpdateSettings.KVPUpdateAction.ADD_SECTION;
                    }

                    @Override
                    public String KVPreplaceWith(final String currentValue) {

                        return null;

                    }

                    @Override
                    public String getReplaceKey(final String currentValue) {
                        return null;
                    }

                    @Override
                    public Collection<UserProperties> getAddedKVP() {
                        final Collection<UserProperties> ret = new ArrayList<>();
                        for (final Map.Entry<String, Integer> entry : cacheOptions.entrySet()) {
                            ret.add(new UserProperties(appSection, entry.getKey(), entry.getValue().toString()));

                        }
                        return ret;
                    }

                    @Override
                    public IUpdateSettings.ObjectUpdateAction getObjectUpdateAction() {
                        return ObjectUpdateAction.KVP_CHANGE;
                    }

                    @Override
                    public boolean isDeleteDependendObjects() {
                        throw new UnsupportedOperationException("Not supported yet."); // To change body of generated
                        // methods, choose Tools |
                        // Templates.
                    }
                };
                // </editor-fold>

            } else {
                // <editor-fold defaultstate="collapsed" desc="turnOff">
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
                    public IUpdateSettings.KVPUpdateAction getKVPUpdateAction() {
                        return IUpdateSettings.KVPUpdateAction.REMOVE;
                    }

                    @Override
                    public String KVPreplaceWith(final String currentValue) {
                        return null;
                    }

                    @Override
                    public String getReplaceKey(final String currentValue) {
                        return null;
                    }

                    @Override
                    public Collection<UserProperties> getAddedKVP() {
                        final Collection<UserProperties> ret = new ArrayList<>();
                        for (final Map.Entry<String, Integer> entry : cacheOptions.entrySet()) {
                            ret.add(new UserProperties(appSection, entry.getKey(), entry.getValue().toString()));

                        }
                        return ret;
                    }

                    @Override
                    public IUpdateSettings.ObjectUpdateAction getObjectUpdateAction() {
                        return ObjectUpdateAction.KVP_CHANGE;
                    }

                    @Override
                    public boolean isDeleteDependendObjects() {
                        throw new UnsupportedOperationException("Not supported yet."); // To change body of generated
                        // methods, choose Tools |
                        // Templates.
                    }
                };
                // </editor-fold>

            }

            final ICfgObjectFoundProc foundProc = new ICfgObjectFoundProc() {
                @Override
                public boolean proc(final CfgObject obj, final KeyValueCollection kv, final int current,
                        final int total) {
                    // kv = getAllValuesInSection(obj, seearchSettings);
                    // kv = new KeyValueCollection();
                    // kv.addList(seearchSettings.getSection(), ((CfgScript)
                    // obj).getUserProperties().getList(seearchSettings.getSection()));
                    // ((CfgTransaction)
                    // obj).getUserProperties().getList(seearchSettings.getSection());
                    logger.info("found " + obj.toString() + "\n kv: " + kv.toString());

                    // int showYesNoPanel = showYesNoPanel(pn.getSearchSummary(), obj.toString() +
                    // "\n kv: " + kv.toString());
                    try {
                        if (yesToAll) {
                            upd.updateObj(us, obj, kv);
                        } else {
                            upd = new UpdateCFGObjectProcessor(configServerManager, obj.getObjectType(), theForm);
                            final String estimateUpdateObj = upd.estimateUpdateObj(us, obj, kv);
                            if (estimateUpdateObj != null) //
                            {
                                switch (showYesNoPanel(searchSettings.toString(),
                                        "Object " + current + " of matched " + total
                                        + "\ntoUpdate: \n----------------------\n" + estimateUpdateObj
                                        + "\n-->\n" + obj.toString() + "\n\t kv: " + kv.toString())) {
                                    case YES_TO_ALL:
                                        if (JOptionPane.showConfirmDialog(theForm,
                                                "Are you sure you want to modify this and all following found objects?",
                                                "Please confirm", JOptionPane.YES_NO_OPTION,
                                                JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {

                                            yesToAll = true;
                                            upd.updateObj(us, obj, kv);
                                            break;
                                        }
                                        break;

                                    case JOptionPane.YES_OPTION:
                                        upd.updateObj(us, obj, kv);
                                        break;

                                    case JOptionPane.NO_OPTION:
                                        break;

                                    case JOptionPane.CANCEL_OPTION:
                                        return false;
                                }
                            }
                        }
                    } catch (final ProtocolException | HeadlessException protocolException) {
                        showError("Exception while updating: " + protocolException.getMessage());
                    }

                    return true;
                }

            };

            try {

                final CfgScriptQuery query = new CfgScriptQuery();
                query.setScriptType(CfgScriptType.CFGEnhancedRouting);

                if (configServerManager.findObjects(query, CfgScript.class, new IKeyValueProperties() {
                    @Override
                    public KeyValueCollection getProperties(final CfgObject obj) {
                        return ((CfgScript) obj).getUserProperties();
                    }

                    @Override
                    public Collection<String> getName(final CfgObject obj) {
                        final Collection<String> ret = new ArrayList<>();
                        ret.add(((CfgScript) obj).getName());
                        return ret;
                    }
                }, new FindWorker(searchSettings), true, foundProc)) {

                }

            } catch (final ConfigException | InterruptedException ex) {
                java.util.logging.Logger.getLogger(AppForm.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (upd != null && upd.isObjectsUpdated()) {
                configServerManager.clearCache();
            }
        }
        logger.info("Done buffering update");
    }

    private void setQueryNameFilter(final CfgQuery query, final String name, final boolean regex) {
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

    private String getNamePattern(final String name, final boolean regex) {
        if (regex) {
            return name;
        } else {
            return "*" + name + "*";
        }
    }

    private void pastePlaceDNs() {
        // Clipboard clipboard = getSystemClipboard();
        // String data = null;
        // boolean errorReading = false;
        //
        // try {
        // data = (String) clipboard.getData(DataFlavor.stringFlavor);
        // } catch (UnsupportedFlavorException ex) {
        // logger.error(ex);
        // errorReading = true;
        // }
        // if (errorReading) {
        // JOptionPane.showMessageDialog(this, "Error reading clipboard", "Error",
        // JOptionPane.ERROR_MESSAGE);
        // return;
        // }
        // if (data == null) {
        // JOptionPane.showMessageDialog(this, "Nothing in clipboard", "Error",
        // JOptionPane.ERROR_MESSAGE);
        // return;
        // }
        // for (String wrd : StringUtils.split(data)) {
        // Matcher m;
        // String app = null;
        // String file = null;
        // if ((m = ptFullFileName.matcher(wrd)).find()) {
        // app = m.group(1);
        // file = m.group(2);
        // }
        // if (file != null) {
        // Pair<AppProfile, App> findAppProfile = ds.findAppProfile(app, file, wrd);
        // if (findAppProfile != null) {
        // ftd.addDownloadFile(findAppProfile, file);
        // }
        // }
        // }
        // if (!ftd.isEmpty()) {
        // if (lsGeneralTab == null) {
        // lsGeneralTab = new JTablePasteFileList();
        // }
        // if (lsPasteOutput == null) {
        // lsPasteOutput = new SettingsPanel.InfoPanel(parent, "List of pasted",
        // lsGeneralTab,
        // "Download %d files");
        // }
        // ArrayList<JTableFileEntryGeneral> lsPasteFiles = new ArrayList<>();
        // for (FilesToGet filesToGet : ftd) {
        // for (String fileName : filesToGet.getFileNames()) {
        // lsPasteFiles.add(new JTableFileEntryGeneral(filesToGet, fileName));
        // }
        // }
        // lsGeneralTab.setFiles(lsPasteFiles);
        // lsPasteOutput.doShow();
        // if (lsPasteOutput.getCloseCause() == JOptionPane.OK_OPTION) {
        // if (lsGeneralTab.getSelectedFiles().size() > 0) {
        //
        // HashMap<FilesToGet, ArrayList<String>> r = new HashMap<>();
        //
        // for (JTableFileEntryGeneral row : lsGeneralTab.getSelectedFiles()) {
        // FilesToGet fToGet = row.getFilesToGet();
        // ArrayList<String> rSyncFiles = r.get(fToGet);
        // if (rSyncFiles == null) {
        // rSyncFiles = new ArrayList<>();
        // r.put(fToGet, rSyncFiles);
        // }
        //
        // rSyncFiles.add(row.getFileName() + "*");
        // }
        // SettingsDialog.info("About to download " +
        // lsGeneralTab.getSelectedFiles().size() + " files (" + r.size() + "
        // threads)");
        //
        // lsPastedFiles(r);
        //
        // }
        // }
        //
        // }
        // logger.debug(ftd);
    }

    private void verifyLDAP() {

        final JFileChooser chooser = new JFileChooser();
        final FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV files", "csv");
        chooser.setFileFilter(filter);
        chooser.setDialogType(JFileChooser.OPEN_DIALOG);
        chooser.setMultiSelectionEnabled(false);
        final int returnVal = chooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            requestOutput("You chose to open this file: " + chooser.getSelectedFile().getName());
            final ArrayList<String> ldapIDs = loadSingleColumn(chooser.getSelectedFile());

            if (shouldCheckLDAPCSV(ldapIDs)) {
                runInThread(new IThreadedFun() {
                    @Override
                    public void fun() throws ConfigException, InterruptedException {
                        if (connectToConfigServer()) {

                            requestOutput("Searching for LDAPs");
                            Collection<CfgPerson> allPersons = configServerManager.getAllPersons();
                            for (String ldapID : ldapIDs) {
                                boolean ldapFound = false;
                                for (CfgPerson cfgPerson : allPersons) {
                                    if (StringUtils.compareIgnoreCase(ldapID, cfgPerson.getExternalID()) == 0) {
                                        requestOutput(ldapID + " used by " + cfgPerson.getUserName() + "(path: " + cfgPerson.getObjectPath() + ")");
                                        ldapFound = true;
                                    }
                                }
                                if (!ldapFound) {

                                    requestOutput(" * LDAP [" + ldapID + "] not found ");
                                }

                            }

                        }
                    }

                }, new IThreadedFun() {
                    @Override
                    public void fun() throws ConfigException, InterruptedException {
                        configServerManager.clearCache();
                    }
                });
            }

        }
    }

    private ArrayList<String> loadSingleColumn(File f) {
        final ArrayList<String> userNames = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(f))) {
            String l;
            while ((l = reader.readLine()) != null) {
                final String[] split = StringUtils.split(l, ",;");
                if (ArrayUtils.isNotEmpty(split) && split.length >= 1) {
                    String trimToNull = StringUtils.trimToNull(split[0]);
                    if (trimToNull != null) {
                        userNames.add(trimToNull);
                    }
                } else {
                    requestOutput("Not parsed expression [" + StringUtils.defaultString(l, "<null>") + "]");
                }
            }
        } catch (final FileNotFoundException ex) {
            java.util.logging.Logger.getLogger(AppForm.class.getName()).log(Level.SEVERE, null, ex);
        } catch (final IOException ex) {
            java.util.logging.Logger.getLogger(AppForm.class.getName()).log(Level.SEVERE, null, ex);
        }
        return userNames;
    }

    private void verifyUserNameLDAP() {

        final JFileChooser chooser = new JFileChooser();
        final FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV files", "csv");
        chooser.setFileFilter(filter);
        chooser.setDialogType(JFileChooser.OPEN_DIALOG);
        chooser.setMultiSelectionEnabled(false);
        final int returnVal = chooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            requestOutput("You chose to open this file: " + chooser.getSelectedFile().getName());
            final ArrayList<String> userNames = loadSingleColumn(chooser.getSelectedFile());

            if (shouldCheckLDAPCSV(userNames)) {
                runInThread(new IThreadedFun() {
                    @Override
                    public void fun() throws ConfigException, InterruptedException {
                        if (connectToConfigServer()) {

                            requestOutput("Searching for userNames");
                            Collection<CfgPerson> allPersons = configServerManager.getAllPersons();
                            for (String user : userNames) {
                                boolean userNameFound = false;
                                for (CfgPerson cfgPerson : allPersons) {
                                    if (StringUtils.compareIgnoreCase(user, cfgPerson.getUserName()) == 0) {
                                        requestOutput(user + " LDAP " + cfgPerson.getExternalID() + "(path: " + cfgPerson.getObjectPath() + ")");
                                        userNameFound = true;
                                    }
                                }
                                if (!userNameFound) {

                                    requestOutput(" * USER [" + user + "] not found ");
                                }

                            }

                        }
                    }

                }, new IThreadedFun() {
                    @Override
                    public void fun() throws ConfigException, InterruptedException {
                        configServerManager.clearCache();
                    }
                });
            }

        }
    }

    private ArrayList<Pair<String, String>> readPlaceDN(File f) {
        final ArrayList<Pair<String, String>> placeDN = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(f))) {
            String l;
            while ((l = reader.readLine()) != null) {
                final String[] split = StringUtils.split(l, ",");
                if (ArrayUtils.isNotEmpty(split) && split.length >= 2) {
                    String trimToNull0 = StringUtils.trimToNull(split[0]);
                    String trimToNull1 = StringUtils.trimToNull(split[1]);
                    if (trimToNull0 != null && trimToNull1 != null) {
                        placeDN.add(new Pair(trimToNull0, trimToNull1));
                    }
                } else {
                    requestOutput("Not parsed expression [" + StringUtils.defaultString(l, "<null>") + "]");
                }
            }
        } catch (final FileNotFoundException ex) {
            java.util.logging.Logger.getLogger(AppForm.class.getName()).log(Level.SEVERE, null, ex);
        } catch (final IOException ex) {
            java.util.logging.Logger.getLogger(AppForm.class.getName()).log(Level.SEVERE, null, ex);
        }
        return placeDN;
    }

    private void verifyCSV() {

        final JFileChooser chooser = new JFileChooser();
        final FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV files", "csv");
        chooser.setFileFilter(filter);
        chooser.setDialogType(JFileChooser.OPEN_DIALOG);
        chooser.setMultiSelectionEnabled(false);
        final int returnVal = chooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            requestOutput("You chose to open this file: " + chooser.getSelectedFile().getName());
            final ArrayList<Pair<String, String>> placeDN = readPlaceDN(chooser.getSelectedFile());

            if (shouldImportCSV(placeDN, false)) {
                runInThread(new IThreadedFun() {
                    @Override
                    public void fun() throws ConfigException, InterruptedException {
                        if (connectToConfigServer()) {
                            final ArrayList<SwitchLookup> switches = new ArrayList<>();
                            switches.add(new SwitchLookup(configServerManager.getService(), "esv1_sipa1"));
                            switches.add(new SwitchLookup(configServerManager.getService(), "edn1_sipa1"));
                            switches.add(new SwitchLookup(configServerManager.getService(), "esg3_sipa1"));

                            for (Pair<String, String> entry : placeDN) {
                                String thePlace = entry.getKey();
                                String theDN = entry.getValue();
                                final HashMap<SwitchLookup, String> DNs = new HashMap<>();
                                for (final SwitchLookup switche : switches) {
                                    DNs.put(switche, (String) null);

                                }
                                for (final SwitchLookup switchLookup : DNs.keySet()) {
                                    DNs.put(switchLookup, theDN);
                                }
                                configServerManager.checkPlace(thePlace, DNs);
                            }
                        }
                    }

                }, new IThreadedFun() {
                    @Override
                    public void fun() throws ConfigException, InterruptedException {
                        configServerManager.clearCache();
                    }
                });
            }

        }
    }

    private void importCSV() {

        final JFileChooser chooser = new JFileChooser();
        final FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV files", "csv");
        chooser.setFileFilter(filter);
        chooser.setDialogType(JFileChooser.OPEN_DIALOG);
        chooser.setMultiSelectionEnabled(false);
        final int returnVal = chooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            requestOutput("You chose to open this file: " + chooser.getSelectedFile().getAbsolutePath());
            final ArrayList<Pair<String, String>> placeDN = readPlaceDN(chooser.getSelectedFile());

            if (shouldImportCSV(placeDN, true)) {
                runInThread(new IThreadedFun() {
                    @Override
                    public void fun() throws ConfigException, InterruptedException {
                        if (connectToConfigServer()) {
                            final ArrayList<SwitchLookup> switches = new ArrayList<>();
                            switches.add(new SwitchLookup(configServerManager.getService(), "esv1_sipa1"));
                            switches.add(new SwitchLookup(configServerManager.getService(), "edn1_sipa1"));
                            switches.add(new SwitchLookup(configServerManager.getService(), "esg3_sipa1"));

                            ExistingObjectDecider eod = ExistingObjectDecider.getInstance();

                            eod.init(ObjectExistAction.UNKNOWN, theForm);
                            for (Pair<String, String> entry : placeDN) {
                                String thePlace = entry.getKey();
                                String theDN = entry.getValue();
                                final HashMap<SwitchLookup, String> DNs = new HashMap<>();
                                for (final SwitchLookup switche : switches) {
                                    DNs.put(switche, (String) null);

                                }
                                for (final SwitchLookup switchLookup : DNs.keySet()) {
                                    DNs.put(switchLookup, theDN);
                                }
                                if (configServerManager.createPlace(thePlace, DNs, eod) == null) // stop creating
                                {
                                    requestOutput("****** Import aborted *******");
                                    break;
                                }
                            }
                        }
                    }

                }, new IThreadedFun() {
                    @Override
                    public void fun() throws ConfigException, InterruptedException {
                        configServerManager.clearCache();
                    }
                });
            }

        }
    }

    private void folderSearch() {

        runInThread(new IThreadedFun() {
            @Override
            public void fun() throws ConfigException, InterruptedException {
                if (connectToConfigServer()) {
//                    try {

                    ArrayList<Pair<String, String>> placeDN = new ArrayList<>();
                    placeDN.add(new Pair<>("Place1", "111"));
                    placeDN.add(new Pair<>("Place2", "222"));
                    shouldImportCSV(placeDN, false);

//                        CfgFolderQuery q = new CfgFolderQuery();
//                        q.setType(CfgObjectType.CFGDN.ordinal());
//                        ArrayList<CfgFolder> results = new ArrayList(configServerManager.getResults(q, CfgFolder.class));
//                        Collections.sort(results, (o1, o2) -> {
//                            return ((CfgFolder) o1).getName().compareToIgnoreCase(((CfgFolder) o2).getName());
//                        });
//                        StringBuilder b = new StringBuilder();
//                        for (CfgFolder result : results) {
//                            b.append("[" + result.getName() + "] at " + result.getObjectPath() + "\n");
//                        }
//                        requestOutput("Found total folders: " + b);
//                    } catch (ConfigException ex) {
//                        java.util.logging.Logger.getLogger(AppForm.class.getName()).log(Level.SEVERE, null, ex);
//                    } catch (InterruptedException ex) {
//                        java.util.logging.Logger.getLogger(AppForm.class.getName()).log(Level.SEVERE, null, ex);
//                    }
                }
            }
        });

    }

    private class LDAP_Dialog extends Utils.InfoPanel {

        DefaultTableModel infoTableModel;
        TableColumnAdjuster tca;

        public LDAP_Dialog(Window parent, int buttonOptions) throws HeadlessException {
            super(parent, buttonOptions);

            infoTableModel = new DefaultTableModel() {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false; // To change body of generated methods, choose Tools | Templates.
                }

            };

            infoTableModel.addColumn("LDAP");

            JTable tab = new JTable(infoTableModel);
            tab.getTableHeader().setVisible(true);
            tab.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

            JScrollPane jp = new JScrollPane(tab);
            super.setMainPanel(jp);

            tca = new TableColumnAdjuster(tab);
            tca.setColumnDataIncluded(true);
            tca.setColumnHeaderIncluded(false);
            tca.setDynamicAdjustment(true);
        }

        public boolean shouldProceed(final ArrayList<String> ldapIDs) {
            infoTableModel.setRowCount(0);
            for (String entry : ldapIDs) {
                infoTableModel.addRow(new Object[]{entry});
            }
            tca.adjustColumns();
            this.setTitle("Checking agents with below LDAPs (" + ldapIDs.size() + ")");
            Utils.ScreenInfo.CenterWindow(this);
            this.showModal();

            return getDialogResult() == JOptionPane.OK_OPTION;
        }

    };

    LDAP_Dialog showLDAP = null;

    private boolean shouldCheckLDAPCSV(final ArrayList<String> ldapIDs) {

        if (showLDAP == null) {
            showLDAP = new LDAP_Dialog(this, JOptionPane.OK_CANCEL_OPTION);
        }

        return showLDAP.shouldProceed(ldapIDs);

    }

    private boolean shouldImportCSV(final ArrayList<Pair<String, String>> placeDN, boolean isImport) {

        DefaultTableModel infoTableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // To change body of generated methods, choose Tools | Templates.
            }

        };

        infoTableModel.addColumn("Place");
        infoTableModel.addColumn("DN");
        for (Pair<String, String> entry : placeDN) {
            String place = entry.getKey();
            String dn = entry.getValue();
            infoTableModel.addRow(new Object[]{place, dn});
        }

        JTable tab = new JTable(infoTableModel);
        tab.getTableHeader().setVisible(true);
        tab.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        TableColumnAdjuster tca = new TableColumnAdjuster(tab);
        tca.setColumnDataIncluded(true);
        tca.setColumnHeaderIncluded(false);
        tca.setDynamicAdjustment(true);
        tca.adjustColumns();

        JScrollPane jp = new JScrollPane(tab);
        // jp.add(tab);

        Dimension preferredSize = new Dimension(600, 200);
        jp.setPreferredSize(preferredSize);
        JPanel topPan = new JPanel();

        topPan.setLayout(new BoxLayout(topPan, BoxLayout.PAGE_AXIS));

        JPanel placeFolder = new JPanel();
        placeFolder.setLayout(new BoxLayout(placeFolder, BoxLayout.PAGE_AXIS));
        placeFolder.setBorder(new TitledBorder("Place folder"));

        JTable tabPlaceFolder = new JTable(infoTableModel);
        tabPlaceFolder.getTableHeader().setVisible(true);
        tabPlaceFolder.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        placeFolder.add(tabPlaceFolder);
        placeFolder.add(new JButton("Select"));

        JPanel dnFolders = new JPanel();
        dnFolders.setLayout(new BoxLayout(dnFolders, BoxLayout.PAGE_AXIS));
        dnFolders.setBorder(new TitledBorder("DN folders"));
        JTable tabDNFolders = new JTable(infoTableModel);
        tabDNFolders.getTableHeader().setVisible(true);
        tabDNFolders.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        dnFolders.add(tabDNFolders);
        dnFolders.add(new JButton("Select"));

        topPan.add(jp);
        topPan.add(placeFolder);
        topPan.add(dnFolders);
        topPan.invalidate();

        StringBuilder title = new StringBuilder();
        if (isImport) {
            title.append("Do you want to check existense Place/DN (total ").append(placeDN.size()).append(")");
        } else {
            title.append("Do you want to import following Place/DN (total ").append(placeDN.size()).append(")");
        }

        Utils.InfoPanel dlg = new Utils.InfoPanel(this, title.toString(), topPan, JOptionPane.OK_CANCEL_OPTION);
        Utils.ScreenInfo.CenterWindow(dlg);
        dlg.showModal();

        return dlg.getDialogResult() == JOptionPane.OK_OPTION;

    }

    private boolean lastLineVisible() {
        int y1 = taOutput.getVisibleRect().y;
        int y2 = y1 + taOutput.getVisibleRect().height;
        int lineHeight = taOutput.getFontMetrics(taOutput.getFont()).getHeight();
        int endRow = (int) Math.floor((double) y2 / lineHeight);
        return (endRow > 0) ? endRow == taOutput.getRows() : true;

    }

    private void getExtensionsWithoutPlace() {
        runInThread(new IThreadedFun() {
            @Override
            public void fun() throws ConfigException, InterruptedException {
                if (connectToConfigServer()) {
                    HashMap<Integer, CfgObject> extensions = configServerManager.getAllExtDBID_Extension();
                    HashMap<Integer, CfgObject> places = configServerManager.getAllExtDBID_Place();
                    ArrayList<CfgDN> orphanDNs = new ArrayList<>();
                    for (Map.Entry<Integer, CfgObject> entry : extensions.entrySet()) {
                        Integer extDBID = entry.getKey();
                        CfgDN dn = (CfgDN) entry.getValue();
                        if (!places.containsKey(extDBID)) {
                            orphanDNs.add(dn);
                        }
                    }
                    if (!orphanDNs.isEmpty()) {
                        Collections.sort(orphanDNs, (o1, o2) -> {
                            return ((CfgDN) o1).getNumber().compareTo(((CfgDN) o2).getNumber());
                        });
                        requestOutput("Found toral orphans: " + orphanDNs.size());
                        for (CfgDN dn : orphanDNs) {
                            requestOutput("Orphan extension at " + dn.getObjectPath() + " switch:" + dn.getSwitch().getName() + ": " + dn.getNumber() + "(DBID: " + dn.getDBID() + ")");

                        }
                    }
                }
            }
        });

    }

    private void getLoginsWithoutAgent() {
        runInThread(new IThreadedFun() {
            @Override
            public void fun() throws ConfigException, InterruptedException {
                if (connectToConfigServer()) {
                    HashMap<Integer, CfgObject> agentLogins = configServerManager.getAllDBID_AgentLogin();
                    HashMap<Integer, CfgObject> agentLoginIDtoAgent = configServerManager.getAllLoginID_Agents();
                    int totalCnt = 0;
                    for (Map.Entry<Integer, CfgObject> entry : agentLogins.entrySet()) {
                        Integer agentLoginDBID = entry.getKey();
                        CfgAgentLogin al = (CfgAgentLogin) entry.getValue();
                        if (!agentLoginIDtoAgent.containsKey(agentLoginDBID)) {
                            requestOutput("Orphan loginID at " + al.getObjectPath() + ": " + al.getLoginCode() + "(DBID: " + al.getDBID() + ")");
                            totalCnt++;
                        }
                    }
                    requestOutput("Found toral orphans: " + totalCnt);
                }
            }
        });

    }

    final class RequestDialog extends StandardDialog {

        private final JPanel contentPanel;

        private RequestDialog(final Window parent, final JPanel contentPanel, final JMenuItem mi) {
            this(parent, contentPanel);
            setTitle(mi.getText() + " parameters");

        }

        private RequestDialog(final Window parent, final JPanel contentPanel) {
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
            final JPanel content = new JPanel();
            content.setLayout(new BoxLayout(content, BoxLayout.PAGE_AXIS));
            content.add(contentPanel);

            return content;
        }

        @Override
        public ButtonPanel createButtonPanel() {
            final ButtonPanel buttonPanel = new ButtonPanel();
            final JButton cancelButton = new JButton();
            buttonPanel.addButton(cancelButton);

            cancelButton.setAction(new AbstractAction() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    setDialogResult(RESULT_CANCELLED);
                    setVisible(false);
                    dispose();
                }
            });
            cancelButton.setText("Close");

            final JButton jbOK = new JButton("OK");
            buttonPanel.addButton(jbOK);

            // listPane.add(jbFilter);
            jbOK.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    setDialogResult(RESULT_AFFIRMED);
                    dispose();
                }
            });

            final String act = "OK";

            setDefaultCancelAction(cancelButton.getAction());
            setDefaultAction(jbOK.getAction());
            getRootPane().setDefaultButton(jbOK);

            buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            buttonPanel.setSizeConstraint(ButtonPanel.NO_LESS_THAN); // since the checkbox is quite wide, we don't want
            // all of them have the same size.
            return buttonPanel;
        }

        public boolean doShow(final String title, final IConfigPanel onShow) {
            if (onShow != null) {
                onShow.showProc();
            }
            return doShow(title);
        }

        public boolean doShow(final String Title) {
            setTitle(Title);
            return doShow();
        }

        public boolean doShow() {

            // setModal(true);
            pack();
            if (contentPanel instanceof ISearchCommon) {
                ((ISearchCommon) contentPanel).setChoices(searchValues);
            }

            // ScreenInfo.CenterWindow(this);
            setLocationRelativeTo(getParent());
            // setVisible(true);
            setAlwaysOnTop(true);
            java.awt.EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    toFront();

                }
            });
            // setVisible(false);
            setVisible(true);

            if (getDialogResult() == StandardDialog.RESULT_AFFIRMED) {
                if (contentPanel instanceof ISearchCommon) {
                    final Collection<String> choices = ((ISearchCommon) contentPanel).getChoices();
                    if (choices != null && !choices.isEmpty()) {
                        for (final String choice : choices) {
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
