package org.pescuma.buildhealth.analyser.unittest;

import static java.util.Arrays.*;
import static org.pescuma.buildhealth.analyser.NumbersFormater.*;
import static org.pescuma.buildhealth.core.BuildHealth.ReportFlags.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.kohsuke.MetaInfServices;
import org.pescuma.buildhealth.analyser.BuildHealthAnalyser;
import org.pescuma.buildhealth.analyser.BuildHealthAnalyserPreference;
import org.pescuma.buildhealth.core.BuildData;
import org.pescuma.buildhealth.core.BuildData.Line;
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
	
	private static final int COLUMN_LANGUAGE = 1;
	private static final int COLUMN_FRAMEWORK = 2;
	static final int COLUMN_TYPE = 3;
	private static final int COLUMN_SUITE = 4;
	private static final int COLUMN_TEST = 5;
	private static final int COLUMN_MESSAGE = 6;
	private static final int COLUMN_STACK = 7;
	
	static final String TYPE_ERROR = "error";
	static final String TYPE_FAILED = "failed";
	static final String TYPE_PASSED = "passed";
	static final String TYPE_TIME = "time";
	
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
		
		boolean highlighProblems = (opts & HighlightProblems) != 0;
		boolean summaryOnly = (opts & SummaryOnly) != 0;
		
		if (highlighProblems && !hasProblems(data))
			highlighProblems = false;
		
		List<UnitTestReport> children = null;
		if (highlighProblems || !summaryOnly) {
			Tree tree = toTree(data);
			
			if (highlighProblems) {
				Trees trees = splitTree(tree);
				
				children = new ArrayList<UnitTestReport>();
				children.add(toReport("Failed", trees.failed.getStats(), createChildren(trees.failed)));
				
				if (!summaryOnly)
					children.add(toReport("Passed", trees.passed.getStats(), createChildren(trees.passed)));
				
			} else {
				children = createChildren(tree);
			}
		}
		
		return asList((Report) toReport(getName(), new Stats(data), children));
	}
	
	private Trees splitTree(Tree tree) {
		final Trees result = new Trees();
		result.failed = new Tree();
		result.passed = new Tree();
		
		tree.visit(new TreeVisitor() {
			private Language lang;
			private Framework fw;
			private Suite s;
			
			@Override
			void visitLanguage(Language language) {
				lang = language;
			}
			
			@Override
			void visitFramework(Framework framework) {
				fw = framework;
			}
			
			@Override
			void visitSuite(Suite suite) {
				s = suite;
				
				Stats old = suite.statsWithoutTests;
				
				int total = old.getTotal();
				if (total < 1)
					return;
				
				int errors = old.getErrors();
				int failures = old.getFailures();
				int problematic = errors + failures;
				Double time = old.getTime();
				
				if (problematic > 0 && total != problematic) {
					// Split
					
					Stats passed = result.passed.getLanguage(lang.name).getFramework(fw.name).getSuite(s.name).statsWithoutTests;
					passed.set(old);
					passed.types.remove(TYPE_ERROR);
					passed.types.remove(TYPE_FAILED);
					
					Stats failed = result.failed.getLanguage(lang.name).getFramework(fw.name).getSuite(s.name).statsWithoutTests;
					failed.add(TYPE_ERROR, errors);
					failed.add(TYPE_FAILED, failures);
					
					// Messages will get duplicated
					failed.message.append(old.message);
					
					if (time != null) {
						double passedTime = time * (total - problematic) / total;
						passed.types.get(TYPE_TIME).value = passedTime;
						failed.add(TYPE_TIME, time - passedTime);
					}
					
				} else {
					Tree other;
					if (problematic > 0)
						other = result.failed;
					else
						other = result.passed;
					
					other.getLanguage(lang.name).getFramework(fw.name).getSuite(s.name).statsWithoutTests.set(old);
				}
			}
			
			@Override
			void visitTest(Test test) {
				Stats old = test.stats;
				
				Tree other;
				if (old.getStatus() == BuildStatus.Good)
					other = result.passed;
				else
					other = result.failed;
				
				other.getLanguage(lang.name).getFramework(fw.name).getSuite(s.name).getTest(test.name).stats.set(old);
			}
		});
		
		return result;
	}
	
	private boolean hasProblems(BuildData data) {
		return !data.filter(COLUMN_TYPE, TYPE_FAILED).isEmpty() || !data.filter(COLUMN_TYPE, TYPE_ERROR).isEmpty();
	}
	
	private List<UnitTestReport> createChildren(Tree tree) {
		List<UnitTestReport> result = new ArrayList<UnitTestReport>();
		
		for (Language language : tree.getLanguages())
			for (Framework framework : language.getFrameworks())
				result.add(toReport(language.name + " - " + framework.name, framework.getStats(),
						createChildren(framework)));
		
		return result;
	}
	
	private List<UnitTestReport> createChildren(Framework framework) {
		List<UnitTestReport> result = new ArrayList<UnitTestReport>();
		
		for (Suite suite : framework.getSuites())
			result.add(toReport(suite.name, suite.getStats(), createChildren(suite)));
		
		return result;
	}
	
	private List<UnitTestReport> createChildren(Suite suite) {
		if (suite.getTests().isEmpty())
			return null;
		
		List<UnitTestReport> result = new ArrayList<UnitTestReport>();
		
		for (Test test : suite.getTests())
			result.add(toTestReport(test.name, test.getStats()));
		
		return result;
	}
	
	private UnitTestReport toReport(String name, Stats stats, List<UnitTestReport> children) {
		return new UnitTestReport(stats.getStatus(), name, stats.getPassed(), stats.getErrors(), stats.getFailures(),
				stats.getTime(), stats.computeDescription(), children);
	}
	
	private UnitTestReport toTestReport(String name, Stats stats) {
		return new UnitTestReport(stats.getStatus(), name, stats.getPassed(), stats.getErrors(), stats.getFailures(),
				stats.getTime(), stats.message.toString());
	}
	
	private Tree toTree(BuildData data) {
		Tree tree = new Tree();
		
		for (Line line : data.getLines()) {
			Framework framework = tree.getLanguage(line.getColumn(COLUMN_LANGUAGE))//
					.getFramework(line.getColumn(COLUMN_FRAMEWORK));
			
			String suiteName = line.getColumn(COLUMN_SUITE);
			if (suiteName.isEmpty())
				suiteName = "No suite name";
			
			Suite suite = framework.getSuite(suiteName);
			
			String testName = line.getColumn(COLUMN_TEST);
			
			Stats stats = (testName.isEmpty() ? suite.statsWithoutTests : suite.getTest(testName).stats);
			
			stats.add(line.getColumn(COLUMN_TYPE), line.getValue());
			
			String message = line.getColumn(COLUMN_MESSAGE);
			String stack = line.getColumn(COLUMN_STACK);
			if (!message.isEmpty() || !stack.isEmpty()) {
				if (stats.message.length() > 0)
					stats.message.append("\n");
				if (!message.isEmpty())
					stats.message.append(message).append("\n");
				if (!stack.isEmpty())
					stats.message.append(stack).append("\n");
			}
		}
		
		tree.visit(new TreeVisitor() {
			@Override
			void visitTest(Test test) {
				Double time = test.stats.getTime();
				if (time != null) {
					if (test.stats.message.length() > 0)
						test.stats.message.append("\n");
					test.stats.message.append("Executed in ").append(format1000(time, "s"));
				}
			}
		});
		return tree;
	}
	
	private static class Trees {
		Tree passed;
		Tree failed;
	}
}
