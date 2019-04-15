package priv.juergenie.vrasland

import io.javalin.Context
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import priv.juergenie.vrasland.common.Constant
import javax.script.Invocable
import javax.script.ScriptEngine
import javax.script.ScriptEngineManager

class VraslandScriptManager {
    private val scriptManager = ScriptEngineManager()
    val engine: ScriptEngine get() = this.scriptManager.getEngineByExtension(this.extension)
    val parser = RequestUrlParser.INSTANCE.useManager(this)[Constant.CONFIG["script"]["urlParser"]]

    // 可塑属性
    private var extension   = "js"
    private var scriptPath  = "./restful api"
    val converter   = VraslandConverter.DEFAULT
    val aop         = VraslandAopObject.DEFAULT

    companion object {
        val log: Logger = LoggerFactory.getLogger(VraslandScriptManager::class.java)
    }

    /**
     * 设置 API 脚本的存放位置，之后的映射将以此作为根目录。
     */
    fun from(path: String): VraslandScriptManager {
        log.info("script manager will load script from: $path")
        this.scriptPath = path
        return this
    }

    /**
     * 设置 API 脚本的后缀名，之后将以此获取脚本引擎以及相应的脚本文件。
     */
    fun using(extension: String): VraslandScriptManager {
        log.info("script manager will using script file: $extension")
        this.extension = extension
        return this
    }

    fun getScriptObject(url: String): ScriptObject? {
        log.info("script manager create script object from: $url")
        return  this.parser(url)?.
                useConverter(this.converter)!!.
                useAop(this.aop)
    }

    fun bind(key: String, value: Any): VraslandScriptManager {
        this.scriptManager.bindings[key] = value
        return this
    }

    operator fun get(key: String): Any? {
        return this.scriptManager[key]
    }

    operator fun set(key: String, value: Any) {
        this.scriptManager.put(key, value)
    }
}

class ScriptObject(private val engine: ScriptEngine, private val source: String) {
    private val initResult: String?
    companion object {
        val log: Logger = LoggerFactory.getLogger(ScriptObject::class.java)
    }
    init {
        this.engine.eval(this.source)
        this.initResult = this.engine.get("result")?.toString()
        log.info("ScriptObject init over. result message: $initResult")
    }

    private lateinit var converter: VraslandConverter
    private lateinit var aop: VraslandAopObject

    fun useConverter(converter: VraslandConverter): ScriptObject {
        this.converter = converter
        return this
    }

    fun useAop(aopObject: VraslandAopObject): ScriptObject {
        this.aop = aopObject
        return this
    }

    /**
     * 绑定一个全局变量，默认下，该全局变量仍不是最高层级的全局变量，而仅限于当前脚本对象。
     * @param key 欲绑定的全局变量名称
     * @param value 欲绑定的全局变量值
     * @param global default false，是否进行全局绑定，不推荐在此进行全局绑定，若要进行全局绑定，请使用 VraslandScriptManager.bind
     * @see priv.juergenie.vrasland.VraslandScriptManager.bind
     */
    fun bind(key: String, value: Any, global: Boolean = false): ScriptObject {
//        val binding = this.engine.getBindings(if (global) Constant.SCOPE_GLOBAL else Constant.SCOPE_ENGINE)
//        binding[key] = this.converter.to(value)
        this.engine.put(key, value)
        return this
    }

    operator fun set(key: String, value: Any) {
        this.bind(key, value)
    }

    operator fun get(key: String): Any {
        return this.engine[key]
    }

    fun call(context: Context, funcName: String, vararg args: Any): Any? {
        return (
                try {
                    this.aop.before(context)
                    val invokable = this.engine as Invocable
                    val result = invokable.invokeFunction(funcName, args)
                    this.aop.after(context, result)
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
                )
    }
}

/**
 * 转换器，用于转换调用者类型与脚本内类型。
 * @param to 从 kotlin 对象类型转换为目标脚本引擎支持的对象类型的函数
 * @param from 从脚本引擎对象类型转换为 kotlin 对象类型的函数
 */
data class VraslandConverter(
        var to: (Any) -> Any,
        var from: (Any) -> Any
) {
    companion object {
        private val log = LoggerFactory.getLogger(VraslandConverter::class.java)
        /**
         * 默认转换器对象，内部不包含任何实现。
         */
        val DEFAULT = VraslandConverter(
                { log.warn("call converter::to, but it haven't invalid function.") },
                { log.warn("call converter::from, but it haven't invalid function.") }
        )
    }
}

/**
 * AOP对象，用于脚本调用前后的流程处理。
 * @param before 脚本被调用前，调用该函数
 * @param after 脚本被调用后，调用该函数，该函数可对脚本返回值进行处理，然后返回一个处理后的返回值（必须有返回值）
 */
data class VraslandAopObject(
        var before: (Context) -> Unit,
        var after: (Context, Any) -> Any
) {
    companion object {
        private val log = LoggerFactory.getLogger(VraslandAopObject::class.java)
        val DEFAULT = VraslandAopObject(
                { log.warn("call VraslandAopObject.before.") },
                { _, result ->
                    log.warn("call VraslandAopObject.after.")
                    result
                }
        )
    }
}
