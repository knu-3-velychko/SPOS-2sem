package lexer

data class Token(
    val str: String? = null,
    val name: String,
    val type: TokenType,
    val attributeValue: Int = -1
)