load("/Users/stepan_sydoruk/src/ConfServUtils/common.js");

/**
 * Searches for DN and create copy on the same switches with the same properties of
 * DNs from the imported list
 */

console.log("-start-");
try {
  CS.connectToConfigServer();

  var srcDNs = findObjectByName("CfgDN", "6108132");
  //   var srcDNs = findObjectByName("CfgDN", "33184884000");

  if (srcDNs != null) {
    var switches = objectDBID_props("CfgSwitch");
    var csv = CS.readCSV("/Users/stepan_sydoruk/RPsToCreate.csv"); // returned is ArrayList of String Array
    for (let index = 0; index < csv.size(); index++) {
      var arr = csv.get(index);
      console.log("processing " + arr);
      var newName;
      if(typeof arr === 'string'){
        newName=arr.trim();
      }
      else {
        newName=arr[0].trim();
      }

      for (const dbid in srcDNs) {
        
        var iSwitchDBID = parseInt(CS.getAttribute(srcDNs[dbid], "switchDBID"));
        var theName =
          newName + "_" + CS.getAttribute(switches[iSwitchDBID], "name");
          // userProperties will be java KeyValueCollection. So just coping it to new DN
        var uProperties = CS.getAttribute(srcDNs[dbid], "userProperties");
        var createObj = {
          number: newName,
          name: theName,
          state: 1,
          useOverride: 2,
          routeType: CS.getAttribute(srcDNs[dbid], "routeType"),
          type: CS.getAttribute(srcDNs[dbid], "type"),
          switchDBID: iSwitchDBID,
        };
        var ret = CS.createObject(
          "CfgDN",
          JSON.stringify(createObj),
          uProperties
        );
        if (ret >= 0) {
          console.log("created DN DBID: " + ret);
        } else {
          console.log("NOT created DN DBID: " + ret);
        }
      }
    }
  } else {
    console.log("Not found source DN!!!");
  }

  console.log("--done--");
} catch (error) {
  console.error("error: " + error);
}
