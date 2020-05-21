import com.hp.gagawa.java.elements.*
import lexer.Lexer
import lexer.Token
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.PrintWriter
import java.util.stream.Collectors

class LexerWrapper(fileName: String) {
    private val tokens: List<Token>
    fun groupedTokens() {
        val mappedTokens = tokens.stream().collect(Collectors.groupingBy { obj: Token -> obj.type })
        mappedTokens.forEach { (key: Token.Type, value: List<Token>) ->
            if (key != Token.Type.WHITESPACE) {
                println("$key : ")
                for (`val` in value) {
                    println("\t" + `val`.tokenString)
                }
            }
        }
    }

    fun printTokens() {
        var i = 0
        for (token in tokens) {
            println((++i).toString() + "   " + token.toString() + "\n")
        }
    }

    private var out: PrintWriter? = null

    private var html: Html? = null
    private var body: Body? = null

    fun toHtml() {
        try {
            val file = File("./src/main/resources/index.html")
            out = PrintWriter(FileOutputStream(file))
            build()
            addTokens()
            finish()
        } catch (ex: FileNotFoundException) {
            ex.printStackTrace()
        }
    }

    private fun build() {
        html = Html()
        val head = Head()
        val title = Title().appendChild(Text("Reporting Page"))
        head.appendChild(title)
        val link = Link().setRel("stylesheet").setHref("style.css")
        head.appendChild(link)
        html!!.appendChild(head)
        body = Body()
    }

    private fun addTokens() {
        for (token in tokens) {
            val span = Span().setCSSClass(token.type.toString().toLowerCase()).appendText(token.tokenString)
            body!!.appendChild(span)
        }
    }

    private fun finish() {
        html!!.appendChild(body)
        out!!.println(html!!.write())
        out!!.close()
    }


    init {
        val lexer = Lexer(fileName)
        lexer.tokenize()
        tokens = lexer.tokens
    }
}