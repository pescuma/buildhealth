package org.pescuma.buildhealth.extractor.jsxlint;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.InputStream;

import org.junit.Test;
import org.pescuma.buildhealth.extractor.BaseExtractorTest;
import org.pescuma.buildhealth.extractor.PseudoFiles;
import org.pescuma.buildhealth.extractor.staticanalysis.JSXLintExtractor;

public class JSXLintExtractorTest extends BaseExtractorTest {
	
	@Test
	public void testJavaScriptLint() {
		InputStream stream = load("javascriptlint.txt");
		
		JSXLintExtractor extractor = new JSXLintExtractor(new PseudoFiles(stream));
		
		extractor.extractTo(table, tracker);
		
		verify(tracker).onStreamProcessed();
		verify(tracker, never()).onFileProcessed(any(File.class));
		
		assertTable(19, 19, table);
		
		assertEquals(1, table.get("Static analysis", "Javascript", "JavaScript Lint", "C:\\devel\\js\\index.js", "6",
				"", "unexpected end of line; it is ambiguous whether these lines are part of the same statement"),
				0.0001);
		assertEquals(1, table.get("Static analysis", "Javascript", "JavaScript Lint", "C:\\devel\\js\\index.js", "164",
				"", "redeclaration of var i"), 0.0001);
	}
	
	@Test
	public void testJSHint() {
		InputStream stream = load("jshint.txt");
		
		JSXLintExtractor extractor = new JSXLintExtractor(new PseudoFiles(stream));
		
		extractor.extractTo(table, tracker);
		
		verify(tracker).onStreamProcessed();
		verify(tracker, never()).onFileProcessed(any(File.class));
		
		assertTable(15, 15, table);
		
		assertEquals(1, table.get("Static analysis", "Javascript", "JSHint", "index.js", "33:14", "",
				"Use '===' to compare with ''"), 0.0001);
		assertEquals(1,
				table.get("Static analysis", "Javascript", "JSHint", "index.js", "335:67", "", "Missing semicolon"),
				0.0001);
	}
	
	@Test
	public void testJSHintXML() {
		InputStream stream = load("jshint.xml");
		
		JSXLintExtractor extractor = new JSXLintExtractor(new PseudoFiles(stream));
		
		extractor.extractTo(table, tracker);
		
		verify(tracker).onStreamProcessed();
		verify(tracker, never()).onFileProcessed(any(File.class));
		
		assertTable(50, 50, table);
		
		assertEquals(1, table.get("Static analysis", "Javascript", "JSHint", "index.js", "33:14", "",
				"Expected '===' and instead saw '=='."), 0.0001);
		assertEquals(1,
				table.get("Static analysis", "Javascript", "JSHint", "index.js", "71:34", "", "Missing semicolon."),
				0.0001);
	}
	
	@Test
	public void testJSLint() {
		InputStream stream = load("jslint.txt");
		
		JSXLintExtractor extractor = new JSXLintExtractor(new PseudoFiles(stream));
		
		extractor.extractTo(table, tracker);
		
		verify(tracker).onStreamProcessed();
		verify(tracker, never()).onFileProcessed(any(File.class));
		
		assertTable(38, 38, table);
		
		assertEquals(1, table.get("Static analysis", "Javascript", "JSLint", "index.js", "3:1", "",
				"'$' was used before it was defined"), 0.0001);
		assertEquals(1, table.get("Static analysis", "Javascript", "JSLint", "index.js", "24:6", "",
				"Move 'var' declarations to the top of the function"), 0.0001);
		
		assertEquals(
				true,
				table.filter("Static analysis", "Javascript", "JSLint", "index.js", "24:6", "",
						"Stopping. (6% scanned)").isEmpty());
	}
	
	@Test
	public void testJSLintXML() {
		InputStream stream = load("jslint.xml");
		
		JSXLintExtractor extractor = new JSXLintExtractor(new PseudoFiles(stream));
		
		extractor.extractTo(table, tracker);
		
		verify(tracker).onStreamProcessed();
		verify(tracker, never()).onFileProcessed(any(File.class));
		
		assertTable(35, 35, table);
		
		assertEquals(1, table.get("Static analysis", "Javascript", "JSLint", "index.js", "3:1", "",
				"'$' was used before it was defined."), 0.0001);
		assertEquals(1, table.get("Static analysis", "Javascript", "JSLint", "index.js", "24:6", "",
				"Move 'var' declarations to the top of the function."), 0.0001);
	}
	
	@Test
	public void testJSLint4Java() {
		InputStream stream = load("jslint4java.txt");
		
		JSXLintExtractor extractor = new JSXLintExtractor(new PseudoFiles(stream));
		
		extractor.extractTo(table, tracker);
		
		verify(tracker).onStreamProcessed();
		verify(tracker, never()).onFileProcessed(any(File.class));
		
		assertTable(34, 34, table);
		
		assertEquals(1, table.get("Static analysis", "Javascript", "JSLint", "index.js", "3:1", "",
				"'$' was used before it was defined"), 0.0001);
		assertEquals(1, table.get("Static analysis", "Javascript", "JSLint", "index.js", "24:6", "",
				"Move 'var' declarations to the top of the function"), 0.0001);
	}
	
	@Test
	public void testSharplinter() {
		InputStream stream = load("sharplinter.txt");
		
		JSXLintExtractor extractor = new JSXLintExtractor(new PseudoFiles(stream));
		
		extractor.extractTo(table, tracker);
		
		verify(tracker).onStreamProcessed();
		verify(tracker, never()).onFileProcessed(any(File.class));
		
		assertTable(17, 17, table);
		
		assertEquals(1, table.get("Static analysis", "Javascript", "JSHint", "C:\\devel\\js\\index.js", "33:14", "",
				"Use '===' to compare with ''"), 0.0001);
		assertEquals(1, table.get("Static analysis", "Javascript", "JSHint", "C:\\devel\\js\\index.js", "335:67", "",
				"Missing semicolon"), 0.0001);
	}
}
