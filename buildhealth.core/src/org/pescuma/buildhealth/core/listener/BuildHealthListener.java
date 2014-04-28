package org.pescuma.buildhealth.core.listener;

import java.io.File;

import org.pescuma.buildhealth.computer.BuildDataComputer;
import org.pescuma.buildhealth.extractor.BuildDataExtractor;

public interface BuildHealthListener {
	
	void onFileComputed(BuildDataComputer computer, File file);
	
	void onErrorComputingFile(BuildDataComputer computer, File file, Exception ex);
	
	void onFileExtracted(BuildDataExtractor extractor, File file);
	
	void onErrorExtractingFile(BuildDataExtractor extractor, File file, Exception ex);
	
	void onStreamExtracted(BuildDataExtractor extractor);
	
	void onOtherExtracted(BuildDataExtractor extractor, String message);
	
}
