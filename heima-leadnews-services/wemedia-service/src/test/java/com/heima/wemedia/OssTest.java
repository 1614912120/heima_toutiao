package com.heima.wemedia;

import com.heima.file.service.FileStorageService;
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


    @Test
    public void upload() {
        //prefix 存在哪里文件夹里
    }

    @Test
    public void delete() {

    }
}
