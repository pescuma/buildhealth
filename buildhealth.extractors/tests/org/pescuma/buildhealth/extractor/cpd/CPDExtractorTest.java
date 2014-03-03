package org.pescuma.buildhealth.extractor.cpd;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.InputStream;

import org.junit.Test;
import org.pescuma.buildhealth.extractor.BaseExtractorTest;
import org.pescuma.buildhealth.extractor.PseudoFiles;
import org.pescuma.buildhealth.extractor.staticanalysis.CPDExtractor;

public class CPDExtractorTest extends BaseExtractorTest {
	
	@Test
	public void testNone() {
		InputStream stream = load("cpd.none.txt");
		
		CPDExtractor extractor = new CPDExtractor(new PseudoFiles(stream));
		
		extractor.extractTo(table, tracker);
		
		verify(tracker).onStreamProcessed();
		verify(tracker, never()).onFileProcessed(any(File.class));
		
		assertTrue(table.isEmpty());
	}
	
	@Test
	public void testOne() {
		InputStream stream = load("cpd.1.txt");
		
		CPDExtractor extractor = new CPDExtractor(new PseudoFiles(stream));
		
		extractor.extractTo(table, tracker);
		
		verify(tracker).onStreamProcessed();
		verify(tracker, never()).onFileProcessed(any(File.class));
		
		assertTable(1, 1, table);
		
		assertEquals(
				1,
				table.get(
						"Static analysis",
						"Java",
						"CPD",
						"buildhealth.core\\src\\org\\pescuma\\buildhealth\\extractor\\CSVExtractor.java>19|java\\buildhealth\\buildhealth.core\\src\\org\\pescuma\\buildhealth\\extractor\\loc\\CLOCExtractor.java>25",
						"Code duplication",
						"Found a 38 line (168 tokens) duplication in the following files:\n"
								+ "Starting at line 19 of buildhealth.core\\src\\org\\pescuma\\buildhealth\\extractor\\CSVExtractor.java\n"
								+ "Starting at line 25 of java\\buildhealth\\buildhealth.core\\src\\org\\pescuma\\buildhealth\\extractor\\loc\\CLOCExtractor.java"),
				0.0001);
	}
	
	@Test
	public void testThree() {
		InputStream stream = load("cpd.3.txt");
		
		CPDExtractor extractor = new CPDExtractor(new PseudoFiles(stream));
		
		extractor.extractTo(table, tracker);
		
		verify(tracker).onStreamProcessed();
		verify(tracker, never()).onFileProcessed(any(File.class));
		
		assertTable(3, 3, table);
	}
	
}
