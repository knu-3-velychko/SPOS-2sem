package lexer;

public class Token {
    public enum Type {
        BLOCK_COMMENT,
        LINE_COMMENT,
        WHITE_SPACE,
        TAB,
        NEW_LINE,
        LEFT_PARENTHESES,
        RIGHT_PARENTHESES,
        LEFT_BRACE,
        RIGHT_BRACE,
        VAL,
        VAR,
        ARRAY,
        LITERAL_STRING,
        LITERAL_DOUBLE,
        LITERAL_INT,
        OPERATOR_PLUS,
        OPERATOR_MINUS,
        OPERATOR_MULTIPLY,
        OPERATOR_DIVIDE,
        OPERATOR_POINT,
        OPERATOR_EQUAL,
        OPERATOR_ASSIGNMENT,
        OPERATOR_EXCLAME_EQUAL,
        OPERATOR_GREATER,
        OPERATOR_LESS,
        TYPE_INT,
        TYPE_DOUBLE,
        TYPE_STRING,
        LITERAL_FALSE,
        LITERAL_TRUE,
        LITERAL_NULL,
        RETURN,
        FUNCTION,
        CLASS,
        IF,
        WHILE,
        ELSE,
        COLON,
        SEMICOLON,
        COMMA,
        IDENTIFIER;

        public boolean isSugar() {
            return this == BLOCK_COMMENT || this == LINE_COMMENT || this == NEW_LINE || this == TAB
                    || this == WHITE_SPACE;
        }
    }

    private Type type;
    private final int indexInString;
    private final int indexString;

    private final String tokenString;

    Token(int indexString, int beginIndex, String tokenString, Type type) {
        this.indexString = indexString;
        this.indexInString = beginIndex;
        this.type = type;
        this.tokenString = tokenString;
    }

    public int getIndexInString() {
        return this.indexInString;
    }

    public int getIndexString() {
        return this.indexString;
    }

    public int getEndTokenIndex() {
        return this.indexInString + this.tokenString.length();
    }

    public String getTokenString() {
        return this.tokenString;
    }

    public Type getType() {
        return this.type;
    }
}
