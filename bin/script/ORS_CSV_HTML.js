
// RECORD.put("eventdesc", "+++"+RECORD.get("eventdesc"));

var classMap = {
  "########## IN THE ROUTING": "in_the_routing",
  "########## IN THE BUSINESS RULE": "in_business_rule",
  "########## IN THE HOOP": "in_the_hoop",
  "########## IN THE REST": "in_the_rest",
  "########## IN THE CALL FLOW STEPS": "in_the_call_flow_steps",
  "########## IN THE HOOP:": "in_the_hoop",
  "########## IN THE ATTACH KVPs": "in_the_attach_kvp",
  "########## IN THE TARGETING": "in_the_targeting",
  "########## IN THE PERCENT TARGETING": "in_the_percent_targeting",
  "########## IN THE OPM": "in_the_opm"
};

var regexp = /((?:[0-9]{4})-(?:[0-9]{2})-(?:[0-9]{2}))T((?:[0-9]{2}):(?:[0-9]{2}):(?:[0-9]{2})\.(?:[0-9]+))/;

var logIGNORE = [
  /"knowledgebase-response/,
  /^\s*'(Diagram created|Running|RelativePathURL|Code Generated|Project version|Project version|Diagram version)/,
  /Reached final in/,
  /knowledgebase-response/,
  /IN THE REST: Request headers parsing --- key/
];

processRecord();

function processRecord() {
  var m;

  try {
    if ((m = RECORD.get("_time").match(regexp)) != undefined) {
      RECORD.put("_time", m[1] + "<br>" + m[2]);
    }
  } catch (error) {

  }

  var raw = RECORD.get("_raw");
  if (raw != null && raw.length > 0) {
    RECORD.put("_raw", wrap(wrap(raw, "span", "class=\"json\""), "pre"));
    return;
  }

  var s = RECORD.get("eventdesc");
  var mod = RECORD.get("mod");
  var PRINTOUT = {};

  try {
    switch (mod) {

      case "log": {

        var re;

        if ((m = s.match(/^(.+(?:IN THE PERCENT TARGETING.+(?:vTarget |vPctTargets )|IN THE OPM: Parameters:|IN THE BUSINESS RULE.+vRequest:|IN THE REST: (?:Request headers|Request data)|IN THE ROUTING: (?:TRANSFER path|TREATMENTS)|IN THE ATTACH KVPs:|FetchConfigsOnDN completed|configuration found for agent|IN THE HOOP: HOOP Flags:|HOOP Rule Response|HOOP Flags|Request result =|_data.data set as:|Segmentation Facts Rule Results|DEFAULT Route Block at| LVQ Request result:|Call Flow Results |Web Service response|CALL FLOW STEPS: REST (?:Request|Response)|IN THE CALL FLOW STEPS: IVR GVP ERROR|IN THE PERCENT ROUTING:[^\{]+result)[^\{]+)(\{.+\})[^\}\",]?/s)) != undefined) {
          // RECORD.put("eventdesc", s.replace(re, "$1" + JSON.stringify(JSON.parse(m[2]), undefined, 4)));
          RECORD.put("eventdesc",  br(m[1]) + printJSON(JSON.stringify(JSON.parse(m[2]), undefined, 4)));
          colorInThe();
          return;
        }



        var re = /fetch done\"({.+})\"/s;
        if ((m = s.match(re)) != undefined) {
          var s = m[1];
          PRINTOUT.detailsMessage = s.replace(/\\u000a/g, '\n').replace(/\\\"/g, '\"');
          break;
        }

        if ((m = s.match(/application thread \((.+)\)\'/)) != undefined) {
          //        var s = m[1].replace(/\\n/g, '\n').replace(/\\\"/g, '\"').replace(/\\\\\"/g, '\"');
          var s = m[1].replace(/\\n/g, '\n').replace(/\\\"/g, '\"');
          // console.log(s);
          try {
            PRINTOUT.detailsMessage = '-->' + JSON.stringify(JSON.parse(s.replace(/(\w+):/g, '\"$1\":')), undefined, 4);
          }
          catch (e) {
            console.log('error parsing: ' + e.message);
            PRINTOUT.detailsMessage = '++>' + s;
          }
          break;
        }

        if ((m = s.match(/^(.+Rule Results :[^\{]+)(\{.+\})(.+_data.data array:[^\{]+)(\{.+\})/s)) != undefined) {
          RECORD.put("eventdesc", br(m[1])
            + printJSON(JSON.stringify(JSON.parse(m[2]), undefined, 4))
            + br(m[3])
            + printJSON(JSON.stringify(JSON.parse(m[4]), undefined, 4))
          );
          colorInThe();
          return;
        }

        if ((m = s.match(/^(.+IN THE HOOP: HOOP Web [^\{]+)(\{.+\})([^\}\>\\S].*HOOP Response[^\{]+)(\{.+\})[^\}\>\\S]*/s)) != undefined) {
          RECORD.put("eventdesc", br(m[1])
            + printJSON(JSON.stringify(JSON.parse(m[2]), undefined, 4))
            + br(m[3])
            + printJSON(JSON.stringify(JSON.parse(m[4]), undefined, 4))
          );
          colorInThe();
          return;
        }        

        if ((m = s.match(/^(.+IN THE ROUTING: Feature settings[^\{]+)(\{.+\})([^\}\>\\S].+_data.ivrdata[^\{]+)(\{.+\})([^\}\>\\S].+_data.featuresdata[^\{]+)(\{.+\})/s)) != undefined) {
          RECORD.put("eventdesc", br(m[1])
            + printJSON(JSON.stringify(JSON.parse(m[2]), undefined, 4))
            + br(m[3])
            + printJSON(JSON.stringify(JSON.parse(m[4]), undefined, 4))
            + br(m[5])
            + printJSON(JSON.stringify(JSON.parse(m[6]), undefined, 4))
          );
          colorInThe();
          return;
        }



        if ((m = s.match(/^(.+IN THE ROUTING: Classification Step[^\{]+)(\{.+\})(.+)/s)) != undefined) {
          RECORD.put("eventdesc", br(m[1])
            + printJSON(JSON.stringify(JSON.parse(m[2]), undefined, 4))
            + br(m[3])
          );
          colorInThe();
          return;
        }


        /* m[2] is string which is javascript object */
        if ((m = s.match(/^(.+(?:IN THE REST: Response is|Call to URS Function.+completed|IN THE ROUTING: ROUTING CODE COMPLETED)[^\(]+)(\(.+\))[^\)\>\w]?/s)) != undefined) {
          RECORD.put("eventdesc", br(m[1])
            + printJSON(JSON.stringify(eval(m[2]), undefined, 4))
          );
          colorInThe();
          return;
        }

        if ((m = s.match(/^(.+)(\(\{data:.+\}\))[^\)\>\w]?/s)) != undefined) {
          RECORD.put("eventdesc", br(m[1])
            + printJSON(JSON.stringify(eval(m[2]), undefined, 4))
          );
          colorInThe();
          return;
        }

        if ((m = s.match(/(.+Rules applied are: LRV_RulesApplied :)(.+)'*$/s)) != undefined) {
          var arr=m[2].split(",");
          var res="";
          for(var s1 of arr){
            res=res+"<li>"+s1;
          }
          RECORD.put("eventdesc", br(m[1])
            + res           
          );
          colorInThe();
          return;
        }


        // ------------------------ in the OPM
        if ((m = s.match(/^(.+IN THE OPM.+vCSS_IVRParamObj:[^\{]+)(\{.+\})/s)) != undefined) {
          RECORD.put("eventdesc", br(m[1])
            + printJSON(JSON.stringify(JSON.parse(m[2]), undefined, 4))
          );
          colorInThe();
          return;
        }

        if ((m = s.match(/_data.data:[^\{]+(\{.+\}).+RoutingParameters:[^\{]+(\{.+\})/)) != undefined) {
          var dt = m[1];
          var rp = m[2];
          try {
            PRINTOUT.detailsMessage = '_data.data\n' + JSON.stringify(JSON.parse(dt), undefined, 4)
              + '\nRoutingParameters\n' + JSON.stringify(JSON.parse(rp), undefined, 4)
          }
          catch (e) {
            console.log('error parsing: ' + e.message);
            PRINTOUT.detailsMessage = '++>' +
              '_data.data\n' + dt
              + '\nRoutingParameters\n' + rp;
          }

          break;

        }

        else {
          // PRINTOUT.detailsMessage=JSON.stringify({"cc":"bb", "key1":"val1"});
        }

        for (var i = 0; i < logIGNORE.length; i++) {
          if (s.match(logIGNORE[i]) != null) {
            IGNORE_RECORD = true;
            return;
          }
        }


        break;
      }

      default:
        break;
    }
  }
  catch (e) {
    console.log('error parsing: ' + e.message);
  }
  if (s != null) {
    RECORD.put("eventdesc", br(s));
  }
  colorInThe();
}


function wrap(orig, tag, attr) {
  var s = "<" + tag;
  if (attr != undefined)
    s += " " + attr;
  return s + ">" + orig + "</" + tag + ">";
}

function br(orig) {
  return orig.replaceAll("\n", '<br>');
}

function printJSON(orig) {
  return "<button type=\"button\" class=\"collapsible\">json</button>" +
    wrap(wrap(orig, "pre"), "span", "class=\"content\"");


  //wrap(wrap(orig, "span", "class=\"json\""), "pre");


  // return 
  // "<button type=\"button\" class=\"collapsible\">json</button>"+
  // wrap(wrap(wrap(orig, "span", "class=\"json\""), "pre"), "span", "class=\"content\"");


}

/**
 * puts CSS class tag into predefined substrings.
 * Assuming only one substring is stiled
 */
function colorInThe() {
  var s = RECORD.get("eventdesc");

  if (s != null && s.length > 0) {

    for (const key in classMap) {
      var idx = s.indexOf(key);
      if (idx >= 0) {
        RECORD.put("eventdesc", s.substring(0, idx) + wrap(key, "span", "class=\"" + classMap[key] + "\"") + s.substring(idx + key.length));
        return;
      }
    }
  }

}