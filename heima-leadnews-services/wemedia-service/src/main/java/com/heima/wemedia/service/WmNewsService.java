package com.heima.wemedia.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.WmNewsDTO;
import com.heima.model.wemedia.dtos.WmNewsPageReqDTO;
import com.heima.model.wemedia.pojos.WmNews;

/**
 * ClassName: WmNewsService
 * Package: com.heima.wemedia.service
 * Description:
 *
 * @Author R
 * @Create 2023/12/31 11:31
 * @Version 1.0
 */
public interface WmNewsService extends IService<WmNews> {
    /**
     * 查询所有自媒体文章
     * @return
     */
    public ResponseResult findList(WmNewsPageReqDTO dto);

    //自媒体文章发布
    public ResponseResult submitNews(WmNewsDTO dto);
}
