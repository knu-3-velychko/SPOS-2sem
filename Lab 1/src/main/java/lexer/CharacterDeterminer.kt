package lexer

import java.util.regex.Pattern

object CharacterDeterminer {
    fun separator(c: Char): Boolean {
        return c == '(' || c == ')' || c == '{' || c == '}' || c == '[' || c == ']' || c == ';' || c == ',' || c == '@'
    }

    fun operator(c: Char): Boolean {
        return c == '=' || c == '>' || c == '<' || c == '!' || c == '~' || c == ':' || c == '?' || c == '&' || c == '|' || c == '+' || c == '-' || c == '*' || c == '/' || c == '^' || c == '%'
    }

    fun special(sequence: String): Boolean {
        return "\\b" == sequence || "\\t" == sequence || "\\n" == sequence || "\\" == sequence || "'" == sequence || "\"" == sequence || "\\r" == sequence || "\\f" == sequence
    }

    fun octal(c: Char): Boolean {
        return Pattern.matches("[0-7]", c.toString())
    }

    fun binary(c: Char): Boolean {
        return c == '0' || c == '1'
    }

    fun hex(c: Char): Boolean {
        return Pattern.matches("\\d|[a-fA-F]", c.toString())
    }

    fun doubleOrFloat(c: Char): Boolean {
        return c == 'f' || c == 'F' || c == 'd' || c == 'D'
    }
}