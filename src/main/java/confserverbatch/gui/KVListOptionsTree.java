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

import com.genesyslab.platform.commons.collections.KeyValueCollection;

import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeModelEvent;

import java.awt.event.ActionEvent;


class KVListOptionsTree
        extends JTree {

    private JPopupMenu jPopupMenu;
    private Action actNewSect = new NewSectionAction();
    private Action actNewOption = new NewOptionAction();
    private Action actRemove = new RemoveSelectedAction();
    private Action actRename = new RenameSelectedAction();


    public KVListOptionsTree() {
        super(new KVListOptionsTreeModel());

        this.setCellEditor(new KVListOptionsTreeCellEditor(this));
        this.setRootVisible(false);
        this.setEnabled(false);

        jPopupMenu = new JPopupMenu();
        jPopupMenu.add(actNewSect);
        jPopupMenu.add(actNewOption);
        jPopupMenu.add(actRemove);
        jPopupMenu.add(actRename);

        if (getSelectionModel() != null) {
            getSelectionModel().setSelectionMode(
                    TreeSelectionModel.SINGLE_TREE_SELECTION);
        }
        this.addTreeSelectionListener(new InnerTreeSelectionListener());
        treeModel.addTreeModelListener(new InnerTreeModelListener());

        this.refreshPopupMenu();
    }

    @Override
    public void setEnabled(final boolean enabled) {
        super.setEnabled(enabled);
        if (enabled) {
            this.setComponentPopupMenu(jPopupMenu);
            this.setEditable(true);
        } else {
            this.setComponentPopupMenu(null);
            this.setEditable(false);
        }
    }

    public void setKVListData(
            final KeyValueCollection data) {
        ((KVListOptionsTreeModel) treeModel).setData(data);
    }

    public KeyValueCollection getKVListData() {
        return ((KVListOptionsTreeModel) treeModel).getData();
    }


    protected String getSelectedSectionName() {
        String sectName = null;
        Object component = null;
        if (getSelectionPath() != null) {
            component = getSelectionPath().getLastPathComponent();
        }
        if (component instanceof KVListOptionsTreeSectionNode) {
            sectName = ((DefaultMutableTreeNode) component).getUserObject().toString();
        } else if (component instanceof KVListOptionsTreeOptionNode) {
            sectName = ((MutableTreeNode) component).getParent().toString();
        }
        return sectName;
    }

    protected String getSelectedOptionName() {
        String optName = null;
        if (getSelectionPath() != null
                && getSelectionPath().getLastPathComponent()
                        instanceof KVListOptionsTreeOptionNode) {
            KVListOptionsTreeOptionNode node = (KVListOptionsTreeOptionNode)
                    getSelectionPath().getLastPathComponent();
            String optVal = node.getUserObject().toString();
            int pos = optVal.indexOf(":");
            if (pos > 0) {
                optName = optVal.substring(0, pos - 1).trim();
            }
        }
        return optName;
    }


    protected void refreshPopupMenu() {
        int cnt = getSelectionCount();
        if (cnt == 1) {
            String optName = getSelectedOptionName();
            String sectName = getSelectedSectionName();
            actNewOption.putValue(Action.NAME, "New option in '" + sectName + "'");
            actNewOption.setEnabled(true);
            if (optName != null) {
                actRemove.putValue(Action.NAME, "Remove option '" + optName + "'");
            } else if (sectName != null) {
                actRemove.putValue(Action.NAME, "Remove section '" + sectName + "'");
            }
            actRemove.setEnabled(true);
            if (optName != null) {
                actRename.putValue(Action.NAME, "Modify option '" + optName + "'");
            } else if (sectName != null) {
                actRename.putValue(Action.NAME, "Rename section '" + sectName + "'");
            }
            actRename.setEnabled(true);
        } else if (cnt == 0) {
            actNewOption.putValue(Action.NAME, "New option");
            actNewOption.setEnabled(false);
            actRemove.putValue(Action.NAME, "Remove selected");
            actRemove.setEnabled(false);
            actRename.putValue(Action.NAME, "Rename selected");
            actRename.setEnabled(false);
        }
    }


    private class NewSectionAction extends AbstractAction {
        NewSectionAction() {
            super("New section");
        }
        public void actionPerformed(final ActionEvent e) {
            KVListOptionsTreeModel model = (KVListOptionsTreeModel) treeModel;
            TreePath path = model.createNewSection();
            selectionModel.setSelectionPath(path);
            startEditingAtPath(path);
        }
    }

    private class NewOptionAction extends AbstractAction {
        NewOptionAction() {
            super("New option");
        }
        public void actionPerformed(final ActionEvent e) {
            KVListOptionsTreeModel model = (KVListOptionsTreeModel) treeModel;
            String sectionName = getSelectedSectionName();
            if (sectionName != null) {
                TreePath path = model.getDataSectionPath(sectionName);
                if (path != null) {
                    if (!isExpanded(path)) {
                        expandPath(path);
                    }
                }
                path = model.setDataOptionValue(sectionName,
                        "NewOptionName", "OptionValue");
                selectionModel.setSelectionPath(path);
                startEditingAtPath(path);
            }
        }
    }

    private class RemoveSelectedAction extends AbstractAction {
        RemoveSelectedAction() {
            super("Remove selected");
        }
        public void actionPerformed(ActionEvent e) {
            TreePath path = getSelectionPath();
            if (path != null) {
                ((KVListOptionsTreeModel) treeModel).removeChild(
                        path.getLastPathComponent());
            }
        }
    }

    private class RenameSelectedAction extends AbstractAction {
        RenameSelectedAction() {
            super("Rename selected");
        }
        public void actionPerformed(ActionEvent e) {
            TreePath path = getSelectionPath();
            if (path != null) {
                if (path.getPathCount() == 2) { // rename options section
                    startEditingAtPath(path);
                } else if (path.getPathCount() == 3) { // modify option in section
                    startEditingAtPath(path);
                }
            }
        }
    }

    private class InnerTreeSelectionListener
            implements TreeSelectionListener {
        
        public void valueChanged(final TreeSelectionEvent e) {
            refreshPopupMenu();
        }
    }

    private class InnerTreeModelListener
            implements TreeModelListener {

        public void treeNodesChanged(TreeModelEvent e) {
            refreshPopupMenu();
        }

        public void treeNodesInserted(TreeModelEvent e) {
        }

        public void treeNodesRemoved(TreeModelEvent e) {
        }

        public void treeStructureChanged(TreeModelEvent e) {
        }
    }
}
