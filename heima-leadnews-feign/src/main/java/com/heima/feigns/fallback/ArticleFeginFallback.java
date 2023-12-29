package com.heima.feigns.fallback;

import com.heima.feigns.ArticleFegin;
import com.heima.model.article.pojos.ApAuthor;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * ClassName: ArticleFeginFallback
 * Package: com.heima.feigns.fallback
 * Description:
 *
 * @Author R
 * @Create 2023/12/29 10:24
 * @Version 1.0
 */
@Component
@Slf4j
public class ArticleFeginFallback implements FallbackFactory<ArticleFegin> {
    @Override
    public ArticleFegin create(Throwable throwable) {
        return new ArticleFegin() {
            @Override
            public ResponseResult<ApAuthor> findByUserId(Integer userId) {
                log.error("参数 userId : {}",userId);
                log.error("ArticleFeign findByUserId 远程调用出错啦 ~~~ !!!! {} ",throwable.getMessage());
                return ResponseResult.errorResult(AppHttpCodeEnum.SERVER_ERROR);
            }

            @Override
            public ResponseResult save(ApAuthor apAuthor) {
                log.error("参数 apAuthor: {}",apAuthor);
                log.error("ArticleFeign save 远程调用出错啦 ~~~ !!!! {} ",throwable.getMessage());
                return ResponseResult.errorResult(AppHttpCodeEnum.SERVER_ERROR);
            }
        };
    }
}
