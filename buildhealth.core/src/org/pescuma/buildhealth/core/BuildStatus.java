package org.pescuma.buildhealth.core;

public enum BuildStatus {
	
	Successful,
	Failed,
	SoSo;
	
	public static BuildStatus merge(BuildStatus first, BuildStatus second) {
		if (first == null)
			return second;
		if (second == null)
			return first;
		
		if (first == Successful)
			return second;
		if (second == Successful)
			return first;
		
		if (first == SoSo)
			return second;
		if (second == SoSo)
			return first;
		
		return Failed;
	}
}
