package org.pescuma.buildhealth.core.listener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.pescuma.buildhealth.extractor.BuildDataExtractor;

public class CompositeBuildHealthListener implements BuildHealthListener {
	
	private final List<BuildHealthListener> listeners = new ArrayList<BuildHealthListener>();
	
	public boolean addListener(BuildHealthListener listener) {
		return listeners.add(listener);
	}
	
	public boolean removeListener(BuildHealthListener listener) {
		return listeners.remove(listener);
	}
	
	@Override
	public void onFileExtracted(BuildDataExtractor extractor, File file) {
		for (BuildHealthListener listener : listeners)
			listener.onFileExtracted(extractor, file);
	}
	
	@Override
	public void onStreamExtracted(BuildDataExtractor extractor) {
		for (BuildHealthListener listener : listeners)
			listener.onStreamExtracted(extractor);
	}
	
}
