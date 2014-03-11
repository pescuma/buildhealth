package org.pescuma.buildhealth.cli.commands.add.staticanalysis;

import io.airlift.command.Command;

import org.pescuma.buildhealth.cli.BaseBuildHealthFilesCliCommand;
import org.pescuma.buildhealth.extractor.staticanalysis.JSXLintExtractor;

@Command(name = "js-lint", description = "Add code duplication information from a JSLint, JSHint or JavaScript Lint TXT or XML file")
public class JSXLintExtractorCommand extends BaseBuildHealthFilesCliCommand {
	
	@Override
	public void execute() {
		buildHealth.extract(new JSXLintExtractor(getFiles()));
	}
	
}
