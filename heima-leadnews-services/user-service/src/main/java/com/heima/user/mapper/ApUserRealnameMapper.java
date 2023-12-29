package com.heima.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.heima.model.User.dtos.AuthDTO;
import com.heima.model.User.pojos.ApUserRealname;
import com.heima.model.common.dtos.ResponseResult;

/**
 * ClassName: ApUserRealnameMapper
 * Package: com.heima.user.mapper
 * Description:
 *
 * @Author R
 * @Create 2023/12/29 8:19
 * @Version 1.0
 */
public interface ApUserRealnameMapper extends BaseMapper<ApUserRealname> {
    /**
     * 根据状态进行审核
     * @param dto
     * @param status  2 审核失败   9 审核成功
     * @return
     */
    ResponseResult updateStatusById(AuthDTO dto, Short status);
}
