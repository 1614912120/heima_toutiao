package com.heima.article.service.impl;

import com.alibaba.fastjson.JSON;
import com.heima.article.mapper.ApArticleMapper;
import com.heima.article.mapper.AuthorMapper;
import com.heima.article.service.GeneratePageService;
import com.heima.common.exception.CustException;
import com.heima.file.service.FileStorageService;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.article.pojos.ApAuthor;
import com.heima.model.common.enums.AppHttpCodeEnum;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * ClassName: GeneratePageServiceImpl
 * Package: com.heima.article.service
 * Description:
 *
 * @Author R
 * @Create 2024/1/9 9:57
 * @Version 1.0
 */
@Service
@Slf4j
public class GeneratePageServiceImpl implements GeneratePageService {
    @Autowired
    private Configuration configuration;
    @Value("${file.minio.prefix}")
    private String prefix;
    @Resource(name = "minIOFileStorageService")
    private FileStorageService fileStorageService;
    @Autowired
    AuthorMapper authorMapper;
    @Autowired
    ApArticleMapper apArticleMapper;
    @Override
    public void generateArticlePage(String content, ApArticle apArticle) {
        //获取freemark模板

        try {
            Template template = configuration.getTemplate("article.ftl");
            //准备数据模型
            HashMap<String, Object> map = new HashMap<>();
            map.put("content", JSON.parseArray(content, Map.class));
            map.put("article",apArticle);
            ApAuthor apAuthor = authorMapper.selectById(apArticle.getAuthorId());
            map.put("authorApUserId",apAuthor.getUserId());
            StringWriter stringWriter = new StringWriter();
            template.process(map,stringWriter);
            //使用数据代替模板 输出到Stringwriter
            InputStream is = new ByteArrayInputStream(stringWriter.toString().getBytes());
            //封装输入流 字节数组输入流
            //将静态页内容上传到minio
            String path = fileStorageService.store(prefix, apArticle.getId() + ".html", "text/html", is);
            //修改文章的static——url静态页路径
            apArticle.setStaticUrl(path);
            apArticleMapper.updateById(apArticle);
            log.info("文章详情静态页生成成功 staticUrl=====> {}", path);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("文章详情静态页生成失败=====>articleId : {}    ========> {}", apArticle.getId(), e.getCause());
            CustException.cust(AppHttpCodeEnum.SERVER_ERROR,"文章详情静态页生成失败");
        }
    }
}
