/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.airbnb.confservutils;

import com.genesyslab.platform.commons.GEnum;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;

/**
 *
 * @author stepan_sydoruk
 */
public class CfgObjectTypeMenu implements Comparable<CfgObjectTypeMenu> {

    public static void setSelectedItem(JComboBox cb, GEnum item) {
        ComboBoxModel model = cb.getModel();
        int idx = -1;
        for (int i = 0; i < model.getSize(); i++) {
            Object elementAt = model.getElementAt(i);
            if (elementAt instanceof CfgObjectTypeMenu) {
//                LogManager.getLogger().info((((CfgObjectTypeMenu) elementAt).getType() + " - " + item));
                if (((CfgObjectTypeMenu) elementAt).getType() == item) {
                    idx = i;
                    break;
                }
            }

        }
        if (idx >= 0) {
            cb.setSelectedIndex(idx);
        }

    }

    private final GEnum type;

    public CfgObjectTypeMenu(GEnum type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CfgObjectTypeMenu) {
            return this.type == ((CfgObjectTypeMenu) obj).getType();
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        String s = type.toString();
        if (s.length() > 3 && s.substring(0, 3).equalsIgnoreCase("cfg")) {
            return s.substring(3);
        } else {
            return s;
        }
    }

    @Override
    public int compareTo(CfgObjectTypeMenu o) {

        return this.toString().compareTo(o.toString());
    }

    public GEnum getType() {
        return type;
    }

}
