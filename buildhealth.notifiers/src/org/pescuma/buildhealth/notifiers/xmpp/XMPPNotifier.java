package org.pescuma.buildhealth.notifiers.xmpp;

import static org.pescuma.buildhealth.core.prefs.BuildHealthPreference.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.net.ssl.SSLSocketFactory;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.kohsuke.MetaInfServices;
import org.pescuma.buildhealth.core.BuildStatus;
import org.pescuma.buildhealth.core.Report;
import org.pescuma.buildhealth.core.prefs.BuildHealthPreference;
import org.pescuma.buildhealth.notifiers.BuildHealthNotifier;
import org.pescuma.buildhealth.notifiers.BuildHealthNotifierTracker;
import org.pescuma.buildhealth.prefs.Preferences;
import org.pescuma.buildhealth.utils.ReportFormater;

@MetaInfServices
public class XMPPNotifier implements BuildHealthNotifier {
	
	@Override
	public String getName() {
		return "XMPP notifier";
	}
	
	@Override
	public int getPriority() {
		return 500;
	}
	
	@Override
	public List<BuildHealthPreference> getKnownPreferences() {
		List<BuildHealthPreference> result = new ArrayList<BuildHealthPreference>();
		
		result.add(new BuildHealthPreference("XMPP notification enabled", "true", "notification", "xmpp",
				ANY_VALUE_KEY_PREFIX + "<config slot>", "enabled"));
		result.add(new BuildHealthPreference("XMPP server name", "<from username>", "notification", "xmpp",
				ANY_VALUE_KEY_PREFIX + "<config slot>", "server"));
		result.add(new BuildHealthPreference("XMPP server port", "5222", "notification", "xmpp", ANY_VALUE_KEY_PREFIX
				+ "<config slot>", "port"));
		result.add(new BuildHealthPreference("XMPP login username", "buildhealth", "notification", "xmpp",
				ANY_VALUE_KEY_PREFIX + "<config slot>", "username"));
		result.add(new BuildHealthPreference("XMPP login password", "", "notification", "xmpp", ANY_VALUE_KEY_PREFIX
				+ "<config slot>", "password"));
		result.add(new BuildHealthPreference("XMPP destination (use # for groups) (multiple can be separated by ;)",
				"", "notification", "xmpp", ANY_VALUE_KEY_PREFIX + "<config slot>", "destination"));
		result.add(new BuildHealthPreference("XMPP ignore Good builds", "false", "notification", "xmpp",
				ANY_VALUE_KEY_PREFIX + "<config slot>", "ignore-good"));
		result.add(new BuildHealthPreference("XMPP ignore SoSo builds", "false", "notification", "xmpp",
				ANY_VALUE_KEY_PREFIX + "<config slot>", "ignore-soso"));
		result.add(new BuildHealthPreference("XMPP ignore PROBLEMATIC builds", "false", "notification", "xmpp",
				ANY_VALUE_KEY_PREFIX + "<config slot>", "ignore-problematic"));
		
		return Collections.unmodifiableList(result);
	}
	
	@Override
	public void sendNotification(Report report, Preferences prefs, BuildHealthNotifierTracker tracker) {
		prefs = prefs.child("notification", "xmpp");
		
		for (String child : prefs.getChildrenKeys())
			internalSendNotification(report, prefs.child(child), child, tracker);
	}
	
	private void internalSendNotification(Report report, Preferences prefs, String name,
			BuildHealthNotifierTracker tracker) {
		if (!prefs.get("enabled", true))
			return;
		
		String server = prefs.get("server", "");
		int port = prefs.get("port", 5222);
		String username = prefs.get("username", "buildhealth");
		String password = prefs.get("password", "");
		List<String> destination = removeEmpty(prefs.get("destination", "").split(";"));
		boolean ignoreGood = prefs.get("ignore-good", false);
		boolean ignoreSoso = prefs.get("ignore-soso", false);
		boolean ignoreProblematic = prefs.get("ignore-problematic", false);
		
		if (ignoreGood && report.getStatus() == BuildStatus.Good)
			return;
		if (ignoreSoso && report.getStatus() == BuildStatus.SoSo)
			return;
		if (ignoreProblematic && report.getStatus() == BuildStatus.Problematic)
			return;
		
		if (server.isEmpty()) {
			tracker.reportNotified("Ignored XMPP notification to " + name + " because of missing server name");
			return;
		}
		if (username.isEmpty()) {
			tracker.reportNotified("Ignored XMPP notification to " + name + " because of missing user name");
			return;
		}
		if (destination.isEmpty()) {
			tracker.reportNotified("Ignored XMPP notification to " + name + " because of missing destinations");
			return;
		}
		
		String reportText = new ReportFormater().writeBuildStatuses().format(report).trim();
		
		Connection conn = null;
		try {
			
			String user = StringUtils.parseName(username);
			String service = StringUtils.parseServer(username);
			String resource = StringUtils.parseResource(username);
			if (resource.isEmpty())
				resource = "buildhealth";
			
			ConnectionConfiguration config;
			if (server.isEmpty())
				config = new ConnectionConfiguration(service, port);
			else if (service.isEmpty())
				config = new ConnectionConfiguration(server, port);
			else
				config = new ConnectionConfiguration(server, port, service);
			
			config.setCompressionEnabled(true);
			config.setSelfSignedCertificateEnabled(true);
			
			conn = connect(config);
			
			conn.login(user, password, resource);
			
			for (String dest : destination) {
				if (dest.startsWith("#"))
					sendGroupChatMessage(conn, dest.substring(1), user, reportText, tracker);
				else
					sendDirectMessage(conn, dest, reportText, tracker);
			}
			
		} catch (XMPPException e) {
			throw new RuntimeException(e);
			
		} finally {
			if (conn != null)
				conn.disconnect();
		}
	}
	
	private Connection connect(ConnectionConfiguration config) throws XMPPException {
		XMPPException originalException = null;
		
		try {
			
			Connection conn = new XMPPConnection(config);
			conn.connect();
			if (conn.isConnected())
				return conn;
			
		} catch (XMPPException e) {
			originalException = e;
		}
		
		try {
			
			config.setSocketFactory(SSLSocketFactory.getDefault());
			XMPPConnection conn = new XMPPConnection(config);
			conn.connect();
			return conn;
			
		} catch (XMPPException e) {
			if (originalException != null) {
				throw new XMPPException("Exception of original (without legacy SSL) connection attempt",
						originalException);
			} else {
				throw new XMPPException(e);
			}
		}
	}
	
	private void sendDirectMessage(Connection conn, String dest, String report, BuildHealthNotifierTracker tracker)
			throws XMPPException {
		Chat chat = conn.getChatManager().createChat(dest, null);
		chat.sendMessage(report);
		
		tracker.reportNotified("Sent XMPP notification to " + dest);
	}
	
	private void sendGroupChatMessage(Connection conn, String dest, String username, String report,
			BuildHealthNotifierTracker tracker) throws XMPPException {
		MultiUserChat chat = new MultiUserChat(conn, dest);
		
		chat.join(username);
		
		chat.sendMessage(report);
		
		chat.leave();
		
		tracker.reportNotified("Sent XMPP notification to #" + dest);
	}
	
	private List<String> removeEmpty(String[] els) {
		List<String> result = new ArrayList<String>();
		
		for (String el : els)
			if (!el.isEmpty())
				result.add(el);
		
		return result;
	}
	
}
