//===============================================================================
// Any authorized distribution of any copy of this code (including any related 
// documentation) must reproduce the following restrictions, disclaimer and copyright 
// notice:

// The Genesys name, trademarks and/or logo(s) of Genesys shall not be used to name 
// (even as a part of another name), endorse and/or promote products derived from 
// this code without prior written permission from Genesys Telecommunications 
// Laboratories, Inc. 

// The use, copy, and/or distribution of this code is subject to the terms of the Genesys 
// Developer License Agreement.  This code shall not be used, copied, and/or 
// distributed under any other license agreement.    

// THIS CODE IS PROVIDED BY GENESYS TELECOMMUNICATIONS LABORATORIES, INC. 
// ("GENESYS") "AS IS" WITHOUT ANY WARRANTY OF ANY KIND. GENESYS HEREBY 
// DISCLAIMS ALL EXPRESS, IMPLIED, OR STATUTORY CONDITIONS, REPRESENTATIONS AND 
// WARRANTIES WITH RESPECT TO THIS CODE (OR ANY PART THEREOF), INCLUDING, BUT 
// NOT LIMITED TO, IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A 
// PARTICULAR PURPOSE OR NON-INFRINGEMENT. GENESYS AND ITS SUPPLIERS SHALL 
// NOT BE LIABLE FOR ANY DAMAGE SUFFERED AS A RESULT OF USING THIS CODE. IN NO 
// EVENT SHALL GENESYS AND ITS SUPPLIERS BE LIABLE FOR ANY DIRECT, INDIRECT, 
// CONSEQUENTIAL, ECONOMIC, INCIDENTAL, OR SPECIAL DAMAGES (INCLUDING, BUT 
// NOT LIMITED TO, ANY LOST REVENUES OR PROFITS).

// Copyright (c) 2007 - 2017 Genesys Telecommunications Laboratories, Inc. All rights reserved.
//===============================================================================
package confserverbatch.gui;

import java.awt.Dimension;
import java.awt.Toolkit;


/**
 * Entry point for the sample application connecting to configuration server and doing some example operations
 * like read application object, subscribe for its' changes and modify it.
 */
public class Program {

    /**
     * The main entry point for the application.
     */
    public static void main(final String[] args) {
        FormQuickStart frm = new FormQuickStart();
        frm.pack();

        // Centralize application main window frame:
        Dimension screenSize =
                Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = frm.getSize();
        int x = (screenSize.width - frameSize.width) / 2;
        int y = (screenSize.height - frameSize.height) / 2;
        frm.setLocation(x, y);

        frm.setVisible(true);
    }
}
