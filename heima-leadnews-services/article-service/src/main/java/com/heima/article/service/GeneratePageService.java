package com.heima.article.service;
import com.heima.model.article.pojos.ApArticle;
import freemarker.template.TemplateException;

import java.io.IOException;

public interface GeneratePageService {
    /**
     * 生成文章静态页
     */
    void generateArticlePage(String content, ApArticle apArticle) throws IOException, TemplateException;
}