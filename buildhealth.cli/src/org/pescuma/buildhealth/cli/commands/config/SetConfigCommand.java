package org.pescuma.buildhealth.cli.commands.config;

import io.airlift.command.Arguments;
import io.airlift.command.Command;
import io.airlift.command.ParseException;

import java.util.List;

import org.pescuma.buildhealth.cli.BuildHealthCliCommand;
import org.pescuma.buildhealth.prefs.Preferences;

@Command(name = "set", description = "Set one report preference")
public class SetConfigCommand extends BuildHealthCliCommand {
	
	@Arguments(title = "keyValue", description = "key name(s) = value", required = true)
	public List<String> args;
	
	@Override
	public void execute() {
		int size = args.size();
		if (size < 3 || !"=".equals(args.get(size - 2)))
			throw new ParseException("The arguments should be a key name(s) = value");
		
		Preferences prefs = buildHealth.getPreferences();
		for (int i = 0; i < size - 3; i++)
			prefs = prefs.child(args.get(i));
		
		prefs.set(args.get(size - 3), args.get(size - 1));
	}
}
