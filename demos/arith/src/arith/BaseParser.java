package arith;

import arith.Driver;
import arith.Expr;
import arith.error.CompileError;
import arith.error.MsgError;

import java.util.Arrays;

/**
 * Base class of `Parser`, generated by our tool.
 */
public abstract class BaseParser {
    private Lexer lexer;

    public void setLexer(Lexer lexer) {
        this.lexer = lexer;
    }

    /**
     * Custom function to handle syntax error when EOF or EOS expected, but `token` found.
     *
     * @param token actual token found.
     * @return an instance of error.
     */
    protected MsgError error(String token) {
        return new MsgError(lexer.getLocation(), "EOF is expected but " + token + " is " + "found");
    }

    /**
     * Custom function to handle syntax error when `expected` is expected but `token` is found.
     *
     * @param token    actual token found.
     * @param expected expected token.
     * @return an instance of error.
     */
    protected MsgError error(String token, String expected) {
        return new MsgError(lexer.getLocation(),
                expected + " is expected but " + token + " is " + "found");
    }

    /**
     * Custom function to handle syntax error when one of `acceptable` token is acceptable but
     * `token` is found.
     *
     * @param token      actual token found.
     * @param acceptable accepted tokens.
     * @return an instance of error.
     */
    protected MsgError error(String token, String[] acceptable) {
        return new MsgError(lexer.getLocation(),
                Arrays.toString(acceptable) + " are expected but " + token + " is " + "found");
    }

    /**
     * Custom function to communicate with lexer.
     *
     * @return token.
     * @throws Exception.
     */
    protected int lex() throws Exception {
        int token = lexer.yylex();
        lexer.handleError();
        return token;
    }

    /**
     * Parse function of our tool.
     *
     * @return value of start symbol if successfully parsed.
     * @throws Exception.
     */
    abstract protected SemValue parse() throws Exception;

    /**
     * Explict interface for calling the parser.
     *
     * @return the expression if successfully parsed.
     * @throws Exception.
     */
    public Expr parseFile() throws Exception {
        SemValue v = parse();
        return v.expr;
    }

}
