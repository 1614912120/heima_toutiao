package com.heima.wemedia.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.common.exception.CustException;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.wemedia.dtos.WmUserDTO;
import com.heima.model.wemedia.pojos.WmUser;
import com.heima.model.wemedia.vos.WmUserVO;
import com.heima.utils.common.AppJwtUtil;
import com.heima.wemedia.mapper.WmUserMapper;
import com.heima.wemedia.service.WmUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.security.DigestException;
import java.util.HashMap;
import java.util.Map;

/**
 * ClassName: WmUserServiceImpl
 * Package: com.heima.wemedia.service.impl
 * Description:
 *
 * @Author R
 * @Create 2023/12/29 9:15
 * @Version 1.0
 */
@Service
public class WmUserServiceImpl extends ServiceImpl<WmUserMapper, WmUser> implements WmUserService {
    @Override
    public ResponseResult login(WmUserDTO dto) {
        //校验参数 name password
        String name = dto.getName();
        String password = dto.getPassword();
        if(StringUtils.isBlank(name) || StringUtils.isBlank(password)) {
            CustException.cust(AppHttpCodeEnum.PARAM_INVALID,"用户名和密码不能为空");
        }
        //根据用户查询用户
        WmUser wmUser = this.getOne(Wrappers.<WmUser>lambdaQuery().eq(WmUser::getName, dto.getName()));
        if(wmUser == null) {
            CustException.cust(AppHttpCodeEnum.DATA_NOT_EXIST,"用户不存在");
        }
        //判断密码和数据库是否一致
        String inputPwd = DigestUtils.md5DigestAsHex((dto.getPassword() + wmUser.getSalt()).getBytes());
        if(!inputPwd.equals(wmUser.getPassword())) {
            CustException.cust(AppHttpCodeEnum.LOGIN_STATUS_ERROR);
        }
        //判断状态
        Integer status = wmUser.getStatus();
        if(status != 9) {
            CustException.cust(AppHttpCodeEnum.LOGIN_STATUS_ERROR);
        }
        //token发布
        String token = AppJwtUtil.getToken(Long.valueOf(wmUser.getApUserId()));
        //封装返回结果
        WmUserVO userVO = new WmUserVO();
        BeanUtils.copyProperties(wmUser,userVO);
        Map result = new HashMap<>();
        result.put("token",token);
        result.put("user",userVO);
        return ResponseResult.okResult(result);
    }
}
