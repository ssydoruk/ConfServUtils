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

// Copyright (c) 2006 - 2017 Genesys Telecommunications Laboratories, Inc. All rights reserved.
//===============================================================================
package confserverbatch.gui;

import com.genesyslab.platform.applicationblocks.com.ConfigServerException;
import java.awt.*;
import java.awt.event.*;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;


class MessageDialog extends JDialog {

    public MessageDialog(
            final Frame owner,
            final String message,
            final Exception exception) {
        super(owner, "COM Quick Start", true);

        Exception exception2show = exception;

        JPanel contentPane = new JPanel();
        JTextArea jtxtMessage = new JTextArea();
        JTextArea jtxtStacktrace = null;
        JPanel buttonsPanel = new JPanel();
        JButton jbtnOk = new JButton("Ok");

        jtxtMessage.setRows(3);
        jtxtMessage.setEditable(false);
        jtxtMessage.setBackground(contentPane.getBackground());

        String messageExt = message;
        if (exception instanceof ConfigServerException) {
            ConfigServerException ex = (ConfigServerException) exception;
            messageExt += "\r\nError type: [" + ex.getErrorType()
                    + "]\r\nObject type: [" + ex.getObjectType()
                    + "]\r\nObject property: [" + ex.getObjectProperty()
                    + "]\r\n\r\nMessage: " + ex.getMessage();
            exception2show = null;
            jtxtMessage.setRows(7);
        } else if (exception != null) {
            messageExt += "\r\n\r\n" + exception.toString();
        }
        jtxtMessage.setText(messageExt);

        if (exception2show != null) {
            StringWriter sw = new StringWriter();
            exception2show.printStackTrace(new PrintWriter(sw));
            jtxtStacktrace = new JTextArea();
            jtxtStacktrace.setEditable(false);
            jtxtStacktrace.setRows(10);
            jtxtStacktrace.setText(sw.toString());
            jtxtStacktrace.setBackground(contentPane.getBackground());
        }

        setContentPane(contentPane);
        contentPane.setBorder(new EmptyBorder(10, 15, 10, 15));
        contentPane.setLayout(new BorderLayout(5, 3));
        contentPane.add(jtxtMessage, BorderLayout.NORTH);
        if (jtxtStacktrace != null) {
            JPanel excPanel = new JPanel();
            excPanel.setLayout(new BorderLayout(5, 3));
            excPanel.add(jtxtStacktrace, BorderLayout.CENTER);
            excPanel.setBorder(new TitledBorder("Exception details"));
            contentPane.add(excPanel, BorderLayout.CENTER);
        }
        contentPane.add(buttonsPanel, BorderLayout.SOUTH);
        buttonsPanel.add(jbtnOk);
        getRootPane().setDefaultButton(jbtnOk);

        jbtnOk.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onExit();
            }
        });

        // call onExit() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onExit();
            }
        });

        // call onExit() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onExit();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onExit() {
        dispose();
    }
}
