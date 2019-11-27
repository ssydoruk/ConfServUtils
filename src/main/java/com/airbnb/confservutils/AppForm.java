/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.airbnb.confservutils;

import Utils.ValuesEditor;
import com.genesyslab.platform.applicationblocks.com.CfgObject;
import com.genesyslab.platform.applicationblocks.com.CfgQuery;
import com.genesyslab.platform.applicationblocks.com.ConfigException;
import com.genesyslab.platform.applicationblocks.com.ICfgObject;
import com.genesyslab.platform.applicationblocks.com.IConfService;
import com.genesyslab.platform.applicationblocks.com.objects.CfgAgentGroup;
import com.genesyslab.platform.applicationblocks.com.objects.CfgAgentLogin;
import com.genesyslab.platform.applicationblocks.com.objects.CfgApplication;
import com.genesyslab.platform.applicationblocks.com.objects.CfgDN;
import com.genesyslab.platform.applicationblocks.com.objects.CfgDNGroup;
import com.genesyslab.platform.applicationblocks.com.objects.CfgEnumerator;
import com.genesyslab.platform.applicationblocks.com.objects.CfgEnumeratorValue;
import com.genesyslab.platform.applicationblocks.com.objects.CfgHost;
import com.genesyslab.platform.applicationblocks.com.objects.CfgPerson;
import com.genesyslab.platform.applicationblocks.com.objects.CfgPlace;
import com.genesyslab.platform.applicationblocks.com.objects.CfgPlaceGroup;
import com.genesyslab.platform.applicationblocks.com.objects.CfgScript;
import com.genesyslab.platform.applicationblocks.com.objects.CfgSwitch;
import com.genesyslab.platform.applicationblocks.com.objects.CfgTransaction;
import com.genesyslab.platform.applicationblocks.com.queries.CfgAgentGroupQuery;
import com.genesyslab.platform.applicationblocks.com.queries.CfgAgentLoginQuery;
import com.genesyslab.platform.applicationblocks.com.queries.CfgApplicationQuery;
import com.genesyslab.platform.applicationblocks.com.queries.CfgDNGroupQuery;
import com.genesyslab.platform.applicationblocks.com.queries.CfgDNQuery;
import com.genesyslab.platform.applicationblocks.com.queries.CfgEnumeratorQuery;
import com.genesyslab.platform.applicationblocks.com.queries.CfgEnumeratorValueQuery;
import com.genesyslab.platform.applicationblocks.com.queries.CfgHostQuery;
import com.genesyslab.platform.applicationblocks.com.queries.CfgPersonQuery;
import com.genesyslab.platform.applicationblocks.com.queries.CfgPlaceGroupQuery;
import com.genesyslab.platform.applicationblocks.com.queries.CfgPlaceQuery;
import com.genesyslab.platform.applicationblocks.com.queries.CfgScriptQuery;
import com.genesyslab.platform.applicationblocks.com.queries.CfgSwitchQuery;
import com.genesyslab.platform.applicationblocks.com.queries.CfgTransactionQuery;
import com.genesyslab.platform.commons.collections.KeyValueCollection;
import com.genesyslab.platform.commons.collections.KeyValuePair;
import com.genesyslab.platform.commons.protocol.ProtocolException;
import com.genesyslab.platform.configuration.protocol.types.CfgAppType;
import com.genesyslab.platform.configuration.protocol.types.CfgDNType;
import com.genesyslab.platform.configuration.protocol.types.CfgObjectType;
import com.genesyslab.platform.configuration.protocol.types.CfgTransactionType;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jidesoft.dialog.ButtonPanel;
import com.jidesoft.dialog.StandardDialog;
import static com.jidesoft.dialog.StandardDialog.RESULT_AFFIRMED;
import static com.jidesoft.dialog.StandardDialog.RESULT_CANCELLED;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
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
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
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

            InputStreamReader reader = new InputStreamReader(new FileInputStream(f));
            ds = gson.fromJson(reader, StoredSettings.class);
            reader.close();
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

    private void loadConfigServers() {
//<editor-fold defaultstate="collapsed" desc="load configservers">
        cbConfigServer.removeAllItems();
        DefaultComboBoxModel mod = (DefaultComboBoxModel) cbConfigServer.getModel();
        for (StoredSettings.ConfServer cs : ds.getConfigServers()) {
            mod.addElement(cs);
        }
        if (mod.getSize() > 0) {
            cbConfigServer.setSelectedIndex(0);
        }
//</editor-fold>

    }

    private ValuesEditor confServEditor;

    private String profile;

    /**
     * Creates new form AppForm
     */
    public AppForm() {
        initComponents();
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
        jMenu3 = new javax.swing.JMenu();

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
                .addContainerGap(815, Short.MAX_VALUE)
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

        taOutput.setColumns(20);
        taOutput.setRows(5);
        jScrollPane1.setViewportView(taOutput);

        jpOutput.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        getContentPane().add(jpOutput);

        jMenu1.setText("ConfigRequest");

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

        jMenu3.setText("Exit");
        jMenu3.addMenuListener(new javax.swing.event.MenuListener() {
            public void menuSelected(javax.swing.event.MenuEvent evt) {
                jMenu3MenuSelected(evt);
            }
            public void menuDeselected(javax.swing.event.MenuEvent evt) {
            }
            public void menuCanceled(javax.swing.event.MenuEvent evt) {
            }
        });
        jMenu3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu3ActionPerformed(evt);
            }
        });
        jMenuBar1.add(jMenu3);

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
            objByDBID = new RequestDialog(this, new ObjByDBID());
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
                                requestOutput(retrieveObject.toString());
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
            appByIP = new RequestDialog(this, new AppByIP());
        }
        if (appByIP.doShow()) {
            runAppByIPActionPerformed(evt);
        }
    }//GEN-LAST:event_miAppByIPActionPerformed

    private void btClearOutputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btClearOutputActionPerformed
        taOutput.setText("");
    }//GEN-LAST:event_btClearOutputActionPerformed

    private void jMenu3MenuSelected(javax.swing.event.MenuEvent evt) {//GEN-FIRST:event_jMenu3MenuSelected
//    logger.info("exit pressed");        // TODO add your handling code here:
        System.exit(0);
    }//GEN-LAST:event_jMenu3MenuSelected

    private void jMenu3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu3ActionPerformed
        // TODO add your handling code here:
        logger.info("jMenu3ActionPerformed pressed " + evt.getActionCommand());
    }//GEN-LAST:event_jMenu3ActionPerformed

    RequestDialog appByOption;

    private void miAppByOptionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miAppByOptionActionPerformed
        if (appByOption == null) {
            appByOption = new RequestDialog(this, new AppByOptions());
        }
        if (appByOption.doShow()) {
            appByOptionThread((AppByOptions) appByOption.getContentPanel());

        }
    }//GEN-LAST:event_miAppByOptionActionPerformed

    RequestDialog objByAnnex;

    private void miObjectByAnnexActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miObjectByAnnexActionPerformed
        if (objByAnnex == null) {
            objByAnnex = new RequestDialog(this, new ObjByAnnex());
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

                if (pn.iscbAttrSelected()) {
                    CfgEnumeratorQuery query = new CfgEnumeratorQuery(configServerManager.getService());

                    Collection<CfgEnumerator> execute = query.execute();
                    execQuery(query, new ISearchNamedProperties() {
                        @Override
                        public String[] getNamedProperties(CfgObject obj) {
                            CfgEnumerator o = (CfgEnumerator) obj;
                            String[] ret = new String[3];
                            ret[0] = o.getDescription();
                            ret[1] = o.getDisplayName();
                            ret[2] = o.getName();
                            return ret;
                        }

                        @Override
                        public String getName(CfgObject obj) {
                            return ((CfgEnumerator) obj).getDisplayName();
                        }

                        @Override
                        public String getShortPrint(CfgObject obj) {
                            CfgEnumerator o = (CfgEnumerator) obj;
                            return "Business Attribute [" + o.getDisplayName() + "] DBID: " + o.getDBID();
                        }
                    },
                            pn);

                }
                if (pn.iscbAttrValueSelected()) {
                    CfgEnumeratorValueQuery query = new CfgEnumeratorValueQuery(configServerManager.getService());

                    execQuery(query, new ISearchNamedProperties() {
                        @Override
                        public String[] getNamedProperties(CfgObject obj) {
                            CfgEnumeratorValue o = (CfgEnumeratorValue) obj;
                            String[] ret = new String[3];
                            ret[0] = o.getDescription();
                            ret[1] = o.getDisplayName();
                            ret[2] = o.getName();

                            return ret;
                        }

                        @Override
                        public String getName(CfgObject obj) {
                            return ((CfgEnumeratorValue) obj).getDisplayName();
                        }

                        @Override
                        public String getShortPrint(CfgObject obj) {
                            CfgEnumeratorValue o = (CfgEnumeratorValue) obj;
                            return "BI Value [" + o.getDisplayName() + "] DBID: " + o.getDBID();
                        }
                    },
                            pn);

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
            bussAttr = new RequestDialog(this, new BussAttr());
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
    private javax.swing.JMenu jMenu3;
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
    private javax.swing.JPanel jpConfServ;
    private javax.swing.JPanel jpOutput;
    private javax.swing.JMenuItem miAppByIP;
    private javax.swing.JMenuItem miAppByOption;
    private javax.swing.JMenuItem miBusinessAttribute;
    private javax.swing.JMenuItem miObjByDBID;
    private javax.swing.JMenuItem miObjectByAnnex;
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

    private boolean matching(Pattern ptSection, String stringKey) {
        if (ptSection != null && stringKey != null) {
            Matcher matcher = ptSection.matcher(stringKey);
            if (matcher != null) {
                return matcher.find();
            }
        }
        return false;
    }

    private <T extends CfgObject> void findApps(
            CfgQuery q,
            Class< T> cls,
            IKeyValueProperties props,
            ISearchSettings ss
    ) throws ConfigException, InterruptedException {

        StringBuilder buf = new StringBuilder();

        Collection<T> cfgObjs = configServerManager.getResults(q, cls);

        if (cfgObjs == null || cfgObjs.isEmpty()) {
            logger.debug("no objects found\n", false);
        } else {
            logger.debug("retrieved " + cfgObjs.size() + " total objects type " + cls.getSimpleName());
            int flags = ((ss.isRegex()) ? Pattern.LITERAL : 0) | ((ss.isCaseSensitive()) ? 0 : Pattern.CASE_INSENSITIVE);
            Pattern ptAll = null;
            Pattern ptSection = null;
            Pattern ptOption = null;
            Pattern ptVal = null;
            String section = ss.getSection();
            String option = ss.getOption();
            String val = ss.getValue();

            if (ss.isSearchAll()) {
                ptAll = (ss.isSearchAll() && ss.getAllSearch() != null ? Pattern.compile(ss.getAllSearch(), flags) : null);
            } else {
                ptSection = (section == null) ? null : Pattern.compile(section, flags);
                ptOption = (option == null) ? null : Pattern.compile(option, flags);
                ptVal = (val == null) ? null : Pattern.compile(val, flags);
            }
            KeyValueCollection kv = new KeyValueCollection();

            boolean checkForSectionOrOption = (ptAll != null || option != null || section != null || val != null);
            int cnt = 0;
            for (CfgObject cfgObj : cfgObjs) {
                boolean shouldInclude = false;
                if (checkForSectionOrOption) {
                    KeyValueCollection options;
                    options = props.getProperties(cfgObj);
                    kv.clear();
                    String sectionFound = null;
                    if (ptAll != null) { // if we got here and we are searching for all, it means name is already matched
                        for (String string : props.getName(cfgObj)) {
                            if (matching(ptAll, string)) {
                                shouldInclude = true;
                                break;
                            }
                        }

                    }
                    if (options != null) {
                        Enumeration<KeyValuePair> enumeration = options.getEnumeration();
                        KeyValuePair el;

                        while (enumeration.hasMoreElements()) {
                            el = enumeration.nextElement();

                            if (ptAll != null) {
                                if (matching(ptAll, el.getStringKey())) {
                                    sectionFound = el.getStringKey();
                                    shouldInclude = true;
                                }
                            } else if (ptSection != null) {
                                if (!matching(ptSection, el.getStringKey())) {
                                    continue;
                                } else {
                                    sectionFound = el.getStringKey();
                                }
                            }

                            if (ptVal == null && ptOption == null && ptAll == null) {
                                kv.addObject(el.getStringKey(), new KeyValueCollection());
                                shouldInclude = true;
                            } else {
                                KeyValueCollection addedValues = new KeyValueCollection();
                                Object value = el.getValue();
                                if (value instanceof KeyValueCollection) {
                                    KeyValueCollection sectionValues = (KeyValueCollection) value;
                                    Enumeration<KeyValuePair> optVal = sectionValues.getEnumeration();
                                    KeyValuePair theOpt;
                                    while (optVal.hasMoreElements()) {
                                        theOpt = optVal.nextElement();
                                        boolean isOptFound = false;
                                        boolean isValFound = false;

                                        if (ptAll != null) {
                                            if (matching(ptAll, theOpt.getStringKey())) {
                                                isOptFound = true;
                                            }
                                            if (matching(ptAll, theOpt.getStringValue())) {
                                                isValFound = true;
                                            }
                                        } else {
                                            if (ptOption != null) {
                                                if (matching(ptOption, theOpt.getStringKey())) {
                                                    isOptFound = true;
                                                }
                                            }
                                            if (ptVal != null) {
                                                if (matching(ptVal, theOpt.getStringValue())) {
                                                    isValFound = true;
                                                }
                                            }
                                        }
                                        if (isOptFound || isValFound) {
                                            addedValues.addPair(theOpt);

                                        }
                                    }
                                } else {
                                    logger.debug("value [" + value + "] is of type " + value.getClass() + " obj: " + cfgObj);
                                    if (ptVal != null) {
                                        if (matching(ptVal, value.toString())) {
                                            addedValues.addPair(el);

                                        }
                                    }
                                }
                                if (!addedValues.isEmpty() || sectionFound != null) {
                                    String sect = (sectionFound != null) ? sectionFound : el.getStringKey();
                                    KeyValueCollection list = kv.getList(sect);
                                    if (list == null) {
                                        list = new KeyValueCollection();
                                        kv.addList(sect, list);
                                    }
                                    for (Object addedValue : addedValues.toArray()) {
                                        list.add(addedValue);
                                    }
//                                    kv.addObject(el.getStringKey(), addedValues);
                                    shouldInclude = true;
                                }

                            }

                        }
                    }

                } else {
                    shouldInclude = true;
                }
                if (shouldInclude) {
                    if (ss.isFullOutputSelected()) {
                        buf.append(cfgObj.toString()).append("\n");
                    } else {
                        Object[] names = props.getName(cfgObj).toArray();
                        buf.append("\"").append(names[0]).append("\"").append(" path: ").append(cfgObj.getObjectPath()).append(", type:").append(cfgObj.getObjectType()).append(", DBID: " + cfgObj.getObjectDbid());
                        buf.append("\n");
                        if (names.length > 1) {
                            buf.append('\t');
                            for (int i = 1; i < names.length; i++) {
                                if (i > 1) {
                                    buf.append(", ");
                                }
                                buf.append(names[i]);
                            }
                        }
                        if (checkForSectionOrOption) {
                            buf.append("   ");
                            buf.append(kv);
                        }
                        buf.append("\n");
                    }
                    cnt++;
                }

            }
            if (cnt > 0) {
                requestOutput("Search done, located " + cnt + " objects type "+cls.getSimpleName()+" -->\n" + buf + "<--\n");
            }
        }
    }

    private void findObjByAnnex(Collection<CfgObject> apps, ObjByAnnex pn) {
        StringBuilder buf = new StringBuilder();
        boolean isRegex = pn.isRegex();
        boolean isCaseSensitive = pn.isCaseSensitive();

        int flags = ((!pn.isRegex()) ? Pattern.LITERAL : 0) | ((pn.isCaseSensitive()) ? Pattern.CASE_INSENSITIVE : 0);
        String section = pn.getSection();
        Pattern ptSection = (section == null) ? null : Pattern.compile(section, flags);
        String option = pn.getOption();
        Pattern ptOption = (option == null) ? null : Pattern.compile(option, flags);
        String val = pn.getValue();
        Pattern ptVal = (val == null) ? null : Pattern.compile(val, flags);
        KeyValueCollection kv = new KeyValueCollection();

        buf.append("searching for sec"
                + "[")
                .append((section == null) ? "" : section)
                .append("]"
                        + " opt[").append((option == null) ? "" : option)
                .append("]"
                        + " val[").append((val == null) ? "" : val)
                .append("] case").append((pn.isCaseSensitive()) ? "" : "in").append("sensitive ")
                .append((pn.isRegex()) ? "regex" : "")
                .append("\n");

        boolean checkForSectionOrOption = (option != null || section != null || val != null);
        int cnt = 0;
        Iterable<CfgApplication> apps1 = null;
        for (CfgApplication cfgApplication : apps1) {
            boolean shouldInclude = false;
            if (checkForSectionOrOption) {
                KeyValueCollection options;
                options = cfgApplication.getFlexibleProperties();
                kv.clear();
                if (options != null) {

                    Enumeration<KeyValuePair> enumeration = options.getEnumeration();
                    KeyValuePair el;

                    while (enumeration.hasMoreElements()) {
                        el = enumeration.nextElement();
                        if (ptSection != null) {
                            if (!matching(ptSection, el.getStringKey())) {
                                continue;
                            }
                        }
                        if (ptVal == null && ptOption == null) {
                            kv.addObject(el.getStringKey(), new KeyValueCollection());
                        } else {
                            KeyValueCollection sectionValues = (KeyValueCollection) el.getValue();
                            Enumeration<KeyValuePair> optVal = sectionValues.getEnumeration();
                            KeyValuePair theOpt;
                            KeyValueCollection addedValues = new KeyValueCollection();
                            while (optVal.hasMoreElements()) {
                                theOpt = optVal.nextElement();
                                boolean isOptFound = false;
                                boolean isValFound = false;
                                if (ptOption != null) {
                                    if (matching(ptOption, theOpt.getStringKey())) {
                                        isOptFound = true;
                                    }
                                }
                                if (ptVal != null) {
                                    if (matching(ptVal, theOpt.getStringValue())) {
                                        isValFound = true;
                                    }
                                }
                                if (isOptFound || isValFound) {
                                    addedValues.addPair(theOpt);

                                }
                            }
                            if (!addedValues.isEmpty()) {
                                kv.addObject(el.getStringKey(), addedValues);
                                shouldInclude = true;
                            }
                        }

                    }
                }

            } else {
                shouldInclude = true;
            }
            if (shouldInclude) {
                if (pn.isFullOutputSelected()) {
                    buf.append(cfgApplication.toString()).append("\n");
                } else {
                    buf.append(cfgApplication.getName()).append(", DBID: " + cfgApplication.getDBID());
                    if (checkForSectionOrOption) {
                        buf.append("\t");
                        buf.append(kv);
                    }
                    buf.append("\n");
                }
                cnt++;
            }

        }
        requestOutput("retrieved " + cnt + " applications\n" + buf + "\n");

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
                        Lookup l = new Lookup(ReverseMap.fromAddress(ip1), org.xbill.DNS.Type.PTR);
                        Record[] hostNames = l.run();
                        if (hostNames == null) {
                            buf.append("IP [" + ip1 + "] not resolved\n");
                        } else {
                            if ((connectToConfigServer())) {
                                CfgHostQuery hq = new CfgHostQuery(configServerManager.getService());
                                CfgApplicationQuery aq = new CfgApplicationQuery(configServerManager.getService());
                                buf.append("resolved IP[" + ip1 + "] to ");
                                for (Record hostName : hostNames) {
                                    PTRRecord r = (PTRRecord) hostName;
                                    buf.append(r.getTarget().toString(true)).append("\n");
                                    String mask = r.getTarget().getLabelString(0) + "*";
                                    hq.setName(mask);
                                    Collection<CfgHost> hostsFound = configServerManager.getResults(hq, CfgHost.class);
                                    if (hostsFound != null) {
                                        for (CfgHost cfgHost : hostsFound) {
                                            buf.append("Found host: ").append(cfgHost.getName()).append(" DBID:").append(cfgHost.getDBID()).append(" type: ").append(cfgHost.getType()).append(" os: ").append(cfgHost.getOSinfo().getOStype()).append("\n");
                                            aq.setHostDbid(cfgHost.getDBID());
                                            Collection<CfgApplication> appsFound = aq.execute();
                                            buf.append("\tapplications on the host:\n");
                                            if (appsFound == null) {
                                                buf.append("**** no apps found!!! ");
                                            } else {
                                                for (CfgApplication cfgApplication : appsFound) {
                                                    buf.append("\t\t\"").append(cfgApplication.getName()).append("\"").append(" (type:").append(cfgApplication.getType()).append(", DBID:").append(cfgApplication.getDBID()).append(")\n");
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
                        buf.append(ex.getMessage() + "\n");
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
                        findApps(
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
                                pn);

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
                } catch (Exception e) {
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
                        requestOutput("Exception processing: " + ex.toString(), false);
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

                    CfgObjectType t = pn.getSelectedObjType();
                    if (t != null) {
                        doTheSearch(t, pn, true);

                    } else {
                        for (CfgObjectType value : CfgObjectType.values()) {
                            doTheSearch(value, pn, false);
                        }
                    }

                }
            }
        });

    }

    private void doTheSearch(CfgObjectType t, ObjByAnnex pn, boolean warnNotFound) throws ConfigException, InterruptedException {
        IConfService service = configServerManager.getService();
        //<editor-fold defaultstate="collapsed" desc="CfgObjectType.CFGDN">
        if (t == CfgObjectType.CFGDN) {
            CfgDNQuery query = new CfgDNQuery();
            String n = pn.getObjName();
            if (n != null) {
                query.setDnNumber(n);
            }
            CfgDNType selectedObjSubType = (CfgDNType) pn.getSelectedObjSubType();
            if (selectedObjSubType != null) {
                query.setDnType(selectedObjSubType);
            }

            findApps(
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
                    pn);
        } //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="CfgObjectType.CFGSwitch">
        else if (t == CfgObjectType.CFGSwitch) {
            CfgSwitchQuery query = new CfgSwitchQuery();
            String n = pn.getObjName();
            if (n != null) {
                query.setName(n);
            }
//                            CfgSwitchType selectedObjSubType = (CfgSwitchType) pn.getSelectedObjSubType();
//                            if (selectedObjSubType != null) {
//                                query.(selectedObjSubType);
//                            }

            findApps(
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
                    pn);
        } //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="CfgObjectType.CFGAgentLogin">
        else if (t == CfgObjectType.CFGAgentLogin) {
            CfgAgentLoginQuery query = new CfgAgentLoginQuery();
            String n = pn.getObjName();
            if (n != null) {
                query.setLoginCode(n);
            }
//                            CfgSwitchType selectedObjSubType = (CfgSwitchType) pn.getSelectedObjSubType();
//                            if (selectedObjSubType != null) {
//                                query.(selectedObjSubType);
//                            }

            findApps(
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
                    pn);
        } //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="CfgObjectType.CFGPlace">
        else if (t == CfgObjectType.CFGPlace) {
            CfgPlaceQuery query = new CfgPlaceQuery();
            String n = pn.getObjName();
            if (n != null) {
                query.setName(n);
            }
//                            CfgSwitchType selectedObjSubType = (CfgSwitchType) pn.getSelectedObjSubType();
//                            if (selectedObjSubType != null) {
//                                query.(selectedObjSubType);
//                            }

            findApps(
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
                    pn);
        } //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="CfgObjectType.CFGPerson">
        else if (t == CfgObjectType.CFGPerson) {
            CfgPersonQuery query = new CfgPersonQuery();
            String n = pn.getObjName();
            if (n != null) {
                query.setUserName(n);
            }
//                            CfgSwitchType selectedObjSubType = (CfgSwitchType) pn.getSelectedObjSubType();
//                            if (selectedObjSubType != null) {
//                                query.(selectedObjSubType);
//                            }

            findApps(
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
                    pn);
        } //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="CfgObjectType.CFGAgentGroup">
        else if (t == CfgObjectType.CFGAgentGroup) {
            CfgAgentGroupQuery query = new CfgAgentGroupQuery();
            String n = pn.getObjName();
            if (n != null) {
                query.setName(n);

            }

            findApps(
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
                    pn);
        } //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="CfgObjectType.CFGDNGroup">
        else if (t == CfgObjectType.CFGDNGroup) {
            CfgDNGroupQuery query = new CfgDNGroupQuery();
            String n = pn.getObjName();
            if (n != null) {
                query.setName(n);

            }

            findApps(
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
                    pn);
        } //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="CfgObjectType.CFGPlaceGroup">
        else if (t == CfgObjectType.CFGPlaceGroup) {
            CfgPlaceGroupQuery query = new CfgPlaceGroupQuery();
            String n = pn.getObjName();
            if (n != null) {
                query.setName(n);

            }

            findApps(
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
                    pn);
        } //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="CfgObjectType.CFGScript">
        else if (t == CfgObjectType.CFGScript) {
            CfgScriptQuery query = new CfgScriptQuery();
            String n = pn.getObjName();
            if (n != null) {
                query.setName(n);
            }
//                            CfgSwitchType selectedObjSubType = (CfgSwitchType) pn.getSelectedObjSubType();
//                            if (selectedObjSubType != null) {
//                                query.(selectedObjSubType);
//                            }

            findApps(
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
                    pn);
//</editor-fold>
            //<editor-fold defaultstate="collapsed" desc="CfgObjectType.CFGTransaction">
        } else if (t == CfgObjectType.CFGTransaction) {
            CfgTransactionQuery query = new CfgTransactionQuery();
            String n = pn.getObjName();
            if (n != null) {
                query.setName(n);
            }
            CfgTransactionType tt = (CfgTransactionType) pn.getSelectedObjSubType();
            if (tt != null) {
                query.setObjectType(tt);
            }

            findApps(
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
                    pn);
        } //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="CfgObjectType.CFGEnumerator">
        else if (t == CfgObjectType.CFGEnumerator) {
            CfgEnumeratorQuery query = new CfgEnumeratorQuery();
            String n = pn.getObjName();
            if (n != null) {
                query.setName(n);
            }

            findApps(
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
                    pn);
        } //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="CfgObjectType.CFGEnumeratorValue">
        else if (t == CfgObjectType.CFGEnumeratorValue) {
            CfgEnumeratorValueQuery query = new CfgEnumeratorValueQuery();
            String n = pn.getObjName();
            if (n != null) {
                query.setName(n);
            }

            findApps(
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
                    pn);
//</editor-fold>

        } else {
            if (warnNotFound) {
                logger.info("Searching for type " + t + " not implemented yet");
            }

        }
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

    class RequestDialog extends StandardDialog {

        private JPanel contentPanel;

        public JPanel getContentPanel() {
            return contentPanel;
        }

        private RequestDialog(Window parent, JPanel contentPanel) {
            super(parent);
            this.contentPanel = contentPanel;
            setTitle("Enter request parameters");
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

        public boolean doShow() {

//            setModal(true);
            pack();

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

            return getDialogResult() == StandardDialog.RESULT_AFFIRMED;

        }
    }

}
