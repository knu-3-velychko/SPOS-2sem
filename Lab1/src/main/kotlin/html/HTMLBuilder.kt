package html

import kotlinx.html.*
import kotlinx.html.stream.appendHTML
import lexer.Token
import java.io.FileOutputStream
import java.io.PrintStream

class HTMLBuilder(val path: String) {
    private val fileStream = FileOutputStream(path)
    private val printStream = PrintStream(fileStream)

    fun addHead() {
        printStream.appendHTML().html {
            head {
                meta { charset = "utf-8" }
                link(rel = "stylesheet", type = "text/css", href = "style.css")
            }
        }
    }

    fun addToken(token: Token) {
        if (token.attributeValue == -1) {
            printStream.appendHTML().html {
                body {
                    div {
                        id=token.name
                        text(token.name)    //FIXME: get token name
                    }
                }
            }
        }
    }

    fun close() {
        printStream.close()
        fileStream.close()
    }
}