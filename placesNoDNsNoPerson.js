load("C:\\Users\\STISY7\\IdeaProjects\\ConfServUtils\\common.js");

/**
 * Wi
 */
try {
    console.log('starting...');
    CS.connectToConfigServer();


    var places = objectDBID_props("CfgPlace");
    var allPersons = objectDBID_props("CfgPerson");
    var allDNs = objectDBID_props("CfgDN");
    const objStateDisabled = findEnum("CfgObjectState", "CFGDisabled");

    var arrPersons = new Array();
    for (const personDBID in allPersons) {
        var o = JSON.parse(CS.objToJson(allPersons[personDBID]));
        arrPersons.push(o);
        // console.log(JSON.stringify(o));
    }
    var total = 0;
    for (const placeDBID in places) {
        var plName = CS.getAttribute(places[placeDBID], "name");
        // var o1 = JSON.parse(CS.objToJson(places[placeDBID]));
        var state = CS.getAttribute(places[placeDBID], "state") == objStateDisabled? "disabled" : "enabled";

        if (
            !dnsOnPlace(JSON.parse(CS.objToJson(places[placeDBID])), allDNs) &&
            !plName.toLowerCase().includes('chat') &&
            !plName.toLowerCase().includes('supdesktop')
        ) {
            total++;
            console.log(
                'No dn on place - ' +
                plName +
                " path: " +
                CS.getObjectPath(places[placeDBID]) +
                " DBID: " +
                CS.getAttribute(places[placeDBID], "DBID") +
                " state: " + state
            );
            // CS.deleteObject("CfgPlace",  parseInt(placeDBID));
            // break;
        }
        if (
            !placeOnPerson(placeDBID, arrPersons) && !plName.includes('GVP')) {
            total++;
            console.log(
                'Place not on person ' +
                plName +
                " path: " +
                CS.getObjectPath(places[placeDBID]) +
                " DBID: " +
                CS.getAttribute(places[placeDBID], "DBID") +
                " state: " + state
            );
            //CS.deleteObject("CfgPlace",  parseInt(placeDBID));
            // break;
        }
        if ( false && 
            state == objStateDisabled //removing disabled place
        ) {
            total++;
            console.log(
                'disabled place ' +
                plName +
                " path: " +
                CS.getObjectPath(places[placeDBID]) +
                " DBID: " +
                CS.getAttribute(places[placeDBID], "DBID") +
                " state: " + state
            );
            // CS.deleteObject("CfgPlace",  parseInt(placeDBID));
            // break;
        }
    }
} catch (error) {
    console.error("error: " + error);
}
console.log('objects found: ' + total);
console.log("--done--");
load("C:\\Users\\STISY7\\IdeaProjects\\ConfServUtils\\common.js");

/**
 * Wi
 */
try {
    console.log('starting...');
    CS.connectToConfigServer();


    var places = objectDBID_props("CfgPlace");
    var allPersons = objectDBID_props("CfgPerson");
    var allDNs = objectDBID_props("CfgDN");
    const objStateDisabled = findEnum("CfgObjectState", "CFGDisabled");

    var arrPersons = new Array();
    for (const personDBID in allPersons) {
        var o = JSON.parse(CS.objToJson(allPersons[personDBID]));
        arrPersons.push(o);
        // console.log(JSON.stringify(o));
    }
    var total = 0;
    for (const placeDBID in places) {
        var plName = CS.getAttribute(places[placeDBID], "name");
        // var o1 = JSON.parse(CS.objToJson(places[placeDBID]));
        var state = CS.getAttribute(places[placeDBID], "state") == objStateDisabled? "disabled" : "enabled";

        if ( 
            !dnsOnPlace(JSON.parse(CS.objToJson(places[placeDBID])), allDNs) &&
            !plName.toLowerCase().includes('chat') 
        ) {
            total++;
            console.log(
                'No dn on place - ' +
                plName +
                " path: " +
                CS.getObjectPath(places[placeDBID]) +
                " DBID: " +
                CS.getAttribute(places[placeDBID], "DBID") +
                " state: " + state
            );
            // CS.deleteObject("CfgPlace",  parseInt(placeDBID));
            // break;
        }
        if (
            !placeOnPerson(placeDBID, arrPersons) && !plName.includes('GVP')) {
            total++;
            console.log(
                'Place not on person ' +
                plName +
                " path: " +
                CS.getObjectPath(places[placeDBID]) +
                " DBID: " +
                CS.getAttribute(places[placeDBID], "DBID") +
                " state: " + state
            );
           // CS.deleteObject("CfgPlace",  parseInt(placeDBID));
            // break;
        }
        if ( false && 
            state == objStateDisabled //removing disabled place
        ) {
            total++;
            console.log(
                'disabled place ' +
                plName +
                " path: " +
                CS.getObjectPath(places[placeDBID]) +
                " DBID: " +
                CS.getAttribute(places[placeDBID], "DBID") +
                " state: " + state
            );
            // CS.deleteObject("CfgPlace",  parseInt(placeDBID));
            // break;
        }
    }
} catch (error) {
    console.error("error: " + error);
}
console.log('objects found: ' + total);
console.log("--done--");
