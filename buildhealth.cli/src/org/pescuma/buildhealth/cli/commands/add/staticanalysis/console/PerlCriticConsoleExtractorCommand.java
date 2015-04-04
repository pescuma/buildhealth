package org.pescuma.buildhealth.cli.commands.add.staticanalysis.console;

import io.airlift.airline.Command;

import org.pescuma.buildhealth.cli.BaseBuildHealthFilesCliCommand;
import org.pescuma.buildhealth.extractor.staticanalysis.console.PerlCriticConsoleExtractor;

@Command(name = "perl-critic-console", description = "Add warnings from Perl::Critic output files")
public class PerlCriticConsoleExtractorCommand extends BaseBuildHealthFilesCliCommand {
	
	@Override
	public void execute() {
		buildHealth.extract(new PerlCriticConsoleExtractor(getFiles()));
	}
	
}
