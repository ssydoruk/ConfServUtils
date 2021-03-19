/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ssydoruk.confservutils;

import com.genesyslab.platform.applicationblocks.com.ConfigException;

/**
 *
 * @author stepan_sydoruk
 */
interface IThreadedFun {

    public void fun() throws ConfigException, InterruptedException;
}
