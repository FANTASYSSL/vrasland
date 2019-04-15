package priv.juergenie.vrasland.core;

import lombok.Data;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import priv.juergenie.vrasland.bean.Result;

import java.io.File;
import java.util.function.Function;

@Data
@Configuration
public class VraslandScriptManagerCallback {
    /**
     * 线程独立的存储变量，用于在每个访问线程中存储check结果。
     */
    private ThreadLocal<ResponseEntity> local = new ThreadLocal<>();
    /**
     * 检测脚本文件是否存在的回调函数。
     */
    private Function<String, ResponseEntity> checkFilePathCallback;

    private Function<File, ResponseEntity> checkFileIsExistsCallBack;

    /**
     * 检测脚本文件是否存在
     * @param filePath 脚本路径
     * @return 检测结果
     */
    public boolean checkFilePath(String filePath) {
//        var result = false;
//        if (this.checkFilePathCallback != null) {
//            var response = this.checkFilePathCallback.apply(filePath);
//            if (response != null)
//                this.setResponse(response);
//            else
//                result = true;
//        }
//        return result;
        return this.callFunc(this.checkFilePathCallback, filePath);
    }

    public boolean checkFileIsExists(File file) {
//        var result = false;
//        if (this.checkFileIsExistsCallBack != null) {
//            var response = this.checkFileIsExistsCallBack.apply(file);
//            if (response != null)
//                this.setResponse(response);
//            else
//                result = true;
//        }
//        return result;
        return this.callFunc(checkFileIsExistsCallBack, file);
    }

    private <T> boolean callFunc(Function<T, ResponseEntity> func, T obj) {
        var result = false;
        if (func != null) {
            var response = func.apply(obj);
            if (response != null)
                this.setResponse(response);
            else
                result = true;
        }
        return result;
    }

    /**
     * 当任意check不为true时，可通过该函数获取到对应的响应信息。
     * @return 对应的响应实体。
     */
    public ResponseEntity getResponse() {
        return this.local.get();
    }

    /**
     * 设置响应信息，以便调用者进行获取。
     * @param response 欲置入的响应实体
     */
    private void setResponse(ResponseEntity response) {
        this.local.set(response);
    }

    /**
     * 默认的callback处理对象，若之后要进行覆盖，可直接覆写一个callback bean。
     * @return callback对象
     */
    @Bean(name = "callback")
    public static VraslandScriptManagerCallback callback() {
        var result = new VraslandScriptManagerCallback();

        result.checkFilePathCallback = file -> {
            ResponseEntity response = null;
            if (StringUtils.isEmpty(file))
                response = Result.RESOURCE_NOT_FOUND;
            return response;
        };

        result.checkFileIsExistsCallBack = file -> {
            ResponseEntity response = null;
            if (!file.exists())
                response = Result.RESOURCE_NOT_FOUND;
            return response;
        };

        return result;
    }
}
