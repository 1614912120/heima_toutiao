package com.heima.common.exception;

/**
 * ClassName: ExceptionCatch
 * Package: com.heima.common.exception
 * Description:
 *
 * @Author R
 * @Create 2023/12/28 9:39
 * @Version 1.0
 */

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@Configuration
@RestControllerAdvice
public class ExceptionCatch {
    /**
     * 解决项目中所有的异常拦截
     * @return
     */
    @ExceptionHandler(Exception.class)  // exception 所有子类
    public ResponseResult exception(Exception ex) {
        ex.printStackTrace();
        // 记录日志
        log.error("ExceptionCatch ex:{}", ex);
        return ResponseResult.errorResult(AppHttpCodeEnum.SERVER_ERROR, "您的网络异常，请稍后重试");
    }

    /**
     * 拦截自定义异常
     * @return
     */
    @ExceptionHandler(CustomException.class)
    public ResponseResult custException(CustomException ex) {
        ex.printStackTrace();
        log.error("CustomException ex:{}", ex);
        AppHttpCodeEnum codeEnum = ex.getAppHttpCodeEnum();
        return ResponseResult.errorResult(codeEnum);
    }

    /**
     * 参数校验
     * @return
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseResult handleValidationException(MethodArgumentNotValidException ex) {
        log.error("MethodArgumentNotValidException ex:{}", ex);
        ex.printStackTrace();
        return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID, ex.getBindingResult().getFieldError().getDefaultMessage());
    }
}
