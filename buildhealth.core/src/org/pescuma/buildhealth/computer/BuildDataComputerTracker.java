package org.pescuma.buildhealth.computer;

import java.io.File;

public interface BuildDataComputerTracker {
	
	void onFileProcessed(File file);
	
	void onErrorProcessingFile(File file, Exception ex);
	
	void onStreamProcessed();
	
	void onFileOutputCreated(File file);
	
}
