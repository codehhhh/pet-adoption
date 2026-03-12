package com.kmbeast.exception;

import com.kmbeast.pojo.api.Result;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * 全局异常处理器
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理HTTP方法不支持异常
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Result<?> handleHttpMethodNotSupported(
            HttpRequestMethodNotSupportedException e,
            HttpServletRequest request) {

        String requestUrl = request.getRequestURI();
        String requestMethod = e.getMethod();
        String supportedMethods;

        if (e.getSupportedHttpMethods() != null) {
            supportedMethods = e.getSupportedHttpMethods().toString();
        } else {
            supportedMethods = "未知";
        }

        // 构建错误信息
        String errorMsg = "请求方法不支持: 当前方法[" + requestMethod + "], 支持的方法: " + supportedMethods;

        // 详细的日志输出
        System.out.println("\n================================ HTTP方法错误 ================================");
        System.out.println("请求URL: " + requestUrl);
        System.out.println("请求方法: " + requestMethod);
        System.out.println("支持的方法: " + supportedMethods);
        System.out.println("客户端IP: " + request.getRemoteAddr());

        String userAgent = request.getHeader("User-Agent");
        if (userAgent != null) {
            System.out.println("用户代理: " + userAgent);
        }

        System.out.println("=======================================================================\n");

        // 提供修复建议
        if (requestUrl.contains("/pet-health-record/list") && "GET".equals(requestMethod)) {
            System.out.println("修复建议: 此接口需要使用POST方法，请检查前端调用");
        }
        if (requestUrl.contains("/pet/list") && "GET".equals(requestMethod)) {
            System.out.println("修复建议: 宠物列表接口需要使用POST方法，请检查前端调用");
        }

        // 创建Result对象返回
        Result<Object> result = new Result<>();
        result.setCode(405);
        result.setmessage(errorMsg);
        return result;
    }

    /**
     * 处理业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public Result<?> handleBusinessException(BusinessException e) {
        System.out.println("\n======================== 业务异常 ========================");
        System.out.println("异常信息: " + e.getMessage());
        System.out.println("========================================================\n");

        Result<Object> result = new Result<>();
        result.setCode(500);
        result.setmessage(e.getMessage());
        return result;
    }

    /**
     * 处理参数校验异常
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public Result<?> handleIllegalArgumentException(IllegalArgumentException e) {
        System.out.println("\n======================== 参数校验异常 ========================");
        System.out.println("异常信息: " + e.getMessage());
        System.out.println("==========================================================\n");

        Result<Object> result = new Result<>();
        result.setCode(400);
        result.setmessage(e.getMessage());
        return result;
    }

    /**
     * 处理空指针异常
     */
    @ExceptionHandler(NullPointerException.class)
    public Result<?> handleNullPointerException(NullPointerException e) {
        System.out.println("\n======================== 空指针异常 ========================");
        System.out.println("异常信息: " + e.getMessage());

        // 打印前几行堆栈信息便于调试
        StackTraceElement[] stackTrace = e.getStackTrace();
        if (stackTrace != null && stackTrace.length > 0) {
            System.out.println("异常位置: " + stackTrace[0].toString());
            if (stackTrace.length > 1) {
                System.out.println("调用链: " + stackTrace[1].toString());
            }
        }
        System.out.println("========================================================\n");

        Result<Object> result = new Result<>();
        result.setCode(500);
        result.setmessage("系统内部错误: 空指针异常");
        return result;
    }

    /**
     * 处理所有其他异常
     */
    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e, HttpServletRequest request) {
        // 检查是否是HTTP方法不支持异常
        if (e instanceof HttpRequestMethodNotSupportedException) {
            // 如果是，调用专门的处理方法
            return handleHttpMethodNotSupported((HttpRequestMethodNotSupportedException) e, request);
        }

        // 如果不是HTTP方法异常，按通用异常处理
        System.out.println("\n======================== 系统异常详情 ========================");
        System.out.println("请求URL: " + request.getRequestURI());
        System.out.println("请求方法: " + request.getMethod());
        System.out.println("异常类型: " + e.getClass().getName());
        System.out.println("异常信息: " + e.getMessage());

        // 对于开发环境，可以打印更多信息
        String activeProfile = System.getProperty("spring.profiles.active");
        if (activeProfile != null && !activeProfile.contains("prod")) {
            // 非生产环境，打印堆栈信息 - 使用循环而不是printStackTrace()
            System.out.println("堆栈轨迹:");
            StackTraceElement[] stackTrace = e.getStackTrace();
            int limit = Math.min(stackTrace.length, 10); // 只打印前10行
            for (int i = 0; i < limit; i++) {
                System.out.println("    at " + stackTrace[i]);
            }
            if (stackTrace.length > 10) {
                System.out.println("    ... " + (stackTrace.length - 10) + " more");
            }
        }

        System.out.println("========================================================\n");

        // 创建Result对象
        Result<Object> result = new Result<>();

        // 生产环境返回友好提示，开发环境返回详细错误
        if (activeProfile != null && activeProfile.contains("prod")) {
            result.setCode(500);
            result.setmessage("系统异常，请联系管理员");
        } else {
            result.setCode(500);
            result.setmessage("系统异常: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }

        return result;
    }
}