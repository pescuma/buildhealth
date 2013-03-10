package org.pescuma.buildhealth.core;

import static java.util.Arrays.*;
import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class BuildHealthTest {
	
	@Before
	public void setUp() throws Exception {
	}
	
	@Test
	public void testNoData() {
		BuildHealth buildhealth = new BuildHealth();
		
		assertEquals("No data to generate report", buildhealth.generateSimpleReport());
	}
	
	@Test
	public void testNoDataWithAnalyser() {
		BuildHealth buildhealth = new BuildHealth();
		
		buildhealth.addAnalyser(new BuildHealthAnalyser() {
			@Override
			public List<Report> computeSimpleReport(BuildData data) {
				return asList(new Report(BuildStatus.Good, "", "", ""));
			}
		});
		
		assertEquals("No data to generate report", buildhealth.generateSimpleReport());
	}
	
	@Test
	public void testExtractGenerateSimpleReport() {
		BuildHealth buildhealth = new BuildHealth();
		
		buildhealth.extract(new BuildDataExtractor() {
			@Override
			public void extractTo(BuildData data) {
				data.add(10, "Unit test", "java", "passed");
			}
		});
		
		buildhealth.addAnalyser(new BuildHealthAnalyser() {
			@Override
			public List<Report> computeSimpleReport(BuildData data) {
				return asList(new Report(BuildStatus.Good, "Unit tests", "100%", data.filter("Unit test").sum()
						+ " passed"));
			}
		});
		
		assertEquals("Your build is GOOD\n    Unit tests: 100% [10.0 passed]\n", buildhealth.generateSimpleReport());
	}
}
