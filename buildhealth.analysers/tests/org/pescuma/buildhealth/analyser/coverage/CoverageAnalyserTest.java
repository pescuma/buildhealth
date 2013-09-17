package org.pescuma.buildhealth.analyser.coverage;

import static java.util.Arrays.*;
import static org.junit.Assert.*;
import static org.pescuma.buildhealth.core.BuildHealth.ReportFlags.*;
import static org.pescuma.buildhealth.core.BuildStatus.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.pescuma.buildhealth.analyser.BaseAnalyserTest;
import org.pescuma.buildhealth.core.BuildHealth.ReportFlags;
import org.pescuma.buildhealth.core.Report;

public class CoverageAnalyserTest extends BaseAnalyserTest {
	
	private CoverageAnalyser analyser;
	
	@Before
	public void setUp() {
		analyser = new CoverageAnalyser();
		analyser.setShowDetailsInDescription(true);
		super.setUp(analyser);
	}
	
	private void create(String type, int covered, int total, String... place) {
		List<String> columns = new ArrayList<String>();
		columns.add("Coverage");
		columns.add("java");
		columns.add("emma");
		columns.add("covered");
		columns.add(type);
		columns.addAll(asList(place));
		data.add(covered, columns.toArray(new String[columns.size()]));
		
		columns.set(3, "total");
		data.add(total, columns.toArray(new String[columns.size()]));
	}
	
	@Test
	public void testOnlyLines() {
		create("line", 1, 2);
		
		Report report = createReport();
		
		assertReport(new Report(Good, "Coverage", "50%", "line: 50% (1/2)"), report);
	}
	
	@Test
	public void testOnlyLinesNoDetail() {
		create("line", 1, 2);
		analyser.setShowDetailsInDescription(false);
		
		Report report = createReport();
		
		assertReport(new Report(Good, "Coverage", "50%", "line: 50%, over 2 lines"), report);
	}
	
	@Test
	public void testNoLinesNoDetail() {
		create("instruction", 1, 2);
		analyser.setShowDetailsInDescription(false);
		
		Report report = createReport();
		
		assertReport(new Report(Good, "Coverage", "50%", "instruction: 50%"), report);
	}
	
	@Test
	public void test0LinesNoDetail() {
		create("instruction", 1, 2);
		create("line", 0, 0);
		analyser.setShowDetailsInDescription(false);
		
		Report report = createReport();
		
		assertReport(new Report(Good, "Coverage", "50%", "instruction: 50%"), report);
	}
	
	@Test
	public void test1LineNoDetail() {
		create("line", 1, 1);
		analyser.setShowDetailsInDescription(false);
		
		Report report = createReport();
		
		assertReport(new Report(Good, "Coverage", "100%", "line: 100%, over 1 line"), report);
	}
	
	@Test
	public void testMultipleWithLine() {
		create("a", 3, 4);
		create("line", 4, 8);
		create("b", 4, 4);
		
		Report report = createReport();
		
		assertReport(new Report(Good, "Coverage", "50%", "line: 50% (4/8), a: 75% (3/4), b: 100% (4/4)"), report);
	}
	
	@Test
	public void testLineIntruction_FirstLine() {
		create("line", 1, 4);
		create("instruction", 2, 4);
		
		Report report = createReport();
		
		assertReport(new Report(Good, "Coverage", "50%", "line: 25% (1/4), instruction: 50% (2/4)"), report);
	}
	
	@Test
	public void testLineIntruction_FirstIntruction() {
		create("instruction", 2, 4);
		create("line", 1, 4);
		
		Report report = createReport();
		
		assertReport(new Report(Good, "Coverage", "50%", "line: 25% (1/4), instruction: 50% (2/4)"), report);
	}
	
	@Test
	public void testLineIntruction_MoreLines() {
		create("line", 1, 4);
		create("line", 1, 4);
		create("instruction", 2, 4);
		
		Report report = createReport();
		
		assertReport(new Report(Good, "Coverage", "50%", "line: 25% (2/8), instruction: 50% (2/4)"), report);
	}
	
	@Test
	public void testLineIntruction_MoreIntructions() {
		create("line", 1, 4);
		create("instruction", 2, 4);
		create("instruction", 2, 4);
		
		Report report = createReport();
		
		assertReport(new Report(Good, "Coverage", "50%", "line: 25% (1/4), instruction: 50% (4/8)"), report);
	}
	
	@Test
	public void testUnknownType() {
		data.add(10, "Coverage", "java", "emma", "all", "unknown", "all");
		
		List<Report> reports = analyser.computeReport(data, prefs, SummaryOnly);
		
		assertEquals(0, reports.size());
	}
	
	@Test
	public void testOnlyCovered() {
		data.add(10, "Coverage", "java", "emma", "all", "covered", "all");
		
		List<Report> reports = analyser.computeReport(data, prefs, SummaryOnly);
		
		assertEquals(0, reports.size());
	}
	
	@Test
	public void testLimitGood() {
		create("line", 1, 2);
		prefs.child("coverage").set("good", 20);
		
		Report report = createReport();
		
		assertReport(new Report(Good, "Coverage", "50%", "line: 50% (1/2)"), report);
	}
	
	@Test
	public void testLimitGoodBorderline() {
		create("line", 1, 2);
		prefs.child("coverage").set("good", 50);
		
		Report report = createReport();
		
		assertReport(new Report(Good, "Coverage", "50%", "line: 50% (1/2)"), report);
	}
	
	@Test
	public void testLimitSoSo() {
		create("line", 1, 2);
		prefs.child("coverage").set("good", 70);
		prefs.child("coverage").set("warn", 20);
		
		Report report = createReport();
		
		assertReport(new Report(SoSo, "Coverage", "50%", "line: 50% (1/2)"), report);
	}
	
	@Test
	public void testLimitSoSoBorderline() {
		create("line", 1, 2);
		prefs.child("coverage").set("good", 70);
		prefs.child("coverage").set("warn", 50);
		
		Report report = createReport();
		
		assertReport(new Report(SoSo, "Coverage", "50%", "line: 50% (1/2)"), report);
	}
	
	@Test
	public void testLimitProblematic() {
		create("line", 1, 2);
		prefs.child("coverage").set("good", 80);
		prefs.child("coverage").set("warn", 70);
		
		Report report = createReport();
		
		assertReport(new Report(Problematic, "Coverage", "50%", "line: 50% (1/2)"), report);
	}
	
	@Test
	public void testLimitSoSoType() {
		create("line", 1, 2);
		create("instruction", 1, 4);
		prefs.child("coverage", "line").set("good", 60);
		prefs.child("coverage", "line").set("warn", 30);
		
		Report report = createReport();
		
		assertReport(new Report(SoSo, "Coverage", "25%", "line: 50% (1/2), instruction: 25% (1/4)"), report);
	}
	
	@Test
	public void testLimitProblematicType() {
		create("line", 1, 2);
		create("instruction", 3, 4);
		prefs.child("coverage", "line").set("good", 70);
		prefs.child("coverage", "line").set("warn", 60);
		
		Report report = createReport();
		
		assertReport(new Report(Problematic, "Coverage", "75%", "line: 50% (1/2), instruction: 75% (3/4)"), report);
	}
	
	@Test
	public void testMainType_Line_JustOne() {
		create("line", 1, 2);
		create("instruction", 3, 4);
		prefs.child("coverage").set("maintype", "line");
		
		Report report = createReport();
		
		assertReport(new Report(Good, "Coverage", "50%", "line: 50% (1/2), instruction: 75% (3/4)"), report);
	}
	
	@Test
	public void testMainType_Instruction_JustOne() {
		create("line", 1, 2);
		create("instruction", 3, 4);
		prefs.child("coverage").set("maintype", "instruction");
		
		Report report = createReport();
		
		assertReport(new Report(Good, "Coverage", "75%", "line: 50% (1/2), instruction: 75% (3/4)"), report);
	}
	
	@Test
	public void testMainType_Line_Multiple() {
		create("line", 1, 2);
		create("instruction", 3, 4);
		prefs.child("coverage").set("maintype", "bla,line,instruction");
		
		Report report = createReport();
		
		assertReport(new Report(Good, "Coverage", "50%", "line: 50% (1/2), instruction: 75% (3/4)"), report);
	}
	
	@Test
	public void testMainType_Instruction_Multiple() {
		create("line", 1, 2);
		create("instruction", 3, 4);
		prefs.child("coverage").set("maintype", "bla,instruction,line");
		
		Report report = createReport();
		
		assertReport(new Report(Good, "Coverage", "75%", "line: 50% (1/2), instruction: 75% (3/4)"), report);
	}
	
	@Test
	public void testFull_OneLine() {
		create("line", 1, 2, "a", "b");
		
		Report report = createReport(ReportFlags.Full);
		
		assertReport(new Report(Good, "Coverage", "50%", "line: 50% (1/2)", //
				new Report(Good, "java", "50%", "line: 50% (1/2)", //
						new Report(Good, "emma", "50%", "line: 50% (1/2)", //
								new Report(Good, "a", "50%", "line: 50% (1/2)", //
										new Report(Good, "b", "50%", "line: 50% (1/2)") //
								) //
						) //
				) //
				), report);
	}
	
	@Test
	public void testFull_2Lines() {
		create("line", 1, 2, "a", "b");
		create("line", 2, 4, "a", "c");
		
		Report report = createReport(ReportFlags.Full);
		
		assertReport(new Report(Good, "Coverage", "50%", "line: 50% (3/6)", //
				new Report(Good, "java", "50%", "line: 50% (3/6)", //
						new Report(Good, "emma", "50%", "line: 50% (3/6)", //
								new Report(Good, "a", "50%", "line: 50% (3/6)", //
										new Report(Good, "b", "50%", "line: 50% (1/2)"), //
										new Report(Good, "c", "50%", "line: 50% (2/4)") //
								) //
						) //
				) //
				), report);
	}
	
	@Test
	public void testFull_ParentOverwriteChild() {
		create("line", 1, 2, "a", "b");
		create("line", 2, 4, "a", "c");
		create("line", 4, 10, "a");
		
		Report report = createReport(Full);
		
		assertReport(new Report(Good, "Coverage", "40%", "line: 40% (4/10)", //
				new Report(Good, "java", "40%", "line: 40% (4/10)", //
						new Report(Good, "emma", "40%", "line: 40% (4/10)", //
								new Report(Good, "a", "40%", "line: 40% (4/10)", //
										new Report(Good, "b", "50%", "line: 50% (1/2)"), //
										new Report(Good, "c", "50%", "line: 50% (2/4)") //
								) //
						) //
				) //
				), report);
	}
	
	@Test
	public void testFull_LimitChild() {
		create("line", 1, 2, "a", "b");
		create("line", 2, 4, "a", "c");
		prefs.child("coverage", "java", "emma", "a", "b", "line").set("good", 70);
		
		Report report = createReport(Full);
		
		assertReport(new Report(SoSo, "Coverage", "50%", "line: 50% (3/6)", //
				new Report(SoSo, "java", "50%", "line: 50% (3/6)", //
						new Report(SoSo, "emma", "50%", "line: 50% (3/6)", //
								new Report(SoSo, "a", "50%", "line: 50% (3/6)", //
										new Report(SoSo, "b", "50%", "line: 50% (1/2)"), //
										new Report(Good, "c", "50%", "line: 50% (2/4)") //
								) //
						) //
				) //
				), report);
	}
	
	@Test
	public void testFull_LimitChildAndParent() {
		create("line", 1, 2, "a", "b");
		create("line", 2, 4, "a", "c");
		prefs.child("coverage", "java", "emma", "a", "c", "line").set("warn", 70);
		prefs.child("coverage", "java", "emma", "a", "line").set("good", 70);
		
		Report report = createReport(Full);
		
		assertReport(new Report(Problematic, "Coverage", "50%", "line: 50% (3/6)", //
				new Report(SoSo, "java", "50%", "line: 50% (3/6)", //
						new Report(SoSo, "emma", "50%", "line: 50% (3/6)", //
								new Report(SoSo, "a", "50%", "line: 50% (3/6)", //
										new Report(Good, "b", "50%", "line: 50% (1/2)"), //
										new Report(Problematic, "c", "50%", "line: 50% (2/4)") //
								) //
						) //
				) //
				), report);
	}
	
	@Test
	public void testFullHighligh_LimitChild() {
		create("line", 1, 2, "a", "b");
		create("line", 2, 4, "a", "c");
		prefs.child("coverage", "java", "emma", "a", "c", "line").set("good", 70);
		
		Report report = createReport(Full | HighlightProblems);
		
		assertReport(new Report(SoSo, "Coverage", "50%", "line: 50% (3/6)", //
				new Report(SoSo, "java", "50%", "line: 50% (3/6)", //
						new Report(SoSo, "emma", "50%", "line: 50% (3/6)", //
								new Report(SoSo, "a", "50%", "line: 50% (3/6)", //
										new Report(SoSo, "c", "50%", "line: 50% (2/4)"), //
										new Report(Good, "b", "50%", "line: 50% (1/2)") //
								) //
						) //
				) //
				), report);
	}
	
	@Test
	public void testHighligh_LimitChild() {
		create("line", 1, 2, "a", "b");
		create("line", 2, 4, "a", "c");
		prefs.child("coverage", "java", "emma", "a", "line").set("good", 70);
		
		Report report = createReport(SummaryOnly | HighlightProblems);
		
		assertReport(new Report(SoSo, "Coverage", "50%", "line: 50% (3/6)", //
				new Report(SoSo, "java", "50%", "line: 50% (3/6)", //
						new Report(SoSo, "emma", "50%", "line: 50% (3/6)", //
								new Report(SoSo, "a", "50%", "line: 50% (3/6)") //
						) //
				) //
				), report);
	}
}
