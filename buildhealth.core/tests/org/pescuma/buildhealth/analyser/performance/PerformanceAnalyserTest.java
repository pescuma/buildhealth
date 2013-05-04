package org.pescuma.buildhealth.analyser.performance;

import static org.junit.Assert.*;
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
	
}
