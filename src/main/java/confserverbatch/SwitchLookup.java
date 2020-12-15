/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package confserverbatch;

import com.genesyslab.platform.applicationblocks.com.ConfigException;
import com.genesyslab.platform.applicationblocks.com.IConfService;
import com.genesyslab.platform.applicationblocks.com.objects.CfgSwitch;
import com.genesyslab.platform.applicationblocks.com.queries.CfgSwitchQuery;

/**
 *
 * @author stepan_sydoruk
 */
public class SwitchLookup {

    private CfgSwitch sw;

    public SwitchLookup(IConfService service, String switch1) throws ConfigException, InterruptedException {
//        CfgSwitchQuery switchQuery = new CfgSwitchQuery(service);
//        switchQuery.setName(switch1);
//        Collection<CfgSwitch> execute = switchQuery.execute();
        CfgSwitchQuery switchQuery = new CfgSwitchQuery(switch1);
        sw = service.retrieveObject(CfgSwitch.class, switchQuery);
        int i = 1;
    }

    public CfgSwitch getSw() {
        return sw;
    }

    public void setSw(CfgSwitch sw) {
        this.sw = sw;
    }

}
