package org.pescuma.buildhealth.analyser.coverage;

import static org.junit.Assert.*;
import static org.pescuma.buildhealth.core.BuildStatus.*;

import org.junit.Before;
import org.junit.Test;
import org.pescuma.buildhealth.analyser.BaseAnalyserTest;
import org.pescuma.buildhealth.core.Report;

public class CoverageAnalyserTest extends BaseAnalyserTest {
	
	private CoverageAnalyser analyser;
	
	@Before
	public void setUp() {
		analyser = new CoverageAnalyser();
		analyser.setShowDetailsInDescription(true);
		super.setUp(analyser);
	}
	
	private void create(String type, int covered, int total) {
		data.add(covered, "Coverage", "java", "emma", type, "covered", "all");
		data.add(total, "Coverage", "java", "emma", type, "total", "all");
	}
	
	@Test
	public void testOnlyLines() {
		create("line", 1, 2);
		
		Report report = createReport();
		
		assertEquals(new Report(Good, "Coverage", "50%", "line: 50% (1/2)"), report);
	}
	
	@Test
	public void testOnlyLinesNoDetail() {
		create("line", 1, 2);
		analyser.setShowDetailsInDescription(false);
		
		Report report = createReport();
		
		assertEquals(new Report(Good, "Coverage", "50%", "line: 50%"), report);
	}
	
	@Test
	public void testMultipleWithLine() {
		create("a", 3, 4);
		create("line", 4, 8);
		create("b", 4, 4);
		
		Report report = createReport();
		
		assertEquals(new Report(Good, "Coverage", "50%", "line: 50% (4/8), a: 75% (3/4), b: 100% (4/4)"), report);
	}
	
	@Test
	public void testLineIntruction_FirstLine() {
		create("line", 1, 4);
		create("instruction", 2, 4);
		
		Report report = createReport();
		
		assertEquals(new Report(Good, "Coverage", "50%", "line: 25% (1/4), instruction: 50% (2/4)"), report);
	}
	
	@Test
	public void testLineIntruction_FirstIntruction() {
		create("instruction", 2, 4);
		create("line", 1, 4);
		
		Report report = createReport();
		
		assertEquals(new Report(Good, "Coverage", "50%", "line: 25% (1/4), instruction: 50% (2/4)"), report);
	}
	
	@Test
	public void testLineIntruction_MoreLines() {
		create("line", 1, 4);
		create("line", 1, 4);
		create("instruction", 2, 4);
		
		Report report = createReport();
		
		assertEquals(new Report(Good, "Coverage", "50%", "line: 25% (2/8), instruction: 50% (2/4)"), report);
	}
	
	@Test
	public void testLineIntruction_MoreIntructions() {
		create("line", 1, 4);
		create("instruction", 2, 4);
		create("instruction", 2, 4);
		
		Report report = createReport();
		
		assertEquals(new Report(Good, "Coverage", "50%", "line: 25% (1/4), instruction: 50% (4/8)"), report);
	}
	
	@Test
	public void testUnknownType() {
		data.add(10, "Coverage", "java", "emma", "all", "unknown", "all");
		
		assertEquals(0, analyser.computeSimpleReport(data, null).size());
	}
	
	@Test
	public void testOnlyCovered() {
		data.add(10, "Coverage", "java", "emma", "all", "covered", "all");
		
		assertEquals(0, analyser.computeSimpleReport(data, null).size());
	}
	
}
