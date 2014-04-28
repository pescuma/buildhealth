package org.pescuma.buildhealth.core.listener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.pescuma.buildhealth.computer.BuildDataComputer;
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
	public synchronized void onFileComputed(BuildDataComputer computer, File file) {
		for (BuildHealthListener listener : listeners)
			listener.onFileComputed(computer, file);
	}
	
	@Override
	public synchronized void onErrorComputingFile(BuildDataComputer computer, File file, Exception ex) {
		for (BuildHealthListener listener : listeners)
			listener.onErrorComputingFile(computer, file, ex);
	}
	
	@Override
	public synchronized void onFileExtracted(BuildDataExtractor extractor, File file) {
		for (BuildHealthListener listener : listeners)
			listener.onFileExtracted(extractor, file);
	}
	
	@Override
	public synchronized void onErrorExtractingFile(BuildDataExtractor extractor, File file, Exception ex) {
		for (BuildHealthListener listener : listeners)
			listener.onErrorExtractingFile(extractor, file, ex);
	}
	
	@Override
	public synchronized void onStreamExtracted(BuildDataExtractor extractor) {
		for (BuildHealthListener listener : listeners)
			listener.onStreamExtracted(extractor);
	}
	
	@Override
	public void onOtherExtracted(BuildDataExtractor extractor, String message) {
		for (BuildHealthListener listener : listeners)
			listener.onOtherExtracted(extractor, message);
	}
	
}
