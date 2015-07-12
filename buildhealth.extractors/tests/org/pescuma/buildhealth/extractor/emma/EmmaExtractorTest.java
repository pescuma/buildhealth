package org.pescuma.buildhealth.extractor.emma;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.InputStream;

import org.junit.Test;
import org.pescuma.buildhealth.extractor.BaseExtractorTest;
import org.pescuma.buildhealth.extractor.PseudoFiles;
import org.pescuma.buildhealth.extractor.coverage.EmmaExtractor;

public class EmmaExtractorTest extends BaseExtractorTest {
	
	@Test
	public void testOnlyAll() {
		InputStream stream = load("emma.onlyall.xml");
		
		EmmaExtractor extractor = new EmmaExtractor(new PseudoFiles(stream));
		
		extractor.extractTo(table, tracker);
		
		verify(tracker).onStreamProcessed();
		verify(tracker, never()).onFileProcessed(any(File.class));
		
		assertEquals(8, table.count());
		assertEquals(8, table.filter("Coverage", "Java", "Emma").count());
		
		assertEquals(85, table.get("Coverage", "Java", "Emma", "covered", "class"), 0.0001);
		assertEquals(85, table.get("Coverage", "Java", "Emma", "total", "class"), 0.0001);
		assertEquals(345, table.get("Coverage", "Java", "Emma", "covered", "method"), 0.0001);
		assertEquals(61, table.get("Coverage", "Java", "Emma", "total", "method"), 0.0001);
		assertEquals(4997, table.get("Coverage", "Java", "Emma", "covered", "block"), 0.0001);
		assertEquals(4846, table.get("Coverage", "Java", "Emma", "total", "block"), 0.0001);
		assertEquals(346.3, table.get("Coverage", "Java", "Emma", "covered", "line"), 0.0001);
		assertEquals(3135, table.get("Coverage", "Java", "Emma", "total", "line"), 0.0001);
	}
	
	@Test
	public void testMethods() {
		InputStream stream = load("emma.methods.xml");
		
		EmmaExtractor extractor = new EmmaExtractor(new PseudoFiles(stream));
		
		extractor.extractTo(table, tracker);
		
		verify(tracker).onStreamProcessed();
		verify(tracker, never()).onFileProcessed(any(File.class));
		
		assertEquals(39, table.count());
		assertEquals(39, table.filter("Coverage", "Java", "Emma").count());
		
		assertEquals(0, table.get("Coverage", "Java", "Emma", "type", "package", "com.sun.tools.javac.v8.resources"),
				0.0001);
		assertEquals(0, table.get("Coverage", "Java", "Emma", "type", "class", "com.sun.tools.javac.v8.resources",
				"compiler_ja"), 0.0001);
		assertEquals(0, table.get("Coverage", "Java", "Emma", "type", "method", "com.sun.tools.javac.v8.resources",
				"compiler_ja", "<static initializer>"), 0.0001);
		assertEquals(1, table.get("Coverage", "Java", "Emma", "covered", "block", "com.sun.tools.javac.v8.resources",
				"compiler_ja", "<static initializer>"), 0.0001);
		assertEquals(1234, table.get("Coverage", "Java", "Emma", "total", "block", "com.sun.tools.javac.v8.resources",
				"compiler_ja", "<static initializer>"), 0.0001);
	}
	
}
