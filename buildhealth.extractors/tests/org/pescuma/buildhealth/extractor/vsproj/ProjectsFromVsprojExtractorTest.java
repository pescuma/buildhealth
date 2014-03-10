package org.pescuma.buildhealth.extractor.vsproj;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.InputStream;

import org.junit.Test;
import org.pescuma.buildhealth.extractor.BaseExtractorTest;
import org.pescuma.buildhealth.extractor.PseudoFiles;
import org.pescuma.buildhealth.extractor.project.ProjectsFromVsprojExtractor;

public class ProjectsFromVsprojExtractorTest extends BaseExtractorTest {
	
	@Test
	public void testSimple() {
		InputStream stream = load("vsproj.xml");
		
		ProjectsFromVsprojExtractor extractor = new ProjectsFromVsprojExtractor(new PseudoFiles(stream,
				"c:\\devel\\MyProj.csproj"));
		
		extractor.extractTo(table, tracker);
		
		verify(tracker).onStreamProcessed();
		verify(tracker, never()).onFileProcessed(any(File.class));
		
		assertTable(7, 0, table);
	}
	
}
