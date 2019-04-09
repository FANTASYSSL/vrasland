package priv.juergenie.vrasland.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import priv.juergenie.vrasland.core.VraslandScriptManager;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
public class LuaRepeatController {
    @Resource
    private VraslandScriptManager vraslandScriptManager;

    @GetMapping("/**")
    public ResponseEntity get(HttpServletRequest request) {
        return this.invokeScript(request, null);
    }

    @PostMapping("/**")
    public ResponseEntity pust(HttpServletRequest request, @RequestBody Map<String, Object> args) {
        return this.invokeScript(request, args);
    }

    private ResponseEntity invokeScript(HttpServletRequest request, Map<String, Object> args) {
        var path = request.getServletPath();
        var local = request.getSession().getServletContext().getRealPath(path);

        return vraslandScriptManager.runScriptByPath(local, args);
    }
}
