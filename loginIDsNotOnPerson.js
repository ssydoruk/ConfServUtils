load("C:\\Users\\STISY7\\IdeaProjects\\ConfServUtils\\common.js");

/**
 * Wi
 */
try {
    console.log('starting...');
    CS.connectToConfigServer();


    var agentLogins = objectDBID_props("CfgAgentLogin");
    var allPersons = objectDBID_props("CfgPerson");
    const objStateDisabled = findEnum("CfgObjectState", "CFGDisabled");

    var arrPersons = new Array();
    for (const personDBID in allPersons) {
        var o = JSON.parse(CS.objToJson(allPersons[personDBID]));
        arrPersons.push(o);
        // console.log(JSON.stringify(o));
    }
    var total = 0;
    for (const loginDBID in agentLogins) {
        if (
             !loginOnPerson(JSON.parse(CS.objToJson(agentLogins[loginDBID])), loginDBID, arrPersons)
            // !loginDBID.toLowerCase().includes('chat') 
        ) {
            total++;
            console.log(
                'loginID not on person - ' +
                CS.getAttribute(agentLogins[loginDBID], "loginCode") +
                " path: " +
                CS.getObjectPath(agentLogins[loginDBID]) +
                " DBID: " +
                CS.getAttribute(agentLogins[loginDBID], "DBID") +
                " state: " + (CS.getAttribute(agentLogins[loginDBID], "state") == objStateDisabled? "disabled" : "enabled")
            );
            // CS.deleteObject("CfgAgentLogin",  parseInt(loginDBID));
            // break;
        }
    }
} catch (error) {
    console.error("error: " + error);
}
console.log('objects found: ' + total);
console.log("--done--");
