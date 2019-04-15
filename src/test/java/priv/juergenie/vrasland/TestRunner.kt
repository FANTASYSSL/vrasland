package priv.juergenie.vrasland

import javax.script.Invocable
import javax.script.ScriptEngineManager

fun main() {

    val manager = ScriptEngineManager()
    val engine = manager.getEngineByExtension("js")

    val result = engine.eval("function test() { return 123456 }")
    println((engine as Invocable).invokeFunction("test")::class.java)
    println(result)
}
