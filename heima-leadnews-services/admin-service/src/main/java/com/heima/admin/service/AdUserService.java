package com.heima.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.admin.mapper.AdUserMapper;
import com.heima.model.admin.dtos.AdUserDTO;
import com.heima.model.admin.pojos.AdUser;
import com.heima.model.common.dtos.ResponseResult;

/**
 * ClassName: AdUserService
 * Package: com.heima.admin.service
 * Description:
 *
 * @Author R
 * @Create 2023/12/28 15:12
 * @Version 1.0
 */
public interface AdUserService extends IService<AdUser> {
    /**
     * 登录功能
     * @param DTO
     * @return
     */
    ResponseResult login(AdUserDTO DTO);
}
