/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.airbnb.confservutils;

import Utils.InfoPanel;
import Utils.Pair;
import Utils.TableColumnAdjuster;
import static com.airbnb.confservutils.AppForm.getObjName;
import com.genesyslab.platform.applicationblocks.com.objects.CfgFolder;
import com.genesyslab.platform.configuration.protocol.types.CfgObjectType;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.util.AbstractCollection;
import java.util.ArrayList;
import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.TitledBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

/**
 *
 * @author stepan_sydoruk
 */
abstract class CSVImportDialog extends Utils.InfoPanel {

    static CSVImportDialog instance = null;

    public static CSVImportDialog getInstance(Window parent, ConfigServerManager cfg) {
        if (instance == null) {
            instance = new CSVImportDialog(parent, cfg) {
            };
        }
        return instance;
    }

    DefaultTableModel modelPlaceDN;
    JTable tabPlaceDN;
    JTable tabPlaceFolder;
    JTable tabDNFolders;

    TableColumnAdjuster tcaPlaceDN;
    private final JDialog theDialog;
    JPanel pPlaceFolder;
    JPanel dnFolders;
    JPanel topPan;
    private final ConfigServerManager cfg;

    private ArrayList<CfgFolder> findFolders(CfgObjectType type, Window theParent, boolean multipleSelect) {

        FindObject objName;

        objName = getObjName(theParent, CfgFolder.class.getSimpleName());

        if (objName == null) {
            return null;
        }
        AbstractCollection<CfgFolder> findFolders = cfg.findFolders(objName, type);
        if (findFolders != null) {
            JTable tab = new JTable();
//            DefaultTableModel infoTableModel = new DefaultTableModel();
//            infoTableModel.addColumn("Folders");
            int grandTotal = 0;
            int selectedRowIdx = -1;
            InfoPanel p = null;

//            for (CfgFolder findFolder : findFolders) {
//                infoTableModel.addRow(new Object[]{findFolder});
//                grandTotal++;
//            }
//        infoTableModel.addRow(new Object[]{"TOTAL(" + grandTotal + ")"});
            tab.setModel(new FoldersModel(findFolders));
            if (selectedRowIdx >= 0) {
                ListSelectionModel selectionModel1 = tab.getSelectionModel();
                selectionModel1.setSelectionInterval(selectedRowIdx, selectedRowIdx);
            }

            String theTitle = "Select a folder (total " + findFolders.size() + ")";

            JScrollPane jScrollPane = new JScrollPane(tab);
            tab.getTableHeader().setVisible(false);
            tab.setSelectionMode((multipleSelect) ? ListSelectionModel.MULTIPLE_INTERVAL_SELECTION : ListSelectionModel.SINGLE_INTERVAL_SELECTION);

            JPanel listPane = new JPanel(new BorderLayout(10, 20));

            listPane.add(new JPanel(new BorderLayout()).add(jScrollPane));

            p = new InfoPanel(theParent, theTitle, listPane, JOptionPane.OK_CANCEL_OPTION);

            p.showModal();
            if (p.getDialogResult() == JOptionPane.OK_OPTION) {
                ArrayList<CfgFolder> ret = new ArrayList<>();
                if (tab.getSelectedRowCount() > 0) {
                    if (multipleSelect) {
                        for (int selectedRow : tab.getSelectedRows()) {
                            ret.add(((FoldersModel) tab.getModel()).getFolderAt(selectedRow));
                        }

                    } else {
                        ret.add(((FoldersModel) tab.getModel()).getFolderAt(tab.getSelectedRow()));
                    }
                }
//apply filter
                return ret;
            }

        }
        return null;
    }

    public CSVImportDialog(Window parent, ConfigServerManager _cfg) throws HeadlessException {
        super(parent, JOptionPane.OK_CANCEL_OPTION);
        cfg = _cfg;
        this.theDialog = this;

        modelPlaceDN = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // To change body of generated methods, choose Tools | Templates.
            }
        };

        modelPlaceDN.addColumn("Place");
        modelPlaceDN.addColumn("DN");

        tabPlaceDN = new JTable(modelPlaceDN);
        tabPlaceDN.getTableHeader().setVisible(true);
//        tabPlaceDN.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

//        tcaPlaceDN = new TableColumnAdjuster(tabPlaceDN);
//        tcaPlaceDN.setColumnDataIncluded(true);
//        tcaPlaceDN.setColumnHeaderIncluded(false);
//        tcaPlaceDN.setDynamicAdjustment(true);
//        tcaPlaceDN.adjustColumns();

        JScrollPane jp = new JScrollPane(tabPlaceDN);
        // jp.add(tab);

        Dimension preferredSize = new Dimension(600, 400);
        jp.setPreferredSize(preferredSize);
        jp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        topPan = new JPanel();

        topPan.setLayout(new BoxLayout(topPan, BoxLayout.PAGE_AXIS));
        topPan.add(jp);

        pPlaceFolder = new JPanel();
        pPlaceFolder.setLayout(new BoxLayout(pPlaceFolder, BoxLayout.PAGE_AXIS));
        pPlaceFolder.setBorder(new TitledBorder("Place folder"));
        tabPlaceFolder = new JTable();
        tabPlaceFolder.getTableHeader().setVisible(false);
        //        tabPlaceFolder.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tabPlaceFolder.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        pPlaceFolder.add(tabPlaceFolder);
        JButton btSelectPlaceFolder = new JButton(new AbstractAction("Select") {
            @Override
            public void actionPerformed(ActionEvent e) {
                ArrayList<CfgFolder> findFolders = findFolders(CfgObjectType.CFGPlace, theDialog, false);
                if (findFolders != null) {
                    tabPlaceFolder.setModel(new FoldersModel(findFolders));
                }
            }
        });
        pPlaceFolder.add(btSelectPlaceFolder);
        dnFolders = new JPanel();
        dnFolders.setLayout(new BoxLayout(dnFolders, BoxLayout.PAGE_AXIS));
        dnFolders.setBorder(new TitledBorder("DN folders"));
        tabDNFolders = new JTable();
        tabDNFolders.getTableHeader().setVisible(false);
        //        tabDNFolders.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        dnFolders.add(tabDNFolders);
        JButton btSelectDNFolder = new JButton(new AbstractAction("Select") {
            @Override
            public void actionPerformed(ActionEvent e) {
                ArrayList<CfgFolder> findFolders = findFolders(CfgObjectType.CFGDN, theDialog, true);
                if (findFolders != null) {
                    tabDNFolders.setModel(new FoldersModel(findFolders));
                }
            }
        });
        dnFolders.add(btSelectDNFolder);
        topPan.add(pPlaceFolder);
        topPan.add(dnFolders);

    }

    private ArrayList<CfgFolder> lastSelectedFolders(JTable tab) {
        TableModel model = tab.getModel();
        if (model instanceof FoldersModel) {
            return ((FoldersModel) model).getAllFolders();
        }
        return null;
    }

    public ArrayList<CfgFolder> lastSelectedPlaceFolder() {
        return lastSelectedFolders(tabPlaceFolder);
    }

    public ArrayList<CfgFolder> lastSelectedDNFolders() {
        return lastSelectedFolders(tabDNFolders);

    }

    public boolean shouldImportCSV(final ArrayList<Pair<String, String>> placeDN, boolean isImport) {

        modelPlaceDN.setRowCount(0);

        for (Pair<String, String> entry : placeDN) {
            String place = entry.getKey();
            String dn = entry.getValue();
            modelPlaceDN.addRow(new Object[]{place, dn});
        }

        pPlaceFolder.setVisible(isImport);
        dnFolders.setVisible(isImport);

        topPan.invalidate();

        StringBuilder title = new StringBuilder();
        if (isImport) {
            title.append("Do you want to check existense Place/DN (total ").append(placeDN.size()).append(")");
        } else {
            title.append("Do you want to import following Place/DN (total ").append(placeDN.size()).append(")");
        }

        Utils.ScreenInfo.CenterWindow(this);
        showModal(topPan, title.toString());

        return getDialogResult() == JOptionPane.OK_OPTION;

    }

    class FoldersModel extends AbstractTableModel {

        private final ArrayList<CfgFolder> folders;

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return false; //To change body of generated methods, choose Tools | Templates.
        }

        public FoldersModel(AbstractCollection<CfgFolder> findFolders) {
            this.folders = new ArrayList(findFolders);
        }

        @Override
        public String getColumnName(int column) {
            return "Folders"; //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public int getRowCount() {
            return folders.size();
        }

        @Override
        public int getColumnCount() {
            return 1;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            CfgFolder fld = folders.get(rowIndex);
            return ConfigServerManager.getFolderFullName(fld);
        }

        private CfgFolder getFolderAt(int selectedRow) {
            return folders.get(selectedRow);
        }

        private ArrayList<CfgFolder> getAllFolders() {
            return folders;
        }

    };

};
