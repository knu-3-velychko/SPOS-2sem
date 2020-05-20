package lexer;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.LinkedList;
import java.util.List;

public class Lexer {
    private int state;
    private List<Token> tokens;
    private String code;
    private StringBuilder buffer;
    private int letter;

    public List<Token> getTokens() {
        return tokens;
    }

    public Lexer(final String filePath) {
        state = 0;
        tokens = new LinkedList<>();
        buffer = new StringBuilder();

        File input_file = new File("src/main/resources/" + filePath);
        byte[] bytes = new byte[0];
        try {
            bytes = Files.readAllBytes(input_file.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        code = new String(bytes, StandardCharsets.UTF_8).replaceAll("\r", "");
    }

    public void tokenize() {
        code += " ";
        for(letter = 0; letter < code.length(); ++letter) {
            char c = code.charAt(letter);
            switch (state) {
                case -1: incorrectState__1(c); break;
                case 0: startingState_0(c); break;
                case 1: slash_1(c); break;
                case 2: identifier_2_26(c); break;
                case 3: zeroFirst_3(c); break;
                case 4: nonzeroDigit_4(c); break;
                case 5: charLiteral_5(c); break;
                case 6: stringLiteral_6(c); break;
                case 7: dot_7(c); break;
                case 8: greater_8(c); break;
                case 9: less_9(c); break;
                case 10: ampersand_10(c); break;
                case 11: singleOperator_11(c); break;
                case 12: colon_12(c); break;
                case 13: plus_13(c); break;
                case 14: minus_14(c); break;
                case 15: singleLineComment_15(c); break;
                case 16: multiLineComment_16(c); break;
                case 17: starInMultiLineComment_17(c); break;
                case 18: divideEqual_18(c); break;
                case 19: maybeComment_19(c); break;
                case 20: pipe_20(c); break;
                case 21: colonOrSeparator_21(c); break;
                case 22: greaterGreater_22(c); break;
                case 23: pointInDigit_23(c); break;
                case 24: lessLess_24(c); break;
                case 25: dotDot_25(c); break;
                case 26: identifier_2_26(c); break;
                case 27: ampersandOrPipe_27(c); break;
                case 28: dotDotDot_28(c); break;
                case 29: maybeCommentAfterIdentifier_29(c); break;
                case 30: maybeEscapeSequence_30(c); break;
                case 31: maybeEscapeSequenceChar_31(c); break;
                case 32: expectEndOfChar_32(c); break;
                case 33: digitInChar_33(c); break;
                case 34: underlineInDigit_34(c); break;
                case 35: binaryDigit_35(c); break;
                case 36: hexDigit_36(c); break;
                case 37: octalDigit_37(c); break;
                case 38: underlineInOctal_38(c); break;
                case 39: underlineInBinary_39(c); break;
                case 40: underlineInHex_40(c); break;
                case 41: integerSuffix_41(c); break;
                case 42: floatSuffix_42(c); break;
                case 43: underlineInFloat_43(c); break;
                case 44: errorCharLiteral_44(c); break;
                default: {
                    System.err.println("Unknown state" + c);
                    break;
                }
            }
        }
        if(state == 15) {
            addToken(Token.Type.COMMENT, buffer.toString());
            state = 0;
        } else if(state == 16 || state == 17 || state == 44) {
            addToken(Token.Type.ERROR, buffer.toString());
            state = 0;
        }
    }

    /**
     * State    : 0
     * Buffer   : empty
     */
    private void startingState_0(char c) {
        if(c == '/') {
            addToBuffer(c, 1);
        } else if(Character.isWhitespace(c)) {
            addToken(Token.Type.WHITESPACE, c);
            state = 0;
        } else if(Character.isJavaIdentifierStart(c)) {
            addToBuffer(c, 2);
        } else if(c == '0') {
            addToBuffer(c, 3);
        } else if(Character.isDigit(c)) {
            addToBuffer(c, 4);
        } else if(c == '\'') {
            addToBuffer(c, 5);
        } else if(c == '\"') {
            addToBuffer(c, 6);
        } else if(c == '.') {
            addToBuffer(c, 7);
        } else if(CharacterDeterminer.separator(c)) {
            addToken(Token.Type.SEPARATOR, c);
            state = 0;
        } else if(c == '>') {
            addToBuffer(c, 8);
        } else if(c == '<') {
            addToBuffer(c, 9);
        } else if(c == '&') {
            addToBuffer(c, 10);
        } else if(c == '^' || c == '!' || c == '*' || c == '=' || c == '%') {
            addToBuffer(c, 11);
        } else if(c == ':') {
            addToBuffer(c, 12);
        } else if(c == '+') {
            addToBuffer(c, 13);
        } else if(c == '-') {
            addToBuffer(c, 14);
        } else if(c == '?' || c == '~') {
            addToken(Token.Type.OPERATOR, c);
            state = 0;
        } else if(c == '#') {
            addToBuffer(c, -1);
        } else if(c == '|') {
            addToBuffer(c, 20);
        } else {
        }
    }

    /**
     * State    : 1
     * Buffer   : /
     * States   : //, /*, /=...
     */
    private void slash_1(char c) {
        if(c == '/') {
            addToBuffer(c, 15);
        } else if(c == '*') {
            addToBuffer(c, 16);
        } else if(c == '=') {
            addToBuffer(c, 18);
        } else if(c == ':') {
            addToBuffer(c, 21);
        } else if(CharacterDeterminer.operator(c)) {
            addToBuffer(c, -1);
        } else {
            addToken(Token.Type.OPERATOR, buffer.toString());
            letter--;
            state = 0;
        }
    }

    /**
     * State    : 2 or 26
     * Buffer   : $ or _ or 0..9
     */
    private void identifier_2_26(char c) {
        if(Character.isJavaIdentifierPart(c)) {
            addToBuffer(c, 26);
        } else if(c == '#') {
            addToBuffer(c, -1);
        } else if(c == '/') {
            addToBuffer(c, 29);
        } else {
            if(isNullLiteral(buffer.toString())) {
                addToken(Token.Type.NULL_LITERAL, buffer.toString());
            } else if(isBooleanLiteral(buffer.toString())) {
                addToken(Token.Type.BOOLEAN_LITERAL, buffer.toString());
            } else if(IsKeyword.parse(buffer.toString())) {
                addToken(Token.Type.KEYWORD, buffer.toString());
            } else {
                addToken(Token.Type.IDENTIFIER, buffer.toString());
            }
            state = 0;
            letter--;
        }
    }

    /**
     * State    : 3
     * Buffer   : 0
     * */
    private void zeroFirst_3(char c) {
        if(CharacterDeterminer.octal(c)) {
            addToBuffer(c, 37);
        } else if(c == '_') {
            addToBuffer(c, 38);
        } else if(c == 'b' || c == 'B') {
            addToBuffer(c, 35);
        } else if(c == 'x' || c == 'X') {
            addToBuffer(c, 36);
        } else if(c == '.') {
            addToBuffer(c, 23);
        } else if(c == 'l' || c == 'L') {
            addToBuffer(c, 41);
        } else if(Character.isJavaIdentifierPart(c) || c == '8' || c == '9') {
            addToBuffer(c, -1);
        } else {
            letter--;
            addToken(Token.Type.INT_LITERAL, buffer.toString());
            state = 0;
        }
    }

    /**
     * State    : 4
     * Buffer   : 1..9
     * Desc     : non-zero digit
     */
    private void nonzeroDigit_4(char c) {
        if(c == '_') {
            addToBuffer(c, 34);
        } else if(Character.isDigit(c)) {
            addToBuffer(c, 4);
        } else if(Character.isJavaIdentifierPart(c)) {
            addToBuffer(c, -1);
        } else if(c == '.') {
            addToBuffer(c, 23);
        } else if(c == 'l' || c == 'L') {
            addToBuffer(c, 41);
        } else if(Character.isJavaIdentifierPart(c)) {
            addToBuffer(c, -1);
        } else {
            addToken(Token.Type.INT_LITERAL, buffer.toString());
            letter--;
            state = 0;
        }
    }

    /**
     * State    : 5
     * Buffer   : '
     */
    private void charLiteral_5(char c) {
        if(c == '\\') {
            addToBuffer(c, 31);
        } else if(Character.isWhitespace(c) && c != ' ' && c != '\t') {
            addToken(Token.Type.ERROR, buffer.toString());
            addToken(Token.Type.WHITESPACE, c);
            state = 0;
        } else {
            addToBuffer(c, 32);
        }
    }

    /**
     * State    : 6
     * Buffer   : "
     */
    private void stringLiteral_6(char c) {
        if(c == '\\') {
            addToBuffer(c, 30);
        } else if(c == '\"') {
            buffer.append(c);
            addToken(Token.Type.STRING_LITERAL, buffer.toString());
            state = 0;
        } else if(Character.isWhitespace(c) && c != ' ' && c != '\t') {
            addToken(Token.Type.ERROR, buffer.toString());
            addToken(Token.Type.WHITESPACE, c);
            state = 0;
        } else {
            addToBuffer(c, 6);
        }
    }

    /**
     * State    : 7
     * Buffer   : .
     */
    private void dot_7(char c) {
        if(Character.isDigit(c)) {
            addToBuffer(c, 23);
        } else if(c == '.') {
            addToBuffer(c, 25);
        } else {
            letter--;
            addToken(Token.Type.SEPARATOR, buffer.toString());
            state = 0;
        }
    }

    /**
     * State    : 8
     * Buffer   : >
     */
    private void greater_8(char c) {
        if(c == '=') {
            addToBuffer(c, 18);
        } else if(c == ':') {
            addToBuffer(c, 21);
        } else if(c == '>') {
            addToBuffer(c, 22);
        } else if(c == '/') {
            addToBuffer(c, 19);
        } else if(CharacterDeterminer.operator(c)) {
            addToBuffer(c, -1);
        } else {
            addToken(Token.Type.OPERATOR, buffer.toString());
            letter--;
            state = 0;
        }
    }

    /**
     * State    : 9
     * Buffer   : <
     */
    private void less_9(char c) {
        if(c == '=') {
            addToBuffer(c, 18);
        } else if(c == ':') {
            addToBuffer(c, 21);
        } else if(c == '<') {
            addToBuffer(c, 24);
        } else if(c == '/') {
            addToBuffer(c, 19);
        } else if(CharacterDeterminer.operator(c)) {
            addToBuffer(c, -1);
        } else {
            addToken(Token.Type.OPERATOR, buffer.toString());
            letter--;
            state = 0;
        }
    }

    /**
     * State    : 10
     * Buffer   : &
     */
    private void ampersand_10(char c) {
        if(c == '&') {
            addToBuffer(c, 27);
        } else if( c == '=') {
            addToBuffer(c, 18);
        } else if(c == ':') {
            addToBuffer(c, 21);
        } else if(c == '/') {
            addToBuffer(c, 19);
        } else if(CharacterDeterminer.operator(c)) {
            addToBuffer(c, -1);
        } else {
            letter--;
            addToken(Token.Type.OPERATOR, buffer.toString());
            state = 0;
        }
    }

    /**
     * State    : 11
     * Buffer   : OPERATOR
     * States   :
     * Desc     :
     */
    private void singleOperator_11(char c) {
        if(c == '=') {
            addToBuffer(c, 18);
        } else if(c == ':') {
            addToBuffer(c, 21);
        } else if(c == '/') {
            addToBuffer(c, 19);
        } else if(CharacterDeterminer.operator(c)) {
            addToBuffer(c, -1);
        } else {
            letter--;
            addToken(Token.Type.OPERATOR, buffer.toString());
            state = 0;
        }
    }

    /**
     * State    : 12
     * Buffer   : :
     */
    private void colon_12(char c) {
        if(c == ':') {
            addToken(Token.Type.SEPARATOR, "::");
            state = 0;
        } else if(CharacterDeterminer.operator(c)) {
            addToBuffer(c, -1);
        } else {
            addToken(Token.Type.OPERATOR, buffer.toString());
            state = 0;
            letter--;
        }
    }

    /**
     * State    : 13
     * Buffer   : +
     */
    private void plus_13(char c) {
        if(c == '+') {
            addToBuffer(c, 11);
        } else if(c == ':') {
            addToBuffer(c, 21);
        } else if(c == '=') {
            addToBuffer(c, 18);
        } else if(c == '/') {
            addToBuffer(c, 19);
        } else if(CharacterDeterminer.operator(c)) {
            addToBuffer(c, -1);
        } else {
            letter--;
            addToken(Token.Type.OPERATOR, buffer.toString());
            state = 0;
        }
    }

    /**
     * State    : 14
     * Buffer   : -
     */
    private void minus_14(char c) {
        if(c == '-') {
            addToBuffer(c, 11);
        } else if(c == ':') {
            addToBuffer(c, 21);
        } else if(c == '=') {
            addToBuffer(c, 18);
        } else if(c == '/') {
            addToBuffer(c, 19);
        } else if(CharacterDeterminer.operator(c)) {
            addToBuffer(c, -1);
        } else {
            letter--;
            addToken(Token.Type.OPERATOR, buffer.toString());
            state = 0;
        }
    }

    /**
     * State    : 15
     * Buffer   : //
     * Desc     : single-line comment
     */
    private void singleLineComment_15(char c) {
        if(Character.isWhitespace(c) && c != '\t' && c != ' ') {
            addToken(Token.Type.COMMENT, buffer.toString());
            addToken(Token.Type.WHITESPACE, c);
            state = 0;
        } else {
            addToBuffer(c, 15);
        }
    }

    /**
     * State    : 16
     * Buffer   : /*
     */
    private void multiLineComment_16(char c) {
        if(c == '*') {
            addToBuffer(c, 17);
        } else {
            addToBuffer(c, 16);
        }
    }

    /**
     * State    : 17
     * Buffer   : /*....*
     */
    private void starInMultiLineComment_17(char c) {
        if(c == '/') {
            addToBuffer(c, 0);
            addToken(Token.Type.COMMENT, buffer.toString());
        } else {
            addToBuffer(c, 16);
        }
    }

    /**
     * State    : 18
     * Buffer   : OPERATOR(=)
     */
    private void divideEqual_18(char c) {
        if(c == '/') {
            addToBuffer(c, 19);
        } else if(c == ':') {
            addToBuffer(c, 21);
        } else if(CharacterDeterminer.operator(c)) {
            addToBuffer(c, -1);
        } else {
            letter--;
            addToken(Token.Type.OPERATOR, buffer.toString());
            state = 0;
        }
    }

    /**
     * State    : 19
     * Buffer   : OPERATOR(/)
     */
    private void maybeComment_19(char c) {
        if(c == '/' || c == '*') {
            buffer.deleteCharAt(buffer.length() - 1);
            addToken(Token.Type.OPERATOR, buffer.toString());
            buffer.append('/');
            if(c == '/') {
                addToBuffer(c, 15);
            } else {
                addToBuffer(c, 16);
            }
        } else {
            addToBuffer(c, -1);
        }
    }

    /**
     * State    : 20
     * Buffer   : |
     */
    private void pipe_20(char c) {
        if(c == '|') {
            addToBuffer(c, 28);
        } else if( c == '=') {
            addToBuffer(c, 18);
        } else if(c == ':') {
            addToBuffer(c, 21);
        } else if(c == '/') {
            addToBuffer(c, 19);
        } else if(CharacterDeterminer.operator(c)) {
            addToBuffer(c, -1);
        } else {
            letter--;
            addToken(Token.Type.OPERATOR, buffer.toString());
            state = 0;
        }
    }

    /**
     * State    : 21
     * Buffer   : OPERATOR(:)
     */
    private void colonOrSeparator_21(char c) {
        if(c == ':') {
            buffer.deleteCharAt(buffer.length() - 1);
            addToken(Token.Type.OPERATOR, buffer.toString());
            addToken(Token.Type.SEPARATOR, "::");
            state = 0;
        } else {
            letter--;
            state = -1;
        }
    }

    /**
     * State    : 22
     * Buffer   : >>
     */
    private void greaterGreater_22(char c) {
        if(c == '=') {
            addToBuffer(c, 18);
        } else if(c == ':') {
            addToBuffer(c, 21);
        } else if(c == '>') {
            addToBuffer(c, 11);
        }else if(c == '/') {
            addToBuffer(c, 19);
        } else if(CharacterDeterminer.operator(c)) {
            addToBuffer(c, -1);
        } else {
            addToken(Token.Type.OPERATOR, buffer.toString());
            letter--;
            state = 0;
        }
    }

    /**
     * State    : 23
     * Buffer   : number
     */
    private void pointInDigit_23(char c) {
        if(Character.isDigit(c)) {
            addToBuffer(c, 23);
        } else if(CharacterDeterminer.doubleOrFloat(c)) {
            addToBuffer(c, 42);
        } else if(Character.isJavaIdentifierPart(c) || c == '.') {
            addToBuffer(c, -1);
        } else if(c == '_') {
            addToBuffer(c, 43);
        } else {
            letter--;
            addToken(Token.Type.FLOAT_LITERAL, buffer.toString());
            state = 0;
        }
    }

    /**
     * State    : 24
     * Buffer   : <<
     */
    private void lessLess_24(char c) {
        if(c == '=') {
            addToBuffer(c, 18);
        } else if(c == ':') {
            addToBuffer(c, 21);
        } else if(c == '<') {
            addToBuffer(c, 11);
        } else if(c == '/') {
            addToBuffer(c, 19);
        } else if(CharacterDeterminer.operator(c)) {
            addToBuffer(c, -1);
        } else {
            addToken(Token.Type.OPERATOR, buffer.toString());
            letter--;
            state = 0;
        }
    }

    /**
     * State    : 25
     * Buffer   : ..
     */
    private void dotDot_25(char c) {
        if(c == '.') {
            addToBuffer(c, 28);
        } else {
            addToken(Token.Type.SEPARATOR, buffer.charAt(0));
            addToken(Token.Type.SEPARATOR, buffer.charAt(1));
            state = 0;
        }
    }

    /**
     * State    : 27
     * Buffer   : && or ||
     * States   :
     * Desc     :
     */
    private void ampersandOrPipe_27(char c) {
        if(c == ':') {
            addToBuffer(c, 21);
        } else if(c == '/') {
            addToBuffer(c, 19);
        } else if(CharacterDeterminer.operator(c)) {
            addToBuffer(c, -1);
        } else {
            letter--;
            addToken(Token.Type.OPERATOR, buffer.toString());
            state = 0;
        }
    }

    /**
     * State    : 28
     * Buffer   : ...
     */
    private void dotDotDot_28(char c) {
        if(c == '.') {
            addToBuffer(c, -1);
        } else {
            letter--;
            addToken(Token.Type.SEPARATOR, buffer.toString());
            state = 0;
        }
    }

    /**
     * State    : 29
     * Buffer   : INDENTIFIER(/)
     * States   :
     * Desc     :
     */
    private void maybeCommentAfterIdentifier_29(char c) {
        if(c == '/' || c == '*') {
            buffer.deleteCharAt(buffer.length() - 1);
            if(isNullLiteral(buffer.toString())) {
                addToken(Token.Type.NULL_LITERAL, buffer.toString());
            } else if(isBooleanLiteral(buffer.toString())) {
                addToken(Token.Type.BOOLEAN_LITERAL, buffer.toString());
            } else if(IsKeyword.parse(buffer.toString())) {
                addToken(Token.Type.KEYWORD, buffer.toString());
            } else {
                addToken(Token.Type.IDENTIFIER, buffer.toString());
            }
            buffer.append('/');
            if (c == '/') {
                addToBuffer(c, 15);
            } else {
                addToBuffer(c, 16);
            }
        }
        else {
            letter -= 2;
            buffer.deleteCharAt(buffer.length()-1);
            if(isNullLiteral(buffer.toString())) {
                addToken(Token.Type.NULL_LITERAL, buffer.toString());
            } else if(isBooleanLiteral(buffer.toString())) {
                addToken(Token.Type.BOOLEAN_LITERAL, buffer.toString());
            } else if(IsKeyword.parse(buffer.toString())) {
                addToken(Token.Type.KEYWORD, buffer.toString());
            } else {
                addToken(Token.Type.IDENTIFIER, buffer.toString());
            }
            state = 0;
        }
    }

    /**
     * State    : 30
     * Buffer   : "STRING\
     */
    private void maybeEscapeSequence_30(char c) {
        if(CharacterDeterminer.special("\\"+c)) {
            addToBuffer(c, 6);
        } else {
            addToBuffer(c, -1);
        }
    }

    /**
     * State    : 31
     * Buffer   : '\
     */
    private void maybeEscapeSequenceChar_31(char c) {
        if(Character.isDigit(c)) {
            addToBuffer(c, 33);
        } else if(CharacterDeterminer.special("\\" + c)) {
            addToBuffer(c, 32);
        } else {
            addToBuffer(c, 44);
        }
    }

    /**
     * State    : 32
     * Buffer   : 'CHAR
     */
    private void expectEndOfChar_32(char c) {
        if(c == '\'') {
            buffer.append(c);
            addToken(Token.Type.CHAR_LITERAL, buffer.toString());
            state = 0;
        } else {
            addToBuffer(c, 44);
        }
    }

    /**
     * State    : 33
     * Buffer   : '\DIGIT
     */
    private void digitInChar_33(char c) {
        if(Character.isDigit(c)) {
            addToBuffer(c, 33);
        } else if(c == '\'') {
            addToBuffer(c, 0);
            addToken(Token.Type.CHAR_LITERAL, buffer.toString());
        } else {
            addToBuffer(c, 44);
        }
    }

    /**
     * State    : 34
     * Buffer   : 0..9+
     * Desc     : digits
     */
    private void underlineInDigit_34(char c) {
        if(Character.isDigit(c)) {
            addToBuffer(c, 4);
        } else if(c == '_') {
            addToBuffer(c, 34);
        } else {
            addToBuffer(c, -1);
        }
    }

    /**
     * State    : 35
     * Buffer   : 0..1+
     * Desc     : binary digits
     */
    private void binaryDigit_35(char c) {
        if(CharacterDeterminer.binary(c)) {
            addToBuffer(c, 35);
        } else if (c == '_') {
            addToBuffer(c, 39);
        } else if(c == 'l' || c == 'L') {
            addToBuffer(c, 41);
        } else if(Character.isJavaIdentifierPart(c)) {
            addToBuffer(c, -1);
        } else {
            letter--;
            addToken(Token.Type.INT_LITERAL, buffer.toString());
            state = 0;
        }
    }

    /**
     * State    : 36
     * Buffer   : 0..F
     * Desc     : hex digit
     */
    private void hexDigit_36(char c) {
        if(CharacterDeterminer.hex(c)) {
            addToBuffer(c, 36);
        } else if (c == '_') {
            addToBuffer(c, 40);
        } else if(c == 'l' || c == 'L') {
            addToBuffer(c, 41);
        } else if(Character.isJavaIdentifierPart(c)) {
            addToBuffer(c, -1);
        } else {
            letter--;
            addToken(Token.Type.INT_LITERAL, buffer.toString());
            state = 0;
        }
    }

    /**
     * State    : 37
     * Buffer   : 0..7+
     * Desc     : octal digits
     */
    private void octalDigit_37(char c) {
        if(c == '_') {
            addToBuffer(c, 38);
        } else if(CharacterDeterminer.octal(c)) {
            addToBuffer(c, 37);
        } else if(c == 'l' || c == 'L') {
            addToBuffer(c, 41);
        } else if(Character.isJavaIdentifierPart(c) || c == '8' || c == '9') {
            addToBuffer(c, -1);
        } else {
            letter--;
            addToken(Token.Type.INT_LITERAL, buffer.toString());
            state = 0;
        }
    }

    /**
     * State    : 38
     * Buffer   : 0..7+_
     * Desc     : octal digits with _
     */
    private void underlineInOctal_38(char c) {
        if(CharacterDeterminer.octal(c)) {
            addToBuffer(c, 37);
        } else if(c == '_') {
            addToBuffer(c, 38);
        } else {
            addToBuffer(c, -1);
        }
    }

    /**
     * State    : 39
     * Buffer   : 0..1
     * Desc     : binary digit
     */
    private void underlineInBinary_39(char c) {
        if(c == '_') {
            addToBuffer(c, 39);
        } else if(CharacterDeterminer.binary(c)) {
            addToBuffer(c, 35);
        } else {
            addToBuffer(c, -1);
        }
    }

    /**
     * State    : 40
     * Buffer   : 0..F+
     * Desc     : hex digit with _
     */
    private void underlineInHex_40(char c) {
        if(c == '_') {
            addToBuffer(c, 40);
        } else if(CharacterDeterminer.hex(c)) {
            addToBuffer(c, 36);
        } else {
            addToBuffer(c, -1);
        }
    }

    /**
     * State    : 41
     * Buffer   : L
     */
    private void integerSuffix_41(char c) {
        if(Character.isJavaIdentifierPart(c)) {
            addToBuffer(c, -1);
        } else {
            letter--;
            addToken(Token.Type.INT_LITERAL, buffer.toString());
            state = 0;
        }
    }

    /**
     * State    : 42
     * Buffer   : number+ f or F or d or D
     */
    private void floatSuffix_42(char c) {
        if(Character.isJavaIdentifierPart(c)) {
            addToBuffer(c, -1);
        } else {
            letter--;
            addToken(Token.Type.FLOAT_LITERAL, buffer.toString());
            state = 0;
        }
    }

    /**
     * State    : 43
     * Buffer   : float_
     * Desc     : float number plus _
     */
    private void underlineInFloat_43(char c) {
        if(Character.isDigit(c)) {
            addToBuffer(c, 23);
        } else if(c == '_') {
            addToBuffer(c, 43);
        } else {
            addToBuffer(c, -1);
        }
    }

    /**
     * State    : 44
     * Buffer   : 'SYMBOLS
     */
    private void errorCharLiteral_44(char c) {
        if(c == '\'') {
            addToBuffer(c, 0);
            addToken(Token.Type.ERROR, buffer.toString());
        } else if(Character.isWhitespace(c) && c != ' ' && c != '\t') {
            addToken(Token.Type.ERROR, buffer.toString());
            addToken(Token.Type.WHITESPACE, c);
            state = 0;
        } else {
            addToBuffer(c, 44);
        }
    }

    /**
     * State    : -1
     */
    private void incorrectState__1(char c) {
        if(Character.isWhitespace(c) || CharacterDeterminer.separator(c) ||
                c == '.' || (CharacterDeterminer.operator(c) && !CharacterDeterminer.operator(buffer.charAt(buffer.length()-1)))) {
            letter--;
            addToken(Token.Type.ERROR, buffer.toString());
            state = 0;
        } else if(buffer.length() > 0 && buffer.charAt(buffer.length()-1) == '/' && (c == '/' || c == '*')) {
            buffer.deleteCharAt(buffer.length()-1);
            addToken(Token.Type.ERROR, buffer.toString());
            buffer.append('/');
            if(c == '/') {
                addToBuffer(c, 15);
            } else {
                addToBuffer(c, 16);
            }
        } else {
            addToBuffer(c, -1);
        }
    }

    private boolean isBooleanLiteral(String value) {
        if(value.length() < 4 || value.length() > 5) return false;
        return "true".equals(value) || "false".equals(value);
    }

    private boolean isNullLiteral(String value) {
        return value.length() == 4 && "null".equals(value);
    }

    private void addToken(Token.Type type, String value) {
        tokens.add(new Token(value, type));
        buffer = new StringBuilder();
    }

    private void addToken(Token.Type type, char value) {
        tokens.add(new Token(String.valueOf(value), type));
        buffer = new StringBuilder();
    }

    private void addToBuffer(char c, int state) {
        buffer.append(c);
        this.state = state;
    }
}