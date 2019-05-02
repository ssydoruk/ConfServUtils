/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.airbnb.confservutils;

import java.util.ArrayList;
import javax.swing.DefaultComboBoxModel;

/**
 *
 * @author stepan_sydoruk
 */
class StoredSettings {

    ArrayList<ConfServer> configServers = new ArrayList<>();
    ArrayList<String> users = new ArrayList<>();

    void loadConfServs(ArrayList<Object[]> data) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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

        String profile;
        String host;
        String port;
        String app;
    }

}
