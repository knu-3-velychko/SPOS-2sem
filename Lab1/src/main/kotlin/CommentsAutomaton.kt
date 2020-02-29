import java.lang.StringBuilder

//TODO: Refactor me
class CommentsAutomaton : FiniteAutomaton {

    private var analyzed: StringBuilder = StringBuilder()
    private val start = State(false)
    private val currentState = start

    init {
        buildAutomatomaton()
    }

    private fun buildAutomatomaton() {
        val commentStart = State(false)
        start.addTransition("/".toRegex(), commentStart)

        addSingleLineComment(commentStart)
        addMultipleLinesComment(commentStart)
    }

    override fun nextSymbol(symbol: Char) {

    }

    // [some characters]\n
    private fun addSingleLineComment(commentStart: State) {
        var nextState = State(false)
        commentStart.addTransition("/".toRegex(), nextState)

        var currentState = nextState
        nextState = State(false)
        currentState.addTransition(".".toRegex(), nextState)
        nextState.addTransition(".".toRegex(), nextState)

        nextState = State(true)
        currentState.addTransition("\n".toRegex(), nextState)

        currentState = currentState.transition.getOrDefault(".".toRegex(), currentState)
        currentState.addTransition("\n".toRegex(), nextState)
    }

    private fun addMultipleLinesComment(commentStart: State) {
        var nextState = State(false)
        commentStart.addTransition("[*]".toRegex(), nextState)

        var currentState = nextState
        nextState = State(false)
        currentState.addTransition("\n".toRegex(), nextState)
        currentState.addTransition("[^*]".toRegex(), nextState)
        currentState.addTransition("[*]".toRegex(), endOfMultipleLinesComment())

        currentState = nextState
        currentState.addTransition("\n".toRegex(), currentState)
        currentState.addTransition("[^*]".toRegex(), currentState)
        currentState.addTransition("[*]".toRegex(), endOfMultipleLinesComment())
    }

    private fun endOfMultipleLinesComment(): State {
        val state = State(false)
        val endState = State(true)

        state.addTransition("[*]".toRegex(), state)
        state.addTransition("/".toRegex(), endState)

        return state
    }
}