package priv.juergenie.vrasland.common

open class Constant {
    companion object {
        const val SCOPE_GLOBAL: Int = 200
        const val SCOPE_ENGINE: Int = 100

        const val RESULT_OK: String = "ok"
        const val RESULT_ERROR: String = "error"

        val CONFIG = Configure("./vrasland.cfg")
    }
}