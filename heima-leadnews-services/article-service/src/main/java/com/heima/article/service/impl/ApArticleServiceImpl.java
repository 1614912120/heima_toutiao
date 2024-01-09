package com.heima.article.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.article.mapper.ApArticleConfigMapper;
import com.heima.article.mapper.ApArticleContentMapper;
import com.heima.article.mapper.ApArticleMapper;
import com.heima.article.mapper.AuthorMapper;
import com.heima.article.service.ApArticleService;
import com.heima.article.service.GeneratePageService;
import com.heima.common.constants.admin.AdminConstants.ArticleConstants;
import com.heima.common.exception.CustException;
import com.heima.feigns.AdminFegin;
import com.heima.feigns.ArticleFegin;
import com.heima.feigns.WemediaFeign;
import com.heima.model.admin.pojos.AdChannel;
import com.heima.model.article.dtos.ArticleHomeDTO;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.article.pojos.ApArticleConfig;
import com.heima.model.article.pojos.ApArticleContent;
import com.heima.model.article.pojos.ApAuthor;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.wemedia.pojos.WmNews;
import freemarker.template.TemplateException;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Slf4j
public class ApArticleServiceImpl extends ServiceImpl<ApArticleMapper, ApArticle> implements ApArticleService {

    @Autowired
    GeneratePageService generatePageService;

    @GlobalTransactional(rollbackFor = Exception.class,timeoutMills = 300000)
    @Override
    public void publishArticle(Integer newsId) throws TemplateException, IOException {
        //根据id查询并校验文章
        WmNews wmNews = getWmNews(newsId);
        //根据wmNews封装aparticle对象
        ApArticle apArticle = getApArticle(wmNews);
        //保存或修改article
        saveOrUpdateArticle(apArticle);
        //保存关联的配置和内容信息
        saveConfigAndContent(wmNews,apArticle);
        //todo 基于新的文章内容生成html静态页
        generatePageService.generateArticlePage(wmNews.getContent(),apArticle);
        //更新wmnews状态9
        updateWmNews(wmNews,apArticle);
        //todo 通过es更新索引库
    }

    @Autowired
    private ApArticleMapper apArticleMapper;
    @Value("${file.oss.web-site}")
    private String webSite;

    @Value("${file.minio.readPath}")
    String readPath;
    /**
     * 根据参数加载文章列表
     * @param loadtype 0为加载更多  1为加载最新
     * @param dto
     * @return
     */
    @Override
    public ResponseResult load(Short loadtype, ArticleHomeDTO dto) {
        //1 参数检查
        // 页大小
        Integer size = dto.getSize();
        if (size == null || size <= 0) {
            size = 10;
        }
        dto.setSize(size);
        // 频道
        if (StringUtils.isBlank(dto.getTag())) {
            dto.setTag(ArticleConstants.DEFAULT_TAG);
        }
        // 时间
        if (dto.getMaxBehotTime() == null) {
            dto.setMaxBehotTime(new Date());
        }
        if (dto.getMinBehotTime() == null) {
            dto.setMinBehotTime(new Date());
        }
        // 类型判断
        if (!loadtype.equals(ArticleConstants.LOADTYPE_LOAD_MORE) && !loadtype.equals(ArticleConstants.LOADTYPE_LOAD_NEW)) {
            loadtype = ArticleConstants.LOADTYPE_LOAD_MORE;
        }
        //2 执行查询
        List<ApArticle> articleList = apArticleMapper.loadArticleList(dto, loadtype);
        for (ApArticle article : articleList) {
            // 获取文章封面字段
            String images = article.getImages();
            if (StringUtils.isNotBlank(images)) {
                // 将封面按照,号切割   生成流
                images = Arrays.stream(images.split(","))
                        // 每一个路径添加前缀
                        .map(url -> webSite + url)
                        // 将加了前缀的路径  拼接成字符串
                        .collect(Collectors.joining(","));
                article.setImages(images);
            }
            //静态页路径添加访问前缀
            article.setStaticUrl(readPath + article.getStaticUrl());
        }
        //3 返回结果
        ResponseResult result = ResponseResult.okResult(articleList);
        return result;
    }

    private void updateWmNews(WmNews wmNews, ApArticle apArticle) {
        wmNews.setStatus(WmNews.Status.PUBLISHED.getCode());
        wmNews.setArticleId(apArticle.getId());
        ResponseResult responseResult = wemediaFeign.updateWmNews(wmNews);
        if(!responseResult.checkCode()) {
            log.info("远程调用自媒体文章接口失败");
            CustException.cust(AppHttpCodeEnum.REMOTE_SERVER_ERROR);
        }
    }

    private void saveConfigAndContent(WmNews wmNews, ApArticle apArticle) {
        //保存配置
        ApArticleConfig config = new ApArticleConfig();
        config.setArticleId(apArticle.getId());
        config.setIsComment(true);
        config.setIsForward(true);
        config.setIsDown(false);
        config.setIsDelete(false);
        apArticleConfigMapper.insert(config);
        //保存文章详情
        ApArticleContent apArticleContent = new ApArticleContent();
        apArticleContent.setArticleId(apArticle.getId());
        apArticleContent.setContent(wmNews.getContent());
        apArticleContentMapper.insert(apArticleContent);
    }

    @Autowired
    private ApArticleConfigMapper apArticleConfigMapper;
    @Autowired
    private ApArticleContentMapper apArticleContentMapper;
    private void saveOrUpdateArticle(ApArticle apArticle) {
        //判断文章id是否存在
        if(apArticle.getId() == null) {
            apArticle.setCollection(0); // 收藏数
            apArticle.setLikes(0);// 点赞数
            apArticle.setComment(0);// 评论数
            apArticle.setViews(0); // 阅读数
            //不存在 保存文章
            save(apArticle);
        }else {
            //存在 修改文章
            ApArticle oldArticle = getById(apArticle.getId());
            // 查询article是否存在
            if(oldArticle == null) {
                CustException.cust(AppHttpCodeEnum.DATA_NOT_EXIST);
            }
            //修改文章
            updateById(apArticle);
            //删除之前所关联的config 和content
            apArticleConfigMapper.delete(Wrappers.<ApArticleConfig>lambdaQuery().eq(ApArticleConfig::getArticleId,apArticle.getId()));
            apArticleContentMapper.delete(Wrappers.<ApArticleContent>lambdaQuery().eq(ApArticleContent::getArticleId,apArticle.getId()));
        }



    }

    @Autowired
    AdminFegin adminFegin;
    private ApArticle getApArticle(WmNews wmNews) {
        //创建对象
        ApArticle apArticle = new ApArticle();
        //拷贝属性
        BeanUtils.copyProperties(wmNews,apArticle);
        //补全其他属性
        apArticle.setId(wmNews.getArticleId());
        apArticle.setFlag((byte) 0);
        apArticle.setLayout(wmNews.getType());

        //补全频道信息
        ResponseResult<AdChannel> channelResult = adminFegin.findOne(wmNews.getChannelId());
        if(!channelResult.checkCode()) {
            CustException.cust(AppHttpCodeEnum.REMOTE_SERVER_ERROR);
        }
        AdChannel channel = channelResult.getData();
        if(channel == null) {
            CustException.cust(AppHttpCodeEnum.DATA_NOT_EXIST);
        }
        apArticle.setChannelName(channel.getName());
        //补全作者信息
        ApAuthor apAuthor = authorMapper.selectOne(Wrappers.<ApAuthor>lambdaQuery().eq(ApAuthor::getWmUserId, wmNews.getUserId()));
        if(apAuthor == null) {
            CustException.cust(AppHttpCodeEnum.DATA_NOT_EXIST);
        }
        apArticle.setAuthorId(Long.valueOf(apAuthor.getId()));
        apArticle.setAuthorName(apAuthor.getName());
        return apArticle;
    }

    @Autowired
    WemediaFeign wemediaFeign;
    @Autowired
    AuthorMapper authorMapper;
    private WmNews getWmNews(Integer newsId) {
        //根据id远程查询自媒体信息
        ResponseResult<WmNews> result = wemediaFeign.findWmNewsById(newsId);
        if(!result.checkCode()) {
            CustException.cust(AppHttpCodeEnum.REMOTE_SERVER_ERROR);
        }
        //判断news是否为null
        WmNews wmNews = result.getData();
        if(wmNews == null) {
            CustException.cust(AppHttpCodeEnum.DATA_NOT_EXIST);
        }
        //检查状态是否为4或者8
        short value = wmNews.getStatus().shortValue();
        if(WmNews.Status.SUCCESS.getCode()!= value && WmNews.Status.ADMIN_SUCCESS.getCode() != value){
            log.info("对应文章状态不为4，8不发布");
            CustException.cust(AppHttpCodeEnum.DATA_NOT_EXIST);
        }

        return wmNews;
    }
}