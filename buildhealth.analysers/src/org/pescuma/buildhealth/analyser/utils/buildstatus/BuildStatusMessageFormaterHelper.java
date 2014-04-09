package org.pescuma.buildhealth.analyser.utils.buildstatus;

import static java.util.Arrays.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class BuildStatusMessageFormaterHelper {
	
	public static String prefKeyWithSeverityToMessage(String[] prefKey) {
		if (prefKey.length < 2)
			return prefKeyToMessage(prefKey);
		
		List<String> prefs = new ArrayList<String>(asList(prefKey));
		
		String severity = prefs.remove(prefs.size() - 1);
		return prefKeyToMessage(prefs) + " with severity " + severity;
	}
	
	public static String prefKeyToMessage(String[] prefKey) {
		return prefKeyToMessage(asList(prefKey));
	}
	
	public static String prefKeyToMessage(Collection<String> prefKey) {
		if (prefKey.size() < 1)
			return "";
		
		Deque<String> pieces = new LinkedList<String>(prefKey);
		pieces.removeFirst();
		
		StringBuilder result = new StringBuilder();
		
		if (!pieces.isEmpty()) {
			String val = pieces.removeFirst();
			if (!val.equals("*"))
				result.append(" in ").append(val);
		}
		
		if (!pieces.isEmpty()) {
			String val = pieces.removeFirst();
			if (!val.equals("*"))
				result.append(" measured by ").append(val);
		}
		
		if (!pieces.isEmpty())
			result.append(" in ").append(StringUtils.join(pieces, "."));
		
		return result.toString();
	}
	
}
