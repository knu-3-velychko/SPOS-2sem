package xml

import org.w3c.dom.Document
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

class TokenParser(val file: File) {
    private val document: Document

    init {
        val dbFactory = DocumentBuilderFactory.newInstance()
        val dBuilder = dbFactory.newDocumentBuilder()
        document = dBuilder.parse(file)
        document.documentElement.normalize()
    }

    fun parseNext() {
        val nodeList = document.getElementsByTagName("token")
    }
}