package com.heima.feigns;

import com.heima.config.HeimaFeignAutoConfiguration;
import com.heima.feigns.fallback.ArticleFeginFallback;
import com.heima.model.User.dtos.UserRelationDTO;
import com.heima.model.common.dtos.ResponseResult;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * ClassName: UserFegin
 * Package: com.heima.feigns
 * Description:
 *
 * @Author R
 * @Create 2024/1/13 19:35
 * @Version 1.0
 */
@Component
@FeignClient(
        value = "leadnews-user",
        fallbackFactory = ArticleFeginFallback.class,
        configuration = HeimaFeignAutoConfiguration.class
)
public interface UserFegin {
    @PostMapping("/api/v1/user/user_follow")
    public ResponseResult follow(@RequestBody UserRelationDTO dto);
}
