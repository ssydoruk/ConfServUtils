/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ssydoruk.confservutils;

import Utils.swing.*;
import java.util.*;
import javax.swing.*;

/**
 *
 * @author stepan_sydoruk
 */
class StoredSettings {

    ArrayList<ConfServer> configServers = new ArrayList<>();
    ArrayList<String> lastFiles = new ArrayList<>();

    ArrayList<String> users = new ArrayList<>();
    String password;

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
