load("C:\\Users\\STISY7\\IdeaProjects\\ConfServUtils\\common.js");


/**
 * Wi
 */
try {
    CS.connectToConfigServer();


    var allDNs = objectDBID_props("CfgDN");
    var allPlaces = objectDBID_props("CfgPlace");
    var arrPlaces = new Array();
    for (const plDBID in objectDBID_props("CfgPlace")) {
        var o = JSON.parse(CS.objToJson(allPlaces[plDBID]));
        arrPlaces.push(o);
        // console.log(JSON.stringify(o));
    }
    const extentionType = findEnum("CfgDNType", "Extension"); // integer value for DN Type Extension
    const acdType = findEnum("CfgDNType", "ACDPosition"); // integer value for DN Type Extension
    const vtoPortType = findEnum("CfgDNType", "CFGEAPort");
    const objStateDisabled = findEnum("CfgObjectState", "CFGDisabled");
    if (extentionType == null || acdType == null || vtoPortType == null || objStateDisabled == null) {
        throw new Error("Not able to init run");
    }
    var total = 0;
    for (const dnDBID in allDNs) {
        var name = CS.getAttribute(allDNs[dnDBID], "number");

        var dnType = CS.getAttribute(allDNs[dnDBID], "type");
        if ((dnType == extentionType || dnType == acdType)) {
            var state = CS.getAttribute(allDNs[dnDBID], "state");
            if (!linkedDN(dnDBID, arrPlaces) 
            || parseInt(state) == objStateDisabled //removing disabled DNs
            ) {
                total++;
                console.log(
                    name +
                    " path: " +
                    CS.getObjectPath(allDNs[dnDBID]) +
                    " DBID: " +
                    CS.getAttribute(allDNs[dnDBID], "DBID") +
                    " type: " +
                    CS.enumToString("CfgDNType", CS.getAttribute(allDNs[dnDBID], "type")
                    )
                );
                CS.deleteObject("CfgDN", parseInt(dnDBID));
            }
        } else if (dnType == vtoPortType) {
            var state = CS.getAttribute(allDNs[dnDBID], "state");
            if (parseInt(state) == objStateDisabled && !linkedDN(dnDBID, arrPlaces)) {
                console.log('Disabled vto port' + CS.objToJson(allDNs[dnDBID]));
                total++;
                console.log(
                    name +
                    " path: " +
                    CS.getObjectPath(allDNs[dnDBID]) +
                    " DBID: " +
                    CS.getAttribute(allDNs[dnDBID], "DBID") +
                    " type: " +
                    CS.enumToString("CfgDNType", CS.getAttribute(allDNs[dnDBID], "type"))
                );
                // CS.deleteObject("CfgDN", parseInt(dnDBID));
            }
        }
    }
} catch (error) {
    console.error("error: " + error);
}
console.log('DNs found: ' + total);
console.log("--done--");