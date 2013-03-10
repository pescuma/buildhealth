package prg.pescuma.buildhealth.core;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class BuildHealthTest {
	
	@Before
	public void setUp() throws Exception {
	}
	
	@Test
	public void test() {
		BuildHealth buildhealth = new BuildHealth();
		
		assertEquals("No data to generate report", buildhealth.generateSimpleReport());
	}
	
}
