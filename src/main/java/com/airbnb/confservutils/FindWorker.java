/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.airbnb.confservutils;

import Utils.Pair;
import static Utils.StringUtils.matching;
import com.genesyslab.platform.applicationblocks.com.CfgObject;
import com.genesyslab.platform.commons.collections.KeyValueCollection;
import com.genesyslab.platform.commons.collections.KeyValuePair;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.regex.Pattern;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author stepan_sydoruk
 */
public class FindWorker {

    private final ISearchSettings ss;

    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger();

    public boolean isFullOutputSelected() {
        return ss.isFullOutputSelected();
    }

    public FindWorker(ISearchSettings ss) {
        this.ss = ss;
        initSearch();
    }

    private Pattern ptAll = null;
    private Pattern ptSection = null;
    private Pattern ptKey = null;
    private Pattern ptVal = null;
    private Pattern ptName = null;

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
     * @return if object matches, return non-null value. If no kvp matched,
     * returns empty kvp. Otherwise return non-empty kvp
     */
    public KeyValueCollection matchConfigObject(CfgObject cfgObj, IKeyValueProperties props, boolean checkNames) {
        KeyValueCollection kv = new KeyValueCollection();

        if (ptSection == null
                && ptKey == null
                && ptVal == null
                && ptName == null
                && ptAll == null) {
            logger.debug("** No criteria; object added");

            return new KeyValueCollection(); // no search parameters means we return all objects
        } else {
            boolean nameMatched = false;
            boolean sectionMatched = false;
            boolean includeKVP = false;
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

            if (ptAll != null || otherNonNull) {
                KeyValueCollection options;
                options = props.getProperties(cfgObj);
                String sectionFound = null;

                if (options == null && (ptAll == null || otherNonNull)) {
                    logger.debug("** no options. Object ignored");
                    return null; // rule 3)
                } else {
                    Enumeration<KeyValuePair> enumeration = options.getEnumeration();
                    KeyValuePair el;

                    while (enumeration.hasMoreElements()) {
                        el = enumeration.nextElement();

                        if (ptAll != null) {
                            if (matching(ptAll, el.getStringKey())) {
                                sectionFound = el.getStringKey();
                                sectionMatched = true;
                            }
                        } else if (ptSection != null) {
                            if (matching(ptSection, el.getStringKey())) {
                                logger.debug("Section [" + el.getStringKey() + "] matched against " + ptSection);

                                sectionFound = el.getStringKey();
                                sectionMatched = true;
                            }
                        }

                        KeyValueCollection addedValues = new KeyValueCollection();
                        Object value = el.getValue();
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
                                    logger.debug("sect[" + el.getStringKey() + "] km[" + keyMatched + "] vm[" + valMatched + "] key[" + theOpt.getStringKey() + "] val[" + theOpt.getStringValue() + "]");

                                    if (ptAll != null) {
                                        addedValues.addPair(theOpt);
                                    } else {
                                        int paramsRequested = numTrue(ptName != null, ptSection != null, ptKey != null, ptVal != null);
                                        int paramsFound = numTrue(nameMatched, sectionMatched, keyMatched, valMatched);
                                        if (paramsFound >= paramsRequested) {
                                            addedValues.addPair(theOpt);
                                        }
                                    }
                                }
                            }
                        } else {
                            logger.info("value [" + value + "] is of type " + value.getClass() + " obj: " + cfgObj);
//                            if (ptVal != null) {
//                                if (matching(ptVal, value.toString())) {
//                                    valMatched = true; // !!!!!
//                                    addedValues.addPair(el);
//
//                                }
//                            }
                        }
                        if (!addedValues.isEmpty() || (sectionFound != null && numTrue(nameMatched, sectionMatched, keyMatched, valMatched) >= numTrue(ptName != null, ptSection != null, ptKey != null, ptVal != null))) {
                            String sect = (sectionFound != null) ? sectionFound : el.getStringKey();
                            KeyValueCollection list = kv.getList(sect);
                            if (list == null) {
                                list = new KeyValueCollection();
                                kv.addList(sect, list);
                            }
                            list.addAll(Arrays.asList(addedValues.toArray()));
//                                    kv.addObject(el.getStringKey(), addedValues);
                        }
                    }
                }
            }
            if (ptAll != null) {
                logger.debug(" ** all **  match " + ((nameMatched || sectionMatched || valMatched || keyMatched) ? "" : "NOT") + " found");
                return (nameMatched || sectionMatched || valMatched || keyMatched) ? kv : null;
            } else {
                logger.debug(" ** match " + ((kv.isEmpty() || (ptName != null && !nameMatched)) ? "NOT" : "") + " found");
                return (kv.isEmpty() || (ptName != null && !nameMatched)) ? null : kv;
            }
        }

    }

    static int numTrue(boolean... bools) {
        int ret = 0;

        // using for each loop to display contents of a 
        for (boolean b : bools) {
            ret += (b ? 1 : 0);
        }
        return ret;

    }

}
