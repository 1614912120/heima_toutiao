package com.heima.common.exception;

/**
 * ClassName: CustException
 * Package: com.heima.common.exception
 * Description:
 *
 * @Author R
 * @Create 2023/12/28 9:57
 * @Version 1.0
 */

import com.heima.model.common.enums.AppHttpCodeEnum;

/**
 * @Description:  抛异常工具类
 * @Version: V1.0
 */
public class CustException {
    public static void cust(AppHttpCodeEnum codeEnum) {
        throw new CustomException(codeEnum );
    }
    public static void cust(AppHttpCodeEnum codeEnum,String msg) {
        throw new CustomException(codeEnum,msg);
    }
}
