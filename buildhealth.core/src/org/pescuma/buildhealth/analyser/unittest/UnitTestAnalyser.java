package org.pescuma.buildhealth.analyser.unittest;

import static java.lang.Math.*;
import static java.util.Arrays.*;
import static org.pescuma.buildhealth.analyser.NumbersFormater.*;
import static org.pescuma.buildhealth.core.BuildHealth.ReportFlags.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.kohsuke.MetaInfServices;
import org.pescuma.buildhealth.analyser.BuildHealthAnalyser;
import org.pescuma.buildhealth.analyser.BuildHealthAnalyserPreference;
import org.pescuma.buildhealth.core.BuildData;
import org.pescuma.buildhealth.core.BuildData.Line;
import org.pescuma.buildhealth.core.BuildStatus;
import org.pescuma.buildhealth.core.Report;
import org.pescuma.buildhealth.prefs.Preferences;

import com.google.common.base.Predicate;

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
	
	private static final int COLUMN_RESULT = 3;
	private static final int COLUMN_SUITE = 4;
	private static final int COLUMN_TEST = 5;
	
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
	public List<Report> computeReport(BuildData data, Preferences prefs, int opts) {
		data = data.filter("Unit test");
		if (data.isEmpty())
			return Collections.emptyList();
		
		List<UnitTestReport> children = null;
		
		boolean highlighProblems = (opts & HighlightProblems) != 0;
		boolean summaryOnly = (opts & SummaryOnly) != 0;
		
		if (highlighProblems) {
			children = new ArrayList<UnitTestReport>();
			
			List<UnitTestReport> failed = createLanguageFrameworkReports(data.filter(filterTestsByType(data, "failed",
					"error")));
			children.add(toUnitTestReport("Failed", new Stats(failed), failed));
			
			if (!summaryOnly) {
				List<UnitTestReport> passed = createLanguageFrameworkReports(data.filter(filterTestsByType(data,
						"passed")));
				children.add(toUnitTestReport("Passed", new Stats(passed), passed));
			}
			
		} else {
			if (!summaryOnly)
				children = createLanguageFrameworkReports(data);
		}
		
		return asList((Report) toUnitTestReport(getName(), new Stats(data), children));
	}
	
	private Predicate<Line> filterTestsByType(BuildData data, String... types) {
		final Set<String> allTypes = new HashSet<String>();
		allTypes.add("passed");
		allTypes.add("failed");
		allTypes.add("error");
		
		final Set<String> toFilter = new HashSet<String>(asList(types));
		
		final Collection<String[]> tests = data.filter(COLUMN_RESULT, new Predicate<String>() {
			@Override
			public boolean apply(String input) {
				return toFilter.contains(input);
			}
		}).getDistinct(COLUMN_SUITE, COLUMN_TEST);
		
		return new Predicate<Line>() {
			@Override
			public boolean apply(Line input) {
				String result = input.getColumn(COLUMN_RESULT);
				if (allTypes.contains(result))
					return toFilter.contains(result);
				else
					// We need to keep other types of meta info
					return tests.contains(new String[] { input.getColumn(COLUMN_SUITE), input.getColumn(COLUMN_TEST) });
			}
		};
	}
	
	private List<UnitTestReport> createLanguageFrameworkReports(BuildData data) {
		List<UnitTestReport> children = new ArrayList<UnitTestReport>();
		for (String[] type : data.getDistinct(1, 2))
			children.add(createLanguageFrameworkReport(data, type[0], type[1]));
		return children;
	}
	
	private UnitTestReport createLanguageFrameworkReport(BuildData data, String language, String framework) {
		data = data.filter("Unit test", language, framework);
		
		List<UnitTestReport> children = new ArrayList<UnitTestReport>();
		for (String suite : data.getDistinct(COLUMN_SUITE))
			children.add(createTestSuiteReport(data.filter(COLUMN_SUITE, suite), suite));
		
		return toUnitTestReport(language + " - " + framework, new Stats(children), children);
	}
	
	private UnitTestReport createTestSuiteReport(BuildData data, String suite) {
		List<UnitTestReport> children = new ArrayList<UnitTestReport>();
		boolean hasToComputeStats = false;
		for (String test : data.getDistinct(COLUMN_TEST)) {
			UnitTestReport testReport = createTestReport(data.filter(COLUMN_TEST, test), test);
			if (testReport != null)
				children.add(testReport);
			else
				hasToComputeStats = true;
		}
		
		if (suite.isEmpty())
			suite = "No suite name";
		
		Stats stats = (hasToComputeStats ? new Stats(data) : new Stats(children));
		return toUnitTestReport(suite, stats, children);
	}
	
	private UnitTestReport createTestReport(BuildData data, String test) {
		Stats stats = new Stats(data);
		
		StringBuilder msg = new StringBuilder();
		for (String[] line : data.getColumns(6, 7)) {
			if (msg.length() > 0)
				msg.append("\n");
			
			if (!line[0].isEmpty())
				msg.append(line[0]).append("\n");
			
			if (!line[1].isEmpty())
				msg.append(line[1]).append("\n");
		}
		
		if (test.isEmpty() && msg.length() < 1)
			// no name and no message means no useful info
			return null;
		
		if (stats.dt != null) {
			if (msg.length() > 0)
				msg.append("\n");
			msg.append("Executed in ").append(format1000(stats.dt, "s"));
		}
		
		if (test.isEmpty())
			test = "No test name";
		
		return toUnitTestReport(test, stats, msg.toString());
	}
	
	private UnitTestReport toUnitTestReport(String name, Stats stats, List<UnitTestReport> children) {
		return new UnitTestReport(stats.getStatus(), name, stats.getPassed(), stats.getErrors(), stats.getFailures(),
				stats.dt, children);
	}
	
	private UnitTestReport toUnitTestReport(String name, Stats stats, String message) {
		return new UnitTestReport(stats.getStatus(), name, stats.getPassed(), stats.getErrors(), stats.getFailures(),
				stats.dt, message);
	}
	
	private class Stats {
		Integer passed;
		Integer errors;
		Integer failures;
		Double dt;
		
		Stats(BuildData data) {
			passed = toInt(sumIfExists(data, COLUMN_RESULT, "passed"));
			errors = toInt(sumIfExists(data, COLUMN_RESULT, "error"));
			failures = toInt(sumIfExists(data, COLUMN_RESULT, "failed"));
			dt = sumIfExists(data, COLUMN_RESULT, "time");
		}
		
		Stats(List<UnitTestReport> children) {
			int passed = 0;
			int errors = 0;
			int failures = 0;
			double dt = 0;
			boolean hasDt = false;
			for (UnitTestReport utr : children) {
				passed += utr.getPassed();
				errors += utr.getErrors();
				failures += utr.getFailures();
				Double utrDt = utr.getTime();
				if (utrDt != null) {
					hasDt = true;
					dt += utrDt;
				}
			}
			
			this.passed = passed;
			this.errors = errors;
			this.failures = failures;
			if (hasDt)
				this.dt = dt;
		}
		
		Integer getPassed() {
			if (passed == null)
				return 0;
			return passed;
		}
		
		Integer getErrors() {
			if (errors == null)
				return 0;
			return errors;
		}
		
		Integer getFailures() {
			if (failures == null)
				return 0;
			return failures;
		}
		
		BuildStatus getStatus() {
			return getErrors() + getFailures() > 0 ? BuildStatus.Problematic : BuildStatus.Good;
		}
		
		private Double sumIfExists(BuildData data, int column, String value) {
			data = data.filter(column, value);
			if (data.isEmpty())
				return null;
			else
				return data.sum();
		}
		
		private Integer toInt(Double val) {
			if (val == null)
				return null;
			else
				return (int) round(val.doubleValue());
		}
	}
}
