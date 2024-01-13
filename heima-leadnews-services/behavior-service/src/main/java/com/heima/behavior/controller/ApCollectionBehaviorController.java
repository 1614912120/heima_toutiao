package com.heima.behavior.controller;

import com.heima.behavior.service.ApCollectionBehaviorService;
import com.heima.behavior.service.ApLikesBehaviorService;
import com.heima.model.behavior.dto.CollectionBehaviorDTO;
import com.heima.model.behavior.dto.LikesBehaviorDTO;
import com.heima.model.behavior.pojos.ApCollection;
import com.heima.model.common.dtos.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ClassName: ApCollectionBehaviorController
 * Package: com.heima.behavior.controller
 * Description:
 *
 * @Author R
 * @Create 2024/1/13 13:25
 * @Version 1.0
 */
@Api(value = "行为管理api",tags = "行为管理api")
@RestController
@RequestMapping("/api/v1/collection_behavior")
public class ApCollectionBehaviorController {
    @Autowired
    private ApCollectionBehaviorService apCollectionBehaviorService;

    @ApiOperation("收藏")
    @PostMapping
    public ResponseResult ApCollectionBehavior(@RequestBody CollectionBehaviorDTO dto) {
        ResponseResult like = apCollectionBehaviorService.collectBehavior(dto);
        return like;
    }
}
