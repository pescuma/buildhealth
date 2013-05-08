package org.jenkinsci.plugins.buildhealth;

import hudson.AbortException;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;
import hudson.util.FormValidation;

import java.io.File;
import java.io.IOException;

import net.sf.json.JSONObject;

import org.kohsuke.stapler.AncestorInPath;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

public class BuildHealthRecorder extends Recorder {
	
	private String folder;
	
	@DataBoundConstructor
	public BuildHealthRecorder(String folder) {
		this.folder = folder;
	}
	
	public String getFolder() {
		return folder;
	}
	
	public void setFolder(String folder) {
		this.folder = folder;
	}
	
	public BuildStepMonitor getRequiredMonitorService() {
		return BuildStepMonitor.NONE;
	}
	
	@Override
	public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener)
			throws InterruptedException, IOException {
		listener.getLogger().println("Recording buildhealth results");
		
		FilePath workspace = build.getWorkspace();
		if (workspace == null)
			throw new AbortException("No workspace found for " + build);
		
		File localHome = new File(build.getRootDir(), "buildhealth");
		
		String homeRelativeFolder = build.getEnvironment(listener).expand(this.folder);
		FilePath remoteHome = workspace.child(homeRelativeFolder);
		if (remoteHome.copyRecursiveTo(new FilePath(localHome)) == 0) {
			listener.getLogger().println(
					"Nothing to do: no files in buildhealth home folder '" + homeRelativeFolder + "'");
			return true;
		}
		
		return true;
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
			if (value.trim().isEmpty())
				return FormValidation.error("A home folder is required");
			
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
