package org.pescuma.buildhealth.cli.commands;

import static org.junit.Assert.*;
import static org.pescuma.buildhealth.cli.commands.CliUtils.*;

import org.junit.Test;

public class CliUtilsTest {
	
	private String[] arr(String... arr) {
		return arr;
	}
	
	@Test
	public void parseEmptyString() {
		assertNull(parseCommand(""));
	}
	
	@Test
	public void parse1Arg() {
		assertArrayEquals(arr("a"), parseCommand("a"));
	}
	
	@Test
	public void parse2Args() {
		assertArrayEquals(arr("a", "b"), parseCommand("a b"));
	}
	
	@Test
	public void parse3Args() {
		assertArrayEquals(arr("a", "b", "cd"), parseCommand("a b cd"));
	}
	
	@Test
	public void parseArgsMultipleSpaces() {
		assertArrayEquals(arr("a", "b"), parseCommand("a   b"));
	}
	
	@Test
	public void parse1Quote() {
		assertArrayEquals(arr("a"), parseCommand("\"a\""));
	}
	
	@Test
	public void parse1QuoteWithSpaceInside() {
		assertArrayEquals(arr("a b c"), parseCommand("\"a b c\""));
	}
	
	@Test
	public void parse1QuoteWithSpaceInside2() {
		assertArrayEquals(arr("  a b  "), parseCommand("\"  a b  \""));
	}
	
	@Test
	public void parse1EmptyQuote() {
		assertArrayEquals(arr(""), parseCommand("\"\""));
	}
	
	@Test
	public void parse2Quote() {
		assertArrayEquals(arr("a", "b"), parseCommand("\"a\" \"b\""));
	}
	
	@Test
	public void parseQuoteArgQuote() {
		assertArrayEquals(arr("a", "c", "b"), parseCommand("\"a\" c \"b\""));
	}
	
	@Test
	public void parseArgQuoteArg() {
		assertArrayEquals(arr("d", "a", "c"), parseCommand("d \"a\" c"));
	}
	
	@Test
	public void parseQuoteMultipleSpaces() {
		assertArrayEquals(arr("a", "b"), parseCommand("\"a\"   \"b\""));
	}
	
}
