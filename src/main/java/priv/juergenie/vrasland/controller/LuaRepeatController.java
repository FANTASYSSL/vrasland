//package priv.juergenie.vrasland.controller;
//
//import org.mapdb.DB;
//import org.mapdb.DBMaker;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.ResponseEntity;
//import org.springframework.util.ResourceUtils;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RestController;
//import priv.juergenie.vrasland.core.VraslandScriptManager;
//import priv.juergenie.vrasland.utils.FileUtil;
//
//import javax.annotation.Resource;
//import javax.servlet.http.HttpServletRequest;
//import java.util.Map;
//
//@RestController
//public class LuaRepeatController {
//    private String staticPath;
//    private VraslandScriptManager vraslandScriptManager;
//
//    // 使用构造函数进行注入
//    public LuaRepeatController(
//            @Value("${staticPath:./restful api}")String staticPath,
//            VraslandScriptManager vraslandScriptManager,
//            DB db
//    ) {
//        // 完成初始化操作，以及脚本执行环境初始化（注入数据库操作对象等）
//        this.staticPath = staticPath;
//        this.vraslandScriptManager = vraslandScriptManager;
//        this.vraslandScriptManager.bind("database", db);    // 当前选择直接注入数据库对象，之后应当改换为注入操作对象（DAO）
//    }
//
//    @GetMapping("/**")
//    public ResponseEntity get(HttpServletRequest request) {
//        return this.invokeScript(request, "get", null);
//    }
//
//    @PostMapping("/**")
//    public ResponseEntity post(HttpServletRequest request, @RequestBody Map<String, Object> args) {
//        return this.invokeScript(request, "post", args);
//    }
//
//    private ResponseEntity invokeScript(HttpServletRequest request, String func, Map<String, Object> args) {
//        var path = request.getServletPath() + ".lua";
//        var local = staticPath + "/" + path;
//
//        return vraslandScriptManager.runScriptByPath(local, func, null, args);
//    }
//}
