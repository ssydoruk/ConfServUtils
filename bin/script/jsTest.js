//  console.log(CS.findObject("CfgPerson", 'name', PARAMS[0]));
// var attr=JSON.parse(CS.getObjectAttributes("CfgPerson"));
// console.log(JSON.stringify(attr,null,3))
console.log("-start-");
try {
  CS.connectToConfigServer();

  var allSwitches = CS.findObjects("CfgPerson");
  for (let index = 0; index < allSwitches.size(); index++) {
    var obj = allSwitches.get(index);
    if (CS.getAttribute(obj, "userName").includes("gaurav")) {
      console.log(JSON.stringify(JSON.parse(CS.objToJson(obj))));
    }
  }

  //  var v = CS.getObject("CfgPlace");
  //  console.log("get object done");
  var attr = JSON.parse(CS.getObjectAttributes("CfgDN"));
  console.log(JSON.stringify(attr, null, 3));
} catch (err) {
  console.error("error: " + err);
}

for (const key in CS.findObject("CfgPerson", "", function () {
  return 2 + 3;
})) {
  console.log(key);
}

// var obj = JSON.parse(

//   CS.findObjects("CfgPerson")
// );
//var all = CS.findObjects("CfgPerson", 5);
//console.log("total persons: " + all.size());

for (let index = 0; index < allSwitches.size(); index++) {
  var obj = allSwitches.get(index);
  if (CS.getAttribute(obj, "userName").includes("stepan")) {
    console.log(JSON.stringify(JSON.parse(CS.objToJson(obj)), null, 3));
  }
}

// console.log(JSON.stringify(obj));
var createObj = {
  // userName: "stepan.sydoruk@test.com",
  agentInfo: {
    skillLevels: {
      changed: {
        // 103: 2,
        238: 5,
      },
      // deleted: [238],
      added: {
        // 103: 2,
        // 238: 3,
      },
    },
    agentLogins: {
      changed: {},
      deleted: {},
    },
  },
};

var obj1 = CS.updateObject("CfgPerson", 247, JSON.stringify(createObj));
console.log("update result: " + obj1);
console.log("--done--");
