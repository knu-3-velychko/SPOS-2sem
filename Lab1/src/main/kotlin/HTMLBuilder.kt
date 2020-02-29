import kotlinx.html.*
import kotlinx.html.stream.appendHTML
import java.io.FileOutputStream
import java.io.PrintStream


class HTMLBuilder {
    fun build() {
        val fout = FileOutputStream("src/main/resources/index.html")
        val pout = PrintStream(fout)

        pout.appendHTML().html {
            head {
                meta { charset = "utf-8" }
                link(rel = "stylesheet",type="text/css", href = "style.css")
            }
            body {
                div {
                    p {
                        id = "hello"
                        classes = setOf("comment")
                        text("Hello")
                    }
                }
            }
        }
        pout.close()
        fout.close()
    }
}