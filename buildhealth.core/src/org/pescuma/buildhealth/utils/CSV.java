package org.pescuma.buildhealth.utils;

import java.io.Reader;
import java.io.Writer;

import au.com.bytecode.opencsv.CSVParser;
import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

/**
 * For some reason the CSVReader and CSVWriter don't agree on encoding :(
 * 
 * So let's make them agree
 */
public class CSV {
	
	public static CSVWriter newWriter(Writer writer) {
		return new CSVWriter(writer, CSVWriter.DEFAULT_SEPARATOR, CSVWriter.DEFAULT_QUOTE_CHARACTER,
				CSVWriter.DEFAULT_ESCAPE_CHARACTER);
	}
	
	public static CSVReader newReader(Reader reader) {
		return new CSVReader(reader, CSVWriter.DEFAULT_SEPARATOR, CSVWriter.DEFAULT_QUOTE_CHARACTER,
				CSVParser.NULL_CHARACTER);
	}
	
}
