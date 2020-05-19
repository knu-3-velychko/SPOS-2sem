package lexer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexer {
    private final Map<Token.Type, String> tokenRegex;

    private final List<Token> tokens;

    public Lexer() {
        tokenRegex = new TreeMap<>();
        initTokenRegex();
        tokens = new ArrayList<>();
    }

    public List<Token> getTokens() {
        return this.tokens;
    }

    public void tokenize(String code, int stringIndex) throws Exception {
        int stringPosition = 0;
        Token token;

        do {
            token = getToken(code, stringPosition, stringIndex);
            if (token != null) {
                stringPosition = token.getEndTokenIndex();
                tokens.add(token);
            }
        } while (token != null && stringPosition != code.length());

        if (stringPosition != code.length()) {
            throw new Exception("Lexer error at [" + stringIndex + "; " + stringPosition + "]");
        }
    }

    private Token getToken(String code, int fromIndex, int stringIndex) throws IllegalAccessException {
        if (fromIndex < 0 || fromIndex >= code.length())
            throw new IllegalAccessException("Index [" + fromIndex + "] is out of boundaries of [0.." + (code.length() - 1) + "]");

        for (var type : Token.Type.values()) {
            Pattern pattern = Pattern.compile(".{" + fromIndex + "}" + tokenRegex.get(type), Pattern.DOTALL);
            Matcher matcher = pattern.matcher(code);

            if (type == Token.Type.NEW_LINE && matcher.matches())
                return new Token(stringIndex, fromIndex, "\n", type);

            if (matcher.matches()) {
                return new Token(
                        stringIndex,
                        fromIndex,
                        matcher.group(1),
                        type
                );
            }
        }

        return null;
    }
    
    private void initTokenRegex() {
        //        tokenRegex.put(Token.Type.NEW_LINE, "(\\n).*");
        tokenRegex.put(Token.Type.NEW_LINE, "\\n");
        tokenRegex.put(Token.Type.BLOCK_COMMENT, "(/\\*.*?\\*/).*");
        tokenRegex.put(Token.Type.LINE_COMMENT, "(//.*).*");
        tokenRegex.put(Token.Type.WHITE_SPACE, "( ).*");
        tokenRegex.put(Token.Type.LEFT_PARENTHESES, "(\\().*");
        tokenRegex.put(Token.Type.RIGHT_PARENTHESES, "(\\)).*");
        tokenRegex.put(Token.Type.VAL, "\\b(val)\\b.*");
        tokenRegex.put(Token.Type.VAR, "\\b(var)\\b.*");
        tokenRegex.put(Token.Type.SEMICOLON, "(;).*");
        tokenRegex.put(Token.Type.COLON, "(:).*");
        tokenRegex.put(Token.Type.COMMA, "(,).*");
        tokenRegex.put(Token.Type.LEFT_BRACE, "(\\{).*");
        tokenRegex.put(Token.Type.RIGHT_BRACE, "(\\}).*");
        tokenRegex.put(Token.Type.LITERAL_STRING, "(\".*?\").*");
        tokenRegex.put(Token.Type.LITERAL_DOUBLE, "\\b(\\d{1,9}\\.\\d{1,32})\\b.*");
        tokenRegex.put(Token.Type.LITERAL_INT, "\\b(\\d{1,9})\\b.*");
        tokenRegex.put(Token.Type.ARRAY, "\\b(Array<Int|Double|String>)\\b.*");
        tokenRegex.put(Token.Type.TYPE_STRING, "\\b(String)\\b.*");
        tokenRegex.put(Token.Type.TYPE_INT, "\\b(Int)\\b.*");
        tokenRegex.put(Token.Type.TYPE_DOUBLE, "\\b(Int|Double)\\b.*");
        tokenRegex.put(Token.Type.TAB, "(\\t).*");
        tokenRegex.put(Token.Type.LITERAL_FALSE, "\\b(false)\\b.*");
        tokenRegex.put(Token.Type.LITERAL_TRUE, "\\b(true)\\b.*");
        tokenRegex.put(Token.Type.LITERAL_NULL, "\\b(null)\\b.*");
        tokenRegex.put(Token.Type.RETURN, "\\b(return)\\b.*");
        tokenRegex.put(Token.Type.FUNCTION, "\\b(fun)\\b.*");
        tokenRegex.put(Token.Type.CLASS, "\\b(class)\\b.*");
        tokenRegex.put(Token.Type.IF, "\\b(if)\\b.*");
        tokenRegex.put(Token.Type.ELSE, "\\b(else)\\b.*");
        tokenRegex.put(Token.Type.WHILE, "\\b(while)\\b.*");
        tokenRegex.put(Token.Type.OPERATOR_POINT, "(\\.).*");
        tokenRegex.put(Token.Type.OPERATOR_PLUS, "(\\+{1}).*");
        tokenRegex.put(Token.Type.OPERATOR_MINUS, "(\\-{1}).*");
        tokenRegex.put(Token.Type.OPERATOR_MULTIPLY, "(\\*).*");
        tokenRegex.put(Token.Type.OPERATOR_DIVIDE, "(/).*");
        tokenRegex.put(Token.Type.OPERATOR_EQUAL, "(==).*");
        tokenRegex.put(Token.Type.OPERATOR_ASSIGNMENT, "(=).*");
        tokenRegex.put(Token.Type.OPERATOR_NOT_EQUAL, "(\\!=).*");
        tokenRegex.put(Token.Type.OPERATOR_GREATER, "(>).*");
        tokenRegex.put(Token.Type.OPERATOR_LESS, "(<).*");
        tokenRegex.put(Token.Type.IDENTIFIER, "\\b([a-zA-Z]{1}[0-9a-zA-Z_]{0,31})\\b.*");
    }
}
