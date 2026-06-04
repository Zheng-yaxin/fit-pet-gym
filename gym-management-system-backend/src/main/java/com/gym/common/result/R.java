
package com.gym.common.result;

import com.gym.common.constant.HttpStatus;
import lombok.Data;
import java.io.Serializable;

@Data
public class R<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private int code;
    private String msg;
    private T data;

    public static <T> R<T> ok() {
        return restResult(null, HttpStatus.SUCCESS, "操作成功");
    }

    public static <T> R<T> ok(T data) {
        return restResult(data, HttpStatus.SUCCESS, "操作成功");
    }

    public static <T> R<T> ok(T data, String msg) {
        return restResult(data, HttpStatus.SUCCESS, msg);
    }

    public static <T> R<T> fail() {
        return restResult(null, HttpStatus.ERROR, "操作失败");
    }

    public static <T> R<T> fail(String msg) {
        return restResult(null, HttpStatus.ERROR, msg);
    }

    public static <T> R<T> fail(int code, String msg) {
        return restResult(null, code, msg);
    }

    private static <T> R<T> restResult(T data, int code, String msg) {
        R<T> apiResult = new R<>();
        apiResult.setCode(code);
        apiResult.setData(data);
        apiResult.setMsg(msg);
        return apiResult;
    }
}