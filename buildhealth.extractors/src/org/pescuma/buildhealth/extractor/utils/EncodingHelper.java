package org.pescuma.buildhealth.extractor.utils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import com.ibm.icu.text.CharsetDetector;
import com.ibm.icu.text.CharsetMatch;

public class EncodingHelper {
	
	public static Reader toReader(InputStream input) throws IOException {
		if (!input.markSupported())
			input = new BufferedInputStream(input);
		
		CharsetDetector charsetDetector = new CharsetDetector();
		charsetDetector.setText(input);
		
		CharsetMatch m = charsetDetector.detect();
		
		Reader reader;
		if (m.getConfidence() > 50) {
			reader = m.getReader();
		} else {
			reader = new InputStreamReader(input);
		}
		return reader;
	}
}
