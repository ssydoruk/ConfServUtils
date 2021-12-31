load("C:\\Users\\STISY7\\IdeaProjects\\ConfServUtils\\common.js");

/**
 * Searches for DN and create copy on the same switches with the same properties of
 * DNs from the imported list
 */

console.log("-start-");
try {
    CS.connectToConfigServer();

    var csv = CS.readCSV("C:\\Users\\STISY7\\Documents\\username.csv"); // returned is ArrayList of String Array
    for (let index = 0; index < csv.size(); index++) {
        var arr = csv.get(index);
        console.log("processing " + arr);
        var newName;
        if (typeof arr === 'string') {
            newName = arr.replace(/[^\x20-\x7E]/g, '');;
        } else {
            newName = arr[0].replace(/[^\x20-\x7E]/g, '');
        }

        var personsFound = findObjectByNameIncludes("CfgPerson", newName);
        if (objectNotEmpty(personsFound)) {
            for (const dbid in personsFound) {
                var thePerson = JSON.parse(CS.objToJson(personsFound[dbid]));
                console.log(JSON.stringify(thePerson));

                var capRule = thePerson.attributes.agentInfo.capacityRuleDBID;
                console.log('Capacity rule: ' + capRule);

                // var ai = thePerson.attributes.agentInfo;
                // ai.capacityRuleDBID=0;
                // var obj2 = CS.updateObject(
                //     "CfgPerson",
                //     dbid,
                //     JSON.stringify(ai)
                // );
                // console.log("update result: " + obj2);

                // var createObj={};
                var createObj = {
                    agentInfo: {
                        capacityRuleDBID: 0
                    },
                };

                var obj1 = CS.updateObject(
                    "CfgPerson",
                    dbid,
                    JSON.stringify(createObj)
                );
                console.log("update result: " + obj1);
            }

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

            //   var updateUserObj = {
            //     userName: replEmail,
            //     emailAddress: replEmail,
            //   };
            //   console.log(
            //     "result of update of DBID:" +
            //       dbid +
            //       ": " +
            //       CS.updateObject("CfgPerson", dbid, JSON.stringify(updateUserObj))
            //   );

        } else {
            console.log("not found person " + newName);
        }
    }


    console.log("--done--");
} catch (error) {
    console.error("error: " + error);
}