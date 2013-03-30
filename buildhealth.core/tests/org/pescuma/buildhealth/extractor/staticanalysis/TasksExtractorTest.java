package org.pescuma.buildhealth.extractor.staticanalysis;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

import org.junit.Test;
import org.pescuma.buildhealth.extractor.BaseExtractorTest;
import org.pescuma.buildhealth.extractor.PseudoFiles;

public class TasksExtractorTest extends BaseExtractorTest {
	
	@Test
	public void testSimple() {
		InputStream stream = load("tasks.oneeach.upper.txt");
		
		TasksExtractor extractor = new TasksExtractor(new PseudoFiles(stream, "tasks.oneeach.upper.java"));
		
		extractor.extractTo(table, tracker);
		
		verify(tracker).streamProcessed();
		verify(tracker, never()).fileProcessed(any(File.class));
		
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
	
	@Test
	public void testRemoveColon() {
		TasksExtractor extractor = new TasksExtractor(new PseudoFiles(new ByteArrayInputStream("TODO: a".getBytes())));
		
		extractor.extractTo(table, tracker);
		
		assertEquals(1, table.size());
		
		assertEquals(1, table.get("Static analysis", "", "Tasks", "<stream>", "1", "TODO", "a"), 0.001);
	}
	
	@Test
	public void testRemoveColonWithSpaceBefore() {
		TasksExtractor extractor = new TasksExtractor(new PseudoFiles(new ByteArrayInputStream("TODO :".getBytes())));
		
		extractor.extractTo(table, tracker);
		
		assertEquals(1, table.size());
		
		assertEquals(1, table.get("Static analysis", "", "Tasks", "<stream>", "1", "TODO", ""), 0.001);
	}
	
	@Test
	public void testLowerIgnore() {
		InputStream stream = load("tasks.oneeach.lower.txt");
		
		TasksExtractor extractor = new TasksExtractor(new PseudoFiles(stream, "c:\\a.cpp"));
		
		extractor.extractTo(table, tracker);
		
		verify(tracker).streamProcessed();
		verify(tracker, never()).fileProcessed(any(File.class));
		
		assertEquals(0, table.size());
	}
	
	@Test
	public void testLower() {
		InputStream stream = load("tasks.oneeach.lower.txt");
		
		TasksExtractor extractor = new TasksExtractor(new PseudoFiles(stream, "c:\\a.cpp"), true);
		
		extractor.extractTo(table, tracker);
		
		verify(tracker).streamProcessed();
		verify(tracker, never()).fileProcessed(any(File.class));
		
		assertEquals(4, table.size());
		
		assertEquals(1, table.get("Static analysis", "C++", "Tasks", "c:\\a.cpp", "2", "TODO", "123"), 0.001);
		assertEquals(1, table.get("Static analysis", "C++", "Tasks", "c:\\a.cpp", "4", "HACK", "456"), 0.001);
		assertEquals(1, table.get("Static analysis", "C++", "Tasks", "c:\\a.cpp", "6", "XXX", "789"), 0.001);
		assertEquals(1, table.get("Static analysis", "C++", "Tasks", "c:\\a.cpp", "8", "FIXME", "012"), 0.001);
	}
	
}
