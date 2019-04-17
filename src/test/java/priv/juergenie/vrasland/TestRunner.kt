package priv.juergenie.vrasland

import priv.juergenie.vrasland.database.CloseablePack
import javax.script.Invocable
import javax.script.ScriptEngineManager
import kotlin.reflect.full.functions

fun main() {

    val manager = ScriptEngineManager()
    val engine = manager.getEngineByExtension("js")
    val global = mutableMapOf<String, Any>()
    manager.put("global", global)
    global["tp"] = "hello world"
    global["tm"] = TM
    val result = engine.eval("function test() { return 123456 } function test2() {return global.tm.testUse('23333', function(p) {return parseInt(p)})}")
    println((engine as Invocable).invokeFunction("test2"))
    val tm = TM()
    CloseablePack(tm).use { println("process!") }
    println(tm::class.functions.filter {
        println(it.name)
        it.name == "close"
    })
}

class TM {
    companion object {
        fun testUse(name: String, process: (String) -> Int): Int {
            println("this is companion object's function, input name is $name")
            return process(name)
        }
    }
    fun close() {
        println("will be closed!")
    }
}