/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ssydoruk.confservutils;

import Utils.InfoPanel;
import static com.ssydoruk.confservutils.AppForm.getObjName;
import com.genesyslab.platform.applicationblocks.com.objects.CfgFolder;
import com.genesyslab.platform.configuration.protocol.types.CfgObjectType;
import java.awt.BorderLayout;
import java.awt.HeadlessException;
import java.awt.Window;
import java.util.AbstractCollection;
import java.util.ArrayList;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author stepan_sydoruk
 */
abstract class CSVGeneralImportDialog extends Utils.InfoPanel {

    protected final JDialog theDialog;

    private final ConfigServerManager cfg;

    protected ArrayList<CfgFolder> findFolders(CfgObjectType type, Window theParent, boolean multipleSelect) {

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

    public CSVGeneralImportDialog(Window parent, ConfigServerManager _cfg) throws HeadlessException {
        super(parent, JOptionPane.OK_CANCEL_OPTION);
        cfg = _cfg;
        this.theDialog = this;
    }

    protected class FoldersModel extends AbstractTableModel {

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

        public ArrayList<CfgFolder> getAllFolders() {
            return folders;
        }

    }

}
