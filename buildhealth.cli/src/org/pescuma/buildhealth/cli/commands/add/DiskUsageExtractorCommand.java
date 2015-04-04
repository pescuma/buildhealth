package org.pescuma.buildhealth.cli.commands.add;

import io.airlift.airline.Command;
import io.airlift.airline.Option;

import org.pescuma.buildhealth.cli.BaseBuildHealthFilesCliCommand;
import org.pescuma.buildhealth.extractor.diskusage.DiskUsageExtractor;

@Command(name = "diskusage", description = "Add disk usage information")
public class DiskUsageExtractorCommand extends BaseBuildHealthFilesCliCommand {
	
	@Option(name = { "--tag", "-t" }, description = "Tag to identify different usage groups")
	public String tag;
	
	@Override
	public void execute() {
		buildHealth.extract(new DiskUsageExtractor(getFiles(), tag));
	}
	
}
