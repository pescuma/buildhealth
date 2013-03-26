package org.pescuma.buildhealth.extractor.jacoco;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.InputStream;

import org.junit.Test;
import org.pescuma.buildhealth.extractor.BaseExtractorTest;
import org.pescuma.buildhealth.extractor.PseudoFiles;
import org.pescuma.buildhealth.extractor.coverage.JacocoExtractor;

public class JacocoExtractorTest extends BaseExtractorTest {
	
	@Test
	public void testAllLevels() {
		InputStream stream = load("jacoco.alllevels.xml");
		
		JacocoExtractor extractor = new JacocoExtractor(new PseudoFiles(stream));
		
		extractor.extractTo(table, tracker);
		
		verify(tracker).streamProcessed();
		verify(tracker, never()).fileProcessed(any(File.class));
		
		assertEquals(60, table.size());
		assertEquals(60, table.filter("Coverage", "Java", "JaCoCo").size());
		
		assertEquals(38, table.get("Coverage", "Java", "JaCoCo", "class", "covered", "all", "report_name"), 0.0001);
		assertEquals(38 + 18, table.get("Coverage", "Java", "JaCoCo", "class", "total", "all", "report_name"), 0.0001);
		assertEquals(180, table.get("Coverage", "Java", "JaCoCo", "method", "covered", "all", "report_name"), 0.0001);
		assertEquals(180 + 90, table.get("Coverage", "Java", "JaCoCo", "method", "total", "all", "report_name"), 0.0001);
		assertEquals(289, table.get("Coverage", "Java", "JaCoCo", "line", "covered", "all", "report_name"), 0.0001);
		assertEquals(289 + 2, table.get("Coverage", "Java", "JaCoCo", "line", "total", "all", "report_name"), 0.0001);
		assertEquals(253, table.get("Coverage", "Java", "JaCoCo", "complexity", "covered", "all", "report_name"),
				0.0001);
		assertEquals(253 + 204, table.get("Coverage", "Java", "JaCoCo", "complexity", "total", "all", "report_name"),
				0.0001);
		assertEquals(201, table.get("Coverage", "Java", "JaCoCo", "branch", "covered", "all", "report_name"), 0.0001);
		assertEquals(201 + 171, table.get("Coverage", "Java", "JaCoCo", "branch", "total", "all", "report_name"),
				0.0001);
		assertEquals(4372, table.get("Coverage", "Java", "JaCoCo", "instruction", "covered", "all", "report_name"),
				0.0001);
		assertEquals(4372 + 1901,
				table.get("Coverage", "Java", "JaCoCo", "instruction", "total", "all", "report_name"), 0.0001);
	}
	
}
