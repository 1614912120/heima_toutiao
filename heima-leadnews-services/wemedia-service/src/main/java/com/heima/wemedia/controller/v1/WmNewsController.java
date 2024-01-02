package com.heima.wemedia.controller.v1;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.WmNewsDTO;
import com.heima.model.wemedia.dtos.WmNewsPageReqDTO;
import com.heima.wemedia.service.WmNewsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@Api(value = "自媒体文章管理API",tags = "自媒体文章管理API")
@RestController
@RequestMapping("/api/v1/news")
public class WmNewsController {
    @Autowired
    private WmNewsService wmNewsService;
    @ApiOperation("根据条件查询文章列表")
    @PostMapping("/list")
    public ResponseResult findAll(@RequestBody WmNewsPageReqDTO wmNewsPageReqDto){
        return wmNewsService.findList(wmNewsPageReqDto);
    }


    @PostMapping("/submit")
    public ResponseResult submitNews(@RequestBody WmNewsDTO dto) {
        return wmNewsService.submitNews(dto);
    }
}