package org.pescuma.buildhealth.extractor.junit;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.pescuma.buildhealth.core.BuildData;
import org.pescuma.buildhealth.core.BuildData.Line;
import org.pescuma.buildhealth.core.table.BuildDataTable;

public class JUnitExtractorTest {
	
	private BuildDataTable table;
	
	@Before
	public void setUp() {
		table = new BuildDataTable();
	}
	
	private void assertTable(int size, double sum, BuildData data) {
		assertEquals(size, data.size());
		assertEquals(sum, data.sum(), 0.0001);
	}
	
	private InputStream load(String filename) {
		Class<?> cls = JUnitExtractorTest.class;
		
		InputStream result = cls.getClassLoader().getResourceAsStream(
				cls.getPackage().getName().replace('.', '/') + "/" + filename);
		if (result == null)
			throw new RuntimeException("Could not read " + filename);
		
		return result;
	}
	
	@Test
	public void test6PassedGeral() {
		InputStream stream = load("junit.6passed.xml");
		try {
			
			JUnitExtractor extractor = new JUnitExtractor(stream);
			
			extractor.extractTo(table);
			
			assertTable(6, 6, table.filter("Unit test").filter(3, "passed"));
			assertTable(6, 0.031, table.filter("Unit test").filter(3, "time"));
			assertEquals(12, table.size(), 0.0001);
			
			Collection<Line> lines = table.filter(5, "test1").filter(3, "passed").getLines();
			assertEquals(1, lines.size());
			
			Line line = lines.iterator().next();
			assertEquals(1, line.getValue(), 0.0001);
			assertArrayEquals(
					new String[] { "Unit test", "java", "junit", "passed", "org.pescuma.test.junit", "test1" },
					line.getInfo());
			
		} finally {
			try {
				stream.close();
			} catch (IOException e) {
			}
		}
	}
	
}
