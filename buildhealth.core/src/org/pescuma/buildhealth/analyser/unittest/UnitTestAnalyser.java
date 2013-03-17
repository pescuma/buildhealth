package org.pescuma.buildhealth.analyser.unittest;

import static java.lang.Math.*;
import static java.util.Arrays.*;

import java.util.Collections;
import java.util.List;

import org.pescuma.buildhealth.analyser.BuildHealthAnalyser;
import org.pescuma.buildhealth.core.BuildData;
import org.pescuma.buildhealth.core.BuildStatus;
import org.pescuma.buildhealth.core.Report;

/**
 * Expect the lines to be:
 * 
 * <pre>
 * Unit test,language,framework,{passed,error,failed,skipped,time},test suite name,method name,message,stack trace
 * </pre>
 * 
 * For time the value is in seconds, for the others is the number of tests.
 * 
 * Example:
 * 
 * <pre>
 * 10 | Unit test,java,junit,passed,package.TestWithoutMethodsInfo
 * 1 | Unit test,java,junit,passed,package.TestWithMethodInfo,testMethod1
 * 1 | Unit test,java,junit,failed,package.TestWithMethodInfo,testMethod2
 * 1 | Unit test,java,junit,passed,package.TestWithMethodInfo,testMethod3
 * 1 | Unit test,java,junit,error,package.TestWithMethodInfo,testMethod4
 * 1 | Unit test,java,junit,passed,package.TestWithMethodAndTimeInfo,testMethod1
 * 0.01 | Unit test,java,junit,time,package.TestWithMethodAndTimeInfo,testMethod1
 * </pre>
 */
public class UnitTestAnalyser implements BuildHealthAnalyser {
	
	@Override
	public List<Report> computeSimpleReport(BuildData data) {
		data = data.filter("Unit test");
		if (data.isEmpty())
			return Collections.emptyList();
		
		int passed = (int) round(data.filter(3, "passed").sum());
		int errors = (int) round(data.filter(3, "error").sum());
		int failures = (int) round(data.filter(3, "failed").sum());
		int total = passed + errors + failures;
		
		BuildStatus status = (errors + failures > 0) ? BuildStatus.Problematic : BuildStatus.Good;
		
		StringBuilder description = new StringBuilder();
		description.append(total).append(" ").append(total == 1 ? "test" : "tests");
		
		append(description, passed, "passed");
		append(description, errors, "error", "errors");
		append(description, failures, "failure", "failures");
		
		BuildData time = data.filter(3, "time");
		if (!time.isEmpty())
			description.append(" (").append(String.format("%.1f", time.sum())).append(" s)");
		
		return asList(new Report(status, "Unit tests", passed == total ? "PASSED" : "FAILED", description.toString()));
	}
	
	private void append(StringBuilder out, int count, String name, String namePlural) {
		if (count > 0)
			out.append(", ").append(count).append(" ").append(count == 1 ? name : namePlural);
	}
	
	private void append(StringBuilder out, int count, String name) {
		append(out, count, name, name);
	}
}
