package org.pescuma.buildhealth.core;

import java.io.File;

public interface BuildDataExtractorTracker {
	
	void fileProcessed(File file);
	
	void streamProcessed();
	
}
