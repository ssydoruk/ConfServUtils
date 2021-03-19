/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ssydoruk.confservutils;

import Utils.TableColumnAdjuster;
import com.genesyslab.platform.applicationblocks.com.objects.CfgFolder;
import com.genesyslab.platform.configuration.protocol.types.CfgObjectType;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

/**
 *
 * @author stepan_sydoruk
 */
abstract class CSVCreateAgentAdmin extends CSVGeneralImportDialog {

    private static CSVCreateAgentAdmin instance = null;

    public static CSVCreateAgentAdmin getInstance(Window parent, ConfigServerManager cfg) {
        if (instance == null) {
            instance = new CSVCreateAgentAdmin(parent, cfg) {
            };
        }
        return instance;
    }

    DefaultTableModel modelLoginID;
    JTable tabLoginID;
    JTable tabLoginIDsFolders;

    TableColumnAdjuster tcaLoginID;
    JPanel loginidFolders;
    JPanel topPan;
    private final ConfigServerManager cfg;

    public CSVCreateAgentAdmin(Window parent, ConfigServerManager _cfg) throws HeadlessException {
        super(parent, _cfg);
        cfg = _cfg;

        modelLoginID = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // To change body of generated methods, choose Tools | Templates.
            }
        };

        modelLoginID.addColumn("LoginID");

        tabLoginID = new JTable(modelLoginID);
        tabLoginID.getTableHeader().setVisible(true);

        JScrollPane jp = new JScrollPane(tabLoginID);
        // jp.add(tab);

        Dimension preferredSize = new Dimension(600, 400);
        jp.setPreferredSize(preferredSize);
        jp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        topPan = new JPanel();

        topPan.setLayout(new BoxLayout(topPan, BoxLayout.PAGE_AXIS));
        topPan.add(jp);

        loginidFolders = new JPanel();
        loginidFolders.setLayout(new BoxLayout(loginidFolders, BoxLayout.PAGE_AXIS));
        loginidFolders.setBorder(new TitledBorder("LoginID folders"));
        tabLoginIDsFolders = new JTable();
        tabLoginIDsFolders.getTableHeader().setVisible(false);
        //        tabDNFolders.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        loginidFolders.add(tabLoginIDsFolders);
        JButton btSelectDNFolder = new JButton(new AbstractAction("Select") {
            @Override
            public void actionPerformed(ActionEvent e) {
                ArrayList<CfgFolder> findFolders = findFolders(CfgObjectType.CFGAgentLogin, theDialog, true);
                if (findFolders != null) {
                    tabLoginIDsFolders.setModel(new FoldersModel(findFolders));
                }
            }
        });
        loginidFolders.add(btSelectDNFolder);
        topPan.add(loginidFolders);

    }

    private ArrayList<CfgFolder> lastSelectedFolders(JTable tab) {
        TableModel model = tab.getModel();
        if (model instanceof FoldersModel) {
            return ((FoldersModel) model).getAllFolders();
        }
        return null;
    }

    public ArrayList<CfgFolder> lastSelectedLoginidFolders() {
        return lastSelectedFolders(tabLoginIDsFolders);

    }

    public boolean shouldImportCSV(final ArrayList<String[]> loginIDs, boolean isImport) {

        modelLoginID.setRowCount(0);

        for ( String[] entry : loginIDs) {
            modelLoginID.addRow(new Object[]{entry[0]});
        }

        loginidFolders.setVisible(isImport);

        topPan.invalidate();

        StringBuilder title = new StringBuilder();
        if (isImport) {
            title.append("Do you want to check existense of agent login (total ").append(loginIDs.size()).append(")");
        } else {
            title.append("Do you want to import following agent login (total ").append(loginIDs.size()).append(")");
        }

        Utils.ScreenInfo.CenterWindow(this);
        showModal(topPan, title.toString());

        return getDialogResult() == JOptionPane.OK_OPTION;

    }
    
    
    public boolean shouldImportCSVCreateAdmin(final ArrayList<String[]> userNames, boolean isImport) {

        modelLoginID.setRowCount(0);

        for ( String[] entry : userNames) {
            modelLoginID.addRow(new Object[]{entry[0]});
        }

        loginidFolders.setVisible(isImport);

        topPan.invalidate();

        StringBuilder title = new StringBuilder();
        if (isImport) {
            title.append("Do you want to check existense of usernames (total ").append(userNames.size()).append(")");
        } else {
            title.append("Do you want to create admin accounts based on agent accounts (total ").append(userNames.size()).append(")");
        }

        Utils.ScreenInfo.CenterWindow(this);
        showModal(topPan, title.toString());

        return getDialogResult() == JOptionPane.OK_OPTION;

    }

}
