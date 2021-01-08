// console.log(CS.findObject("CfgPerson", 'name', PARAMS[0]));
// var attr=JSON.parse(CS.getObjectAttributes("CfgPerson"));
// console.log(JSON.stringify(attr,null,3))
var obj = JSON.parse(
  CS.findObject("CfgPerson", "userName", "stepan.sydoruk@ext.airbnb.com")
);
console.log(JSON.stringify(obj, null, 3));
if (UPDATEPROPS != null) {
  UPDATEPROPS.put("userName", "stepan.sydoruk@ext.airbnb.com11");
  var updateObj = {
    // userName: "stepan.sydoruk@ext.airbnb.com",
    agentInfo: {
      skillLevels: {
        changed: { 
          // 103: 2, 
          // 238: 3 
        },
        // deleted: [238],
        added: { 
          // 103: 2, 
          238: 3 
        },
      },
      agentLogins: {
        changed: {},
        deleted: {},
      },
    },
  };

  var obj1 = CS.updateObject("CfgPerson", 247, JSON.stringify(updateObj));
  console.log("update result: " + obj1);
}

console.log("--done--");
console.error("error");
