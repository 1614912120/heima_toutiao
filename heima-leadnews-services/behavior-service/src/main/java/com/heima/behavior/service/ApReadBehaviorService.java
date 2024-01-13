package com.heima.behavior.service;

import com.heima.model.behavior.dto.ReadBehaviorDTO;
import com.heima.model.common.dtos.ResponseResult;

/**
 * ClassName: ApReadBehaviorService
 * Package: com.heima.behavior.controller
 * Description:
 *
 * @Author R
 * @Create 2024/1/12 13:13
 * @Version 1.0
 */

public interface ApReadBehaviorService {
    /**
     * 记录阅读行为
     * @param dto
     * @return
     */
    ResponseResult readBehavior(ReadBehaviorDTO dto);
}
