/* CFG:
    S -> A A | A B
    Left-factor!

    ll1pg -strict left-factor.spec output.java
*/

%package ambig
%import
%class Parser extends BaseParser
%sem SemValue
%start E

%tokens A B

%%
S   : A A
    | A B
    ;
