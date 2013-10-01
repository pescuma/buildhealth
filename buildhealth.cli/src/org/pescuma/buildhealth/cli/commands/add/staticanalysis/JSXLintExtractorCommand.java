package org.pescuma.buildhealth.cli.commands.add.staticanalysis;

import io.airlift.command.Arguments;
import io.airlift.command.Command;

import java.io.File;

import org.pescuma.buildhealth.cli.BuildHealthCliCommand;
import org.pescuma.buildhealth.extractor.PseudoFiles;
import org.pescuma.buildhealth.extractor.staticanalysis.JSXLintExtractor;

@Command(name = "js-lint", description = "Add code duplication information from a JSLint, JSHint or JavaScript Lint TXT or XML file")
public class JSXLintExtractorCommand extends BuildHealthCliCommand {
	
	@Arguments(title = "file or folder", description = "File or folder with TXT or XML output(s)", required = true)
	public File file;
	
	@Override
	public void execute() {
		buildHealth.extract(new JSXLintExtractor(new PseudoFiles(file)));
	}
	
}
