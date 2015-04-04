package org.pescuma.buildhealth.cli.commands;

import static com.google.common.base.MoreObjects.*;
import static com.google.common.collect.Maps.*;
import static org.pescuma.buildhealth.cli.commands.CliUtils.*;
import io.airlift.airline.Command;
import io.airlift.airline.UsagePrinter;
import io.airlift.airline.model.CommandGroupMetadata;
import io.airlift.airline.model.CommandMetadata;
import io.airlift.airline.model.GlobalMetadata;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;

import org.pescuma.buildhealth.cli.BuildHealthCliCommand;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

@Command(name = "console", description = "Open a console to run multiple commands")
public class ConsoleCommand extends BuildHealthCliCommand {
	
	@Inject
	public GlobalMetadata global;
	
	@Override
	protected void execute() {
		System.out.println("type quit to quit, ? for help");
		try {
			
			while (true) {
				String line = promptAndReadLine();
				
				if (line == null)
					break;
				
				if ("quit".equals(line))
					break;
				
				if ("console".equals(line)) {
					System.out.println("How meta are we?");
					continue;
				}
				
				if ("?".equals(line) || "help".equals(line)) {
					help();
					continue;
				}
				
				String[] cmd = parseCommand(line);
				if (cmd != null)
					executeCommand(buildHealth, cmd);
			}
			
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private void help() {
		StringBuilder builder = new StringBuilder();
		UsagePrinter out = new UsagePrinter(builder, 79);
		
		Map<String, String> commands = newTreeMap();
		for (CommandMetadata commandMetadata : global.getDefaultGroupCommands())
			if (!commandMetadata.isHidden())
				commands.put(commandMetadata.getName(), commandMetadata.getDescription());
		for (CommandGroupMetadata commandGroupMetadata : global.getCommandGroups())
			commands.put(commandGroupMetadata.getName(), commandGroupMetadata.getDescription());
		commands.put("quit", "Quit buildhealth");
		commands.remove("suggest");
		commands.remove("console");
		
		out.appendTable(Iterables.transform(commands.entrySet(),
				new Function<Entry<String, String>, Iterable<String>>() {
					@Override
					public Iterable<String> apply(Entry<String, String> entry) {
						return ImmutableList.of(entry.getKey(), firstNonNull(entry.getValue(), ""));
					}
				}));
		
		System.out.print(builder.toString());
	}
	
	private String promptAndReadLine() throws IOException {
		int available = System.in.available();
		if (available > 0)
			System.in.skip(available);
		
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		
		System.out.println();
		System.out.print("> ");
		
		String result = in.readLine();
		if (result != null)
			result = result.trim();
		
		return result;
	}
	
}
