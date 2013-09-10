package org.pescuma.buildhealth.analyser.unittest;

import static org.pescuma.buildhealth.core.BuildHealth.ReportFlags.*;
import static org.pescuma.buildhealth.core.BuildStatus.*;

import org.junit.Before;
import org.junit.Test;
import org.pescuma.buildhealth.analyser.BaseAnalyserTest;
import org.pescuma.buildhealth.core.Report;

public class UnitTestAnalyserTest extends BaseAnalyserTest {
	
	@Before
	public void setUp() {
		super.setUp(new UnitTestAnalyser());
	}
	
	private void createPassed(int i) {
		data.add(i, "Unit test", "java", "junit", "passed");
	}
	
	private void createFailed(int i) {
		data.add(i, "Unit test", "java", "junit", "failed");
	}
	
	private void createError(int i) {
		data.add(i, "Unit test", "java", "junit", "error");
	}
	
	private void createTime(double i) {
		data.add(i, "Unit test", "java", "junit", "time");
	}
	
	@Test
	public void testAllPassed() {
		createPassed(1);
		createPassed(9);
		
		Report report = createReport();
		
		assertReport(new Report(Good, "Unit tests", "PASSED", "10 tests, 10 passed"), report);
	}
	
	@Test
	public void testAllPassedWithTime() {
		createPassed(5);
		createTime(1);
		createTime(1.01);
		createTime(10);
		
		Report report = createReport();
		
		assertReport(new Report(Good, "Unit tests", "PASSED", "5 tests, 5 passed (12.01 s)"), report);
	}
	
	@Test
	public void testPassedFailed() {
		createPassed(1);
		createFailed(1);
		
		Report report = createReport();
		
		assertReport(new Report(Problematic, "Unit tests", "FAILED", "2 tests, 1 passed, 1 failure"), report);
	}
	
	@Test
	public void testFailed() {
		createFailed(1);
		
		Report report = createReport();
		
		assertReport(new Report(Problematic, "Unit tests", "FAILED", "1 test, 1 failure"), report);
	}
	
	@Test
	public void testFailedPlural() {
		createFailed(2);
		
		Report report = createReport();
		
		assertReport(new Report(Problematic, "Unit tests", "FAILED", "2 tests, 2 failures"), report);
	}
	
	@Test
	public void testError() {
		createError(1);
		
		Report report = createReport();
		
		assertReport(new Report(Problematic, "Unit tests", "FAILED", "1 test, 1 error"), report);
	}
	
	@Test
	public void testErrorPlural() {
		createError(2);
		
		Report report = createReport();
		
		assertReport(new Report(Problematic, "Unit tests", "FAILED", "2 tests, 2 errors"), report);
	}
	
	@Test
	public void testEverything() {
		for (int i = 1; i < 4; i++) {
			createPassed(i);
			createError(i);
			createFailed(i);
			createTime(i);
		}
		
		Report report = createReport();
		
		assertReport(new Report(Problematic, "Unit tests", "FAILED", "18 tests, 6 passed, 6 errors, 6 failures (6 s)"),
				report);
	}
	
	@Test
	public void testSummaryHighlightProblems() {
		createPassed(1);
		createFailed(1);
		
		Report report = createReport(SummaryOnly | HighlightProblems);
		
		assertReport(new Report(Problematic, "Unit tests", "FAILED", "2 tests, 1 passed, 1 failure", //
				new Report(Problematic, "Failed", "FAILED", "1 test, 1 failure", //
						new Report(Problematic, "java - junit", "FAILED", "1 test, 1 failure", //
								new Report(Problematic, "No suite name", "FAILED", "1 test, 1 failure") //
						) //
				) //
				), report);
	}
	
	@Test
	public void testFull() {
		createPassed(1);
		createFailed(1);
		
		Report report = createReport(Full);
		
		assertReport(new Report(Problematic, "Unit tests", "FAILED", "2 tests, 1 passed, 1 failure", //
				new Report(Problematic, "java - junit", "FAILED", "2 tests, 1 passed, 1 failure", //
						new Report(Problematic, "No suite name", "FAILED", "2 tests, 1 passed, 1 failure") //
				) //
				), report);
	}
	
	@Test
	public void testHighlightProblems() {
		createPassed(1);
		createFailed(1);
		
		Report report = createReport(HighlightProblems);
		
		assertReport(new Report(Problematic, "Unit tests", "FAILED", "2 tests, 1 passed, 1 failure", //
				new Report(Problematic, "Failed", "FAILED", "1 test, 1 failure", //
						new Report(Problematic, "java - junit", "FAILED", "1 test, 1 failure", //
								new Report(Problematic, "No suite name", "FAILED", "1 test, 1 failure") //
						) //
				), //
				new Report(Good, "Passed", "PASSED", "1 test, 1 passed", //
						new Report(Good, "java - junit", "PASSED", "1 test, 1 passed", //
								new Report(Good, "No suite name", "PASSED", "1 test, 1 passed") //
						) //
				) //
				), report);
	}
	
	@Test
	public void testHighlightProblemsWithTimeForSuite() {
		createPassed(1);
		createFailed(1);
		createTime(1);
		
		Report report = createReport(HighlightProblems);
		
		assertReport(new Report(Problematic, "Unit tests", "FAILED", "2 tests, 1 passed, 1 failure (1 s)", //
				new Report(Problematic, "Failed", "FAILED", "1 test, 1 failure (500 ms)", //
						new Report(Problematic, "java - junit", "FAILED", "1 test, 1 failure (500 ms)", //
								new Report(Problematic, "No suite name", "FAILED", "1 test, 1 failure (500 ms)") //
						) //
				), //
				new Report(Good, "Passed", "PASSED", "1 test, 1 passed (500 ms)", //
						new Report(Good, "java - junit", "PASSED", "1 test, 1 passed (500 ms)", //
								new Report(Good, "No suite name", "PASSED", "1 test, 1 passed (500 ms)") //
						) //
				) //
				), report);
	}
	
	@Test
	public void testHighlightProblemsWithTimeForTests() {
		data.add(1, "Unit test", "java", "junit", "passed", "Suite", "Test 1");
		data.add(0.8, "Unit test", "java", "junit", "time", "Suite", "Test 1");
		data.add(1, "Unit test", "java", "junit", "failed", "Suite", "Test 2");
		data.add(0.2, "Unit test", "java", "junit", "time", "Suite", "Test 2");
		
		Report report = createReport(HighlightProblems);
		
		assertReport(new Report(Problematic, "Unit tests", "FAILED", "2 tests, 1 passed, 1 failure (1 s)", //
				new Report(Problematic, "Failed", "FAILED", "1 test, 1 failure (200 ms)", //
						new Report(Problematic, "java - junit", "FAILED", "1 test, 1 failure (200 ms)", //
								new Report(Problematic, "Suite", "FAILED", "1 test, 1 failure (200 ms)", //
										new Report(Problematic, "Test 2", "FAILED", "Executed in 200 ms") //
								) //
						) //
				), //
				new Report(Good, "Passed", "PASSED", "1 test, 1 passed (800 ms)", //
						new Report(Good, "java - junit", "PASSED", "1 test, 1 passed (800 ms)", //
								new Report(Good, "Suite", "PASSED", "1 test, 1 passed (800 ms)", //
										new Report(Good, "Test 1", "PASSED", "Executed in 800 ms") //
								) //
						) //
				) //
				), report);
	}
	
	@Test
	public void testFullWithTimeForTests() {
		data.add(1, "Unit test", "java", "junit", "passed", "Suite", "Test 1");
		data.add(0.8, "Unit test", "java", "junit", "time", "Suite", "Test 1");
		data.add(1, "Unit test", "java", "junit", "failed", "Suite", "Test 2");
		data.add(0.2, "Unit test", "java", "junit", "time", "Suite", "Test 2");
		
		Report report = createReport(Full);
		
		assertReport(new Report(Problematic, "Unit tests", "FAILED", "2 tests, 1 passed, 1 failure (1 s)", //
				new Report(Problematic, "java - junit", "FAILED", "2 tests, 1 passed, 1 failure (1 s)", //
						new Report(Problematic, "Suite", "FAILED", "2 tests, 1 passed, 1 failure (1 s)", //
								new Report(Good, "Test 1", "PASSED", "Executed in 800 ms"), //
								new Report(Problematic, "Test 2", "FAILED", "Executed in 200 ms") //
						) //
				) //
				), report);
	}
}
