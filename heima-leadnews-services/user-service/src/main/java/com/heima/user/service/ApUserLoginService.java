package com.heima.user.service;

import com.heima.model.User.dtos.LoginDTO;
import com.heima.model.common.dtos.ResponseResult;

/**
 * ClassName: ApUserLoginService
 * Package: com.heima.user.service
 * Description:
 *
 * @Author R
 * @Create 2024/1/8 21:19
 * @Version 1.0
 */
public interface ApUserLoginService {

    public ResponseResult login(LoginDTO dto);
}
