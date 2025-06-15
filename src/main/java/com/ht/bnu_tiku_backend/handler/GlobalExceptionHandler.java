package com.ht.bnu_tiku_backend.handler;

import com.ht.bnu_tiku_backend.utils.Exception.NoLoginException;
import com.ht.bnu_tiku_backend.utils.ResponseResult.Result;
import com.ht.bnu_tiku_backend.utils.ResultCode;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Hidden
@RestControllerAdvice
public class GlobalExceptionHandler {

//    @ExceptionHandler(RuntimeException.class)
//    public Result<?> handleRuntimeException(RuntimeException e) {
//        return Result.fail(ResultCode.SERVER_ERROR);
//    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Result<?> handleIllegalArgument(IllegalArgumentException e) {
        return Result.fail(ResultCode.PARAM_ERROR);
    }

    @ExceptionHandler(NoLoginException.class)
    public Result<?> handleNoLogin(NoLoginException e) {
        return Result.fail(ResultCode.UNAUTHORIZED);
    }

    // 可继续添加其他异常类型，如 MethodArgumentNotValidException 等
}
