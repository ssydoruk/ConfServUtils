load("/Users/stepan_sydoruk/src/ConfServUtils/common.js");

/**
 * Prints app,dbid
 */
console.log("-start-");
try {
  CS.connectToConfigServer();

  var objs = objectDBID_props("CfgApplication");
  var hosts = objectDBID_props("CfgHost");

    for (const dbid in objs) {
        var obj=JSON.parse(CS.objToJson(objs[dbid]));
        var hostDBID=getAppHostDBID(obj);
        if( hostDBID!=null){
            console.log(obj['attributes']['name']+','+ CS.getAttribute(hosts[hostDBID], 'name'));
        }

    }
} catch (err) {
  console.error("error: " + err);
}
console.log('done');