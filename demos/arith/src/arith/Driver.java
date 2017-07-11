package arith;

import java.io.*;
import java.util.*;

import arith.Expr;
import arith.error.CompileError;
import arith.Lexer;
import arith.Parser;

public final class Driver {

	private static Driver driver;

	private List<CompileError> errors;

	private Lexer lexer;

	private Parser parser;

	public static Driver getDriver() {
		return driver;
	}

	public void issueError(CompileError error) {
		errors.add(error);
	}

	public void checkPoint() {
		if (errors.size() > 0) {
			Collections.sort(errors, new Comparator<CompileError>() {

				public int compare(CompileError o1, CompileError o2) {
					return o1.getLocation().compareTo(o2.getLocation());
				}

			});
			for (CompileError error : errors) {
				System.err.println(error);
			}
		}
	}

	private void compile(InputStream in) {
		lexer = new Lexer(in);
		parser = new Parser();
		lexer.setParser(parser);
		parser.setLexer(lexer);
		errors = new ArrayList<CompileError>();
		Expr tree = parser.parseFile();
		checkPoint();
		System.out.println(tree);
		System.out.println(tree.eval());
	}

	public static void main(String[] args) throws IOException {
		driver = new Driver();
		Console console = System.console();
		System.out.println("Arith - type expressions and enter to evaluate it.");
		while (true) {
			String expr = console.readLine(">> ");
			InputStream in = new ByteArrayInputStream(expr.getBytes());
			driver.compile(in);
		}
	}

}
