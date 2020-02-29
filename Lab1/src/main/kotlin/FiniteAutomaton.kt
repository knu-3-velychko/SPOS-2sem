interface FiniteAutomaton {
    fun nextSymbol(char: Char): Boolean
    fun getRecognizedString(): String
    fun reset()
    fun finished():Boolean
}