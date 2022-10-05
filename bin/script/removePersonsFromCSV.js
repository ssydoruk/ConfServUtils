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
                CS.deleteObject("CfgPerson", dbid);
                               
            }

        } else {
            console.log("not found person " + newName);
        }
    }


    console.log("--done--");
} catch (error) {
    console.error("error: " + error);
}
