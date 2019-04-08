package priv.juergenie.vrasland.core.scriptengine;

public interface VraslandScriptEngine {
    <T> T call(String func);

    <T> T runFile(String file);

    ScriptObject getScriptByUrl(String url);
}
