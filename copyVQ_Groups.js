load("/Users/stepan_sydoruk/src/ConfServUtils/common.js");

/**
 * Searches for DN and create copy on the same switches with the same properties of
 * DNs from the imported list
 */

console.log("-start-");
try {
  CS.connectToConfigServer();

    // var switches = objectDBID_props("CfgDNGroup");


    var csv = CS.readCSV("/Users/stepan_sydoruk/Documents/CMEImport/TRIPEXP-36/VQ_Groups.csv"); // returned is ArrayList of String Array
    for (let index = 0; index < csv.size(); index++) {
      var arr = csv.get(index);
      console.log("processing " + arr);
      var newName;
      if(typeof arr === 'string'){
        newName=arr;
      }
      else {
        newName=arr[0];
      }
      if(newName === ""){
          continue;
      }
      var uProperties;
        var createObj = {
          name: newName,
          state: 1,
        };
        var ret = CS.createObject(
          "CfgDNGroup",
          JSON.stringify(createObj),
          uProperties
        );
        if (ret >= 0) {
          console.log("created DNGroup DBID: " + ret);
        } else {
          console.log("NOT created DNGroup: " + ret);
        }
    }

  console.log("--done--");
} catch (error) {
  console.error("error: " + error);
}
