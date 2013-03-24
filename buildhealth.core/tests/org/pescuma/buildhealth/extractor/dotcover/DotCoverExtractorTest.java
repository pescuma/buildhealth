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
	public void testSimple() {
		InputStream stream = load("dotcover.simple.xml");
		
		DotCoverExtractor extractor = new DotCoverExtractor(new PseudoFiles(stream));
		
		extractor.extractTo(table, tracker);
		
		verify(tracker).streamProcessed();
		verify(tracker, never()).fileProcessed(any(File.class));
		
		assertEquals(12, table.size());
		assertEquals(12, table.filter("Coverage", "c#", "dotCover").size());
		
		assertEquals(1, table.get("Coverage", "c#", "dotCover", "line", "covered", "all"), 0.0001);
		assertEquals(5, table.get("Coverage", "c#", "dotCover", "line", "total", "all"), 0.0001);
		
		assertEquals(1, table.get("Coverage", "c#", "dotCover", "line", "covered", "method", "Assembly.Name",
				"Namespace.Name", "MyClass", ".ctor"), 0.0001);
		assertEquals(4, table.get("Coverage", "c#", "dotCover", "line", "total", "method", "Assembly.Name",
				"Namespace.Name", "MyClass", ".ctor"), 0.0001);
	}
	
}
