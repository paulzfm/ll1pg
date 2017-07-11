package arith;

import arith.Expr;
import java.util.ArrayList;

public class SemValue {
    public Location loc;

    public Expr expr;

    public ArrayList<Expr.Term> terms = new ArrayList<Expr.Term>();

    public int op;

    public static SemValue createOperator(int op) {
        SemValue v = new SemValue();
        v.op = op;
        return v;
    }

    public static SemValue createNum(int value) {
        SemValue v = new SemValue();
        v.expr = new Expr.Number(value);
        return v;
    }

    @Override
    public String toString() {
        String s = loc + ": expr=" + expr + ", op=" + op + ", terms=";
        for (Expr.Term t: terms) {
            s += (t + ",");
        }
        return s;
    }
}