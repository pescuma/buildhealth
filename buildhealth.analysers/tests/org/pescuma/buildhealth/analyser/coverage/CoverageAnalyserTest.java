package org.pescuma.buildhealth.analyser.coverage;

import static org.junit.Assert.*;
import static org.pescuma.buildhealth.core.BuildHealth.ReportFlags.*;
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
		
		assertEquals(new Report(Good, "Coverage", "50%", "line: 50%, over 2 lines"), report);
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
		
		assertEquals(0, analyser.computeReport(data, prefs, SummaryOnly).size());
	}
	
	@Test
	public void testOnlyCovered() {
		data.add(10, "Coverage", "java", "emma", "all", "covered", "all");
		
		assertEquals(0, analyser.computeReport(data, prefs, SummaryOnly).size());
	}
	
	@Test
	public void testLimitGood() {
		create("line", 1, 2);
		prefs.child("coverage").set("good", 20);
		
		Report report = createReport();
		
		assertEquals(new Report(Good, "Coverage", "50%", "line: 50% (1/2)"), report);
	}
	
	@Test
	public void testLimitGoodBorderline() {
		create("line", 1, 2);
		prefs.child("coverage").set("good", 50);
		
		Report report = createReport();
		
		assertEquals(new Report(Good, "Coverage", "50%", "line: 50% (1/2)"), report);
	}
	
	@Test
	public void testLimitSoSo() {
		create("line", 1, 2);
		prefs.child("coverage").set("good", 70);
		prefs.child("coverage").set("warn", 20);
		
		Report report = createReport();
		
		assertEquals(new Report(SoSo, "Coverage", "50%", "line: 50% (1/2)"), report);
	}
	
	@Test
	public void testLimitSoSoBorderline() {
		create("line", 1, 2);
		prefs.child("coverage").set("good", 70);
		prefs.child("coverage").set("warn", 50);
		
		Report report = createReport();
		
		assertEquals(new Report(SoSo, "Coverage", "50%", "line: 50% (1/2)"), report);
	}
	
	@Test
	public void testLimitProblematic() {
		create("line", 1, 2);
		prefs.child("coverage").set("good", 80);
		prefs.child("coverage").set("warn", 70);
		
		Report report = createReport();
		
		assertEquals(new Report(Problematic, "Coverage", "50%", "line: 50% (1/2)"), report);
	}
	
	@Test
	public void testLimitSoSoType() {
		create("line", 1, 2);
		create("instruction", 1, 4);
		prefs.child("coverage", "line").set("good", 60);
		prefs.child("coverage", "line").set("warn", 30);
		
		Report report = createReport();
		
		assertEquals(new Report(SoSo, "Coverage", "25%", "line: 50% (1/2), instruction: 25% (1/4)"), report);
	}
	
	@Test
	public void testLimitProblematicType() {
		create("line", 1, 2);
		create("instruction", 3, 4);
		prefs.child("coverage", "line").set("good", 70);
		prefs.child("coverage", "line").set("warn", 60);
		
		Report report = createReport();
		
		assertEquals(new Report(Problematic, "Coverage", "75%", "line: 50% (1/2), instruction: 75% (3/4)"), report);
	}
	
	@Test
	public void testMainType_Line_JustOne() {
		create("line", 1, 2);
		create("instruction", 3, 4);
		prefs.child("coverage").set("maintype", "line");
		
		Report report = createReport();
		
		assertEquals(new Report(Good, "Coverage", "50%", "line: 50% (1/2), instruction: 75% (3/4)"), report);
	}
	
	@Test
	public void testMainType_Instruction_JustOne() {
		create("line", 1, 2);
		create("instruction", 3, 4);
		prefs.child("coverage").set("maintype", "instruction");
		
		Report report = createReport();
		
		assertEquals(new Report(Good, "Coverage", "75%", "line: 50% (1/2), instruction: 75% (3/4)"), report);
	}
	
	@Test
	public void testMainType_Line_Multiple() {
		create("line", 1, 2);
		create("instruction", 3, 4);
		prefs.child("coverage").set("maintype", "bla,line,instruction");
		
		Report report = createReport();
		
		assertEquals(new Report(Good, "Coverage", "50%", "line: 50% (1/2), instruction: 75% (3/4)"), report);
	}
	
	@Test
	public void testMainType_Instruction_Multiple() {
		create("line", 1, 2);
		create("instruction", 3, 4);
		prefs.child("coverage").set("maintype", "bla,instruction,line");
		
		Report report = createReport();
		
		assertEquals(new Report(Good, "Coverage", "75%", "line: 50% (1/2), instruction: 75% (3/4)"), report);
	}
	
}
