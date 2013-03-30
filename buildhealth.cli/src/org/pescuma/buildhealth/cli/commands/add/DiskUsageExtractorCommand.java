package org.pescuma.buildhealth.cli.commands.add;

import io.airlift.command.Arguments;
import io.airlift.command.Command;
import io.airlift.command.Option;

import java.io.File;

import org.pescuma.buildhealth.cli.BuildHealthCliCommand;
import org.pescuma.buildhealth.extractor.PseudoFiles;
import org.pescuma.buildhealth.extractor.diskusage.DiskUsageExtractor;

@Command(name = "diskusage", description = "Add disk usage information")
public class DiskUsageExtractorCommand extends BuildHealthCliCommand {
	
	@Arguments(title = "file or folder", description = "File or folder to compute disk usage", required = true)
	public File file;
	
	@Option(name = { "--tag", "-t" }, description = "Tag to identify different usage groups")
	public String tag;
	
	@Override
	public void execute() {
		buildHealth.extract(new DiskUsageExtractor(new PseudoFiles(file), tag));
	}
	
}
