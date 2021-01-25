load("/Users/stepan_sydoruk/src/ConfServUtils/common.js");

try {
  CS.connectToConfigServer();
  var objs = getFolderTypes("CfgDN");
  var agentSwitches = getAgentSwitches();
  for (const dbid in objs) {
    if (agentSwitches.hasOwnProperty(objs[dbid].getOwnerID().getDBID())) {
      console.log(
        CS.getAttribute(objs[dbid], "name") +
          " DBID: " +
          CS.getAttribute(objs[dbid], "DBID") +
          " path: " +
          CS.getObjectPath(objs[dbid])
      );
    }
  }
} catch (error) {
  console.error("error: " + error);
}
console.log("--done--");
