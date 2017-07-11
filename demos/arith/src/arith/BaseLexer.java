package arith;

import java.io.IOException;

import arith.Driver;
import arith.Location;
import arith.error.CompileError;

public abstract class BaseLexer {

    private Parser parser;

    public void setParser(Parser parser) {
        this.parser = parser;
    }

    abstract int yylex() throws IOException;

    abstract Location getLocation();

    protected void issueError(CompileError error) {
        Driver.getDriver().issueError(error);
    }

    protected void setSemantic(Location where, SemValue v) {
        v.loc = where;
        parser.yylval = v;
    }

    protected int operator(int code) {
        setSemantic(getLocation(), SemValue.createOperator(code));
        return code;
    }

    protected int intConst(String ival) {
        setSemantic(getLocation(), SemValue.createNum(Integer.decode(ival)));
        return Parser.NUM;
    }

    public void diagnose() throws IOException {
        while (yylex() != 0) {
            System.out.println(parser.yylval);
        }
    }
}
