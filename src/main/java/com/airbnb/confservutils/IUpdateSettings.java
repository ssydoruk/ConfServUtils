/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.airbnb.confservutils;

/**
 *
 * @author stepan_sydoruk
 */
public interface IUpdateSettings {

    public String getReplaceKey(String stringKey);

    public enum UpdateAction {
        ADD_SECTION, REMOVE, REPLACE_WITH, RESTORE_FROM_BACKUP,
        RENAME_SECTION
    };

    public String addSection();

    public String addKey();

    public String addValue();

    public boolean isMakeBackup();

    public UpdateAction getUpdateAction();

    public String replaceWith(String currentValue);

};
