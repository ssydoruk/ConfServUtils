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
import com.genesyslab.platform.commons.collections.KeyValuePair;
import com.genesyslab.platform.commons.collections.ValueType;
import java.util.LinkedList;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.*;


class KVListOptionsTreeModel
        implements TreeModel {

    private DefaultMutableTreeNode rootNode;


    private final LinkedList<TreeModelListener> listeners =
            new LinkedList<TreeModelListener>();


    public KVListOptionsTreeModel() {
        rootNode = new DefaultMutableTreeNode("Options", true);
    }

    public KVListOptionsTreeModel(
            final KeyValueCollection initData) {
        this();
        setData(initData);
    }

    /**
     * Returns the root of the tree. Returns <code>null</code>
     * only if the tree has no nodes.
     *
     * @return  the root of the tree
     */
    public Object getRoot() {
        return rootNode;
    }

    /**
     * Returns the child of <code>parent</code> at index <code>index</code>
     * in the parent's
     * child array.  <code>parent</code> must be a node previously obtained
     * from this data source. This should not return <code>null</code>
     * if <code>index</code>
     * is a valid index for <code>parent</code> (that is <code>index >= 0 &&
     * index < getChildCount(parent</code>)).
     *
     * @param   parent  a node in the tree, obtained from this data source
     * @return  the child of <code>parent</code> at index <code>index</code>
     */
    public Object getChild(
            final Object parent,
            final int index) {
        if (parent instanceof TreeNode) {
            return ((TreeNode) parent).getChildAt(index);
        }
        return null;
    }

    /**
     * Returns the number of children of <code>parent</code>.
     * Returns 0 if the node
     * is a leaf or if it has no children.  <code>parent</code> must be a node
     * previously obtained from this data source.
     *
     * @param   parent  a node in the tree, obtained from this data source
     * @return  the number of children of the node <code>parent</code>
     */
    public int getChildCount(final Object parent) {
        if (parent instanceof TreeNode) {
            return ((TreeNode) parent).getChildCount();
        }
        return 0;
    }

    /**
     * Returns <code>true</code> if <code>node</code> is a leaf.
     * It is possible for this method to return <code>false</code>
     * even if <code>node</code> has no children.
     * A directory in a filesystem, for example,
     * may contain no files; the node representing
     * the directory is not a leaf, but it also has no children.
     *
     * @param   node  a node in the tree, obtained from this data source
     * @return  true if <code>node</code> is a leaf
     */
    public boolean isLeaf(final Object node) {
        if (node instanceof TreeNode) {
            return ((TreeNode) node).isLeaf();
        }
        return true;
    }

    /**
      * Messaged when the user has altered the value for the item identified
      * by <code>path</code> to <code>newValue</code>.
      * If <code>newValue</code> signifies a truly new value
      * the model should post a <code>treeNodesChanged</code> event.
      *
      * @param path path to the node that the user has altered
      * @param newValue the new value from the TreeCellEditor
      */
    public void valueForPathChanged(
            final TreePath path,
            final Object newValue) {
        DefaultMutableTreeNode node =
                (DefaultMutableTreeNode) path.getLastPathComponent();
        if (node instanceof KVListOptionsTreeSectionNode) {
            node.setUserObject(newValue);
        } else if (node instanceof KVListOptionsTreeOptionNode) {
            String oldVal = node.getUserObject().toString();
            String newVal = newValue.toString();
            String optName = null;
            String optValue = null;
            int pos = newVal.indexOf(':');
            if (pos > 0) {
                optName = newVal.substring(0, pos).trim();
                optValue = newVal.substring(pos + 1).trim();
            } else {
                pos = oldVal.indexOf(':'); // no option name in new input - use it from old value
                if (pos > 0) {
                    optName = oldVal.substring(0, pos).trim();
                    optValue = newVal.trim();
                } else {
                    // todo
                    //messageBox("Option name and value must be separated by ':' character.");
                    return;
                }
            }
            if (optName != null && optName.length() > 0) {
                node.setUserObject(optName + " : " + optValue);
            }
        }
        //String name = node.getUserObject().toString();
        for (TreeModelListener l : listeners) {
            l.treeNodesChanged(new TreeModelEvent(this, path));
        }
    }

    /**
     * Returns the index of child in parent. If either <code>parent</code>
     * or <code>child</code> is <code>null</code>, returns -1.
     * If either <code>parent</code> or <code>child</code> don't
     * belong to this tree model, returns -1.
     *
     * @param parent a note in the tree, obtained from this data source
     * @param child the node we are interested in
     * @return the index of the child in the parent, or -1 if either
     *    <code>child</code> or <code>parent</code> are <code>null</code>
     *    or don't belong to this tree model
     */
    public int getIndexOfChild(
            final Object parent,
            final Object child) {
        if ((parent instanceof TreeNode)
                && (child instanceof TreeNode)) {
            return ((TreeNode) parent).getIndex((TreeNode) child);
        }
        return -1;
    }


    public void setData(
            final KeyValueCollection data) {
        setData(rootNode, data);
    }

    protected void setData(
            final DefaultMutableTreeNode node,
            final KeyValueCollection data) {
        node.removeAllChildren();
        if (data != null) {
            for (Object pairObj : data) {
                DefaultMutableTreeNode child;
                KeyValuePair pair = (KeyValuePair) pairObj;
                if (pair.getValueType() == ValueType.TKV_LIST
                        || pair.getValueType() == ValueType.XKV_LIST) {
                    child = new KVListOptionsTreeSectionNode(pair.getStringKey());
                    setData(child, pair.getTKVValue());
                } else {
                    child = new KVListOptionsTreeOptionNode(pair);
                }
                node.add(child);
            }
        }
        for (TreeModelListener l : listeners) {
            l.treeStructureChanged(new TreeModelEvent(this, node.getPath()));
        }
    }


    private KVListOptionsTreeSectionNode getSectionNode(
            final String name) {
        for (int i = 0; i < rootNode.getChildCount(); i++) {
            TreeNode child = rootNode.getChildAt(i);
            if (child instanceof KVListOptionsTreeSectionNode
                    && ((KVListOptionsTreeSectionNode) child).getUserObject().equals(name)) {
                return (KVListOptionsTreeSectionNode) child;
            }
        }
        return null;
    }

    private KVListOptionsTreeOptionNode getOptionNode(
            final KVListOptionsTreeSectionNode section,
            final String name) {
        for (int i = 0; i < section.getChildCount(); i++) {
            TreeNode child = section.getChildAt(i);
            if (child instanceof KVListOptionsTreeOptionNode
                    && (((KVListOptionsTreeOptionNode) child).getUserObject().toString()
                            .startsWith(name + " :"))) {
                return (KVListOptionsTreeOptionNode) child;
            }
        }
        return null;
    }

    private String selectNewSectionName() {
        String name = "New section";
        do {
            KVListOptionsTreeSectionNode existing = getSectionNode(name);
            if (existing != null) {
                name += "1";
            } else {
                return name;
            }
        } while (true);
    }

    public TreePath createNewSection() {
        String name = selectNewSectionName();
        DefaultMutableTreeNode child = new KVListOptionsTreeSectionNode(name);
        rootNode.add(child);
        for (TreeModelListener l : listeners) {
            l.treeNodesInserted(new TreeModelEvent(
                    this, rootNode.getPath(),
                    new int[] {getIndexOfChild(rootNode, child)},
                    new Object[] {child}));
        }
        return new TreePath(child.getPath());
    }


    public TreePath getDataSectionPath(
            final String sectName) {
        KVListOptionsTreeSectionNode sect = getSectionNode(sectName);
        if (sect != null) {
            return new TreePath(sect.getPath());
        }
        return null;
    }

    public TreePath getDataOptionPath(
            final String sectName,
            final String optName) {
        KVListOptionsTreeSectionNode sect = getSectionNode(sectName);
        if (sect != null) {
            KVListOptionsTreeOptionNode opt = getOptionNode(sect, optName);
            if (opt != null) {
                return new TreePath(opt.getPath());
            }
        }
        return null;
    }

    public TreePath setDataOptionValue(
            final String sectName,
            final String optName,
            final Object value) {
        if (value == null) {
            throw new NullPointerException("object value");
        }
        KeyValuePair newOptionValue = new KeyValuePair(optName);
        if (value instanceof String) {
            newOptionValue.setStringValue((String) value);
        } else if (value instanceof byte[]) {
            newOptionValue.setBinaryValue((byte[]) value);
        } else if (value instanceof Integer) {
            newOptionValue.setIntValue((Integer) value);
        } else if (value instanceof Long) {
            newOptionValue.setIntValue(((Long) value).intValue());
        } else if (value instanceof KeyValueCollection) {
            newOptionValue.setTKVValue((KeyValueCollection) value);
        } else {
            throw new RuntimeException("Unknown object value type - "
                    + value.getClass());
        }

        KVListOptionsTreeSectionNode sect = getSectionNode(sectName);
        if (sect != null) {
            KVListOptionsTreeOptionNode opt = getOptionNode(sect, optName);
            if (opt == null) {
                opt = new KVListOptionsTreeOptionNode(newOptionValue);
                sect.add(opt);
                TreeModelEvent event = new TreeModelEvent(
                        this, sect.getPath(),
                        new int[] {getIndexOfChild(sect, opt)},
                        new Object[] {opt});
                for (TreeModelListener l : listeners) {
                    l.treeNodesInserted(event);
                }
            } else {
                opt.setUserObject(newOptionValue);
                TreeModelEvent event = new TreeModelEvent(
                        this, sect.getPath(),
                        new int[] {getIndexOfChild(sect, opt)},
                        new Object[] {opt});
                for (TreeModelListener l : listeners) {
                    l.treeNodesChanged(event);
                }
            }
            return new TreePath(opt.getPath());
        } else {
            throw new RuntimeException("Section '" + sectName + "' is not exist");
        }
    }


    public void removeChild(final Object node) {
        if (node instanceof DefaultMutableTreeNode) {
            DefaultMutableTreeNode item = (DefaultMutableTreeNode) node;
            TreeModelEvent event = new TreeModelEvent(
                    this, ((DefaultMutableTreeNode) item.getParent()).getPath(),
                    new int[] {getIndexOfChild(item.getParent(), item)},
                    new Object[] {item});
            item.removeFromParent();
            for (TreeModelListener l : listeners) {
                l.treeNodesRemoved(event);
            }
        }
    }

    public KeyValueCollection getData() {
        return getData(rootNode);
    }

    public KeyValueCollection getData(final DefaultMutableTreeNode node) {
        KeyValueCollection ret = new KeyValueCollection();
        for (int i = 0; i < node.getChildCount(); i++) {
            TreeNode child = node.getChildAt(i);
            if (child instanceof KVListOptionsTreeSectionNode) {
                KVListOptionsTreeSectionNode sect = (KVListOptionsTreeSectionNode) child;
                KeyValueCollection sectVal = getData(sect);
                if (sectVal != null && sectVal.length() > 0) {
                    ret.addList(sect.getUserObject().toString(), sectVal);
                }
            } else if (child instanceof KVListOptionsTreeOptionNode) {
                KVListOptionsTreeOptionNode opt = (KVListOptionsTreeOptionNode) child;
                String optVal = opt.getUserObject().toString();
                int pos = optVal.indexOf(":");
                if (pos > 0) {
                    String optName = optVal.substring(0, pos).trim();
                    String optValue = optVal.substring(pos + 1).trim();
                    ret.addString(optName, optValue);
                }
            }
        }
        return ret;
    }

//
//  Change Events
//

    /**
     * Adds a listener for the <code>TreeModelEvent</code>
     * posted after the tree changes.
     *
     * @param l the listener to add
     * @see #removeTreeModelListener
     */
    public void addTreeModelListener(final TreeModelListener l) {
        if (!listeners.contains(l)) {
            listeners.add(l);
        }
    }

    /**
     * Removes a listener previously added with
     * <code>addTreeModelListener</code>.
     *
     * @param l the listener to remove
     * @see #addTreeModelListener
     */
    public void removeTreeModelListener(final TreeModelListener l) {
        listeners.remove(l);
    }
}
