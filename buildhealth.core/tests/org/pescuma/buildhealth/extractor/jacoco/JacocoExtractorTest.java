package org.pescuma.buildhealth.extractor.jacoco;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.InputStream;

import org.junit.Test;
import org.pescuma.buildhealth.extractor.BaseExtractorTest;
import org.pescuma.buildhealth.extractor.PseudoFiles;

public class JacocoExtractorTest extends BaseExtractorTest {
	
	@Test
	public void testAllLevels() {
		InputStream stream = load("jacoco.alllevels.xml");
		
		JacocoExtractor extractor = new JacocoExtractor(new PseudoFiles(stream));
		
		extractor.extractTo(table, tracker);
		
		verify(tracker).streamProcessed();
		verify(tracker, never()).fileProcessed(any(File.class));
		
		assertEquals(60, table.size());
		assertEquals(60, table.filter("Coverage", "java", "jacoco").size());
		
		assertEquals(38, table.get("Coverage", "java", "jacoco", "class", "covered", "all", "report_name"), 0.0001);
		assertEquals(38 + 18, table.get("Coverage", "java", "jacoco", "class", "total", "all", "report_name"), 0.0001);
		assertEquals(180, table.get("Coverage", "java", "jacoco", "method", "covered", "all", "report_name"), 0.0001);
		assertEquals(180 + 90, table.get("Coverage", "java", "jacoco", "method", "total", "all", "report_name"), 0.0001);
		assertEquals(289, table.get("Coverage", "java", "jacoco", "line", "covered", "all", "report_name"), 0.0001);
		assertEquals(289 + 2, table.get("Coverage", "java", "jacoco", "line", "total", "all", "report_name"), 0.0001);
		assertEquals(253, table.get("Coverage", "java", "jacoco", "complexity", "covered", "all", "report_name"),
				0.0001);
		assertEquals(253 + 204, table.get("Coverage", "java", "jacoco", "complexity", "total", "all", "report_name"),
				0.0001);
		assertEquals(201, table.get("Coverage", "java", "jacoco", "branch", "covered", "all", "report_name"), 0.0001);
		assertEquals(201 + 171, table.get("Coverage", "java", "jacoco", "branch", "total", "all", "report_name"),
				0.0001);
		assertEquals(4372, table.get("Coverage", "java", "jacoco", "instruction", "covered", "all", "report_name"),
				0.0001);
		assertEquals(4372 + 1901,
				table.get("Coverage", "java", "jacoco", "instruction", "total", "all", "report_name"), 0.0001);
	}
	
}
