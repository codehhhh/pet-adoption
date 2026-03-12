package com.kmbeast.pojo.api;

import lombok.Getter;
import lombok.Setter;

/**
 * 通用响应
 * @param <T> 泛型
 */
@Setter
@Getter
public class ApiResult<T> extends Result<T> {

    private T data; //响应数据
    private Integer total; //数据总页，分页使用

    public ApiResult(Integer code) {
        super(code, "操作成功");
    }

    public ApiResult(Integer code, String msg) {
        super(code, msg);
    }

    public ApiResult(Integer code, String msg, T data) {
        super(code, msg);
        this.data = data;
    }

    public static <T> Result<T> success() {
        ApiResult<T> result = new ApiResult<>(ResultCode.REQUEST_SUCCESS.getCode());
        result.setData(null);
        return result;
    }

    public static <T> Result<T> success(T data) {
        ApiResult<T> result = new ApiResult<>(ResultCode.REQUEST_SUCCESS.getCode());
        result.setData(data);
        return result;
    }

    /**
     * 分页响应，返回数据列表，总记录数
     */
    public static <T> ApiResult<T> success(T data, Integer total) {
        ApiResult<T> result = new ApiResult<>(ResultCode.REQUEST_SUCCESS.getCode());
        result.setData(data);
        result.setTotal(total);
        return result;
    }

    public static <T> Result<T> success(String msg) {
        return new Result<>(ResultCode.REQUEST_SUCCESS.getCode(), msg);
    }

    public static <T> Result<T> success(String msg, T data) {
        return new ApiResult<T>(ResultCode.REQUEST_SUCCESS.getCode(), msg, data);
    }


    public static <T> ApiResult<T> error(String msg) {
        ApiResult<T> result = new ApiResult<>(ResultCode.REQUEST_ERROR.getCode(), msg);
        return result;
    }

    public ApiResult(T data, Integer total) {
        this.data = data;
        this.total = total;
    }

    public ApiResult(Integer code, String msg, T data, Integer total) {
        super(code, msg);
        this.data = data;
        this.total = total;
    }
}