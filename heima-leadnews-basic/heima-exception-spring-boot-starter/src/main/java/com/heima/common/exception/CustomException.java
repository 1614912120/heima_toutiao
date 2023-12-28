package com.heima.common.exception;

import com.heima.model.common.enums.AppHttpCodeEnum;

/**
 * ClassName: CustomException
 * Package: com.heima.common.exception
 * Description:
 *
 * @Author R
 * @Create 2023/12/28 9:48
 * @Version 1.0
 */
public class CustomException extends RuntimeException{
    // 异常处理的枚举
    private AppHttpCodeEnum appHttpCodeEnum;
    public CustomException(AppHttpCodeEnum appHttpCodeEnum) {
        this.appHttpCodeEnum = appHttpCodeEnum;
    }
    public CustomException(AppHttpCodeEnum appHttpCodeEnum,String msg) {
        appHttpCodeEnum.setErrorMessage(msg);
        this.appHttpCodeEnum = appHttpCodeEnum;
    }
    public AppHttpCodeEnum getAppHttpCodeEnum() {
        return appHttpCodeEnum;
    }
}
