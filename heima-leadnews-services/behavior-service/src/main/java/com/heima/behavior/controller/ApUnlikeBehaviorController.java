package com.heima.behavior.controller;

import com.heima.behavior.service.ApUnlikeBehaviorService;
import com.heima.model.behavior.dto.ReadBehaviorDTO;
import com.heima.model.behavior.dto.UnLikesBehaviorDTO;
import com.heima.model.behavior.pojos.ApUnlikesBehavior;
import com.heima.model.common.dtos.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ClassName: ApUnlikeBehaviorController
 * Package: com.heima.behavior.controller
 * Description:
 *
 * @Author R
 * @Create 2024/1/13 12:02
 * @Version 1.0
 */
@Api(value = "行为管理api",tags = "行为管理api")
@RestController
@RequestMapping("/api/v1/un_likes_behavior")
public class ApUnlikeBehaviorController {

    @Autowired
    private ApUnlikeBehaviorService apUnlikeBehaviorService;

    @ApiOperation("浏览统计")
    @PostMapping
    public ResponseResult ApUnlikeBehavior(@RequestBody UnLikesBehaviorDTO dto) {
        ResponseResult responseResult = apUnlikeBehaviorService.unlikeBehavior(dto);
        return responseResult;
    }
}
