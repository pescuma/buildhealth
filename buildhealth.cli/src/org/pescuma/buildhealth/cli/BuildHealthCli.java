package org.pescuma.buildhealth.cli;

import io.airlift.airline.Cli;
import io.airlift.airline.Cli.CliBuilder;
import io.airlift.airline.Help;
import io.airlift.airline.ParseException;
import io.airlift.airline.SuggestCommand;

import org.pescuma.buildhealth.cli.commands.ConsoleCommand;
import org.pescuma.buildhealth.cli.commands.NotifyCommand;
import org.pescuma.buildhealth.cli.commands.ReportCommand;
import org.pescuma.buildhealth.cli.commands.ScriptCommand;
import org.pescuma.buildhealth.cli.commands.StartNewBuildCommand;
import org.pescuma.buildhealth.cli.commands.add.CLOCExtractorCommand;
import org.pescuma.buildhealth.cli.commands.add.DiskUsageExtractorCommand;
import org.pescuma.buildhealth.cli.commands.add.coverage.DotCoverExtractorCommand;
import org.pescuma.buildhealth.cli.commands.add.coverage.EmmaExtractorCommand;
import org.pescuma.buildhealth.cli.commands.add.coverage.JacocoExtractorCommand;
import org.pescuma.buildhealth.cli.commands.add.coverage.OpenCoverExtractorCommand;
import org.pescuma.buildhealth.cli.commands.add.coverage.VstestCoverageExtractorCommand;
import org.pescuma.buildhealth.cli.commands.add.japex.JapexExtractorCommand;
import org.pescuma.buildhealth.cli.commands.add.staticanalysis.CPDExtractorCommand;
import org.pescuma.buildhealth.cli.commands.add.staticanalysis.DependencyCheckerExtractorCommand;
import org.pescuma.buildhealth.cli.commands.add.staticanalysis.FindBugsExtractorCommand;
import org.pescuma.buildhealth.cli.commands.add.staticanalysis.FxCopExtractorCommand;
import org.pescuma.buildhealth.cli.commands.add.staticanalysis.GendarmeExtractorCommand;
import org.pescuma.buildhealth.cli.commands.add.staticanalysis.JSXLintExtractorCommand;
import org.pescuma.buildhealth.cli.commands.add.staticanalysis.PMDExtractorCommand;
import org.pescuma.buildhealth.cli.commands.add.staticanalysis.ResharperDupFinderExtractorCommand;
import org.pescuma.buildhealth.cli.commands.add.staticanalysis.ResharperInspectCodeExtractorCommand;
import org.pescuma.buildhealth.cli.commands.add.staticanalysis.StyleCopExtractorCommand;
import org.pescuma.buildhealth.cli.commands.add.tasks.BugsEverywhereExtractorCommand;
import org.pescuma.buildhealth.cli.commands.add.tasks.GitHubIssuesExtractorCommand;
import org.pescuma.buildhealth.cli.commands.add.tasks.TFSWorkItemsExtractorCommand;
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
import org.pescuma.buildhealth.cli.commands.compute.tasks.CodeTasksComputerCommand;
import org.pescuma.buildhealth.cli.commands.config.ListConfigCommand;
import org.pescuma.buildhealth.cli.commands.config.SetConfigCommand;
import org.pescuma.buildhealth.cli.commands.projects.ProjectsFromEclipseExtractorCommand;
import org.pescuma.buildhealth.cli.commands.projects.ProjectsFromVisualStudioExtractorCommand;
import org.pescuma.buildhealth.cli.commands.webserver.ExportCommand;
import org.pescuma.buildhealth.cli.commands.webserver.WebServerCommand;

public class BuildHealthCli {
	
	public static void main(String[] args) {
		Cli<Runnable> parser = createParser();
		
		try {
			
			Runnable command = parser.parse(args);
			command.run();
			
		} catch (ParseException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		}
	}
	
	@SuppressWarnings("unchecked")
	public static Cli<Runnable> createParser() {
		CliBuilder<Runnable> builder = Cli
				.<Runnable> builder("buildhealth")
				.withDescription("check the quality of your builds")
				.withDefaultCommand(Help.class)
				.withCommands(Help.class, SuggestCommand.class, StartNewBuildCommand.class, ReportCommand.class,
						NotifyCommand.class, WebServerCommand.class, ExportCommand.class, ConsoleCommand.class,
						ScriptCommand.class);
		
		builder.withGroup("add")
				.withDescription("Add information to the current build")
				.withDefaultCommand(AddGroupHelp.class)
				.withCommands(
						AUnitExtractorCommand.class,
						BoostTestExtractorCommand.class,
						CppTestExtractorCommand.class,
						CppUnitExtractorCommand.class,
						FPCUnitExtractorCommand.class,
						JUnitExtractorCommand.class,
						MNMLSTCUnitTestExtractorCommand.class,
						MSTestExtractorCommand.class,
						NUnitExtractorCommand.class,
						PHPUnitExtractorCommand.class,
						TusarExtractorCommand.class,
						EmmaExtractorCommand.class,
						JacocoExtractorCommand.class,
						DotCoverExtractorCommand.class,
						OpenCoverExtractorCommand.class,
						VstestCoverageExtractorCommand.class,
						PMDExtractorCommand.class,
						CPDExtractorCommand.class,
						DependencyCheckerExtractorCommand.class,
						ResharperDupFinderExtractorCommand.class,
						ResharperInspectCodeExtractorCommand.class,
						FindBugsExtractorCommand.class,
						GendarmeExtractorCommand.class,
						FxCopExtractorCommand.class,
						StyleCopExtractorCommand.class,
						JSXLintExtractorCommand.class,
						JapexExtractorCommand.class,
						DiskUsageExtractorCommand.class,
						CLOCExtractorCommand.class,
						BugsEverywhereExtractorCommand.class,
						TFSWorkItemsExtractorCommand.class,
						GitHubIssuesExtractorCommand.class,
						// Start of auto generated entries
						org.pescuma.buildhealth.cli.commands.add.staticanalysis.console.AcuCobolConsoleExtractorCommand.class,
						org.pescuma.buildhealth.cli.commands.add.staticanalysis.console.AntJavacConsoleExtractorCommand.class,
						org.pescuma.buildhealth.cli.commands.add.staticanalysis.console.ArmccCompilerConsoleExtractorCommand.class,
						org.pescuma.buildhealth.cli.commands.add.staticanalysis.console.BuckminsterConsoleExtractorCommand.class,
						org.pescuma.buildhealth.cli.commands.add.staticanalysis.console.CoolfluxChessccConsoleExtractorCommand.class,
						org.pescuma.buildhealth.cli.commands.add.staticanalysis.console.GhsMultiConsoleExtractorCommand.class,
						org.pescuma.buildhealth.cli.commands.add.staticanalysis.console.ClangConsoleExtractorCommand.class,
						org.pescuma.buildhealth.cli.commands.add.staticanalysis.console.CodeAnalysisConsoleExtractorCommand.class,
						org.pescuma.buildhealth.cli.commands.add.staticanalysis.console.DiabCConsoleExtractorCommand.class,
						org.pescuma.buildhealth.cli.commands.add.staticanalysis.console.DoxygenConsoleExtractorCommand.class,
						org.pescuma.buildhealth.cli.commands.add.staticanalysis.console.EclipseConsoleExtractorCommand.class,
						org.pescuma.buildhealth.cli.commands.add.staticanalysis.console.ErlcConsoleExtractorCommand.class,
						org.pescuma.buildhealth.cli.commands.add.staticanalysis.console.FlexSDKConsoleExtractorCommand.class,
						org.pescuma.buildhealth.cli.commands.add.staticanalysis.console.GccConsoleExtractorCommand.class,
						org.pescuma.buildhealth.cli.commands.add.staticanalysis.console.Gcc4CompilerConsoleExtractorCommand.class,
						org.pescuma.buildhealth.cli.commands.add.staticanalysis.console.Gcc4LinkerConsoleExtractorCommand.class,
						org.pescuma.buildhealth.cli.commands.add.staticanalysis.console.GnatConsoleExtractorCommand.class,
						org.pescuma.buildhealth.cli.commands.add.staticanalysis.console.GnuFortranConsoleExtractorCommand.class,
						org.pescuma.buildhealth.cli.commands.add.staticanalysis.console.GnuMakeGccConsoleExtractorCommand.class,
						org.pescuma.buildhealth.cli.commands.add.staticanalysis.console.IarConsoleExtractorCommand.class,
						org.pescuma.buildhealth.cli.commands.add.staticanalysis.console.IntelConsoleExtractorCommand.class,
						org.pescuma.buildhealth.cli.commands.add.staticanalysis.console.InvalidsConsoleExtractorCommand.class,
						org.pescuma.buildhealth.cli.commands.add.staticanalysis.console.JavacConsoleExtractorCommand.class,
						org.pescuma.buildhealth.cli.commands.add.staticanalysis.console.JavaDocConsoleExtractorCommand.class,
						org.pescuma.buildhealth.cli.commands.add.staticanalysis.console.MavenConsoleConsoleExtractorCommand.class,
						org.pescuma.buildhealth.cli.commands.add.staticanalysis.console.MsBuildConsoleExtractorCommand.class,
						org.pescuma.buildhealth.cli.commands.add.staticanalysis.console.NagFortranConsoleExtractorCommand.class,
						org.pescuma.buildhealth.cli.commands.add.staticanalysis.console.P4ConsoleExtractorCommand.class,
						org.pescuma.buildhealth.cli.commands.add.staticanalysis.console.Pep8ConsoleExtractorCommand.class,
						org.pescuma.buildhealth.cli.commands.add.staticanalysis.console.PerlCriticConsoleExtractorCommand.class,
						org.pescuma.buildhealth.cli.commands.add.staticanalysis.console.PhpConsoleExtractorCommand.class,
						org.pescuma.buildhealth.cli.commands.add.staticanalysis.console.PyLintConsoleExtractorCommand.class,
						org.pescuma.buildhealth.cli.commands.add.staticanalysis.console.RobocopyConsoleExtractorCommand.class,
						org.pescuma.buildhealth.cli.commands.add.staticanalysis.console.SunCConsoleExtractorCommand.class,
						org.pescuma.buildhealth.cli.commands.add.staticanalysis.console.TiCcsConsoleExtractorCommand.class,
						org.pescuma.buildhealth.cli.commands.add.staticanalysis.console.TnsdlConsoleExtractorCommand.class,
						org.pescuma.buildhealth.cli.commands.add.staticanalysis.console.XlcCompilerConsoleExtractorCommand.class,
						org.pescuma.buildhealth.cli.commands.add.staticanalysis.console.XlcLinkerConsoleExtractorCommand.class,
						org.pescuma.buildhealth.cli.commands.add.staticanalysis.console.YuiCompressorConsoleExtractorCommand.class
				// End of auto generated entries
				);
		
		builder.withGroup("compute")
				.withDescription("Compute new information and add it to the current build")
				.withDefaultCommand(ComputeGroupHelp.class) //
				.withCommands(LOCComputerCommand.class, CodeTasksComputerCommand.class);
		
		builder.withGroup("projects")
				.withDescription("Add projects information to current build")
				.withDefaultCommand(ProjectsGroupHelp.class)
				.withCommands(ProjectsFromEclipseExtractorCommand.class,
						ProjectsFromVisualStudioExtractorCommand.class);
		
		builder.withGroup("config").withDescription("Configure your report preferences")
				.withDefaultCommand(ConfigGroupHelp.class) //
				.withCommands(SetConfigCommand.class, ListConfigCommand.class);
		
		return builder.build();
	}
}
