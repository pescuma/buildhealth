package org.pescuma.buildhealth.cli.commands.config;

import static java.lang.Math.*;
import io.airlift.command.Arguments;
import io.airlift.command.Command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.pescuma.buildhealth.cli.BuildHealthCliCommand;
import org.pescuma.buildhealth.prefs.Preferences;

@Command(name = "list", description = "List report preferences")
public class ListConfigCommand extends BuildHealthCliCommand {
	
	@Arguments(title = "key", description = "only show keys starting with this value")
	public List<String> args;
	
	@Override
	public void execute() {
		Preferences prefs = buildHealth.getPreferences();
		List<String[]> keys = prefs.getKeys();
		keys = sort(keys);
		
		StringBuilder out = new StringBuilder();
		out.append("Preferences:\n");
		for (String[] key : keys) {
			out.append("  ");
			for (int i = 0; i < key.length; i++) {
				if (i > 0)
					out.append(" ");
				out.append(key[i]);
			}
			out.append(" = ");
			out.append(get(prefs, key, ""));
			out.append("\n");
		}
		System.out.println(out.toString());
	}
	
	private String get(Preferences prefs, String[] key, String string) {
		for (int i = 0; i < key.length - 1; i++)
			prefs = prefs.child(key[i]);
		
		return prefs.get(key[key.length - 1], "");
	}
	
	private List<String[]> sort(List<String[]> keys) {
		keys = new ArrayList<String[]>(keys);
		
		Collections.sort(keys, new Comparator<String[]>() {
			@Override
			public int compare(String[] o1, String[] o2) {
				int size = min(o1.length, o2.length);
				for (int i = 0; i < size; i++) {
					int comp = o1[i].compareToIgnoreCase(o2[i]);
					if (comp != 0)
						return 0;
				}
				return o1.length - o2.length;
			}
		});
		
		return keys;
	}
}
