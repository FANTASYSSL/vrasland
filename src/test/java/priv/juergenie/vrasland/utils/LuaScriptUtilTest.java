//package priv.juergenie.vrasland.utils;
//
//import com.google.gson.Gson;
//import org.intellij.lang.annotations.Language;
//import org.junit.jupiter.api.Test;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
//import priv.juergenie.vrasland.core.ScriptObject;
//import priv.juergenie.vrasland.core.VraslandScriptManager;
//
//import javax.annotation.Resource;
//import javax.script.ScriptException;
//
//@SpringBootTest
//public class LuaScriptUtilTest {
//    @Resource
//    private VraslandScriptManager vraslandScriptManager;
//
//    @Test
//    public void convertTest() throws ScriptException {
//        @Language("Lua")
//        String script = "local tb = luajava.newInstance('java.util.ArrayList')\n" +
//                "tb:add(\"list test.\")\n" +
//                "\n" +
//                "return 1,2,tb,{'23333'}";
//        ScriptObject object = vraslandScriptManager.getScriptObject(script);
//        Object[] result = (Object[])object.init();
//        Gson gson = new Gson();
//        for (var obj: result) {
//            System.out.println(gson.toJson(LuaScriptUtil.convertLuaValue(obj)));
//        }
//    }
//}
