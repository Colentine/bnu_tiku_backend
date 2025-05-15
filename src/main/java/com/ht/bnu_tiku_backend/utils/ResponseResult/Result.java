package com.ht.bnu_tiku_backend.utils.ResponseResult;

import com.ht.bnu_tiku_backend.utils.ResultCode;
import lombok.Data;

@Data
public class Result<T> {
    private ResultCode code;
    private T data;

    public static <T> Result<T> ok(T data) {
        Result<T> r = new Result<>();// 0 通常表示成功
        r.setCode(ResultCode.SUCCESS);
        r.setData(data);
        return r;
    }

    public static <T> Result<T> fail() {
        Result<T> r = new Result<>();
        r.setCode(ResultCode.FAIL);
        return r;
    }

    public static <T> Result<T> fail(ResultCode code) {
        Result<T> r = new Result<>();
        r.setCode(code);
        return r;
    }
}
