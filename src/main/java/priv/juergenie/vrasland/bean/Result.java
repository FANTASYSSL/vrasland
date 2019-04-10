package priv.juergenie.vrasland.bean;

import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

@Data
public class Result<T> {
    private String statue;
    private String message;
    private Map<String, Object> meta;
    private T data;

    public Result<T> isOk() {
        this.statue = "ok";
        return this;
    }

    public Result<T> notOk() {
        this.statue = "error";
        return this;
    }

    public Result<T> send(String message) {
        this.message = message;
        return this;
    }

    public Result<T> body(T data) {
        this.data = data;
        return this;
    }

    public Result<T> meta(String key, Object value) {
        this.meta.put(key, value);
        return this;
    }

    public ResponseEntity<Result<T>> toResponse(HttpStatus status) {
        return new ResponseEntity<>(this, status);
    }

    public ResponseEntity<Result<T>> toResponse(int status) {
        return this.toResponse(HttpStatus.valueOf(status));
    }

    public final static ResponseEntity
            RESOURCE_NOT_FOUND = new Result<String>().notOk().send("not found resources.").toResponse(HttpStatus.NOT_FOUND);
}
