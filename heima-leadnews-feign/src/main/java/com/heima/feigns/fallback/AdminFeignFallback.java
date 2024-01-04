package com.heima.feigns.fallback;

import com.heima.feigns.AdminFegin;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * ClassName: AdminFeignFallback
 * Package: com.heima.feigns.fallback
 * Description:
 *
 * @Author R
 * @Create 2024/1/2 23:19
 * @Version 1.0
 */
@Slf4j
@Component
public class AdminFeignFallback implements FallbackFactory<AdminFegin> {
    @Override
    public AdminFegin create(Throwable throwable) {
        return new AdminFegin() {
            @Override
            public ResponseResult<List<String>> sensitives() {
                log.error("AdminFeign sensitives 远程调用出错啦 ~~~ !!!! {} ",throwable.getMessage());
                return ResponseResult.errorResult(AppHttpCodeEnum.SERVER_ERROR);
            }
        };
    }
}
