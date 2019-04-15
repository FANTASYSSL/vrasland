//package priv.juergenie.vrasland.core;
//
//import org.springframework.stereotype.Component;
//import priv.juergenie.vrasland.utils.FileUtil;
//
//import javax.annotation.Resource;
//import javax.script.ScriptEngine;
//import javax.script.ScriptException;
//import java.io.File;
//import java.io.IOException;
//
///**
// * 脚本映射解析器，用于解析带参数路径 >>> 如：/users/1000 -> /users/{users}.lua [local bind: index=1000]
// */
//@Component
//public class VraslandScriptFileParser {
//    @Resource
//    private VraslandScriptManager vraslandScriptManager;
//
//    public ScriptObject scriptFileParse(String url) throws IOException, ScriptException {
//        ScriptObject result = null;
//        File script = new File(url);
//
//        if (!script.exists()) {
//            String[] sections = url.split("/");
//            StringBuffer path = new StringBuffer(".");
//            for (var section: sections) {
//                String tempPath = path.toString() + "/" + section;
//                File temp = new File(tempPath);
//                if (!temp.exists()) {
//                    tempPath = path.toString() + "/{index}.lua";
//                    temp = new File(tempPath);
//                    if (!temp.exists())
//                        return null;
//
//                    result = vraslandScriptManager.getScriptObject(temp);
//                    result.bind("index", section);
//                    break;
//                }
//
//                path.append("/").append(section);
//            }
//        } else {
//            result = vraslandScriptManager.getScriptObject(script);
//        }
//
//        return result;
//    }
//}
