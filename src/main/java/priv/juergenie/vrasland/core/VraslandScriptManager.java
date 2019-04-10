package priv.juergenie.vrasland.core;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import priv.juergenie.vrasland.bean.Result;
import priv.juergenie.vrasland.utils.LuaScriptUtil;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.File;
import java.io.IOException;
import java.util.Map;

@Repository
public class VraslandScriptManager {
    private ScriptEngineManager manager = new ScriptEngineManager();

    public ResponseEntity runScriptByPath(String filePath, String func, Map<String, Object> bind, Object... args) {
        if (filePath == null || filePath.isEmpty())
            return new Result<String>().notOk().send("not found resource.").toResponse(HttpStatus.NOT_FOUND);

        var file = new File(filePath);
        if (!file.exists())
            return new Result<String>().notOk().send("not found resource.").toResponse(HttpStatus.NOT_FOUND);

        ResponseEntity response;
        try {
            // 获取脚本对象
            var script = this.getScriptObject(file);

            // 检测是否有对应的处理函数的定义，若没有，则需要返回405
            if (!script.hasAttribute(func))
                return new Result<String>().notOk().send("not allowable request.").toResponse(HttpStatus.METHOD_NOT_ALLOWED);

            var retArgs = (bind == null ? script : script.bind(bind)).callFunc(func, args);

            Result<Object> result = new Result<>().isOk();
            // 针对脚本返回值，构造不同的响应对象
            if (retArgs == null) {
                response = result.send("empty result.").toResponse(HttpStatus.ACCEPTED);
            } else {
                /* ========================================================================================
                 | 脚本应当在最末尾处返回这些值：return [result_data], [result_message], [response_status]
                 | 但这并不是强制性的，若没有任何需要返回的，则可以都不返回，否则，需要按顺序进行返回。
                 */
                int count = retArgs.narg();
                if (count > 0)
                    result.body(LuaScriptUtil.convertLuaValue(retArgs.checkvalue(1)));
                if (count > 1)
                    result.send(LuaScriptUtil.convertLuaValue(retArgs.checkvalue(2)).toString());

                var state = 200;
                if (count > 2)
                    state = retArgs.checkint(3);
                response = result.toResponse(state);
            }
        } catch (Exception e) {
            response = new Result<>().notOk().send("internal error.").body(e).toResponse(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return response;
    }

    public ScriptObject getScriptObject(File file) throws IOException, ScriptException {
        return new ScriptObject(getScriptEngine(), file);
    }

    public ScriptObject getScriptObject(String source) throws ScriptException {
        return new ScriptObject(getScriptEngine(), source);
    }

    public ScriptEngine getScriptEngine() {
        return manager.getEngineByName("luaj");
    }
}
