package org.pescuma.buildhealth.cli.commands.add.coverage;

import io.airlift.airline.Command;

import org.pescuma.buildhealth.cli.BaseBuildHealthFilesCliCommand;
import org.pescuma.buildhealth.extractor.coverage.VstestCoverageExtractor;

@Command(name = "vstest-coverage", description = "Add coverage information from an vtest XML file.\n"
		+ "Info on how to convert the .coverage into XML can be found at http://reportgenerator.codeplex.com/wikipage?title=Visual%20Studio%20Coverage%20Tools")
public class VstestCoverageExtractorCommand extends BaseBuildHealthFilesCliCommand {
	
	@Override
	public void execute() {
		buildHealth.extract(new VstestCoverageExtractor(getFiles()));
	}
	
}
