// const req = require('aa');
// import 'aa';

console.log("-start-");
try {
  CS.connectToConfigServer();

  // testFunctions();
  // testLoginIDs();
  renameObjects();

  console.log("--done--");
} catch (error) {
  console.error("error: " + error);
}

/**
 *
 * @param {String} type - type of the folder
 */
function getFolderTypes(type) {
  var ret = Object();

  var objTypeOrd = CS.enumToNum("CfgObjectType", type);
  var objs = objectDBID_props("CfgFolder");
  for (const dbid in objs) {
    if (CS.getAttribute(objs[dbid], "type") == objTypeOrd) {
      ret[dbid] = objs[dbid];
    }
  }

  return ret;
}

/**
 * retrieves all objects from ConfigServer
 *
 * Objects returned is Object { dbid: {params...}, dbid1: {params,}...}
 *
 * @param {*} objType CfgPerson, etc
 * @param {*} refresh - if true, means reread data from ConfigServer
 */
function objectDBID_props(objType, refresh = false) {
  var allObjects = CS.findObjects(objType, refresh);
  var ret = Object();
  if (allObjects != null) {
    for (let index = 0; index < allObjects.size(); index++) {
      var obj = allObjects.get(index);
      ret[parseInt(CS.getAttribute(obj, "DBID").toString())] = obj;
    }
    return ret;
  } else {
    return null;
  }
}

/**
 *
 * @param {String} objType - type of the object
 * @param {*} compareProc - function that takes CfgObject as paremeter. Should return:
 *  0 - ignore object
 *  1 - match found; stop search
 *  2 - match found; continue search
 * @param {*} refresh
 */
function findObject(objType, compareProc, refresh = false) {
  var objs = objectDBID_props(objType, refresh);
  if (objs != null) {
    // console.log("read logins: " + Object.keys(objs).length);
    var ret = Object();
    var shouldStop = false;
    for (const dbid in objs) {
      switch (compareProc(objs[dbid])) {
        case 0:
          break;

        case 1:
          ret[dbid] = objs[dbid];
          shouldStop = true;
          break;

        case 2:
          ret[dbid] = objs[dbid];
      }
      if (shouldStop) break;
    }
    return ret;
  }
  return null;
}

function findObjectByName(objType, objName, refresh = false) {
  var nameName = getNameName(objType);
  var fun = function (obj) {
    if (CS.getAttribute(obj, nameName) == objName) {
      return 2;
    } else return 0;
  };
  return findObject(objType, fun, refresh);
}

function getNameName(objType) {
  switch (objType) {
    case "CfgAgentLogin":
      return "loginCode";

    case "CfgPerson":
      return "userName";

    default:
      return "name";
  }
}

function testFunctions() {
  console.log(CS.enumToString("CfgSwitchType", 72));

  // var attrSwitch = JSON.parse(CS.getObjectAttributes("CfgSwitch"));
  // console.log("switch attributes: " + JSON.stringify(attrSwitch));

  var attrFolder = JSON.parse(CS.getObjectAttributes("CfgFolder"));
  console.log("folder attributes: " + JSON.stringify(attrFolder));

  var objs = objectDBID_props("CfgSwitch");
  var agentSwitches = Object();
  for (const dbid in objs) {
    if (CS.getAttribute(objs[dbid], "name").includes("sipa1")) {
      agentSwitches[dbid] = objs[dbid];
      console.log(
        CS.getAttribute(objs[dbid], "name") +
          " DBID: " +
          CS.getAttribute(objs[dbid], "DBID")
      );
    }
  }

  objs = getFolderTypes("CfgDN");
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

  objs = getFolderTypes("CFGAgentLogin");
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

  var agLoginAttr = JSON.parse(CS.getObjectAttributes("CfgAgentLogin"));
  console.log("folder attributes: " + JSON.stringify(agLoginAttr));
}

function testLoginIDs() {
  const NEWNAME = "aaaaaa";
  {
    var dbid = findObjectByName("CfgAgentLogin", NEWNAME);
    // if (dbid != null)
    //   console.log("deleted login: " + CS.deleteObject("CfgAgentLogin", dbid));
  }

  // var createObj = {
  //   loginCode: NEWNAME,
  //   state: 1,
  //   useOverride: 2,
  //   switchDBID: 103,
  //   switchSpecificType: 1,
  //   // userName: "stepan.sydoruk@ext.airbnb.com",
  // };

  // console.log(
  //   "created new loginID " +
  //     NEWNAME +
  //     ": " +
  //     CS.createObject("CfgAgentLogin", JSON.stringify(createObj))
  // );

  // console.log(
  //   "new object DBID: " + findObjectByName("CfgAgentLogin", NEWNAME, true)
  // );
}

function renameObjects() {
  console.log("renaming");
  // load('/Users/stepan_sydoruk/src/ConfServUtils/jsTest.js');
  // var attrFolder = JSON.parse(CS.getObjectAttributes("CfgPerson"));
  // console.log("folder attributes: " + JSON.stringify(attrFolder));
  var csv = CS.readCSV("/Users/stepan_sydoruk/aaa.csv"); // returned is ArrayList of String Array
  for (let index = 0; index < csv.size(); index++) {
    var arr = csv.get(index);
    console.log("processing " + arr);

    var personsFound = findObjectByName("CfgPerson", arr[0]);
    if (personsFound != null) {
      for (const dbid in personsFound) {
        console.log(
          JSON.stringify(JSON.parse(CS.objToJson(personsFound[dbid])))
        );

        var createObj = {
          userName: arr[1],
          emailAddress: arr[1],
        };
        console.log(
          "result of update of DBID:" +
            dbid +
            ": " +
            CS.updateObject("CfgPerson", dbid, JSON.stringify(createObj))
        );
      }
    } else {
      console.log("not found person " + arr[0]);
    }

    var placeFound = findObjectByName("CfgPlace", arr[0]);
    if (placeFound != null) {
      for (const dbid in placeFound) {
        console.log(JSON.stringify(JSON.parse(CS.objToJson(placeFound[dbid]))));
        var createObj = {
          name: arr[1],
        };
        console.log(
          "result of update of DBID:" +
            dbid +
            ": " +
            CS.updateObject("CfgPlace", dbid, JSON.stringify(createObj))
        );
      }
    } else {
      console.log("not found place " + arr[0]);
    }

    console.log("done " + arr);
  }
  console.log("done rename");
}
