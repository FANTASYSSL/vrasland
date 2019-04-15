package priv.juergenie.vrasland

import priv.juergenie.vrasland.common.Constant
import java.io.Serializable

class Result<T>: Serializable {
    private var status: String = Constant.RESULT_OK
    private var message: String = "nothing to result."
    private var data: T? = null

    fun isOk(): Result<T> {
        this.status = Constant.RESULT_OK
        return this
    }

    fun isStatus(status: String): Result<T> {
        this.status = status
        return this
    }

    fun isError(): Result<T> {
        this.status = Constant.RESULT_ERROR
        return this
    }

    fun send(message: String): Result<T> {
        this.message = message
        return this
    }

    fun use(data: T?): Result<T> {
        this.data = data
        return this
    }

    override fun toString(): String {
        return "Result(status=$status, message=$message, data=$data)"
    }
}
