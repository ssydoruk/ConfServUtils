/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ssydoruk.confservutils;

import Utils.FileUtils;
import static com.ssydoruk.confservutils.AppForm.searchValues;
import com.jidesoft.dialog.ButtonPanel;
import com.jidesoft.dialog.StandardDialog;
import static com.jidesoft.dialog.StandardDialog.RESULT_CANCELLED;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.csv.QuoteMode;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.logging.Level;

/**
 *
 * @author stepan_sydoruk
 */
final class CSVConvertDialog extends StandardDialog {

    private final JPanel contentPanel;
    private CSVtoHTMLDialog csvToHTMLPanel;
    private AppForm mainForm;
    private static final Logger logger = LogManager.getLogger();

    public CSVConvertDialog(final AppForm parent, final JPanel contentPanel, final JMenuItem mi) {
        this(parent, contentPanel);
        setTitle(mi.getText() + " parameters");
        mainForm = parent;
    }

    public CSVConvertDialog(final Window parent, final JPanel contentPanel) {
        super(parent);
        this.contentPanel = contentPanel;
        this.setMinimumSize(new Dimension(parent.getWidth() - 200, getHeight()));
//            this.setResizable(false);

        csvToHTMLPanel = new CSVtoHTMLDialog(this);
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
        content.add(csvToHTMLPanel);

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

        final JButton jbRun = new JButton("Run");
        buttonPanel.addButton(jbRun);

        // listPane.add(jbFilter);
        jbRun.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                runConvert();
            }

        });

        final String act = "OK";

        setDefaultCancelAction(cancelButton.getAction());
        setDefaultAction(jbRun.getAction());
        getRootPane().setDefaultButton(jbRun);

        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        buttonPanel.setSizeConstraint(ButtonPanel.NO_LESS_THAN); // since the checkbox is quite wide, we don't want
        // all of them have the same size.
        return buttonPanel;
    }

    public void doShow(final String title, final IConfigPanel onShow) {
        if (onShow != null) {
            onShow.showProc();
        }
        doShow(title);
    }

    public void doShow(final String Title) {
        setTitle(Title);
        doShow();
    }

    public void doShow() {
        // setModal(true);
        csvToHTMLPanel.setTexttfCSSFile(mainForm.getDs().getCcsFile());
        csvToHTMLPanel.setTexttfCSVFile(mainForm.getDs().getCsvFile());
        csvToHTMLPanel.setTexttfOutputFile(mainForm.getDs().getOutputFile());
        csvToHTMLPanel.setTexttfJSScript(mainForm.getDs().getJsFile());
        csvToHTMLPanel.setActiveButton(mainForm.getDs().getAction());
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

    }

    private void saveConfig() {
        mainForm.getDs().setCcsFile(csvToHTMLPanel.getTexttfCSSFile());
        mainForm.getDs().setCsvFile(csvToHTMLPanel.getTexttfCSVFile());
        mainForm.getDs().setOutputFile(csvToHTMLPanel.getTexttfOutputFile());
        mainForm.getDs().setJsFile(csvToHTMLPanel.getTexttfJSScript());
        mainForm.getDs().setAction(csvToHTMLPanel.getActiveButton());
        mainForm.saveConfig();
    }

    private void runConvert() {
        saveConfig();
        File outputFile = new File(mainForm.getDs().getOutputFile());
        File jsFormatFile = new File(mainForm.getDs().getJsFile());
        String script = null;
        if (jsFormatFile.canRead()) {
            script = FileUtils.loadFile(jsFormatFile);
//            JSRunner.getInstance().setDebugPort("4889");
        }

        try (BufferedReader in = new BufferedReader(new FileReader(mainForm.getDs().getCsvFile()))) {
            CSVParser records = CSVFormat.RFC4180.builder()
                    .setDelimiter(",").setHeader().setSkipHeaderRecord(true).setQuoteMode(QuoteMode.MINIMAL).build().parse(in);
            Document html = createShell("aaa.html");
            html.title(FilenameUtils.getBaseName(outputFile.getName()));
            html.body().appendElement("h1").attr("id", "header").text("Welcome");
            File ccsFile = new File(mainForm.getDs().getCcsFile());
            if (ccsFile.canRead()) {
                html.body().appendElement("style").text(FileUtils.loadFile(ccsFile));
            }

            Element tab = html.body().appendElement("table").attr("id", "tab").attr("border", "1");

            Element colgroup = tab.appendElement("colgroup");
            java.util.List<String> headerNames = records.getHeaderNames();
            HashMap<String, String> row = new HashMap();
            for (int i = 0; i < headerNames.size(); i++) {
                colgroup.appendElement("col").attr("class", headerNames.get(i));
            }

            Element tabHead = tab.appendElement("thead");
            Element tabRow = tabHead.appendElement("tr");
            headerNames = records.getHeaderNames();
            int timeIdx = -1;
            row = new HashMap();
            for (int i = 0; i < headerNames.size(); i++) {
                if (headerNames.get(i).equals("_time")) {
                    timeIdx = i;
                }
                tabRow.appendElement("th").attr("class", headerNames.get(i)).text(headerNames.get(i));
            }

            Element tabBody = tab.appendElement("tbody");
            for (CSVRecord record : records.getRecords()) {
                for (int i = 0; i < record.size(); i++) {
                    row.put(headerNames.get(i), record.get(i));
                }

                if (script != null) {
                    JSRunner.runCSVFormatScript(script, row);
                }
                tabRow = tabBody.appendElement("tr");
                for (int i = 0; i < record.size(); i++) {
                    Element cell = tabRow.appendElement("td");
                    if (i != timeIdx) {
                        cell.attr("style", "word-break:break-all;");
                    }
//                    cell.html(row.get(headerNames.get(i)).replaceAll("\n", "<br>"));
                    cell.html(row.get(headerNames.get(i)));
                }
            }
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
                writer.write(html.outerHtml());
            } catch (IOException ex) {
                java.util.logging.Logger.getLogger(CSVToHTML.class.getName()).log(Level.SEVERE, null, ex);
            }

        } catch (FileNotFoundException ex) {
            java.util.logging.Logger.getLogger(CSVToHTML.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(CSVToHTML.class.getName()).log(Level.SEVERE, null, ex);
        }
        switch (mainForm.getDs().getAction()) {
            case 0: // default browser
            {
                Desktop desktop = Desktop.getDesktop();
                try {
                    desktop.browse(outputFile.toURI());
                } catch (IllegalArgumentException iae) {
                    System.out.println("File Not Found");
                } catch (IOException ex) {
                    java.util.logging.Logger.getLogger(CSVConvertDialog.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            break;

            case 1: // explorer
            {
                try {
                    Process proc = Runtime.getRuntime().exec("explorer.exe /select," + outputFile.getAbsolutePath().replaceAll("/", "\\\\"));
                    proc.waitFor();
                } catch (IOException | InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
            break;
            case 2:
                logger.info("Done nothing");
                break;

            default:
                throw new AssertionError();
        }
    }

    public static Document createShell(String baseUri) {
        Validate.notNull(baseUri);
        Document doc = new Document(baseUri);
        Element html = doc.appendElement("html");
        html.appendElement("head");
        html.appendElement("body");
        return doc;
    }
}
