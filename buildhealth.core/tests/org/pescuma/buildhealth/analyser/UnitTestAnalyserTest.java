package org.pescuma.buildhealth.analyser;

import static org.junit.Assert.*;
import static org.pescuma.buildhealth.core.BuildStatus.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.pescuma.buildhealth.core.Report;
import org.pescuma.buildhealth.core.table.BuildDataTable;

public class UnitTestAnalyserTest {
	
	private BuildDataTable data;
	private UnitTestAnalyser analyser;
	
	@Before
	public void setUp() throws Exception {
		data = new BuildDataTable();
		analyser = new UnitTestAnalyser();
	}
	
	@Test
	public void testNoData() {
		assertEquals(0, analyser.computeSimpleReport(data).size());
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
	
	private Report createReport() {
		List<Report> report = analyser.computeSimpleReport(data);
		assertEquals(1, report.size());
		return report.get(0);
	}
	
	@Test
	public void testAllPassed() {
		createPassed(1);
		createPassed(9);
		
		Report report = createReport();
		
		assertEquals(new Report(Good, "Unit tests", "PASSED", "10 tests, 10 passed"), report);
	}
	
	@Test
	public void testAllPassedWithTime() {
		createPassed(5);
		createTime(1);
		createTime(1.01);
		createTime(10);
		
		Report report = createReport();
		
		assertEquals(new Report(Good, "Unit tests", "PASSED", "5 tests, 5 passed (12.01 s)"), report);
	}
	
	@Test
	public void testPassedFailed() {
		createPassed(1);
		createFailed(1);
		
		Report report = createReport();
		
		assertEquals(new Report(Problematic, "Unit tests", "FAILED", "2 tests, 1 passed, 1 failure"), report);
	}
	
	@Test
	public void testFailed() {
		createFailed(1);
		
		Report report = createReport();
		
		assertEquals(new Report(Problematic, "Unit tests", "FAILED", "1 test, 1 failure"), report);
	}
	
	@Test
	public void testFailedPlural() {
		createFailed(2);
		
		Report report = createReport();
		
		assertEquals(new Report(Problematic, "Unit tests", "FAILED", "2 tests, 2 failures"), report);
	}
	
	@Test
	public void testError() {
		createError(1);
		
		Report report = createReport();
		
		assertEquals(new Report(Problematic, "Unit tests", "FAILED", "1 test, 1 error"), report);
	}
	
	@Test
	public void testErrorPlural() {
		createError(2);
		
		Report report = createReport();
		
		assertEquals(new Report(Problematic, "Unit tests", "FAILED", "2 tests, 2 errors"), report);
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
		
		assertEquals(
				new Report(Problematic, "Unit tests", "FAILED", "18 tests, 6 passed, 6 errors, 6 failures (6.0 s)"),
				report);
	}
}
