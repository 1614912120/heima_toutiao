package com.heima.feigns.fallback;

import com.heima.feigns.UserFegin;
import com.heima.model.User.dtos.UserRelationDTO;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * ClassName: UserFeginFallback
 * Package: com.heima.feigns.fallback
 * Description:
 *
 * @Author R
 * @Create 2024/1/13 19:35
 * @Version 1.0
 */
@Component
@Slf4j
public class UserFeginFallback implements FallbackFactory<UserFegin> {
    @Override
    public UserFegin create(Throwable throwable) {
        return new UserFegin() {
            @Override
            public ResponseResult follow(UserRelationDTO dto) {
                log.error("参数: {}",dto);
                log.error("user远程调用出错啦 ~~~ !!!! {} ",throwable.getMessage());
                return ResponseResult.errorResult(AppHttpCodeEnum.REMOTE_SERVER_ERROR);
            }
        };
    }
}
