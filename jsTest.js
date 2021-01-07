// console.log(CS.findObject("CfgPerson", 'name', PARAMS[0]));
// var attr=JSON.parse(CS.getObjectAttributes("CfgPerson"));
// console.log(JSON.stringify(attr,null,3))
var obj=JSON.parse(CS.findObject("CfgPerson", "userName", "stepan.sydoruk@ext.airbnb.com"));
console.log(JSON.stringify(obj,null,3))

console.log('--done--');
console.error('error');
