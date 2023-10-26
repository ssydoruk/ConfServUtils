package com.ssydoruk.confservutils;

import com.genesyslab.platform.commons.collections.KeyValueCollection;

public class Misc {
	public static KeyValueCollection getSection(KeyValueCollection sections, String section) {
		KeyValueCollection list = sections.getList(section);
		if (list == null) {
			list = new KeyValueCollection();
			sections.addList(section, list);
		}
		return list;
	}
}
