package org.pescuma.buildhealth.extractor.vtest;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.InputStream;

import org.junit.Test;
import org.pescuma.buildhealth.extractor.BaseExtractorTest;
import org.pescuma.buildhealth.extractor.PseudoFiles;
import org.pescuma.buildhealth.extractor.coverage.VtestCoverageExtractor;

public class VtestCoverageExtractorTest extends BaseExtractorTest {
	
	@Test
	public void testSimple() {
		InputStream stream = load("vtest.coverage.xml");
		
		VtestCoverageExtractor extractor = new VtestCoverageExtractor(new PseudoFiles(stream));
		
		extractor.extractTo(table, tracker);
		
		verify(tracker).onStreamProcessed();
		verify(tracker, never()).onFileProcessed(any(File.class));
		
		assertTable(8, 13, table);
		assertEquals(8, table.filter("Coverage", "C#", "vtest").size());
		
		assertEquals(0, table.get("Coverage", "C#", "vtest", "type", "library", "some.random.dll"), 0.0001);
		assertEquals(0,
				table.get("Coverage", "C#", "vtest", "type", "package", "some.random.dll", "Some.Random.Namespace"),
				0.0001);
		assertEquals(0, table.get("Coverage", "C#", "vtest", "type", "class", "some.random.dll",
				"Some.Random.Namespace", "Classs"), 0.0001);
		assertEquals(0, table.get("Coverage", "C#", "vtest", "type", "method", "some.random.dll",
				"Some.Random.Namespace", "Classs", "JustDoIt(string)"), 0.0001);
		
		assertEquals(4, table.get("Coverage", "C#", "vtest", "total", "line", "some.random.dll",
				"Some.Random.Namespace", "Classs", "JustDoIt(string)"), 0.0001);
		assertEquals(3, table.get("Coverage", "C#", "vtest", "covered", "line", "some.random.dll",
				"Some.Random.Namespace", "Classs", "JustDoIt(string)"), 0.0001);
		
		assertEquals(3, table.get("Coverage", "C#", "vtest", "total", "block", "some.random.dll",
				"Some.Random.Namespace", "Classs", "JustDoIt(string)"), 0.0001);
		assertEquals(3, table.get("Coverage", "C#", "vtest", "covered", "block", "some.random.dll",
				"Some.Random.Namespace", "Classs", "JustDoIt(string)"), 0.0001);
	}
	
}
