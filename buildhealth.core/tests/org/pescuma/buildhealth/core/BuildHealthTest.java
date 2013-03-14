package org.pescuma.buildhealth.core;

import static java.util.Arrays.*;
import static org.junit.Assert.*;
import static org.pescuma.buildhealth.core.BuildStatus.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.pescuma.buildhealth.analyser.BuildHealthAnalyser;
import org.pescuma.buildhealth.extractor.BuildDataExtractor;

public class BuildHealthTest {
	
	private BuildHealth buildhealth;
	
	@Before
	public void setUp() throws Exception {
		buildhealth = new BuildHealth();
	}
	
	@Test
	public void testNoData() {
		assertEquals(null, buildhealth.generateReportSummary());
	}
	
	@Test
	public void testNoDataWithAnalyser() {
		buildhealth.addAnalyser(new BuildHealthAnalyser() {
			@Override
			public List<Report> computeSimpleReport(BuildData data) {
				return asList(new Report(BuildStatus.Good, "", "", ""));
			}
		});
		
		assertEquals(null, buildhealth.generateReportSummary());
	}
	
	@Test
	public void testExtractGenerateSimpleReport() {
		buildhealth.extract(new BuildDataExtractor() {
			@Override
			public void extractTo(BuildData data, BuildDataExtractorTracker tracker) {
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
		
		assertEquals(new Report(Good, "Build", "Good",
				new Report(BuildStatus.Good, "Unit tests", "100%", "10.0 passed")), buildhealth.generateReportSummary());
	}
}
