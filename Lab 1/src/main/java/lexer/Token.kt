package lexer

class Token internal constructor(val tokenString: String, val type: Type) {
    enum class Type {
        COMMENT, WHITESPACE, IDENTIFIER, OPERATOR, SEPARATOR, INT_LITERAL, FLOAT_LITERAL, CHAR_LITERAL, STRING_LITERAL, BOOLEAN_LITERAL, NULL_LITERAL, KEYWORD, ERROR
    }

    override fun toString(): String {
        return "Token{" +
                "type=" + type +
                ", tokenString='" + tokenString + '\'' +
                '}'
    }

}