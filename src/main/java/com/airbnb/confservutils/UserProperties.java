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
class UserProperties {

    @Override
    public String toString() {
        return kvpToString(section, key, value);
    }

    private final String key;
    private final String section;
    private final String value;

 
    public UserProperties(String _section, String _key, String _value) {
        section = _section;
        key = _key;
        value = _value;

    }

    public String getKey() {
        return key;
    }

    public String getSection() {
        return section;
    }

    public String getValue() {
        return value;
    }
    
    

    public static String kvpToString(String _section, String _key, String _value) {
        return "[" + _section + "]/\"" + _key + "\"=\'" + _value + "\'";

    }

}
