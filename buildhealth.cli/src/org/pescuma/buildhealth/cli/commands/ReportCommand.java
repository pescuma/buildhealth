package org.pescuma.buildhealth.cli.commands;

import io.airlift.command.Command;

import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.Ansi.Color;
import org.fusesource.jansi.AnsiConsole;
import org.pescuma.buildhealth.analyser.coverage.CoverageAnalyser;
import org.pescuma.buildhealth.analyser.diskusage.DiskUsageAnalyser;
import org.pescuma.buildhealth.analyser.loc.LOCAnalyser;
import org.pescuma.buildhealth.analyser.staticanalysis.StaticAnalysisAnalyser;
import org.pescuma.buildhealth.analyser.unittest.UnitTestAnalyser;
import org.pescuma.buildhealth.cli.BuildHealthCliCommand;
import org.pescuma.buildhealth.core.BuildStatus;
import org.pescuma.buildhealth.core.Report;
import org.pescuma.buildhealth.core.ReportFormater;
import org.pescuma.buildhealth.core.ReportFormater.Outputer;

@Command(name = "report", description = "Report the status of the current build")
public class ReportCommand extends BuildHealthCliCommand {
	
	@Override
	public void execute() {
		// TODO
		buildHealth.addAnalyser(new UnitTestAnalyser());
		buildHealth.addAnalyser(new CoverageAnalyser());
		buildHealth.addAnalyser(new LOCAnalyser());
		buildHealth.addAnalyser(new StaticAnalysisAnalyser());
		buildHealth.addAnalyser(new DiskUsageAnalyser());
		
		Report report = buildHealth.generateReportSummary();
		
		ReportFormater reportFormater = new ReportFormater(new ReportFormater.Outputer() {
			private Ansi ansi;
			
			@Override
			public Outputer start() {
				ansi = Ansi.ansi();
				return this;
			}
			
			@Override
			public Outputer append(String text, BuildStatus status) {
				ansi.fg(toColor(status)).a(text).reset();
				return this;
			}
			
			private Color toColor(BuildStatus status) {
				switch (status) {
					case Good:
						return Ansi.Color.GREEN;
					case SoSo:
						return Ansi.Color.YELLOW;
					case Problematic:
						return Ansi.Color.RED;
					default:
						throw new IllegalStateException();
				}
			}
			
			@Override
			public Outputer append(String text) {
				ansi.a(text);
				return this;
			}
			
			@Override
			public String toString() {
				return ansi.toString();
			}
		});
		
		AnsiConsole.out.print(reportFormater.format(report));
	}
	
}
