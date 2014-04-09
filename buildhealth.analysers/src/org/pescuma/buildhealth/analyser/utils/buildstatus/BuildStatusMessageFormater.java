package org.pescuma.buildhealth.analyser.utils.buildstatus;

public interface BuildStatusMessageFormater {
	
	String computeSoSoMessage(double good, String[] prefKey);
	
	String computeProblematicMessage(double warn, String[] prefKey);
}
