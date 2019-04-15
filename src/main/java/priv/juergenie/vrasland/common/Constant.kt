package priv.juergenie.vrasland.common

import java.nio.file.Files
import java.nio.file.Path

open class Constant {
    companion object {
        const val SCOPE_GLOBAL: Int = 200
        const val SCOPE_ENGINE: Int = 100

        const val RESULT_OK: String = "ok"
        const val RESULT_ERROR: String = "error"

        val CONFIG: Configure

        init {
            if (Files.exists(Path.of("./vrasland.cfg"))) {
                CONFIG = Configure("./vrasland.cfg")
            } else {
                CONFIG = Configure.DEFAULT
                // 将默认配置项写入其中
                CONFIG.save()
            }
        }
    }
}