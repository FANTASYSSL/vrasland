package priv.juergenie.vrasland.common

import java.nio.file.Files
import java.nio.file.NoSuchFileException
import java.nio.file.Path
import java.util.HashMap

class Configure(private val path: String, newFile: Boolean = false) {
    private var configStr: String
    private val source: String get() {
        val buffer = StringBuffer()
        for (value in this.configMap.values)
            buffer.append(value.source).append('\n')
        return buffer.toString()
    }

    private var configMap: HashMap<String, Section> = HashMap()

    init {
        if (!newFile) {
            val pathOf = Path.of(path)
            if (!Files.exists(pathOf))
                throw NoSuchFileException("file not exists: $path")
            configStr = Files.readString(pathOf).replace("\r\n", "\n")
            val arr = configStr.split("\n")
            // 循环解析配置字符串
            var section = Section(this, "default")
            for (i in arr.indices) {
                val str = arr[i]
                // 忽略注释
                if (str.isEmpty() || str[0] == '#') {
                    continue
                }
                // 如果解析到节点，则更换当前节点
                if (str[0] == '[') {
                    // 判断语法是否正确
                    if (str.indexOf(']', 2) == -1) {
                        throw java.lang.IllegalArgumentException("Config have a syntax error: Invalid section name on $path (line<$i>)")
                    } else {
                        // 判断当前是否正在解析另一节点，如果是，则将当前节点入库，然后创建新节点对象
                        if (!section.empty)
                            this.configMap[section.name.trim()] = section
                        section = Section(this, str.substring(1, str.indexOf(']', 2)))
                        continue
                    }
                }
                // 判断当前节点结构是否正确，不正确则抛出异常
                val sp = str.split("=", limit = 2)
                if (sp.count() < 2)
                    throw IllegalArgumentException("Config have a syntax error: Invalid pair on $path (line<$i>)")

                val key = sp[0]
                val value = sp[1].substringBefore('#')

                section[key.trim()] = value.trim()
            }
            this.configMap[section.name] = section
        } else {
            this.configStr = ""
        }
    }

    companion object {
        val DEFAULT = Configure("./vrasland.cfg", true)
        init {
            // 服务器相关配置
            DEFAULT["server"] = Section(DEFAULT, "server")
            DEFAULT["server"]["port"] = "8080"          // 服务器监听端口
            DEFAULT["server"]["apiPath"] = "./api"      // 服务器 API 所在根路径
            // 脚本解析器相关配置
            DEFAULT["script"] = Section(DEFAULT, "script")
            DEFAULT["script"]["extension"] = "js"       // 脚本后缀名
            DEFAULT["script"]["urlParser"] = "default"  // 请求路径解析器配置，如果要添加的话，请向 RequestUrlParser.INSTANCE 中添加: RequestUrlParser.INSTANCE[name] = {//...}
        }
    }

    operator fun get(key: String): Section {
        if (!this.configMap.containsKey(key))
            this.configMap[key] = Section(this, key)
        return this.configMap[key] ?: error("Unexpected error: Section[$key]")
    }

    operator fun set(key: String, value: Section) {
        this.configMap[key] = value
    }

    /**
     * 使用次级key取值。
     * @param key 必须满足以下形式：section.key
     * @param default 默认值
     */
    fun get(key: String, default: String): String {
        val sp = key.split('.', limit = 2)
        if (sp.count() != 2)
            throw java.lang.IllegalArgumentException("key must be multi-level like a.b, but got: $key")

        if (sp[0] !in this.configMap.keys) {
            this.configMap[sp[0]] = Section(this, sp[0])
            this.save()
        }

        val section = this.configMap[sp[0]]!!
        if (sp[1] !in section.keys) {
            section[sp[1]] = default
            section.save()
        }

        return section[sp[1]]
    }

    fun save() {
        this.configStr = this.source
        Files.writeString(Path.of(this.path), this.configStr)
    }

    class Section(private val parent: Configure, val name: String) {
        private val innerMap: HashMap<String, String> = HashMap()
        val keys get() = innerMap.keys
        val source: String get() {
            val buffer = StringBuffer("[$name]\n")
            for ((k, v) in this.innerMap)
                buffer.append("$k=$v\n")
            return buffer.toString()
        }
        val empty: Boolean get() = this.innerMap.isEmpty()

        operator fun get(key: String): String {
            return innerMap.getOrDefault(key, "")
        }

        operator fun set(key: String, value: String) {
            innerMap[key] = value
        }

        fun save() {
            this.parent.save()
        }

        override fun toString(): String {
            return "Section<$name>"
        }
    }
}