package org.pescuma.buildhealth.extractor;

import java.io.IOException;
import java.io.InputStream;

import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.pescuma.buildhealth.core.BuildData;

public abstract class BaseXMLExtractor extends BaseBuildDataExtractor {
	
	public BaseXMLExtractor(PseudoFiles files) {
		super(files, "xml");
	}
	
	@Override
	protected void extract(String filename, InputStream input, BuildData data) throws IOException {
		try {
			
			Document doc = JDomUtil.parse(input);
			extractDocument(filename, doc, data);
			
		} catch (JDOMException e) {
			throw new BuildDataExtractorException(e);
		}
	}
	
	protected abstract void extractDocument(String filename, Document doc, BuildData data);
	
	protected void checkRoot(Document doc, String name) {
		if (!doc.getRootElement().getName().equals(name))
			throw new BuildDataExtractorException("Invalid file format: top node must be " + name);
	}
	
}
