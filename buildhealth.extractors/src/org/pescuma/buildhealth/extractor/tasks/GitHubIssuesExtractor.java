package org.pescuma.buildhealth.extractor.tasks;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHIssueState;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHUser;
import org.kohsuke.github.GitHub;
import org.pescuma.buildhealth.core.BuildData;
import org.pescuma.buildhealth.extractor.BuildDataExtractor;
import org.pescuma.buildhealth.extractor.BuildDataExtractorTracker;

// http://developer.github.com/v3/issues/#list-issues-for-a-repository
public class GitHubIssuesExtractor implements BuildDataExtractor {
	
	private static final DateFormat isoDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
	static {
		TimeZone tz = TimeZone.getTimeZone("UTC");
		isoDateFormat.setTimeZone(tz);
	}
	
	private final String username;
	private final String repository;
	private final boolean onlyOpen;
	private final Map<String, String> userToName = new HashMap<String, String>();
	
	public GitHubIssuesExtractor(String username, String repository, boolean onlyOpen) {
		this.username = username;
		this.repository = repository;
		this.onlyOpen = onlyOpen;
	}
	
	@Override
	public void extractTo(BuildData data, BuildDataExtractorTracker tracker) {
		try {
			
			GitHub gitHub = GitHub.connectAnonymously();
			
			GHRepository repo = gitHub.getRepository(username + "/" + repository);
			
			extractIssues(data, repo.getIssues(GHIssueState.OPEN));
			
			if (!onlyOpen)
				extractIssues(data, repo.getIssues(GHIssueState.CLOSED));
			
			tracker.onProcessed("Got github issues from " + username + "/" + repository);
			
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private void extractIssues(BuildData data, List<GHIssue> issues) {
		for (GHIssue issue : issues) {
			data.add(1, "Tasks", "GitHub", toLabels(issue.getLabels()), toState(issue.getState()), issue.getTitle(),
					toName(issue.getAssignee()), toName(issue.getUser()), toDate(issue.getCreatedAt()),
					Integer.toString(issue.getNumber()), "", issue.getBody());
			
		}
	}
	
	private String toLabels(Collection<String> labels) {
		// BUG: the issue should be an object
		
		StringBuilder result = new StringBuilder();
		
		boolean lastWasName = false;
		for (String label : labels) {
			if (lastWasName) {
				lastWasName = false;
				
				if (result.length() > 0)
					result.append(", ");
				result.append(label);
				
			} else if (label.equals("name")) {
				lastWasName = true;
			}
		}
		
		if (result.length() > 0)
			return result.toString();
		else
			return "Issue";
	}
	
	private String toDate(Date createdAt) {
		if (createdAt == null)
			return "";
		
		return isoDateFormat.format(createdAt);
	}
	
	private String toName(GHUser user) {
		if (user == null)
			return "";
		
		String login = user.getLogin();
		String result = userToName.get(login);
		if (result == null) {
			result = getName(user);
			userToName.put(login, result);
		}
		
		return result;
		
	}
	
	private String getName(GHUser user) {
		try {
			
			String name = user.getName();
			if (name != null)
				return name;
			
		} catch (IOException e) {
			// Ignore
		}
		
		return user.getLogin();
	}
	
	private String toState(GHIssueState state) {
		switch (state) {
			case CLOSED:
				return "Closed";
			default:
				return "Open";
		}
	}
	
}
