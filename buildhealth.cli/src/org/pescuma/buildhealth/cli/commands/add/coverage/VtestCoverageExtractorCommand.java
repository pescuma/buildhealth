package org.pescuma.buildhealth.cli.commands.add.coverage;

import io.airlift.command.Arguments;
import io.airlift.command.Command;

import java.io.File;

import org.pescuma.buildhealth.cli.BuildHealthCliCommand;
import org.pescuma.buildhealth.extractor.PseudoFiles;
import org.pescuma.buildhealth.extractor.coverage.VtestCoverageExtractor;

@Command(name = "vtest-coverage", description = "Add coverage information from an vtest XML file.\n"
		+ "Info on how to convert the .coverage into XML can be found at http://reportgenerator.codeplex.com/wikipage?title=Visual%20Studio%20Coverage%20Tools")
public class VtestCoverageExtractorCommand extends BuildHealthCliCommand {
	
	@Arguments(title = "file or folder", description = "File or folder with vtest XML output(s)", required = true)
	public File file;
	
	@Override
	public void execute() {
		buildHealth.extract(new VtestCoverageExtractor(new PseudoFiles(file)));
	}
	
}
