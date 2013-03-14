package org.pescuma.buildhealth.extractor;

import org.pescuma.buildhealth.core.BuildData;
import org.pescuma.buildhealth.core.BuildDataExtractorTracker;

public interface BuildDataExtractor {
	
	void extractTo(BuildData data, BuildDataExtractorTracker tracker);
	
}
