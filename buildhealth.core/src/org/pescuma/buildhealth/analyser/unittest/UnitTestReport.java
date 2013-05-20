package org.pescuma.buildhealth.analyser.unittest;

import static org.pescuma.buildhealth.analyser.NumbersFormater.*;

import org.pescuma.buildhealth.core.BuildStatus;
import org.pescuma.buildhealth.core.Report;

public class UnitTestReport extends Report {
	
	private final int passed;
	private final int errors;
	private final int failures;
	private final Double time;
	
	public UnitTestReport(BuildStatus status, String name, int passed, int errors, int failures, Double time) {
		super(status, name, errors + failures == 0 ? "PASSED" : "FAILED", computeDescription(passed, errors, failures,
				time));
		this.passed = passed;
		this.errors = errors;
		this.failures = failures;
		this.time = time;
	}
	
	private static String computeDescription(int passed, int errors, int failures, Double time) {
		StringBuilder description = new StringBuilder();
		append(description, passed + errors + failures, "test", "tests");
		append(description, passed, "passed");
		append(description, errors, "error", "errors");
		append(description, failures, "failure", "failures");
		
		if (time != null)
			description.append(" (").append(format1000(time, "s")).append(")");
		
		return description.toString();
	}
	
	private static void append(StringBuilder out, int count, String name) {
		append(out, count, name, name);
	}
	
	private static void append(StringBuilder out, int count, String name, String namePlural) {
		if (count <= 0)
			return;
		if (out.length() > 0)
			out.append(", ");
		out.append(count).append(" ").append(count == 1 ? name : namePlural);
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
