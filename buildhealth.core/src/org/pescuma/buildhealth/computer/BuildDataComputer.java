package org.pescuma.buildhealth.computer;

import java.io.File;

import org.pescuma.buildhealth.extractor.BuildDataExtractor;

public interface BuildDataComputer {
	
	BuildDataExtractor compute(File folder, BuildDataComputerTracker tracker);
	
}
