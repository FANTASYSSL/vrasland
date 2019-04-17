package priv.juergenie.vrasland

import priv.juergenie.vrasland.common.Constant
import java.lang.IllegalArgumentException
import java.lang.NullPointerException
import java.nio.file.Files
import java.nio.file.Path
import java.util.HashMap


private typealias Parser = (String) -> ScriptObject?

class RequestUrlParser private constructor() {
    private val parserMapper = HashMap<String, Parser>()
    private lateinit var manager: VraslandScriptManager

    operator fun get(name: String): Parser {
        return parserMapper[name] ?: throw IllegalArgumentException("parser <$name> is not exists!")
    }

    operator fun set(name: String, parser: Parser) {
        this.parserMapper[name] = parser
    }

    fun useManager(manager: VraslandScriptManager): RequestUrlParser {
        this.manager = manager
        return this
    }

    companion object {
        val INSTANCE = RequestUrlParser()

        init {
            // 添加默认的路径解析器
            INSTANCE.parserMapper["default"] = fun(path): ScriptObject? {
                val result: ScriptObject?
                val extension = Constant.CONFIG["script"]["extension"]
                val fullPath = Constant.CONFIG["script"]["apiPath"] + path
                val args = ArrayList<String>()

                if (!Files.exists(Path.of("$fullPath.$extension"))) {
                    val sections = path.split('/')
                    val buffer = StringBuffer("./${Constant.CONFIG["script"]["apiPath"]}")
                    for (section in sections) {
                        val last = if (sections.last() == section) ".$extension" else ""
                        var tempPath = "$buffer/$section$last"

                        if (!Files.exists(Path.of(tempPath))) {
                            tempPath = "$buffer/{index}$last"
                            if (Files.exists(Path.of(tempPath))) {
                                args.add(section)
                                buffer.append("/{index}$last")
                            } else {
                                throw NullPointerException("error url: $section not found!")
                            }
                        } else {
                            buffer.append("/$section$last")
                        }
                    }
                    result = ScriptObject(INSTANCE.manager.engine, loadScript(buffer.toString()))
                } else {
                    result = ScriptObject(INSTANCE.manager.engine, loadScript("$fullPath.$extension"))
                }

                return  result.
                        useConverter(INSTANCE.manager.converter).
                        useAop(INSTANCE.manager.aop).
                        bind("args", args)
            }
        }
    }
}

fun loadScript(path: String): String {
    return Files.readString(Path.of(path))
}
