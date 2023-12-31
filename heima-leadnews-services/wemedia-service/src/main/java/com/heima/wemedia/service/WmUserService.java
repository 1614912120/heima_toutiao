package com.heima.wemedia.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.WmUserDTO;
import com.heima.model.wemedia.pojos.WmUser;

/**
 * ClassName: WmUserService
 * Package: com.heima.wemedia.service
 * Description:
 *
 * @Author R
 * @Create 2023/12/29 9:14
 * @Version 1.0
 */
public interface WmUserService extends IService<WmUser> {

    //登录
    public ResponseResult login(WmUserDTO dto);
}
