package org.pescuma.buildhealth.analyser.tasks;

import static org.pescuma.buildhealth.core.BuildHealth.ReportFlags.*;
import static org.pescuma.buildhealth.core.BuildStatus.*;

import org.junit.Before;
import org.junit.Test;
import org.pescuma.buildhealth.analyser.BaseAnalyserTest;
import org.pescuma.buildhealth.core.Report;

public class TasksAnalyserTest extends BaseAnalyserTest {
	
	@Before
	public void setUp() {
		super.setUp(new TasksAnalyser());
	}
	
	private void create(String orign, String type, String status, int count) {
		data.add(count, "Tasks", orign, type, status);
	}
	
	private void create(String orign, String type, String status, String text, String owner, String id,
			String parentId, int count) {
		data.add(count, "Tasks", orign, type, status, text, owner, "", "", id, parentId);
	}
	
	@Test
	public void testSimpleType() {
		create("Code", "XXX", "", 1);
		
		Report report = createReport();
		
		assertReport(new Report(Good, "Tasks", "1", "Open: 1 ; XXX: 1"), report);
	}
	
	@Test
	public void testSimpleTypeAndStatus() {
		create("Code", "XXX", "Open", 1);
		
		Report report = createReport();
		
		assertReport(new Report(Good, "Tasks", "1", "Open: 1 ; XXX: 1"), report);
	}
	
	@Test
	public void test2Types() {
		create("Code", "XXX", "", 1);
		create("Code", "TODO", "", 2);
		
		Report report = createReport();
		
		assertReport(new Report(Good, "Tasks", "3", "Open: 3 ; TODO: 2, XXX: 1"), report);
	}
	
	@Test
	public void test1Type2Status() {
		create("Code", "TODO", "Open", 1);
		create("Code", "TODO", "Closed", 2);
		
		Report report = createReport();
		
		assertReport(new Report(Good, "Tasks", "3", "Closed: 2, Open: 1 ; TODO: 3"), report);
	}
	
	@Test
	public void test2Origins() {
		create("Trac", "TODO", "", 1);
		create("Code", "TODO", "", 2);
		
		Report report = createReport();
		
		assertReport(new Report(Good, "Tasks", "3", "Open: 3 ; TODO: 3"), report);
	}
	
	@Test
	public void testFull_SimpleType() {
		create("Code", "TODO", "", "Text", "own", "1", "", 1);
		
		Report report = createReport(Full);
		
		assertReport(new Report(Good, "Tasks", "1", "Open: 1 ; TODO: 1", //
				new Report(Good, "Code", "1", "Open: 1 ; TODO: 1", //
						new Report(Good, "Text", "", "TODO, Open, id: 1, owner: own") //
				) //
				), report);
	}
	
	@Test
	public void testFull_SimpleTypeAndStatus() {
		create("Code", "TODO", "Open", "Text", "own", "1", "", 1);
		
		Report report = createReport(Full);
		
		assertReport(new Report(Good, "Tasks", "1", "Open: 1 ; TODO: 1", //
				new Report(Good, "Code", "1", "Open: 1 ; TODO: 1", //
						new Report(Good, "Text", "", "TODO, Open, id: 1, owner: own") //
				) //
				), report);
	}
	
	@Test
	public void testFull_TaskWithParent() {
		create("Code", "TODO", "", "Parent", "", "1", "", 1);
		create("Code", "TODO", "", "Child", "", "2", "1", 1);
		
		Report report = createReport(Full);
		
		assertReport(new Report(Good, "Tasks", "2", "Open: 2 ; TODO: 2", //
				new Report(Good, "Code", "2", "Open: 2 ; TODO: 2", //
						new Report(Good, "Parent", "", "TODO, Open, id: 1", //
								new Report(Good, "Child", "", "TODO, Open, id: 2") //
						) //
				) //
				), report);
	}
	
	@Test
	public void testFull_StrangeParentWith2Count() {
		create("Code", "TODO", "", "Parent", "", "1", "", 2);
		create("Code", "TODO", "", "Child", "", "2", "1", 1);
		
		Report report = createReport(Full);
		
		assertReport(new Report(Good, "Tasks", "3", "Open: 3 ; TODO: 3", //
				new Report(Good, "Code", "3", "Open: 3 ; TODO: 3", //
						new Report(Good, "Parent", "", "TODO, Open, id: 1, representing 2 tasks", //
								new Report(Good, "Child", "", "TODO, Open, id: 2") //
						) //
				) //
				), report);
	}
}
