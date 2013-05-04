package org.pescuma.buildhealth.cli;

import static java.util.Arrays.*;
import io.airlift.command.Cli;
import io.airlift.command.Cli.CliBuilder;
import io.airlift.command.Help;
import io.airlift.command.ParseException;
import io.airlift.command.SuggestCommand;

import org.pescuma.buildhealth.cli.commands.ReportCommand;
import org.pescuma.buildhealth.cli.commands.StartNewBuildCommand;
import org.pescuma.buildhealth.cli.commands.add.CLOCExtractorCommand;
import org.pescuma.buildhealth.cli.commands.add.DiskUsageExtractorCommand;
import org.pescuma.buildhealth.cli.commands.add.coverage.DotCoverExtractorCommand;
import org.pescuma.buildhealth.cli.commands.add.coverage.EmmaExtractorCommand;
import org.pescuma.buildhealth.cli.commands.add.coverage.JacocoExtractorCommand;
import org.pescuma.buildhealth.cli.commands.add.japex.JapexExtractorCommand;
import org.pescuma.buildhealth.cli.commands.add.staticanalysis.CPDExtractorCommand;
import org.pescuma.buildhealth.cli.commands.add.staticanalysis.FindBugsExtractorCommand;
import org.pescuma.buildhealth.cli.commands.add.staticanalysis.PMDExtractorCommand;
import org.pescuma.buildhealth.cli.commands.add.unittest.AUnitExtractorCommand;
import org.pescuma.buildhealth.cli.commands.add.unittest.BoostTestExtractorCommand;
import org.pescuma.buildhealth.cli.commands.add.unittest.CppTestExtractorCommand;
import org.pescuma.buildhealth.cli.commands.add.unittest.CppUnitExtractorCommand;
import org.pescuma.buildhealth.cli.commands.add.unittest.FPCUnitExtractorCommand;
import org.pescuma.buildhealth.cli.commands.add.unittest.JUnitExtractorCommand;
import org.pescuma.buildhealth.cli.commands.add.unittest.MNMLSTCUnitTestExtractorCommand;
import org.pescuma.buildhealth.cli.commands.add.unittest.MSTestExtractorCommand;
import org.pescuma.buildhealth.cli.commands.add.unittest.NUnitExtractorCommand;
import org.pescuma.buildhealth.cli.commands.add.unittest.PHPUnitExtractorCommand;
import org.pescuma.buildhealth.cli.commands.add.unittest.TusarExtractorCommand;
import org.pescuma.buildhealth.cli.commands.compute.LOCComputerCommand;
import org.pescuma.buildhealth.cli.commands.compute.staticanalysis.TasksExtractorCommand;
import org.pescuma.buildhealth.cli.commands.config.ListConfigCommand;
import org.pescuma.buildhealth.cli.commands.config.SetConfigCommand;

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
						EmmaExtractorCommand.class, JacocoExtractorCommand.class, DotCoverExtractorCommand.class, //
						PMDExtractorCommand.class, CPDExtractorCommand.class, FindBugsExtractorCommand.class, //
						JapexExtractorCommand.class, //
						DiskUsageExtractorCommand.class, CLOCExtractorCommand.class);
		
		builder.withGroup("compute").withDescription("Compute new information and add it to the current build")
				.withDefaultCommand(ComputeGroupHelp.class) //
				.withCommands(LOCComputerCommand.class, TasksExtractorCommand.class);
		
		builder.withGroup("config").withDescription("Configure your report preferences")
				.withDefaultCommand(ConfigGroupHelp.class) //
				.withCommands(SetConfigCommand.class, ListConfigCommand.class);
		
		Cli<Runnable> parser = builder.build();
		
		Runnable command = null;
		try {
			
			command = parser.parse(args);
			
			command.run();
			
		} catch (ParseException e) {
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append(e.getMessage()).append("\n\n");
			Help.help(parser.getMetadata(), asList(args), stringBuilder);
			System.err.println(stringBuilder.toString());
			System.exit(-1);
		}
	}
	
}
