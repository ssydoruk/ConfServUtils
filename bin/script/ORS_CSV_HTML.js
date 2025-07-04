
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
  "########## IN THE OPM": "in_the_opm",
  "########## IN THE AgentExtension": "in_the_agentextension",
  "########## CLUSTER - OMNICHANNEL ROUTING": "in_the_routing"
};

var regexp = /((?:[0-9]{4})-(?:[0-9]{2})-(?:[0-9]{2}))T((?:[0-9]{2}):(?:[0-9]{2}):(?:[0-9]{2})\.(?:[0-9]+))/;

var logIGNORE = [
  // /"knowledgebase-response/,
  /^\s*'(Diagram created|Running|RelativePathURL|Code Generated|Project version|Project version|Diagram version)/,
  /Reached final in/,
  // /knowledgebase-response/,
  /IN THE REST: Request headers parsing --- key/
];


if (typeof HTML_STAGE != 'undefined') {
  switch (HTML_STAGE) {
    case "head":
      HTML = "";
      HTML += "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">";
      HTML += wrap("    function JSONclick(obj){\n" +
        "        obj.classList.toggle(\"active\");\n" +
        "                var content = obj.nextElementSibling;\n" +
        "                if (content.style.display === \"block\") {\n" +
        "                    content.style.display = \"none\";\n" +
        "                } else {\n" +
        "                    content.style.display = \"block\";\n" +
        "                }\n" +
        "    }\n" +
        "        function doAllJSONs(shouldExpand) {\n" +
        "            var coll = document.getElementsByClassName(\"content\");\n" +
        "            var i;\n" +
        "\n" +
        "            for (i = 0; i < coll.length; i++) {\n" +
        "                coll[i].style.display = (shouldExpand) ? \"block\" : \"none\";\n" +
        "            }\n" +
        "        }\n" +
        "\n" +
        "        function jumpDoc(isTop) {\n" +
        "            (isTop) ? window.scrollTo(0, 0) : window.scrollTo(0, document.body.scrollHeight);\n" +
        "        }", "script");

      break;

    case "body_before_table":
      HTML = wrap(
        "        <button onclick=\"doAllJSONs(true)\">Expand all</button>\n" +
        "        <button onclick=\"doAllJSONs(false)\">Collapse all</button>\n" +
        "        <button onclick=\"jumpDoc(true)\">Top</button>\n" +
        "        <button onclick=\"jumpDoc(false)\">Bottom</button>\n",
        "div", "class=\"sticky\"");
      break;

    case "body_after_table":
      //      HTML = "<script>\n"+
      //      "var coll = document.getElementsByClassName(\"collapsible\");\n" +
      //        "        var i;\n" +
      //        "\n" +
      //        "        for (i = 0; i < coll.length; i++) {\n" +
      //        "            coll[i].addEventListener(\"click\", function () {\n" +
      //        "                this.classList.toggle(\"active\");\n" +
      //        "                var content = this.nextElementSibling;\n" +
      //        "                if (content.style.display === \"block\") {\n" +
      //        "                    content.style.display = \"none\";\n" +
      //        "                } else {\n" +
      //        "                    content.style.display = \"block\";\n" +
      //        "                }\n" +
      //        "            });\n" +
      //        "        }\n"+
      //        "</script>\n"
      //        ;

      break;

    case "cell":
    default:
      processRecord();

  }

}
else {
  processRecord();
}


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

        if ((m = s.match(/^['\s]*(\{\".+\})[^\}\",]?/s)) != undefined) {
          RECORD.put("eventdesc", printJSON(JSON.stringify(JSON.parse(m[1]), undefined, 4))
          );
          colorInThe();
          return;
        }

        if ((m = s.match(/^(.+(?:IN THE CALL FLOW STEPS[^\{]+(?:OPM Data\s+Type|REST Request|translated Request Map is|REST Request|IVR GVP ERROR|Reporting VQ ClearTarget)|DEFAULT ROUTED to|IN THE PERCENT TARGETING.+(?:vTarget |vPctTargets )|IN THE OPM[^\{]+(?:Parameters|data to be attached)|IN THE BUSINESS RULE.+vRequest:|IN THE REST: (?:Request headers|Request data)|IN THE ROUTING:\s*(?:TRANSFER path|TREATMENTS|Genesys Callback Check Module)|IN THE ATTACH KVPs:|FetchConfigsOnDN completed|configuration found for agent|IN THE HOOP: HOOP Flags:|HOOP Rule Response|HOOP Flags|_data.data set as:|Segmentation Facts Rule Results|DEFAULT Route Block at|Call Flow Results |Web Service response|CALL FLOW STEPS: REST (?:Request|Response)|IN THE PERCENT ROUTING:[^\{]+result|IN THE AgentExtension|IN THE TARGETING: TargetListSeleted KVPs|Blind Xfr Return targets|ROUTE_TO ROUTEPOINT added target| LVQ Request result:)[^\{]+)(\{.+\})[^\}\",]?/s)) != undefined) {
          // RECORD.put("mod", RECORD.get("mod") + '<br>' + m[1] + '<br>' + m[2]);

          updateDesc(m[1], m[2], null, ["content", "CSS_IVRParamObj", "REST_RequestMap", "REST_ResponseMap", "REST_RequestBody"]);
          colorInThe();
          return;
        }

        if ((m = s.match(/^(.+(?:IN THE CALL FLOW STEPS|Request result =|IN THE TARGETING:TARGET BLOCK: Target selected| Event =| error =)[^\(]+)(\(.+\))['"]$/s)) != undefined) {
          updateDescObj(m[1], eval(m[2]), null, ["content", "CSS_IVRParamObj", "udata"]);
          colorInThe();
          return;
        }

        if ((m = s.match(/^(.+(?:Afiniti request parameters)[^\(]+)(\(.+\))['"]$/s)) != undefined) {
          updateDescObj(m[1], eval(m[2]), null, ["udata"]);
          colorInThe();
          return;
        }



        if ((m = s.match(/^(.+Rule Results :[^\{]+)(\{.+\})(.+_data.data array:[^\{]+)(\{.+\})/s)) != undefined) {
          var m2Obj = JSON.parse(m[2]);
          dequoteParams(m2Obj, ["CSS_IVRParamObj"]);
          var m4Obj = JSON.parse(m[4]);
          dequoteParams(m4Obj, ["CSS_IVRParamObj"]);

          RECORD.put("eventdesc", br(m[1])
            + printJSON(JSON.stringify(m2Obj, undefined, 4))
            + br(m[3])
            + printJSON(JSON.stringify(m4Obj, undefined, 4))
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

        if ((m = s.match(/^(.+IN THE ROUTING: Feature settings[^\{]+)(\{.*\})([^\}\>\\S].+ Facts:[^\{]+)(\{.*\})([^\}\>\\S].+ Facts:[^\{]+)(\{.*\})/s)) != undefined) {
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

        if ((m = s.match(/(.+FORCE ROUTE BLOCK[^\{]+)(\{.+\})(.+vHints[^\{]+)(\{.+\})(.+)/s)) != undefined) {
          RECORD.put("eventdesc", br(m[1])
            + printJSON(JSON.stringify(JSON.parse(m[2]), undefined, 4))
            + br(m[3])
            + printJSON(JSON.stringify(JSON.parse(m[4]), undefined, 4))
            + br(m[5])
          );
          colorInThe();
          return;
        }

        if ((m = s.match(/(.+IN THE ROUTING:\s+TARGETS Parsed[^\{]+)(\{.+\})([\s]*TargetsResults:[^\{]+)(\{.+\})([\s\w].+)/s)) != undefined) {
          RECORD.put("eventdesc", br(m[1])
            + printJSON(JSON.stringify(JSON.parse(m[2]), undefined, 4))
            + br(m[3])
            + printJSON(JSON.stringify(JSON.parse(m[4]), undefined, 4))
            + br(m[5])
          );
          colorInThe();
          return;
        }


        if ((m = s.match(/^(.+IN THE INTERCOMMUNICATION: URS response is[^(]+)(\(\{.+\}\))(?:.+)/s)) != undefined) {
          updateDescObj(m[1], eval(m[2]), null, null);
          colorInThe();
          return;
        }


        if ((m = s.match(/^(.+IN THE ROUTING: Classification Step[^\{]+)(\{.+\})(.+)/s)) != undefined) {

          updateDesc(m[1], m[2].replaceAll("\\\\", "\\"), m[3], ["CSS_IVRParamObj"]);
          colorInThe();
          return;
        }


        if ((m = s.match(/^(.+(?:IN THE REST: Response is|Call to URS Function.+completed|IN THE ROUTING: (?:ROUTING CODE COMPLETED|Parameters override)|IN THE PERCENT TARGETING.+(?:Calls in queue stats|Calls distributed stats))[^\(]+)(\(.+\))[^\)\>]?/s)) != undefined) {
          var obj = eval(m[2]);
          dequoteParams(obj, ["content", "CSS_IVRParamObj"]);
          decodeURIComponentParams(obj, ["KVPs"]);

          RECORD.put("eventdesc", br(m[1])
            + printJSON(JSON.stringify(obj, undefined, 4))
          );
          colorInThe();
          return;
        }


        // if ((m = s.match(/^(.+(?:IN THE CALL FLOW STEPS[^\(]+OPM Data Type|IN THE REST: Response is|Call to URS Function.+completed|IN THE ROUTING: (?:ROUTING CODE COMPLETED|Parameters override)|IN THE PERCENT TARGETING.+(?:Calls in queue stats|Calls distributed stats))[^\(]+)(\(.+\))[^\)\>]?/s)) != undefined) {
        //   RECORD.put("mod", RECORD.get("mod") + '<br>' + m[1] + '<br>' + m[2]);

        //   var obj = eval(m[2]);
        //   dequoteParams(obj, ["content", "CSS_IVRParamObj"]);

        //   RECORD.put("eventdesc", br(m[1])
        //     + printJSON(JSON.stringify(obj, undefined, 4))
        //   );
        //   colorInThe();
        //   return;
        // }


        if ((m = s.match(/^(.+in user application thread[^\(]*)(\(\{.+\}\))[^\)\>\w]?/s)) != undefined) {
          try {
            updateDescObj(m[1], eval(m[2].replaceAll("\n", "").replaceAll("\\\\", "\\").replaceAll("\\\\", "\\")), null, "content");
          } catch (error) {
            updateDescObj(m[1], m[2].replaceAll("\n", "").replaceAll("\\\\", "\\").replaceAll("\\\\", "\\"), null, "content");

          }
          colorInThe();
          return;
        }

        if ((m = s.match(/(.+Rules applied are: LRV_RulesApplied :)(.+)'*$/s)) != undefined) {
          var arr = m[2].split(",");
          var res = "";
          for (var s1 of arr) {
            res = res + "<li>" + s1;
          }
          RECORD.put("eventdesc", br(m[1])
            + res
          );
          colorInThe();
          return;
        }


        // ------------------------ in the OPM
        if ((m = s.match(/^(.+IN THE OPM: (?:Play Application|data to be attached)[^\{]+)(\{.+\})[^\)\>\w]?/s)) != undefined) {
          var obj = JSON.parse(m[2]);
          if (obj.hasOwnProperty("CSS_IVRParamObj")) {
            obj.CSS_IVRParamObj = JSON.parse(obj.CSS_IVRParamObj.replaceAll("\\\"", "\""));
          }
          RECORD.put("eventdesc", br(m[1])
            + printJSON(JSON.stringify(obj, undefined, 4))
          );
          colorInThe();
          return;
        }

        if ((m = s.match(/^(.+IN THE OPM.+setting udata:[^\{]+vCSS_IVRParamObj:[^\{]+)(.+\})[\\"']+$/s)) != undefined) {
          RECORD.put("eventdesc", br(m[1])
            + printJSON(JSON.stringify(JSON.parse(m[2].replaceAll("\\\"", "\"")), undefined, 4))
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
          colorInThe();
          break;

        }

        /****************** here are messages in plain log tag ************************/

        if ((m = s.match(/^(.+(?:never match|Data Fetched|LVQ Request result =)[^\{]+)(\{.+\})[^\}\",]?/s)) != undefined) {
          // RECORD.put("mod", RECORD.get("mod") + '<br>' + m[1] + '<br>' + m[2]);

          var obj = JSON.parse(m[2]);
          dequoteParams(obj, ["content", "CSS_IVRParamObj"]);
          decodeURIComponentParams(obj, ["KVPs"]);

          RECORD.put("eventdesc", br(m[1])
            + printJSON(JSON.stringify(obj, undefined, 4))
          );

          colorInThe();
          return;
        }

        // JSON object as in javascript
        if ((m = s.match(/^(.+(?:Data Assigned|return values =|ERROR:)[^\(]+)(\(.+\))['"]$/s)) != undefined) {
          // RECORD.put("mod", RECORD.get("mod") + '<br>' + m[1] + '<br>' + m[2]);

          obj = eval(m[2].replaceAll("\\\\", "\\"));
          dequoteParams(obj, ["content", "CSS_IVRParamObj"]);
          decodeURIComponentParams(obj, ["KVPs"]);

          RECORD.put("eventdesc", br(m[1])
            + printJSON(JSON.stringify(obj, undefined, 4))
          );
          colorInThe();
          return;
        }

        /******** JSON message in a string ********/
        if ((m = s.match(/^(.+(?:BusinessRule: fetch done)[^\"]*)(\".+\")[']*$/s)) != undefined) {
          // RECORD.put("mod", RECORD.get("mod") + '<br>' + m[1] + '<br>' + m[2]);
          var first = m[1];
          var s1 = dequote(un2quote(m[2]).replaceAll("\n", "")).replaceAll("\\\\", "\\");
          obj = JSON.parse(s1);
          // dequoteParams(obj, ["content", "CSS_IVRParamObj"]);

          RECORD.put("eventdesc", br(first)
            + printJSON(JSON.stringify(obj, undefined, 4))
          );
          colorInThe();
          return;
        }


        if ((m = s.match(/^(.+CLUSTER - OMNICHANNEL[^\{]+(?: = :|API Response =|with _data context)[^\{]+)(\{.+\})[^\}\",]?/s)) != undefined) {
          // RECORD.put("mod", RECORD.get("mod") + '<br>' + m[1] + '<br>' + m[2]);

          updateDesc(m[1], m[2], null, ["content", "CSS_IVRParamObj"]);
          colorInThe();
          return;
        }


        /* URL encoded JSON object */
        if ((m = s.match(/^(.+(?:IN THE TARGETING:[^%]+vGmsStorageData set as:))(.+)'+$/s)) != undefined) {
          // RECORD.put("mod", RECORD.get("mod") + '<br>' + m[1] + '<br>' + m[2]);

          RECORD.put("eventdesc", br(m[1])
            + printJSON(JSON.stringify(decodeURIJSON(m[2]), undefined, 4))
          );
          colorInThe();
          return;
        }

        /* URL encoded JSON statement */
        if ((m = s.match(/^(.+(?:IN THE REST: Request data is[:"\s]+))([^"']+)/s)) != undefined) {
          // RECORD.put("mod", RECORD.get("mod") + '<br>' + m[1] + '<br>' + m[2]);
          try {
            var first = m[1];
            var second = decodeURIComponent(m[2]);
            if ((m = second.match(/^(KVPs=)(.+)/)) != undefined) {
              RECORD.put("eventdesc", br(first)
                + m[1]
                + printJSON(JSON.stringify(JSON.parse(m[2]), undefined, 4))
              );

            }
            else {
              RECORD.put("eventdesc", br(first)
                + printJSON(JSON.stringify(decodeURIJSON(m[2]), undefined, 4))
              );
            }

          } catch (error) {

          } colorInThe();
          return;
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
  return "<button type=\"button\" onclick=\"JSONclick(this)\" class=\"collapsible\">..</button>" +
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


/**
 * Updates eventdesc. 
 * @param {*} first first part
 * @param {*} second JSON object
 * @param {*} props array of keys where value is a string as JSON object
 */
function updateDesc(first, second, third, props) {
  updateDescObj(first, JSON.parse(second), third, props);
}


/**
 * 
 * @param {*} first initial part
 * @param {*} second is an object
 * @param {*} third trailing part. can be empty
 * @param {*} props array of property names that are strings that should be transformed to JSON 
 */
function updateDescObj(first, obj, third, props) {
  dequoteParams(obj, props);

  RECORD.put("eventdesc", br(first)
    + printJSON(JSON.stringify(obj, undefined, 4))
    + ((third != null) ? br(third) : "")
  );
}


function dequote(str) {
  return (str != null) ? str.replaceAll("\\\"", "\"") : "";
}


function dequoteParams(obj, props) {
  if (props != null && obj != null) {
    for (var i = 0; i < props.length; i++) {
      var propName = props[i];
      if (obj.hasOwnProperty(propName) && obj[propName] != null && obj[propName].length > 0) {
        // RECORD.put("mod", RECORD.get("mod") +'<br>'+JSON.stringify(obj)+'<br>'+JSON.stringify(propName));
        try {
          obj[propName] = JSON.parse(dequote(obj[propName]));
          // RECORD.put("mod", RECORD.get("mod") +'%%%%%%'+JSON.stringify(obj)+'-- -- '+JSON.stringify(propName));
        } catch (error) {
          console.log('err: ' + JSON.stringify(error));
        }
      }
    }
  }
}

function decodeURIComponentParams(obj, props) {
  for (var i = 0; i < props.length; i++) {
    var propName = props[i];
    if (obj.hasOwnProperty(propName) && obj[propName] != null && obj[propName].length > 0) {
      try {
        obj[propName] = decodeURIJSON(obj[propName]);
      } catch (error) {
        console.log('err: ' + JSON.stringify(error));
      }
    }
  }
}


function decodeURIJSON(s) {
  return JSON.parse(decodeURIComponent(s));
}

/**
 * Remove single set of quotation characters
 * @param {*} str string
 */

function un2quote(str) {
  var m2;
  if ((m2 = str.match(/^\"(.+)\"$/s)) != undefined) {
    return m2[1];
  }
  return str;
}
