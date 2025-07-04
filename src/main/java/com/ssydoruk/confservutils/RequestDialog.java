/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ssydoruk.confservutils;

import static com.ssydoruk.confservutils.AppForm.searchValues;
import com.jidesoft.dialog.ButtonPanel;
import com.jidesoft.dialog.StandardDialog;
import static com.jidesoft.dialog.StandardDialog.RESULT_AFFIRMED;
import static com.jidesoft.dialog.StandardDialog.RESULT_CANCELLED;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

/**
 *
 * @author stepan_sydoruk
 */
final class RequestDialog extends StandardDialog {

	private final JPanel contentPanel;

	public RequestDialog(final Window parent, final JPanel contentPanel, final JMenuItem mi) {
		this(parent, contentPanel);
		setTitle(mi.getText() + " parameters");

	}

	public RequestDialog(final Window parent, final JPanel contentPanel) {
		super(parent);
		this.contentPanel = contentPanel;
		setTitle("Enter request parameters");
	}

	@Override
	public JPanel getContentPanel() {
		return contentPanel;
	}

	@Override
	public JComponent createBannerPanel() {
		return null;
	}

	@Override
	public JComponent createContentPanel() {
		final JPanel content = new JPanel();
		content.setLayout(new BoxLayout(content, BoxLayout.PAGE_AXIS));
		content.add(contentPanel);

		return content;
	}

	@Override
	public ButtonPanel createButtonPanel() {
		final ButtonPanel buttonPanel = new ButtonPanel();
		final JButton cancelButton = new JButton();
		buttonPanel.addButton(cancelButton);

		cancelButton.setAction(new AbstractAction() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				setDialogResult(RESULT_CANCELLED);
				setVisible(false);
				dispose();
			}
		});
		cancelButton.setText("Close");

		final JButton jbOK = new JButton("OK");
		buttonPanel.addButton(jbOK);

		// listPane.add(jbFilter);
		jbOK.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				setDialogResult(RESULT_AFFIRMED);
				dispose();
			}
		});

		final String act = "OK";

		setDefaultCancelAction(cancelButton.getAction());
		setDefaultAction(jbOK.getAction());
		getRootPane().setDefaultButton(jbOK);

		buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		buttonPanel.setSizeConstraint(ButtonPanel.NO_LESS_THAN); // since the checkbox is quite wide, we don't want
		// all of them have the same size.
		return buttonPanel;
	}

	public boolean doShow(final String title, final IConfigPanel onShow) {
		if (onShow != null) {
			onShow.showProc();
		}
		return doShow(title);
	}

	public boolean doShow(final String Title) {
		setTitle(Title);
		return doShow();
	}

	public boolean doShow() {

		// setModal(true);
		pack();
		if (contentPanel instanceof ISearchCommon) {
			((ISearchCommon) contentPanel).setChoices(searchValues);
		}

		// ScreenInfo.CenterWindow(this);
		setLocationRelativeTo(getParent());
		// setVisible(true);
		setAlwaysOnTop(true);
		java.awt.EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				toFront();

			}
		});
		// setVisible(false);
		setVisible(true);

		if (getDialogResult() == StandardDialog.RESULT_AFFIRMED) {
			if (contentPanel instanceof ISearchCommon) {
				final Collection<String> choices = ((ISearchCommon) contentPanel).getChoices();
				if (choices != null && !choices.isEmpty()) {
					for (final String choice : choices) {
						searchValues.add(choice);
					}
				}
			}
			return true;
		} else {
			return false;
		}

	}
}
