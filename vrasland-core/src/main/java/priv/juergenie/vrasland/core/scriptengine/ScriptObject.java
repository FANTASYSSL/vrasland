package priv.juergenie.vrasland.core.scriptengine;

public interface ScriptObject {
    <T> T call();
    ScriptObject setArguments(Object... args);

    String getFileName();
    String getSource();
}
