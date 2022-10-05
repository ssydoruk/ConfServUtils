load("C:\\Users\\STISY7\\IdeaProjects\\ConfServUtils\\common.js");

/**
 * Prints app,dbid
 */
console.log("-start-");
try {
    CS.connectToConfigServer();

    var objs = objectDBID_props("CfgApplication");
    var hosts = objectDBID_props("CfgHost");

    for (const dbid in objs) {
        var obj = JSON.parse(CS.objToJson(objs[dbid]));
        var hostDBID = getAppHostDBID(obj);
        if (hostDBID != null) {
            var hostObj = JSON.parse(CS.objToJson(hosts[hostDBID]));
            //console.log(JSON.stringify(obj)+'\n'+JSON.stringify(hostObj)+'--->');
            // next conditions checks for non-windows host
            if (hostObj['attributes']['OSinfo'].OStype != null &&
            hostObj['attributes'].state != 2  // not disabled
            && !hostObj['attributes']['OSinfo'].OStype.toLowerCase().includes('windows') // not Windows host
            ) {
                console.log(hostObj['attributes']['name'] + ',' + obj['attributes']['name']);
            }
        }

    }
} catch (err) {
    console.error("error: " + err);
}
console.log('done');
