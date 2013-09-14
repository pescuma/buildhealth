package org.pescuma.buildhealth.notifiers.growl;

import static org.pescuma.buildhealth.core.BuildStatus.*;
import static org.pescuma.buildhealth.core.ReportFormater.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import net.sf.libgrowl.Application;
import net.sf.libgrowl.GrowlConnector;
import net.sf.libgrowl.Notification;
import net.sf.libgrowl.NotificationType;
import net.sf.libgrowl.internal.IResponse;

import org.kohsuke.MetaInfServices;
import org.pescuma.buildhealth.core.BuildStatus;
import org.pescuma.buildhealth.core.Report;
import org.pescuma.buildhealth.core.ReportFormater;
import org.pescuma.buildhealth.core.prefs.BuildHealthPreference;
import org.pescuma.buildhealth.notifiers.BuildHealthNotifier;
import org.pescuma.buildhealth.notifiers.BuildHealthNotifierTracker;
import org.pescuma.buildhealth.prefs.Preferences;

@MetaInfServices
public class GrowlNotifier implements BuildHealthNotifier {
	
	@Override
	public String getName() {
		return "Growl notifier";
	}
	
	@Override
	public int getPriority() {
		return 500;
	}
	
	@Override
	public List<BuildHealthPreference> getPreferences() {
		List<BuildHealthPreference> result = new ArrayList<BuildHealthPreference>();
		
		result.add(new BuildHealthPreference("Growl server name", "true", "notification", "growl", "enabled"));
		result.add(new BuildHealthPreference("Growl server name", "localhost", "notification", "growl", "server"));
		result.add(new BuildHealthPreference("Growl server port", "23053", "notification", "growl", "port"));
		
		return Collections.unmodifiableList(result);
	}
	
	@Override
	public void sendNotification(Report report, Preferences prefs, BuildHealthNotifierTracker tracker) {
		prefs = prefs.child("notification", "growl");
		
		if (!prefs.get("enabled", true))
			return;
		
		String reportText = new ReportFormater().hideDescriptions().format(report).trim();
		
		GrowlConnector growl = new GrowlConnector(prefs.get("server", "localhost"), prefs.get("port", 23053));
		
		Application app = new Application("buildhealth", getIcon());
		NotificationType goodNotification = newNotificationType(Good);
		NotificationType sosoNotification = newNotificationType(SoSo);
		NotificationType problematicNotification = newNotificationType(Problematic);
		
		int registered = growl.register(app, new NotificationType[] { goodNotification, sosoNotification,
				problematicNotification });
		if (registered != IResponse.OK)
			return;
		
		NotificationType toUse;
		switch (report.getStatus()) {
			case Good:
				toUse = goodNotification;
				break;
			case SoSo:
				toUse = sosoNotification;
				break;
			case Problematic:
				toUse = problematicNotification;
				break;
			default:
				throw new IllegalStateException();
		}
		
		Notification notif = new Notification(app, toUse, "Build finished", reportText);
		// It has to send the icon here too or snarl doesn't work
		notif.setIcon(getIcon(report.getStatus()));
		growl.notify(notif);
		
		tracker.reportNotified("Sent growl notification");
	}
	
	private NotificationType newNotificationType(BuildStatus status) {
		NotificationType type = new NotificationType(status.name(), "Build is " + createTitle(status), getIcon(status));
		return type;
	}
	
	private String getIcon() {
		return "https://raw.github.com/pescuma/buildhealth/master/icons/bh.png";
	}
	
	private String getIcon(BuildStatus status) {
		return "https://raw.github.com/pescuma/buildhealth/master/icons/bh_"
				+ status.name().toLowerCase(Locale.ENGLISH) + ".png";
	}
}
