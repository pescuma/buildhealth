package org.pescuma.buildhealth.analyser.tasks;

import static java.util.Arrays.*;
import static org.pescuma.buildhealth.analyser.utils.NumbersFormater.*;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.kohsuke.MetaInfServices;
import org.pescuma.buildhealth.analyser.BuildHealthAnalyser;
import org.pescuma.buildhealth.analyser.utils.BuildHealthAnalyserUtils.TreeStats;
import org.pescuma.buildhealth.analyser.utils.SimpleTree;
import org.pescuma.buildhealth.core.BuildData;
import org.pescuma.buildhealth.core.BuildData.Line;
import org.pescuma.buildhealth.core.BuildStatus;
import org.pescuma.buildhealth.core.Report;
import org.pescuma.buildhealth.core.prefs.BuildHealthPreference;
import org.pescuma.buildhealth.prefs.Preferences;
import org.pescuma.buildhealth.projects.Projects;
import org.pescuma.buildhealth.utils.Location;

import com.google.common.base.Function;

/**
 * Expect the lines to be:
 * 
 * <pre>
 * Tasks,origin,{type:Bug,Feature,...},{status:Open,Closed,...},text,owner,created by,creation date,id,parent id,details,location
 * </pre>
 * 
 * Parent id is only used if it also has an id. If the line has no message nor id, it is assumed to be only a counting
 * of tasks.
 * 
 * Location can be one of:
 * <ul>
 * <li>filename
 * <li>filename>line
 * <li>filename>line:column
 * <li>filename>beginLine:beginColumn:endLine:endColumn
 * <li>or multiple of above, separated by | (ex: file1|file2:32)
 * </ul>
 * 
 * Example:
 * 
 * <pre>
 * 1 | Task,From code,TO DO,,pescuma,implement this,c:\a\b.java,50
 * 1 | Task,Trac,Bug,Open,,Implement task analyser,12,34,bla bla bla
 * 20 | Task,Trac,Bug,Closed
 * </pre>
 */
@MetaInfServices
public class TasksAnalyser implements BuildHealthAnalyser {
	
	private static final int COLUMN_ORIGIN = 1;
	private static final int COLUMN_TYPE = 2;
	private static final int COLUMN_STATUS = 3;
	private static final int COLUMN_TEXT = 4;
	private static final int COLUMN_OWNER = 5;
	private static final int COLUMN_CREATED_BY = 6;
	private static final int COLUMN_CREATION_DATE = 7;
	private static final int COLUMN_ID = 8;
	private static final int COLUMN_PARENT_ID = 9;
	private static final int COLUMN_DETAILS = 10;
	private static final int COLUMN_LOCATION = 11;
	
	@Override
	public String getName() {
		return "Tasks";
	}
	
	@Override
	public int getPriority() {
		return 400;
	}
	
	@Override
	public List<BuildHealthPreference> getKnownPreferences() {
		return Collections.emptyList();
	}
	
	@Override
	public List<Report> computeReport(BuildData data, Projects projects, Preferences prefs, int opts) {
		data = data.filter("Tasks");
		if (data.isEmpty())
			return Collections.emptyList();
		
		SimpleTree<Stats> tree = buildTree(data);
		
		sumChildStats(tree);
		
		return asList(toReport(tree.getRoot(), getName()));
	}
	
	private SimpleTree<Stats> buildTree(BuildData data) {
		SimpleTree<Stats> tree = new SimpleTree<Stats>(new Function<String[], Stats>() {
			@Override
			public Stats apply(String[] name) {
				return new Stats(name);
			}
		});
		
		Map<String, Entry> keyToEntry = new HashMap<String, Entry>();
		
		convertToEntries(data, tree, keyToEntry);
		buildEntriesTree(keyToEntry);
		addToStatsTree(tree, keyToEntry);
		
		return tree;
	}
	
	private void convertToEntries(BuildData data, SimpleTree<Stats> tree, Map<String, Entry> keyToEntry) {
		for (Line line : data.getLines()) {
			Entry entry = new Entry(line);
			
			if (entry.id.isEmpty()) {
				// Add to tree, no further processing needed
				addToTree(tree, entry);
				
			} else {
				keyToEntry.put(entry.fullId, entry);
			}
		}
	}
	
	private void buildEntriesTree(Map<String, Entry> keyToEntry) {
		for (Entry entry : keyToEntry.values()) {
			if (entry.fullParentId.isEmpty())
				continue;
			
			Entry parentEntry = keyToEntry.get(entry.fullParentId);
			if (parentEntry == null)
				continue;
			
			parentEntry.addChild(entry);
			entry.hasParent = true;
		}
	}
	
	private void addToStatsTree(SimpleTree<Stats> tree, Map<String, Entry> keyToEntry) {
		for (Entry entry : keyToEntry.values())
			if (!entry.hasParent)
				addToTree(tree, entry);
	}
	
	private void addToTree(SimpleTree<Stats> tree, Entry entry) {
		SimpleTree<Stats>.Node node = tree.getRoot();
		node = node.getChild(entry.origin);
		
		addToTree(node, entry);
	}
	
	private void addToTree(SimpleTree<Stats>.Node node, Entry entry) {
		boolean isRealTask = !entry.id.isEmpty() || !entry.text.isEmpty();
		
		if (!isRealTask) {
			// Only add stats to parent
			Stats stats = node.getData();
			stats.addToType(entry);
			return;
		}
		
		String key = entry.type + "\n" + entry.status + "\n" + entry.id + "\n" + entry.text;
		node = node.getChild(key);
		
		Stats stats = node.getData();
		stats.entry = entry;
		stats.addToType(entry);
		
		for (Entry child : entry.children)
			addToTree(node, child);
	}
	
	private void sumChildStats(SimpleTree<Stats> tree) {
		tree.visit(new SimpleTree.Visitor<Stats>() {
			Deque<Stats> parents = new ArrayDeque<Stats>();
			
			@Override
			public void preVisitNode(SimpleTree<Stats>.Node node) {
				parents.push(node.getData());
			}
			
			@Override
			public void posVisitNode(SimpleTree<Stats>.Node node) {
				parents.pop();
				
				Stats stats = node.getData();
				
				if (!parents.isEmpty())
					parents.peekFirst().addChild(stats);
			}
		});
	}
	
	private Report toReport(SimpleTree<Stats>.Node node, String name) {
		Stats stats = node.getData();
		
		List<Report> children = new ArrayList<Report>();
		
		for (SimpleTree<Stats>.Node child : node.getChildren())
			children.add(toReport(child, child.getName()));
		
		if (stats.entry != null) {
			return new TaskReport(BuildStatus.Good, stats.entry.id, stats.entry.text, stats.entry.owner,
					stats.entry.createdBy, stats.entry.creationDate, stats.entry.type, stats.entry.status,
					stats.entry.details, stats.entry.locations, stats.entry.count, children);
			
		} else {
			StringBuilder description = new StringBuilder();
			double total = 0;
			
			if (!stats.types.isEmpty()) {
				Set<String> statuses = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
				Set<String> types = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
				
				for (Type type : stats.types.values())
					(Stats.TYPE.equals(type.type) ? types : statuses).add(type.name);
				
				for (String status : statuses) {
					Type type = stats.getType(Stats.STATUS, status);
					total += type.count;
					append(description, type);
				}
				
				description.append(" ; ");
				
				for (String type : types)
					append(description, stats.getType(Stats.TYPE, type));
			}
			
			return new Report(stats.getStatus(), name, format1000(total), description.toString(), null, children);
		}
	}
	
	private void append(StringBuilder description, Type type) {
		int length = description.length();
		if (length > 0 && description.charAt(length - 1) != ' ')
			description.append(", ");
		
		description.append(type.name.trim()).append(": ").append(format1000(type.count));
	}
	
	private static class Entry {
		double count;
		String origin;
		String id;
		String text;
		String type;
		String status;
		String owner;
		String createdBy;
		String creationDate;
		String fullId;
		String fullParentId;
		String details;
		List<Location> locations;
		boolean hasParent = false;
		final List<Entry> children = new ArrayList<Entry>();
		
		Entry(Line line) {
			count = line.getValue();
			origin = line.getColumn(COLUMN_ORIGIN);
			text = line.getColumn(COLUMN_TEXT);
			type = line.getColumn(COLUMN_TYPE);
			status = line.getColumn(COLUMN_STATUS);
			if (status.isEmpty())
				status = "Open";
			owner = line.getColumn(COLUMN_OWNER);
			createdBy = line.getColumn(COLUMN_CREATED_BY);
			creationDate = line.getColumn(COLUMN_CREATION_DATE);
			details = line.getColumn(COLUMN_DETAILS);
			locations = Location.parse(line.getColumn(COLUMN_LOCATION));
			
			id = line.getColumn(COLUMN_ID);
			fullId = origin + "\n" + id;
			
			String parentId = line.getColumn(COLUMN_PARENT_ID);
			if (parentId.isEmpty())
				fullParentId = "";
			else
				fullParentId = origin + "\n" + parentId;
		}
		
		void addChild(Entry stats) {
			children.add(stats);
		}
	}
	
	private static class Stats extends TreeStats {
		static final String STATUS = "status";
		static final String TYPE = "type";
		
		Entry entry;
		
		final Map<String, Type> types = new HashMap<String, Type>();
		
		Stats(String... names) {
			super(names);
		}
		
		void addToType(Entry entry) {
			getType(STATUS, entry.status).count += entry.count;
			getType(TYPE, entry.type).count += entry.count;
		}
		
		Type getType(String type, String name) {
			String key = type + "_" + name;
			
			Type result = types.get(key);
			if (result == null) {
				result = new Type(type, name);
				types.put(key, result);
			}
			
			return result;
		}
		
		void addChild(Stats child) {
			for (Type type : child.types.values())
				getType(type.type, type.name).count += type.count;
		}
	}
	
	private static class Type {
		final String type;
		final String name;
		double count;
		
		Type(String type, String name) {
			this.type = type;
			this.name = name;
		}
	}
}
