package org.pescuma.buildhealth.utils;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class LocationTest {
	
	@Test
	public void testCreate_Line() {
		assertEquals(new Location("c:/a", 1), Location.create("c:/a", "1"));
	}
	
	@Test
	public void testCreate_LineEmpty() {
		assertEquals(new Location("c:/a"), Location.create("c:/a", ""));
	}
	
	@Test
	public void testCreate_LineCol() {
		assertEquals(new Location("c:/a", 1, 2), Location.create("c:/a", "1", "2"));
	}
	
	@Test
	public void testCreate_LineEmptyCol() {
		assertEquals(new Location("c:/a"), Location.create("c:/a", "", "2"));
	}
	
	@Test
	public void testCreate_LineColEmpty() {
		assertEquals(new Location("c:/a", 1), Location.create("c:/a", "1", ""));
	}
	
	@Test
	public void testCreate_LineEmptyColEmpty() {
		assertEquals(new Location("c:/a"), Location.create("c:/a", "", ""));
	}
	
	@Test
	public void testCreate_Full() {
		assertEquals(new Location("c:/a", 1, 2, 3, 4), Location.create("c:/a", "1", "2", "3", "4"));
	}
	
	@Test
	public void testCreate_Full_NoBeginLine() {
		assertEquals(new Location("c:/a", 1, 2, 3, 4), Location.create("c:/a", "", "2", "3", "4"));
	}
	
	@Test
	public void testCreate_Full_NoEndLine() {
		assertEquals(new Location("c:/a", 1, 2, 1, 4), Location.create("c:/a", "1", "2", "", "4"));
	}
	
	@Test
	public void testCreate_Full_NoBeginColumn() {
		assertEquals(new Location("c:/a", 1, 1, 3, 4), Location.create("c:/a", "1", "", "3", "4"));
	}
	
	@Test
	public void testCreate_NoEndColumn() {
		assertEquals(new Location("c:/a", 1, 2, 3, 2), Location.create("c:/a", "1", "2", "3", ""));
	}
	
	@Test
	public void testCreate_NoBegin() {
		assertEquals(new Location("c:/a", 1, 1, 3, 4), Location.create("c:/a", "", "", "3", "4"));
	}
	
	@Test
	public void testFormat_Full() {
		assertEquals("c:/a>1:2:3:4", Location.toFormatedString(new Location("c:/a", 1, 2, 3, 4)));
	}
	
	@Test
	public void testFormat_OnlyBegin() {
		assertEquals("c:/a>1:2", Location.toFormatedString(new Location("c:/a", 1, 2)));
	}
	
	@Test
	public void testFormat_OnlyBeginLine() {
		assertEquals("c:/a>1", Location.toFormatedString(new Location("c:/a", 1)));
	}
	
	@Test
	public void testFormat_OnlyFile() {
		assertEquals("c:/a", Location.toFormatedString(new Location("c:/a")));
	}
	
	@Test
	public void testFormat_List() {
		List<Location> locs = new ArrayList<Location>();
		locs.add(new Location("a", 1));
		locs.add(new Location("b", 2, 3, 4, 5));
		assertEquals("a>1|b>2:3:4:5", Location.toFormatedString(locs));
	}
}
