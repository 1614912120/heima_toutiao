package com.heima.behavior.controller;

import com.heima.behavior.service.ApReadBehaviorService;
import com.heima.model.behavior.dto.ReadBehaviorDTO;
import com.heima.model.common.dtos.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ClassName: ApReadBehaviorController
 * Package: com.heima.behavior.controller
 * Description:
 *
 * @Author R
 * @Create 2024/1/13 11:12
 * @Version 1.0
 */
@Api(value = "行为管理api",tags = "行为管理api")
@RestController
@RequestMapping("/api/v1/read_behavior")
public class ApReadBehaviorController {

    @Autowired
    private ApReadBehaviorService apReadBehaviorService;


    @ApiOperation("浏览统计")
    @PostMapping
    public ResponseResult apReadBehavior(@RequestBody ReadBehaviorDTO dto) {
        ResponseResult responseResult = apReadBehaviorService.readBehavior(dto);
        return responseResult;
    }

}
