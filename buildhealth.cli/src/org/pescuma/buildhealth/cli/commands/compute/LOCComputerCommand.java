package org.pescuma.buildhealth.cli.commands.compute;

import io.airlift.airline.Command;

import org.pescuma.buildhealth.cli.BaseBuildHealthFilesCliCommand;
import org.pescuma.buildhealth.computer.loc.LOCComputer;

@Command(name = "loc", description = "Compute lines of code")
public class LOCComputerCommand extends BaseBuildHealthFilesCliCommand {
	
	@Override
	public void execute() {
		buildHealth.compute(new LOCComputer(getFiles()));
	}
	
}
