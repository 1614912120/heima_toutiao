package com.heima.behavior.controller;

import com.heima.behavior.service.ApLikesBehaviorService;
import com.heima.model.behavior.dto.LikesBehaviorDTO;
import com.heima.model.common.dtos.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ClassName: ApBehaviorController
 * Package: com.heima.behavior.controller
 * Description:
 *
 * @Author R
 * @Create 2024/1/10 11:36
 * @Version 1.0
 */
@Api(value = "行为管理api",tags = "行为管理api")
@RestController
@RequestMapping("/api/v1")
public class ApLikesBehaviorController {
    @Autowired
    private ApLikesBehaviorService apLikesBehaviorService;

    @ApiOperation("点赞")
    @PostMapping("/likes_behavior")
    public ResponseResult apLikesBehavior(@RequestBody LikesBehaviorDTO dto) {
        ResponseResult like = apLikesBehaviorService.like(dto);
        return like;
    }
}
