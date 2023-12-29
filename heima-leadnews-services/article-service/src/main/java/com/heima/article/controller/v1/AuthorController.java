package com.heima.article.controller.v1;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.heima.article.service.AuthorService;
import com.heima.model.article.pojos.ApAuthor;
import com.heima.model.common.dtos.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * ClassName: AuthorController
 * Package: com.heima.article.controller.v1
 * Description:
 *
 * @Author R
 * @Create 2023/12/29 10:18
 * @Version 1.0
 */
@Api(value = "app作者管理API",tags = "app作者管理API")
@RestController
@RequestMapping("/api/v1/author")
public class AuthorController {
    @Autowired
    private AuthorService authorService;
    @ApiOperation(value = "查询作者",notes = "根据appUserId查询关联作者信息")
    @GetMapping("/findByUserId/{userId}")
    public ResponseResult<ApAuthor> findByUserId(@PathVariable("userId") Integer userId) {
        //根据用户id查作者信息
        ApAuthor one = authorService.getOne(Wrappers.<ApAuthor>lambdaQuery().eq(ApAuthor::getUserId, userId));
        return ResponseResult.okResult(one);
    }

    @ApiOperation(value = "保存作者",notes = "保存作者信息")
    @PostMapping("/save")
    public ResponseResult save(@RequestBody ApAuthor apAuthor) {
        authorService.save(apAuthor);
        return ResponseResult.okResult();
    }
}
