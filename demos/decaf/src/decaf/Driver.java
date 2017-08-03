package decaf;

import java.io.*;

import decaf.Lexer;
import decaf.Parser;
import decaf.Tree;
import decaf.error.DecafError;
import decaf.utils.IndentPrintWriter;

/**
 * Main class of demo `decaf`.
 *
 * Input: a decaf program source path.
 * Output: the decaf AST.
 */
public final class Driver {

    private static Driver driver;

    private Lexer lexer;

    private Parser parser;

    public static Driver getDriver() {
        return driver;
    }

    public void issueError(DecafError error) throws DecafError {
        throw error;
    }

    private void compile(InputStream in) throws Exception {
        lexer = new Lexer(in);
        parser = new Parser();
        lexer.setParser(parser);
        parser.setLexer(lexer);
        Tree.TopLevel tree = parser.parseFile();
        IndentPrintWriter pw = new IndentPrintWriter(System.out, 4);
        tree.printTo(pw);
        pw.close();
    }

    public static void main(String[] args) throws IOException {
        driver = new Driver();
        if (args.length != 1) {
            System.err.println("Usage: java -jar decaf.jar source_file");
            System.exit(1);
        }

        InputStream in = new FileInputStream(args[0]);
        try {
            driver.compile(in);
        } catch (Exception err) {
            System.err.println(err);
        }
    }

}
