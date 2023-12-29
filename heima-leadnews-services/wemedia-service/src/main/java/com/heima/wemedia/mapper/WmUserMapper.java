package com.heima.wemedia.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.heima.model.wemedia.pojos.WmUser;
import io.swagger.annotations.Api;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ClassName: WmUserMapper
 * Package: com.heima.wemedia.mapper
 * Description:
 *
 * @Author R
 * @Create 2023/12/29 9:14
 * @Version 1.0
 */

public interface WmUserMapper extends BaseMapper<WmUser> {

}
