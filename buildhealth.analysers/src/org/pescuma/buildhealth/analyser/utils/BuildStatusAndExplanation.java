package org.pescuma.buildhealth.analyser.utils;

import org.pescuma.buildhealth.core.BuildStatus;

public class BuildStatusAndExplanation {
	
	public final BuildStatus status;
	public final String explanation;
	
	public BuildStatusAndExplanation(BuildStatus status, String explanation) {
		this.status = status;
		this.explanation = explanation;
	}
	
	public BuildStatusAndExplanation mergeWith(BuildStatusAndExplanation other) {
		return merge(this, other);
	}
	
	public static BuildStatusAndExplanation merge(BuildStatusAndExplanation one, BuildStatusAndExplanation other) {
		if (one == null)
			return other;
		if (other == null)
			return one;
		
		if (one.status == BuildStatus.Good)
			return other;
		if (other.status == BuildStatus.Good)
			return one;
		
		if (one.status == BuildStatus.Problematic)
			return one;
		if (other.status == BuildStatus.Problematic)
			return other;
		
		return one;
	}
}
