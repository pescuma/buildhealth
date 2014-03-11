package org.pescuma.buildhealth.cli;

import io.airlift.command.Arguments;
import io.airlift.command.Option;

import java.io.File;
import java.util.List;

import org.pescuma.buildhealth.extractor.PseudoFiles;

public abstract class BaseBuildHealthFilesCliCommand extends BuildHealthCliCommand {
	
	@Arguments(title = "file or folder", description = "File or folder with files to process", required = true)
	public File file;
	
	@Option(name = { "--exclude" }, description = "Files or folders to exclude while searching")
	public List<File> excludes;
	
	@Option(name = { "--dont-ignore-source-control" }, description = "Don't ignore source control folders")
	public boolean dontIgnoreSourceControlFolders = false;
	
	protected PseudoFiles getFiles() {
		return new PseudoFiles(file, excludes, !dontIgnoreSourceControlFolders);
	}
	
}
