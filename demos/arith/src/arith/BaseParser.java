package arith;

import arith.Driver;
import arith.Expr;
import arith.error.CompileError;
import arith.error.MsgError;
import java.util.Arrays;

public abstract class BaseParser {
	private Lexer lexer;

	protected Expr tree;

	public void setLexer(Lexer lexer) {
		this.lexer = lexer;
	}

	public Expr getTree() {
		return tree;
	}

	protected MsgError error(String token) {
		return new MsgError(lexer.getLocation(), token);
	}

	protected MsgError error(String token, String expected) {
		return new MsgError(lexer.getLocation(),
				expected + " is expected but " + token + " is " + "found");
	}

	protected MsgError error(String token, String[] acceptable) {
		return new MsgError(lexer.getLocation(),
				Arrays.toString(acceptable) + " are expected but " + token + " is " + "found");
	}

	protected int lex() throws Exception {
		int token = lexer.yylex();
		lexer.handleError();
		return token;
	}

	abstract protected SemValue parse() throws CompileError;

	public Expr parseFile() throws CompileError {
		parse();
		return tree;
	}

}
