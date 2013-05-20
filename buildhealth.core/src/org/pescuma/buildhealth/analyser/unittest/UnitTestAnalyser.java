package org.pescuma.buildhealth.analyser.unittest;

import static java.lang.Math.*;
import static java.util.Arrays.*;

import java.util.Collections;
import java.util.List;

import org.kohsuke.MetaInfServices;
import org.pescuma.buildhealth.analyser.BuildHealthAnalyser;
import org.pescuma.buildhealth.analyser.BuildHealthAnalyserPreference;
import org.pescuma.buildhealth.core.BuildData;
import org.pescuma.buildhealth.core.BuildStatus;
import org.pescuma.buildhealth.core.Report;
import org.pescuma.buildhealth.prefs.Preferences;

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
 * 10 | Unit test,Java,JUnit,passed,package.TestWithoutMethodsInfo
 * 1 | Unit test,Java,JUnit,passed,package.TestWithMethodInfo,testMethod1
 * 1 | Unit test,Java,JUnit,failed,package.TestWithMethodInfo,testMethod2
 * 1 | Unit test,Java,JUnit,passed,package.TestWithMethodInfo,testMethod3
 * 1 | Unit test,Java,JUnit,error,package.TestWithMethodInfo,testMethod4
 * 1 | Unit test,Java,JUnit,passed,package.TestWithMethodAndTimeInfo,testMethod1
 * 0.01 | Unit test,Java,JUnit,time,package.TestWithMethodAndTimeInfo,testMethod1
 * </pre>
 */
@MetaInfServices
public class UnitTestAnalyser implements BuildHealthAnalyser {
	
	@Override
	public String getName() {
		return "Unit tests";
	}
	
	@Override
	public int getPriority() {
		return 100;
	}
	
	@Override
	public List<BuildHealthAnalyserPreference> getPreferences() {
		return Collections.emptyList();
	}
	
	@Override
	public List<Report> computeSimpleReport(BuildData data, Preferences prefs) {
		data = data.filter("Unit test");
		if (data.isEmpty())
			return Collections.emptyList();
		
		int passed = (int) round(data.filter(3, "passed").sum());
		int errors = (int) round(data.filter(3, "error").sum());
		int failures = (int) round(data.filter(3, "failed").sum());
		
		BuildStatus status = (errors + failures > 0) ? BuildStatus.Problematic : BuildStatus.Good;
		
		BuildData time = data.filter(3, "time");
		Double dt = null;
		if (!time.isEmpty())
			dt = time.sum();
		
		return asList((Report) new UnitTestReport(status, getName(), passed, errors, failures, dt));
	}
	
	private void append(StringBuilder out, int count, String name, String namePlural) {
		if (count > 0)
			out.append(", ").append(count).append(" ").append(count == 1 ? name : namePlural);
	}
	
	private void append(StringBuilder out, int count, String name) {
		append(out, count, name, name);
	}
	
}
