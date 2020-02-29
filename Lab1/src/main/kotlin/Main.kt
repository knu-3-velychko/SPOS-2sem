class Main {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val regex = "[^*]".toRegex()
            println("*".matches(regex))
        }
    }
}