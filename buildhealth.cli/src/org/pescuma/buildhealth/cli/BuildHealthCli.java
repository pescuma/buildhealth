package org.pescuma.buildhealth.cli;

import static java.util.Arrays.*;
import io.airlift.command.Cli;
import io.airlift.command.Cli.CliBuilder;
import io.airlift.command.Help;
import io.airlift.command.ParseException;
import io.airlift.command.SuggestCommand;

import org.pescuma.buildhealth.cli.commands.CLOCExtractorCommand;
import org.pescuma.buildhealth.cli.commands.DiskUsageExtractorCommand;
import org.pescuma.buildhealth.cli.commands.ReportCommand;
import org.pescuma.buildhealth.cli.commands.StartNewBuildCommand;
import org.pescuma.buildhealth.cli.commands.unittest.AUnitExtractorCommand;
import org.pescuma.buildhealth.cli.commands.unittest.BoostTestExtractorCommand;
import org.pescuma.buildhealth.cli.commands.unittest.CppTestExtractorCommand;
import org.pescuma.buildhealth.cli.commands.unittest.CppUnitExtractorCommand;
import org.pescuma.buildhealth.cli.commands.unittest.FPCUnitExtractorCommand;
import org.pescuma.buildhealth.cli.commands.unittest.JUnitExtractorCommand;
import org.pescuma.buildhealth.cli.commands.unittest.MNMLSTCUnitTestExtractorCommand;
import org.pescuma.buildhealth.cli.commands.unittest.MSTestExtractorCommand;
import org.pescuma.buildhealth.cli.commands.unittest.NUnitExtractorCommand;
import org.pescuma.buildhealth.cli.commands.unittest.PHPUnitExtractorCommand;
import org.pescuma.buildhealth.cli.commands.unittest.TusarExtractorCommand;

public class BuildHealthCli {
	
	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		CliBuilder<Runnable> builder = Cli.<Runnable> builder("buildhealth") //
				.withDescription("check the quality of your builds") //
				.withDefaultCommand(Help.class) //
				.withCommands(Help.class, SuggestCommand.class, StartNewBuildCommand.class, ReportCommand.class);
		
		builder.withGroup("add").withDescription("Add information to the current build")
				.withDefaultCommand(AddGroupHelp.class)
				.withCommands(AUnitExtractorCommand.class, BoostTestExtractorCommand.class, //
						CppTestExtractorCommand.class, CppUnitExtractorCommand.class, //
						FPCUnitExtractorCommand.class, JUnitExtractorCommand.class, //
						MNMLSTCUnitTestExtractorCommand.class, //
						MSTestExtractorCommand.class, //
						NUnitExtractorCommand.class, PHPUnitExtractorCommand.class, //
						TusarExtractorCommand.class, //
						DiskUsageExtractorCommand.class, CLOCExtractorCommand.class);
		
		Cli<Runnable> parser = builder.build();
		
		Runnable command = null;
		try {
			
			command = parser.parse(args);
			
		} catch (ParseException e) {
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append(e.getMessage()).append("\n\n");
			Help.help(parser.getMetadata(), asList(args), stringBuilder);
			System.err.println(stringBuilder.toString());
			System.exit(-1);
		}
		
		command.run();
	}
	
}
