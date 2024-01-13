package com.heima.behavior.controller;

import com.heima.behavior.service.ApArticleBehaviorService;
import com.heima.model.behavior.dto.ArticleBehaviorDTO;
import com.heima.model.behavior.dto.CollectionBehaviorDTO;
import com.heima.model.behavior.vo.behaviorVo;
import com.heima.model.common.dtos.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ClassName: ApArticleBehaviorController
 * Package: com.heima.behavior.controller
 * Description:
 *
 * @Author R
 * @Create 2024/1/13 20:24
 * @Version 1.0
 */
@Api(value = "行为管理api",tags = "行为管理api")
@RestController
@RequestMapping("/api/v1/article/load_article_behavior")
public class ApArticleBehaviorController {
    @Autowired
    private ApArticleBehaviorService apArticleBehaviorService;
    @ApiOperation("回显")
    @PostMapping
    public String ApCollectionBehavior(@RequestBody ArticleBehaviorDTO dto) {
        String behaviorVos = apArticleBehaviorService.loadArticleBehavior(dto);
        return behaviorVos;
    }
}
