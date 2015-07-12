package org.pescuma.buildhealth.extractor.dotnetdependencychecker;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.InputStream;

import org.junit.Test;
import org.pescuma.buildhealth.extractor.BaseExtractorTest;
import org.pescuma.buildhealth.extractor.PseudoFiles;
import org.pescuma.buildhealth.extractor.staticanalysis.DependencyCheckerExtractor;

public class DependencyCheckerExtractorTest extends BaseExtractorTest {
	
	@Test
	public void testSimple() {
		InputStream stream = load("dependency-checker.xml");
		
		DependencyCheckerExtractor extractor = new DependencyCheckerExtractor(new PseudoFiles(stream));
		
		extractor.extractTo(table, tracker);
		
		verify(tracker).onStreamProcessed();
		verify(tracker, never()).onFileProcessed(any(File.class));
		
		assertEquals(3, table.count());
		assertEquals(3, table.filter("Static analysis", "C#", "Dependency Checker").count());
		
		assertEquals(
				1,
				table.get(
						"Static analysis",
						"C#",
						"Dependency Checker",
						"C:\\devel\\MyTestProject.csproj>13",
						"Loading/Project not found",
						"The project MyTestProject references the project C:\\devel\\MyTestProject2.csproj but it could not be loaded. Guessing assembly name to be the same as project name.",
						"Low"), 0.0001);
		
		assertEquals(1, table.get("Static analysis", "C#", "Dependency Checker", "C:\\devel\\MyTestProject.csproj>14",
				"Dependency", "Dependence between MyTestProject and MyTestGUIProject (in group GUI) not allowed",
				"High"), 0.0001);
		
		assertEquals(1, table.get("Static analysis", "C#", "Dependency Checker",
				"C:\\devel\\1\\MyDuplicatedProject.csproj|C:\\devel\\2\\MyDuplicatedProject.csproj",
				"Non unique project",
				"2 projects named MyDuplicatedProject and with GUID 12345678-0123-4567-8901-234567890123 found",
				"Medium"), 0.0001);
	}
	
}
