package priv.juergenie.vrasland

import io.javalin.Context
import javax.script.Bindings
import javax.script.ScriptException

private typealias ConvertToMap = (Any) -> Map<String, Any>
class RouterHandler {

    var scriptManager: VraslandScriptManager = VraslandScriptManager()
    var convertToMap: ConvertToMap = {any -> any as Bindings}

    fun getHandler(context: Context) {
        callScript(context, "get")
    }

    fun postHandler(context: Context) {
        callScript(context, "post")
    }

    fun putHandler(context: Context) {
        callScript(context, "put")
    }

    fun deleteHandler(context: Context) {
        callScript(context, "delete")
    }

    fun patchHandler(context: Context) {
        callScript(context, "patch")
    }

    private fun callScript(context: Context, funcName: String) {
        val script = scriptManager.getScriptObject(context.path())
        val result: Result<Any>
        var status = 200
        if (script == null) {
            result =    Result<Any>().
                        isError().
                        send("resource not found.")
            status = 404
        } else {
            val callRet = convertToMap(script.call(context, funcName)?:throw ScriptException("script must be return something like this struct: (status: Int, message: String, data: Any?)!"))
            result = Result<Any>().isOk()
            if ("message" in callRet.keys)
                result.send(callRet["message"].toString())
            if ("data" in callRet.keys)
                result.use(callRet["data"])
            if ("status" in callRet.keys)
                status = callRet["status"] as Int
        }

        context.json(result).status(status)
    }
}