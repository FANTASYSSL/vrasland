package priv.juergenie.vrasland.core;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import priv.juergenie.vrasland.bean.Result;
import priv.juergenie.vrasland.bean.ScriptResult;
import priv.juergenie.vrasland.utils.LuaScriptUtil;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Map;

@Repository
public class VraslandScriptManager {
    private ScriptEngineManager manager = new ScriptEngineManager();

    public ResponseEntity runScriptByPath(String filePath, Map<String, Object> args) {
        if (filePath == null || filePath.isEmpty())
            return new Result<String>().notOk().send("not found resource.").toResponse(HttpStatus.NOT_FOUND);

        var file = new File(filePath);
        if (!file.exists())
            return new Result<String>().notOk().send("not found resource.").toResponse(HttpStatus.NOT_FOUND);

        Result<Object> result = new Result<>().isOk();
        ResponseEntity response;
        try {
            // 获取并执行脚本
            var script = this.getScriptObject(file);
            var retArray = (args == null ? script : script.bind(args)).eval(Object[].class);

            // 针对脚本返回值，构造不同的响应对象
            if (retArray == null) {
                response = result.send("empty result.").toResponse(HttpStatus.ACCEPTED);
            } else {
                /* ========================================================================================
                 | 脚本应当在最末尾处返回这些值：return [result_data], [result_message], [response_status]
                 | 但这并不是强制性的，若没有任何需要返回的，则可以都不返回，否则，需要按顺序进行返回。
                 */
                if (retArray.length > 0)
                    result.body(LuaScriptUtil.convertLuaValue(retArray[0]));
                if (retArray.length > 1)
                    result.send(retArray[1].toString());

                var state = 200;
                if (retArray.length > 2)
                    state = Integer.valueOf(retArray[2].toString());
                response = result.toResponse(state);
            }
        } catch (Exception e) {
            response = result.notOk().send("internal error.").body(e).toResponse(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return response;
    }

    public ScriptObject getScriptObject(File file) throws IOException {
        return new ScriptObject(getScriptEngine(), file);
    }

    public ScriptObject getScriptObject(String source) {
        return new ScriptObject(getScriptEngine(), source);
    }

    public ScriptEngine getScriptEngine() {
        return manager.getEngineByName("luaj");
    }
}
