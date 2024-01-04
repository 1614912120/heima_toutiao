package com.heima.wemedia;

import com.heima.file.service.FileStorageService;
import com.heima.wemedia.service.WmNewsAutoScanService;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * ClassName: OssTest
 * Package: com.heima.wemedia
 * Description:
 *
 * @Author R
 * @Create 2023/12/30 11:08
 * @Version 1.0
 */
@SpringBootTest
public class OssTest {
    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    WmNewsAutoScanService wmNewsAutoScanService;

    @Test
    public void upload() {
        //prefix 存在哪里文件夹里
    }

    @Test
    public void sadsad() {
        wmNewsAutoScanService.autoScanWmNews(6286);
    }


}
