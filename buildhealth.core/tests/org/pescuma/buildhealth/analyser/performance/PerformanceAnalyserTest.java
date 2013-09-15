package org.pescuma.buildhealth.analyser.performance;

import static org.junit.Assert.*;
import static org.pescuma.buildhealth.core.BuildHealth.ReportFlags.*;
import static org.pescuma.buildhealth.core.BuildStatus.*;

import org.junit.Before;
import org.junit.Test;
import org.pescuma.buildhealth.analyser.BaseAnalyserTest;
import org.pescuma.buildhealth.core.Report;

public class PerformanceAnalyserTest extends BaseAnalyserTest {
	
	private PerformanceAnalyser analyser;
	
	@Before
	public void setUp() {
		analyser = new PerformanceAnalyser();
		super.setUp(analyser);
	}
	
	private void create(String type, double val) {
		data.add(val, "Performance", "Java", "Japex", type);
	}
	
	private void create(String name, String type, double val) {
		data.add(val, "Performance", "Java", "Japex", type, name);
	}
	
	@Test
	public void testOnlyMs() {
		create("ms", 1);
		create("ms", 1);
		
		Report report = createReport();
		
		assertEquals(new Report(Good, "Performance", "2 ms"), report);
	}
	
	@Test
	public void testOnlyMsBig() {
		create("ms", 10000);
		create("ms", 100);
		
		Report report = createReport();
		
		assertEquals(new Report(Good, "Performance", "10.1 s"), report);
	}
	
	@Test
	public void testOnlyRunsPerS() {
		create("runsPerS", 10);
		create("runsPerS", 1);
		
		Report report = createReport();
		
		assertEquals(new Report(Good, "Performance", "11 runs per second"), report);
	}
	
	@Test
	public void testMixed() {
		create("ms", 10);
		create("runsPerS", 10);
		create("runsPerS", 50);
		
		Report report = createReport();
		
		assertEquals(new Report(Good, "Performance", "130 ms"), report);
	}
	
	@Test
	public void testLimitMsGood() {
		create("ms", 100);
		create("runsPerS", 10);
		prefs.child("performance").child("ms").set("good", 300);
		
		Report report = createReport();
		
		assertEquals(new Report(Good, "Performance", "200 ms"), report);
	}
	
	@Test
	public void testLimitMsSoSo() {
		create("ms", 100);
		create("runsPerS", 10);
		prefs.child("performance").child("ms").set("good", 100);
		prefs.child("performance").child("ms").set("warn", 300);
		
		Report report = createReport();
		
		assertEquals(new Report(SoSo, "Performance", "200 ms"), report);
	}
	
	@Test
	public void testLimitMsProblematic() {
		create("ms", 100);
		create("runsPerS", 10);
		prefs.child("performance").child("ms").set("warn", 150);
		
		Report report = createReport();
		
		assertEquals(new Report(Problematic, "Performance", "200 ms"), report);
	}
	
	@Test
	public void testLimitRunsPerSGood() {
		create("ms", 100);
		create("runsPerS", 10);
		prefs.child("performance").child("runsPerS").set("good", 10);
		
		Report report = createReport();
		
		assertEquals(new Report(Good, "Performance", "200 ms"), report);
	}
	
	@Test
	public void testLimitRunsPerSSoSo() {
		create("ms", 100);
		create("runsPerS", 10);
		prefs.child("performance").child("runsPerS").set("good", 30);
		prefs.child("performance").child("runsPerS").set("warn", 10);
		
		Report report = createReport();
		
		assertEquals(new Report(SoSo, "Performance", "200 ms"), report);
	}
	
	@Test
	public void testLimitRunsPerSProblematic() {
		create("ms", 100);
		create("runsPerS", 10);
		prefs.child("performance").child("runsPerS").set("warn", 30);
		
		Report report = createReport();
		
		assertEquals(new Report(Problematic, "Performance", "200 ms"), report);
	}
	
	@Test
	public void testReportMs() {
		create("runsPerS", 10);
		prefs.child("performance").set("report", "ms");
		
		Report report = createReport();
		
		assertEquals(new Report(Good, "Performance", "100 ms"), report);
	}
	
	@Test
	public void testReportRunsPerS() {
		create("ms", 100);
		prefs.child("performance").set("report", "runsPerS");
		
		Report report = createReport();
		
		assertEquals(new Report(Good, "Performance", "10 runs per second"), report);
	}
	
	@Test
	public void testReport_Full_OneEntry() {
		create("A/B", "ms", 10);
		
		Report report = createReport(Full);
		
		assertEquals(new Report(Good, "Performance", "10 ms", //
				new Report(Good, "Java", "10 ms", //
						new Report(Good, "Japex", "10 ms", //
								new Report(Good, "A", "10 ms", //
										new Report(Good, "B", "10 ms") //
								) //
						) //
				) //
				), report);
	}
	
	@Test
	public void testReport_Full_FewEntries() {
		create("A/B", "ms", 10);
		create("A/C", "ms", 20);
		create("X/Y", "runsPerS", 10);
		
		Report report = createReport(Full);
		
		assertEquals(new Report(Good, "Performance", "130 ms", //
				new Report(Good, "Java", "130 ms", //
						new Report(Good, "Japex", "130 ms", //
								new Report(Good, "A", "30 ms", //
										new Report(Good, "B", "10 ms"), //
										new Report(Good, "C", "20 ms") //
								), //
								new Report(Good, "X", "10 runs per second", //
										new Report(Good, "Y", "10 runs per second") //
								) //
						) //
				) //
				), report);
	}
	
	@Test
	public void testReport_Full_RootSoSo() {
		create("A/B", "ms", 10);
		create("A/C", "ms", 20);
		create("X/Y", "runsPerS", 10);
		
		prefs.child("performance").child("ms").set("good", 5);
		
		Report report = createReport(Full);
		
		assertEquals(new Report(SoSo, "Performance", "130 ms", //
				new Report(Good, "Java", "130 ms", //
						new Report(Good, "Japex", "130 ms", //
								new Report(Good, "A", "30 ms", //
										new Report(Good, "B", "10 ms"), //
										new Report(Good, "C", "20 ms") //
								), //
								new Report(Good, "X", "10 runs per second", //
										new Report(Good, "Y", "10 runs per second") //
								) //
						) //
				) //
				), report);
	}
	
	@Test
	public void testReport_Full_NodesDifferentStatusByFullName() {
		create("A/B", "ms", 10);
		create("A/C", "ms", 20);
		create("X/Y", "runsPerS", 10);
		
		prefs.child("performance", "A/B", "ms").set("good", 5);
		prefs.child("performance", "X/Y", "ms").set("warn", 30);
		
		Report report = createReport(Full);
		
		assertEquals(new Report(Problematic, "Performance", "130 ms", //
				new Report(Good, "Java", "130 ms", //
						new Report(Good, "Japex", "130 ms", //
								new Report(Good, "A", "30 ms", //
										new Report(SoSo, "B", "10 ms"), //
										new Report(Good, "C", "20 ms") //
								), //
								new Report(Good, "X", "10 runs per second", //
										new Report(Problematic, "Y", "10 runs per second") //
								) //
						) //
				) //
				), report);
	}
	
	@Test
	public void testReport_Full_NodesDifferentStatusByNameSplit() {
		create("A/B", "ms", 10);
		create("A/C", "ms", 20);
		create("X/Y", "runsPerS", 10);
		
		prefs.child("performance", "Java", "Japex", "A", "B", "ms").set("good", 5);
		prefs.child("performance", "Java", "Japex", "X", "Y", "ms").set("warn", 30);
		
		Report report = createReport(Full);
		
		assertEquals(new Report(Problematic, "Performance", "130 ms", //
				new Report(Good, "Java", "130 ms", //
						new Report(Good, "Japex", "130 ms", //
								new Report(Good, "A", "30 ms", //
										new Report(SoSo, "B", "10 ms"), //
										new Report(Good, "C", "20 ms") //
								), //
								new Report(Good, "X", "10 runs per second", //
										new Report(Problematic, "Y", "10 runs per second") //
								) //
						) //
				) //
				), report);
	}
	
	@Test
	public void testReport_FullHighlightProblems_NodesDifferentStatusByNameSplit() {
		create("A/B", "ms", 10);
		create("A/C", "ms", 20);
		create("X/Y", "runsPerS", 10);
		
		prefs.child("performance", "Java", "Japex", "A", "C", "ms").set("good", 5);
		prefs.child("performance", "Java", "Japex", "X", "Y", "ms").set("warn", 30);
		
		Report report = createReport(Full | HighlightProblems);
		
		assertEquals(new Report(Problematic, "Performance", "130 ms", //
				new Report(Good, "Java", "130 ms", //
						new Report(Good, "Japex", "130 ms", //
								new Report(Good, "X", "10 runs per second", //
										new Report(Problematic, "Y", "10 runs per second") //
								), //
								new Report(Good, "A", "30 ms", //
										new Report(SoSo, "C", "20 ms"), //
										new Report(Good, "B", "10 ms") //
								) //
						) //
				) //
				), report);
	}
	
	@Test
	public void testReport_SummaryOnlyHighlightProblems_NodesDifferentStatusByNameSplit() {
		create("A/B", "ms", 10);
		create("A/C", "ms", 20);
		create("X/Y", "runsPerS", 10);
		
		prefs.child("performance", "Java", "Japex", "A", "C", "ms").set("good", 5);
		
		Report report = createReport(SummaryOnly | HighlightProblems);
		
		assertEquals(new Report(SoSo, "Performance", "130 ms", //
				new Report(Good, "Java", "130 ms", //
						new Report(Good, "Japex", "130 ms", //
								new Report(Good, "A", "30 ms", //
										new Report(SoSo, "C", "20 ms") //
								) //
						) //
				) //
				), report);
	}
	
}
