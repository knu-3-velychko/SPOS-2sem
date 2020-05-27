package lexer

import java.io.File
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.util.*

class Lexer(filePath: String) {
    private var state = 0
    val tokens: MutableList<Token>
    private var code: String
    private var buffer: StringBuilder
    private var letter = 0

    fun tokenize() {
        code += " "
        letter = 0
        while (letter < code.length) {
            val c = code[letter]
            when (state) {
                -1 -> incorrectState__1(c)
                0 -> startingState_0(c)
                1 -> slash_1(c)
                2, 26 -> identifier_2_26(c)
                3 -> zeroFirst_3(c)
                4 -> nonzeroDigit_4(c)
                5 -> charLiteral_5(c)
                6 -> stringLiteral_6(c)
                7 -> dot_7(c)
                8 -> greater_8(c)
                9 -> less_9(c)
                10 -> ampersand_10(c)
                11 -> singleOperator_11(c)
                12 -> colon_12(c)
                13 -> plus_13(c)
                14 -> minus_14(c)
                15 -> singleLineComment_15(c)
                16 -> multiLineComment_16(c)
                17 -> starInMultiLineComment_17(c)
                18 -> divideEqual_18(c)
                19 -> maybeComment_19(c)
                20 -> pipe_20(c)
                21 -> colonOrSeparator_21(c)
                22 -> greaterGreater_22(c)
                23 -> pointInDigit_23(c)
                24 -> lessLess_24(c)
                25 -> dotDot_25(c)
                27 -> ampersandOrPipe_27(c)
                28 -> dotDotDot_28(c)
                29 -> maybeCommentAfterIdentifier_29(c)
                30 -> maybeEscapeSequence_30(c)
                31 -> maybeEscapeSequenceChar_31(c)
                32 -> expectEndOfChar_32(c)
                33 -> digitInChar_33(c)
                34 -> underlineInDigit_34(c)
                35 -> binaryDigit_35(c)
                36 -> hexDigit_36(c)
                37 -> octalDigit_37(c)
                38 -> underlineInOctal_38(c)
                39 -> underlineInBinary_39(c)
                40 -> underlineInHex_40(c)
                41 -> integerSuffix_41(c)
                42 -> floatSuffix_42(c)
                43 -> underlineInFloat_43(c)
                44 -> errorCharLiteral_44(c)
                else -> {
                    System.err.println("Unknown state$c")
                }
            }
            ++letter
        }
        if (state == 15) {
            addToken(Token.Type.COMMENT, buffer.toString())
            state = 0
        } else if (state == 16 || state == 17 || state == 44) {
            addToken(Token.Type.ERROR, buffer.toString())
            state = 0
        }
    }

    /**
     * State    : 0
     * Buffer   : empty
     */
    private fun startingState_0(c: Char) {
        when {
            c == '/' -> {
                addToBuffer(c, 1)
            }
            Character.isWhitespace(c) -> {
                addToken(Token.Type.WHITESPACE, c)
                state = 0
            }
            Character.isJavaIdentifierStart(c) -> {
                addToBuffer(c, 2)
            }
            c == '0' -> {
                addToBuffer(c, 3)
            }
            Character.isDigit(c) -> {
                addToBuffer(c, 4)
            }
            c == '\'' -> {
                addToBuffer(c, 5)
            }
            c == '\"' -> {
                addToBuffer(c, 6)
            }
            c == '.' -> {
                addToBuffer(c, 7)
            }
            CharacterDeterminer.separator(c) -> {
                addToken(Token.Type.SEPARATOR, c)
                state = 0
            }
            c == '>' -> {
                addToBuffer(c, 8)
            }
            c == '<' -> {
                addToBuffer(c, 9)
            }
            c == '&' -> {
                addToBuffer(c, 10)
            }
            c == '^' || c == '!' || c == '*' || c == '=' || c == '%' -> {
                addToBuffer(c, 11)
            }
            c == ':' -> {
                addToBuffer(c, 12)
            }
            c == '+' -> {
                addToBuffer(c, 13)
            }
            c == '-' -> {
                addToBuffer(c, 14)
            }
            c == '?' || c == '~' -> {
                addToken(Token.Type.OPERATOR, c)
                state = 0
            }
            c == '#' -> {
                addToBuffer(c, -1)
            }
            c == '|' -> {
                addToBuffer(c, 20)
            }
        }
    }

    /**
     * State    : 1
     * Buffer   : /
     * States   : //, / *, /=...
     */
    private fun slash_1(c: Char) = when {
        c == '/' -> {
            addToBuffer(c, 15)
        }
        c == '*' -> {
            addToBuffer(c, 16)
        }
        c == '=' -> {
            addToBuffer(c, 18)
        }
        c == ':' -> {
            addToBuffer(c, 21)
        }
        CharacterDeterminer.operator(c) -> {
            addToBuffer(c, -1)
        }
        else -> {
            addToken(Token.Type.OPERATOR, buffer.toString())
            letter--
            state = 0
        }
    }

    /**
     * State    : 2 or 26
     * Buffer   : $ or _ or 0..9
     */
    private fun identifier_2_26(c: Char) {
        when {
            Character.isJavaIdentifierPart(c) -> {
                addToBuffer(c, 26)
            }
            c == '#' -> {
                addToBuffer(c, -1)
            }
            c == '/' -> {
                addToBuffer(c, 29)
            }

            (Character.isWhitespace(c) || CharacterDeterminer.operator(c) || CharacterDeterminer.separator(c)) -> {
                when {
                    isNullLiteral(buffer.toString()) -> {
                        addToken(Token.Type.NULL_LITERAL, buffer.toString())
                    }
                    isBooleanLiteral(buffer.toString()) -> {
                        addToken(Token.Type.BOOLEAN_LITERAL, buffer.toString())
                    }
                    IsKeyword.parse(buffer.toString()) -> {
                        addToken(Token.Type.KEYWORD, buffer.toString())
                    }
                    else -> {
                        addToken(Token.Type.IDENTIFIER, buffer.toString())
                    }
                }
                state = 0
                letter--
            }
            else -> {
                addToBuffer(c, -1)
            }
        }
    }

    /**
     * State    : 3
     * Buffer   : 0
     */
    private fun zeroFirst_3(c: Char) = when {
        CharacterDeterminer.octal(c) -> {
            addToBuffer(c, 37)
        }
        c == '_' -> {
            addToBuffer(c, 38)
        }
        c == 'b' || c == 'B' -> {
            addToBuffer(c, 35)
        }
        c == 'x' || c == 'X' -> {
            addToBuffer(c, 36)
        }
        c == '.' -> {
            addToBuffer(c, 23)
        }
        c == 'l' || c == 'L' -> {
            addToBuffer(c, 41)
        }
        Character.isJavaIdentifierPart(c) || c == '8' || c == '9' -> {
            addToBuffer(c, -1)
        }
        else -> {
            letter--
            addToken(Token.Type.INT_LITERAL, buffer.toString())
            state = 0
        }
    }

    /**
     * State    : 4
     * Buffer   : 1..9
     * Desc     : non-zero digit
     */
    private fun nonzeroDigit_4(c: Char) = when {
        c == '_' -> {
            addToBuffer(c, 34)
        }
        Character.isDigit(c) -> {
            addToBuffer(c, 4)
        }
        Character.isJavaIdentifierPart(c) -> {
            addToBuffer(c, -1)
        }
        c == '.' -> {
            addToBuffer(c, 23)
        }
        c == 'l' || c == 'L' -> {
            addToBuffer(c, 41)
        }
        Character.isJavaIdentifierPart(c) -> {
            addToBuffer(c, -1)
        }
        else -> {
            addToken(Token.Type.INT_LITERAL, buffer.toString())
            letter--
            state = 0
        }
    }

    /**
     * State    : 5
     * Buffer   : '
     */
    private fun charLiteral_5(c: Char) = when {
        c == '\\' -> {
            addToBuffer(c, 31)
        }
        Character.isWhitespace(c) && c != ' ' && c != '\t' -> {
            addToken(Token.Type.ERROR, buffer.toString())
            addToken(Token.Type.WHITESPACE, c)
            state = 0
        }
        else -> {
            addToBuffer(c, 32)
        }
    }

    /**
     * State    : 6
     * Buffer   : "
     */
    private fun stringLiteral_6(c: Char) = when {
        c == '\\' -> {
            addToBuffer(c, 30)
        }
        c == '\"' -> {
            buffer.append(c)
            addToken(Token.Type.STRING_LITERAL, buffer.toString())
            state = 0
        }
        Character.isWhitespace(c) && c != ' ' && c != '\t' -> {
            addToken(Token.Type.ERROR, buffer.toString())
            addToken(Token.Type.WHITESPACE, c)
            state = 0
        }
        else -> {
            addToBuffer(c, 6)
        }
    }

    /**
     * State    : 7
     * Buffer   : .
     */
    private fun dot_7(c: Char) = when {
        Character.isDigit(c) -> {
            addToBuffer(c, 23)
        }
        c == '.' -> {
            addToBuffer(c, 25)
        }
        else -> {
            letter--
            addToken(Token.Type.SEPARATOR, buffer.toString())
            state = 0
        }
    }

    /**
     * State    : 8
     * Buffer   : >
     */
    private fun greater_8(c: Char) = when {
        c == '=' -> {
            addToBuffer(c, 18)
        }
        c == ':' -> {
            addToBuffer(c, 21)
        }
        c == '>' -> {
            addToBuffer(c, 22)
        }
        c == '/' -> {
            addToBuffer(c, 19)
        }
        CharacterDeterminer.operator(c) -> {
            addToBuffer(c, -1)
        }
        else -> {
            addToken(Token.Type.OPERATOR, buffer.toString())
            letter--
            state = 0
        }
    }

    /**
     * State    : 9
     * Buffer   : <
     */
    private fun less_9(c: Char) = when {
        c == '=' -> {
            addToBuffer(c, 18)
        }
        c == ':' -> {
            addToBuffer(c, 21)
        }
        c == '<' -> {
            addToBuffer(c, 24)
        }
        c == '/' -> {
            addToBuffer(c, 19)
        }
        CharacterDeterminer.operator(c) -> {
            addToBuffer(c, -1)
        }
        else -> {
            addToken(Token.Type.OPERATOR, buffer.toString())
            letter--
            state = 0
        }
    }

    /**
     * State    : 10
     * Buffer   : &
     */
    private fun ampersand_10(c: Char) = when {
        c == '&' -> {
            addToBuffer(c, 27)
        }
        c == '=' -> {
            addToBuffer(c, 18)
        }
        c == ':' -> {
            addToBuffer(c, 21)
        }
        c == '/' -> {
            addToBuffer(c, 19)
        }
        CharacterDeterminer.operator(c) -> {
            addToBuffer(c, -1)
        }
        else -> {
            letter--
            addToken(Token.Type.OPERATOR, buffer.toString())
            state = 0
        }
    }

    /**
     * State    : 11
     * Buffer   : OPERATOR
     * States   :
     * Desc     :
     */
    private fun singleOperator_11(c: Char) {
        if (c == '=') {
            addToBuffer(c, 18)
        } else if (c == ':') {
            addToBuffer(c, 21)
        } else if (c == '/') {
            addToBuffer(c, 19)
        } else if (CharacterDeterminer.operator(c)) {
            addToBuffer(c, -1)
        } else {
            letter--
            addToken(Token.Type.OPERATOR, buffer.toString())
            state = 0
        }
    }

    /**
     * State    : 12
     * Buffer   : :
     */
    private fun colon_12(c: Char) {
        if (c == ':') {
            addToken(Token.Type.SEPARATOR, "::")
            state = 0
        } else if (CharacterDeterminer.operator(c)) {
            addToBuffer(c, -1)
        } else {
            addToken(Token.Type.OPERATOR, buffer.toString())
            state = 0
            letter--
        }
    }

    /**
     * State    : 13
     * Buffer   : +
     */
    private fun plus_13(c: Char) = when {
        c == '+' -> {
            addToBuffer(c, 11)
        }
        c == ':' -> {
            addToBuffer(c, 21)
        }
        c == '=' -> {
            addToBuffer(c, 18)
        }
        c == '/' -> {
            addToBuffer(c, 19)
        }
        CharacterDeterminer.operator(c) -> {
            addToBuffer(c, -1)
        }
        else -> {
            letter--
            addToken(Token.Type.OPERATOR, buffer.toString())
            state = 0
        }
    }

    /**
     * State    : 14
     * Buffer   : -
     */
    private fun minus_14(c: Char) = when {
        c == '-' -> {
            addToBuffer(c, 11)
        }
        c == ':' -> {
            addToBuffer(c, 21)
        }
        c == '=' -> {
            addToBuffer(c, 18)
        }
        c == '/' -> {
            addToBuffer(c, 19)
        }
        c =='>'->{
            addToBuffer(c,18)
        }
        CharacterDeterminer.operator(c) -> {
            addToBuffer(c, -1)
        }
        else -> {
            letter--
            addToken(Token.Type.OPERATOR, buffer.toString())
            state = 0
        }
    }

    /**
     * State    : 15
     * Buffer   : //
     * Desc     : single-line comment
     */
    private fun singleLineComment_15(c: Char) = if (Character.isWhitespace(c) && c != '\t' && c != ' ') {
        addToken(Token.Type.COMMENT, buffer.toString())
        addToken(Token.Type.WHITESPACE, c)
        state = 0
    } else {
        addToBuffer(c, 15)
    }

    /**
     * State    : 16
     * Buffer   : / *
     */
    private fun multiLineComment_16(c: Char) = if (c == '*') {
        addToBuffer(c, 17)
    } else {
        addToBuffer(c, 16)
    }

    /**
     * State    : 17
     * Buffer   : / *....*
     */
    private fun starInMultiLineComment_17(c: Char) = if (c == '/') {
        addToBuffer(c, 0)
        addToken(Token.Type.COMMENT, buffer.toString())
    } else {
        addToBuffer(c, 16)
    }

    /**
     * State    : 18
     * Buffer   : OPERATOR(=)
     */
    private fun divideEqual_18(c: Char) = when {
        c == '/' -> {
            addToBuffer(c, 19)
        }
        c == ':' -> {
            addToBuffer(c, 21)
        }
        CharacterDeterminer.operator(c) -> {
            addToBuffer(c, -1)
        }
        else -> {
            letter--
            addToken(Token.Type.OPERATOR, buffer.toString())
            state = 0
        }
    }

    /**
     * State    : 19
     * Buffer   : OPERATOR(/)
     */
    private fun maybeComment_19(c: Char) = if (c == '/' || c == '*') {
        buffer.deleteCharAt(buffer.length - 1)
        addToken(Token.Type.OPERATOR, buffer.toString())
        buffer.append('/')
        if (c == '/') {
            addToBuffer(c, 15)
        } else {
            addToBuffer(c, 16)
        }
    } else {
        addToBuffer(c, -1)
    }

    /**
     * State    : 20
     * Buffer   : |
     */
    private fun pipe_20(c: Char) = when {
        c == '|' -> {
            addToBuffer(c, 28)
        }
        c == '=' -> {
            addToBuffer(c, 18)
        }
        c == ':' -> {
            addToBuffer(c, 21)
        }
        c == '/' -> {
            addToBuffer(c, 19)
        }
        CharacterDeterminer.operator(c) -> {
            addToBuffer(c, -1)
        }
        else -> {
            letter--
            addToken(Token.Type.OPERATOR, buffer.toString())
            state = 0
        }
    }

    /**
     * State    : 21
     * Buffer   : OPERATOR(:)
     */
    private fun colonOrSeparator_21(c: Char) {
        state = if (c == ':') {
            buffer.deleteCharAt(buffer.length - 1)
            addToken(Token.Type.OPERATOR, buffer.toString())
            addToken(Token.Type.SEPARATOR, "::")
            0
        } else {
            letter--
            -1
        }
    }

    /**
     * State    : 22
     * Buffer   : >>
     */
    private fun greaterGreater_22(c: Char) = when {
        c == '=' -> {
            addToBuffer(c, 18)
        }
        c == ':' -> {
            addToBuffer(c, 21)
        }
        c == '>' -> {
            addToBuffer(c, 11)
        }
        c == '/' -> {
            addToBuffer(c, 19)
        }
        CharacterDeterminer.operator(c) -> {
            addToBuffer(c, -1)
        }
        else -> {
            addToken(Token.Type.OPERATOR, buffer.toString())
            letter--
            state = 0
        }
    }

    /**
     * State    : 23
     * Buffer   : number
     */
    private fun pointInDigit_23(c: Char) = when {
        Character.isDigit(c) -> {
            addToBuffer(c, 23)
        }
        CharacterDeterminer.doubleOrFloat(c) -> {
            addToBuffer(c, 42)
        }
        Character.isJavaIdentifierPart(c) || c == '.' -> {
            addToBuffer(c, -1)
        }
        c == '_' -> {
            addToBuffer(c, 43)
        }
        else -> {
            letter--
            addToken(Token.Type.FLOAT_LITERAL, buffer.toString())
            state = 0
        }
    }

    /**
     * State    : 24
     * Buffer   : <<
     */
    private fun lessLess_24(c: Char) = when {
        c == '=' -> {
            addToBuffer(c, 18)
        }
        c == ':' -> {
            addToBuffer(c, 21)
        }
        c == '<' -> {
            addToBuffer(c, 11)
        }
        c == '/' -> {
            addToBuffer(c, 19)
        }
        CharacterDeterminer.operator(c) -> {
            addToBuffer(c, -1)
        }
        else -> {
            addToken(Token.Type.OPERATOR, buffer.toString())
            letter--
            state = 0
        }
    }

    /**
     * State    : 25
     * Buffer   : ..
     */
    private fun dotDot_25(c: Char) = if (c == '.') {
        addToBuffer(c, 28)
    } else {
        addToken(Token.Type.SEPARATOR, buffer[0])
        addToken(Token.Type.SEPARATOR, buffer[1])
        state = 0
    }

    /**
     * State    : 27
     * Buffer   : && or ||
     * States   :
     * Desc     :
     */
    private fun ampersandOrPipe_27(c: Char) = when {
        c == ':' -> {
            addToBuffer(c, 21)
        }
        c == '/' -> {
            addToBuffer(c, 19)
        }
        CharacterDeterminer.operator(c) -> {
            addToBuffer(c, -1)
        }
        else -> {
            letter--
            addToken(Token.Type.OPERATOR, buffer.toString())
            state = 0
        }
    }

    /**
     * State    : 28
     * Buffer   : ...
     */
    private fun dotDotDot_28(c: Char) = if (c == '.') {
        addToBuffer(c, -1)
    } else {
        letter--
        addToken(Token.Type.SEPARATOR, buffer.toString())
        state = 0
    }

    /**
     * State    : 29
     * Buffer   : INDENTIFIER(/)
     * States   :
     * Desc     :
     */
    private fun maybeCommentAfterIdentifier_29(c: Char) = if (c == '/' || c == '*') {
        buffer.deleteCharAt(buffer.length - 1)
        when {
            isNullLiteral(buffer.toString()) -> {
                addToken(Token.Type.NULL_LITERAL, buffer.toString())
            }
            isBooleanLiteral(buffer.toString()) -> {
                addToken(Token.Type.BOOLEAN_LITERAL, buffer.toString())
            }
            IsKeyword.parse(buffer.toString()) -> {
                addToken(Token.Type.KEYWORD, buffer.toString())
            }
            else -> {
                addToken(Token.Type.IDENTIFIER, buffer.toString())
            }
        }
        buffer.append('/')
        if (c == '/') {
            addToBuffer(c, 15)
        } else {
            addToBuffer(c, 16)
        }
    } else {
        letter -= 2
        buffer.deleteCharAt(buffer.length - 1)
        when {
            isNullLiteral(buffer.toString()) -> {
                addToken(Token.Type.NULL_LITERAL, buffer.toString())
            }
            isBooleanLiteral(buffer.toString()) -> {
                addToken(Token.Type.BOOLEAN_LITERAL, buffer.toString())
            }
            IsKeyword.parse(buffer.toString()) -> {
                addToken(Token.Type.KEYWORD, buffer.toString())
            }
            else -> {
                addToken(Token.Type.IDENTIFIER, buffer.toString())
            }
        }
        state = 0
    }

    /**
     * State    : 30
     * Buffer   : "STRING\
     */
    private fun maybeEscapeSequence_30(c: Char) = if (CharacterDeterminer.special("\\" + c)) {
        addToBuffer(c, 6)
    } else {
        addToBuffer(c, -1)
    }

    /**
     * State    : 31
     * Buffer   : '\
     */
    private fun maybeEscapeSequenceChar_31(c: Char) = when {
        Character.isDigit(c) -> {
            addToBuffer(c, 33)
        }
        CharacterDeterminer.special("\\" + c) -> {
            addToBuffer(c, 32)
        }
        else -> {
            addToBuffer(c, 44)
        }
    }

    /**
     * State    : 32
     * Buffer   : 'CHAR
     */
    private fun expectEndOfChar_32(c: Char) = if (c == '\'') {
        buffer.append(c)
        addToken(Token.Type.CHAR_LITERAL, buffer.toString())
        state = 0
    } else {
        addToBuffer(c, 44)
    }

    /**
     * State    : 33
     * Buffer   : '\DIGIT
     */
    private fun digitInChar_33(c: Char) = when {
        Character.isDigit(c) -> {
            addToBuffer(c, 33)
        }
        c == '\'' -> {
            addToBuffer(c, 0)
            addToken(Token.Type.CHAR_LITERAL, buffer.toString())
        }
        else -> {
            addToBuffer(c, 44)
        }
    }

    /**
     * State    : 34
     * Buffer   : 0..9+
     * Desc     : digits
     */
    private fun underlineInDigit_34(c: Char) = when {
        Character.isDigit(c) -> {
            addToBuffer(c, 4)
        }
        c == '_' -> {
            addToBuffer(c, 34)
        }
        else -> {
            addToBuffer(c, -1)
        }
    }

    /**
     * State    : 35
     * Buffer   : 0..1+
     * Desc     : binary digits
     */
    private fun binaryDigit_35(c: Char) = when {
        CharacterDeterminer.binary(c) -> {
            addToBuffer(c, 35)
        }
        c == '_' -> {
            addToBuffer(c, 39)
        }
        c == 'l' || c == 'L' -> {
            addToBuffer(c, 41)
        }
        Character.isJavaIdentifierPart(c) -> {
            addToBuffer(c, -1)
        }
        else -> {
            letter--
            addToken(Token.Type.INT_LITERAL, buffer.toString())
            state = 0
        }
    }

    /**
     * State    : 36
     * Buffer   : 0..F
     * Desc     : hex digit
     */
    private fun hexDigit_36(c: Char) = when {
        CharacterDeterminer.hex(c) -> {
            addToBuffer(c, 36)
        }
        c == '_' -> {
            addToBuffer(c, 40)
        }
        c == 'l' || c == 'L' -> {
            addToBuffer(c, 41)
        }
        Character.isJavaIdentifierPart(c) -> {
            addToBuffer(c, -1)
        }
        else -> {
            letter--
            addToken(Token.Type.INT_LITERAL, buffer.toString())
            state = 0
        }
    }

    /**
     * State    : 37
     * Buffer   : 0..7+
     * Desc     : octal digits
     */
    private fun octalDigit_37(c: Char) = when {
        c == '_' -> {
            addToBuffer(c, 38)
        }
        CharacterDeterminer.octal(c) -> {
            addToBuffer(c, 37)
        }
        c == 'l' || c == 'L' -> {
            addToBuffer(c, 41)
        }
        Character.isJavaIdentifierPart(c) || c == '8' || c == '9' -> {
            addToBuffer(c, -1)
        }
        else -> {
            letter--
            addToken(Token.Type.INT_LITERAL, buffer.toString())
            state = 0
        }
    }

    /**
     * State    : 38
     * Buffer   : 0..7+_
     * Desc     : octal digits with _
     */
    private fun underlineInOctal_38(c: Char) = when {
        CharacterDeterminer.octal(c) -> {
            addToBuffer(c, 37)
        }
        c == '_' -> {
            addToBuffer(c, 38)
        }
        else -> {
            addToBuffer(c, -1)
        }
    }

    /**
     * State    : 39
     * Buffer   : 0..1
     * Desc     : binary digit
     */
    private fun underlineInBinary_39(c: Char) = when {
        c == '_' -> {
            addToBuffer(c, 39)
        }
        CharacterDeterminer.binary(c) -> {
            addToBuffer(c, 35)
        }
        else -> {
            addToBuffer(c, -1)
        }
    }

    /**
     * State    : 40
     * Buffer   : 0..F+
     * Desc     : hex digit with _
     */
    private fun underlineInHex_40(c: Char) = when {
        c == '_' -> {
            addToBuffer(c, 40)
        }
        CharacterDeterminer.hex(c) -> {
            addToBuffer(c, 36)
        }
        else -> {
            addToBuffer(c, -1)
        }
    }

    /**
     * State    : 41
     * Buffer   : L
     */
    private fun integerSuffix_41(c: Char) = if (Character.isJavaIdentifierPart(c)) {
        addToBuffer(c, -1)
    } else {
        letter--
        addToken(Token.Type.INT_LITERAL, buffer.toString())
        state = 0
    }

    /**
     * State    : 42
     * Buffer   : number+ f or F or d or D
     */
    private fun floatSuffix_42(c: Char) = if (Character.isJavaIdentifierPart(c)) {
        addToBuffer(c, -1)
    } else {
        letter--
        addToken(Token.Type.FLOAT_LITERAL, buffer.toString())
        state = 0
    }

    /**
     * State    : 43
     * Buffer   : float_
     * Desc     : float number plus _
     */
    private fun underlineInFloat_43(c: Char) = when {
        Character.isDigit(c) -> {
            addToBuffer(c, 23)
        }
        c == '_' -> {
            addToBuffer(c, 43)
        }
        else -> {
            addToBuffer(c, -1)
        }
    }

    /**
     * State    : 44
     * Buffer   : 'SYMBOLS
     */
    private fun errorCharLiteral_44(c: Char) = if (c == '\'') {
        addToBuffer(c, 0)
        addToken(Token.Type.ERROR, buffer.toString())
    } else if (Character.isWhitespace(c) && c != ' ' && c != '\t') {
        addToken(Token.Type.ERROR, buffer.toString())
        addToken(Token.Type.WHITESPACE, c)
        state = 0
    } else {
        addToBuffer(c, 44)
    }

    /**
     * State    : -1
     */
    private fun incorrectState__1(c: Char) =
            if (Character.isWhitespace(c) || CharacterDeterminer.separator(c) || c == '.' || CharacterDeterminer.operator(c) && !CharacterDeterminer.operator(buffer[buffer.length - 1])) {
                letter--
                addToken(Token.Type.ERROR, buffer.toString())
                state = 0
            } else if (buffer.length > 0 && buffer[buffer.length - 1] == '/' && (c == '/' || c == '*')) {
                buffer.deleteCharAt(buffer.length - 1)
                addToken(Token.Type.ERROR, buffer.toString())
                buffer.append('/')
                if (c == '/') {
                    addToBuffer(c, 15)
                } else {
                    addToBuffer(c, 16)
                }
            } else {
                addToBuffer(c, -1)
            }

    private fun isBooleanLiteral(value: String): Boolean {
        return if (value.length < 4 || value.length > 5) {
            false
        } else "true" == value || "false" == value
    }

    private fun isNullLiteral(value: String): Boolean {
        return value.length == 4 && "null" == value
    }

    private fun addToken(type: Token.Type, value: String) {
        tokens.add(Token(value, type))
        buffer = StringBuilder()
    }

    private fun addToken(type: Token.Type, value: Char) {
        tokens.add(Token(value.toString(), type))
        buffer = StringBuilder()
    }

    private fun addToBuffer(c: Char, state: Int) {
        buffer.append(c)
        this.state = state
    }

    init {
        tokens = LinkedList()
        buffer = StringBuilder()
        val inputFile = File("src/main/resources/$filePath")
        var bytes: ByteArray? = ByteArray(0)
        try {
            bytes = Files.readAllBytes(inputFile.toPath())
        } catch (e: IOException) {
            e.printStackTrace()
        }
        code = String(bytes!!, StandardCharsets.UTF_8).replace("\r".toRegex(), "")
    }
}