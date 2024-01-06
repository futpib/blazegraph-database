package com.bigdata.rdf.sail.sparql.ast;

import java.io.IOException;
import java.io.StringReader;
import java.util.LinkedList;

/**
 * A string reader that converts unicode escape sequences "\" + "uXXXX" and "\Uxxxxxxxx" to characters
 * before passing the SPARQL query string to the grammar-based parser.
 * 
 * https://www.w3.org/TR/sparql11-query/#codepointEscape
 */
public class QueryStringReader extends StringReader {

    private LinkedList<Integer> lookahead = new LinkedList<Integer>();

    public QueryStringReader(String string) {
        super(string);
    }

    private int peek() throws IOException {
        int c = super.read();

        if (c != -1) {
            lookahead.addLast(c);
        }

        return c;
    }

    /**
     * HEX ::= [0-9] | [A-F] | [a-f]
     */
    private boolean isHexDigit(int c) {
        return (c >= '0' && c <= '9') || (c >= 'A' && c <= 'F') || (c >= 'a' && c <= 'f');
    }

    @Override
    public int read() throws IOException {
        if (!lookahead.isEmpty()) {
            return lookahead.removeFirst();
        }

        int c = super.read();

        if (c == '\\') {
            int cU = peek();

            if (cU == 'u') {
                int c1 = peek();
                int c2 = peek();
                int c3 = peek();
                int c4 = peek();

                if (isHexDigit(c1) && isHexDigit(c2) && isHexDigit(c3) && isHexDigit(c4)) {
                    int unicodeC = Integer.parseInt(new String(new char[] { (char) c1, (char) c2, (char) c3, (char) c4 }), 16);
                    lookahead.clear();

                    return unicodeC;
                }
            } else if (cU == 'U') {
                int c1 = peek();
                int c2 = peek();
                int c3 = peek();
                int c4 = peek();
                int c5 = peek();
                int c6 = peek();
                int c7 = peek();
                int c8 = peek();

                if (isHexDigit(c1) && isHexDigit(c2) && isHexDigit(c3) && isHexDigit(c4) && isHexDigit(c5) && isHexDigit(c6) && isHexDigit(c7) && isHexDigit(c8)) {
                    int unicodeC = Integer.parseInt(new String(new char[] { (char) c1, (char) c2, (char) c3, (char) c4, (char) c5, (char) c6, (char) c7, (char) c8 }), 16);
                    lookahead.clear();

                    for (char unicodeCChar : Character.toChars(unicodeC)) {
                        lookahead.addLast((int) unicodeCChar);
                    }

                    return lookahead.removeFirst();
                }
            }
        }

        return c;
    }

    public int read(char[] cbuf, int off, int len) throws IOException {
        synchronized (lock) {
            int initialOffset = off;
            while (len > 0) {
                int c = read();
                if (c == -1) {
                    return off - initialOffset == 0 ? -1 : off - initialOffset;
                }
                cbuf[off++] = (char) c;
                len--;
            }
            return off - initialOffset;
        }
    }

}
