package com.heima.wemedia.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.heima.model.wemedia.pojos.WmMaterial;
import io.swagger.models.auth.In;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * ClassName: WmMaterialMapper
 * Package: com.heima.wemedia.mapper
 * Description:
 *
 * @Author R
 * @Create 2023/12/30 13:48
 * @Version 1.0
 */
@Mapper
public interface WmMaterialMapper extends BaseMapper<WmMaterial> {
    /**
     * 根据素材资源路径，查询相关素材id
     * @param urls 素材路径
     * @param userId
     * @return
     */
    public List<Integer> selectRelationsIds(@Param("urls") List<String> urls,
                                            @Param("userId") Integer userId);
}
