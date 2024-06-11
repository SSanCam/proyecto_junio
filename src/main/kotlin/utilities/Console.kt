package utilities

import interfaces.IOutputInfo

/**
 * Console manejará los datos de entrada/salida.
 */

class Console : IOutputInfo {

    override fun showInfo(output: Any, lineBreak: Boolean) {
        if (lineBreak) {
            println(output)
        } else {
            print(output)
        }
    }

    override fun readInput(request: Any): String {
        showInfo(request)
        return readLine() ?: ""
    }

    override fun showError(errorMessage: String) {
        showInfo(errorMessage)
    }

    override fun showConfirmation(message: String) {
        showInfo(message)
    }

}