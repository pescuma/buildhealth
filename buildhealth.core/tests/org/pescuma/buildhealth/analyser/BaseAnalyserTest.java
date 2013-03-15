package org.pescuma.buildhealth.analyser;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.pescuma.buildhealth.core.Report;
import org.pescuma.buildhealth.core.table.BuildDataTable;

public abstract class BaseAnalyserTest {
	
	protected BuildDataTable data;
	private BuildHealthAnalyser analyser;
	
	protected void setUp(BuildHealthAnalyser analyser) {
		this.analyser = analyser;
		this.data = new BuildDataTable();
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
