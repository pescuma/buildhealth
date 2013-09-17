package org.pescuma.buildhealth.analyser.coverage;

import static java.lang.Math.*;

public class CoverageMetric {
	
	private final String name;
	private final double covered;
	private final double total;
	
	public CoverageMetric(String name, double covered, double total) {
		this.name = name;
		this.covered = covered;
		this.total = total;
	}
	
	public String getName() {
		return name;
	}
	
	public int getPercentage() {
		return (int) round(100 * covered / total);
	}
	
	public double getCovered() {
		return covered;
	}
	
	public double getTotal() {
		return total;
	}
	
}
