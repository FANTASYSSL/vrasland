package priv.juergenie.vrasland

import io.javalin.Context
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor
import org.apache.commons.io.monitor.FileAlterationMonitor
import org.apache.commons.io.monitor.FileAlterationObserver
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import priv.juergenie.vrasland.common.Constant.Companion.CONFIG
import priv.juergenie.vrasland.database.DbManager
import java.io.File
import java.lang.UnsupportedOperationException
import javax.script.Invocable
import javax.script.ScriptEngine
import javax.script.ScriptEngineManager
import kotlin.collections.LinkedHashMap

class VraslandScriptManager {
    private val scriptManager = ScriptEngineManager()
    private val global = HashMap<String, Any>()
    val module = ModuleMap()
    val moduleManager = ModuleScriptManager(
            this,
            CONFIG.get("script.modules", "./scriptlibs"),
            CONFIG.get("script.interval", "5000").toLong(),
            CONFIG.get("script.extension", "js")
    )

    val engine: ScriptEngine get() = this.scriptManager.getEngineByExtension(this.extension)
    val parser = RequestUrlParser.INSTANCE.useManager(this)[CONFIG.get("script.urlParser", "default")]

    // 可塑属性
    private var extension   = "js"
    private var scriptPath  = "./api"
    val converter   = VraslandConverter.DEFAULT
    val aop         = VraslandAopObject.DEFAULT

    companion object {
        val log: Logger = LoggerFactory.getLogger(VraslandScriptManager::class.java)
    }

    init {
        // 为引擎绑定一些全局性的工具

        // 提供全局存储器，用于存储全局数据（但不推荐，秉承 RESTful API 的理念，每次请求应当都是无状态/不相关的。
        // 若需要存储状态，可使用 javalin 的 context.sessionAttribute，context 对象亦会被提供到脚本对象域中。）
        scriptManager.put("global", global)

        // 提供全局模块存储器，也可存储一些常量设置。
        scriptManager.put("module", module)

        val vraslandModule = LinkedHashMap<String, Any>()
        vraslandModule["database"] = DbManager
        // 注入 vrasland 框架相关模块，使用时，通过 module.import("vrasland") 引用
        module.registry("vrasland", vraslandModule)

        // 启动模块加载器
        moduleManager.start()
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

    /**
     * 绑定一个全局性的变量，该变量作用域为最高级作用域，将对所有脚本对象产生影响。
     * @param key 变量名
     * @param value 变量值
     */
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
        var before: (ScriptObject, Context) -> Unit,
        var after: (ScriptObject, Context, Any) -> Any
) {
    companion object {
        private val log = LoggerFactory.getLogger(VraslandAopObject::class.java)
        val DEFAULT = VraslandAopObject(
                { _, _ ->
                    log.warn("call VraslandAopObject.before.")
                },
                { _, _, result ->
                    log.warn("call VraslandAopObject.after.")
                    result
                }
        )
    }
}

/**
 * 模块存储器，用于提供一个简易的脚本模块构建机制。
 * 该机制主要用于提高脚本代码的可重用性，并使模块脚本的内容能够常驻（框架本身的脚本执行是即用即丢的，不会常驻于内存之中）。
 */
class ModuleMap {
    private val mapper = LinkedHashMap<String, Any>()

    fun exists(moduleName: String): Boolean {
        return "module_$moduleName" in mapper.keys
    }

    fun registry(moduleName: String, module: Any): ModuleMap {
        this.mapper["module_$moduleName"] = module
        return this
    }

    fun remove(moduleName: String) {
        val module = this.mapper.remove("module_$moduleName") as ScriptObject
    }

    fun import(moduleName: String): Any? {
        return this.mapper["module_$moduleName"]
    }

    operator fun get(key: String): Any? {
        return this.mapper["module_$key"]
    }

    operator fun set(key: String, value: Any) {
        throw UnsupportedOperationException("module cannot be set: (key: $key, value: $value)\nwould this operation is you want? > module.registry(...)")
    }
}

/**
 * 脚本对象的封装，对应每一个脚本（即每一个 ScriptObject 都对应一个 API），可通过 call 函数调用脚本内的相关函数。
 * @param engine 脚本引擎对象
 * @param source 脚本源码，该源码将会在加载的时候被调用一次
 */
open class ScriptObject(private val engine: ScriptEngine, private val source: String) {
    private val initResult: String?
    companion object {
        val log: Logger = LoggerFactory.getLogger(ScriptObject::class.java)
    }
    init {
        // 执行脚本源码，进行初始化，并获取初始化结果（如果有的话）（对应脚本中的 result 变量）
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
     * 绑定一个全局变量，而仅限于当前脚本对象。
     * @param key 欲绑定的全局变量名称
     * @param value 欲绑定的全局变量值
     * @see priv.juergenie.vrasland.VraslandScriptManager.bind
     */
    fun bind(key: String, value: Any): ScriptObject {
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
        return try {
            this.aop.before(this, context)
            this.bind("context", context)
            val result = (this.engine as Invocable).invokeFunction(funcName, args)
            this.aop.after(this, context, result)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

class ModuleScriptManager(val manager: VraslandScriptManager, filePath: String, interval: Long, extension: String):
        FileAlterationListenerAdaptor() {
    private val monitor = FileAlterationMonitor(interval)
    private val observer = FileAlterationObserver(filePath) {
        // 过滤出所有以指定后缀名结尾的脚本
        it.name.endsWith(".$extension")
    }
    private val directory = File(filePath)

    init {
        monitor.addObserver(observer)
        observer.addListener(this)
    }

    fun start() {
        this.monitor.start()
    }

    fun stop() {
        this.monitor.stop()
    }

    companion object {
        val logger: Logger = LoggerFactory.getLogger(ModuleScriptManager::class.java)
    }

    override fun onDirectoryDelete(directory: File) {
        if (this.directory == directory) {
            logger.warn("directory on delete, listener will be stop: ${directory.name}")
            observer.removeListener(this)
        }
    }

    override fun onFileCreate(file: File) {
        logger.info("script was on created: ${file.path}")
        this.loadScript(file)
    }

    override fun onFileChange(file: File) {
        logger.info("script was on changed: ${file.path}")
        this.loadScript(file)
    }

    override fun onFileDelete(file: File) {
        logger.info("script was on deleted, and remove it from module map: ${file.path}")
        this.manager.module.remove(file.nameWithoutExtension)
    }

    private fun loadScript(file: File) {
        val script = manager.getScriptObject(file.absolutePath)
        if (script != null) {
            val scriptName = file.nameWithoutExtension
            // 若已存在，则将其移除。
            if (manager.module.exists(scriptName)) {
                manager.module.remove(scriptName)
            }
            manager.module.registry(scriptName, script)
        }
    }
}
