package org.pescuma.buildhealth.core;

import static java.util.Arrays.*;
import static org.junit.Assert.*;
import static org.pescuma.buildhealth.core.BuildStatus.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.pescuma.buildhealth.core.BuildHealth.ReportFlags;
import org.pescuma.buildhealth.extractor.BuildDataExtractor;
import org.pescuma.buildhealth.extractor.BuildDataExtractorTracker;
import org.pescuma.buildhealth.prefs.Preferences;
import org.pescuma.buildhealth.projects.Projects;
import org.pescuma.datatable.DataTable;

public class BuildHealthTest {
	
	private BuildHealth buildhealth;
	
	@Before
	public void setUp() throws Exception {
		buildhealth = new BuildHealth();
	}
	
	@Test
	public void testNoData() {
		assertEquals(null, buildhealth.generateReport(0));
	}
	
	@Test
	public void testNoDataWithAnalyser() {
		buildhealth.addAnalyser(new BaseBuildHealthAnalyser() {
			@Override
			public List<Report> computeReport(DataTable data, Projects projects, Preferences prefs, int opts) {
				return asList(new Report(BuildStatus.Good, "", "", ""));
			}
		});
		
		assertEquals(null, buildhealth.generateReport(0));
	}
	
	@Test
	public void testExtractGenerateSimpleReport() {
		buildhealth.extract(new BuildDataExtractor() {
			@Override
			public void extractTo(DataTable data, BuildDataExtractorTracker tracker) {
				data.add(10, "Unit test", "java", "passed");
			}
		});
		
		buildhealth.addAnalyser(new BaseBuildHealthAnalyser() {
			@Override
			public List<Report> computeReport(DataTable data, Projects projects, Preferences prefs, int opts) {
				return asList(new Report(BuildStatus.Good, "Unit tests", "100%", data.filter("Unit test").sum()
						+ " passed"));
			}
		});
		
		assertEquals(new BuildReport(Good, "Build", "Good", null, //
				new Report(BuildStatus.Good, "Unit tests", "100%", "10.0 passed") //
				), //
				buildhealth.generateReport(ReportFlags.SummaryOnly));
	}
}
