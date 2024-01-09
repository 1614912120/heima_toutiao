package com.heima.user.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.heima.common.exception.CustException;
import com.heima.model.User.dtos.LoginDTO;
import com.heima.model.User.pojos.ApUser;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.user.mapper.ApUserMapper;
import com.heima.user.service.ApUserLoginService;
import com.heima.utils.common.AppJwtUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.message.ReusableMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * ClassName: ApUserLoginServiceImpl
 * Package: com.heima.user.service
 * Description:
 *
 * @Author R
 * @Create 2024/1/8 21:20
 * @Version 1.0
 */
@Service
public class ApUserLoginServiceImpl implements ApUserLoginService {
    @Autowired
    private ApUserMapper apUserMapper;
    @Override
    public ResponseResult login(LoginDTO dto) {
        //判断手机号密码是否为空
        String phone = dto.getPhone();
        String password = dto.getPassword();
        if(StringUtils.isNotBlank(password) && StringUtils.isNotBlank(phone)){
            //不为空查询对应的用户是否存在
            ApUser apUser = apUserMapper.selectOne(Wrappers.<ApUser>lambdaQuery().eq(ApUser::getPhone, phone));
            if(Objects.isNull(apUser)) {
                CustException.cust(AppHttpCodeEnum.DATA_NOT_EXIST,"用户不存在");
            }
            //对比输入的密码和数据库中的密码是否一致
            String dbPassword = apUser.getPassword();
            String inputPassword = DigestUtils.md5DigestAsHex((password + apUser.getSalt()).getBytes());
            if(!inputPassword.equals(dbPassword)) {
                CustException.cust(AppHttpCodeEnum.LOGIN_PASSWORD_ERROR,"用户密码错误");
            }
            //颁发token
            String token = AppJwtUtil.getToken(Long.valueOf(apUser.getId()));
            Map map = new HashMap<>();
            map.put("token",token);
            apUser.setSalt("");
            apUser.setPassword("");
            map.put("user",apUser);
            return  ResponseResult.okResult(map);
        }else{
            //如果手机号为空，采用设备id登录
            if(dto.getEquipmentId() == null) {
                return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
            }
            //判断设备id是否存在
            String token = AppJwtUtil.getToken(0l);
            Map map = new HashMap();
            map.put("token",token);
            return ResponseResult.okResult(map);
            //直接颁发token userid存0
        }
    }
}
