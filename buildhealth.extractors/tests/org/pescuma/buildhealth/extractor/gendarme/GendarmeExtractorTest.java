package org.pescuma.buildhealth.extractor.gendarme;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.InputStream;

import org.junit.Test;
import org.pescuma.buildhealth.extractor.BaseExtractorTest;
import org.pescuma.buildhealth.extractor.PseudoFiles;
import org.pescuma.buildhealth.extractor.staticanalysis.GendarmeExtractor;

public class GendarmeExtractorTest extends BaseExtractorTest {
	
	@Test
	public void testSimple() {
		InputStream stream = load("gendarme.xml");
		
		GendarmeExtractor extractor = new GendarmeExtractor(new PseudoFiles(stream));
		
		extractor.extractTo(table, tracker);
		
		verify(tracker).onStreamProcessed();
		verify(tracker, never()).onFileProcessed(any(File.class));
		
		assertTable(2, 2, table);
		
		assertEquals(
				1,
				table.get(
						"Static analysis",
						"C#",
						"Gendarme",
						"c:\\devel\\proj\\source\\Utils\\Argument.cs>15",
						"Exceptions/AvoidArgumentExceptionDefaultConstructorRule",
						"System.ArgumentNullException",
						"Medium",
						"Problem: This method creates an ArgumentException (or derived class) but does not supply any useful information, such as the argument name, to it.\n"
								+ "Solution: Provide more information when creating the exception.\n"
								+ "Severity: Medium\n"
								+ "Confidence: Total\n"
								+ "Location: System.Void Dll.Name.Utils.Argument::ThrowIfNull(T)",
						"https://github.com/spouliot/gendarme/wiki/Gendarme.Rules.Exceptions.AvoidArgumentExceptionDefaultConstructorRule(2.10)"),
				0.0001);
		
		assertEquals(
				1,
				table.get(
						"Static analysis",
						"Visual Basic",
						"Gendarme",
						"c:\\devel\\proj\\source\\Utils\\Argument.vb>21:5",
						"Exceptions/AvoidArgumentExceptionDefaultConstructorRule",
						"System.ArgumentNullException",
						"Medium",
						"Problem: This method creates an ArgumentException (or derived class) but does not supply any useful information, such as the argument name, to it.\n"
								+ "Solution: Provide more information when creating the exception.\n"
								+ "Severity: Medium\n"
								+ "Confidence: Total\n"
								+ "Location: System.Void Dll.Name.Utils.Argument::ThrowIfNull(System.Nullable`1<T>)",
						"https://github.com/spouliot/gendarme/wiki/Gendarme.Rules.Exceptions.AvoidArgumentExceptionDefaultConstructorRule(2.10)"),
				0.0001);
	}
}
