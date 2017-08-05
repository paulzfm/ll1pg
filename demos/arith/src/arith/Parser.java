/* This is auto-generated Parser source by LL1-Parser-Gen.
 * Generated at: Sat Aug 05 22:19:22 CST 2017
 * Please do NOT modify it!
 *
 * Project repository: https://github.com/paulzfm/LL1-Parser-Gen
 * Author: Zhu Fengmin (Paul)
 */

package arith;

import arith.Expr;
import arith.Expr.*;
import arith.error.CompileError;
import java.util.*;

public class Parser extends BaseParser
 {
    public static final int eof = -1;
    public static final int eos = 0;
    public int lookahead = -1;
    public SemValue val = new SemValue();
    
    /* tokens */
    public static final int NUM = 257; //# line 25
    
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
    
    public SemValue parse() throws Exception {
        if (lookahead < 0) {
            lookahead = lex();
        }
        SemValue result = parseE();
        if (lookahead != eos && lookahead != eof) {
            throw error(name(lookahead));
        }
        return result;
    }
    
    public SemValue matchToken(int expected) throws Exception {
        SemValue self = val;
        if (lookahead == expected) {
            lookahead = lex();
        } else {
            throw error(name(lookahead), name(expected));
        }
        return self;
    }
    
    /* parsers */
    //# line 38
    private SemValue parseE1() throws Exception {
        switch (lookahead) {
            case '+':
            {
                SemValue[] params = new SemValue[4];
                params[0] = new SemValue();
                params[1] = matchToken('+');
                params[2] = parseT();
                params[3] = parseE1();
                //# line 39
                params[0].terms.add(new Term(Expr.ADD, params[2].expr));
                params[0].terms.addAll(params[3].terms);
                return params[0];
            }
            case eof:
            case eos:
            case ')':
            {
                SemValue[] params = new SemValue[1];
                params[0] = new SemValue();
                /* no action */
                return params[0];
            }
            default:
            {
                String[] acc = {name('+'), name('#'), name(')')};
                throw error(name(lookahead), acc);
            }
        }
    }
    
    //# line 63
    private SemValue parseF() throws Exception {
        switch (lookahead) {
            case '(':
            {
                SemValue[] params = new SemValue[4];
                params[0] = new SemValue();
                params[1] = matchToken('(');
                params[2] = parseE();
                params[3] = matchToken(')');
                //# line 64
                params[0].expr = params[2].expr;
                return params[0];
            }
            case NUM:
            {
                SemValue[] params = new SemValue[2];
                params[0] = new SemValue();
                params[1] = matchToken(NUM);
                //# line 68
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
    
    //# line 55
    private SemValue parseT1() throws Exception {
        switch (lookahead) {
            case '*':
            {
                SemValue[] params = new SemValue[4];
                params[0] = new SemValue();
                params[1] = matchToken('*');
                params[2] = parseF();
                params[3] = parseT1();
                //# line 56
                params[0].terms.add(new Term(Expr.MUL, params[2].expr));
                params[0].terms.addAll(params[3].terms);
                return params[0];
            }
            case '+':
            case eof:
            case eos:
            case ')':
            {
                SemValue[] params = new SemValue[1];
                params[0] = new SemValue();
                /* no action */
                return params[0];
            }
            default:
            {
                String[] acc = {name('*'), name('+'), name('#'), name(')')};
                throw error(name(lookahead), acc);
            }
        }
    }
    
    //# line 29
    private SemValue parseE() throws Exception {
        switch (lookahead) {
            case NUM:
            case '(':
            {
                SemValue[] params = new SemValue[3];
                params[0] = new SemValue();
                params[1] = parseT();
                params[2] = parseE1();
                //# line 30
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
    
    //# line 46
    private SemValue parseT() throws Exception {
        switch (lookahead) {
            case NUM:
            case '(':
            {
                SemValue[] params = new SemValue[3];
                params[0] = new SemValue();
                params[1] = parseF();
                params[2] = parseT1();
                //# line 47
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
