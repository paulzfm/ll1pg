/* This is auto-generated Parser source by LL1-Parser-Gen.
 * Generated at: Fri Jul 28 16:26:42 CST 2017
 * Please do NOT modify it unless you know what you are doing!
 *
 * Project repository: https://github.com/paulzfm/LL1-Parser-Gen
 * Author: Zhu Fengmin (Paul)
 */

package arith;

import arith.Expr;
import arith.Expr.*;
import arith.error.CompileError;
import java.util.*;

public class Parser extends BaseParser {
    public static final int eof = -1;
    public static final int eos = 0;
    public int lookahead = -1;
    public SemValue val = new SemValue();

    /* tokens */
    public static final int NUM = 257;

    /* search token name */
    String[] tokens = {
            "NUM",
    };

    private String name(int token) {
        if (token >= 0 && token <= 256) {
            return "'" + (char) token + "'";
        } else {
            return tokens[token - 257];
        }
    }

    public SemValue parse() throws CompileError {
        if (lookahead < 0) {
            lookahead = lex();
        }
        SemValue result = parseS();
        if (lookahead != eos && lookahead != eof) {
            throw error(name(lookahead));
        }
        return result;
    }

    public SemValue matchToken(int expected) throws CompileError {
        SemValue self = val;
        if (lookahead == expected) {
            lookahead = lex();
        } else {
            throw error(name(lookahead), name(expected));
        }
        return self;
    }

    /* parsers */
    private SemValue parseE1() throws CompileError {
        switch (lookahead) {
            case '+':
            {
                SemValue[] params = new SemValue[4];
                params[0] = new SemValue();
                params[1] = matchToken('+');
                params[2] = parseT();
                params[3] = parseE1();
                params[0].terms.add(new Term(Expr.ADD, params[2].expr));
                params[0].terms.addAll(params[3].terms);
                return params[0];
            }
            case ')':
            case eof:
            case eos:
            {
                SemValue[] params = new SemValue[1];
                params[0] = new SemValue();

                return params[0];
            }
            default:
            {
                String[] acc = {name('+'), name(')'), name('#')};
                throw error(name(lookahead), acc);
            }
        }
    }

    private SemValue parseF() throws CompileError {
        switch (lookahead) {
            case '(':
            {
                SemValue[] params = new SemValue[4];
                params[0] = new SemValue();
                params[1] = matchToken('(');
                params[2] = parseE();
                params[3] = matchToken(')');
                params[0].expr = params[2].expr;
                return params[0];
            }
            case NUM:
            {
                SemValue[] params = new SemValue[2];
                params[0] = new SemValue();
                params[1] = matchToken(NUM);
                params[0].expr = params[1].expr;
                return params[0];
            }
            default:
            {
                String[] acc = {name('('), name(NUM)};
                throw error(name(lookahead), acc);
            }
        }
    }

    private SemValue parseS() throws CompileError {
        switch (lookahead) {
            case NUM:
            case '(':
            {
                SemValue[] params = new SemValue[2];
                params[0] = new SemValue();
                params[1] = parseE();
                tree = params[1].expr;
                return params[0];
            }
            default:
            {
                String[] acc = {name(NUM), name('(')};
                throw error(name(lookahead), acc);
            }
        }
    }

    private SemValue parseT1() throws CompileError {
        switch (lookahead) {
            case '*':
            {
                SemValue[] params = new SemValue[4];
                params[0] = new SemValue();
                params[1] = matchToken('*');
                params[2] = parseF();
                params[3] = parseT1();
                params[0].terms.add(new Term(Expr.MUL, params[2].expr));
                params[0].terms.addAll(params[3].terms);
                return params[0];
            }
            case '+':
            case ')':
            case eof:
            case eos:
            {
                SemValue[] params = new SemValue[1];
                params[0] = new SemValue();

                return params[0];
            }
            default:
            {
                String[] acc = {name('*'), name('+'), name(')'), name('#')};
                throw error(name(lookahead), acc);
            }
        }
    }

    private SemValue parseE() throws CompileError {
        switch (lookahead) {
            case NUM:
            case '(':
            {
                SemValue[] params = new SemValue[3];
                params[0] = new SemValue();
                params[1] = parseT();
                params[2] = parseE1();
                params[0].expr = params[1].expr;
                for (Term t : params[2].terms) {
                    params[0].expr = new ArithExpr(t.op, params[0].expr, t.expr);
                }
                return params[0];
            }
            default:
            {
                String[] acc = {name(NUM), name('(')};
                throw error(name(lookahead), acc);
            }
        }
    }

    private SemValue parseT() throws CompileError {
        switch (lookahead) {
            case NUM:
            case '(':
            {
                SemValue[] params = new SemValue[3];
                params[0] = new SemValue();
                params[1] = parseF();
                params[2] = parseT1();
                params[0].expr = params[1].expr;
                for (Term t : params[2].terms) {
                    params[0].expr = new ArithExpr(t.op, params[0].expr, t.expr);
                }
                return params[0];
            }
            default:
            {
                String[] acc = {name(NUM), name('(')};
                throw error(name(lookahead), acc);
            }
        }
    }

}
/* end of file */
