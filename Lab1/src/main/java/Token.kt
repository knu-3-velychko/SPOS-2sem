data class Token(
    val name: String,
    val attributeValue: Int? = null,
    val afterToken: Regex="\\w+".toRegex()
)