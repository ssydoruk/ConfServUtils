/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ssydoruk.confservutils;

import Utils.swing.*;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import javax.swing.*;

/**
 * @author stepan_sydoruk
 */
class StoredSettings {

    ArrayList<ConfServer> configServers = new ArrayList<>();
    ArrayList<String> lastFiles = new ArrayList<>();

    ArrayList<String> users = new ArrayList<>();
    String password;

    CSVToHTMLSettings csvToHTMLSettings = new CSVToHTMLSettings();

    public void setLastUsedConfigServer(int lastUsedConfigServer) {
        if (lastUsedConfigServer >= 0 && lastUsedConfigServer < configServers.size()) {
            ArrayList<ConfServer> configServers1 = new ArrayList<>();
            configServers1.add(configServers.get(lastUsedConfigServer));
            for (int i = 0; i < configServers.size(); i++) {
                if (i != lastUsedConfigServer) {
                    configServers1.add(configServers.get(i));
                }

            }
            configServers = configServers1;
        }
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    void loadConfServs(ArrayList<EditableValue[]> data) {
        configServers.clear();
        for (EditableValue[] objects : data) {
            configServers.add(new ConfServer(objects[0].getValue(), objects[1].getValue(), objects[2].getValue(), objects[3].getValue()));
        }
    }

    void updateUsers(DefaultComboBoxModel model) {
        users.clear();
        for (int i = 0; i < model.getSize(); i++) {
            String s = (String) model.getElementAt(i);
            if (s != null && !s.trim().isEmpty()) {
                users.add(s);
            }
        }

    }

    public ArrayList<ConfServer> getConfigServers() {
        return configServers;
    }

    public void addLastFile(String fileName) {
        int indexOf = lastFiles.indexOf(fileName);
        if (indexOf > 0) {
            lastFiles.remove(indexOf);
        }
        if (indexOf != 0) {
            lastFiles.add(0, fileName);
        }
    }

    public ArrayList<String> getLastFiles() {
        return lastFiles;
    }

    public ArrayList<String> getUsers() {
        return users;
    }

    public List<String> getCsvFile() {
        return csvToHTMLSettings.getCsvFile();
    }

    public void setCsvFile(List<String> csvFile) {
        csvToHTMLSettings.setCsvFile(csvFile);
    }

    public List<String> getOutputFile() {
        return csvToHTMLSettings.getOutputFile();
    }

    public void setOutputFile(List<String> outputFile) {
        csvToHTMLSettings.setOutputFile(outputFile);
    }

    public List<String> getCcsFile() {
        return csvToHTMLSettings.getCcsFile();
    }

    public void setCcsFile(List<String> ccsFile) {
        csvToHTMLSettings.setCcsFile(ccsFile);
    }

    public List<String> getJsFile() {
        return csvToHTMLSettings.getJsFile();
    }

    public void setJsFile(List<String> jsFile) {
        csvToHTMLSettings.setJsFile(jsFile);
    }

    public int getAction() {
        return csvToHTMLSettings.getAction();
    }

    public void setAction(int action) {
        csvToHTMLSettings.setAction(action);
    }

    public int getcssEmbed() {
        return csvToHTMLSettings.getCssEmbed();
    }

    public void setcssEmbed(int action) {
        csvToHTMLSettings.setCssEmbed(action);
    }

    public static class CSVToHTMLSettings {

        private ArrayList<String> csvFile = new ArrayList<>();
        private ArrayList<String> outputFile = new ArrayList<>();
        private ArrayList<String> ccsFile = new ArrayList<>();
        ArrayList<String> jsFile = new ArrayList<>();
        private int action;
        private int cssEmbed;
        static final int MAX_COMBO_ITEMS = 5;

        public int getCssEmbed() {
            return cssEmbed;
        }

        public void setCssEmbed(int cssEmbed) {
            this.cssEmbed = cssEmbed;
        }

        public int getAction() {
            return action;
        }

        public void setAction(int action) {
            this.action = action;
        }

        public List<String> getCsvFile() {
            return nonNull(csvFile);
        }

        public void setCsvFile(List<String> list) {
            if (csvFile == null) {
                csvFile = new ArrayList<>();
            }
            storeListItems(csvFile, list);
        }

        public List<String> getOutputFile() {
            return nonNull(outputFile);
        }

        public void setOutputFile(List<String> list) {
            if (outputFile == null) {
                outputFile = new ArrayList<>();
            }
            storeListItems(outputFile, list);
        }

        public List<String> getCcsFile() {
            return nonNull(ccsFile);
        }

        private List<String> nonNull(ArrayList<String> list) {
            ArrayList<String> ret = new ArrayList<String>();
            for (String f :list
                 ) {
                if(StringUtils.isNotEmpty(f))
                    ret.add(f);
            }
            return ret;
        }

        public void setCcsFile(List<String> list) {
            if (ccsFile == null) {
                ccsFile = new ArrayList<>();
            }
            storeListItems(ccsFile, list);
        }

        public List<String> getJsFile() {
            return nonNull(jsFile);
        }

        public void setJsFile(List<String> newFiles) {
            if (jsFile == null) {
                jsFile = new ArrayList<>();
            }
            storeListItems(jsFile, newFiles);
        }

        private void storeListItems(ArrayList<String> jsFile, List<String> newFiles) {
            jsFile.clear();
            for (int i = 0; i < newFiles.size() && i < MAX_COMBO_ITEMS; i++) {
                String newFile = newFiles.get(i);
                if (StringUtils.isNotBlank(newFile))
                    jsFile.add(newFile);
            }
        }

    }

    public static class ConfServer {

        String profile;
        String host;
        String port;
        String app;

        public ConfServer(Object profile, Object host, Object port, Object app) {
            this.profile = profile.toString();
            this.host = host.toString();
            this.port = port.toString();
            this.app = app.toString();
        }

        @Override
        public String toString() {
            return profile + " ( " + host + ":" + port + ')';
        }

        public String getProfile() {
            return profile;
        }

        public String getHost() {
            return host;
        }

        public String getPort() {
            return port;
        }

        public int getPortInt() {
            return Integer.parseInt(port);
        }

        public String getApp() {
            return app;
        }

    }

}
