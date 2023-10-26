/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ssydoruk.confservutils;

import com.genesyslab.platform.applicationblocks.com.CfgObject;
import com.genesyslab.platform.commons.collections.KeyValueCollection;
import java.util.Collection;

/**
 *
 * @author stepan_sydoruk
 */
public interface IKeyValueProperties {

	KeyValueCollection getProperties(CfgObject obj);

	Collection<String> getName(CfgObject obj);
}
