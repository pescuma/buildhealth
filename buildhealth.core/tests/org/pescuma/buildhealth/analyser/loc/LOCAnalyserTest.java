package org.pescuma.buildhealth.analyser.loc;

import static org.junit.Assert.*;
import static org.pescuma.buildhealth.core.BuildStatus.*;

import org.junit.Before;
import org.junit.Test;
import org.pescuma.buildhealth.analyser.BaseAnalyserTest;
import org.pescuma.buildhealth.core.Report;

public class LOCAnalyserTest extends BaseAnalyserTest {
	
	private LOCAnalyser analyser;
	
	@Before
	public void setUp() {
		analyser = new LOCAnalyser();
		
		super.setUp(analyser);
	}
	
	private void createSource(double size) {
		data.add(size, "LOC", "java", "source");
	}
	
	private void createComment(double size) {
		data.add(size, "LOC", "java", "comment");
	}
	
	private void createBlank(double size) {
		data.add(size, "LOC", "java", "blank");
	}
	
	private void createUnknown(double size) {
		data.add(size, "LOC", "java", "unknown");
	}
	
	private void createFiles(double size) {
		data.add(size, "LOC", "java", "files");
	}
	
	@Test
	public void testJustAll() {
		createUnknown(10);
		createUnknown(20);
		
		assertEquals(new Report(Good, "Lines of code", "30"), createReport());
	}
	
	@Test
	public void testAllAndFiles() {
		createUnknown(10);
		createFiles(20);
		
		assertEquals(new Report(Good, "Lines of code", "10", "in 20 files"), createReport());
	}
	
	@Test
	public void testAllAndFilesSingular() {
		createUnknown(1);
		createFiles(1);
		
		assertEquals(new Report(Good, "Lines of code", "1", "in 1 file"), createReport());
	}
	
	@Test
	public void testAllAndFilesWihtUnits() {
		createUnknown(12 * 1000);
		createFiles(1 * 1000 * 1000);
		
		assertEquals(new Report(Good, "Lines of code", "12.0 k", "in 1.0 M files"), createReport());
	}
	
	@Test
	public void testSourceBlankComments() {
		createSource(123);
		createBlank(12);
		createComment(1234567);
		
		assertEquals(new Report(Good, "Lines of code", "1.2 M", "12 blank, 1.2 M comment, 123 source"), createReport());
	}
	
	@Test
	public void testSourceBlankCommentsAndFiles() {
		createSource(123);
		createBlank(12);
		createComment(1234567);
		createFiles(1);
		
		assertEquals(new Report(Good, "Lines of code", "1.2 M", "12 blank, 1.2 M comment, 123 source, in 1 file"),
				createReport());
	}
	
	@Test
	public void testSourceBlankCommentsAllAndFiles() {
		createSource(123);
		createBlank(12);
		createComment(6);
		createUnknown(66);
		createFiles(1);
		
		assertEquals(
				new Report(Good, "Lines of code", "207", "12 blank, 6 comment, 123 source, 66 unknown, in 1 file"),
				createReport());
	}
	
}
