package priv.juergenie.vrasland.database

import org.mapdb.DB
import org.mapdb.Serializer


open class DbManager(private val db: DB) {
    fun list(name: String): List<Any?> {
        if (name.isEmpty())
            throw NullPointerException("repository's name cannot be empty!")
        return this.db.
                indexTreeList(name).
                layout(64, 8).
                createOrOpen()
    }

    fun table(name: String): Map<String, Any?> {
        if (name.isEmpty())
            throw NullPointerException("repository's name cannot be empty!")
        return this.db.
                treeMap(name, Serializer.STRING, Serializer.JAVA).
                maxNodeSize(64).
                createOrOpen()
    }
}
