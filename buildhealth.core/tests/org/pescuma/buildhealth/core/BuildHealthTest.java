package org.pescuma.buildhealth.core;

import static java.util.Arrays.*;
import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.pescuma.buildhealth.core.BuildData;
import org.pescuma.buildhealth.core.BuildDataExtractor;
import org.pescuma.buildhealth.core.BuildHealth;
import org.pescuma.buildhealth.core.BuildHealthAnalyser;
import org.pescuma.buildhealth.core.BuildStatus;
import org.pescuma.buildhealth.core.FinalReport;
import org.pescuma.buildhealth.core.Report;

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
				return asList((Report) new FinalReport(BuildStatus.Successful, "", "", ""));
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
				return asList((Report) new FinalReport(BuildStatus.Successful, "Unit tests", "100%", data.filter(
						"Unit test").sum()
						+ " passed"));
			}
		});
		
		assertEquals("Build was SUCCESSFUL\nUnit tests: 100% [10.0 passed]\n", buildhealth.generateSimpleReport());
	}
}
