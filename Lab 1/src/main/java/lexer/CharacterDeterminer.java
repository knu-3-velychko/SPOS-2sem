package lexer;

import java.util.regex.Pattern;

public class CharacterDeterminer {
    public static boolean separator(char c) {
        return c == '(' || c == ')' || c == '{' || c == '}' ||
                c == '[' || c == ']' || c == ';' || c == ',' ||
                c == '@';
    }

    public static boolean operator(char c) {
        return c == '=' || c == '>' || c == '<' || c == '!' ||
                c == '~' || c == ':' || c == '?' || c == '&' ||
                c == '|' || c == '+' || c == '-' || c == '*' ||
                c == '/' || c == '^' || c == '%';
    }

    public static boolean special(String sequence) {
        return "\\b".equals(sequence) || "\\t".equals(sequence) ||
                "\\n".equals(sequence) || "\\".equals(sequence) ||
                "'".equals(sequence) || "\"".equals(sequence) ||
                "\\r".equals(sequence) || "\\f".equals(sequence);
    }

    public static boolean octal(char c) {
        return Pattern.matches("[0-7]", String.valueOf(c));
    }

    public static boolean binary(char c) {
        return c == '0' || c == '1';
    }

    public static boolean hex(char c) {
        return Pattern.matches("\\d|[a-fA-F]", String.valueOf(c));
    }

    public static boolean doubleOrFloat(char c) {
        return c == 'f' || c == 'F' || c == 'd' || c == 'D';
    }
}