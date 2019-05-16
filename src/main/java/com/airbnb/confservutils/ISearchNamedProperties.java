/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.airbnb.confservutils;

import com.genesyslab.platform.applicationblocks.com.CfgObject;

/**
 *
 * @author stepan_sydoruk
 */
public interface ISearchNamedProperties {

    String[] getNamedProperties(CfgObject obj);

    String getName(CfgObject obj);

    String getShortPrint(CfgObject obj);
}
