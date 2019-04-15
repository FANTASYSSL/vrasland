//package priv.juergenie.vrasland.database;
//
//import org.luaj.vm2.LuaFunction;
//import org.luaj.vm2.LuaTable;
//import org.luaj.vm2.LuaValue;
//import org.mapdb.IndexTreeList;
//import priv.juergenie.vrasland.utils.LuaScriptUtil;
//
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//
//public class VraslandTableList {
//    private List<Object> repository;
//
//    public VraslandTableList(IndexTreeList<Object> repository) {
//        this.repository = repository;
//    }
//
//    public int size() {
//        return this.repository.size();
//    }
//
//    public boolean contains(Object o) {
//        if (o instanceof LuaValue)  o = LuaScriptUtil.convertLuaValue(o);
//        if (o instanceof Map)       return this.repository.contains(o);
//        else                        return false;
//    }
//
//    public boolean add(LuaTable object) {
//        return this.repository.add(LuaScriptUtil.convertLuaTable(object));
//    }
//
//    public boolean remove(Object o) {
//        if (o instanceof LuaValue)
//            o = LuaScriptUtil.convertLuaValue(o);
//        if (o instanceof Map)
//            return this.repository.remove(o);
//        else
//            return false;
//    }
//
//    public boolean containsAll(List<LuaTable> list) {
//        return this.repository.containsAll(LuaScriptUtil.convertLuaList(list));
//    }
//
//    public boolean addAll(List<LuaTable> list) {
//        return this.repository.addAll(LuaScriptUtil.convertLuaList(list));
//    }
//
//    public boolean addAll(int index, List<LuaTable> list) {
//        return this.repository.addAll(index, LuaScriptUtil.convertLuaList(list));
//    }
//
//    public boolean removeAll(List<LuaTable> list) {
//        return this.repository.removeAll(LuaScriptUtil.convertLuaList(list));
//    }
//
//    public boolean retainAll(List<LuaTable> list) {
//        return this.repository.retainAll(LuaScriptUtil.convertLuaList(list));
//    }
//
//    public void clear() {
//        this.repository.clear();
//    }
//
//    public List<LuaTable> select(LuaFunction function) {
//        return  this.repository.
//                stream().
//                filter(map -> function.invoke(LuaScriptUtil.convertJavaTable((Map)map)).checkboolean(1)).
//                map(LuaScriptUtil::convertJavaTable).
//                collect(Collectors.toList());
//    }
//
//    public LuaTable get(int index) {
//        return LuaScriptUtil.convertJavaTable(this.repository.get(index));
//    }
//
//    public LuaTable set(int index, LuaTable table) {
//        this.repository.set(index, LuaScriptUtil.convertLuaTable(table));
//        return table;
//    }
//
//    public void add(int index, LuaTable element) {
//        this.repository.add(index, LuaScriptUtil.convertLuaTable(element));
//    }
//
//    public LuaTable remove(int index) {
//        return LuaScriptUtil.convertJavaTable(this.repository.remove(index));
//    }
//}
