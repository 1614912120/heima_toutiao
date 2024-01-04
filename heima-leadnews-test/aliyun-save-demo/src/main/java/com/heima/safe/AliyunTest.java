package com.heima.safe;
 
import aliyun.GreenImageScan;
import aliyun.GreenTextScan;
import com.heima.file.service.FileStorageService;
import com.heima.wemedia.WemediaApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
 
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
 
@SpringBootTest(classes = WemediaApplication.class)
@RunWith(SpringRunner.class)
public class AliyunTest {
 
    @Autowired
    private GreenTextScan greenTextScan;
 
    @Autowired
    private GreenImageScan greenImageScan;
 
    @Autowired
    private FileStorageService fileStorageService;
 
    @Test
    public void testScanText() throws Exception {
        Map map = greenTextScan.greeTextScan("王天刚去饭店吃饭后发现自己的车子被刮了，破口大骂傻逼是哪个干的法轮功");
        System.out.println(map.get("data"));
        System.out.println(map.get("suggestion"));
        System.out.println(map.get("label"));

    }
 
    @Test
    public void testScanImage() throws Exception {
        List<String> list=new ArrayList<>();
        list.add("https://heimadianpingryj.oss-cn-shanghai.aliyuncs.com/1584967491149.png?Expires=1704211351&OSSAccessKeyId=TMP.3KirUAGvuAMcrfbHhonpLXqy6yrAA2PZXDFA8syhXMqYKjz3pJBPHho62M7xxqNzSULAx28MjafG59JjdUbVWdGRymshX7&Signature=gvKjEUsWBQkngtPzqdTyZsoe8kg%3D");
        Map map = greenImageScan.imageScan(list);
        System.out.println("======================");
        String o = (String)map.get("suggestion");
        System.out.println(o);
        System.out.println(map);
    }
 
}