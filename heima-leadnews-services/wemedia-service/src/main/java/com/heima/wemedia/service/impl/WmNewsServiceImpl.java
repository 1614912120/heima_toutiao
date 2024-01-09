package com.heima.wemedia.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.common.constants.admin.AdminConstants.WemediaConstants;
import com.heima.common.exception.CustException;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.threadlocal.WmThreadLocalUtils;
import com.heima.model.wemedia.dtos.NewsAuthDTO;
import com.heima.model.wemedia.dtos.WmNewsDTO;
import com.heima.model.wemedia.dtos.WmNewsPageReqDTO;
import com.heima.model.wemedia.pojos.WmMaterial;
import com.heima.model.wemedia.pojos.WmNews;
import com.heima.model.wemedia.pojos.WmNewsMaterial;
import com.heima.model.wemedia.pojos.WmUser;
import com.heima.model.wemedia.vos.WmNewsVO;
import com.heima.wemedia.mapper.WmMaterialMapper;
import com.heima.wemedia.mapper.WmNewsMapper;
import com.heima.wemedia.mapper.WmNewsMaterialMapper;
import com.heima.wemedia.mapper.WmUserMapper;
import com.heima.wemedia.service.WmNewsService;
import com.heima.wemedia.service.WmUserService;
import lombok.extern.slf4j.Slf4j;
import message.NewsAutoScanConstants;
import message.NewsUpOrDownConstants;
import message.PublishArticleConstants;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.weaver.ast.Var;
import org.checkerframework.checker.units.qual.A;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.expression.spel.ast.NullLiteral;
import org.springframework.stereotype.Service;

import javax.swing.*;
import javax.xml.transform.Result;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * ClassName: WmNewsServiceImpl
 * Package: com.heima.wemedia.service.impl
 * Description:
 *
 * @Author R
 * @Create 2023/12/31 11:33
 * @Version 1.0
 */
@Service
@Slf4j
public class WmNewsServiceImpl extends ServiceImpl<WmNewsMapper, WmNews> implements WmNewsService {
    @Value("${file.oss.web-site}")
    String webSite;

    @Autowired
    private WmUserMapper wmUserMapper;
    @Override
    public ResponseResult findList(WmNewsPageReqDTO dto) {
        //参数检查
        if(dto == null) {
            CustException.cust(AppHttpCodeEnum.PARAM_INVALID);
        }
        //判断页数是否正常，不正常默认设
        dto.checkParam();

        LambdaQueryWrapper<WmNews> wrapper = new LambdaQueryWrapper<>();
        LambdaQueryWrapper<WmUser> Userwrapper = new LambdaQueryWrapper<>();
        //文章标题查询
        wrapper.like(StringUtils.isNotBlank(dto.getKeyword()),WmNews::getTitle,dto.getKeyword());
        //频道id
        wrapper.eq(dto.getChannelId() != null, WmNews::getChannelId,dto.getChannelId());
        //文章状态
        wrapper.eq(dto.getStatus() != null,WmNews::getStatus,dto.getStatus());
        //发布时间大于开始时间
        wrapper.ge(dto.getBeginPubDate()!= null, WmNews::getPublishTime,dto.getBeginPubDate());
        wrapper.le(dto.getBeginPubDate()!= null, WmNews::getPublishTime,dto.getBeginPubDate());

        //当前媒体人的文章
        WmUser user = WmThreadLocalUtils.getUser();
        if(user == null) {
            CustException.cust(AppHttpCodeEnum.NEED_LOGIN);
        }

        LambdaQueryWrapper<WmUser> wrapper1 = Userwrapper.eq(WmUser::getApUserId, user.getId());
        WmUser wmUser = wmUserMapper.selectOne(wrapper1);
        wrapper.eq(WmNews::getUserId,wmUser.getId());
        //创建时间倒叙
        wrapper.orderByDesc(WmNews::getCreatedTime);
        //分页
        Page<WmNews> pages = new Page<>(dto.getPage(), dto.getSize());
        IPage<WmNews> pageResult = page(pages, wrapper);
        PageResponseResult result = new PageResponseResult(dto.getPage(), dto.getSize(), pageResult.getTotal());
        result.setData(pageResult.getRecords());
        result.setHost(webSite);
        return result;
    }

    @Autowired
    RabbitTemplate rabbitTemplate;
    @Override

    public ResponseResult submitNews(WmNewsDTO dto) {
        //条件判断
        if(dto == null || dto.getContent() == null) {
            CustException.cust(AppHttpCodeEnum.PARAM_INVALID);
        }
        //保存或修改文章
        WmNews wmNews = new WmNews();
        List<String> strings = new ArrayList<>();
        //通过图片url查询素材的id
        for (String material : dto.getImages()) {
            String substring = material.substring(material.lastIndexOf("material/"));
            strings.add(substring);
        }
        BeanUtils.copyProperties(dto,wmNews);
        if(dto.getImages() != null && dto.getImages().size() !=0){
            //将集合转成字符串
            String imageStr = StringUtils.join(strings, ",");
            wmNews.setImages(imageStr);
        }
        //当前封面类型是-1
        if(dto.getType().equals(WemediaConstants.WM_NEWS_TYPE_AUTO)){
            wmNews.setType(null);
        }

        saveOrUpdateNews(wmNews);
        //判断是否事草稿
        if(dto.getStatus().equals(WmNews.Status.NORMAL.getCode())) {
           return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
        }
        //不是草稿，保存文章内容图片和素材的关系
        //提取文章图片信息
        List<String> materials = ectractUrlInfo(dto.getContent());
        //转换图片格式
        List<String> materialsStrings = new ArrayList<>();
        //通过图片url查询素材的id
        for (String material : materials) {
            String substring = material.substring(material.lastIndexOf("material/"));
            materialsStrings.add(substring);
        }
        saveRelativeInfoForContent(materialsStrings,wmNews.getId());
        //不是草稿，保存问斩封面图片和素材的关系
        saveRelativeInfoForCover(dto,wmNews,materialsStrings);

        //发送待审核的消息MQ
        rabbitTemplate.convertAndSend(NewsAutoScanConstants.WM_NEWS_AUTO_SCAN_QUEUE,wmNews.getId());
        log.info("mq  成功发送待审核的消息，文章id==",wmNews.getId());
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }
    @Override
    public ResponseResult downOrUp(WmNewsDTO dto) {
        //1.检查参数
        if(dto == null || dto.getId() == null){
            CustException.cust(AppHttpCodeEnum.PARAM_INVALID);
        }
        Short enable = dto.getEnable();
        if(enable == null ||
                (!WemediaConstants.WM_NEWS_UP.equals(enable)&&!WemediaConstants.WM_NEWS_DOWN.equals(enable))){
            CustException.cust(AppHttpCodeEnum.PARAM_INVALID,"上下架状态错误");
        }
        //2.查询文章
        WmNews wmNews = getById(dto.getId());
        if(wmNews == null){
            CustException.cust(AppHttpCodeEnum.DATA_NOT_EXIST,"文章不存在");
        }
        //3.判断文章是否发布
        if(!wmNews.getStatus().equals(WmNews.Status.PUBLISHED.getCode())){
            CustException.cust(AppHttpCodeEnum.DATA_NOT_EXIST,"当前文章不是发布状态，不能上下架");
        }
        //4.修改文章状态，同步到app端（后期做）TODO
        update(Wrappers.<WmNews>lambdaUpdate().eq(WmNews::getId,dto.getId())
                .set(WmNews::getEnable,dto.getEnable()));

        if (wmNews.getArticleId()!=null) {
            if(dto.getEnable().equals(WemediaConstants.WM_NEWS_UP)){
                // 上架消息
                rabbitTemplate.convertAndSend(NewsUpOrDownConstants.NEWS_UP_OR_DOWN_EXCHANGE,
                        NewsUpOrDownConstants.NEWS_UP_ROUTE_KEY,wmNews.getArticleId());
                log.info("发送了上架消息",wmNews.getArticleId());
            }else {
                // 下架消息
                rabbitTemplate.convertAndSend(NewsUpOrDownConstants.NEWS_UP_OR_DOWN_EXCHANGE,
                        NewsUpOrDownConstants.NEWS_DOWN_ROUTE_KEY,wmNews.getArticleId());
                log.info("发送了下架消息");
            }
        }
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    //封面类是自动，设置封面类型的数据
    //image》1 type 1
    //image>3 ytype 3
    private void saveRelativeInfoForCover(WmNewsDTO dto, WmNews wmNews, List<String> materials) {
        //是否有封面
        List<String> images = dto.getImages();
        //封面是自动类型，设置封面类型的数据
        if(dto.getType().equals(WemediaConstants.WM_NEWS_TYPE_AUTO)) {
            //多图
            if(materials.size() >= 3) {
                wmNews.setType(WemediaConstants.WM_NEWS_MANY_IMAGE);
                images = materials.stream().limit(3).collect(Collectors.toList());
            }else if(materials.size()>1 && materials.size()<3) {
                //单图
                wmNews.setType(WemediaConstants.WM_NEWS_SINGLE_IMAGE);
                images = materials.stream().limit(1).collect(Collectors.toList());
            }else {
                //无图
                wmNews.setType(WemediaConstants.WM_NEWS_NONE_IMAGE);
            }
            //修改文章
            if(images != null && images.size()>0) {
                wmNews.setImages(StringUtils.join(images,","));
            }
            updateById(wmNews);
        }
        if(images != null && images.size()>0) {
            saveRelativeInfo(images,wmNews.getId(),WemediaConstants.WM_IMAGE_REFERENCE);
        }
    }

    private void saveRelativeInfoForContent(List<String> materials, Integer newsId) {
        saveRelativeInfo(materials,newsId,WemediaConstants.WM_CONTENT_REFERENCE);
    }
    @Autowired
    private WmMaterialMapper wmMaterialMapper;

    //保存文章图片与素材的关系到数据库中
    private void saveRelativeInfo(List<String> materials, Integer newsId, Short wmContentReference) {
        if(materials!= null  && !materials.isEmpty()) {
            List<String> strings = new ArrayList<>();
            //通过图片url查询素材的id
            for (String material : materials) {
                String substring = material.substring(material.lastIndexOf("material/"));
                strings.add(substring);
            }
            List<WmMaterial> wmMaterials = wmMaterialMapper.selectList(Wrappers.<WmMaterial>lambdaQuery().in(WmMaterial::getUrl, strings));
            if(wmMaterials == null) {
                CustException.cust(AppHttpCodeEnum.DATA_NOT_EXIST);
            }
            if(strings.size() != wmMaterials.size()){
                CustException.cust(AppHttpCodeEnum.PARAM_INVALID);
            }
            List<Integer> collect = wmMaterials.stream().map(WmMaterial::getId).collect(Collectors.toList());

            //批量保存
            wmNewsMaterialMapper.saveRelations(collect,newsId,wmContentReference);
        }

    }

    private List<String> ectractUrlInfo(String content) {
        List<String> materials = new ArrayList<>();
        List<Map> maps = JSON.parseArray(content, Map.class);
        for (Map map : maps) {
            if(map.get("type").equals("image")) {
                String imgUrl = (String) map.get("value");
                materials.add(imgUrl);
            }
        }
        return materials;
    }




    @Autowired
    private WmNewsMaterialMapper wmNewsMaterialMapper;
    //保存修改
    private void saveOrUpdateNews(WmNews wmNews) {
        //补全属性
        wmNews.setCreatedTime(new Date());
        WmUser user = WmThreadLocalUtils.getUser();
        WmUser wmUser = wmUserMapper.selectOne(Wrappers.<WmUser>lambdaQuery().eq(WmUser::getApUserId, user.getId()));
        wmNews.setUserId(user.getId());
        wmNews.setSubmitedTime(new Date());
        wmNews.setEnable(WemediaConstants.WM_NEWS_UP); // 上架
        if(wmNews.getId() == null) {
            save(wmNews);
        }else {
            //修改
            //当前文章和素材关系表删除
            wmNewsMaterialMapper.delete(Wrappers.<WmNewsMaterial>lambdaQuery()
                    .eq(WmNewsMaterial::getNewsId,wmNews.getId()));
            updateById(wmNews);
        }
    }


    @Override
    public ResponseResult findWmNewsById(Integer id) {
        //1 参数检查
        if (id == null) {
            CustException.cust(AppHttpCodeEnum.PARAM_INVALID);
        }
        //2 执行查询
        WmNews wmNews = getById(id);
        if (wmNews == null) {
            CustException.cust(AppHttpCodeEnum.DATA_NOT_EXIST);
        }
        //3 返回结果
        ResponseResult result = ResponseResult.okResult(wmNews);
        result.setHost(webSite);
        return result;
    }

    @Autowired
    WmNewsMapper wmNewsMapper;
    /**
     * 查询文章列表
     * @param dto
     * @return
     */
    @Override
    public ResponseResult findList(NewsAuthDTO dto) {
        //1.检查参数
        dto.checkParam();
        //记录当前页
        int currentPage = dto.getPage();
        //设置起始页
        dto.setPage((dto.getPage()-1)*dto.getSize());
        if(StringUtils.isNotBlank(dto.getTitle())){
            dto.setTitle("%"+dto.getTitle()+"%");
        }

        //2.分页查询
        List<WmNewsVO> wmNewsVoList = wmNewsMapper.findListAndPage(dto);
        //统计多少条数据
        long count = wmNewsMapper.findListCount(dto);

        //3.结果返回
        ResponseResult result = new PageResponseResult(currentPage, dto.getSize(), count, wmNewsVoList);
        result.setHost(webSite);
        return result;
    }

    @Override
    public ResponseResult findWmNewsVo(Integer id) {
        if(id == null) {
            CustException.cust(AppHttpCodeEnum.PARAM_INVALID);
        }
        WmNews wmNews = getById(id);
        if(wmNews == null) {
            CustException.cust(AppHttpCodeEnum.DATA_NOT_EXIST);
        }
        WmUser wmUser = null;
        if(wmNews.getUserId() != null) {
            wmUser = wmUserMapper.selectById(wmNews.getUserId());
        }
        //封装信息 加上作者信息
        WmNewsVO wmNewsVO = new WmNewsVO();
        BeanUtils.copyProperties(wmNews,wmNewsVO);
        if(wmUser != null) {
            wmNewsVO.setAuthorName(wmUser.getName());
        }
        ResponseResult responseResult = ResponseResult.okResult(wmNewsVO);
        responseResult.setHost(webSite);
        return responseResult;
    }

    /**
     * 自媒体文章人工审核
     * @param status 2  审核失败  4 审核成功
     * @param dto
     * @return
     */
    @Override
    public ResponseResult updateStatus(Short status, NewsAuthDTO dto) {
        //参数检查
        if(dto == null || dto.getId() == null) {
            CustException.cust(AppHttpCodeEnum.PARAM_INVALID);
        }
        //查询文章
        WmNews wmNews = getById(dto.getId());
        //检查文章状态不能为9 已发布
        if(wmNews.getStatus().equals(WmNews.Status.PUBLISHED.getCode())) {
            CustException.cust(AppHttpCodeEnum.DATA_NOT_ALLOW,"文章已发布");
        }
        //修改文章状态
        wmNews.setStatus(status);
        if(StringUtils.isNotBlank(dto.getMsg())) {
            wmNews.setReason(dto.getMsg());
        }
        updateById(wmNews);
        //todo 发布延迟队列
        //5. 通知定时发布文章
        // 发布时间
        long publishTime = wmNews.getPublishTime().getTime();
        // 当前时间
        long nowTime = new Date().getTime();
        long remainTime = publishTime - nowTime;
        // 发布文章
        rabbitTemplate.convertAndSend(PublishArticleConstants.DELAY_DIRECT_EXCHANGE
                , PublishArticleConstants.PUBLISH_ARTICLE_ROUTE_KEY
                , wmNews.getId()
                , (message)->{                              // 延时消息 必设置
                    message.getMessageProperties().setHeader("x-delay",remainTime<=0?0:remainTime);
                    return message;
                }
        );
        log.info("立即发布文章通知成功发送，文章id : {}", wmNews.getId());
        //
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);

    }
}
