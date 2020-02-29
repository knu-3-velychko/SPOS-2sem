class State(
    val isFinal: Boolean = true
) {
    val transition = hashMapOf<Regex, State>()

    fun addTransition(regex: Regex, state: State) {
        transition[regex] = state
    }
}