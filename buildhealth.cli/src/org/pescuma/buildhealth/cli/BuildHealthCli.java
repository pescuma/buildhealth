package org.pescuma.buildhealth.cli;

import static java.util.Arrays.*;
import io.airlift.command.Cli;
import io.airlift.command.Cli.CliBuilder;
import io.airlift.command.Help;
import io.airlift.command.ParseException;
import io.airlift.command.SuggestCommand;

import org.pescuma.buildhealth.cli.commands.AUnitExtractorCommand;
import org.pescuma.buildhealth.cli.commands.BoostTestExtractorCommand;
import org.pescuma.buildhealth.cli.commands.CppTestExtractorCommand;
import org.pescuma.buildhealth.cli.commands.CppUnitExtractorCommand;
import org.pescuma.buildhealth.cli.commands.FPCUnitExtractorCommand;
import org.pescuma.buildhealth.cli.commands.JUnitExtractorCommand;
import org.pescuma.buildhealth.cli.commands.MNMLSTCUnitTestExtractorCommand;
import org.pescuma.buildhealth.cli.commands.MSTestExtractorCommand;
import org.pescuma.buildhealth.cli.commands.NUnitExtractorCommand;
import org.pescuma.buildhealth.cli.commands.PHPUnitExtractorCommand;
import org.pescuma.buildhealth.cli.commands.StartNewBuildCommand;
import org.pescuma.buildhealth.cli.commands.TusarExtractorCommand;

public class BuildHealthCli {
	
	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		CliBuilder<Runnable> builder = Cli.<Runnable> builder("buildhealth") //
				.withDescription("check the quality of your builds") //
				.withDefaultCommand(Help.class) //
				.withCommands(Help.class, SuggestCommand.class, StartNewBuildCommand.class);
		
		builder.withGroup("add").withDescription("Add information to the current build")
				.withDefaultCommand(AddGroupHelp.class)
				.withCommands(AUnitExtractorCommand.class, BoostTestExtractorCommand.class, //
						CppTestExtractorCommand.class, CppUnitExtractorCommand.class, //
						FPCUnitExtractorCommand.class, JUnitExtractorCommand.class, //
						MNMLSTCUnitTestExtractorCommand.class, //
						MSTestExtractorCommand.class, //
						NUnitExtractorCommand.class, PHPUnitExtractorCommand.class, //
						TusarExtractorCommand.class);
		
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
