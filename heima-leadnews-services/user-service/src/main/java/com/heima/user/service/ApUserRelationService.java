package com.heima.user.service;
import com.heima.model.User.dtos.UserRelationDTO;
import com.heima.model.common.dtos.ResponseResult;
public interface ApUserRelationService {
    /**
     * 用户关注/取消关注
     * @param dto
     * @return
     */
    public ResponseResult follow(UserRelationDTO dto);
}