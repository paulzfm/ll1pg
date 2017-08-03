/* This is auto-generated Parser source by LL1-Parser-Gen.
 * Generated at: Thu Aug 03 17:34:11 CST 2017
 * Please do NOT modify it unless you know what you are doing!
 *
 * Project repository: https://github.com/paulzfm/LL1-Parser-Gen
 * Author: Zhu Fengmin (Paul)
 */

package decaf;

import decaf.Location;
import decaf.Tree;
import decaf.Tree.*;
import java.util.*;

public class Parser extends BaseParser {
    public static final int eof = -1;
    public static final int eos = 0;
    public int lookahead = -1;
    public SemValue val = new SemValue();

    /* tokens */
    public static final int VOID = 257;
    public static final int BOOL = 258;
    public static final int INT = 259;
    public static final int STRING = 260;
    public static final int CLASS = 261;
    public static final int NULL = 262;
    public static final int EXTENDS = 263;
    public static final int THIS = 264;
    public static final int WHILE = 265;
    public static final int FOR = 266;
    public static final int IF = 267;
    public static final int ELSE = 268;
    public static final int RETURN = 269;
    public static final int BREAK = 270;
    public static final int NEW = 271;
    public static final int PRINT = 272;
    public static final int READ_INTEGER = 273;
    public static final int READ_LINE = 274;
    public static final int LITERAL = 275;
    public static final int IDENTIFIER = 276;
    public static final int AND = 277;
    public static final int OR = 278;
    public static final int STATIC = 279;
    public static final int INSTANCEOF = 280;
    public static final int LESS_EQUAL = 281;
    public static final int GREATER_EQUAL = 282;
    public static final int EQUAL = 283;
    public static final int NOT_EQUAL = 284;

    /* search token name */
    String[] tokens = {
            "VOID", "BOOL", "INT", "STRING", "CLASS",
            "NULL", "EXTENDS", "THIS", "WHILE", "FOR",
            "IF", "ELSE", "RETURN", "BREAK", "NEW",
            "PRINT", "READ_INTEGER", "READ_LINE", "LITERAL", "IDENTIFIER",
            "AND", "OR", "STATIC", "INSTANCEOF", "LESS_EQUAL",
            "GREATER_EQUAL", "EQUAL", "NOT_EQUAL",
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
        SemValue result = parseProgram();
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
    private SemValue parseVariableDef() throws Exception {
        switch (lookahead) {
            case VOID:
            case CLASS:
            case INT:
            case STRING:
            case BOOL:
            {
                SemValue[] params = new SemValue[3];
                params[0] = new SemValue();
                params[1] = parseVariable();
                params[2] = matchToken(';');
                params[0].vdef = params[1].vdef;
                return params[0];
            }
            default:
            {
                String[] acc = {name(VOID), name(CLASS), name(INT), name(STRING), name(BOOL)};
                throw error(name(lookahead), acc);
            }
        }
    }

    private SemValue parseExprT5() throws Exception {
        switch (lookahead) {
            case '+':
            case '-':
            {
                SemValue[] params = new SemValue[4];
                params[0] = new SemValue();
                params[1] = parseOper5();
                params[2] = parseExpr6();
                params[3] = parseExprT5();
                params[0].svec = new Vector<Integer>();
                params[0].lvec = new Vector<Location>();
                params[0].evec = new Vector<Expr>();
                params[0].svec.add(params[1].counter);
                params[0].lvec.add(params[1].loc);
                params[0].evec.add(params[2].expr);
                if (params[3].svec != null) {
                    params[0].svec.addAll(params[3].svec);
                    params[0].lvec.addAll(params[3].lvec);
                    params[0].evec.addAll(params[3].evec);
                }
                return params[0];
            }
            case LESS_EQUAL:
            case ']':
            case GREATER_EQUAL:
            case EQUAL:
            case ')':
            case NOT_EQUAL:
            case ',':
            case '=':
            case OR:
            case AND:
            case ';':
            case '<':
            case '>':
            {
                SemValue[] params = new SemValue[1];
                params[0] = new SemValue();

                return params[0];
            }
            default:
            {
                String[] acc = {name('+'), name('-'), name(LESS_EQUAL), name(']'), name(GREATER_EQUAL), name(EQUAL), name(')'), name(NOT_EQUAL), name(','), name('='), name(OR), name(AND), name(';'), name('<'), name('>')};
                throw error(name(lookahead), acc);
            }
        }
    }

    private SemValue parseOper3() throws Exception {
        switch (lookahead) {
            case NOT_EQUAL:
            {
                SemValue[] params = new SemValue[2];
                params[0] = new SemValue();
                params[1] = matchToken(NOT_EQUAL);
                params[0].counter = Tree.NE;
                params[0].loc = params[1].loc;
                return params[0];
            }
            case EQUAL:
            {
                SemValue[] params = new SemValue[2];
                params[0] = new SemValue();
                params[1] = matchToken(EQUAL);
                params[0].counter = Tree.EQ;
                params[0].loc = params[1].loc;
                return params[0];
            }
            default:
            {
                String[] acc = {name(NOT_EQUAL), name(EQUAL)};
                throw error(name(lookahead), acc);
            }
        }
    }

    private SemValue parseOper6() throws Exception {
        switch (lookahead) {
            case '*':
            {
                SemValue[] params = new SemValue[2];
                params[0] = new SemValue();
                params[1] = matchToken('*');
                params[0].counter = Tree.MUL;
                params[0].loc = params[1].loc;
                return params[0];
            }
            case '/':
            {
                SemValue[] params = new SemValue[2];
                params[0] = new SemValue();
                params[1] = matchToken('/');
                params[0].counter = Tree.DIV;
                params[0].loc = params[1].loc;
                return params[0];
            }
            case '%':
            {
                SemValue[] params = new SemValue[2];
                params[0] = new SemValue();
                params[1] = matchToken('%');
                params[0].counter = Tree.MOD;
                params[0].loc = params[1].loc;
                return params[0];
            }
            default:
            {
                String[] acc = {name('*'), name('/'), name('%')};
                throw error(name(lookahead), acc);
            }
        }
    }

    private SemValue parseVariableList() throws Exception {
        switch (lookahead) {
            case VOID:
            case CLASS:
            case INT:
            case STRING:
            case BOOL:
            {
                SemValue[] params = new SemValue[3];
                params[0] = new SemValue();
                params[1] = parseVariable();
                params[2] = parseSubVariableList();
                params[0].vlist = new ArrayList<VarDef>();
                params[0].vlist.add(params[1].vdef);
                if (params[2].vlist != null) {
                    params[0].vlist.addAll(params[2].vlist);
                }
                return params[0];
            }
            default:
            {
                String[] acc = {name(VOID), name(CLASS), name(INT), name(STRING), name(BOOL)};
                throw error(name(lookahead), acc);
            }
        }
    }

    private SemValue parseFormals() throws Exception {
        switch (lookahead) {
            case VOID:
            case CLASS:
            case INT:
            case STRING:
            case BOOL:
            {
                SemValue[] params = new SemValue[2];
                params[0] = new SemValue();
                params[1] = parseVariableList();
                params[0].vlist = params[1].vlist;
                return params[0];
            }
            case ')':
            {
                SemValue[] params = new SemValue[1];
                params[0] = new SemValue();
                params[0].vlist = new ArrayList<VarDef>();
                return params[0];
            }
            default:
            {
                String[] acc = {name(VOID), name(CLASS), name(INT), name(STRING), name(BOOL), name(')')};
                throw error(name(lookahead), acc);
            }
        }
    }

    private SemValue parseOper7() throws Exception {
        switch (lookahead) {
            case '!':
            {
                SemValue[] params = new SemValue[2];
                params[0] = new SemValue();
                params[1] = matchToken('!');
                params[0].counter = Tree.NOT;
                params[0].loc = params[1].loc;
                return params[0];
            }
            case '-':
            {
                SemValue[] params = new SemValue[2];
                params[0] = new SemValue();
                params[1] = matchToken('-');
                params[0].counter = Tree.NEG;
                params[0].loc = params[1].loc;
                return params[0];
            }
            default:
            {
                String[] acc = {name('!'), name('-')};
                throw error(name(lookahead), acc);
            }
        }
    }

    private SemValue parseExpr8() throws Exception {
        switch (lookahead) {
            case READ_LINE:
            case NULL:
            case IDENTIFIER:
            case NEW:
            case THIS:
            case INSTANCEOF:
            case LITERAL:
            case '(':
            case READ_INTEGER:
            {
                SemValue[] params = new SemValue[3];
                params[0] = new SemValue();
                params[1] = parseExpr9();
                params[2] = parseExprT8();
                params[0].expr = params[1].expr;
                if (params[2].svec != null) {
                    for (int i = 0; i < params[2].svec.size(); ++i) {
                        params[0].expr = new Tree.Binary(params[2].svec.get(i), params[0].expr,
                                params[2].evec.get(i), params[2].lvec.get(i));
                    }
                }
                return params[0];
            }
            default:
            {
                String[] acc = {name(READ_LINE), name(NULL), name(IDENTIFIER), name(NEW), name(THIS), name(INSTANCEOF), name(LITERAL), name('('), name(READ_INTEGER)};
                throw error(name(lookahead), acc);
            }
        }
    }

    private SemValue parseAfterSimpleTypeExpr() throws Exception {
        switch (lookahead) {
            case '!':
            case '-':
            case READ_LINE:
            case NULL:
            case IDENTIFIER:
            case NEW:
            case THIS:
            case INSTANCEOF:
            case LITERAL:
            case '(':
            case READ_INTEGER:
            {
                SemValue[] params = new SemValue[3];
                params[0] = new SemValue();
                params[1] = parseExpr();
                params[2] = matchToken(']');
                params[0].expr = params[1].expr;
                params[0].counter = 0;
                return params[0];
            }
            case ']':
            {
                SemValue[] params = new SemValue[4];
                params[0] = new SemValue();
                params[1] = matchToken(']');
                params[2] = matchToken('[');
                params[3] = parseAfterSimpleTypeExpr();
                params[0].expr = params[3].expr;
                params[0].counter = 1 + params[3].counter;
                return params[0];
            }
            default:
            {
                String[] acc = {name('!'), name('-'), name(READ_LINE), name(NULL), name(IDENTIFIER), name(NEW), name(THIS), name(INSTANCEOF), name(LITERAL), name('('), name(READ_INTEGER), name(']')};
                throw error(name(lookahead), acc);
            }
        }
    }

    private SemValue parseExpr2() throws Exception {
        switch (lookahead) {
            case '!':
            case '-':
            case READ_LINE:
            case NULL:
            case IDENTIFIER:
            case NEW:
            case THIS:
            case INSTANCEOF:
            case LITERAL:
            case '(':
            case READ_INTEGER:
            {
                SemValue[] params = new SemValue[3];
                params[0] = new SemValue();
                params[1] = parseExpr3();
                params[2] = parseExprT2();
                params[0].expr = params[1].expr;
                if (params[2].svec != null) {
                    for (int i = 0; i < params[2].svec.size(); ++i) {
                        params[0].expr = new Tree.Binary(params[2].svec.get(i), params[0].expr,
                                params[2].evec.get(i), params[2].lvec.get(i));
                    }
                }
                return params[0];
            }
            default:
            {
                String[] acc = {name('!'), name('-'), name(READ_LINE), name(NULL), name(IDENTIFIER), name(NEW), name(THIS), name(INSTANCEOF), name(LITERAL), name('('), name(READ_INTEGER)};
                throw error(name(lookahead), acc);
            }
        }
    }

    private SemValue parseOper2() throws Exception {
        switch (lookahead) {
            case AND:
            {
                SemValue[] params = new SemValue[2];
                params[0] = new SemValue();
                params[1] = matchToken(AND);
                params[0].counter = Tree.AND;
                params[0].loc = params[1].loc;
                return params[0];
            }
            default:
            {
                String[] acc = {name(AND)};
                throw error(name(lookahead), acc);
            }
        }
    }

    private SemValue parseExpr6() throws Exception {
        switch (lookahead) {
            case '!':
            case '-':
            case READ_LINE:
            case NULL:
            case IDENTIFIER:
            case NEW:
            case THIS:
            case INSTANCEOF:
            case LITERAL:
            case '(':
            case READ_INTEGER:
            {
                SemValue[] params = new SemValue[3];
                params[0] = new SemValue();
                params[1] = parseExpr7();
                params[2] = parseExprT6();
                params[0].expr = params[1].expr;
                if (params[2].svec != null) {
                    for (int i = 0; i < params[2].svec.size(); ++i) {
                        params[0].expr = new Tree.Binary(params[2].svec.get(i), params[0].expr,
                                params[2].evec.get(i), params[2].lvec.get(i));
                    }
                }
                return params[0];
            }
            default:
            {
                String[] acc = {name('!'), name('-'), name(READ_LINE), name(NULL), name(IDENTIFIER), name(NEW), name(THIS), name(INSTANCEOF), name(LITERAL), name('('), name(READ_INTEGER)};
                throw error(name(lookahead), acc);
            }
        }
    }

    private SemValue parseBreakStmt() throws Exception {
        switch (lookahead) {
            case BREAK:
            {
                SemValue[] params = new SemValue[2];
                params[0] = new SemValue();
                params[1] = matchToken(BREAK);
                params[0].stmt = new Tree.Break(params[1].loc);
                return params[0];
            }
            default:
            {
                String[] acc = {name(BREAK)};
                throw error(name(lookahead), acc);
            }
        }
    }

    private SemValue parseExprT2() throws Exception {
        switch (lookahead) {
            case AND:
            {
                SemValue[] params = new SemValue[4];
                params[0] = new SemValue();
                params[1] = parseOper2();
                params[2] = parseExpr3();
                params[3] = parseExprT2();
                params[0].svec = new Vector<Integer>();
                params[0].lvec = new Vector<Location>();
                params[0].evec = new Vector<Expr>();
                params[0].svec.add(params[1].counter);
                params[0].lvec.add(params[1].loc);
                params[0].evec.add(params[2].expr);
                if (params[3].svec != null) {
                    params[0].svec.addAll(params[3].svec);
                    params[0].lvec.addAll(params[3].lvec);
                    params[0].evec.addAll(params[3].evec);
                }
                return params[0];
            }
            case ']':
            case ')':
            case ',':
            case '=':
            case OR:
            case ';':
            {
                SemValue[] params = new SemValue[1];
                params[0] = new SemValue();

                return params[0];
            }
            default:
            {
                String[] acc = {name(AND), name(']'), name(')'), name(','), name('='), name(OR), name(';')};
                throw error(name(lookahead), acc);
            }
        }
    }

    private SemValue parseStmtList() throws Exception {
        switch (lookahead) {
            case PRINT:
            case VOID:
            case FOR:
            case '!':
            case '-':
            case CLASS:
            case READ_LINE:
            case WHILE:
            case RETURN:
            case NULL:
            case INT:
            case IDENTIFIER:
            case NEW:
            case IF:
            case THIS:
            case INSTANCEOF:
            case STRING:
            case LITERAL:
            case '(':
            case ';':
            case BOOL:
            case BREAK:
            case READ_INTEGER:
            case '{':
            {
                SemValue[] params = new SemValue[3];
                params[0] = new SemValue();
                params[1] = parseStmt();
                params[2] = parseStmtList();
                params[0].slist.add(params[1].stmt);
                params[0].slist.addAll(params[2].slist);
                return params[0];
            }
            case '}':
            {
                SemValue[] params = new SemValue[1];
                params[0] = new SemValue();

                return params[0];
            }
            default:
            {
                String[] acc = {name(PRINT), name(VOID), name(FOR), name('!'), name('-'), name(CLASS), name(READ_LINE), name(WHILE), name(RETURN), name(NULL), name(INT), name(IDENTIFIER), name(NEW), name(IF), name(THIS), name(INSTANCEOF), name(STRING), name(LITERAL), name('('), name(';'), name(BOOL), name(BREAK), name(READ_INTEGER), name('{'), name('}')};
                throw error(name(lookahead), acc);
            }
        }
    }

    private SemValue parseConstant() throws Exception {
        switch (lookahead) {
            case NULL:
            {
                SemValue[] params = new SemValue[2];
                params[0] = new SemValue();
                params[1] = matchToken(NULL);
                params[0].expr = new Null(params[1].loc);
                return params[0];
            }
            case LITERAL:
            {
                SemValue[] params = new SemValue[2];
                params[0] = new SemValue();
                params[1] = matchToken(LITERAL);
                params[0].expr = new Tree.Literal(params[1].typeTag, params[1].literal, params[1].loc);
                return params[0];
            }
            default:
            {
                String[] acc = {name(NULL), name(LITERAL)};
                throw error(name(lookahead), acc);
            }
        }
    }

    private SemValue parseSubVariableList() throws Exception {
        switch (lookahead) {
            case ',':
            {
                SemValue[] params = new SemValue[4];
                params[0] = new SemValue();
                params[1] = matchToken(',');
                params[2] = parseVariable();
                params[3] = parseSubVariableList();
                params[0].vlist = new ArrayList<VarDef>();
                params[0].vlist.add(params[2].vdef);
                if (params[3].vlist != null) {
                    params[0].vlist.addAll(params[3].vlist);
                }
                return params[0];
            }
            case ')':
            {
                SemValue[] params = new SemValue[1];
                params[0] = new SemValue();

                return params[0];
            }
            default:
            {
                String[] acc = {name(','), name(')')};
                throw error(name(lookahead), acc);
            }
        }
    }

    private SemValue parsePrintStmt() throws Exception {
        switch (lookahead) {
            case PRINT:
            {
                SemValue[] params = new SemValue[5];
                params[0] = new SemValue();
                params[1] = matchToken(PRINT);
                params[2] = matchToken('(');
                params[3] = parseExprList();
                params[4] = matchToken(')');
                params[0].stmt = new Tree.Print(params[3].elist, params[1].loc);
                return params[0];
            }
            default:
            {
                String[] acc = {name(PRINT)};
                throw error(name(lookahead), acc);
            }
        }
    }

    private SemValue parseForStmt() throws Exception {
        switch (lookahead) {
            case FOR:
            {
                SemValue[] params = new SemValue[10];
                params[0] = new SemValue();
                params[1] = matchToken(FOR);
                params[2] = matchToken('(');
                params[3] = parseSimpleStmt();
                params[4] = matchToken(';');
                params[5] = parseExpr();
                params[6] = matchToken(';');
                params[7] = parseSimpleStmt();
                params[8] = matchToken(')');
                params[9] = parseStmt();
                params[0].stmt = new Tree.ForLoop(params[3].stmt, params[5].expr, params[7].stmt, params[9].stmt, params[1].loc);
                return params[0];
            }
            default:
            {
                String[] acc = {name(FOR)};
                throw error(name(lookahead), acc);
            }
        }
    }

    private SemValue parseExpr9() throws Exception {
        switch (lookahead) {
            case LITERAL:
            case NULL:
            {
                SemValue[] params = new SemValue[2];
                params[0] = new SemValue();
                params[1] = parseConstant();
                params[0].expr = params[1].expr;
                return params[0];
            }
            case INSTANCEOF:
            {
                SemValue[] params = new SemValue[7];
                params[0] = new SemValue();
                params[1] = matchToken(INSTANCEOF);
                params[2] = matchToken('(');
                params[3] = parseExpr();
                params[4] = matchToken(',');
                params[5] = matchToken(IDENTIFIER);
                params[6] = matchToken(')');
                params[0].expr = new Tree.TypeTest(params[3].expr, params[5].ident, params[1].loc);
                return params[0];
            }
            case IDENTIFIER:
            {
                SemValue[] params = new SemValue[3];
                params[0] = new SemValue();
                params[1] = matchToken(IDENTIFIER);
                params[2] = parseAfterIdentExpr();
                if (params[2].elist != null) {
                    params[0].expr = new Tree.CallExpr(null, params[1].ident, params[2].elist, params[1].loc);
                } else {
                    params[0].expr = new Tree.Ident(null, params[1].ident, params[1].loc);
                }
                return params[0];
            }
            case THIS:
            {
                SemValue[] params = new SemValue[2];
                params[0] = new SemValue();
                params[1] = matchToken(THIS);
                params[0].expr = new Tree.ThisExpr(params[1].loc);
                return params[0];
            }
            case READ_INTEGER:
            {
                SemValue[] params = new SemValue[4];
                params[0] = new SemValue();
                params[1] = matchToken(READ_INTEGER);
                params[2] = matchToken('(');
                params[3] = matchToken(')');
                params[0].expr = new Tree.ReadIntExpr(params[1].loc);
                return params[0];
            }
            case NEW:
            {
                SemValue[] params = new SemValue[3];
                params[0] = new SemValue();
                params[1] = matchToken(NEW);
                params[2] = parseAfterNewExpr();
                if (params[2].ident != null) {
                    params[0].expr = new Tree.NewClass(params[2].ident, params[1].loc);
                } else {
                    params[0].expr = new Tree.NewArray(params[2].type, params[2].expr, params[1].loc);
                }
                return params[0];
            }
            case '(':
            {
                SemValue[] params = new SemValue[3];
                params[0] = new SemValue();
                params[1] = matchToken('(');
                params[2] = parseAfterParenExpr();
                params[0].expr = params[2].expr;
                return params[0];
            }
            case READ_LINE:
            {
                SemValue[] params = new SemValue[4];
                params[0] = new SemValue();
                params[1] = matchToken(READ_LINE);
                params[2] = matchToken('(');
                params[3] = matchToken(')');
                params[0].expr = new Tree.ReadLineExpr(params[1].loc);
                return params[0];
            }
            default:
            {
                String[] acc = {name(LITERAL), name(NULL), name(INSTANCEOF), name(IDENTIFIER), name(THIS), name(READ_INTEGER), name(NEW), name('('), name(READ_LINE)};
                throw error(name(lookahead), acc);
            }
        }
    }

    private SemValue parseExpr1() throws Exception {
        switch (lookahead) {
            case '!':
            case '-':
            case READ_LINE:
            case NULL:
            case IDENTIFIER:
            case NEW:
            case THIS:
            case INSTANCEOF:
            case LITERAL:
            case '(':
            case READ_INTEGER:
            {
                SemValue[] params = new SemValue[3];
                params[0] = new SemValue();
                params[1] = parseExpr2();
                params[2] = parseExprT1();
                params[0].expr = params[1].expr;
                if (params[2].svec != null) {
                    for (int i = 0; i < params[2].svec.size(); ++i) {
                        params[0].expr = new Tree.Binary(params[2].svec.get(i), params[0].expr,
                                params[2].evec.get(i), params[2].lvec.get(i));
                    }
                }
                return params[0];
            }
            default:
            {
                String[] acc = {name('!'), name('-'), name(READ_LINE), name(NULL), name(IDENTIFIER), name(NEW), name(THIS), name(INSTANCEOF), name(LITERAL), name('('), name(READ_INTEGER)};
                throw error(name(lookahead), acc);
            }
        }
    }

    private SemValue parseOper1() throws Exception {
        switch (lookahead) {
            case OR:
            {
                SemValue[] params = new SemValue[2];
                params[0] = new SemValue();
                params[1] = matchToken(OR);
                params[0].counter = Tree.OR;
                params[0].loc = params[1].loc;
                return params[0];
            }
            default:
            {
                String[] acc = {name(OR)};
                throw error(name(lookahead), acc);
            }
        }
    }

    private SemValue parseElseClause() throws Exception {
        switch (lookahead) {
            case ELSE:
            {
                SemValue[] params = new SemValue[3];
                params[0] = new SemValue();
                params[1] = matchToken(ELSE);
                params[2] = parseStmt();
                params[0].stmt = params[2].stmt;
                return params[0];
            }
            case PRINT:
            case VOID:
            case FOR:
            case '!':
            case '-':
            case CLASS:
            case READ_LINE:
            case WHILE:
            case RETURN:
            case NULL:
            case INT:
            case '}':
            case IDENTIFIER:
            case NEW:
            case IF:
            case THIS:
            case INSTANCEOF:
            case STRING:
            case LITERAL:
            case '(':
            case ';':
            case BOOL:
            case BREAK:
            case READ_INTEGER:
            case '{':
            {
                SemValue[] params = new SemValue[1];
                params[0] = new SemValue();

                return params[0];
            }
            default:
            {
                String[] acc = {name(ELSE), name(PRINT), name(VOID), name(FOR), name('!'), name('-'), name(CLASS), name(READ_LINE), name(WHILE), name(RETURN), name(NULL), name(INT), name('}'), name(IDENTIFIER), name(NEW), name(IF), name(THIS), name(INSTANCEOF), name(STRING), name(LITERAL), name(ELSE), name('('), name(';'), name(BOOL), name(BREAK), name(READ_INTEGER), name('{')};
                throw error(name(lookahead), acc);
            }
        }
    }

    private SemValue parseFieldList() throws Exception {
        switch (lookahead) {
            case '}':
            {
                SemValue[] params = new SemValue[1];
                params[0] = new SemValue();
                params[0].flist = new ArrayList<Tree>();
                return params[0];
            }
            case VOID:
            case CLASS:
            case INT:
            case STRING:
            case STATIC:
            case BOOL:
            {
                SemValue[] params = new SemValue[3];
                params[0] = new SemValue();
                params[1] = parseField();
                params[2] = parseFieldList();
                params[0].flist = new ArrayList<Tree>();
                if (params[1].vdef != null) {
                    params[0].flist.add(params[1].vdef);
                } else {
                    params[0].flist.add(params[1].fdef);
                }
                params[0].flist.addAll(params[2].flist);
                return params[0];
            }
            default:
            {
                String[] acc = {name('}'), name(VOID), name(CLASS), name(INT), name(STRING), name(STATIC), name(BOOL)};
                throw error(name(lookahead), acc);
            }
        }
    }

    private SemValue parseSubExprList() throws Exception {
        switch (lookahead) {
            case ',':
            {
                SemValue[] params = new SemValue[4];
                params[0] = new SemValue();
                params[1] = matchToken(',');
                params[2] = parseExpr();
                params[3] = parseSubExprList();
                params[0].elist = new ArrayList<Tree.Expr>();
                params[0].elist.add(params[2].expr);
                params[0].elist.addAll(params[3].elist);
                return params[0];
            }
            case ')':
            {
                SemValue[] params = new SemValue[1];
                params[0] = new SemValue();
                params[0].elist = new ArrayList<Tree.Expr>();
                return params[0];
            }
            default:
            {
                String[] acc = {name(','), name(')')};
                throw error(name(lookahead), acc);
            }
        }
    }

    private SemValue parseAfterParenExpr() throws Exception {
        switch (lookahead) {
            case CLASS:
            {
                SemValue[] params = new SemValue[5];
                params[0] = new SemValue();
                params[1] = matchToken(CLASS);
                params[2] = matchToken(IDENTIFIER);
                params[3] = matchToken(')');
                params[4] = parseExpr9();
                params[0].expr = new Tree.TypeCast(params[2].ident, params[4].expr, params[4].loc);
                return params[0];
            }
            case '!':
            case '-':
            case READ_LINE:
            case NULL:
            case IDENTIFIER:
            case NEW:
            case THIS:
            case INSTANCEOF:
            case LITERAL:
            case '(':
            case READ_INTEGER:
            {
                SemValue[] params = new SemValue[3];
                params[0] = new SemValue();
                params[1] = parseExpr();
                params[2] = matchToken(')');
                params[0].expr = params[1].expr;
                return params[0];
            }
            default:
            {
                String[] acc = {name(CLASS), name('!'), name('-'), name(READ_LINE), name(NULL), name(IDENTIFIER), name(NEW), name(THIS), name(INSTANCEOF), name(LITERAL), name('('), name(READ_INTEGER)};
                throw error(name(lookahead), acc);
            }
        }
    }

    private SemValue parseClassDef() throws Exception {
        switch (lookahead) {
            case CLASS:
            {
                SemValue[] params = new SemValue[7];
                params[0] = new SemValue();
                params[1] = matchToken(CLASS);
                params[2] = matchToken(IDENTIFIER);
                params[3] = parseExtendsClause();
                params[4] = matchToken('{');
                params[5] = parseFieldList();
                params[6] = matchToken('}');
                params[0].cdef = new Tree.ClassDef(params[2].ident, params[3].ident, params[5].flist, params[1].loc);
                return params[0];
            }
            default:
            {
                String[] acc = {name(CLASS)};
                throw error(name(lookahead), acc);
            }
        }
    }

    private SemValue parseReturnStmt() throws Exception {
        switch (lookahead) {
            case RETURN:
            {
                SemValue[] params = new SemValue[3];
                params[0] = new SemValue();
                params[1] = matchToken(RETURN);
                params[2] = parseReturnExpr();
                params[0].stmt = new Tree.Return(params[2].expr, params[1].loc);
                return params[0];
            }
            default:
            {
                String[] acc = {name(RETURN)};
                throw error(name(lookahead), acc);
            }
        }
    }

    private SemValue parseExprList() throws Exception {
        switch (lookahead) {
            case '!':
            case '-':
            case READ_LINE:
            case NULL:
            case IDENTIFIER:
            case NEW:
            case THIS:
            case INSTANCEOF:
            case LITERAL:
            case '(':
            case READ_INTEGER:
            {
                SemValue[] params = new SemValue[3];
                params[0] = new SemValue();
                params[1] = parseExpr();
                params[2] = parseSubExprList();
                params[0].elist = new ArrayList<Tree.Expr>();
                params[0].elist.add(params[1].expr);
                params[0].elist.addAll(params[2].elist);
                return params[0];
            }
            default:
            {
                String[] acc = {name('!'), name('-'), name(READ_LINE), name(NULL), name(IDENTIFIER), name(NEW), name(THIS), name(INSTANCEOF), name(LITERAL), name('('), name(READ_INTEGER)};
                throw error(name(lookahead), acc);
            }
        }
    }

    private SemValue parseStmtBlock() throws Exception {
        switch (lookahead) {
            case '{':
            {
                SemValue[] params = new SemValue[4];
                params[0] = new SemValue();
                params[1] = matchToken('{');
                params[2] = parseStmtList();
                params[3] = matchToken('}');
                params[0].stmt = new Tree.Block(params[2].slist, params[1].loc);
                return params[0];
            }
            default:
            {
                String[] acc = {name('{')};
                throw error(name(lookahead), acc);
            }
        }
    }

    private SemValue parseFunctionField() throws Exception {
        switch (lookahead) {
            case '(':
            {
                SemValue[] params = new SemValue[5];
                params[0] = new SemValue();
                params[1] = matchToken('(');
                params[2] = parseFormals();
                params[3] = matchToken(')');
                params[4] = parseStmtBlock();
                params[0].vlist = params[2].vlist;
                params[0].stmt = params[4].stmt;
                return params[0];
            }
            case ';':
            {
                SemValue[] params = new SemValue[2];
                params[0] = new SemValue();
                params[1] = matchToken(';');

                return params[0];
            }
            default:
            {
                String[] acc = {name('('), name(';')};
                throw error(name(lookahead), acc);
            }
        }
    }

    private SemValue parseAfterIdentExpr() throws Exception {
        switch (lookahead) {
            case '(':
            {
                SemValue[] params = new SemValue[4];
                params[0] = new SemValue();
                params[1] = matchToken('(');
                params[2] = parseActuals();
                params[3] = matchToken(')');
                params[0].elist = params[2].elist;
                return params[0];
            }
            case '/':
            case LESS_EQUAL:
            case ']':
            case GREATER_EQUAL:
            case '.':
            case '-':
            case EQUAL:
            case ')':
            case NOT_EQUAL:
            case ',':
            case '=':
            case OR:
            case '+':
            case AND:
            case '*':
            case ';':
            case '<':
            case '[':
            case '>':
            case '%':
            {
                SemValue[] params = new SemValue[1];
                params[0] = new SemValue();

                return params[0];
            }
            default:
            {
                String[] acc = {name('('), name('/'), name(LESS_EQUAL), name(']'), name(GREATER_EQUAL), name('.'), name('-'), name(EQUAL), name(')'), name(NOT_EQUAL), name(','), name('='), name(OR), name('+'), name(AND), name('*'), name(';'), name('<'), name('['), name('>'), name('%')};
                throw error(name(lookahead), acc);
            }
        }
    }

    private SemValue parseProgram() throws Exception {
        switch (lookahead) {
            case CLASS:
            {
                SemValue[] params = new SemValue[3];
                params[0] = new SemValue();
                params[1] = parseClassDef();
                params[2] = parseClassList();
                params[0].clist = new ArrayList<ClassDef>();
                params[0].clist.add(params[1].cdef);
                if (params[2].clist != null) {
                    params[0].clist.addAll(params[2].clist);
                }
                params[0].prog = new Tree.TopLevel(params[0].clist, params[0].loc);
                return params[0];
            }
            default:
            {
                String[] acc = {name(CLASS)};
                throw error(name(lookahead), acc);
            }
        }
    }

    private SemValue parseExpr() throws Exception {
        switch (lookahead) {
            case '!':
            case '-':
            case READ_LINE:
            case NULL:
            case IDENTIFIER:
            case NEW:
            case THIS:
            case INSTANCEOF:
            case LITERAL:
            case '(':
            case READ_INTEGER:
            {
                SemValue[] params = new SemValue[2];
                params[0] = new SemValue();
                params[1] = parseExpr1();
                params[0].expr = params[1].expr;
                return params[0];
            }
            default:
            {
                String[] acc = {name('!'), name('-'), name(READ_LINE), name(NULL), name(IDENTIFIER), name(NEW), name(THIS), name(INSTANCEOF), name(LITERAL), name('('), name(READ_INTEGER)};
                throw error(name(lookahead), acc);
            }
        }
    }

    private SemValue parseType() throws Exception {
        switch (lookahead) {
            case VOID:
            case CLASS:
            case INT:
            case STRING:
            case BOOL:
            {
                SemValue[] params = new SemValue[3];
                params[0] = new SemValue();
                params[1] = parseSimpleType();
                params[2] = parseArrayType();
                params[0].type = params[1].type;
                for (int i = 0; i < params[2].counter; ++i) {
                    params[0].type = new Tree.TypeArray(params[0].type, params[1].loc);
                }
                return params[0];
            }
            default:
            {
                String[] acc = {name(VOID), name(CLASS), name(INT), name(STRING), name(BOOL)};
                throw error(name(lookahead), acc);
            }
        }
    }

    private SemValue parseExpr5() throws Exception {
        switch (lookahead) {
            case '!':
            case '-':
            case READ_LINE:
            case NULL:
            case IDENTIFIER:
            case NEW:
            case THIS:
            case INSTANCEOF:
            case LITERAL:
            case '(':
            case READ_INTEGER:
            {
                SemValue[] params = new SemValue[3];
                params[0] = new SemValue();
                params[1] = parseExpr6();
                params[2] = parseExprT5();
                params[0].expr = params[1].expr;
                if (params[2].svec != null) {
                    for (int i = 0; i < params[2].svec.size(); ++i) {
                        params[0].expr = new Tree.Binary(params[2].svec.get(i), params[0].expr,
                                params[2].evec.get(i), params[2].lvec.get(i));
                    }
                }
                return params[0];
            }
            default:
            {
                String[] acc = {name('!'), name('-'), name(READ_LINE), name(NULL), name(IDENTIFIER), name(NEW), name(THIS), name(INSTANCEOF), name(LITERAL), name('('), name(READ_INTEGER)};
                throw error(name(lookahead), acc);
            }
        }
    }

    private SemValue parseAfterNewExpr() throws Exception {
        switch (lookahead) {
            case IDENTIFIER:
            {
                SemValue[] params = new SemValue[4];
                params[0] = new SemValue();
                params[1] = matchToken(IDENTIFIER);
                params[2] = matchToken('(');
                params[3] = matchToken(')');
                params[0].ident = params[1].ident;
                return params[0];
            }
            case VOID:
            case CLASS:
            case INT:
            case STRING:
            case BOOL:
            {
                SemValue[] params = new SemValue[4];
                params[0] = new SemValue();
                params[1] = parseSimpleType();
                params[2] = matchToken('[');
                params[3] = parseAfterSimpleTypeExpr();
                params[0].type = params[1].type;
                for (int i = 0; i < params[3].counter; ++i) {
                    params[0].type = new Tree.TypeArray(params[0].type, params[1].loc);
                }
                params[0].expr = params[3].expr;
                return params[0];
            }
            default:
            {
                String[] acc = {name(IDENTIFIER), name(VOID), name(CLASS), name(INT), name(STRING), name(BOOL)};
                throw error(name(lookahead), acc);
            }
        }
    }

    private SemValue parseAssignment() throws Exception {
        switch (lookahead) {
            case '=':
            {
                SemValue[] params = new SemValue[3];
                params[0] = new SemValue();
                params[1] = matchToken('=');
                params[2] = parseExpr();
                params[0].loc = params[1].loc;
                params[0].expr = params[2].expr;
                return params[0];
            }
            case ';':
            case ')':
            {
                SemValue[] params = new SemValue[1];
                params[0] = new SemValue();

                return params[0];
            }
            default:
            {
                String[] acc = {name('='), name(';'), name(')')};
                throw error(name(lookahead), acc);
            }
        }
    }

    private SemValue parseExtendsClause() throws Exception {
        switch (lookahead) {
            case EXTENDS:
            {
                SemValue[] params = new SemValue[3];
                params[0] = new SemValue();
                params[1] = matchToken(EXTENDS);
                params[2] = matchToken(IDENTIFIER);
                params[0].ident = params[2].ident;
                return params[0];
            }
            case '{':
            {
                SemValue[] params = new SemValue[1];
                params[0] = new SemValue();

                return params[0];
            }
            default:
            {
                String[] acc = {name(EXTENDS), name('{')};
                throw error(name(lookahead), acc);
            }
        }
    }

    private SemValue parseOper5() throws Exception {
        switch (lookahead) {
            case '-':
            {
                SemValue[] params = new SemValue[2];
                params[0] = new SemValue();
                params[1] = matchToken('-');
                params[0].counter = Tree.MINUS;
                params[0].loc = params[1].loc;
                return params[0];
            }
            case '+':
            {
                SemValue[] params = new SemValue[2];
                params[0] = new SemValue();
                params[1] = matchToken('+');
                params[0].counter = Tree.PLUS;
                params[0].loc = params[1].loc;
                return params[0];
            }
            default:
            {
                String[] acc = {name('-'), name('+')};
                throw error(name(lookahead), acc);
            }
        }
    }

    private SemValue parseArrayType() throws Exception {
        switch (lookahead) {
            case '[':
            {
                SemValue[] params = new SemValue[4];
                params[0] = new SemValue();
                params[1] = matchToken('[');
                params[2] = matchToken(']');
                params[3] = parseArrayType();
                params[0].counter = 1 + params[3].counter;
                return params[0];
            }
            case IDENTIFIER:
            {
                SemValue[] params = new SemValue[1];
                params[0] = new SemValue();
                params[0].counter = 0;
                return params[0];
            }
            default:
            {
                String[] acc = {name('['), name(IDENTIFIER)};
                throw error(name(lookahead), acc);
            }
        }
    }

    private SemValue parseExpr3() throws Exception {
        switch (lookahead) {
            case '!':
            case '-':
            case READ_LINE:
            case NULL:
            case IDENTIFIER:
            case NEW:
            case THIS:
            case INSTANCEOF:
            case LITERAL:
            case '(':
            case READ_INTEGER:
            {
                SemValue[] params = new SemValue[3];
                params[0] = new SemValue();
                params[1] = parseExpr4();
                params[2] = parseExprT3();
                params[0].expr = params[1].expr;
                if (params[2].svec != null) {
                    for (int i = 0; i < params[2].svec.size(); ++i) {
                        params[0].expr = new Tree.Binary(params[2].svec.get(i), params[0].expr,
                                params[2].evec.get(i), params[2].lvec.get(i));
                    }
                }
                return params[0];
            }
            default:
            {
                String[] acc = {name('!'), name('-'), name(READ_LINE), name(NULL), name(IDENTIFIER), name(NEW), name(THIS), name(INSTANCEOF), name(LITERAL), name('('), name(READ_INTEGER)};
                throw error(name(lookahead), acc);
            }
        }
    }

    private SemValue parseActuals() throws Exception {
        switch (lookahead) {
            case '!':
            case '-':
            case READ_LINE:
            case NULL:
            case IDENTIFIER:
            case NEW:
            case THIS:
            case INSTANCEOF:
            case LITERAL:
            case '(':
            case READ_INTEGER:
            {
                SemValue[] params = new SemValue[2];
                params[0] = new SemValue();
                params[1] = parseExprList();
                params[0].elist = params[1].elist;
                return params[0];
            }
            case ')':
            {
                SemValue[] params = new SemValue[1];
                params[0] = new SemValue();
                params[0].elist = new ArrayList<Tree.Expr>();
                return params[0];
            }
            default:
            {
                String[] acc = {name('!'), name('-'), name(READ_LINE), name(NULL), name(IDENTIFIER), name(NEW), name(THIS), name(INSTANCEOF), name(LITERAL), name('('), name(READ_INTEGER), name(')')};
                throw error(name(lookahead), acc);
            }
        }
    }

    private SemValue parseVariable() throws Exception {
        switch (lookahead) {
            case VOID:
            case CLASS:
            case INT:
            case STRING:
            case BOOL:
            {
                SemValue[] params = new SemValue[3];
                params[0] = new SemValue();
                params[1] = parseType();
                params[2] = matchToken(IDENTIFIER);
                params[0].vdef = new Tree.VarDef(params[2].ident, params[1].type, params[2].loc);
                return params[0];
            }
            default:
            {
                String[] acc = {name(VOID), name(CLASS), name(INT), name(STRING), name(BOOL)};
                throw error(name(lookahead), acc);
            }
        }
    }

    private SemValue parseExprT3() throws Exception {
        switch (lookahead) {
            case EQUAL:
            case NOT_EQUAL:
            {
                SemValue[] params = new SemValue[4];
                params[0] = new SemValue();
                params[1] = parseOper3();
                params[2] = parseExpr4();
                params[3] = parseExprT3();
                params[0].svec = new Vector<Integer>();
                params[0].lvec = new Vector<Location>();
                params[0].evec = new Vector<Expr>();
                params[0].svec.add(params[1].counter);
                params[0].lvec.add(params[1].loc);
                params[0].evec.add(params[2].expr);
                if (params[3].svec != null) {
                    params[0].svec.addAll(params[3].svec);
                    params[0].lvec.addAll(params[3].lvec);
                    params[0].evec.addAll(params[3].evec);
                }
                return params[0];
            }
            case ']':
            case ')':
            case ',':
            case '=':
            case OR:
            case AND:
            case ';':
            {
                SemValue[] params = new SemValue[1];
                params[0] = new SemValue();

                return params[0];
            }
            default:
            {
                String[] acc = {name(EQUAL), name(NOT_EQUAL), name(']'), name(')'), name(','), name('='), name(OR), name(AND), name(';')};
                throw error(name(lookahead), acc);
            }
        }
    }

    private SemValue parseStmt() throws Exception {
        switch (lookahead) {
            case WHILE:
            {
                SemValue[] params = new SemValue[2];
                params[0] = new SemValue();
                params[1] = parseWhileStmt();
                params[0].stmt = params[1].stmt;
                return params[0];
            }
            case '!':
            case '-':
            case READ_LINE:
            case NULL:
            case IDENTIFIER:
            case NEW:
            case THIS:
            case INSTANCEOF:
            case LITERAL:
            case '(':
            case ';':
            case READ_INTEGER:
            {
                SemValue[] params = new SemValue[3];
                params[0] = new SemValue();
                params[1] = parseSimpleStmt();
                params[2] = matchToken(';');
                if (params[1].stmt == null) {
                    params[0].stmt = new Tree.Skip(params[2].loc);
                } else {
                    params[0].stmt = params[1].stmt;
                }
                return params[0];
            }
            case VOID:
            case CLASS:
            case INT:
            case STRING:
            case BOOL:
            {
                SemValue[] params = new SemValue[2];
                params[0] = new SemValue();
                params[1] = parseVariableDef();
                params[0].stmt = params[1].vdef;
                return params[0];
            }
            case PRINT:
            {
                SemValue[] params = new SemValue[3];
                params[0] = new SemValue();
                params[1] = parsePrintStmt();
                params[2] = matchToken(';');
                params[0].stmt = params[1].stmt;
                return params[0];
            }
            case BREAK:
            {
                SemValue[] params = new SemValue[3];
                params[0] = new SemValue();
                params[1] = parseBreakStmt();
                params[2] = matchToken(';');
                params[0].stmt = params[1].stmt;
                return params[0];
            }
            case RETURN:
            {
                SemValue[] params = new SemValue[3];
                params[0] = new SemValue();
                params[1] = parseReturnStmt();
                params[2] = matchToken(';');
                params[0].stmt = params[1].stmt;
                return params[0];
            }
            case '{':
            {
                SemValue[] params = new SemValue[2];
                params[0] = new SemValue();
                params[1] = parseStmtBlock();
                params[0].stmt = params[1].stmt;
                return params[0];
            }
            case IF:
            {
                SemValue[] params = new SemValue[2];
                params[0] = new SemValue();
                params[1] = parseIfStmt();
                params[0].stmt = params[1].stmt;
                return params[0];
            }
            case FOR:
            {
                SemValue[] params = new SemValue[2];
                params[0] = new SemValue();
                params[1] = parseForStmt();
                params[0].stmt = params[1].stmt;
                return params[0];
            }
            default:
            {
                String[] acc = {name(WHILE), name('!'), name('-'), name(READ_LINE), name(NULL), name(IDENTIFIER), name(NEW), name(THIS), name(INSTANCEOF), name(LITERAL), name('('), name(';'), name(READ_INTEGER), name(VOID), name(CLASS), name(INT), name(STRING), name(BOOL), name(PRINT), name(BREAK), name(RETURN), name('{'), name(IF), name(FOR)};
                throw error(name(lookahead), acc);
            }
        }
    }

    private SemValue parseSimpleStmt() throws Exception {
        switch (lookahead) {
            case '!':
            case '-':
            case READ_LINE:
            case NULL:
            case IDENTIFIER:
            case NEW:
            case THIS:
            case INSTANCEOF:
            case LITERAL:
            case '(':
            case READ_INTEGER:
            {
                SemValue[] params = new SemValue[3];
                params[0] = new SemValue();
                params[1] = parseExpr();
                params[2] = parseAssignment();
                if (params[2].expr == null) {
                    params[0].stmt = new Tree.Calculate(params[1].expr, params[1].loc);
                } else {
                    params[0].stmt = new Tree.Assign(params[1].expr, params[2].expr, params[2].loc);
                }
                return params[0];
            }
            case ';':
            case ')':
            {
                SemValue[] params = new SemValue[1];
                params[0] = new SemValue();

                return params[0];
            }
            default:
            {
                String[] acc = {name('!'), name('-'), name(READ_LINE), name(NULL), name(IDENTIFIER), name(NEW), name(THIS), name(INSTANCEOF), name(LITERAL), name('('), name(READ_INTEGER), name(';'), name(')')};
                throw error(name(lookahead), acc);
            }
        }
    }

    private SemValue parseSimpleType() throws Exception {
        switch (lookahead) {
            case BOOL:
            {
                SemValue[] params = new SemValue[2];
                params[0] = new SemValue();
                params[1] = matchToken(BOOL);
                params[0].type = new Tree.TypeIdent(Tree.BOOL, params[1].loc);
                return params[0];
            }
            case CLASS:
            {
                SemValue[] params = new SemValue[3];
                params[0] = new SemValue();
                params[1] = matchToken(CLASS);
                params[2] = matchToken(IDENTIFIER);
                params[0].type = new Tree.TypeClass(params[2].ident, params[1].loc);
                return params[0];
            }
            case VOID:
            {
                SemValue[] params = new SemValue[2];
                params[0] = new SemValue();
                params[1] = matchToken(VOID);
                params[0].type = new Tree.TypeIdent(Tree.VOID, params[1].loc);
                return params[0];
            }
            case INT:
            {
                SemValue[] params = new SemValue[2];
                params[0] = new SemValue();
                params[1] = matchToken(INT);
                params[0].type = new Tree.TypeIdent(Tree.INT, params[1].loc);
                return params[0];
            }
            case STRING:
            {
                SemValue[] params = new SemValue[2];
                params[0] = new SemValue();
                params[1] = matchToken(STRING);
                params[0].type = new Tree.TypeIdent(Tree.STRING, params[1].loc);
                return params[0];
            }
            default:
            {
                String[] acc = {name(BOOL), name(CLASS), name(VOID), name(INT), name(STRING)};
                throw error(name(lookahead), acc);
            }
        }
    }

    private SemValue parseWhileStmt() throws Exception {
        switch (lookahead) {
            case WHILE:
            {
                SemValue[] params = new SemValue[6];
                params[0] = new SemValue();
                params[1] = matchToken(WHILE);
                params[2] = matchToken('(');
                params[3] = parseExpr();
                params[4] = matchToken(')');
                params[5] = parseStmt();
                params[0].stmt = new Tree.WhileLoop(params[3].expr, params[5].stmt, params[1].loc);
                return params[0];
            }
            default:
            {
                String[] acc = {name(WHILE)};
                throw error(name(lookahead), acc);
            }
        }
    }

    private SemValue parseExprT1() throws Exception {
        switch (lookahead) {
            case OR:
            {
                SemValue[] params = new SemValue[4];
                params[0] = new SemValue();
                params[1] = parseOper1();
                params[2] = parseExpr2();
                params[3] = parseExprT1();
                params[0].svec = new Vector<Integer>();
                params[0].lvec = new Vector<Location>();
                params[0].evec = new Vector<Expr>();
                params[0].svec.add(params[1].counter);
                params[0].lvec.add(params[1].loc);
                params[0].evec.add(params[2].expr);
                if (params[3].svec != null) {
                    params[0].svec.addAll(params[3].svec);
                    params[0].lvec.addAll(params[3].lvec);
                    params[0].evec.addAll(params[3].evec);
                }
                return params[0];
            }
            case ']':
            case ')':
            case ',':
            case '=':
            case ';':
            {
                SemValue[] params = new SemValue[1];
                params[0] = new SemValue();

                return params[0];
            }
            default:
            {
                String[] acc = {name(OR), name(']'), name(')'), name(','), name('='), name(';')};
                throw error(name(lookahead), acc);
            }
        }
    }

    private SemValue parseExpr4() throws Exception {
        switch (lookahead) {
            case '!':
            case '-':
            case READ_LINE:
            case NULL:
            case IDENTIFIER:
            case NEW:
            case THIS:
            case INSTANCEOF:
            case LITERAL:
            case '(':
            case READ_INTEGER:
            {
                SemValue[] params = new SemValue[3];
                params[0] = new SemValue();
                params[1] = parseExpr5();
                params[2] = parseExprT4();
                params[0].expr = params[1].expr;
                if (params[2].svec != null) {
                    for (int i = 0; i < params[2].svec.size(); ++i) {
                        params[0].expr = new Tree.Binary(params[2].svec.get(i), params[0].expr,
                                params[2].evec.get(i), params[2].lvec.get(i));
                    }
                }
                return params[0];
            }
            default:
            {
                String[] acc = {name('!'), name('-'), name(READ_LINE), name(NULL), name(IDENTIFIER), name(NEW), name(THIS), name(INSTANCEOF), name(LITERAL), name('('), name(READ_INTEGER)};
                throw error(name(lookahead), acc);
            }
        }
    }

    private SemValue parseExprT4() throws Exception {
        switch (lookahead) {
            case ']':
            case EQUAL:
            case ')':
            case NOT_EQUAL:
            case ',':
            case '=':
            case OR:
            case AND:
            case ';':
            {
                SemValue[] params = new SemValue[1];
                params[0] = new SemValue();

                return params[0];
            }
            case LESS_EQUAL:
            case GREATER_EQUAL:
            case '<':
            case '>':
            {
                SemValue[] params = new SemValue[4];
                params[0] = new SemValue();
                params[1] = parseOper4();
                params[2] = parseExpr5();
                params[3] = parseExprT4();
                params[0].svec = new Vector<Integer>();
                params[0].lvec = new Vector<Location>();
                params[0].evec = new Vector<Expr>();
                params[0].svec.add(params[1].counter);
                params[0].lvec.add(params[1].loc);
                params[0].evec.add(params[2].expr);
                if (params[3].svec != null) {
                    params[0].svec.addAll(params[3].svec);
                    params[0].lvec.addAll(params[3].lvec);
                    params[0].evec.addAll(params[3].evec);
                }
                return params[0];
            }
            default:
            {
                String[] acc = {name(']'), name(EQUAL), name(')'), name(NOT_EQUAL), name(','), name('='), name(OR), name(AND), name(';'), name(LESS_EQUAL), name(GREATER_EQUAL), name('<'), name('>')};
                throw error(name(lookahead), acc);
            }
        }
    }

    private SemValue parseReturnExpr() throws Exception {
        switch (lookahead) {
            case '!':
            case '-':
            case READ_LINE:
            case NULL:
            case IDENTIFIER:
            case NEW:
            case THIS:
            case INSTANCEOF:
            case LITERAL:
            case '(':
            case READ_INTEGER:
            {
                SemValue[] params = new SemValue[2];
                params[0] = new SemValue();
                params[1] = parseExpr();
                params[0].expr = params[1].expr;
                return params[0];
            }
            case ';':
            {
                SemValue[] params = new SemValue[1];
                params[0] = new SemValue();

                return params[0];
            }
            default:
            {
                String[] acc = {name('!'), name('-'), name(READ_LINE), name(NULL), name(IDENTIFIER), name(NEW), name(THIS), name(INSTANCEOF), name(LITERAL), name('('), name(READ_INTEGER), name(';')};
                throw error(name(lookahead), acc);
            }
        }
    }

    private SemValue parseIfStmt() throws Exception {
        switch (lookahead) {
            case IF:
            {
                SemValue[] params = new SemValue[7];
                params[0] = new SemValue();
                params[1] = matchToken(IF);
                params[2] = matchToken('(');
                params[3] = parseExpr();
                params[4] = matchToken(')');
                params[5] = parseStmt();
                params[6] = parseElseClause();
                params[0].stmt = new Tree.If(params[3].expr, params[5].stmt, params[6].stmt, params[1].loc);
                return params[0];
            }
            default:
            {
                String[] acc = {name(IF)};
                throw error(name(lookahead), acc);
            }
        }
    }

    private SemValue parseExprT6() throws Exception {
        switch (lookahead) {
            case '*':
            case '/':
            case '%':
            {
                SemValue[] params = new SemValue[4];
                params[0] = new SemValue();
                params[1] = parseOper6();
                params[2] = parseExpr7();
                params[3] = parseExprT6();
                params[0].svec = new Vector<Integer>();
                params[0].lvec = new Vector<Location>();
                params[0].evec = new Vector<Expr>();
                params[0].svec.add(params[1].counter);
                params[0].lvec.add(params[1].loc);
                params[0].evec.add(params[2].expr);
                if (params[3].svec != null) {
                    params[0].svec.addAll(params[3].svec);
                    params[0].lvec.addAll(params[3].lvec);
                    params[0].evec.addAll(params[3].evec);
                }
                return params[0];
            }
            case LESS_EQUAL:
            case ']':
            case GREATER_EQUAL:
            case '-':
            case EQUAL:
            case ')':
            case NOT_EQUAL:
            case ',':
            case '=':
            case OR:
            case '+':
            case AND:
            case ';':
            case '<':
            case '>':
            {
                SemValue[] params = new SemValue[1];
                params[0] = new SemValue();

                return params[0];
            }
            default:
            {
                String[] acc = {name('*'), name('/'), name('%'), name(LESS_EQUAL), name(']'), name(GREATER_EQUAL), name('-'), name(EQUAL), name(')'), name(NOT_EQUAL), name(','), name('='), name(OR), name('+'), name(AND), name(';'), name('<'), name('>')};
                throw error(name(lookahead), acc);
            }
        }
    }

    private SemValue parseExprT8() throws Exception {
        switch (lookahead) {
            case '.':
            {
                SemValue[] params = new SemValue[5];
                params[0] = new SemValue();
                params[1] = matchToken('.');
                params[2] = matchToken(IDENTIFIER);
                params[3] = parseAfterIdentExpr();
                params[4] = parseExprT8();
                SemValue sem = new SemValue();
                sem.ident = params[2].ident;
                sem.loc = params[2].loc;
                sem.elist = params[3].elist;
                params[0].vec = new Vector<SemValue>();
                params[0].vec.add(sem);
                if (params[4].vec != null) {
                    params[0].vec.addAll(params[4].vec);
                }
                return params[0];
            }
            case '[':
            {
                SemValue[] params = new SemValue[5];
                params[0] = new SemValue();
                params[1] = matchToken('[');
                params[2] = parseExpr();
                params[3] = matchToken(']');
                params[4] = parseExprT8();
                SemValue sem = new SemValue();
                sem.expr = params[2].expr;
                params[0].vec = new Vector<SemValue>();
                params[0].vec.add(sem);
                if (params[4].vec != null) {
                    params[0].vec.addAll(params[4].vec);
                }
                return params[0];
            }
            case '/':
            case LESS_EQUAL:
            case ']':
            case GREATER_EQUAL:
            case '-':
            case EQUAL:
            case ')':
            case NOT_EQUAL:
            case ',':
            case '=':
            case OR:
            case '+':
            case AND:
            case '*':
            case ';':
            case '<':
            case '>':
            case '%':
            {
                SemValue[] params = new SemValue[1];
                params[0] = new SemValue();

                return params[0];
            }
            default:
            {
                String[] acc = {name('.'), name('['), name('/'), name(LESS_EQUAL), name(']'), name(GREATER_EQUAL), name('-'), name(EQUAL), name(')'), name(NOT_EQUAL), name(','), name('='), name(OR), name('+'), name(AND), name('*'), name(';'), name('<'), name('>'), name('%')};
                throw error(name(lookahead), acc);
            }
        }
    }

    private SemValue parseExpr7() throws Exception {
        switch (lookahead) {
            case READ_LINE:
            case NULL:
            case IDENTIFIER:
            case NEW:
            case THIS:
            case INSTANCEOF:
            case LITERAL:
            case '(':
            case READ_INTEGER:
            {
                SemValue[] params = new SemValue[2];
                params[0] = new SemValue();
                params[1] = parseExpr8();
                params[0].expr = params[1].expr;
                return params[0];
            }
            case '-':
            case '!':
            {
                SemValue[] params = new SemValue[3];
                params[0] = new SemValue();
                params[1] = parseOper7();
                params[2] = parseExpr8();
                params[0].expr = new Tree.Unary(params[1].counter, params[2].expr, params[1].loc);
                return params[0];
            }
            default:
            {
                String[] acc = {name(READ_LINE), name(NULL), name(IDENTIFIER), name(NEW), name(THIS), name(INSTANCEOF), name(LITERAL), name('('), name(READ_INTEGER), name('-'), name('!')};
                throw error(name(lookahead), acc);
            }
        }
    }

    private SemValue parseClassList() throws Exception {
        switch (lookahead) {
            case CLASS:
            {
                SemValue[] params = new SemValue[3];
                params[0] = new SemValue();
                params[1] = parseClassDef();
                params[2] = parseClassList();
                params[0].clist = new ArrayList<ClassDef>();
                params[0].clist.add(params[1].cdef);
                if (params[2].clist != null) {
                    params[0].clist.addAll(params[2].clist);
                }
                return params[0];
            }
            case eof:
            case eos:
            {
                SemValue[] params = new SemValue[1];
                params[0] = new SemValue();

                return params[0];
            }
            default:
            {
                String[] acc = {name(CLASS), name('#')};
                throw error(name(lookahead), acc);
            }
        }
    }

    private SemValue parseOper4() throws Exception {
        switch (lookahead) {
            case LESS_EQUAL:
            {
                SemValue[] params = new SemValue[2];
                params[0] = new SemValue();
                params[1] = matchToken(LESS_EQUAL);
                params[0].counter = Tree.LE;
                params[0].loc = params[1].loc;
                return params[0];
            }
            case GREATER_EQUAL:
            {
                SemValue[] params = new SemValue[2];
                params[0] = new SemValue();
                params[1] = matchToken(GREATER_EQUAL);
                params[0].counter = Tree.GE;
                params[0].loc = params[1].loc;
                return params[0];
            }
            case '<':
            {
                SemValue[] params = new SemValue[2];
                params[0] = new SemValue();
                params[1] = matchToken('<');
                params[0].counter = Tree.LT;
                params[0].loc = params[1].loc;
                return params[0];
            }
            case '>':
            {
                SemValue[] params = new SemValue[2];
                params[0] = new SemValue();
                params[1] = matchToken('>');
                params[0].counter = Tree.GT;
                params[0].loc = params[1].loc;
                return params[0];
            }
            default:
            {
                String[] acc = {name(LESS_EQUAL), name(GREATER_EQUAL), name('<'), name('>')};
                throw error(name(lookahead), acc);
            }
        }
    }

    private SemValue parseField() throws Exception {
        switch (lookahead) {
            case VOID:
            case CLASS:
            case INT:
            case STRING:
            case BOOL:
            {
                SemValue[] params = new SemValue[4];
                params[0] = new SemValue();
                params[1] = parseType();
                params[2] = matchToken(IDENTIFIER);
                params[3] = parseFunctionField();
                if (params[3].vlist != null) {
                    params[0].fdef = new Tree.MethodDef(false, params[2].ident, params[1].type, params[3].vlist,
                            (Tree.Block) params[3].stmt, params[2].loc);
                } else {
                    params[0].vdef = new Tree.VarDef(params[2].ident, params[1].type, params[2].loc);
                }
                return params[0];
            }
            case STATIC:
            {
                SemValue[] params = new SemValue[8];
                params[0] = new SemValue();
                params[1] = matchToken(STATIC);
                params[2] = parseType();
                params[3] = matchToken(IDENTIFIER);
                params[4] = matchToken('(');
                params[5] = parseFormals();
                params[6] = matchToken(')');
                params[7] = parseStmtBlock();
                params[0].fdef = new Tree.MethodDef(true, params[3].ident, params[2].type, params[5].vlist,
                        (Tree.Block) params[7].stmt, params[3].loc);
                return params[0];
            }
            default:
            {
                String[] acc = {name(VOID), name(CLASS), name(INT), name(STRING), name(BOOL), name(STATIC)};
                throw error(name(lookahead), acc);
            }
        }
    }

}
/* end of file */
