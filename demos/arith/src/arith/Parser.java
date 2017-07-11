/* This is auto-generated Parser source by LL1-Parser-Gen.
 * Generated at: Tue Jul 11 18:26:29 CST 2017
 * Please do NOT modify it unless you know what you are doing!
 *
 * Project repository: https://github.com/paulzfm/LL1-Parser-Gen
 * Author: Zhu Fengmin (Paul)
 */

package arith;

import arith.Expr;
import arith.Expr.*;
import java.util.*;

public class Parser extends BaseParser {
    public static final int YYEOF = -1;
    public static final int YYEOS = 0;
    public int lookahead = -1;
    public SemValue yylval = new SemValue();

    /* tokens */
    public static final int NUM = 257;

    /* search token name */
    String[] tokens = {
            "NUM",
    };

    private String name(int token) {
        if (token >= 0 && token <= 256) {
            return (char) token + "";
        } else {
            return tokens[token - 257];
        }
    }

    public int yyparse() {
        if (lookahead < 0) {
            lookahead = yylex();
        }
        ParseS();
        if (lookahead != YYEOS && lookahead != YYEOF) {
            throw new RuntimeException(String.format("\n***Syntax Error: Invalid tokens after:(%d)%s at %s", lookahead, name(lookahead), yylval.loc));
        }
        return 0;
    }

    public SemValue MatchToken(int expected) {
        SemValue self = yylval;
        if (lookahead == expected) {
            lookahead = yylex(); // get next token
        } else {
            throw new RuntimeException(String.format("\n***Syntax Error: Expecting:(%d)%s at %s, but [%s] given", expected, name(expected), yylval.loc, name(lookahead)));
        }
        return self;
    }

    /* parsers */
    private SemValue ParseE1() {
        switch (lookahead) {
            case '+':
            {
                SemValue[] params = new SemValue[4];
                params[0] = new SemValue();
                params[1] = MatchToken('+');
                params[2] = ParseT();
                params[3] = ParseE1();
                params[0].terms.add(new Term(Expr.ADD, params[2].expr));
                params[0].terms.addAll(params[3].terms);
                return params[0];
            }
            case ')':
            case YYEOF:
            case YYEOS:
            {
                SemValue[] params = new SemValue[1];
                params[0] = new SemValue();

                return params[0];
            }
            default:
                throw new RuntimeException("invalid lookahead: " + lookahead);
        }
    }

    private SemValue ParseF() {
        switch (lookahead) {
            case '(':
            {
                SemValue[] params = new SemValue[4];
                params[0] = new SemValue();
                params[1] = MatchToken('(');
                params[2] = ParseE();
                params[3] = MatchToken(')');
                params[0].expr = params[2].expr;
                return params[0];
            }
            case NUM:
            {
                SemValue[] params = new SemValue[2];
                params[0] = new SemValue();
                params[1] = MatchToken(NUM);
                params[0].expr = params[1].expr;
                return params[0];
            }
            default:
                throw new RuntimeException("invalid lookahead: " + lookahead);
        }
    }

    private SemValue ParseS() {
        switch (lookahead) {
            case NUM:
            case '(':
            {
                SemValue[] params = new SemValue[2];
                params[0] = new SemValue();
                params[1] = ParseE();
                tree = params[1].expr;
                return params[0];
            }
            default:
                throw new RuntimeException("invalid lookahead: " + lookahead);
        }
    }

    private SemValue ParseT1() {
        switch (lookahead) {
            case '*':
            {
                SemValue[] params = new SemValue[4];
                params[0] = new SemValue();
                params[1] = MatchToken('*');
                params[2] = ParseF();
                params[3] = ParseT1();
                params[0].terms.add(new Term(Expr.MUL, params[2].expr));
                params[0].terms.addAll(params[3].terms);
                return params[0];
            }
            case '+':
            case ')':
            case YYEOF:
            case YYEOS:
            {
                SemValue[] params = new SemValue[1];
                params[0] = new SemValue();

                return params[0];
            }
            default:
                throw new RuntimeException("invalid lookahead: " + lookahead);
        }
    }

    private SemValue ParseE() {
        switch (lookahead) {
            case NUM:
            case '(':
            {
                SemValue[] params = new SemValue[3];
                params[0] = new SemValue();
                params[1] = ParseT();
                params[2] = ParseE1();
                params[0].expr = params[1].expr;
                for (Term t : params[2].terms) {
                    params[0].expr = new ArithExpr(t.op, params[0].expr, t.expr);
                }
                return params[0];
            }
            default:
                throw new RuntimeException("invalid lookahead: " + lookahead);
        }
    }

    private SemValue ParseT() {
        switch (lookahead) {
            case NUM:
            case '(':
            {
                SemValue[] params = new SemValue[3];
                params[0] = new SemValue();
                params[1] = ParseF();
                params[2] = ParseT1();
                params[0].expr = params[1].expr;
                for (Term t : params[2].terms) {
                    params[0].expr = new ArithExpr(t.op, params[0].expr, t.expr);
                }
                return params[0];
            }
            default:
                throw new RuntimeException("invalid lookahead: " + lookahead);
        }
    }

}
/* end of file */
