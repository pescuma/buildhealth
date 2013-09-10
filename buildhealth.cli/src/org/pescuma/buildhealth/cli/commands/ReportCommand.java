package org.pescuma.buildhealth.cli.commands;

import io.airlift.command.Arguments;
import io.airlift.command.Command;

import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.Ansi.Color;
import org.fusesource.jansi.AnsiConsole;
import org.pescuma.buildhealth.cli.BuildHealthCliCommand;
import org.pescuma.buildhealth.core.BuildHealth.ReportFlags;
import org.pescuma.buildhealth.core.BuildStatus;
import org.pescuma.buildhealth.core.Report;
import org.pescuma.buildhealth.core.ReportFormater;
import org.pescuma.buildhealth.core.ReportFormater.Outputer;

@Command(name = "report", description = "Report the status of the current build")
public class ReportCommand extends BuildHealthCliCommand {
	
	@Arguments(title = "category", description = "Category to provide a detailed report")
	public String category;
	
	@Override
	public void execute() {
		Report report;
		if (category == null || category.isEmpty())
			report = buildHealth.generateReport(ReportFlags.SummaryOnly);
		else
			report = buildHealth.generateReport(category, ReportFlags.Full);
		
		final Ansi ansi = Ansi.ansi();
		
		new ReportFormater().format(report, new ReportFormater.Outputer() {
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
		});
		
		AnsiConsole.out.print(ansi.toString());
		AnsiConsole.out.flush();
	}
}
