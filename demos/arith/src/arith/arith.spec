/* CFG:
    E -> E + T | T
    T -> T * F | F
    F -> (E) | a

   LL(1) Grammar:
    E  -> TE'
    E' -> +TE' | `epsilon`
    T  -> FT'
    T' -> *FT' | `epsilon`
    F  -> (E) | a
 */

%package arith
%import
arith.Expr
arith.Expr.*
arith.error.CompileError
java.util.*
%class public class Parser extends BaseParser
%sem SemValue
%start E

%tokens NUM '+' '*' '(' ')'

%%

E   :   T E1
        {
            $$.expr = $1.expr;
            for (Term t : $2.terms) {
                $$.expr = new ArithExpr(t.op, $$.expr, t.expr);
            }
        }
    ;

E1  :   '+' T E1
        {
            $$.terms.add(new Term(Expr.ADD, $2.expr));
            $$.terms.addAll($3.terms);
        }
    |   /* empty */
    ;

T   :   F T1
        {
            $$.expr = $1.expr;
            for (Term t : $2.terms) {
                $$.expr = new ArithExpr(t.op, $$.expr, t.expr);
            }
        }
    ;

T1  :   '*' F T1
        {
            $$.terms.add(new Term(Expr.MUL, $2.expr));
            $$.terms.addAll($3.terms);
        }
    |   /* empty */
    ;

F   :   '(' E ')'
        {
            $$.expr = $2.expr;
        }
    |   NUM
        {
            $$.expr = $1.expr;
        }
    ;