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
public interface ISearchSettings {

    public boolean isCaseSensitive();

    public boolean isRegex();

    public boolean isFullOutputSelected();

    public boolean isSearchAll();

    public String getAllSearch();

    public String getSection();

    public String getObjName();

    public String getOption();

    public String getValue();
    
    public void setCaseSensitive(boolean setBool);
    
    public void setRegex(boolean setBool);
    
    public void setObjName(String objName);

}
