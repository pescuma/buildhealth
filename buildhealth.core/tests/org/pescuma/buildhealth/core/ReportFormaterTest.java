package org.pescuma.buildhealth.core;

import static java.util.Arrays.*;
import static org.junit.Assert.*;
import static org.pescuma.buildhealth.core.BuildStatus.*;

import org.junit.Before;
import org.junit.Test;
import org.pescuma.buildhealth.utils.ReportFormater;

public class ReportFormaterTest {
	
	private ReportFormater formater;
	
	@Before
	public void setUp() throws Exception {
		formater = new ReportFormater();
	}
	
	@Test
	public void testNoData() {
		assertEquals("No data to generate report\n", formater.format(null));
	}
	
	@Test
	public void testExtractGenerateSimpleReport() {
		assertEquals("Your build is GOOD\n" //
				+ "    Unit tests: 100% [10.0 passed]\n", //
				formater.format( //
				new BuildReport(Good, "Build", "Good", null, //
						new Report(BuildStatus.Good, "Unit tests", "100%", "10.0 passed", false) //
				)));
	}
	
	@Test
	public void testExtractGenerateReportWithSources() {
		Report problematic = new Report(BuildStatus.Problematic, "Unit tests", "0%", "10.0 failed", true);
		
		assertEquals("Your build is PROBLEMATIC\n" //
				+ "    Unit tests: 100% [10.0 passed]\n" //
				+ "    Unit tests: 0% [10.0 failed]\n" //
				+ "\n" //
				+ "Sources of instability:\n" //
				+ "    Unit tests: 0%\n" //
		, //
				formater.format( //
				new BuildReport(Problematic, "Build", "Problematic", asList(problematic), //
						new Report(BuildStatus.Good, "Unit tests", "100%", "10.0 passed", false), //
						problematic //
				)));
	}
	
}
