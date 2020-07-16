/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.airbnb.confservutils;

import java.util.Collection;

/**
 *
 * @author stepan_sydoruk
 */
public interface IUpdateSettings {

    public String getReplaceKey(String stringKey);

    public enum UpdateAction {
        ADD_SECTION,
        REMOVE,
        REPLACE_WITH,
        RESTORE_FROM_BACKUP,
        ADD_OPTION_FORCE,
        RENAME_SECTION
    };

    public Collection<UserProperties> getAddedKVP();

    public boolean isMakeBackup();

    public UpdateAction getUpdateAction();

    public String replaceWith(String currentValue);

};
