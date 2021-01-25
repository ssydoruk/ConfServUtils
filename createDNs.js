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
