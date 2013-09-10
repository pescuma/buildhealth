package org.pescuma.buildhealth.analyser.unittest;

import java.util.List;

import org.pescuma.buildhealth.core.BuildStatus;
import org.pescuma.buildhealth.core.Report;

public class UnitTestReport extends Report {
	
	private final int passed;
	private final int errors;
	private final int failures;
	private final Double time;
	
	public UnitTestReport(BuildStatus status, String name, int passed, int errors, int failures, Double time,
			String message) {
		this(status, name, passed, errors, failures, time, message, null);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public UnitTestReport(BuildStatus status, String name, int passed, int errors, int failures, Double time,
			String message, List<UnitTestReport> children) {
		super(status, name, status == BuildStatus.Good ? "PASSED" : "FAILED", message, (List) children);
		
		this.passed = passed;
		this.errors = errors;
		this.failures = failures;
		this.time = time;
	}
	
	public int getTotal() {
		return passed + errors + failures;
	}
	
	public int getPassed() {
		return passed;
	}
	
	public int getErrors() {
		return errors;
	}
	
	public int getFailures() {
		return failures;
	}
	
	public Double getTime() {
		return time;
	}
	
}
