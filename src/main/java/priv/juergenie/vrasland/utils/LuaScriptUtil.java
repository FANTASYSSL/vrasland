package priv.juergenie.vrasland.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.luaj.vm2.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LuaScriptUtil {
    private static Logger logger = LogManager.getLogger(LuaScriptUtil.class);

    public static Map<String, Object> convertLuaTable(Map table) {
        Map<String, Object> map = new HashMap<>();
        if (table != null) {
            for (var key : table.keySet()) {
                var str = convertLuaValue(key).toString();
                var value = convertLuaValue(table.get(key));
                map.put(str, value);
            }
        }
        return map;
    }

    public static Map<String, Object> convertLuaTable(LuaTable table) {
        Map<String, Object> map = new HashMap<>();

        logger.debug("convert LuaTable to JavaTable...");
        if (table != null) {
            for (var key : table.keys()) {
                var str = key.tojstring();
                var value = convertLuaValue(table.get(key));
                logger.debug("=> convert [{} : {}]", key, value);
                map.put(str, value);
            }
        }

        return map;
    }

    public static Object[] convertLuaValues(Object[] value) {
        Object[] result = null;

        if (value != null) {
            result = new Object[value.length];

            for (int i = 0; i < result.length; i++) {
                result[i] = convertLuaValue(value[i]);
            }
        }

        return result;
    }

    public static Object convertLuaValue(Object value) {
        Object result = null;

        logger.debug("convert LuaValue to JavaObject...");
        if (value != null) {
            if (value instanceof LuaValue) {
                var luaValue = (LuaValue) value;
                switch (luaValue.type()) {
                    case LuaValue.TBOOLEAN:
                        result = luaValue.checkboolean();
                        break;
                    case LuaValue.TNUMBER:
                        result = luaValue.checkdouble();
                        break;
                    case LuaValue.TSTRING:
                        result = luaValue.checkjstring();
                        break;
                    case LuaValue.TUSERDATA:
                        result = luaValue.checkuserdata();
                        break;
                    case LuaValue.TTABLE:
                        result = convertLuaTable(luaValue.checktable());
                        break;
                }
            } else if (value instanceof Object[] || value instanceof List) {
                if (value instanceof List)
                    value = ((List) value).toArray();

                result = convertLuaValues((Object[]) value);
            } else if (value instanceof Map) {
                result = convertLuaTable((Map) value);
            } else {
                result = value;
            }
        }
        logger.debug("convert [{}]", result);
        return result;
    }

    public static LuaTable convertJavaTable(Map map) {
        var result = new LuaTable();
        if (map != null) {
            for (var key : map.keySet()) {
                var value = convertJavaBean(map.get(key));
                result.set(key.toString(), value);
            }
        }
        return result;
    }

    public static LuaTable convertJavaList(Object[] list) {
        var result = new LuaTable();
        for (int i = 1; i <= list.length; i++)
            result.set(i, convertJavaBean(list[i-1]));

        return result;
    }

    public static LuaValue convertJavaBean(Object bean) {
        LuaValue result = null;

        if (bean != null) {
            if      (bean instanceof String)
                result = LuaValue.valueOf((String)bean);

            else if (bean instanceof Number)
                result = LuaValue.valueOf(((Number)bean).doubleValue());

            else if (bean instanceof Boolean)
                result = LuaValue.valueOf((Boolean)bean);

            else if (bean instanceof byte[])
                result = LuaValue.valueOf((byte[])bean);

            else if (bean instanceof Map)
                result = convertJavaTable((Map)bean);

            else if (bean instanceof Object[])
                result = convertJavaList((Object[])bean);

            else
                result = new LuaUserdata(bean);
        }

        return result;
    }

    public static LuaValue[] convertJavaBeans(Object[] beans) {
        var result = new LuaValue[beans.length];

        for (int i = 0; i < beans.length; i++)
            result[i] = convertJavaBean(beans[i]);

        return result;
    }
}
