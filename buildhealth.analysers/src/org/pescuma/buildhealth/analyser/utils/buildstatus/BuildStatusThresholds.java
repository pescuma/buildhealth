package org.pescuma.buildhealth.analyser.utils.buildstatus;

public class BuildStatusThresholds {
	public final double good;
	public final double warn;
	
	public BuildStatusThresholds(double good, double warn) {
		this.good = good;
		this.warn = warn;
	}
}