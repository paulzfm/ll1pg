package arith;

import arith.Driver;
import arith.Expr;
import arith.error.CompileError;
import arith.error.MsgError;

public abstract class BaseParser {
	private Lexer lexer;

	protected Expr tree;

	public void setLexer(Lexer lexer) {
		this.lexer = lexer;
	}

	public Expr getTree() {
		return tree;
	}

	protected void issueError(CompileError error) {
		Driver.getDriver().issueError(error);
	}

	void yyerror(String msg) {
		Driver.getDriver().issueError(
				new MsgError(lexer.getLocation(), msg));
	}

	int yylex() {
		int token = -1;
		try {
			token = lexer.yylex();
		} catch (Exception e) {
			yyerror("lexer error: " + e.getMessage());
		}

		return token;
	}

	abstract int yyparse();

	public Expr parseFile() {
		yyparse();
		return tree;
	}

}
