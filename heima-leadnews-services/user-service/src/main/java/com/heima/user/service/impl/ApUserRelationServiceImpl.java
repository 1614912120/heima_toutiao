package com.heima.user.service.impl;

import com.heima.common.constants.admin.AdminConstants.UserRelationConstants;
import com.heima.common.exception.CustException;
import com.heima.model.User.dtos.UserRelationDTO;
import com.heima.model.User.pojos.ApUser;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.threadlocal.AppThreadLocalUtils;
import com.heima.user.service.ApUserRelationService;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * ClassName: ApUserRelationServiceImpl
 * Package: com.heima.user.service
 * Description:
 *
 * @Author R
 * @Create 2024/1/9 10:48
 * @Version 1.0
 */
@Service
public class ApUserRelationServiceImpl implements ApUserRelationService {
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Override
    public ResponseResult follow(UserRelationDTO dto) {
        ZSetOperations<String, String> zSetOperations = redisTemplate.opsForZSet();
        //检查参数 判断是否登录  操作方式 0 1  自己不能关注自己
        ApUser loginUser = AppThreadLocalUtils.getUser();
        if(loginUser ==null) {
            CustException.cust(AppHttpCodeEnum.NEED_LOGIN);
        }
        int operation = dto.getOperation().intValue();
        if(operation !=0 &&  operation!=1) {
            CustException.cust(AppHttpCodeEnum.PARAM_INVALID);
        }
        //operation = 0 关注集合 粉丝集合 添加数据
        if(operation ==0) {
            if(loginUser.getId().equals(dto.getAuthorApUserId())) {
                CustException.cust(AppHttpCodeEnum.DATA_NOT_ALLOW,"自己不能关注自己");
            }
            Double score = zSetOperations.score(UserRelationConstants.FOLLOW_LIST + loginUser.getId(), String.valueOf(dto.getAuthorApUserId()));
            if(score != null) {
               CustException.cust(AppHttpCodeEnum.DATA_NOT_ALLOW,"请勿重复关注");
            }
            //关注
            zSetOperations.add(UserRelationConstants.FOLLOW_LIST+loginUser.getId(),String.valueOf(dto.getAuthorApUserId()),System.currentTimeMillis());
            //粉丝
            zSetOperations.add(UserRelationConstants.FANS_LIST+dto.getAuthorApUserId(),String.valueOf(loginUser.getId()),System.currentTimeMillis());
        }else {

            //operation = 1 关注集合 粉丝集合 删除数据
            zSetOperations.remove(UserRelationConstants.FOLLOW_LIST+loginUser.getId(),String.valueOf(dto.getAuthorApUserId()));
            zSetOperations.remove(UserRelationConstants.FANS_LIST+dto.getAuthorApUserId(),String.valueOf(loginUser.getId()));

        }
        return ResponseResult.okResult();
    }
}
