package org.pescuma.buildhealth.analyser;

import static org.junit.Assert.*;
import static org.pescuma.buildhealth.core.BuildHealth.ReportFlags.*;

import java.util.List;
import java.util.Locale;

import org.junit.After;
import org.junit.Test;
import org.pescuma.buildhealth.core.Report;
import org.pescuma.buildhealth.core.data.BuildDataTable;
import org.pescuma.buildhealth.prefs.MemoryPreferencesStore;
import org.pescuma.buildhealth.prefs.Preferences;

public abstract class BaseAnalyserTest {
	
	protected Locale oldLocale;
	protected BuildDataTable data;
	protected Preferences prefs;
	private BuildHealthAnalyser analyser;
	
	protected void setUp(BuildHealthAnalyser analyser) {
		oldLocale = Locale.getDefault();
		Locale.setDefault(Locale.US);
		
		this.analyser = analyser;
		this.data = new BuildDataTable();
		this.prefs = new Preferences(new MemoryPreferencesStore());
	}
	
	@After
	public void tearDown() {
		Locale.setDefault(oldLocale);
	}
	
	protected Report createReport() {
		return createReport(SummaryOnly);
	}
	
	protected Report createReport(int opts) {
		List<Report> report = analyser.computeReport(data, prefs, opts);
		assertEquals(1, report.size());
		return report.get(0);
	}
	
	protected void assertReport(Report expected, Report actual) {
		assertEquals(expected.getStatus(), actual.getStatus());
		assertEquals(expected.getName(), actual.getName());
		assertEquals(expected.getValue(), actual.getValue());
		assertEquals(expected.getDescription(), actual.getDescription());
		
		List<Report> expectedChildren = expected.getChildren();
		List<Report> actualChildren = actual.getChildren();
		assertEquals(expectedChildren.size(), actualChildren.size());
		for (int i = 0; i < expectedChildren.size(); i++)
			assertReport(expectedChildren.get(i), actualChildren.get(i));
	}
	
	@Test
	public void testNoData() {
		assertEquals(0, analyser.computeReport(data, prefs, SummaryOnly).size());
	}
	
}