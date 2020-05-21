object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        val lexer = LexerWrapper("kotlin.txt")
        lexer.printTokens()
        lexer.toHtml()
        lexer.groupedTokens()
    }
}