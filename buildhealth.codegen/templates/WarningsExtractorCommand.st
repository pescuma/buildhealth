package org.pescuma.buildhealth.cli.commands.add.staticanalysis.console;

import io.airlift.command.Command;

import java.io.File;

import org.pescuma.buildhealth.cli.BaseBuildHealthFilesCliCommand;
import org.pescuma.buildhealth.extractor.staticanalysis.console.$parser.baseClassName$ConsoleExtractor;

@Command(name = "$parser.name$-console", description = "Add warnings from $parser.description$ output files")
public class $class$ extends BaseBuildHealthFilesCliCommand {
	
	@Override
	public void execute() {
		buildHealth.extract(new $parser.baseClassName$ConsoleExtractor(getFiles()));
	}
	
}
