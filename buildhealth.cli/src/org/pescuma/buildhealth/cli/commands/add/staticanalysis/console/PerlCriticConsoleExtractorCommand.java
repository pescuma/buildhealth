package org.pescuma.buildhealth.cli.commands.add.staticanalysis.console;

import io.airlift.command.Arguments;
import io.airlift.command.Command;

import java.io.File;

import org.pescuma.buildhealth.cli.BuildHealthCliCommand;
import org.pescuma.buildhealth.extractor.PseudoFiles;
import org.pescuma.buildhealth.extractor.staticanalysis.console.PerlCriticConsoleExtractor;

@Command(name = "perl-critic-console", description = "Add warnings from Perl::Critic output files")
public class PerlCriticConsoleExtractorCommand extends BuildHealthCliCommand {

	@Arguments(title = "file or folder", description = "File or folder with Perl::Critic output(s)", required = true)
	public File file;

	@Override
	public void execute() {
		buildHealth.extract(new PerlCriticConsoleExtractor(new PseudoFiles(file)));
	}

}
