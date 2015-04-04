package org.pescuma.buildhealth.cli.commands.add.tasks;

import io.airlift.airline.Arguments;
import io.airlift.airline.Command;
import io.airlift.airline.Option;
import io.airlift.airline.ParseException;

import org.pescuma.buildhealth.cli.BuildHealthCliCommand;
import org.pescuma.buildhealth.extractor.tasks.GitHubIssuesExtractor;

@Command(name = "github-issues", description = "Add tasks from a GitHub repository")
public class GitHubIssuesExtractorCommand extends BuildHealthCliCommand {
	
	@Arguments(title = "repository", description = "Repository name, in the format username/repository", required = true)
	public String repository;
	
	@Option(name = "--only-open", title = "only open issues", description = "Only include open issues from the repository")
	public boolean onlyOpen;
	
	@Override
	public void execute() {
		String[] split = repository.split("/");
		if (split.length != 2)
			throw new ParseException(
					"The repository name should have the format username/repository");
		
		buildHealth.extract(new GitHubIssuesExtractor(split[0], split[1], onlyOpen));
	}
	
}
