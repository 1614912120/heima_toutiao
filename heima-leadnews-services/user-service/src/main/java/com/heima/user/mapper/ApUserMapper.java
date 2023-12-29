package com.heima.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.heima.model.User.pojos.ApUser;
import org.apache.ibatis.annotations.Mapper;
import org.jsoup.Connection;
import org.mybatis.spring.annotation.MapperScan;

/**
 * ClassName: ApUserMapper
 * Package: com.heima.user.mapper
 * Description:
 *
 * @Author R
 * @Create 2023/12/29 11:29
 * @Version 1.0
 */
@Mapper
public interface ApUserMapper extends BaseMapper<ApUser> {
}
