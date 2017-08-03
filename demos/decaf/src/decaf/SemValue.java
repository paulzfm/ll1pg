package decaf;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import decaf.Location;
import decaf.Tree;
import decaf.Tree.ClassDef;
import decaf.Tree.Expr;
import decaf.Tree.LValue;
import decaf.Tree.MethodDef;
import decaf.Tree.TopLevel;
import decaf.Tree.TypeLiteral;
import decaf.Tree.VarDef;
import decaf.utils.MiscUtils;

/**
 * Semantic value.
 */
public class SemValue {

    public int code;

    public int counter;

    public Vector<Integer> svec;

    public Location loc;

    public Vector<Location> lvec;

    public int typeTag;

    public Object literal;

    public String ident;

    public List<ClassDef> clist;

    /**
     * field list
     */
    public List<Tree> flist;

    public List<VarDef> vlist;

    /**
     * statement list
     */
    public List<Tree> slist = new ArrayList<Tree>();

    public List<Expr> elist;

    public TopLevel prog;

    public ClassDef cdef;

    public VarDef vdef;

    public MethodDef fdef;

    public TypeLiteral type;

    public Tree stmt;

    public Expr expr;

    public Expr expr1;

    public Vector<Expr> evec;

    public LValue lvalue;

    public Vector<SemValue> vec;

    /**
     * Create semantic value for keywords.
     *
     * @param code keyword token
     * @return the semantic value
     */
    public static SemValue createKeyword(int code) {
        SemValue v = new SemValue();
        v.code = code;
        return v;
    }

    /**
     * Create semantic value for operators.
     *
     * @param code operator token
     * @return the semantic value
     */
    public static SemValue createOperator(int code) {
        SemValue v = new SemValue();
        v.code = code;
        return v;
    }

    /**
     * Create semantic value for literals.
     *
     * @param value value for the literal
     * @return the semantic value
     */
    public static SemValue createLiteral(int tag, Object value) {
        SemValue v = new SemValue();
        v.code = Parser.LITERAL;
        v.typeTag = tag;
        v.literal = value;
        return v;
    }

    /**
     * Create semantic value for identifiers.
     *
     * @param name name of the identifier
     * @return the semantic value
     */
    public static SemValue createIdentifier(String name) {
        SemValue v = new SemValue();
        v.code = Parser.IDENTIFIER;
        v.ident = name;
        return v;
    }

    @Override
    public String toString() {
        String msg;
        switch (code) {
            // keywords
            case Parser.BOOL:
                msg = "keyword  : bool";
                break;
            case Parser.BREAK:
                msg = "keyword  : break";
                break;
            case Parser.CLASS:
                msg = "keyword  : class";
                break;
            case Parser.ELSE:
                msg = "keyword  : else";
                break;
            case Parser.EXTENDS:
                msg = "keyword  : extends";
                break;
            case Parser.FOR:
                msg = "keyword  : for";
                break;
            case Parser.IF:
                msg = "keyword  : if";
                break;
            case Parser.INT:
                msg = "keyword  : int";
                break;
            case Parser.INSTANCEOF:
                msg = "keyword  : instanceof";
                break;
            case Parser.NEW:
                msg = "keyword  : new";
                break;
            case Parser.NULL:
                msg = "keyword  : null";
                break;
            case Parser.PRINT:
                msg = "keyword  : Print";
                break;
            case Parser.READ_INTEGER:
                msg = "keyword  : ReadInteger";
                break;
            case Parser.READ_LINE:
                msg = "keyword  : ReadLine";
                break;
            case Parser.RETURN:
                msg = "keyword  : return";
                break;
            case Parser.STRING:
                msg = "keyword  : string";
                break;
            case Parser.THIS:
                msg = "keyword  : this";
                break;
            case Parser.VOID:
                msg = "keyword  : void";
                break;
            case Parser.WHILE:
                msg = "keyword  : while";
                break;
            case Parser.STATIC:
                msg = "keyword : static";
                break;

            // literals
            case Parser.LITERAL:
                switch (typeTag) {
                    case Tree.INT:
                    case Tree.BOOL:
                        msg = "constant : " + literal;
                        break;
                    default:
                        msg = "constant : " + MiscUtils.quote((String) literal);
                }
                break;

            // identifiers
            case Parser.IDENTIFIER:
                msg = "identifier: " + ident;
                break;

            // operators
            case Parser.AND:
                msg = "operator : &&";
                break;
            case Parser.EQUAL:
                msg = "operator : ==";
                break;
            case Parser.GREATER_EQUAL:
                msg = "operator : >=";
                break;
            case Parser.LESS_EQUAL:
                msg = "operator : <=";
                break;
            case Parser.NOT_EQUAL:
                msg = "operator : !=";
                break;
            case Parser.OR:
                msg = "operator : ||";
                break;
            default:
                msg = "operator : " + (char) code;
                break;
        }
        return (String.format("%-15s%s", loc, msg));
    }
}