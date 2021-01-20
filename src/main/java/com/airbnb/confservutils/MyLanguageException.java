/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.airbnb.confservutils;

import com.oracle.truffle.api.exception.AbstractTruffleException;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.library.ExportLibrary;

/**
 *
 * @author stepan_sydoruk
 */
@ExportLibrary(InteropLibrary.class)
 final class MyLanguageException extends AbstractTruffleException {
     MyLanguageException(String message) {
         super(message);
     }
 }
