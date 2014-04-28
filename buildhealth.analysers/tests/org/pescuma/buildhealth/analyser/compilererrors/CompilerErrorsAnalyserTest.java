package org.pescuma.buildhealth.analyser.compilererrors;

import static org.pescuma.buildhealth.core.BuildHealth.ReportFlags.*;
import static org.pescuma.buildhealth.core.BuildStatus.*;

import org.junit.Before;
import org.junit.Test;
import org.pescuma.buildhealth.analyser.BaseAnalyserTest;
import org.pescuma.buildhealth.core.Report;

public class CompilerErrorsAnalyserTest extends BaseAnalyserTest {
	
	@Before
	public void setUp() {
		super.setUp(new CompilerErrorsAnalyser());
	}
	
	private void create(String type, int count) {
		create("Java", type, count);
	}
	
	private void create(String language, String type, int count) {
		data.add(count, "Compiler error", language, type);
	}
	
	@Test
	public void testSimple() {
		create("javac", 1);
		
		Report report = createReport();
		
		assertReport(new Report(Problematic, "Compiler errors", "1", "javac: 1", "Should have no compiler errors"),
				report);
	}
	
	@Test
	public void testMultipleLines() {
		create("javac", 1);
		create("javac", 3);
		create("javac", 1);
		
		Report report = createReport();
		
		assertReport(new Report(Problematic, "Compiler errors", "5", "javac: 5", "Should have no compiler errors"),
				report);
	}
	
	@Test
	public void testMultipleTypes() {
		create("javac", 1);
		create("msbuild", 3);
		create("gcc", 1);
		
		Report report = createReport();
		
		assertReport(new Report(Problematic, "Compiler errors", "5", "gcc: 1, javac: 1, msbuild: 3",
				"Should have no compiler errors"), report);
	}
	
	@Test
	public void testMultipleLanguages() {
		create("Java", "javac", 1);
		create("C++", "msbuild", 3);
		create("C++", "gcc", 1);
		
		Report report = createReport();
		
		assertReport(new Report(Problematic, "Compiler errors", "5", "C++: gcc: 1, msbuild: 3; Java: javac: 1",
				"Should have no compiler errors"), report);
	}
	
	@Test
	public void testLimitGood() {
		create("javac", 10);
		prefs.child("compilererrors").set("good", 20);
		
		Report report = createReport();
		
		assertReport(new Report(Good, "Compiler errors", "10", "javac: 10"), report);
	}
	
	@Test
	public void testLimitGoodBorderline() {
		create("javac", 10);
		prefs.child("compilererrors").set("good", 10);
		
		Report report = createReport();
		
		assertReport(new Report(Good, "Compiler errors", "10", "javac: 10"), report);
	}
	
	@Test
	public void testLimitSoSo() {
		create("javac", 10);
		prefs.child("compilererrors").set("good", 5);
		prefs.child("compilererrors").set("warn", 20);
		
		Report report = createReport();
		
		assertReport(new Report(SoSo, "Compiler errors", "10", "javac: 10",
				"Instable if has more than 5 compiler errors"), report);
	}
	
	@Test
	public void testLimitSoSoBorderline() {
		create("javac", 10);
		prefs.child("compilererrors").set("good", 5);
		prefs.child("compilererrors").set("warn", 10);
		
		Report report = createReport();
		
		assertReport(new Report(SoSo, "Compiler errors", "10", "javac: 10",
				"Instable if has more than 5 compiler errors"), report);
	}
	
	@Test
	public void testLimitProblematic() {
		create("javac", 10);
		prefs.child("compilererrors").set("good", 5);
		prefs.child("compilererrors").set("warn", 9);
		
		Report report = createReport();
		
		assertReport(new Report(Problematic, "Compiler errors", "10", "javac: 10",
				"Should not have more than 9 compiler errors"), report);
	}
	
	@Test
	public void testLimitLanguageSoSo() {
		create("Java", "javac", 1);
		create("C++", "gcc", 3);
		create("C++", "msbuild", 1);
		
		prefs.child("compilererrors").set("good", Integer.MAX_VALUE);
		prefs.child("compilererrors", "C++").set("good", 1);
		prefs.child("compilererrors", "C++").set("warn", 4);
		
		Report report = createReport();
		
		assertReport(new Report(SoSo, "Compiler errors", "5", "C++: gcc: 3, msbuild: 1; Java: javac: 1"), report);
	}
	
	@Test
	public void testLimitLanguageProblematic() {
		create("Java", "javac", 1);
		create("C++", "gcc", 3);
		create("C++", "msbuild", 1);
		
		prefs.child("compilererrors").set("good", Integer.MAX_VALUE);
		prefs.child("compilererrors", "C++").set("good", 1);
		prefs.child("compilererrors", "C++").set("warn", 3);
		
		Report report = createReport();
		
		assertReport(new Report(Problematic, "Compiler errors", "5", "C++: gcc: 3, msbuild: 1; Java: javac: 1"), report);
	}
	
	@Test
	public void testLimitFrameworkSoSo() {
		create("Java", "javac", 1);
		create("C++", "gcc", 3);
		create("C++", "msbuild", 1);
		
		prefs.child("compilererrors").set("good", Integer.MAX_VALUE);
		prefs.child("compilererrors", "C++", "gcc").set("good", 1);
		prefs.child("compilererrors", "C++", "gcc").set("warn", 3);
		
		Report report = createReport();
		
		assertReport(new Report(SoSo, "Compiler errors", "5", "C++: gcc: 3, msbuild: 1; Java: javac: 1"), report);
	}
	
	@Test
	public void testFull() {
		create("Java", "javac", 1);
		create("C++", "gcc", 3);
		create("C++", "msbuild", 1);
		
		prefs.child("compilererrors").set("good", Integer.MAX_VALUE);
		
		Report report = createReport(Full);
		
		assertReport(new Report(Good, "Compiler errors", "5", "C++: gcc: 3, msbuild: 1; Java: javac: 1", //
				new Report(Good, "C++", "4", "", //
						new Report(Good, "gcc", "3"), //
						new Report(Good, "msbuild", "1") //
				), //
				new Report(Good, "Java", "1", "", //
						new Report(Good, "javac", "1") //
				)//
				), report);
	}
	
	@Test
	public void testFullSoSo() {
		create("Java", "javac", 1);
		create("C++", "gcc", 3);
		create("C++", "msbuild", 1);
		
		prefs.child("compilererrors").set("good", Integer.MAX_VALUE);
		prefs.child("compilererrors", "C++", "gcc").set("good", 1);
		prefs.child("compilererrors", "C++", "gcc").set("warn", 3);
		
		Report report = createReport(Full);
		
		assertReport(new Report(SoSo, "Compiler errors", "5", "C++: gcc: 3, msbuild: 1; Java: javac: 1", //
				new Report(SoSo, "C++", "4", "", //
						new Report(SoSo, "gcc", "3", null,
								"Instable if has more than 1 compiler errors in C++ measured by gcc"), //
						new Report(Good, "msbuild", "1") //
				), //
				new Report(Good, "Java", "1", "", //
						new Report(Good, "javac", "1") //
				)//
				), report);
	}
	
	@Test
	public void testProjects_Full() {
		data.add(1, "Compiler error", "Java", "javac", "/src/a/a.txt>10");
		data.add(2, "Compiler error", "Java", "javac", "/src/b/b.txt>11");
		
		projects.addProjectBasePath("P A", "/src/a");
		projects.addProjectBasePath("P B", "/src/b");
		
		prefs.child("compilererrors").set("good", Integer.MAX_VALUE);
		
		Report report = createReport(Full);
		
		assertReport(new Report(Good, "Compiler errors", "3", "javac: 3", //
				new Report(Good, "Java", "3", "", //
						new Report(Good, "javac", "3", //
								new Report(Good, "P A", "1", //
										new Report(Good, "/src/a/a.txt", "10") //
								), //
								new Report(Good, "P B", "2", //
										new Report(Good, "/src/b/b.txt", "11") //
								)) //
				)//
				), report);
	}
}
