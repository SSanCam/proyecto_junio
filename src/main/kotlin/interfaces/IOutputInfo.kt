package interfaces

interface IOutputInfo {
    fun showInfo(output: Any, lineBreak: Boolean = true)
    fun readInput(request: Any): String
    fun showError(errorMessage: String)
    fun showConfirmation(message: String)
}