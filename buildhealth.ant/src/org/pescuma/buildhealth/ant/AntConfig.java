package org.pescuma.buildhealth.ant;

import org.apache.tools.ant.BuildException;
import org.pescuma.buildhealth.core.BuildHealth;

public interface AntConfig {
	
	public void execute(BuildHealth buildHealth) throws BuildException;
	
}
