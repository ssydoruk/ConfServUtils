/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.airbnb.confservutils;

import Utils.ScreenInfo;
import com.jidesoft.dialog.BannerPanel;
import com.jidesoft.dialog.ButtonPanel;
import com.jidesoft.dialog.StandardDialog;
import confserverbatch.ObjectExistAction;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;

/**
 *
 * @author stepan_sydoruk
 */
public class ExistingObjectDecider {

    private ObjectExistAction currentAction;
    private ConfirmAction actionDialog = null;
    private JFrame parent;
    private ActionChoice selectedAction;

    private static ExistingObjectDecider INSTANCE = null;

    public static ExistingObjectDecider getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ExistingObjectDecider();
        }
        return INSTANCE;
    }

    public void init(ObjectExistAction act, AppForm theForm) {
        currentAction = act;
        parent = theForm;
        selectedAction = ActionChoice.APPLY;
//        if(actionDialog!=null){
//            actionDialog.
//        }
    }

    public ObjectExistAction getCurrentAction(String existingDesc, String dependentDesc) {
        if (currentAction == ObjectExistAction.UNKNOWN || selectedAction == ActionChoice.APPLY) {
            if (actionDialog == null) {
                actionDialog = new ConfirmAction(parent);
            }
            actionDialog.showModal(existingDesc, dependentDesc);
            selectedAction = actionDialog.getButtonChosen();
            switch (selectedAction) {
                case FAIL:
                    currentAction = ObjectExistAction.FAIL;
                    break;

                case APPLY:
                case APPLY_TO_ALL:
                    currentAction = actionDialog.getSelectedItem();
                    break;

            }
        }
        return currentAction;
    }

    public static class ConfirmAction extends StandardDialog {

        private Container mainPanel;
//        private final int buttonOptions;
        private JComponent bannerPannel = null;
        private JTextArea existingText;
        private JTextArea dependentText;
        JComboBox theType;

        private ActionChoice buttonChosen;
        private final ConfirmAction theDialog;

        public ActionChoice getButtonChosen() {
            return buttonChosen;
        }

        public ObjectExistAction getSelectedItem() {
            return (ObjectExistAction) theType.getSelectedItem();
        }

        private ConfirmAction(JFrame parent) {
            super(parent, "Existing object found", ModalityType.APPLICATION_MODAL);
            theDialog = this;
            existingText = new JTextArea();
            dependentText = new JTextArea();
            mainPanel = new JPanel();
            mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.LINE_AXIS));
            theType = new JComboBox<>(new ObjectExistAction[]{ObjectExistAction.RECREATE, ObjectExistAction.REUSE});
            mainPanel.add(new JLabel("Existing object action"));
            mainPanel.add(theType);

        }

        public void setMainPanel(Container mainPanel) {
            this.mainPanel = mainPanel;
        }

        @Override
        public JComponent createBannerPanel() {
            if (bannerPannel != null) {
                bannerPannel = new BannerPanel("pannel tytle", "descrr");
                return bannerPannel;
            }
            return bannerPannel;
        }

        @Override
        public JComponent createContentPanel() {
            JPanel panel = new JPanel();
            panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
            panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

            JPanel p = new JPanel(new BorderLayout());
            p.setBorder(new TitledBorder("Existing object"));
            p.add(new JScrollPane(existingText));
            panel.add(p);

            p = new JPanel(new BorderLayout());
            p.setBorder(new TitledBorder("Used in objects"));
            p.add(new JScrollPane(dependentText));
            panel.add(p);
            panel.add(mainPanel);
//            panel.add(mainPanel, BorderLayout.CENTER);
            return panel;
        }

        @Override
        public ButtonPanel createButtonPanel() {
            ButtonPanel buttonPanel = new ButtonPanel();

            JButton yesButton = new JButton();
            buttonPanel.addButton(yesButton);
            yesButton.setAction(new AbstractAction("Apply") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    buttonChosen = ActionChoice.APPLY;
                    setVisible(false);
                    dispose();
                }
            });
            JButton noButton = new JButton();
            buttonPanel.addButton(noButton);
            noButton.setAction(new AbstractAction("Apply to all") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (JOptionPane.showConfirmDialog(theDialog,
                            "Are you sure?", "Please confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                        buttonChosen = ActionChoice.APPLY_TO_ALL;
                        setVisible(false);
                        dispose();
                    }
                }
            });
            JButton cancelButton = new JButton();
            buttonPanel.addButton(cancelButton);
            cancelButton.setAction(new AbstractAction("Stop") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    buttonChosen = ActionChoice.FAIL;
                    setVisible(false);
                    dispose();
                }
            });
            setDefaultCancelAction(cancelButton.getAction());
            getRootPane().setDefaultButton(noButton);

//            buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            buttonPanel.setSizeConstraint(ButtonPanel.NO_LESS_THAN); // since the checkbox is quite wide, we don't want all of them have the same size.
            return buttonPanel;
        }

        public void showModal() {
            pack();
//        ScreenInfo.CenterWindow(allFiles);
            setModal(true);
            setSize(850, 700);
            ScreenInfo.setVisible(getParent(), this, true);
        }

        public void showModal(String existingDesc, String dependentDesc) {
            pack();
            existingText.setText(existingDesc);
            dependentText.setText(dependentDesc);
            showModal();
        }
    }

    enum ActionChoice {
        FAIL,
        APPLY,
        APPLY_TO_ALL;

    }

    public static void main(String[] args) {
        JFrame f = new JFrame("test1");
        ConfirmAction ca = new ConfirmAction(f);

        ca.showModal("Object \nproperties", "Dependent properties");
    }

}
