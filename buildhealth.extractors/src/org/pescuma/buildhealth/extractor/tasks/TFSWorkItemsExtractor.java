package org.pescuma.buildhealth.extractor.tasks;

import static org.pescuma.buildhealth.extractor.utils.StringBuilderUtils.*;
import static org.pescuma.buildhealth.utils.StringHelper.*;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jdom2.Document;
import org.jdom2.Element;
import org.pescuma.buildhealth.core.BuildData;
import org.pescuma.buildhealth.extractor.BaseXMLExtractor;
import org.pescuma.buildhealth.extractor.PseudoFiles;

// From tfpt query /collection:uri /format:xml > wis.xml
public class TFSWorkItemsExtractor extends BaseXMLExtractor {
	
	private static final String FIELDS_COMMON_PREFIX = "System.";
	
	public TFSWorkItemsExtractor(PseudoFiles files) {
		super(files);
	}
	
	@Override
	protected void extractDocument(File file, String filename, Document doc, BuildData data) {
		checkRoot(doc, "WorkItems", filename);
		
		extractChildren(data, doc.getRootElement(), "");
	}
	
	private void extractChildren(BuildData data, Element el, String parentId) {
		for (Element child : el.getChildren("WorkItem"))
			extractWorkItem(data, child, parentId);
		
		for (Element child : findElementsXPath(el, "Links/Link")) {
			String type = child.getAttributeValue("Type", "");
			extractChildren(data, child, type.equals("Child") ? parentId : "");
		}
	}
	
	private void extractWorkItem(BuildData data, Element el, String parentId) {
		// Keep insertion order so we show data in a nice order in details
		@SuppressWarnings("serial")
		Map<String, String> fields = new LinkedHashMap<String, String>() {
			@Override
			public String remove(Object key) {
				String result = super.remove(key);
				if (result == null)
					return "";
				else
					return result;
			}
		};
		
		extractFields(fields, el);
		
		String id = firstNonEmpty(el.getAttributeValue("Id"), fields.remove("Id"));
		String title = fields.remove("Title");
		if (!id.isEmpty() || !title.isEmpty()) {
			String type = firstNonEmpty(fields.remove("WorkItemType"), "Bug");
			String state = firstNonEmpty(fields.remove("State"), "Open");
			String owner = fields.remove("AssignedTo");
			String createdBy = fields.remove("CreatedBy");
			String creationDate = fields.remove("CreatedDate");
			
			StringBuilder details = new StringBuilder();
			for (Map.Entry<String, String> entry : fields.entrySet())
				appendInNewLine(details, splitCamelCase(entry.getKey()), entry.getValue());
			
			data.add(1, "Tasks", "TFS", type, state, title, owner, createdBy, creationDate, id, parentId,
					details.toString());
		}
		
		extractChildren(data, el, id);
	}
	
	private Map<String, String> extractFields(Map<String, String> result, Element el) {
		for (Element child : findElementsXPath(el, "Fields/Field")) {
			String name = child.getAttributeValue("RefName", "");
			String value = child.getAttributeValue("Value", "");
			
			if (name.isEmpty() || value.isEmpty())
				continue;
			
			if (name.startsWith(FIELDS_COMMON_PREFIX))
				name = name.substring(FIELDS_COMMON_PREFIX.length());
			
			result.put(name, value);
		}
		
		return result;
	}
}
