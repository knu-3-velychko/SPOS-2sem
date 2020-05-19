package lexer;

import java.util.List;

public class IsKeyword {
    private static final List<String> keywords = List.of(
            "Boolean", "Char", "Double", "Float", "Int", "Long", "Short",
            "String", "as", "break", "class", "continue", "do", "else", "false",
            "for", "fun", "if", "in", "interface", "is", "null", "object",
            "package", "private", "protected", "public", "return", "super", "this", "throw", "true", "try",
            "typealias", "typeof", "val", "var", "when", "while"
    );

    public static boolean parse(String word) {
        return keywords.contains(word);
    }
}
