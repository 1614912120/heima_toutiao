package com.heima.wemedia.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.heima.model.wemedia.pojos.WmNewsMaterial;
import feign.Param;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * ClassName: WmNewsMaterialMapper
 * Package: com.heima.wemedia.mapper
 * Description:
 *
 * @Author R
 * @Create 2023/12/30 16:04
 * @Version 1.0
 */
@Mapper
public interface WmNewsMaterialMapper extends BaseMapper<WmNewsMaterial> {
    /**
     * 保存文章和素材的关联关系
     * @param wmMaterialIds 素材id集合
     * @param newsId        文章ID
     * @param type          文章封面类型  0 内容引用  1 封面引用
     */
    public void saveRelations(@Param("wmMaterialIds") List<Integer> wmMaterialIds,
                              @Param("newsId") Integer newsId,
                              @Param("type") Short type);
}
