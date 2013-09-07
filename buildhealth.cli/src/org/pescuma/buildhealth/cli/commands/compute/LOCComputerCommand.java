package org.pescuma.buildhealth.cli.commands.compute;

import io.airlift.command.Arguments;
import io.airlift.command.Command;

import java.io.File;

import org.pescuma.buildhealth.cli.BuildHealthCliCommand;
import org.pescuma.buildhealth.computer.loc.LOCComputer;
import org.pescuma.buildhealth.extractor.PseudoFiles;

@Command(name = "loc", description = "Compute lines of code, using cloc")
public class LOCComputerCommand extends BuildHealthCliCommand {
	
	@Arguments(title = "file or folder", description = "File or folder with source files", required = true)
	public File file;
	
	@Override
	public void execute() {
		buildHealth.compute(new LOCComputer(new PseudoFiles(file)));
	}
	
}
