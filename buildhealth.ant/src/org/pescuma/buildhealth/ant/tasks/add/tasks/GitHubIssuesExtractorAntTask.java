package org.pescuma.buildhealth.ant.tasks.add.tasks;

import org.pescuma.buildhealth.ant.BaseBuildHealthAntSubTask;
import org.pescuma.buildhealth.core.BuildHealth;
import org.pescuma.buildhealth.extractor.tasks.GitHubIssuesExtractor;

public class GitHubIssuesExtractorAntTask extends BaseBuildHealthAntSubTask {
	
	private String username;
	private String repository;
	private boolean onlyOpen = false;
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getRepository() {
		return repository;
	}
	
	public void setRepository(String repository) {
		this.repository = repository;
	}
	
	public boolean isOnlyOpen() {
		return onlyOpen;
	}
	
	public void setOnlyOpen(boolean onlyOpen) {
		this.onlyOpen = onlyOpen;
	}
	
	@Override
	protected void execute(BuildHealth buildHealth) {
		buildHealth.extract(new GitHubIssuesExtractor(username, repository, onlyOpen));
	}
	
}
