/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package confserverbatch;

import com.genesyslab.platform.applicationblocks.com.ConfigException;
import com.genesyslab.platform.applicationblocks.com.IConfService;
import com.genesyslab.platform.applicationblocks.com.objects.CfgFolder;
import com.genesyslab.platform.applicationblocks.com.objects.CfgSwitch;
import com.genesyslab.platform.applicationblocks.com.queries.CfgSwitchQuery;

/**
 *
 * @author stepan_sydoruk
 */
public class SwitchObjectLocation {

    private CfgSwitch sw;
    private Integer folderDBID = null;

    public Integer getFolderDBID() {
        return folderDBID;
    }

    public SwitchObjectLocation(IConfService service, String switch1) throws ConfigException, InterruptedException {

        CfgSwitchQuery switchQuery = new CfgSwitchQuery(switch1);
        sw = service.retrieveObject(CfgSwitch.class, switchQuery);
    }

    public SwitchObjectLocation(IConfService service, CfgFolder folder) throws ConfigException {
        CfgSwitchQuery switchQuery = new CfgSwitchQuery(folder.getOwnerID().getDBID());
        sw = service.retrieveObject(CfgSwitch.class, switchQuery);
        folderDBID = folder.getDBID();
    }

    public CfgSwitch getSw() {
        return sw;
    }

    public void setSw(CfgSwitch sw) {
        this.sw = sw;
    }

}
