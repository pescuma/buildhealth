package org.pescuma.buildhealth.analyser.unittest;

import static org.junit.Assert.*;
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
	
	private void assertReport(Report expected, Report actual) {
		assertEquals(expected.getStatus(), actual.getStatus());
		assertEquals(expected.getName(), actual.getName());
		assertEquals(expected.getValue(), actual.getValue());
		assertEquals(expected.getDescription(), actual.getDescription());
		assertEquals(0, actual.getChildren().size());
	}
}
