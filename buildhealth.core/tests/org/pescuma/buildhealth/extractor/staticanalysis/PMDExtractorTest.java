package org.pescuma.buildhealth.extractor.staticanalysis;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.InputStream;

import org.junit.Test;
import org.pescuma.buildhealth.extractor.BaseExtractorTest;
import org.pescuma.buildhealth.extractor.PseudoFiles;

public class PMDExtractorTest extends BaseExtractorTest {
	
	@Test
	public void testSimple() {
		InputStream stream = load("pmd.simple.xml");
		
		PMDExtractor extractor = new PMDExtractor(new PseudoFiles(stream));
		
		extractor.extractTo(table, tracker);
		
		verify(tracker).onStreamProcessed();
		verify(tracker, never()).onFileProcessed(any(File.class));
		
		assertTable(3, 3, table);
		
		assertEquals(
				1,
				table.get(
						"Static analysis",
						"Java",
						"PMD",
						"org\\pescuma\\buildhealth\\ant\\BuildHealthAntTask.java",
						"47:23:47:46",
						"Design/FieldDeclarationsShouldBeAtStartOfClass",
						"Fields should be declared at the top of the class, before any method declarations, constructors, initializers or inner classes.",
						"http://pmd.sourceforge.net/pmd-5.0.2/rules/java/design.html#FieldDeclarationsShouldBeAtStartOfClass"),
				0.0001);
		assertEquals(1, table.get("Static analysis", "Java", "PMD", "org\\pescuma\\buildhealth\\ant\\DummyTest.java",
				"11:28:12:9", "Design/UncommentedEmptyMethod", "Document empty method",
				"http://pmd.sourceforge.net/pmd-5.0.2/rules/java/design.html#UncommentedEmptyMethod"), 0.0001);
		assertEquals(1, table.get("Static analysis", "Java", "PMD", "org\\pescuma\\buildhealth\\ant\\DummyTest.java",
				"10:1:10:2", "UncommentedEmptyMethod", "Bla"), 0.0001);
	}
	
}
