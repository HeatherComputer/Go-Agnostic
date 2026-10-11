package computer.heather.goagnostic.api.util;


/*
Mostly the same as Brigadier's equivalent.
Allows for parsing commands nicely on pre-brigadier minecraft versions.
 */

public class StringReader {

    private static final char SYNTAX_ESCAPE = '\\';
    private static final char DOUBLE_QUOTE = '"';
    private static final char SINGLE_QUOTE = '\'';

    private final String string;
    private int cursor;

    public StringReader(final StringReader other) {
        this.string = other.string;
        this.cursor = other.cursor;
    }

    public StringReader(String string) {
        this.string = string;
    }

    public String getString() {
        return string;
    }

    public void setCursor(int cursor) {
        this.cursor = cursor;
    }

    public int getRemainingLength() {
        return string.length() - cursor;
    }
    
    public int getTotalLength() {
        return string.length();
    }
    
    
    public String getRemaining() {
        return string.substring(cursor);
    }

    public boolean canRead(final int length) {
        return cursor + length <= string.length();
    }

    public boolean canRead() {
        return canRead(1);
    }
    
    public char peek() {
        return string.charAt(cursor);
    }

    public char peek(int offset) {
        return string.charAt(cursor + offset);
    }

    public char read() {
        return string.charAt(cursor++);
    }

    public void skip() {
        cursor++;
    }

    public String readAll() {
        String ret = getRemaining();
        setCursor(getTotalLength());
        return ret;
    }

    public static boolean isQuote(char c) {
        return c == DOUBLE_QUOTE || c == SINGLE_QUOTE;
    }
    
    public static boolean isAllowedNumber(final char c) {
        return c >= '0' && c <= '9' || c == '.' || c == '-';
    }

    public void skipWhitespace() {
        while (canRead() && Character.isWhitespace(peek())) {
            read();
        }
    }



    //Lots of helper methods below


    
    public String readString() throws ParseException {
        if (!canRead()) {
            return "";
        }
        final char next = peek();
        if (isQuote(next)) {
            skip();
            return readStringUntil(next);
        }
        return readUnquotedString();
    }

    public String readStringUntil(char terminator) throws ParseException {
        final StringBuilder result = new StringBuilder();
        boolean escaped = false;
        while (canRead()) {
            final char c = read();
            if (escaped) {
                if (c == terminator || c == SYNTAX_ESCAPE) {
                    result.append(c);
                    escaped = false;
                } else {
                    setCursor(cursor - 1);
                    throw new ParseException("Invalid escape was used when parsing string!", String.valueOf(c), result.toString());
                }
            } else if (c == SYNTAX_ESCAPE) {
                escaped = true;
            } else if (c == terminator) {
                return result.toString();
            } else {
                result.append(c);
            }
        }
        throw new ParseException("Expected end of quote but couldn't find it!", result.toString());
    }

    public String readUnquotedString() {
        final int start = cursor;
        while (canRead() && !Character.isWhitespace(peek())) {
            skip();
        }
        return string.substring(start, cursor);
    }



    public boolean readBoolean() throws ParseException {
        final int start = cursor;
        final String value = readString();
        if (value.isEmpty()) {
            throw new ParseException("Error parsing boolean: ", "Provided value was empty");
        }

        if (value.equals("true")) {
            return true;
        } else if (value.equals("false")) {
            return false;
        } else {
            cursor = start;
            throw new ParseException("Error parsing boolean: ", "Provided value is not true or false", value);
        }
    }
    

    public int readInt() throws ParseException {
        final int start = cursor;
        while (canRead() && isAllowedNumber(peek())) {
            skip();
        }
        final String number = string.substring(start, cursor);
        if (number.isEmpty()) {
            throw new ParseException("Expected integer was empty!", string.substring(start));
        }
        try {
            return Integer.parseInt(number);
        } catch (final NumberFormatException ex) {
            cursor = start;
            throw new ParseException("Provided number was not a valid integer!", number);
        }
    }



    //Exceptions
    public static class ParseException extends Exception {

		public ParseException(String... strings) {
            super(String.join("\n", strings));
		}

    }



}
