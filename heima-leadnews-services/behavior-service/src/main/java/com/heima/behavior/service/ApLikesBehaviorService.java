package com.heima.behavior.service;

import com.heima.model.behavior.dto.LikesBehaviorDTO;
import com.heima.model.common.dtos.ResponseResult;

/**
 * ClassName: ApLikesBehaviorController
 * Package: com.heima.behavior.service
 * Description:
 *
 * @Author R
 * @Create 2024/1/10 11:03
 * @Version 1.0
 */
public interface ApLikesBehaviorService {
    /**
     * 点赞或取消点赞
     * @param dto
     * @return
     */
    public ResponseResult like(LikesBehaviorDTO dto);
}
