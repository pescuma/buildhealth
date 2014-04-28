package org.pescuma.buildhealth.extractor;

import java.io.File;

public interface BuildDataExtractorTracker {
	
	void onFileProcessed(File file);
	
	void onErrorProcessingFile(File file, Exception ex);
	
	void onStreamProcessed();
	
	void onProcessed(String message);
	
}
