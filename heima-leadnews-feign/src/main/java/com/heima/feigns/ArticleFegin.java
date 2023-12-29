package com.heima.feigns;

import com.heima.config.HeimaFeignAutoConfiguration;
import com.heima.feigns.fallback.ArticleFeginFallback;
import com.heima.model.article.pojos.ApAuthor;
import com.heima.model.common.dtos.ResponseResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * ClassName: ArticleFegin
 * Package: com.heima.feigns.article
 * Description:
 *
 * @Author R
 * @Create 2023/12/29 10:23
 * @Version 1.0
 */
@Component
@FeignClient(
        value = "leadnews-article",
        fallbackFactory = ArticleFeginFallback.class,
        configuration = HeimaFeignAutoConfiguration.class
)
public interface ArticleFegin {
    @GetMapping("/api/v1/author/findByUserId/{userId}")
    ResponseResult<ApAuthor> findByUserId(@PathVariable("userId") Integer userId);
    @PostMapping("/api/v1/author/save")
    ResponseResult save(@RequestBody ApAuthor apAuthor);
}
