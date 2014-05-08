package org.pescuma.buildhealth.cli.commands;

import io.airlift.command.Cli;
import io.airlift.command.ParseException;

import java.util.ArrayList;
import java.util.List;

import org.pescuma.buildhealth.cli.BuildHealthCli;
import org.pescuma.buildhealth.cli.BuildHealthCliCommand;
import org.pescuma.buildhealth.core.BuildHealth;

class CliUtils {
	
	public static String[] parseCommand(String line) {
		if (line.isEmpty())
			return null;
		
		List<String> result = new ArrayList<String>();
		
		boolean insideQuotes = false;
		StringBuilder arg = new StringBuilder();
		
		for (int i = 0; i < line.length(); i++) {
			char c = line.charAt(i);
			
			if (c == '\"') {
				String txt = arg.toString();
				arg.setLength(0);
				
				if (insideQuotes || !txt.isEmpty())
					result.add(txt);
				
				insideQuotes = !insideQuotes;
				
			} else if (c == ' ' && !insideQuotes) {
				if (arg.length() > 0) {
					result.add(arg.toString());
					arg.setLength(0);
				}
			} else {
				arg.append(c);
			}
		}
		
		if (arg.length() > 0)
			result.add(arg.toString());
		
		return result.toArray(new String[result.size()]);
	}
	
	public static void executeCommand(BuildHealth buildHealth, String[] cmd) {
		Cli<Runnable> parser = BuildHealthCli.createParser();
		
		try {
			
			Runnable command = parser.parse(cmd);
			
			if (command instanceof BuildHealthCliCommand)
				((BuildHealthCliCommand) command).useExternalBuildHealth(buildHealth);
			
			command.run();
			
		} catch (ParseException e) {
			System.out.println(e.getMessage());
		}
	}
	
}
