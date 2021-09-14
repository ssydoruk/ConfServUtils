load("C:\\Users\\STISY7\\IdeaProjects\\ConfServUtils\\common.js");

/**
 * Wi
 */
try {
    CS.connectToConfigServer();


    var places = objectDBID_props("CfgPlace");
    var allPersons = objectDBID_props("CfgPerson");
    var allDNs = objectDBID_props("CfgDN");
    var arrPersons = new Array();
    for (const personDBID in allPersons) {
        var o = JSON.parse(CS.objToJson(allPersons[personDBID]));
        arrPersons.push(o);
        // console.log(JSON.stringify(o));
    }
    var total = 0;
    for (const placeDBID in places) {
        var name = CS.getAttribute(places[placeDBID], "name");

        if (!dnsOnPlace(JSON.parse(CS.objToJson(places[placeDBID])), allDNs) && !placeOnPerson(placeDBID, arrPersons)) {
            total++;
            console.log(
                name +
                " path: " +
                CS.getObjectPath(places[placeDBID]) +
                " DBID: " +
                CS.getAttribute(places[placeDBID], "DBID") 
            );
            CS.deleteObject("CfgPlace",  parseInt(placeDBID));
            // break;
        }
    }
} catch (error) {
    console.error("error: " + error);
}
console.log('objects found: ' + total);
console.log("--done--");