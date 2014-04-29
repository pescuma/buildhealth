package org.pescuma.buildhealth.analyser.staticanalysis;

import static org.pescuma.buildhealth.core.BuildHealth.ReportFlags.*;
import static org.pescuma.buildhealth.core.BuildStatus.*;

import org.junit.Before;
import org.junit.Test;
import org.pescuma.buildhealth.analyser.BaseAnalyserTest;
import org.pescuma.buildhealth.core.Report;

public class StaticAnalysisAnalyserTest extends BaseAnalyserTest {
	
	@Before
	public void setUp() {
		super.setUp(new StaticAnalysisAnalyser());
	}
	
	private void create(String type, int count) {
		create("Java", type, count);
	}
	
	private void create(String language, String type, int count) {
		create(language, type, "", count);
	}
	
	private void create(String language, String type, String severity, int count) {
		data.add(count, "Static analysis", language, type, "", "", "", severity);
	}
	
	@Test
	public void testSimple() {
		create("Task", 1);
		
		Report report = createReport();
		
		assertReport(new Report(Good, "Static analysis", "1", "Task: 1"), report);
	}
	
	@Test
	public void testMultipleLines() {
		create("Task", 1);
		create("Task", 3);
		create("Task", 1);
		
		Report report = createReport();
		
		assertReport(new Report(Good, "Static analysis", "5", "Task: 5"), report);
	}
	
	@Test
	public void testMultipleTypes() {
		create("Task", 1);
		create("PMD", 3);
		create("CheckStyle", 1);
		
		Report report = createReport();
		
		assertReport(new Report(Good, "Static analysis", "5", "CheckStyle: 1, PMD: 3, Task: 1"), report);
	}
	
	@Test
	public void testMultipleLanguages() {
		create("Java", "Task", 1);
		create("C++", "CppLint", 3);
		create("C++", "Task", 1);
		
		Report report = createReport();
		
		assertReport(new Report(Good, "Static analysis", "5", "C++: CppLint: 3, Task: 1 ; Java: Task: 1"), report);
	}
	
	@Test
	public void testLimitGood() {
		create("Task", 10);
		prefs.child("staticanalysis").set("good", 20);
		
		Report report = createReport();
		
		assertReport(new Report(Good, "Static analysis", "10", "Task: 10"), report);
	}
	
	@Test
	public void testLimitGoodBorderline() {
		create("Task", 10);
		prefs.child("staticanalysis").set("good", 10);
		
		Report report = createReport();
		
		assertReport(new Report(Good, "Static analysis", "10", "Task: 10"), report);
	}
	
	@Test
	public void testLimitSoSo() {
		create("Task", 10);
		prefs.child("staticanalysis").set("good", 5);
		prefs.child("staticanalysis").set("warn", 20);
		
		Report report = createReport();
		
		assertReport(new Report(SoSo, "Static analysis", "10", "Task: 10", "Instable if has more than 5 violations"),
				report);
	}
	
	@Test
	public void testLimitSoSoBorderline() {
		create("Task", 10);
		prefs.child("staticanalysis").set("good", 5);
		prefs.child("staticanalysis").set("warn", 10);
		
		Report report = createReport();
		
		assertReport(new Report(SoSo, "Static analysis", "10", "Task: 10", "Instable if has more than 5 violations"),
				report);
	}
	
	@Test
	public void testLimitProblematic() {
		create("Task", 10);
		prefs.child("staticanalysis").set("good", 5);
		prefs.child("staticanalysis").set("warn", 9);
		
		Report report = createReport();
		
		assertReport(new Report(Problematic, "Static analysis", "10", "Task: 10",
				"Should not have more than 9 violations"), report);
	}
	
	@Test
	public void testLimitLanguageSoSo() {
		create("Java", "Task", 1);
		create("C++", "CppLint", 3);
		create("C++", "Task", 1);
		
		prefs.child("staticanalysis", "C++").set("good", 1);
		prefs.child("staticanalysis", "C++").set("warn", 4);
		
		Report report = createReport();
		
		assertReport(new Report(SoSo, "Static analysis", "5", "C++: CppLint: 3, Task: 1 ; Java: Task: 1"), report);
	}
	
	@Test
	public void testLimitLanguageProblematic() {
		create("Java", "Task", 1);
		create("C++", "CppLint", 3);
		create("C++", "Task", 1);
		
		prefs.child("staticanalysis", "C++").set("good", 1);
		prefs.child("staticanalysis", "C++").set("warn", 3);
		
		Report report = createReport();
		
		assertReport(new Report(Problematic, "Static analysis", "5", "C++: CppLint: 3, Task: 1 ; Java: Task: 1"),
				report);
	}
	
	@Test
	public void testLimitFrameworkSoSo() {
		create("Java", "Task", 1);
		create("C++", "CppLint", 3);
		create("C++", "Task", 1);
		
		prefs.child("staticanalysis", "C++", "CppLint").set("good", 1);
		prefs.child("staticanalysis", "C++", "CppLint").set("warn", 3);
		
		Report report = createReport();
		
		assertReport(new Report(SoSo, "Static analysis", "5", "C++: CppLint: 3, Task: 1 ; Java: Task: 1"), report);
	}
	
	@Test
	public void testFull() {
		create("Java", "Task", 1);
		create("C++", "CppLint", 3);
		create("C++", "Task", 1);
		
		Report report = createReport(Full);
		
		assertReport(new Report(Good, "Static analysis", "5", "C++: CppLint: 3, Task: 1 ; Java: Task: 1", //
				new Report(Good, "C++", "4", "", //
						new Report(Good, "CppLint", "3"), //
						new Report(Good, "Task", "1") //
				), //
				new Report(Good, "Java", "1", "", //
						new Report(Good, "Task", "1") //
				)//
				), report);
	}
	
	@Test
	public void testFullSoSo() {
		create("Java", "Task", 1);
		create("C++", "CppLint", 3);
		create("C++", "Task", 1);
		
		prefs.child("staticanalysis", "C++", "CppLint").set("good", 1);
		prefs.child("staticanalysis", "C++", "CppLint").set("warn", 3);
		
		Report report = createReport(Full);
		
		assertReport(new Report(SoSo, "Static analysis", "5", "C++: CppLint: 3, Task: 1 ; Java: Task: 1", //
				new Report(SoSo, "C++", "4", "", //
						new Report(SoSo, "CppLint", "3", null,
								"Instable if has more than 1 violations in C++ measured by CppLint"), //
						new Report(Good, "Task", "1") //
				), //
				new Report(Good, "Java", "1", "", //
						new Report(Good, "Task", "1") //
				)//
				), report);
	}
	
	@Test
	public void testSeveityThreshold_Simple() {
		create("Java", "Task", "Low", 1);
		create("C++", "CppLint", "High", 3);
		
		prefs.child("staticanalysis").set("warn", 2);
		
		Report report = createReport(Full);
		
		assertReport(new Report(Problematic, "Static analysis", "4", "C++: CppLint: 3 ; Java: Task: 1", //
				"Should not have more than 2 violations", //
				new Report(Good, "C++", "3", "", //
						new Report(Good, "CppLint", "3") //
				), //
				new Report(Good, "Java", "1", "", //
						new Report(Good, "Task", "1") //
				)//
				), report);
	}
	
	@Test
	public void testSeveityThreshold_GlobalMatches() {
		create("Java", "Task", "Low", 1);
		create("C++", "CppLint", "High", 3);
		
		prefs.child("staticanalysis", "High").set("warn", 2);
		
		Report report = createReport(Full);
		
		assertReport(new Report(Problematic, "Static analysis", "4", "C++: CppLint: 3 ; Java: Task: 1", //
				"Should not have more than 2 violations with severity High", //
				new Report(Good, "C++", "3", "", //
						new Report(Good, "CppLint", "3") //
				), //
				new Report(Good, "Java", "1", "", //
						new Report(Good, "Task", "1") //
				)//
				), report);
	}
	
	@Test
	public void testSeverityThreshold_GlobalDoesntMatch() {
		create("Java", "Task", "Low", 1);
		create("C++", "CppLint", "High", 3);
		
		prefs.child("staticanalysis", "Low").set("warn", 2);
		
		Report report = createReport(Full);
		
		assertReport(new Report(Good, "Static analysis", "4", "C++: CppLint: 3 ; Java: Task: 1", //
				new Report(Good, "C++", "3", "", //
						new Report(Good, "CppLint", "3") //
				), //
				new Report(Good, "Java", "1", "", //
						new Report(Good, "Task", "1") //
				)//
				), report);
	}
	
	@Test
	public void testProjects_Full() {
		data.add(1, "Static analysis", "Java", "Task", "/src/a/a.txt>10");
		data.add(2, "Static analysis", "Java", "Task", "/src/b/b.txt>11");
		
		projects.addProjectBasePath("P A", "/src/a");
		projects.addProjectBasePath("P B", "/src/b");
		
		Report report = createReport(Full);
		
		assertReport(new Report(Good, "Static analysis", "3", "Task: 3", //
				new Report(Good, "Java", "3", "", //
						new Report(Good, "Task", "3", //
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
