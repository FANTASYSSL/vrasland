package priv.juergenie.vrasland.utils;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import priv.juergenie.vrasland.core.ScriptObject;
import priv.juergenie.vrasland.core.VraslandScriptManager;

import javax.annotation.Resource;
import javax.script.ScriptException;

@SpringBootTest
public class LuaScriptUtilTest {
    @Resource
    private VraslandScriptManager vraslandScriptManager;

    @Test
    public void convertTest() throws ScriptException {
        String script = "local tb = luajava.newInstance('java.util.HashMap')\n" +
                "tb:put('test', 1234)\n" +
                "\n" +
                "return 1,2,tb,{'23333'}";
        ScriptObject object = vraslandScriptManager.getScriptObject(script);
        Object[] result = object.eval(Object[].class);
        for (var obj: result) {
            System.out.println(LuaScriptUtil.convertLuaValue(obj));
        }
    }
}
