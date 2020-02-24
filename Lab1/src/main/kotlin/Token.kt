data class Token(
    val name: String,
    val type: Type,
    val attributeValue: Int? = null,
    val afterToken: Regex = "\\w+".toRegex()
)