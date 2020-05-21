package lexer

import lexer.IsKeyword
import java.util.List

object IsKeyword {
    private val keywords = List.of(
            "Boolean", "Char", "Double", "Float", "Int", "Long", "Short",
            "String", "as", "break", "class", "continue", "do", "else", "false",
            "for", "fun", "if", "in", "interface", "is", "null", "object",
            "package", "private", "protected", "public", "return", "super", "this", "throw", "true", "try",
            "typealias", "typeof", "val", "var", "when", "while"
    )

    fun parse(word: String): Boolean {
        return keywords.contains(word)
    }
}