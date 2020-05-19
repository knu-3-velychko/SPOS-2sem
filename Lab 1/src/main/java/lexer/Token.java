package lexer;

public class Token {
    public enum Type {
        COMMENT,
        WHITESPACE,
        IDENTIFIER,
        OPERATOR,
        SEPARATOR,
        INT_LITERAL,
        FLOAT_LITERAL,
        CHAR_LITERAL,
        STRING_LITERAL,
        BOOLEAN_LITERAL,
        NULL_LITERAL,
        KEYWORD,
        ERROR
    }

    private final Type type;

    private final String tokenString;

    Token(String tokenString, Type type) {
        this.type = type;
        this.tokenString = tokenString;
    }

    public String getTokenString() {
        return this.tokenString;
    }

    public Type getType() {
        return this.type;
    }

    @Override
    public String toString() {
        return "Token{" +
                "type=" + type +
                ", tokenString='" + tokenString + '\'' +
                '}';
    }
}
