package org.jenkinsci.plugins.buildhealth;

import hudson.Extension;
import hudson.FilePath;
import hudson.model.AbstractProject;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;
import hudson.util.FormValidation;

import java.io.IOException;

import net.sf.json.JSONObject;

import org.kohsuke.stapler.AncestorInPath;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

public class BuildHealthRecorder extends Recorder {
	
	private String folder;
	
	public BuildHealthRecorder(String folder) {
		this.folder = folder;
	}
	
	public BuildStepMonitor getRequiredMonitorService() {
		return BuildStepMonitor.NONE;
	}
	
	@Extension
	public static class DescriptorImpl extends BuildStepDescriptor<Publisher> {
		
		public String getDisplayName() {
			return "Publish buildhealth report";
		}
		
		@Override
		public String getHelpFile() {
			return "/plugin/buildhealth/help.html";
		}
		
		@Override
		public Publisher newInstance(StaplerRequest req, JSONObject formData)
				throws hudson.model.Descriptor.FormException {
			String folder = formData.getString("folder");
			return new BuildHealthRecorder(folder);
		}
		
		public FormValidation doCheckFolder(@AncestorInPath AbstractProject project, @QueryParameter String value)
				throws IOException {
			FilePath ws = project.getSomeWorkspace();
			if (ws == null)
				return FormValidation.ok();
			
			return ws.validateRelativeDirectory(value);
		}
		
		public boolean isApplicable(Class<? extends AbstractProject> jobType) {
			return true;
		}
	}
	
}
