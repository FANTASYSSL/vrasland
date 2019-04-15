package priv.juergenie.vrasland.common

import java.nio.file.Files
import java.nio.file.NoSuchFileException
import java.nio.file.Path
import java.util.HashMap

class Configure(path: String) {
    private var configStr: String
    private val source: String get() {
        val buffer = StringBuffer()
        for (value in this.configMap.values)
            buffer.append(value.source).append('\n')
        return buffer.toString()
    }

    private var configMap: HashMap<String, Section> = HashMap()

    init {
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
                    throw java.lang.IllegalArgumentException("Config have a syntax error: Invalid section name on line<$i>")
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
                throw IllegalArgumentException("Config have a syntax error: Invalid pair on line<$i>")

            val key = sp[0]
            val value = sp[1].substringBefore('#')

            section[key.trim()] = value.trim()
        }
        this.configMap[section.name] = section
    }

    operator fun get(key: String): Section {
        if (!this.configMap.containsKey(key))
            this.configMap.plus(Pair(key, Section(this, key)))
        return this.configMap[key] ?: error("Unexpected error: Section[$key]")
    }

    operator fun set(key: String, value: Section) {
        this.configMap.plus(Pair(key, value))
    }

    fun save() {
        this.configStr = this.source
    }

    class Section(private val parent: Configure, val name: String) {
        private val innerMap: HashMap<String, String> = HashMap()
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