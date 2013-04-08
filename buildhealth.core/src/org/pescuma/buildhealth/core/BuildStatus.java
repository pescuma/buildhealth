package org.pescuma.buildhealth.core;

public enum BuildStatus {
	
	Good,
	Problematic,
	SoSo;
	
	public static BuildStatus merge(BuildStatus first, BuildStatus second) {
		if (first == null)
			return second;
		if (second == null)
			return first;
		
		if (first == Good)
			return second;
		if (second == Good)
			return first;
		
		if (first == SoSo)
			return second;
		if (second == SoSo)
			return first;
		
		return Problematic;
	}
	
	public BuildStatus mergeWith(BuildStatus other) {
		return merge(this, other);
	}
}
