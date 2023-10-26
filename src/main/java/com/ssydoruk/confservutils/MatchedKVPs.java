package com.ssydoruk.confservutils;

import com.genesyslab.platform.commons.collections.KeyValueCollection;
import com.genesyslab.platform.commons.collections.KeyValuePair;

import java.util.ArrayList;
import java.util.Arrays;

public class MatchedKVPs {
	public MatchedKVPs() {
		matchedKVPs = new KeyValueCollection();
		matchedSections = new KeyValueCollection();
	}

	public KeyValueCollection getMatchedKVPs() {
		return matchedKVPs;
	}

//    public ArrayList<KeyValuePair> getMatchedSections() {
//        return matchedSections;
//    }
	public KeyValueCollection getMatchedSections() {
		return matchedSections;
	}

	private KeyValueCollection matchedKVPs;
//    private ArrayList<KeyValuePair > matchedSections;
	private KeyValueCollection matchedSections;

	public void addValues(String sectionName, KeyValueCollection addedValues, KeyValuePair el) {
		matchedSections.add(el);
		KeyValueCollection list = getMatchedKVPs().getList(sectionName);
		if (list == null) {
			list = new KeyValueCollection();
			getMatchedKVPs().addList(sectionName, list);
		}
		list.addAll(Arrays.asList(addedValues.toArray()));

	}
}
