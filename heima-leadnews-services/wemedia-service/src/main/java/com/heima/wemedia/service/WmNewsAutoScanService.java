package com.heima.wemedia.service;

/**
 * ClassName: WmNewsAutoScanService
 * Package: com.heima.wemedia.service
 * Description:
 *
 * @Author R
 * @Create 2024/1/2 23:29
 * @Version 1.0
 */
public interface WmNewsAutoScanService {
    /**
     * 自媒体文章审核
     * @param id  自媒体文章id
     */
    public void autoScanWmNews(Integer id);
}
