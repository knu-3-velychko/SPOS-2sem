import java.io.File

class Main {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val lexer = Lexer(Tokens(), File("src/main/resources/input.txt"))
            lexer.analyze()
        }
    }
}