package org.pescuma.buildhealth.computer.staticanalysis;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.pescuma.buildhealth.extractor.BaseExtractorTest;
import org.pescuma.buildhealth.extractor.PseudoFiles;

public class TasksComputerTest extends BaseExtractorTest {
	
	private final File temp = FileUtils.getTempDirectory();
	
	private void computeAndExtract(TasksComputer extractor) {
		extractor.compute(temp, computerTracker).extractTo(table, tracker);
		
		verify(computerTracker).onStreamProcessed();
		verify(computerTracker, never()).onFileProcessed(any(File.class));
		
		verify(tracker, never()).onStreamProcessed();
		verify(tracker).onFileProcessed(any(File.class));
	}
	
	@Test
	public void testSimple() {
		InputStream stream = load("tasks.oneeach.upper.txt");
		
		TasksComputer extractor = new TasksComputer(new PseudoFiles(stream, "tasks.oneeach.upper.java"));
		
		computeAndExtract(extractor);
		
		assertEquals(4, table.size());
		
		assertEquals(1, table.get("Static analysis", "Java", "Tasks", "tasks.oneeach.upper.java", "2", "TODO", "123"),
				0.001);
		assertEquals(1, table.get("Static analysis", "Java", "Tasks", "tasks.oneeach.upper.java", "4", "HACK", "456"),
				0.001);
		assertEquals(1, table.get("Static analysis", "Java", "Tasks", "tasks.oneeach.upper.java", "6", "XXX", "789"),
				0.001);
		assertEquals(1, table.get("Static analysis", "Java", "Tasks", "tasks.oneeach.upper.java", "8", "FIXME", "012"),
				0.001);
	}
	
	private TasksComputer computerFor(String filename, String text) {
		return new TasksComputer(new PseudoFiles(new ByteArrayInputStream(text.getBytes()), filename));
	}
	
	private TasksComputer computerFor(String text) {
		return new TasksComputer(new PseudoFiles(new ByteArrayInputStream(text.getBytes())));
	}
	
	@Test
	public void testRemoveColon() {
		TasksComputer extractor = computerFor("TODO: a");
		
		computeAndExtract(extractor);
		
		assertEquals(1, table.size());
		
		assertEquals(1, table.get("Static analysis", "", "Tasks", "<stream>", "1", "TODO", "a"), 0.001);
	}
	
	@Test
	public void testRemoveColonWithSpaceBefore() {
		TasksComputer extractor = computerFor("TODO : ");
		
		computeAndExtract(extractor);
		
		assertEquals(1, table.size());
		
		assertEquals(1, table.get("Static analysis", "", "Tasks", "<stream>", "1", "TODO", ""), 0.001);
	}
	
	@Test
	public void testDetectOwnerWithText() {
		TasksComputer extractor = computerFor("TODO [pescuma] Works ");
		
		computeAndExtract(extractor);
		
		assertEquals(1, table.size());
		
		assertEquals(1, table.get("Static analysis", "", "Tasks", "<stream>", "1", "TODO", "Works (pescuma)"), 0.001);
	}
	
	@Test
	public void testDetectOwnerWithoutText() {
		TasksComputer extractor = computerFor("TODO [pescuma]");
		
		computeAndExtract(extractor);
		
		assertEquals(1, table.size());
		
		assertEquals(1, table.get("Static analysis", "", "Tasks", "<stream>", "1", "TODO", "(pescuma)"), 0.001);
	}
	
	@Test
	public void testTerminator() {
		TasksComputer extractor = computerFor("a.java", " /* TODO abc */");
		
		computeAndExtract(extractor);
		
		assertEquals(1, table.size());
		
		assertEquals(1, table.get("Static analysis", "Java", "Tasks", "a.java", "1", "TODO", "abc"), 0.001);
	}
	
	@Test
	public void testLowerIgnore() {
		InputStream stream = load("tasks.oneeach.lower.txt");
		
		TasksComputer extractor = new TasksComputer(new PseudoFiles(stream, "c:\\a.cpp"));
		
		computeAndExtract(extractor);
		
		assertEquals(0, table.size());
	}
	
	@Test
	public void testLower() {
		InputStream stream = load("tasks.oneeach.lower.txt");
		
		TasksComputer extractor = new TasksComputer(new PseudoFiles(stream, "c:\\a.cpp"), true);
		
		computeAndExtract(extractor);
		
		assertEquals(4, table.size());
		
		assertEquals(1, table.get("Static analysis", "C++", "Tasks", "c:\\a.cpp", "2", "TODO", "123"), 0.001);
		assertEquals(1, table.get("Static analysis", "C++", "Tasks", "c:\\a.cpp", "4", "HACK", "456"), 0.001);
		assertEquals(1, table.get("Static analysis", "C++", "Tasks", "c:\\a.cpp", "6", "XXX", "789"), 0.001);
		assertEquals(1, table.get("Static analysis", "C++", "Tasks", "c:\\a.cpp", "8", "FIXME", "012"), 0.001);
	}
	
}
