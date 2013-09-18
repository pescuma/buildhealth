package org.pescuma.buildhealth.extractor.dotcover;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.InputStream;

import org.junit.Test;
import org.pescuma.buildhealth.extractor.BaseExtractorTest;
import org.pescuma.buildhealth.extractor.PseudoFiles;
import org.pescuma.buildhealth.extractor.coverage.DotCoverExtractor;

public class DotCoverExtractorTest extends BaseExtractorTest {
	
	@Test
	public void testV1() {
		InputStream stream = load("dotcover.v1.xml");
		
		DotCoverExtractor extractor = new DotCoverExtractor(new PseudoFiles(stream));
		
		extractor.extractTo(table, tracker);
		
		verify(tracker).onStreamProcessed();
		verify(tracker, never()).onFileProcessed(any(File.class));
		
		assertEquals(11, table.size());
		assertEquals(11, table.filter("Coverage", "C#", "dotCover").size());
		
		assertEquals(0, table.get("Coverage", "C#", "dotCover", "type", "library", "Assembly.Name"), 0.0001);
		assertEquals(0, table.get("Coverage", "C#", "dotCover", "type", "package", "Assembly.Name", "Namespace.Name"),
				0.0001);
		assertEquals(0,
				table.get("Coverage", "C#", "dotCover", "type", "class", "Assembly.Name", "Namespace.Name", "MyClass"),
				0.0001);
		assertEquals(0, table.get("Coverage", "C#", "dotCover", "type", "method", "Assembly.Name", "Namespace.Name",
				"MyClass", ".ctor"), 0.0001);
		assertEquals(4, table.get("Coverage", "C#", "dotCover", "total", "line", "Assembly.Name", "Namespace.Name",
				"MyClass", ".ctor"), 0.0001);
		assertEquals(1, table.get("Coverage", "C#", "dotCover", "covered", "line", "Assembly.Name", "Namespace.Name",
				"MyClass", ".ctor"), 0.0001);
	}
	
	@Test
	public void testV2() {
		InputStream stream = load("dotcover.v2.xml");
		
		DotCoverExtractor extractor = new DotCoverExtractor(new PseudoFiles(stream));
		
		extractor.extractTo(table, tracker);
		
		verify(tracker).onStreamProcessed();
		verify(tracker, never()).onFileProcessed(any(File.class));
		
		assertEquals(37, table.size());
		assertEquals(37, table.filter("Coverage", "C#", "dotCover").size());
		
		assertEquals(0, table.get("Coverage", "C#", "dotCover", "type", "library", "Assembly.Name"), 0.0001);
		assertEquals(0, table.get("Coverage", "C#", "dotCover", "type", "package", "Assembly.Name", "Namespace.Name"),
				0.0001);
		assertEquals(0,
				table.get("Coverage", "C#", "dotCover", "type", "class", "Assembly.Name", "Namespace.Name", "MyClass"),
				0.0001);
		assertEquals(0, table.get("Coverage", "C#", "dotCover", "type", "method", "Assembly.Name", "Namespace.Name",
				"MyClass", "MyClass()"), 0.0001);
		assertEquals(4, table.get("Coverage", "C#", "dotCover", "total", "line", "Assembly.Name", "Namespace.Name",
				"MyClass", "MyClass()"), 0.0001);
		assertEquals(1, table.get("Coverage", "C#", "dotCover", "covered", "line", "Assembly.Name", "Namespace.Name",
				"MyClass", "MyClass()"), 0.0001);
	}
	
}
