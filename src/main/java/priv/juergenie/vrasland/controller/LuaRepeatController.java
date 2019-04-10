package priv.juergenie.vrasland.controller;

import org.mapdb.DBMaker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import priv.juergenie.vrasland.core.VraslandScriptManager;
import priv.juergenie.vrasland.utils.FileUtil;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.FileNotFoundException;
import java.util.Map;

@RestController
public class LuaRepeatController {
    private String staticPath;

    @Resource
    private VraslandScriptManager vraslandScriptManager;

    public LuaRepeatController(@Value("${dbPath:}")String dbPath,
                               @Value("${staticPath:./restful api}")String staticPath) {
        // TODO: 完成初始化操作，以及脚本执行环境初始化（注入数据库操作对象等）
        this.staticPath = staticPath;
        DBMaker.fileDB((dbPath == null || dbPath.isEmpty() ? "." : dbPath) + "/vrasland.db");
    }

    @GetMapping("/**")
    public ResponseEntity get(HttpServletRequest request) {
        return this.invokeScript(request, "get", null);
    }

    @PostMapping("/**")
    public ResponseEntity post(HttpServletRequest request, @RequestBody Map<String, Object> args) {
        return this.invokeScript(request, "post", args);
    }

    private ResponseEntity invokeScript(HttpServletRequest request, String func, Map<String, Object> args) {
        var path = request.getServletPath() + ".lua";
        var local = staticPath + "/" + path;

        return vraslandScriptManager.runScriptByPath(local, func, null, args);
    }
}
