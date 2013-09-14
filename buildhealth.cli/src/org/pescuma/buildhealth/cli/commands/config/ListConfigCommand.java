package org.pescuma.buildhealth.cli.commands.config;

import static com.google.common.base.Strings.*;
import static java.lang.Math.*;
import static org.pescuma.buildhealth.core.prefs.BuildHealthPreference.*;
import io.airlift.command.Command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.pescuma.buildhealth.analyser.BuildHealthAnalyser;
import org.pescuma.buildhealth.cli.BuildHealthCliCommand;
import org.pescuma.buildhealth.core.prefs.BuildHealthPreference;
import org.pescuma.buildhealth.notifiers.BuildHealthNotifier;
import org.pescuma.buildhealth.prefs.Preferences;

@Command(name = "list", description = "List report preferences")
public class ListConfigCommand extends BuildHealthCliCommand {
	
	private static final String PREFIX = "    ";
	
	// TODO
	// @Arguments(title = "key", description = "only show keys starting with this value")
	// public List<String> args;
	
	@Override
	public void execute() {
		Preferences prefs = buildHealth.getPreferences();
		
		Set<String[]> keys = new TreeSet<String[]>(getKeyComparator());
		keys.addAll(prefs.getKeys());
		
		StringBuilder out = new StringBuilder();
		
		boolean needNewLine = false;
		
		for (BuildHealthAnalyser analyser : getSortedAnalysers())
			needNewLine = append(out, analyser.getName(), analyser.getPreferences(), needNewLine, keys, prefs);
		
		for (BuildHealthNotifier notifier : getSortedNotifiers())
			needNewLine = append(out, notifier.getName(), notifier.getPreferences(), needNewLine, keys, prefs);
		
		if (needNewLine && !keys.isEmpty())
			out.append("\n");
		
		for (String[] key : keys)
			append(out, prefs, key, "<no default value>", "");
		
		if (out.length() < 1)
			out.append("No preferences set");
		
		System.out.print(out.toString());
	}
	
	private boolean append(StringBuilder out, String name, List<BuildHealthPreference> ps, boolean needNewLine,
			Set<String[]> keys, Preferences prefs) {
		
		if (ps.isEmpty())
			return needNewLine;
		
		boolean hasName = !isNullOrEmpty(name);
		
		if (needNewLine)
			out.append("\n");
		needNewLine = hasName;
		
		String prefix = "";
		if (hasName) {
			out.append(prefix).append(name).append(":\n");
			prefix = PREFIX;
		}
		
		for (BuildHealthPreference pref : ps) {
			for (String[] key : findKeys(keys, pref.getKey())) {
				out.append(prefix);
				append(out, prefs, key, pref.getDefVal(), pref.getDescription());
				keys.remove(key);
			}
		}
		
		return needNewLine;
	}
	
	private List<String[]> findKeys(Set<String[]> keys, String[] key) {
		if (!hasAnyKeyEntry(key))
			return Arrays.<String[]> asList(key);
		
		List<String[]> result = new ArrayList<String[]>();
		
		for (String[] candiate : keys) {
			String[] match = match(candiate, key);
			if (match != null)
				result.add(match);
		}
		
		String[] template = new String[key.length];
		for (int i = 0; i < key.length; i++) {
			String k = key[i];
			
			if (k.startsWith(ANY_VALUE_KEY_PREFIX))
				template[i] = k.substring(ANY_VALUE_KEY_PREFIX.length());
			else
				template[i] = k;
		}
		result.add(template);
		
		return result;
	}
	
	private String[] match(String[] candiate, String[] key) {
		if (candiate.length != key.length)
			return null;
		
		String[] template = new String[key.length];
		
		for (int i = 0; i < key.length; i++) {
			String k = key[i];
			String c = candiate[i];
			
			if (k.startsWith(ANY_VALUE_KEY_PREFIX)) {
				template[i] = c;
				
			} else if (k.equals(c)) {
				template[i] = k;
				
			} else {
				return null;
			}
		}
		
		return template;
	}
	
	private boolean hasAnyKeyEntry(String[] key) {
		for (int i = 0; i < key.length; i++)
			if (key[i].startsWith(ANY_VALUE_KEY_PREFIX))
				return true;
		return false;
	}
	
	private void append(StringBuilder out, Preferences prefs, String[] key, String defVal, String description) {
		for (int i = 0; i < key.length; i++) {
			if (i > 0)
				out.append(" ");
			out.append(key[i]);
		}
		out.append(" = ");
		out.append(get(prefs, key, defVal));
		if (!description.isEmpty())
			out.append(" [").append(description).append("]");
		out.append("\n");
	}
	
	private List<BuildHealthAnalyser> getSortedAnalysers() {
		List<BuildHealthAnalyser> analysers = buildHealth.getAnalysers();
		analysers = sortAnalysers(analysers);
		return analysers;
	}
	
	private List<BuildHealthAnalyser> sortAnalysers(List<BuildHealthAnalyser> analysers) {
		analysers = new ArrayList<BuildHealthAnalyser>(analysers);
		
		Collections.sort(analysers, new Comparator<BuildHealthAnalyser>() {
			@Override
			public int compare(BuildHealthAnalyser o1, BuildHealthAnalyser o2) {
				String n1 = nullToEmpty(o1.getName());
				String n2 = nullToEmpty(o2.getName());
				if (n1.equals(n2))
					return 0;
				if (n1.isEmpty())
					return 1;
				if (n2.isEmpty())
					return -1;
				return n1.compareToIgnoreCase(n2);
			}
		});
		
		return analysers;
	}
	
	private List<BuildHealthNotifier> getSortedNotifiers() {
		List<BuildHealthNotifier> notifiers = buildHealth.getNotifiers();
		notifiers = sortNotifier(notifiers);
		return notifiers;
	}
	
	private List<BuildHealthNotifier> sortNotifier(List<BuildHealthNotifier> notifiers) {
		notifiers = new ArrayList<BuildHealthNotifier>(notifiers);
		
		Collections.sort(notifiers, new Comparator<BuildHealthNotifier>() {
			@Override
			public int compare(BuildHealthNotifier o1, BuildHealthNotifier o2) {
				String n1 = nullToEmpty(o1.getName());
				String n2 = nullToEmpty(o2.getName());
				if (n1.equals(n2))
					return 0;
				if (n1.isEmpty())
					return 1;
				if (n2.isEmpty())
					return -1;
				return n1.compareToIgnoreCase(n2);
			}
		});
		
		return notifiers;
	}
	
	private String get(Preferences prefs, String[] key, String defVal) {
		for (int i = 0; i < key.length - 1; i++)
			prefs = prefs.child(key[i]);
		
		return prefs.get(key[key.length - 1], defVal);
	}
	
	private Comparator<String[]> getKeyComparator() {
		return new Comparator<String[]>() {
			@Override
			public int compare(String[] o1, String[] o2) {
				int size = min(o1.length, o2.length);
				for (int i = 0; i < size; i++) {
					int comp = o1[i].compareTo(o2[i]);
					if (comp != 0)
						return comp;
				}
				return o1.length - o2.length;
			}
		};
	}
}
