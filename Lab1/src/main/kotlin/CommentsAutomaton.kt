import java.lang.StringBuilder

//TODO: Refactor me
class CommentsAutomaton : FiniteAutomaton {

    private var recognized: StringBuilder = StringBuilder()
    private val start = State(false)
    private var currentState = start

    init {
        val commentStart = State(false)
        start.addTransition("/".toRegex(), commentStart)

        addSingleLineComment(commentStart)
        addMultipleLinesComment(commentStart)
    }

    override fun nextSymbol(char: Char): Boolean {
        val symbol = char.toString()
        var nextState: State? = null
        for (regex in currentState.transition.keys) {
            if (symbol.matches(regex)) {
                nextState = currentState.transition[regex]
                recognized.append(symbol)
                break
            }
        }
        if (nextState != null) {
            currentState = nextState
            return true
        }
        return false
    }

    override fun getRecognizedString(): String {
        return recognized.toString()
    }

    override fun reset() {
        currentState = start
        recognized.setLength(0)
    }

    override fun finished(): Boolean {
        return currentState.isFinal
    }

    // [some characters]\n
    private fun addSingleLineComment(commentStart: State) {
        var nextState = State(false)
        commentStart.addTransition("/".toRegex(), nextState)

        var currentState = nextState
        nextState = State(false)
        currentState.addTransition(".".toRegex(), nextState)
        nextState.addTransition(".".toRegex(), nextState)

        currentState = nextState
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