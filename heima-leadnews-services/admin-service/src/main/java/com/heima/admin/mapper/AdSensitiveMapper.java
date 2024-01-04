package com.heima.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.heima.model.admin.pojos.AdSensitive;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * ClassName: AdSensitiveMapper
 * Package: com.heima.admin.mapper
 * Description:
 *
 * @Author R
 * @Create 2024/1/2 22:47
 * @Version 1.0
 */
public interface AdSensitiveMapper extends BaseMapper<AdSensitive> {
    @Select("select sensitives from ad_sensitive")
    List<String> findAllSensitives();
}
