load("C:\\Users\\STISY7\\IdeaProjects\\ConfServUtils\\common.js");

/**
 * Prints app,dbid
 */
console.log("-start-");
try {
  CS.connectToConfigServer();

  var apps = objectDBID_props("CfgApplication");
  var hosts = objectDBID_props("CfgHost");

  for (const dbid in apps) {
    var app = JSON.parse(CS.objToJson(apps[dbid]));
    var hostDBID = getAppHostDBID(app);
    if (hostDBID != null) {
      var hostObj = JSON.parse(CS.objToJson(hosts[hostDBID]));
    //   console.log(JSON.stringify(hostObj));
      if (hostObj['attributes']['OSinfo'].OStype != null
      && hostObj['attributes'].state != 2  // not disabled
      && !hostObj['attributes']['OSinfo'].OStype.toLowerCase().includes('windows') // not Windows host

        ) {
            if(app['attributes']['name'].includes('ResourceManager')){
                console.log(  app['attributes']['name']+ ','+hostObj['attributes']['name']+ ','+hostObj['attributes']['IPaddress']+ ','+hostObj['attributes']['OSinfo'].OStype);
            }
      }
    }

  }
} catch (err) {
  console.error("error: " + err);
}
console.log('done');
