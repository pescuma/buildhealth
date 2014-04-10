package org.pescuma.buildhealth.core.data;

import static java.lang.System.*;
import static java.util.Arrays.*;
import static org.apache.commons.io.FileUtils.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.Collection;
import java.util.Map;

import org.pescuma.buildhealth.core.BuildData;
import org.pescuma.buildhealth.utils.CSV;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

import com.google.common.base.Predicate;
import com.google.common.io.Closer;

public class DiskBuildData implements BuildData {
	
	private final File file;
	private final BuildData data;
	private boolean loadedFromDisk = false;
	private boolean wroteData = false;
	
	public DiskBuildData(File file, BuildData data) {
		this.file = file;
		this.data = data;
	}
	
	public void saveToDisk() {
		if (!wroteData)
			return;
		
		Closer closer = Closer.create();
		try {
			try {
				forceMkdir(file.getParentFile());
				
				Writer writer = closer.register(new OutputStreamWriter(new FileOutputStream(file, !loadedFromDisk),
						"UTF-8"));
				CSVWriter csv = closer.register(CSV.newWriter(writer));
				
				for (Line line : data.getLines()) {
					String[] cols = line.getColumns();
					
					String[] toWrite = new String[cols.length + 1];
					toWrite[0] = Double.toString(line.getValue());
					arraycopy(cols, 0, toWrite, 1, cols.length);
					
					csv.writeNext(toWrite);
				}
				
			} catch (IOException e) {
				throw closer.rethrow(e);
				
			} finally {
				closer.close();
			}
		} catch (IOException e) {
			throw new RuntimeException("Error writing csv: " + file, e);
		}
		
	}
	
	private void loadFromDisk() {
		if (loadedFromDisk)
			return;
		loadedFromDisk = true;
		
		if (!file.exists())
			return;
		
		Closer closer = Closer.create();
		try {
			try {
				
				Reader reader = closer.register(new InputStreamReader(new FileInputStream(file), "UTF-8"));
				CSVReader csv = closer.register(CSV.newReader(reader));
				
				String[] line;
				while ((line = csv.readNext()) != null) {
					if (line.length == 1 && line[0].trim().isEmpty())
						continue;
					
					double val = Double.parseDouble(line[0]);
					String[] info = copyOfRange(line, 1, line.length);
					data.add(val, info);
				}
				
			} catch (IOException e) {
				throw closer.rethrow(e);
				
			} finally {
				closer.close();
			}
		} catch (IOException e) {
			throw new RuntimeException("Error reading csv: " + file, e);
		}
	}
	
	@Override
	public boolean isEmpty() {
		loadFromDisk();
		return data.isEmpty();
	}
	
	@Override
	public void add(double value, String... info) {
		wroteData = true;
		data.add(value, info);
	}
	
	@Override
	public double get(String... info) {
		loadFromDisk();
		return data.get(info);
	}
	
	@Override
	public Collection<Line> getLines() {
		loadFromDisk();
		return data.getLines();
	}
	
	@Override
	public Collection<String> getDistinct(int column) {
		loadFromDisk();
		return data.getDistinct(column);
	}
	
	@Override
	public Collection<String[]> getDistinct(int... columns) {
		loadFromDisk();
		return data.getDistinct(columns);
	}
	
	@Override
	public Map<String, Value> sumDistinct(int columns) {
		loadFromDisk();
		return data.sumDistinct(columns);
	}
	
	@Override
	public Map<String[], Value> sumDistinct(int... columns) {
		loadFromDisk();
		return data.sumDistinct(columns);
	}
	
	@Override
	public BuildData filter(String... info) {
		loadFromDisk();
		return data.filter(info);
	}
	
	@Override
	public BuildData filter(int column, String value) {
		loadFromDisk();
		return data.filter(column, value);
	}
	
	@Override
	public BuildData filter(Predicate<Line> predicate) {
		loadFromDisk();
		return data.filter(predicate);
	}
	
	@Override
	public BuildData filter(int column, Predicate<String> predicate) {
		loadFromDisk();
		return data.filter(column, predicate);
	}
	
	@Override
	public double sum() {
		loadFromDisk();
		return data.sum();
	}
	
	@Override
	public int size() {
		loadFromDisk();
		return data.size();
	}
	
	@Override
	public Collection<String> getColumn(int column) {
		loadFromDisk();
		return data.getColumn(column);
	}
	
	@Override
	public Collection<String[]> getColumns(int... columns) {
		loadFromDisk();
		return data.getColumns(columns);
	}
	
}
