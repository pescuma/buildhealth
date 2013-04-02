package org.pescuma.buildhealth.core;

import static org.junit.Assert.*;
import static org.pescuma.buildhealth.core.BuildStatus.*;

import org.junit.Before;
import org.junit.Test;

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
		assertEquals("Your build is GOOD\n    Unit tests: 100% [10.0 passed]\n", formater.format(new Report(Good,
				"Build", "Good", new Report(BuildStatus.Good, "Unit tests", "100%", "10.0 passed"))));
	}
	
}
