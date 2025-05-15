package com.ht.bnu_tiku_backend.utils;

public enum ResultCode {

    // ====== 成功 ======
    SUCCESS(200, "操作成功"),

    // ====== 通用错误 ======
    FAIL(1, "操作失败"),
    UNAUTHORIZED(401, "未登录或登录已过期"),
    FORBIDDEN(403, "没有权限"),
    NOT_FOUND(404, "资源不存在"),
    SERVER_ERROR(500, "服务器异常"),

    // ====== 用户相关 ======
    USER_NOT_FOUND(1001, "用户不存在"),
    PASSWORD_INCORRECT(1002, "密码错误"),
    ACCOUNT_DISABLED(1003, "账号已被禁用"),
    VERIFY_CODE_INVALID(1004, "验证码无效"),

    // ====== 参数相关 ======
    PARAM_ERROR(2001, "请求参数有误"),
    VALIDATION_FAILED(2002, "参数校验失败"),

    // ====== 业务异常 ======
    OPERATION_TOO_FREQUENT(3001, "操作过于频繁"),
    DATA_CONFLICT(3002, "数据冲突"),
    LOGIN_TYPE_UNSUPPORTED(3003, "不支持的登录方式");

    private final int code;
    private final String message;

    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int code() {
        return code;
    }

    public String message() {
        return message;
    }
}
