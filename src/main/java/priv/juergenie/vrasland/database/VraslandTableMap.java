//package priv.juergenie.vrasland.database;
//
//import org.luaj.vm2.LuaTable;
//import org.luaj.vm2.LuaValue;
//import priv.juergenie.vrasland.utils.LuaScriptUtil;
//
//import java.util.Map;
//
//public class VraslandTableMap {
//    private Map<String, Object> repository;
//
//    public VraslandTableMap(Map<String, Object> repository) {
//        this.repository = repository;
//    }
//
//    public LuaValue put(String key, LuaValue value) {
//        this.repository.put(key, LuaScriptUtil.convertLuaValue(value));
//        return value;
//    }
//
//    public LuaValue get(String key, LuaValue defaultValue) {
//        if (!this.repository.containsKey(key))
//            this.put(key, defaultValue);
//        return LuaScriptUtil.convertJavaBean(this.repository.get(key));
//    }
//
//    public LuaValue[] get(String[] keys) {
//        LuaValue[] result = new LuaValue[keys.length];
//        for (var i=0; i<keys.length; i++)
//            result[i] = LuaScriptUtil.convertJavaBean(this.repository.get(keys[i]));
//        return result;
//    }
//}
