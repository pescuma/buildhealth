package org.pescuma.buildhealth.extractor;

import java.io.File;

public interface BuildDataExtractorTracker {
	
	void onFileProcessed(File file);
	
	void onStreamProcessed();
	
	void onProcessed(String message);
	
}
