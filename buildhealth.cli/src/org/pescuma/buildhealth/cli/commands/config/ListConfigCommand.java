package org.pescuma.buildhealth.cli.commands.config;

import static java.lang.Math.*;
import static org.pescuma.buildhealth.core.prefs.BuildHealthPreference.*;
import io.airlift.command.Arguments;
import io.airlift.command.Command;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.pescuma.buildhealth.cli.BuildHealthCliCommand;
import org.pescuma.buildhealth.core.prefs.BuildHealthPreference;
import org.pescuma.buildhealth.prefs.Preferences;

@Command(name = "list", description = "List report preferences")
public class ListConfigCommand extends BuildHealthCliCommand {
	
	@Arguments(title = "key", description = "only show keys starting with this value")
	public List<String> args;
	
	@Override
	public void execute() {
		Preferences prefs = buildHealth.getPreferences();
		List<BuildHealthPreference> knownPreferences = buildHealth.getKnownPreferences();
		
		Map<String[], BuildHealthPreference> keys = new TreeMap<String[], BuildHealthPreference>(getKeyComparator());
		for (BuildHealthPreference kp : knownPreferences)
			keys.put(kp.getKey(), kp);
		for (String[] key : prefs.getKeys())
			keys.put(key, findKnownPreference(knownPreferences, key));
		
		StringBuilder out = new StringBuilder();
		
		String lastRoot = null;
		for (Map.Entry<String[], BuildHealthPreference> entry : keys.entrySet()) {
			String[] key = entry.getKey();
			BuildHealthPreference bhp = entry.getValue();
			
			if (!startsWith(key, args))
				continue;
			
			if (lastRoot != null && !key[0].equals(lastRoot))
				out.append("\n");
			lastRoot = key[0];
			
			if (bhp == null)
				append(out, prefs, key, "<no default value>", "");
			else
				append(out, prefs, removeAnyKeyEntryPrefix(key), bhp.getDefVal(), bhp.getDescription());
		}
		
		if (out.length() < 1)
			out.append("No preferences set");
		
		System.out.print(out.toString());
	}
	
	private boolean startsWith(String[] key, List<String> prefix) {
		if (prefix == null)
			return true;
		
		int size = prefix.size();
		if (size < 1)
			return true;
		
		if (key.length < size)
			return false;
		
		for (int i = 0; i < size; i++)
			if (!prefix.get(i).equals(key[i]))
				return false;
		
		return true;
	}
	
	private BuildHealthPreference findKnownPreference(List<BuildHealthPreference> knownPreferences, String[] key) {
		for (BuildHealthPreference kp : knownPreferences) {
			if (!hasAnyKeyEntry(kp.getKey()))
				continue;
			
			if (matches(kp.getKey(), key))
				return kp;
		}
		return null;
	}
	
	private boolean matches(String[] template, String[] key) {
		if (template.length != key.length)
			return false;
		
		for (int i = 0; i < key.length; i++) {
			String t = template[i];
			String k = key[i];
			
			if (t.startsWith(ANY_VALUE_KEY_PREFIX)) {
				continue;
				
			} else if (k.equals(t)) {
				continue;
				
			} else {
				return false;
			}
		}
		
		return true;
	}
	
	private boolean hasAnyKeyEntry(String[] key) {
		for (int i = 0; i < key.length; i++)
			if (key[i].startsWith(ANY_VALUE_KEY_PREFIX))
				return true;
		return false;
	}
	
	private String[] removeAnyKeyEntryPrefix(String[] key) {
		if (!hasAnyKeyEntry(key))
			return key;
		
		String[] result = Arrays.copyOf(key, key.length);
		
		for (int i = 0; i < result.length; i++) {
			String k = result[i];
			if (k.startsWith(ANY_VALUE_KEY_PREFIX))
				result[i] = k.substring(ANY_VALUE_KEY_PREFIX.length());
		}
		
		return result;
	}
	
	private void append(StringBuilder out, Preferences prefs, String[] key, String defVal, String description) {
		for (int i = 0; i < key.length; i++) {
			if (i > 0)
				out.append(" ");
			
			boolean hasSpace = key[i].indexOf(' ') >= 0;
			if (hasSpace)
				out.append("\"");
			out.append(key[i]);
			if (hasSpace)
				out.append("\"");
		}
		out.append(" = ");
		out.append(get(prefs, key, defVal));
		if (!description.isEmpty())
			out.append("    [").append(description).append("]");
		out.append("\n");
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
					boolean o1Any = o1[i].startsWith(ANY_VALUE_KEY_PREFIX);
					boolean o2Any = o2[i].startsWith(ANY_VALUE_KEY_PREFIX);
					if (o1Any != o2Any)
						return o1Any ? -1 : 1;
					
					int comp = o1[i].compareTo(o2[i]);
					if (comp != 0)
						return comp;
				}
				return o1.length - o2.length;
			}
		};
	}
}
