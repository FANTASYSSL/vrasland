package priv.juergenie.vrasland.core;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.io.FileUtils;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;

public class ScriptObject {
    @Getter @Setter
    private ScriptEngine engine;
    @Getter @Setter
    private String fileName;
    @Getter @Setter
    private String source;
    private File file;

    public ScriptObject(ScriptEngine engine, File file) throws IOException {
        this(engine, file, file.getAbsolutePath(), FileUtils.readFileToString(file, "utf-8"));
//        this.engine = engine;
//        this.file = file;
//        this.fileName = file.getAbsolutePath();
//        this.source = FileUtils.readFileToString(file, "utf-8");
    }

    public ScriptObject(ScriptEngine engine, String source) {
        this(engine, null, ":memory:", source);
//        this.engine = engine;
//        this.file = null;
//        this.fileName = "<:memory>";
//        this.source = source;
    }

    private ScriptObject(ScriptEngine engine, File file, String fileName, String source) {
        this.engine = engine;
        this.file = file;
        this.fileName = fileName;
        this.source = source;
    }

    public ScriptObject bind(String key, Object value) {
        return this.bind(new HashMap<>(){{put(key, value);}});
    }

    public ScriptObject bind(Map<String, Object> args) {
        var context = this.engine.getContext();

        for(var key : args.keySet())
            context.setAttribute(key, args.get(key), ScriptContext.ENGINE_SCOPE);

        return this;
    }

    public <T> T eval(Class<T> cls) throws ScriptException {
        var result = this.engine.eval(this.source);
        if (cls.isInstance(result))
            return cls.cast(result);
        else
            return null;
    }
}
