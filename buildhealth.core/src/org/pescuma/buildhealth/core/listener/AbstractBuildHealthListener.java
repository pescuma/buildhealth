package org.pescuma.buildhealth.core.listener;

import java.io.File;

import org.pescuma.buildhealth.extractor.BuildDataExtractor;

public abstract class AbstractBuildHealthListener implements BuildHealthListener {
	
	@Override
	public void onFileExtracted(BuildDataExtractor extractor, File file) {
	}
	
	@Override
	public void onStreamExtracted(BuildDataExtractor extractor) {
	}
	
}
