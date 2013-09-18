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
		
		verify(tracker).onStreamProcessed();
		verify(tracker, never()).onFileProcessed(any(File.class));
		
		assertEquals(40, table.size());
		assertEquals(40, table.filter("Coverage", "Java", "JaCoCo").size());
		
		assertEquals(0, table.get("Coverage", "Java", "JaCoCo", "type", "package", "org.pescuma.buildhealth.analyser"),
				0.0001);
		assertEquals(0, table.get("Coverage", "Java", "JaCoCo", "type", "class", "org.pescuma.buildhealth.analyser",
				"BaseAnalyserTest"), 0.0001);
		assertEquals(0, table.get("Coverage", "Java", "JaCoCo", "type", "method", "org.pescuma.buildhealth.analyser",
				"BaseAnalyserTest", "<init> ()V"), 0.0001);
		assertEquals(3, table.get("Coverage", "Java", "JaCoCo", "covered", "instruction",
				"org.pescuma.buildhealth.analyser", "BaseAnalyserTest", "<init> ()V"), 0.0001);
		assertEquals(3, table.get("Coverage", "Java", "JaCoCo", "total", "instruction",
				"org.pescuma.buildhealth.analyser", "BaseAnalyserTest", "<init> ()V"), 0.0001);
	}
	
}
