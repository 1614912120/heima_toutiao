package com.heima.wemedia;

import com.heima.file.service.FileStorageService;
import com.heima.wemedia.service.WmNewsAutoScanService;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

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
    WmNewsAutoScanService wmNewsAutoScanService;

    @Test
    public void upload() {
        //prefix 存在哪里文件夹里
    }


    @Test
    public void sadsad() {
        wmNewsAutoScanService.autoScanWmNews(6286);
    }

    // 指定MinIo实现
    @Resource(name = "minIOFileStorageService")
    FileStorageService fileStorageService;
    // 不指定 beanName 注入的是OSS的实现
    @Autowired
    FileStorageService fileStorageService2;

    @Value("${file.minio.readPath}")
    private String readPath;
    @Test
    public void uploadToMinIo() throws FileNotFoundException {
        System.out.println(fileStorageService);
        System.out.println(fileStorageService2);
        // 准备好一个静态页
        FileInputStream fileInputStream = new FileInputStream("D://list.html");
        // 将静态页上传到minIO文件服务器中          文件名称            文件类型             文件流
        String aa = fileStorageService.store("aa", "list.html", "text/html", fileInputStream);
        System.out.println(readPath +aa );
    }
}
