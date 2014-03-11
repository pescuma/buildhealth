package org.pescuma.buildhealth.extractor.opencover;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.io.InputStream;

import org.junit.Test;
import org.pescuma.buildhealth.extractor.BaseExtractorTest;
import org.pescuma.buildhealth.extractor.PseudoFiles;
import org.pescuma.buildhealth.extractor.coverage.OpenCoverExtractor;

public class OpenCoverExtractorTest extends BaseExtractorTest {
	
	@Test
	public void testV1() {
		InputStream stream = load("opencover.xml");
		
		OpenCoverExtractor extractor = new OpenCoverExtractor(new PseudoFiles(stream));
		
		extractor.extractTo(table, tracker);
		
		verify(tracker).onStreamProcessed();
		verify(tracker, never()).onFileProcessed(any(File.class));
		
		assertEquals(20, table.size());
		assertEquals(20, table.filter("Coverage", "C#", "OpenCover").size());
		
		assertEquals(0, table.get("Coverage", "C#", "OpenCover", "type", "library", "MyAssembly"), 0.0001);
		assertEquals(2, table.get("Coverage", "C#", "OpenCover", "covered", "line", "MyAssembly", "mypkg", "MyClass",
				"System.Void MyMethod(System.String)"), 0.0001);
		assertEquals(5, table.get("Coverage", "C#", "OpenCover", "total", "line", "MyAssembly", "mypkg", "MyClass",
				"System.Void MyMethod(System.String)"), 0.0001);
	}
	
}
