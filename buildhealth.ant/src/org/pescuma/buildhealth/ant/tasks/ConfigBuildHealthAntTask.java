package org.pescuma.buildhealth.ant.tasks;

import org.apache.tools.ant.BuildException;
import org.pescuma.buildhealth.ant.BaseBuildHealthAntSubTask;
import org.pescuma.buildhealth.core.BuildHealth;
import org.pescuma.buildhealth.prefs.Preferences;

public class ConfigBuildHealthAntTask extends BaseBuildHealthAntSubTask {
	
	private String key;
	private String value;
	
	public String getKey() {
		return key;
	}
	
	public void setKey(String key) {
		this.key = key;
	}
	
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	@Override
	protected void execute(BuildHealth buildHealth) {
		if (key == null || key.isEmpty())
			throw new BuildException("config must have a non-empty attribute 'key'", getLocation());
		if (value == null || value.isEmpty())
			throw new BuildException("config must have a non-empty attribute 'value'", getLocation());
		
		Preferences prefs = buildHealth.getPreferences();
		
		String[] ks = key.trim().split(" ");
		for (int i = 0; i < ks.length - 1; i++)
			prefs = prefs.child(ks[i]);
		
		prefs.set(ks[ks.length - 1], value);
	}
	
}
