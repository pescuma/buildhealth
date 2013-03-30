package org.pescuma.buildhealth.analyser.staticanalysis;

import static org.junit.Assert.*;
import static org.pescuma.buildhealth.core.BuildStatus.*;

import org.junit.Before;
import org.junit.Test;
import org.pescuma.buildhealth.analyser.BaseAnalyserTest;
import org.pescuma.buildhealth.core.Report;

public class StaticAnalysisAnalyserTest extends BaseAnalyserTest {
	
	private StaticAnalysisAnalyser analyser;
	
	@Before
	public void setUp() {
		analyser = new StaticAnalysisAnalyser();
		super.setUp(analyser);
	}
	
	private void create(String type, int count) {
		data.add(count, "Static analysis", "Java", type);
	}
	
	@Test
	public void testSimple() {
		create("Task", 1);
		
		Report report = createReport();
		
		assertEquals(new Report(Good, "Static analysis", "1", "Task: 1"), report);
	}
	
	@Test
	public void testMultipleLines() {
		create("Task", 1);
		create("Task", 3);
		create("Task", 1);
		
		Report report = createReport();
		
		assertEquals(new Report(Good, "Static analysis", "5", "Task: 5"), report);
	}
	
	@Test
	public void testMultipleTypes() {
		create("Task", 1);
		create("PMD", 3);
		create("CheckStyle", 1);
		
		Report report = createReport();
		
		assertEquals(new Report(Good, "Static analysis", "5", "CheckStyle: 1, PMD: 3, Task: 1"), report);
	}
	
}
