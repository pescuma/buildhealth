package org.pescuma.buildhealth.extractor.stylecop;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.InputStream;

import org.junit.Test;
import org.pescuma.buildhealth.extractor.BaseExtractorTest;
import org.pescuma.buildhealth.extractor.PseudoFiles;
import org.pescuma.buildhealth.extractor.staticanalysis.StyleCopExtractor;

public class StyleCopExtractorTest extends BaseExtractorTest {
	
	@Test
	public void testOrig() {
		InputStream stream = load("stylecop-orig.xml");
		
		StyleCopExtractor extractor = new StyleCopExtractor(new PseudoFiles(stream));
		
		extractor.extractTo(table, tracker);
		
		verify(tracker).onStreamProcessed();
		verify(tracker, never()).onFileProcessed(any(File.class));
		
		assertTable(3, 3, table);
		
		assertEquals(1, table.get("Static analysis", "C#", "StyleCop", "MainClass.cs>7",
				"DocumentationRules/ElementsMustBeDocumented", "The class must have a documentation header.", "",
				"at Root.UnitTests.MainClass", "http://www.stylecop.com/docs/SA1600.html"), 0.0001);
	}
	
	@Test
	public void test43() {
		InputStream stream = load("stylecop-v4.3.xml");
		
		StyleCopExtractor extractor = new StyleCopExtractor(new PseudoFiles(stream));
		
		extractor.extractTo(table, tracker);
		
		verify(tracker).onStreamProcessed();
		verify(tracker, never()).onFileProcessed(any(File.class));
		
		assertTable(2, 2, table);
		
		assertEquals(
				1,
				table.get(
						"Static analysis",
						"C#",
						"StyleCop",
						"Form1.Designer.cs>16",
						"ReadabilityRules/PrefixLocalCallsWithThis",
						"The call to components must begin with the 'this.' prefix to indicate that the item is a member of the class.",
						"", "at Root.ListViewProblem.Form1.Dispose%bool", "http://www.stylecop.com/docs/SA1101.html"),
				0.0001);
	}
}
