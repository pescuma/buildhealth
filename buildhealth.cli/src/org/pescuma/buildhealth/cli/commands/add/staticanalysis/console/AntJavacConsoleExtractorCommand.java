package org.pescuma.buildhealth.cli.commands.add.staticanalysis.console;

import io.airlift.command.Command;

import java.io.File;

import org.pescuma.buildhealth.cli.BaseBuildHealthFilesCliCommand;
import org.pescuma.buildhealth.extractor.staticanalysis.console.AntJavacConsoleExtractor;

@Command(name = "ant-javac-console", description = "Add warnings from Java Compiler (javac) output files")
public class AntJavacConsoleExtractorCommand extends BaseBuildHealthFilesCliCommand {

	@Override
	public void execute() {
		buildHealth.extract(new AntJavacConsoleExtractor(getFiles()));
	}

}
