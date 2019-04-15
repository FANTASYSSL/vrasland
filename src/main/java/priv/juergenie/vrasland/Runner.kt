package priv.juergenie.vrasland

import com.google.gson.Gson
import io.javalin.Javalin
import io.javalin.json.FromJsonMapper
import io.javalin.json.JavalinJson
import io.javalin.json.ToJsonMapper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import priv.juergenie.vrasland.common.Constant.Companion.CONFIG

val log: Logger = LoggerFactory.getLogger("main")

class Mapper: FromJsonMapper, ToJsonMapper {
    val gson = Gson()

    override fun map(obj: Any): String {
        return gson.toJson(obj)
    }

    override fun <T> map(json: String, targetClass: Class<T>): T {
        return gson.fromJson(json, targetClass)
    }
}

fun main() {
    val routerHandler = RouterHandler()
    routerHandler.scriptManager.
            from(CONFIG.get("server.apiPath", "./api")).
            using(CONFIG.get("script.extension", "js"))

    val mapper = Mapper()
    JavalinJson.fromJsonMapper = mapper
    JavalinJson.toJsonMapper = mapper

    val app = Javalin.create().start(CONFIG.get("server.port", "8080").toInt())
    app.exception(Exception::class.java) { exception, context ->
//        log.warn("something error happened on request(${context.path()}): ${exception.message}")
        exception.printStackTrace()
        val result = Result<Exception>().
                isError().
                send(exception.message!!).
                use(exception)
        context.json(result).
                status(500)
    }
    app.get("/*", routerHandler::getHandler)
    app.post("/*", routerHandler::postHandler)
    app.put("/*", routerHandler::putHandler)
    app.delete("/*", routerHandler::deleteHandler)
    app.patch("/*", routerHandler::patchHandler)
}
