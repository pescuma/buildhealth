package org.pescuma.buildhealth.core;

import static org.junit.Assert.*;

import org.junit.Test;
import org.pescuma.buildhealth.core.BuildStatus;

public class BuildStatusTest {
	
	@Test
	public void testMergeNulls() {
		assertEquals(null, BuildStatus.merge(null, null));
	}
	
	@Test
	public void testMergeFirstNull() {
		assertEquals(BuildStatus.Successful, BuildStatus.merge(null, BuildStatus.Successful));
		assertEquals(BuildStatus.Failed, BuildStatus.merge(null, BuildStatus.Failed));
		assertEquals(BuildStatus.SoSo, BuildStatus.merge(null, BuildStatus.SoSo));
	}
	
	@Test
	public void testMergeSecondNull() {
		assertEquals(BuildStatus.Successful, BuildStatus.merge(BuildStatus.Successful, null));
		assertEquals(BuildStatus.Failed, BuildStatus.merge(BuildStatus.Failed, null));
		assertEquals(BuildStatus.SoSo, BuildStatus.merge(BuildStatus.SoSo, null));
	}
	
	@Test
	public void testMergeEquals() {
		assertEquals(BuildStatus.Successful, BuildStatus.merge(BuildStatus.Successful, BuildStatus.Successful));
		assertEquals(BuildStatus.Failed, BuildStatus.merge(BuildStatus.Failed, BuildStatus.Failed));
		assertEquals(BuildStatus.SoSo, BuildStatus.merge(BuildStatus.SoSo, BuildStatus.SoSo));
	}
	
	@Test
	public void testMergeSuccessfulFailed() {
		assertEquals(BuildStatus.Failed, BuildStatus.merge(BuildStatus.Successful, BuildStatus.Failed));
		assertEquals(BuildStatus.Failed, BuildStatus.merge(BuildStatus.Failed, BuildStatus.Successful));
	}
	
	@Test
	public void testMergeSuccessfulSoSo() {
		assertEquals(BuildStatus.SoSo, BuildStatus.merge(BuildStatus.Successful, BuildStatus.SoSo));
		assertEquals(BuildStatus.SoSo, BuildStatus.merge(BuildStatus.SoSo, BuildStatus.Successful));
	}
	
	@Test
	public void testMergeSoSoFailed() {
		assertEquals(BuildStatus.Failed, BuildStatus.merge(BuildStatus.SoSo, BuildStatus.Failed));
		assertEquals(BuildStatus.Failed, BuildStatus.merge(BuildStatus.Failed, BuildStatus.SoSo));
	}
}
