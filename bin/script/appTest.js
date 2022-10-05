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
      if (hostObj['attributes']['OSinfo'].OStype != null &&
        !hostObj['attributes']['OSinfo'].OStype.toLowerCase().includes('windows')) {
        console.log(CS.getAttribute(hosts[hostDBID], 'name') + ',' + app['attributes']['name']);
      }
    }

  }
} catch (err) {
  console.error("error: " + err);
}
console.log('done');
