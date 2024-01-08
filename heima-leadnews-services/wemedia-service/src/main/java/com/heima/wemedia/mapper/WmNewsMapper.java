package com.heima.wemedia.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.heima.model.wemedia.dtos.NewsAuthDTO;
import com.heima.model.wemedia.pojos.WmNews;
import com.heima.model.wemedia.vos.WmNewsVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * ClassName: WmNewsMapper.xml
 * Package: com.heima.wemedia.mapper
 * Description:
 *
 * @Author R
 * @Create 2023/12/31 11:30
 * @Version 1.0
 */
@Mapper
public interface WmNewsMapper extends BaseMapper<WmNews> {
    List<WmNewsVO> findListAndPage(@Param("dto")NewsAuthDTO dto);
    long findListCount(@Param("dto") NewsAuthDTO dto);
}
