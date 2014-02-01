package org.pescuma.buildhealth.cli.commands.add.staticanalysis.console;

import io.airlift.command.Arguments;
import io.airlift.command.Command;

import java.io.File;

import org.pescuma.buildhealth.cli.BuildHealthCliCommand;
import org.pescuma.buildhealth.extractor.PseudoFiles;
import org.pescuma.buildhealth.extractor.staticanalysis.console.ClangConsoleExtractor;

@Command(name = "clang-console", description = "Add warnings from Clang (LLVM based) output files")
public class ClangConsoleExtractorCommand extends BuildHealthCliCommand {

	@Arguments(title = "file or folder", description = "File or folder with Clang (LLVM based) output(s)", required = true)
	public File file;

	@Override
	public void execute() {
		buildHealth.extract(new ClangConsoleExtractor(new PseudoFiles(file)));
	}

}
