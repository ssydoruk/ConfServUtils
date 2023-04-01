/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ssydoruk.confservutils;

/**
 *
 * @author stepan_sydoruk
 */
public enum HTMLstage {
    UNKNOWN("Unknown"),
    HEAD("head"),
    BODY_BEFORE_TABLE("body_before_table"),
    BODY_AFTER_TABLE("body_after_table"),
    ROW("row");

    private final String name;

    private HTMLstage(String s) {
        name = s;
    }

    public boolean equalsName(String otherName) {
        return (otherName == null) ? false : name.equals(otherName);
    }

    @Override
    public String toString() {
        return this.name;
    }

}
