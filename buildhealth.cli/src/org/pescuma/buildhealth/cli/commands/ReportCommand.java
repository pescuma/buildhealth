package org.pescuma.buildhealth.cli.commands;

import io.airlift.command.Command;

import org.pescuma.buildhealth.analyser.diskusage.DiskUsageAnalyser;
import org.pescuma.buildhealth.analyser.loc.LOCAnalyser;
import org.pescuma.buildhealth.analyser.unittest.UnitTestAnalyser;
import org.pescuma.buildhealth.cli.BuildHealthCliCommand;
import org.pescuma.buildhealth.cli.ReportFormater;
import org.pescuma.buildhealth.core.Report;

@Command(name = "report", description = "Report the status of the current build")
public class ReportCommand extends BuildHealthCliCommand {
	
	@Override
	public void execute() {
		// TODO
		buildHealth.addAnalyser(new UnitTestAnalyser());
		buildHealth.addAnalyser(new LOCAnalyser());
		buildHealth.addAnalyser(new DiskUsageAnalyser());
		
		Report report = buildHealth.generateReportSummary();
		System.out.println(new ReportFormater().format(report));
	}
	
}
