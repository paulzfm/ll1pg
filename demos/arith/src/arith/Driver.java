package arith;

import java.io.*;
import java.util.*;

import arith.Expr;
import arith.error.CompileError;
import arith.Lexer;
import arith.Parser;

/**
 * Main class of demo `arith`.
 *
 * Input: an arithmetic expression consist of `+`, `*` and numbers.
 * Output: the parsed AST and the evaluation result.
 */
public final class Driver {

	private static Driver driver;

	private Lexer lexer;

	private Parser parser;

	public static Driver getDriver() {
		return driver;
	}

	public void issueError(CompileError error) throws CompileError {
		throw error;
	}

	private void compile(InputStream in) throws Exception {
		lexer = new Lexer(in);
		parser = new Parser();
		lexer.setParser(parser);
		parser.setLexer(lexer);
		Expr tree = parser.parseFile();
		System.out.println(tree);
		System.out.println(tree.eval());
	}

	public static void main(String[] args) throws IOException {
		driver = new Driver();
		Console console = System.console();
		System.out.println("Arith - type expressions and enter to evaluate.");
		System.out.println("      - type 'q' to quit.");
		while (true) {
			String expr = console.readLine(">> ");
			if (expr.charAt(0) == 'q') {
				break;
			}

			InputStream in = new ByteArrayInputStream(expr.getBytes());
			try {
				driver.compile(in);
			} catch (Exception ex) {
				System.err.println(ex);
			}
		}
	}

}
