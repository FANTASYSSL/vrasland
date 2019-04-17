package priv.juergenie.vrasland

import com.google.gson.Gson
import io.javalin.Javalin
import io.javalin.json.FromJsonMapper
import io.javalin.json.JavalinJson
import io.javalin.json.ToJsonMapper
import priv.juergenie.vrasland.common.Constant.Companion.CONFIG

class Mapper: FromJsonMapper, ToJsonMapper {
    private val gson = Gson()

    override fun map(obj: Any): String {
        return gson.toJson(obj)
    }

    override fun <T> map(json: String, targetClass: Class<T>): T {
        return gson.fromJson(json, targetClass)
    }
}

fun main() {
    // 初始化路由处理器
    val routerHandler = RouterHandler()
    routerHandler.scriptManager.
            from(CONFIG.get("script.apiPath", "./api")).
            using(CONFIG.get("script.extension", "js"))

    // 初始化 JSON 解析器，使用 Gson 替换默认的 Jackson
    val mapper = Mapper()
    JavalinJson.fromJsonMapper = mapper
    JavalinJson.toJsonMapper = mapper

    // 启动应用
    val app = Javalin.create().start(CONFIG.get("server.port", "8080").toInt())
    app.exception(Exception::class.java) { exception, context ->
        exception.printStackTrace()
        val result = Result<Exception>().
                isError().
                send(exception.message!!).
                use(exception)
        context.json(result).
                status(500)
    }
    // 添加通用路由映射
    app.get("/*", routerHandler::getHandler)
    app.post("/*", routerHandler::postHandler)
    app.put("/*", routerHandler::putHandler)
    app.delete("/*", routerHandler::deleteHandler)
    app.patch("/*", routerHandler::patchHandler)
}
