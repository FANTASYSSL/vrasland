package priv.juergenie.vrasland.core;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import priv.juergenie.vrasland.bean.Result;
import priv.juergenie.vrasland.utils.LuaScriptUtil;

import javax.annotation.Resource;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Repository
public class VraslandScriptManager {
    // 系统脚本存放路径，该路径下放置预定的系统处理逻辑脚本。
    @Value("${systemScriptPath:./system script}")
    private String systemScriptPath;

    private ScriptEngineManager manager = new ScriptEngineManager();

    // 回调函数对象，包含了一些列函数引用，该对象用于构造针对脚本执行的 AOP 流程。
    @Resource(name = "callback")
    private VraslandScriptManagerCallback callback;

    /**
     * 脚本执行函数，将会返回一个已生成的响应实体。
     * @param filePath 脚本文件路径
     * @param func 要调用的函数名
     * @param bind 要进行绑定的全局变量
     * @param args 要传入的调用参数
     * @return 生成的响应实体
     * @see org.springframework.http.ResponseEntity
     * @see priv.juergenie.vrasland.core.ScriptObject
     */
    public ResponseEntity runScriptByPath(String filePath, String func, Map<String, Object> bind, Object... args) {
        if (!callback.checkFilePath(filePath))
            return callback.getResponse();

        var file = new File(filePath);
        if (!callback.checkFileIsExists(file))
            return callback.getResponse();

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

    /**
     * 获取一个经过封装的 Lua 脚本执行对象，该执行对象会读入并预执行所传入的脚本源码。
     * @param file 脚本文件。
     * @return 经过封装的 Lua 脚本执行对象。
     * @see priv.juergenie.vrasland.core.ScriptObject
     */
    public ScriptObject getScriptObject(File file) throws IOException, ScriptException {
        return new ScriptObject(getScriptEngine(), file);
    }

    /**
     * 获取一个经过封装的 Lua 脚本执行对象，该执行对象会预执行所传入的脚本源码。
     * @param source 脚本源码。
     * @return 经过封装的 Lua 脚本执行对象。
     * @see priv.juergenie.vrasland.core.ScriptObject
     */
    public ScriptObject getScriptObject(String source) throws ScriptException {
        return new ScriptObject(getScriptEngine(), source);
    }

    /**
     * 获取一个 LuaJ 脚本引擎对象。
     * @return LuaJ 脚本引擎。
     * @see org.luaj.vm2.script.LuaScriptEngine
     */
    public ScriptEngine getScriptEngine() {
        return manager.getEngineByName("luaj");
    }

    /**
     * 全局绑定，该绑定下，在所有由该管理器实例获取的脚本对象中皆生效。
     * @param key 绑定键，在脚本中通过该键获取所绑定的值
     * @param value 绑定值，将会被映射为 userdata 数据
     * @return 当前管理器实例
     */
    public VraslandScriptManager bind(String key, Object value) {
        var map = new HashMap<String, Object>();
        map.put(key, value);
        return this.bind(map);
    }

    /**
     * 全局绑定，该绑定下，在所有由该管理器实例获取的脚本对象中皆生效。
     * @param args 绑定数据
     * @return 当前管理器实例
     */
    public VraslandScriptManager bind(Map<String, Object> args) {
        this.manager.getBindings().putAll(args);
        return this;
    }
}
