package priv.juergenie.vrasland.utils

open class ClassUtil {
    fun isInstance(cls: Class<Any>, obj: Any): Boolean {
        return cls.isInstance(obj)
    }
}
