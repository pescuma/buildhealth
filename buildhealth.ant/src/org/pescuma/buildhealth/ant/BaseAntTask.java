package org.pescuma.buildhealth.ant;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.pescuma.buildhealth.core.BuildHealth;

abstract class BaseAntTask extends Task {
	
	@Override
	public void execute() throws BuildException {
		if (BuildHealthAntTask.buildHealth == null)
			throw new BuildException("This task must be inside a buildhealth task.", getLocation());
		
		execute(BuildHealthAntTask.buildHealth);
	}
	
	protected abstract void execute(BuildHealth buildHealth);
	
}
