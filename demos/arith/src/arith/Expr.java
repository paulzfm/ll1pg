package arith;

import java.util.*;

/**
 * Expressions.
 */
public abstract class Expr {
    // Expr types
    public static final int TYPE_ARITH = 1;
    public static final int TYPE_NUM = 2;

    // Operator types
    public static final int ADD = 3;
    public static final int MUL = 4;

    public int type;

    public Expr(int type) {
        this.type = type;
    }

    /**
     * Evaluate the expression.
     *
     * @return computation result.
     */
    public abstract int eval();

    /**
     * Arithmetic expressions.
     */
    public static class ArithExpr extends Expr {
        public int op;
        public Expr left, right;

        public ArithExpr(int op, Expr left, Expr right) {
            super(TYPE_ARITH);
            this.op = op;
            this.left = left;
            this.right = right;
        }

        @Override
        public int eval() {
            int l = left.eval();
            int r = right.eval();
            switch (op) {
                case ADD:
                    return l + r;
                case MUL:
                    return l * r;
            }
            return 0;
        }

        @Override
        public String toString() {
            String s = "(" + (op == ADD ? "+" : "*") + " ";
            s += left;
            s += " ";
            s += right;
            s += ")";
            return s;
        }
    }

    /**
     * Numbers (integers).
     */
    public static class Number extends Expr {
        public int value;

        public Number(int value) {
            super(TYPE_NUM);
            this.value = value;
        }

        @Override
        public int eval() {
            return value;
        }

        @Override
        public String toString() { return "" + value; }
    }

    /**
     * Tuple of operator and expression, used by parser as a temporary storage.
     */
    public static class Term {
        public int op;
        public Expr expr;

        public Term(int op, Expr expr) {
            this.op = op;
            this.expr = expr;
        }

        @Override
        public String toString() {
            return "(" + (op == ADD ? "+" : "*") + ", " + expr + ")";
        }
    }
}