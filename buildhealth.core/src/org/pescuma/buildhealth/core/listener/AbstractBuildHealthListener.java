package org.pescuma.buildhealth.core.listener;

import java.io.File;

import org.pescuma.buildhealth.computer.BuildDataComputer;
import org.pescuma.buildhealth.extractor.BuildDataExtractor;

public abstract class AbstractBuildHealthListener implements BuildHealthListener {
	
	@Override
	public void onFileComputed(BuildDataComputer computer, File file) {
	}
	
	@Override
	public void onFileExtracted(BuildDataExtractor extractor, File file) {
	}
	
	@Override
	public void onStreamExtracted(BuildDataExtractor extractor) {
	}
	
}
