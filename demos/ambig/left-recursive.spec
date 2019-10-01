/* CFG:
    E -> E + T | T
    T -> (E) | a

    Left-recursive!

    ll1pg -strict left-recursive.spec output.java
*/

%package ambig
%import
%class Parser extends BaseParser
%sem SemValue
%start E

%tokens NUM '+' '(' ')'

%%
E   : E '+' T
    | T
    ;

T   : '(' E ')'
    | NUM
    ;
