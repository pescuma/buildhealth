package org.pescuma.buildhealth.extractor.coverage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.pescuma.buildhealth.core.BuildData;

class PlacesTracker {
	
	private final BuildData data;
	private final String language;
	private final String framework;
	private final List<String> current = new ArrayList<String>();
	private final Set<String> added = new HashSet<String>();
	
	PlacesTracker(BuildData data, String language, String framework) {
		this.data = data;
		this.language = language;
		this.framework = framework;
	}
	
	void goInto(String placeType, String... names) {
		for (String name : names) {
			current.add(name);
			
			String full = StringUtils.join(current, "\n");
			if (!added.contains(full)) {
				added.add(full);
				addToData(0, placeType, "type");
			}
		}
	}
	
	int getBookmark() {
		return current.size();
	}
	
	void goBackTo(int bookmark) {
		while (current.size() > bookmark)
			current.remove(current.size() - 1);
	}
	
	void addToData(double value, String type, String what) {
		List<String> infos = new ArrayList<String>();
		infos.add("Coverage");
		infos.add(language);
		infos.add(framework);
		infos.add(what);
		infos.add(type);
		infos.addAll(current);
		data.add(value, infos.toArray(new String[infos.size()]));
	}
}