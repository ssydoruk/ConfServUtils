load("/Users/stepan_sydoruk/src/ConfServUtils/common.js");

/**
 * Script to update user name and place name
 *
 * Uses importFile file name that has 2 columns: oldemail,newemail
 */
var importFile = "/Users/stepan_sydoruk/aaa.csv";

console.log("-start-");
try {
  CS.connectToConfigServer();

  renameObjects();

  console.log("--done--");
} catch (error) {
  console.error("error: " + error);
}

function renameObjects() {
  console.log("renaming");

  var csv = CS.readCSV(importFile); // returned is ArrayList of String Array
  for (let index = 0; index < csv.size(); index++) {
    var arr = csv.get(index);
    console.log("processing " + arr);

    var email = arr[0].trim();
    var replEmail = arr[1].trim();
    var nameOnly = email.substr(0, email.indexOf("@"));
    console.log(nameOnly);
    var personsFound = findObjectByNameIncludes("CfgPerson", nameOnly);
    if (objectNotEmpty(personsFound)) {
      for (const dbid in personsFound) {
        var thePerson = JSON.parse(CS.objToJson(personsFound[dbid]));
        console.log(JSON.stringify(thePerson));

        /* modification of agent LoginIDs. Leave unfinished for now 

        var arrAgentLogins = thePerson.attributes.agentInfo.agentLogins;
        var newLoginDBIDs = [];
        var removeLoginDBIDs = [];
        arrAgentLogins.forEach((element) => {
          var agentLogin = objectDBID_props("CfgAgentLogin")[
            element.agentLoginDBID
          ];
          removeLoginDBIDs.push(element.agentLoginDBID);
          if (objectNotEmpty(agentLogin)) {
            var loginCode = CS.getAttribute(agentLogin, "loginCode");
            console.log(loginCode);
            if (loginCode == email) {
              console.log("equals!");
              newLoginDBIDs.push(createAgentLogin(replEmail, CS.getAttribute(agentLogin, "switchDBID")));
            }
            // console.log(JSON.stringify(JSON.parse(CS.objToJson(agentLogin))));
          } else {
            console.log("not found dbid: " + element);
          }
        });
        updatePersonLoginIDs(dbid, newLoginDBIDs, removeLoginDBIDs);
        */

        var updateUserObj = {
          userName: replEmail,
          emailAddress: replEmail,
        };
        console.log(
          "result of update of DBID:" +
            dbid +
            ": " +
            CS.updateObject("CfgPerson", dbid, JSON.stringify(updateUserObj))
        );
      }
    } else {
      console.log("not found person " + email);
    }

    var placeFound = findObjectByName("CfgPlace", email);
    if (objectNotEmpty(placeFound) && false) {
      for (const dbid in placeFound) {
        console.log(JSON.stringify(JSON.parse(CS.objToJson(placeFound[dbid]))));
        var updatePlaceObj = {
          name: replEmail,
        };
        console.log(
          "result of update of DBID:" +
            dbid +
            ": " +
            CS.updateObject("CfgPlace", dbid, JSON.stringify(updatePlaceObj))
        );
      }
    } else {
      console.log("not found place " + email);
    }

    console.log("done " + arr);
  }
  console.log("done rename");
}
