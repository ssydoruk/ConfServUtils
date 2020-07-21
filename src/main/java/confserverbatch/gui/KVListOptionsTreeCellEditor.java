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

import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.DefaultCellEditor;
import javax.swing.border.Border;

import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellEditor;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import java.util.EventObject;


class KVListOptionsTreeCellEditor
        extends DefaultTreeCellEditor {

    KVListOptionsTreeCellEditor(final JTree tree) {
        super(tree, new DefaultTreeCellRenderer());
    }

    @Override
    public boolean isCellEditable(final EventObject event) {
        boolean returnValue = super.isCellEditable(event);
        if (returnValue) {
            if (!(tree.getLastSelectedPathComponent()
                    instanceof KVListOptionsTreeSectionNode)
                && !(tree.getLastSelectedPathComponent()
                        instanceof KVListOptionsTreeOptionNode)) {
                returnValue = false;
            }
        }
        return returnValue;
    }

    @Override
    protected TreeCellEditor createTreeCellEditor() {
	    Border aBorder = UIManager.getBorder("Tree.editorBorder");
        DefaultTextField tf = new DefaultTextField(aBorder);
	    final DefaultCellEditor editor = new DefaultCellEditor(tf);
        editor.setClickCountToStart(2);
        tf.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(final FocusEvent e) {
                editor.stopCellEditing();
            }
        });
        return editor;
    }
}
