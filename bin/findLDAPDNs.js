load("/Users/stepan_sydoruk/src/ConfServUtils/common.js");

/**
 * Wi
 */
try {
  CS.connectToConfigServer();

  var switchDBID = null;
  for (const dbid in findObject("CfgSwitch", function (obj) {
    if (CS.getAttribute(obj, "name").match(/esv1_sipa1/)) {
      return 2;
    } else return 0;
  })) {
    switchDBID = dbid;
    break;
  }

  var objs = objectDBID_props("CfgDN");
  let re = new RegExp("^[a-z]{2,}");
  const dnExtension = findEnum("CfgDNType", "Extension"); // integer value for DN Type Extension
  if (switchDBID == null || dnExtension == null) {
    throw new Error("Not able to init run");
  }
  for (const dbid in objs) {
    var name = CS.getAttribute(objs[dbid], "number");

    if (
      CS.getAttribute(objs[dbid], "switchDBID") == switchDBID &&
      CS.getAttribute(objs[dbid], "type") == dnExtension &&
      re.test(name)
    ) {
      console.log(
        name +
          " path: " +
          CS.getObjectPath(objs[dbid]) +
          " DBID: " +
          CS.getAttribute(objs[dbid], "DBID") +
          " type: " +
          CS.enumToString("CfgDNType", CS.getAttribute(objs[dbid], "type"))
      );
    }
  }
} catch (error) {
  console.error("error: " + error);
}
console.log("--done--");
