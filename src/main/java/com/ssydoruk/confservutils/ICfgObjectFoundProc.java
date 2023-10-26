/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ssydoruk.confservutils;

import com.genesyslab.platform.applicationblocks.com.CfgObject;
import com.genesyslab.platform.commons.collections.KeyValueCollection;

/**
 *
 * @author stepan_sydoruk
 */
@FunctionalInterface
public interface ICfgObjectFoundProc {

	boolean proc(CfgObject obj, KeyValueCollection kv, int current, int total);
}
