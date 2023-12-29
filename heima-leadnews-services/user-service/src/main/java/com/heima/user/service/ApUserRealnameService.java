package com.heima.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.User.dtos.AuthDTO;
import com.heima.model.User.pojos.ApUserRealname;
import com.heima.model.common.dtos.ResponseResult;

/**
 * ClassName: ApUserRealnameService
 * Package: com.heima.user.service
 * Description:
 *
 * @Author R
 * @Create 2023/12/29 8:21
 * @Version 1.0
 */
public interface ApUserRealnameService extends IService<ApUserRealname> {
    /**
     * 根据状态查询需要认证相关的用户信息
     * @param DTO
     * @return
     */
    ResponseResult loadListByStatus(AuthDTO DTO);

    ResponseResult updateStatusById(AuthDTO dto, Short status);
}
