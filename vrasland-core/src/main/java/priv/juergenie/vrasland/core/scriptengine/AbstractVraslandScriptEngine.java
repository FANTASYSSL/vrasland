package priv.juergenie.vrasland.core.scriptengine;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractVraslandScriptEngine implements VraslandScriptEngine {
    private Map<String, ScriptObject> scriptMapper;
    private String basePath;

    public AbstractVraslandScriptEngine(String basePath) {
        this.basePath = basePath;
        this.scriptMapper = new ConcurrentHashMap<>();
    }

    @Override
    public ScriptObject getScriptByUrl(String url) {
        return this.scriptMapper.get(url);
    }
}
