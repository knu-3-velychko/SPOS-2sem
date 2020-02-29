import java.io.File

class Lexer(private val tokens: Tokens, val file: File) {
    val commentsAutomaton = CommentsAutomaton()

    fun analyze() {
        file.forEachLine {
            val string = it + "\n"
            string.forEach {
                if (commentsAutomaton.nextSymbol(it)) {
                    if (commentsAutomaton.finished()) {
                        println(commentsAutomaton.getRecognizedString())
                        commentsAutomaton.reset()
                    }
                }
            }
        }
    }
}