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

function hasOwnPropertyCaseInsensitive(obj, property) {
  var props = [];
  for (var i in obj) if (obj.hasOwnProperty(i)) props.push(i);
  var prop;
  while (prop = props.pop()) if (prop.toLowerCase() === property.toLowerCase()) return true;
  return false;
}


function getOwnPropertyCaseInsensitive(obj, property) {
  var props = [];
  for (var i in obj) if (obj.hasOwnProperty(i)) props.push(i);
  var prop;
  while (prop = props.pop()) if (prop.toLowerCase() === property.toLowerCase()) return obj[prop];
  return null;
}


/**
 * Checks if DN dnDBID is linked on any place
 * @param {int} dnDBID 
 * @param {array of objects/places} place 
 * @returns true if DN is linked to a place; false otherwise
 */
function linkedDN(dnDBID, place) {
  var dnInt = parseInt(dnDBID);
  for (let i = 0; i < place.length; i++) {
    var v = place[i];
    if (v != null && v.hasOwnProperty("attributes")) {
      var attrs = v["attributes"];
      if (
        attrs != undefined &&
        attrs.hasOwnProperty("DNDBIDs") &&
        attrs["DNDBIDs"] != null
      ) {
        for (var dn of attrs["DNDBIDs"]) {
          var dnID = parseInt(dn);
          if (dnID > 0 && dnID == dnInt) {
            return true;
          }
        }
      }
    }
  }
  return false;
}


/**
 * checks if place placeDBID is linked to any person
 * 
 * @param {int} placeDBID 
 * @param {array} arrPersons array of persons objects
 * @param {object} associating array of all DNs
 * @returns true/false
 */
function dnsOnPlace(place, allDNs) {
  if (place != null && place.hasOwnProperty("attributes")) {
    var attrs = place["attributes"];
    if (
      attrs != undefined &&
      attrs.hasOwnProperty("DNDBIDs") &&
      Array.isArray(attrs["DNDBIDs"]) &&
      attrs["DNDBIDs"].length > 0
    ) {
      for (const dnDBID of attrs["DNDBIDs"]) {
        var dnJSON = JSON.parse(CS.objToJson(allDNs[dnDBID]));
        if (dnJSON != null && dnJSON != undefined && Object.keys(dnJSON).length > 0) {
          return true;
        }
      }
    }
  }
  return false;
}

/**
 * checks if place placeDBID is linked to any person
 * 
 * @param {int} placeDBID 
 * @param {array} arrPersons array of persons objects
 * @returns true/false
 */

function placeOnPerson(placeDBID, arrPersons) {
  var placeDBID = parseInt(placeDBID);

  for (const v of arrPersons) {
    if (v != null && v.hasOwnProperty("attributes")) {
      var attrs = v["attributes"];
      if (
        attrs != undefined &&
        attrs.hasOwnProperty("agentInfo") &&
        attrs["agentInfo"] != null
      ) {
        var ai = attrs["agentInfo"];
        if (ai.hasOwnProperty('placeDBID') && ai.placeDBID != null && ai.placeDBID === placeDBID) {
          // console.log('placedbid'+placeDBID + ' on person '+ JSON.stringify(v));
          return true;
        }
      }
    }
  }

  return false;
}

/**
 * checks if place placeDBID is linked to any person
 * 
 * @param {int} loginDBID 
 * @param {array} arrPersons array of persons objects
 * @returns true/false
 */

function loginOnPerson(someID, loginDBID, arrPersons) {
  var loginDBID = parseInt(loginDBID);

  for (const v of arrPersons) {
    if (v != null && v.hasOwnProperty("attributes")) {
      var attrs = v["attributes"];
      if (
        attrs != undefined &&
        attrs.hasOwnProperty("agentInfo") &&
        attrs["agentInfo"] != null
      ) {
        var ai = attrs["agentInfo"];
        if (ai.hasOwnProperty('agentLogins') &&  ai.agentLogins != null) {
          for (const agLogin of ai.agentLogins) {
            if (agLogin != null && agLogin.agentLoginDBID == loginDBID) {
              // console.log('placedbid'+placeDBID + ' on person '+ JSON.stringify(v));
              return true;
            }
          }

        }
      }
    }
  }
  return false;
}

function updatePersonLoginIDs(personDBID, arrLoginsToAdd, arrLoginsToRemove) {
  // var createObj={};
  var createObj = {
    agentInfo: {
      agentLogins: {
        changed: {},
        deleted: {},
        added: {},
      },
    },
  };

  createObj.agentInfo.agentLogins.added = arrLoginsToAdd;
  createObj.agentInfo.agentLogins.deleted = arrLoginsToRemove;

  var obj1 = CS.updateObject(
    "CfgPerson",
    personDBID,
    JSON.stringify(createObj)
  );
  console.log("update result: " + obj1);
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
 * @param {function} compareProc - function that takes CfgObject as paremeter. Should return:
 *  0 - ignore object
 *  1 - match found; stop search
 *  2 - match found; continue search
 * @param {boolean} refresh - should data be refetch from ConfigServer. False by default (use cached)
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

/**
 * Returns true if object is null or empty; false otherwise
 *
 * @param {obj} object object to check for
 */
function objectNotEmpty(object) {
  return object == null || Object.keys(object).length === 0 ? false : true;
}

/**
 * Find object by name attribute.
 * @param {string} objType
 * @param {string} objName - name to search for
 * @param {boolean} refresh - if true, means disable cache
 */
function findObjectByName(objType, objName, refresh = false) {
  var nameName = getNameName(objType);
  var fun = function (obj) {
    if (CS.getAttribute(obj, nameName) == objName) {
      return 2;
    } else return 0;
  };
  return findObject(objType, fun, refresh);
}

/**
 * Find object by name attribute.
 * @param {string} objType
 * @param {string} objName - name to search for
 * @param {boolean} refresh - if true, means disable cache
 */
function findObjectByNameIncludes(objType, objName, refresh = false) {
  return findObject(
    objType,
    function (obj) {
      if (CS.getAttribute(obj, getNameName(objType)).includes(objName)) {
        return 2;
      } else return 0;
    },
    refresh
  );
}

/**
 * gets the name of the 'name' attribute depending of objectType
 * @param {string} objType
 */
function getNameName(objType) {
  switch (objType) {
    case "CfgAgentLogin":
      return "loginCode";

    case "CfgPerson":
      return "userName";

    case "CfgDN":
      return "number";

    default:
      return "name";
  }
}

/**
 * returns agent switches as {dbid:CfgObject, ...}
 */
function getAgentSwitches() {
  var objs = objectDBID_props("CfgSwitch");
  var agentSwitches = Object();
  for (const dbid in objs) {
    if (CS.getAttribute(objs[dbid], "name").includes("sipa1")) {
      agentSwitches[dbid] = objs[dbid];
      //   console.log(
      //     CS.getAttribute(objs[dbid], "name") +
      //       " DBID: " +
      //       CS.getAttribute(objs[dbid], "DBID")
      //   );
    }
  }
  return agentSwitches;
}

/**
 * gets CfgObject and returns HostDBID if obj is server and HostDBID is configured
 *
 * @param {*} appObj
 */
function getAppHostDBID(v) {
  if (v != null && v.hasOwnProperty("attributes")) {
    var attrs = v["attributes"];
    if (
      attrs != undefined &&
      attrs.hasOwnProperty("serverInfo") &&
      attrs["serverInfo"] != null &&
      attrs["serverInfo"].hasOwnProperty("hostDBID")
    ) {
      var dbid = parseInt(attrs["serverInfo"]["hostDBID"]);
      if (dbid > 0) {
        return dbid;
      }
    }
  }
  return null;
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

/**
 * Helper function; will fire exception if variable is empty
 * @param {*} varName
 */
function required(varName) {
  throw new Error(`${varName} is required. `);
}

/**
 * gets first id of GEnum constant
 * @param {String} gEnumName GEnum name
 * @param {String} subString Substring to search for
 */
function findEnum(gEnumName, subString = required("subString")) {
  const subLower = subString.toLowerCase();
  for (const element of CS.getGEnum(gEnumName)) {
    if (element.toString().toLowerCase().includes(subLower)) {
      return element.asInteger();
    }
  }
  return null;
}

function createAgentLogin(loginCode, switchDBID) {
  var createObj = {
    loginCode: loginCode,
    state: 1,
    useOverride: 2,
    switchDBID: switchDBID,
    switchSpecificType: 1,
  };
  var ret = CS.createObject("CfgAgentLogin", JSON.stringify(createObj));
  if (ret >= 0) {
    console.log(
      "created login DBID: " +
      ret +
      " code:" +
      loginCode +
      " switchDBID:" +
      switchDBID
    );
  } else {
    console.log(
      "NOT created login DBID: " +
      ret +
      " code:" +
      loginCode +
      " switchDBID:" +
      switchDBID
    );
  }
  return ret;
}

function testLoginIDs() {
  const NEWNAME = "aaaaaa"; {
    // var dbid = findObjectByName("CfgAgentLogin", NEWNAME);
    // if (dbid != null)
    //   console.log("deleted login: " + CS.deleteObject("CfgAgentLogin", dbid));
  }

  // var createObj = {
  //   loginCode: NEWNAME,
  //   state: 1,
  //   useOverride: 2,
  //   switchDBID: 103,
  //   switchSpecificType: 1,
  //   // userName: "stepan.sydoruk@test.com",
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


function isEmpty(obj) {
  for (var prop in obj) {
    if (Object.prototype.hasOwnProperty.call(obj, prop)) {
      return false;
    }
  }

  return true
}