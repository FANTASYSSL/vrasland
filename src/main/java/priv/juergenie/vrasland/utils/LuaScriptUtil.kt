//package priv.juergenie.vrasland.utils
//
//import org.luaj.vm2.LuaTable
//import org.luaj.vm2.LuaValue
//
//open class LuaScriptUtil {
//    companion object {
//        fun convertLuaTable(table: LuaTable): Map<String, Any?> {
//            val result = HashMap<String, Any?>()
//            for (key in table.keys())
//                result[key.checkjstring()] = convertLuaValue(table[key])
//            return result
//        }
//
//        fun convertLuaValue(value: LuaValue): Any? {
//            return when (value.type()) {
//                LuaValue.TNUMBER -> value.checkdouble()
//                LuaValue.TSTRING -> value.checkjstring()
//                LuaValue.TBOOLEAN -> value.checkboolean()
//                LuaValue.TTABLE -> convertLuaTable(value.checktable())
//                LuaValue.TUSERDATA -> value.checkuserdata()
//                else -> null
//            }
//        }
//
//        fun convertLuaList(value: LuaValue): List<Any?> {
//            val result = ArrayList<Any?>(value.length())
//            for (i in result.indices)
//                result[i] = convertLuaValue(value[i])
//            return result
//        }
//
//        fun convertBean(value: Any?): LuaValue? {
//            return when (value) {
//                value is Map<*, *> -> convertBeanMap(value as Map<*, *>)
//                value is List<*> -> convertBeanListTable(value as List<*>)
//                value is String -> LuaValue.valueOf(value.toString())
//                value is Number -> LuaValue.valueOf(value as Double)
//                value is Boolean -> LuaValue.valueOf(value as Boolean)
//                else -> null
//            }
//        }
//
//        fun convertBeanMap(value: Map<*, *>): LuaTable {
//            val result = LuaTable()
//            for (key in value.keys)
//                result[key.toString()] = convertBean(value[key])
//            return result
//        }
//
//        fun convertBeanList(value: List<*>): ArrayList<LuaValue?> {
//            val result = ArrayList<LuaValue?>()
//            for (i in value.indices)
//                result[i] = convertBean(value[i])
//            return result
//        }
//
//        fun convertBeanListTable(value: List<*>): LuaTable {
//            val result = LuaTable()
//            for (i in value.indices)
//                result[i] = convertBean(value[i])
//            return result
//        }
//    }
//}
