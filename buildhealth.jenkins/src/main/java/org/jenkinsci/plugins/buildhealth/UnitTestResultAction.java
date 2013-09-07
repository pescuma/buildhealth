package org.jenkinsci.plugins.buildhealth;

import hudson.model.BuildListener;
import hudson.model.AbstractBuild;
import hudson.tasks.test.AbstractTestResultAction;

import org.kohsuke.stapler.export.Exported;

public class UnitTestResultAction extends AbstractTestResultAction<UnitTestResultAction> {
	
	private UnitTestResult result;
	
	protected UnitTestResultAction(@SuppressWarnings("rawtypes") AbstractBuild owner, UnitTestResult result,
			BuildListener listener) {
		super(owner);
		this.result = result;
	}
	
	@Override
	@Exported(visibility = 2)
	public int getTotalCount() {
		return result.getTotalCount();
	}
	
	@Override
	@Exported(visibility = 2)
	public int getFailCount() {
		return result.getFailCount();
	}
	
	@Override
	@Exported(visibility = 2)
	public int getSkipCount() {
		return result.getSkipCount();
	}
	
	@Override
	public Object getResult() {
		return result;
	}
	
}
