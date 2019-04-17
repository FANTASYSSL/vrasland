package priv.juergenie.vrasland.database

import org.mapdb.Serializer
import java.io.Closeable
import kotlin.reflect.full.functions


class DbManager {
    companion object {
        private val db = DbConfigure.db

        private fun list(name: String): List<Any?> {
            if (name.isEmpty())
                throw NullPointerException("repository's name cannot be empty!")
            return this.db.indexTreeList(name).layout(64, 8).createOrOpen()
        }

        private fun table(name: String): MutableMap<String, Any?> {
            if (name.isEmpty())
                throw NullPointerException("repository's name cannot be empty!")
            return this.db.treeMap(name, Serializer.STRING, Serializer.JAVA).maxNodeSize(64).createOrOpen()
        }

        /**
         * 处理一个针对数据库中的集合对象的操作，该操作会被注入一个数据库集合对象，并该对象会被自动关闭。
         * @param name 集合对象的 key
         * @param process 操作流程
         */
        fun useList(name: String, process: (List<Any?>) -> Unit) {
            val list = this.list(name)
            CloseablePack(list).use { process(list) }
        }

        /**
         * 处理一个针对数据库中的字典对象的操作，该操作会被注入一个数据库字典对象，并该对象会被自动关闭。
         * @param name 字典对象的 key
         * @param process 操作流程
         */
        fun useTable(name: String, process: (MutableMap<String, Any?>) -> Unit) {
            val table = this.table(name)
            CloseablePack(table).use { process(table) }
        }
    }
}

/**
 * Closeable 包装器，若要对拥有 close 函数的对象使用 use，但这个对象又没有实现 Closeable 接口时，使用这个类对其进行包装即可。
 */
class CloseablePack(val inner: Any): Closeable {
    override fun close() {
        val func = inner::class.functions.filter { it.name == "close" }
        if (func.isNotEmpty())
            func[0].call(inner)
    }
}
