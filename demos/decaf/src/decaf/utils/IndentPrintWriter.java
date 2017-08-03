package decaf.utils;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;

/**
 * Indent printer, used as the printer for the parsed AST.
 */
public class IndentPrintWriter extends PrintWriter {
    private int step;

    private StringBuilder indent;

    private boolean newLineBegin;

    /**
     * Constructor.
     *
     * @param out  output stream.
     * @param step number of spaces for each indentation level.
     */
    public IndentPrintWriter(OutputStream out, int step) {
        this(new OutputStreamWriter(out), step);
    }

    /**
     * Constructor.
     *
     * @param out  output writer.
     * @param step step number of spaces for each indentation level.
     */
    public IndentPrintWriter(Writer out, int step) {
        super(out);
        this.step = step;
        indent = new StringBuilder();
        newLineBegin = true;
    }

    /**
     * Increase indentation level.
     */
    public void incIndent() {
        for (int i = 0; i < step; i++) {
            indent.append(" ");
        }
    }

    /**
     * Decrease indentation level.
     */
    public void decIndent() {
        indent.setLength(indent.length() - step);
    }

    @Override
    public void println() {
        super.println();
        newLineBegin = true;
    }

    private void writeIndent() {
        if (newLineBegin) {
            newLineBegin = false;
            print(indent);
        }
    }

    @Override
    public void write(char[] buf, int off, int len) {
        writeIndent();
        super.write(buf, off, len);
    }

    @Override
    public void write(int c) {
        writeIndent();
        super.write(c);
    }

    @Override
    public void write(String s, int off, int len) {
        writeIndent();
        super.write(s, off, len);
    }

}
