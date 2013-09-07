package org.jenkinsci.plugins.buildhealth;

import hudson.model.AbstractBuild;
import hudson.tasks.test.AbstractTestResultAction;
import hudson.tasks.test.TestObject;
import hudson.tasks.test.TestResult;

import org.kohsuke.stapler.export.Exported;

public class UnitTestResult extends TestResult {
	
	private static final long serialVersionUID = -672458641075544208L;
	
	private UnitTestResultAction parentAction;
	private TestObject parent;
	private final float duration;
	private final int totalTests;
	private final int failedTests;
	private final int skippedTests;
	
	public UnitTestResult(int totalTests, int failedTests, int skippedTests, float duration) {
		this.totalTests = totalTests;
		this.failedTests = failedTests;
		this.skippedTests = skippedTests;
		this.duration = duration;
	}
	
	@Override
	public String getName() {
		return "buildhealth-unittests";
	}
	
	@Override
	public void setParentAction(@SuppressWarnings("rawtypes") AbstractTestResultAction action) {
		this.parentAction = (UnitTestResultAction) action;
	}
	
	@Override
	public UnitTestResultAction getParentAction() {
		return this.parentAction;
	}
	
	@Override
	public AbstractBuild<?, ?> getOwner() {
		return (parentAction == null ? null : parentAction.owner);
	}
	
	public TestObject getParent() {
		return parent;
	}
	
	@Override
	public void setParent(TestObject parent) {
		this.parent = parent;
	}
	
	public String getDisplayName() {
		return "Test Results";
	}
	
	@Override
	public String getTitle() {
		return "Test Result";
	}
	
	@Exported(visibility = 999)
	@Override
	public float getDuration() {
		return duration;
	}
	
	@Exported(visibility = 999)
	@Override
	public int getPassCount() {
		return totalTests - getFailCount() - getSkipCount();
	}
	
	@Exported(visibility = 999)
	@Override
	public int getFailCount() {
		return failedTests;
	}
	
	@Exported(visibility = 999)
	@Override
	public int getSkipCount() {
		return skippedTests;
	}
	
	@Override
	public boolean isPassed() {
		return failedTests == 0;
	}
	
	@Override
	public TestResult findCorrespondingResult(String id) {
		if (getId().equals(id) || (id == null))
			return this;
		
		return null;
	}
	
}
