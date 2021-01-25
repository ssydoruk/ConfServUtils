/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.airbnb.confservutils;

import Utils.*;
import java.awt.*;
import javax.swing.*;

/**
 *
 * @author stepan_sydoruk
 */
public class DebugInfoDialog extends InfoPanel {

    JTextArea annText;

    public DebugInfoDialog(Window parent) {
        super(parent, JOptionPane.DEFAULT_OPTION);
        annText = new JTextArea();
        annText.setEditable(false);
        JScrollPane jp = new JScrollPane(annText);
        super.setMainPanel(jp);
    }

    public void showAnn( String title, String txt) {
        annText.setText(txt);
        setTitle(title);
        showModal();
    }

}
