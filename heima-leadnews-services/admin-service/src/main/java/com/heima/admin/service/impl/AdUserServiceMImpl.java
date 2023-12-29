package com.heima.admin.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.admin.mapper.AdUserMapper;
import com.heima.admin.service.AdUserService;
import com.heima.common.exception.CustException;
import com.heima.model.admin.dtos.AdUserDTO;
import com.heima.model.admin.pojos.AdUser;
import com.heima.model.admin.vos.AdUserVO;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.utils.common.AppJwtUtil;
import net.sf.jsqlparser.expression.CastExpression;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * ClassName: AdUserServiceMImpl
 * Package: com.heima.admin.service.impl
 * Description:
 *
 * @Author R
 * @Create 2023/12/28 15:12
 * @Version 1.0
 */
@Service
public class AdUserServiceMImpl extends ServiceImpl<AdUserMapper, AdUser> implements AdUserService {
    @Override
    public ResponseResult login(AdUserDTO dto) {
        //校验参数
        String name = dto.getName();
        String password = dto.getPassword();
        if(StringUtils.isBlank(name) || StringUtils.isBlank(password)){
            CustException.cust(AppHttpCodeEnum.PARAM_INVALID,"用户名密码不为空");
        }
        //是否存在
        AdUser one = getOne(Wrappers.<AdUser>lambdaQuery().eq(AdUser::getName, name));
        if(one == null) {
            CustException.cust(AppHttpCodeEnum.DATA_NOT_EXIST,"用户不存在");
        }
        //密码对比
        String inputPwd = DigestUtils.md5DigestAsHex((password + one.getSalt()).getBytes());
        if(!inputPwd.equals(one.getPassword())){
            CustException.cust(AppHttpCodeEnum.LOGIN_PASSWORD_ERROR);
        }
        //用户状态
        if(one.getStatus().intValue() != 9) {
            CustException.cust(AppHttpCodeEnum.LOGIN_STATUS_ERROR);
        }
        //修改最近登录时间
        one.setLoginTime(new Date());
        updateById(one);
        //生成token
        String token = AppJwtUtil.getToken(Long.valueOf(one.getId()));
        //返回
        AdUserVO userVO = new AdUserVO();
        BeanUtils.copyProperties(one,userVO);
        Map map = new HashMap<>();
        map.put("token", token);
        map.put("user", userVO);
        return ResponseResult.okResult(map);
    }
}
