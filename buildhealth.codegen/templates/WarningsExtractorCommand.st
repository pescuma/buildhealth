package org.pescuma.buildhealth.cli.commands.add.staticanalysis.console;

import io.airlift.command.Arguments;
import io.airlift.command.Command;

import java.io.File;

import org.pescuma.buildhealth.cli.BuildHealthCliCommand;
import org.pescuma.buildhealth.extractor.PseudoFiles;
import org.pescuma.buildhealth.extractor.staticanalysis.console.$parser.baseClassName$ConsoleExtractor;

@Command(name = "$parser.name$", description = "Add warnings from $parser.description$ output files")
public class $class$ extends BuildHealthCliCommand {
	
	@Arguments(title = "file or folder", description = "File or folder with $parser.description$ output(s)", required = true)
	public File file;
	
	@Override
	public void execute() {
		buildHealth.extract(new $parser.baseClassName$ConsoleExtractor(new PseudoFiles(file)));
	}
	
}
