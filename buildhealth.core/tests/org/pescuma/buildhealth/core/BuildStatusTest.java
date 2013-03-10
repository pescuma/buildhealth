package org.pescuma.buildhealth.core;

import static org.junit.Assert.*;

import org.junit.Test;

public class BuildStatusTest {
	
	@Test
	public void testMergeNulls() {
		assertEquals(null, BuildStatus.merge(null, null));
	}
	
	@Test
	public void testMergeFirstNull() {
		assertEquals(BuildStatus.Good, BuildStatus.merge(null, BuildStatus.Good));
		assertEquals(BuildStatus.Problematic, BuildStatus.merge(null, BuildStatus.Problematic));
		assertEquals(BuildStatus.SoSo, BuildStatus.merge(null, BuildStatus.SoSo));
	}
	
	@Test
	public void testMergeSecondNull() {
		assertEquals(BuildStatus.Good, BuildStatus.merge(BuildStatus.Good, null));
		assertEquals(BuildStatus.Problematic, BuildStatus.merge(BuildStatus.Problematic, null));
		assertEquals(BuildStatus.SoSo, BuildStatus.merge(BuildStatus.SoSo, null));
	}
	
	@Test
	public void testMergeEquals() {
		assertEquals(BuildStatus.Good, BuildStatus.merge(BuildStatus.Good, BuildStatus.Good));
		assertEquals(BuildStatus.Problematic, BuildStatus.merge(BuildStatus.Problematic, BuildStatus.Problematic));
		assertEquals(BuildStatus.SoSo, BuildStatus.merge(BuildStatus.SoSo, BuildStatus.SoSo));
	}
	
	@Test
	public void testMergeGoodProblematic() {
		assertEquals(BuildStatus.Problematic, BuildStatus.merge(BuildStatus.Good, BuildStatus.Problematic));
		assertEquals(BuildStatus.Problematic, BuildStatus.merge(BuildStatus.Problematic, BuildStatus.Good));
	}
	
	@Test
	public void testMergeGoodSoSo() {
		assertEquals(BuildStatus.SoSo, BuildStatus.merge(BuildStatus.Good, BuildStatus.SoSo));
		assertEquals(BuildStatus.SoSo, BuildStatus.merge(BuildStatus.SoSo, BuildStatus.Good));
	}
	
	@Test
	public void testMergeSoSoProblematic() {
		assertEquals(BuildStatus.Problematic, BuildStatus.merge(BuildStatus.SoSo, BuildStatus.Problematic));
		assertEquals(BuildStatus.Problematic, BuildStatus.merge(BuildStatus.Problematic, BuildStatus.SoSo));
	}
}
