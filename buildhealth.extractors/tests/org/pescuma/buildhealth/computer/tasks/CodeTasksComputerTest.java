package org.pescuma.buildhealth.computer.tasks;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.pescuma.buildhealth.extractor.BaseExtractorTest;
import org.pescuma.buildhealth.extractor.PseudoFiles;

public class CodeTasksComputerTest extends BaseExtractorTest {
	
	private final File temp = FileUtils.getTempDirectory();
	
	private void computeAndExtract(CodeTasksComputer extractor) {
		extractor.compute(temp, computerTracker).extractTo(table, tracker);
		
		verify(computerTracker).onStreamProcessed();
		verify(computerTracker, never()).onFileProcessed(any(File.class));
		
		verify(tracker, never()).onStreamProcessed();
		verify(tracker).onFileProcessed(any(File.class));
	}
	
	@Test
	public void testSimple() {
		InputStream stream = load("tasks.oneeach.upper.txt");
		
		CodeTasksComputer extractor = new CodeTasksComputer(new PseudoFiles(stream, "tasks.java"));
		
		computeAndExtract(extractor);
		
		assertEquals(4, table.count());
		
		assertEquals(1, table.get("Tasks", "From code", "TODO", "", "123", "", "", "", "", "", "", "tasks.java>2"),
				0.001);
		assertEquals(1, table.get("Tasks", "From code", "HACK", "", "456", "", "", "", "", "", "", "tasks.java>4"),
				0.001);
		assertEquals(1, table.get("Tasks", "From code", "XXX", "", "789", "", "", "", "", "", "", "tasks.java>6"),
				0.001);
		assertEquals(1, table.get("Tasks", "From code", "FIXME", "", "012", "", "", "", "", "", "", "tasks.java>8"),
				0.001);
	}
	
	private CodeTasksComputer computerFor(String filename, String text) {
		return new CodeTasksComputer(new PseudoFiles(new ByteArrayInputStream(text.getBytes()), filename));
	}
	
	private CodeTasksComputer computerFor(String text) {
		return new CodeTasksComputer(new PseudoFiles(new ByteArrayInputStream(text.getBytes())));
	}
	
	@Test
	public void testRemoveColon() {
		CodeTasksComputer extractor = computerFor("TODO: a");
		
		computeAndExtract(extractor);
		
		assertEquals(1, table.count());
		
		assertEquals(1, table.get("Tasks", "From code", "TODO", "", "a"), 0.001);
	}
	
	@Test
	public void testRemoveColonWithSpaceBefore() {
		CodeTasksComputer extractor = computerFor("TODO : ");
		
		computeAndExtract(extractor);
		
		assertEquals(1, table.count());
		
		assertEquals(1, table.get("Tasks", "From code", "TODO", "", CodeTasksComputer.NO_MESSAGE), 0.001);
	}
	
	@Test
	public void testDetectCreatedByWithText() {
		CodeTasksComputer extractor = computerFor("TODO [pescuma] Works ");
		
		computeAndExtract(extractor);
		
		assertEquals(1, table.count());
		
		assertEquals(1, table.get("Tasks", "From code", "TODO", "", "Works", "", "pescuma"), 0.001);
	}
	
	@Test
	public void testDetectCreatedByWithoutText() {
		CodeTasksComputer extractor = computerFor("TODO [pescuma]");
		
		computeAndExtract(extractor);
		
		assertEquals(1, table.count());
		
		assertEquals(1, table.get("Tasks", "From code", "TODO", "", CodeTasksComputer.NO_MESSAGE, "", "pescuma"), 0.001);
	}
	
	@Test
	public void testTerminator() {
		CodeTasksComputer extractor = computerFor("a.java", " /* TODO abc */ def");
		
		computeAndExtract(extractor);
		
		assertEquals(1, table.count());
		
		assertEquals(1, table.get("Tasks", "From code", "TODO", "", "abc", "", "", "", "", "", "", "a.java>1"), 0.001);
	}
	
	@Test
	public void testLowerIgnore() {
		InputStream stream = load("tasks.oneeach.lower.txt");
		
		CodeTasksComputer extractor = new CodeTasksComputer(new PseudoFiles(stream, "c:\\a.cpp"));
		
		computeAndExtract(extractor);
		
		assertEquals(0, table.count());
	}
	
	@Test
	public void testLower() {
		InputStream stream = load("tasks.oneeach.lower.txt");
		
		CodeTasksComputer extractor = new CodeTasksComputer(new PseudoFiles(stream, "c:\\a.cpp"), true);
		
		computeAndExtract(extractor);
		
		assertEquals(4, table.count());
		
		assertEquals(1, table.get("Tasks", "From code", "TODO", "", "123", "", "", "", "", "", "", "c:\\a.cpp>2"),
				0.001);
		assertEquals(1, table.get("Tasks", "From code", "HACK", "", "456", "", "", "", "", "", "", "c:\\a.cpp>4"),
				0.001);
		assertEquals(1, table.get("Tasks", "From code", "XXX", "", "789", "", "", "", "", "", "", "c:\\a.cpp>6"), 0.001);
		assertEquals(1, table.get("Tasks", "From code", "FIXME", "", "012", "", "", "", "", "", "", "c:\\a.cpp>8"),
				0.001);
	}
	
}
