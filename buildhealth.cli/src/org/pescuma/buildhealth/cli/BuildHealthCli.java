package org.pescuma.buildhealth.cli;

import static java.util.Arrays.*;
import io.airlift.command.Cli;
import io.airlift.command.Cli.CliBuilder;
import io.airlift.command.Help;
import io.airlift.command.ParseException;
import io.airlift.command.SuggestCommand;

import org.pescuma.buildhealth.cli.commands.JUnitExtractorCommand;
import org.pescuma.buildhealth.cli.commands.StartNewBuildCommand;

public class BuildHealthCli {
	
	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		CliBuilder<Runnable> builder = Cli.<Runnable> builder("buildhealth") //
				.withDescription("check the quality of your builds") //
				.withDefaultCommand(Help.class) //
				.withCommands(Help.class, SuggestCommand.class, StartNewBuildCommand.class);
		
		builder.withGroup("add") //
				.withDescription("Add information to the current build") //
				.withDefaultCommand(AddGroupHelp.class) //
				.withCommands(JUnitExtractorCommand.class);
		
		Cli<Runnable> parser = builder.build();
		
		try {
			
			Runnable command = parser.parse(args);
			command.run();
			
		} catch (ParseException e) {
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append(e.getMessage()).append("\n\n");
			Help.help(parser.getMetadata(), asList(args), stringBuilder);
			System.err.println(stringBuilder.toString());
		}
	}
	
}
