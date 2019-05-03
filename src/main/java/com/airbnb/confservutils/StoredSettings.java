/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.airbnb.confservutils;

import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.DefaultComboBoxModel;

/**
 *
 * @author stepan_sydoruk
 */
class StoredSettings {

    ArrayList<ConfServer> configServers = new ArrayList<>();

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
    ArrayList<String> users = new ArrayList<>();
    String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    void loadConfServs(ArrayList<Object[]> data) {
        configServers.clear();
        for (Iterator<Object[]> it = data.iterator(); it.hasNext();) {
            Object[] objects = (Object[]) it.next();
            configServers.add(new ConfServer(objects[0], objects[1], objects[2], objects[3]));
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

    public ArrayList<String> getUsers() {
        return users;
    }

    public static class ConfServer {

        public ConfServer(Object profile, Object host, Object port, Object app) {
            this.profile = profile.toString();
            this.host = host.toString();
            this.port = port.toString();
            this.app = app.toString();
        }

        @Override
        public String toString() {
            return profile + " ( " + host + ":"+port+')';
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

        String profile;
        String host;
        String port;
        String app;
    }

}
