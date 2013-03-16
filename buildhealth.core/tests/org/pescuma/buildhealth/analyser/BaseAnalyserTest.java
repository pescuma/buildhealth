package org.pescuma.buildhealth.analyser;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Locale;

import org.junit.After;
import org.junit.Test;
import org.pescuma.buildhealth.core.Report;
import org.pescuma.buildhealth.core.table.BuildDataTable;

public abstract class BaseAnalyserTest {
	
	protected Locale oldLocale;
	protected BuildDataTable data;
	private BuildHealthAnalyser analyser;
	
	protected void setUp(BuildHealthAnalyser analyser) {
		oldLocale = Locale.getDefault();
		Locale.setDefault(Locale.US);
		
		this.analyser = analyser;
		this.data = new BuildDataTable();
	}
	
	@After
	public void tearDown() {
		Locale.setDefault(oldLocale);
	}
	
	protected Report createReport() {
		List<Report> report = analyser.computeSimpleReport(data);
		assertEquals(1, report.size());
		return report.get(0);
	}
	
	@Test
	public void testNoData() {
		assertEquals(0, analyser.computeSimpleReport(data).size());
	}
	
}
