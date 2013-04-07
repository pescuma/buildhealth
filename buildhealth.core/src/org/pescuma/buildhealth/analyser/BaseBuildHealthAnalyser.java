package org.pescuma.buildhealth.analyser;

import java.util.Collections;
import java.util.List;

public abstract class BaseBuildHealthAnalyser implements BuildHealthAnalyser {
	
	@Override
	public String getName() {
		return null;
	}
	
	@Override
	public List<BuildHealthAnalyserPreference> getPreferences() {
		return Collections.emptyList();
	}
	
}
