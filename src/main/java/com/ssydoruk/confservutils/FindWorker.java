/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ssydoruk.confservutils;

import static Utils.StringUtils.matching;
import com.genesyslab.platform.applicationblocks.com.*;
import com.genesyslab.platform.commons.collections.*;
import java.util.*;
import java.util.regex.*;
import org.apache.logging.log4j.*;


public class FindWorker {

    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger();

    static int numTrue(boolean... bools) {
        int ret = 0;

        // using for each loop to display contents of a
        for (boolean b : bools) {
            ret += (b ? 1 : 0);
        }
        return ret;

    }

    private final ISearchSettings ss;

    private Pattern ptAll = null;
    private Pattern ptSection = null;
    private Pattern ptKey = null;
    private Pattern ptVal = null;
    private Pattern ptName = null;

    public FindWorker(ISearchSettings ss) {
        this.ss = ss;
        initSearch();
    }

    public boolean isFullOutputSelected() {
        return ss.isFullOutputSelected();
    }

    public boolean isAllKVPOutput(){
        return ss.isAllKVPsInOutput();
    }

    private void initSearch() {
        int flags = ((ss.isRegex()) ? 0 : Pattern.LITERAL) | ((ss.isCaseSensitive()) ? 0 : Pattern.CASE_INSENSITIVE);
        String objName = ss.getObjName();
        String section = ss.getSection();
        String option = ss.getOption();
        String val = ss.getValue();

        if (ss.isSearchAll()) {
            ptAll = (ss.isSearchAll() && ss.getAllSearch() != null ? Pattern.compile(ss.getAllSearch(), flags) : null);
        } else {
            ptSection = (section == null) ? null : Pattern.compile(section, flags);
            ptKey = (option == null) ? null : Pattern.compile(option, flags);
            ptVal = (val == null) ? null : Pattern.compile(val, flags);
            ptName = (objName == null) ? null : Pattern.compile(objName, flags);

        }
    }

    /**
     *
     * @param cfgObj
     * @param props
     * @param checkNames
     * @return if object matches, return non-null value. If no kvp matched, returns
     *         empty kvp. Otherwise return non-empty kvp
     */
    public MatchedKVPs matchConfigObject(CfgObject cfgObj, IKeyValueProperties props, boolean checkNames) {

        if (ptSection == null && ptKey == null && ptVal == null && ptName == null && ptAll == null) {
            logger.debug("** No criteria; object added");

            return null; // no search parameters means we return all objects
        } else {
            MatchedKVPs kv = new MatchedKVPs();
            boolean nameMatched = false;
            boolean sectionMatched;
            boolean keyMatched = false;
            boolean valMatched = false;

            boolean otherNonNull = ptSection != null || ptKey != null || ptVal != null;

            // matching object name(s)
            if (checkNames) {
                if (ptAll != null) {
                    for (String string : props.getName(cfgObj)) {
                        if (matching(ptAll, string)) {
                            nameMatched = true;
                            break;
                        }
                    }
                } else {
                    if (ptName != null) {
                        for (String string : props.getName(cfgObj)) {
                            logger.trace("checking name of obj[" + cfgObj.getObjectType() + "] path["
                                    + cfgObj.getObjectPath() + "] name[" + string + "]");
                            if (matching(ptName, string)) {
                                logger.debug("Name [" + string + "] matched against " + ptName);
                                nameMatched = true;

                            }
                        }
                        if (!nameMatched) { // name specified but none matches
                            logger.debug("** name specified but not matched. Object ignored");

                            return null;
                        }
                    }
                }
            }
            // boolean shouldContinue=( ptName==null || nameMatched);

            if (ptAll != null || ((ptName == null || nameMatched) && otherNonNull)) {
                KeyValueCollection options;
                options = props.getProperties(cfgObj);
                String sectionName;

                if (options == null) {
                    if (ptAll == null || otherNonNull) {
                        logger.debug("** no options. Object ignored");
                        return null; // rule 3)
                    }
                } else {
                    Enumeration<KeyValuePair> enumeration = options.getEnumeration();
                    KeyValuePair el;

                    while (enumeration.hasMoreElements()) {
                        sectionMatched = false;
                        el = enumeration.nextElement();
                        sectionName = el.getStringKey();
                        if (ptAll != null) {
                            if (matching(ptAll, sectionName)) {

                                sectionMatched = true;
                            }
                        } else if (ptSection != null) {
                            if (matching(ptSection, sectionName)) {
                                logger.debug("Section [" + sectionName + "] matched against " + ptSection);
                                sectionMatched = true;
                            } else {
                                continue;
                            }
                        }

                        KeyValueCollection addedValues = new KeyValueCollection();
                        Object value = el.getValue();
                        if (value != null) {
                            if (value instanceof KeyValueCollection) {
                                KeyValueCollection sectionValues = (KeyValueCollection) value;
                                Enumeration<KeyValuePair> optVal = sectionValues.getEnumeration();
                                KeyValuePair theOpt;
                                while (optVal.hasMoreElements()) {
                                    keyMatched = false;
                                    valMatched = false;

                                    theOpt = optVal.nextElement();

                                    if (ptAll != null) {
                                        if (matching(ptAll, theOpt.getStringKey())) {
                                            keyMatched = true;
                                        }
                                        if (matching(ptAll, theOpt.getStringValue())) {
                                            valMatched = true;
                                        }
                                    } else {
                                        if (ptKey != null) {
                                            if (matching(ptKey, theOpt.getStringKey())) {
                                                keyMatched = true;
                                            }
                                        }
                                        if (ptVal != null) {
                                            if (matching(ptVal, theOpt.getStringValue())) {
                                                valMatched = true;
                                            }
                                        }
                                    }
                                    if (keyMatched || valMatched) {
                                        logger.debug("sect[" + sectionName + "] km[" + keyMatched + "] vm[" + valMatched
                                                + "] key[" + theOpt.getStringKey() + "] val[" + theOpt.getStringValue()
                                                + "]");

                                        if (ptAll != null) {
                                            addedValues.addPair(theOpt);
                                        } else {
                                            int paramsRequested = numTrue(ptName != null, ptSection != null,
                                                    ptKey != null, ptVal != null);
                                            int paramsFound = numTrue(nameMatched, sectionMatched, keyMatched,
                                                    valMatched);
                                            if (paramsFound >= paramsRequested) {
                                                addedValues.addPair(theOpt);
                                            }
                                        }
                                    }
                                }
                            } else {
                                logger.info("value [" + value + "] is of type " + value.getClass() + " obj: " + cfgObj);
                                // if (ptVal != null) {
                                // if (matching(ptVal, value.toString())) {
                                // valMatched = true; // !!!!!
                                // addedValues.addPair(el);
                                //
                                // }
                                // }
                            }
                        }
                        if (!addedValues.isEmpty() || (((ptAll != null)
                                && numTrue(nameMatched, sectionMatched, keyMatched, valMatched) > 0)
                                || ((ptAll == null)
                                        && numTrue(nameMatched, sectionMatched, keyMatched, valMatched) >= numTrue(
                                                ptName != null, ptSection != null, ptKey != null, ptVal != null)))) {
                                kv.addValues(sectionName, addedValues, el);
                        }
                    }
                }
            }
            if (ptAll != null) {
                logger.debug(" ** all **  match " + ((nameMatched || !kv.getMatchedKVPs().isEmpty()) ? "" : "NOT") + " found, obj "
                        + props.getName(cfgObj));
                return (nameMatched || !kv.getMatchedKVPs().isEmpty()) ? kv : null;
            } else {
                MatchedKVPs ret = (!kv.getMatchedKVPs().isEmpty() || (ptName != null && nameMatched
                        && numTrue(ptSection != null, ptKey != null, ptVal != null) == 0)) ? kv : null;

                logger.debug(" ** match " + ((ret == null) ? "NOT" : "") + " found, obj " + props.getName(cfgObj));

                return ret;
            }
        }

    }

}
