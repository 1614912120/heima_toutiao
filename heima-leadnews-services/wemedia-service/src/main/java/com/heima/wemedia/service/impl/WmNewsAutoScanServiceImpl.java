package com.heima.wemedia.service.impl;

import aliyun.GreenImageScan;
import aliyun.GreenTextScan;
import com.alibaba.fastjson.JSONArray;
import com.heima.common.exception.CustException;
import com.heima.feigns.AdminFegin;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.wemedia.pojos.WmNews;
import com.heima.utils.common.SensitiveWordUtil;
import com.heima.wemedia.mapper.WmNewsMapper;
import com.heima.wemedia.service.WmNewsAutoScanService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * ClassName: WmNewsAutoScanServiceImpl
 * Package: com.heima.wemedia.service.impl
 * Description:
 *
 * @Author R
 * @Create 2024/1/2 23:30
 * @Version 1.0
 */
@Service
@Slf4j
public class WmNewsAutoScanServiceImpl implements WmNewsAutoScanService {

    //status字段：0 草稿  1 待审核  2 审核失败
    // 3 人工审核  4 人工审核通过  8 审核通过（待发布） 9 已发布
    @Autowired
    private WmNewsMapper wmNewsMapper;

    @Value("${file.oss.web-site}")
    private String webSite;
    @Override
    public void autoScanWmNews(Integer id) {
        //判断文章id是否为空
        log.info("文章自动审核 待审核文章id",id);
        if(id == null) {
            CustException.cust(AppHttpCodeEnum.PARAM_INVALID,"待审核文章id为空");
        }
        //根据id查询自媒体文章
        WmNews wmNews = wmNewsMapper.selectById(id);
        if(wmNews == null) {
            CustException.cust(AppHttpCodeEnum.DATA_NOT_EXIST,"对应的文章不存在");
        }
        //判断文章状态 必须是1 避免重复消费
        Short status = wmNews.getStatus();
        if(!(status.shortValue() == WmNews.Status.SUBMIT.getCode())) {
            log.info("当前状态不是待审核状态 无需在审核");
           return;
        }
        //todo 抽取文章中的所有文章内容 所有图片内容
        Map<String,Object> contentAndImage =  handleTextAndImages(wmNews);
        //todo dfa进行敏感词的审核  有敏感词不通过 通过继续下一步
        boolean scanSensitive =  handleSensitive(contentAndImage.get("content"),wmNews);
        if(scanSensitive) {
            //审核不成功
            log.info("文章审核不通过，包含敏感词");
            return;
        }

        // todo 视觉智能开放平台文本审核 有违规词汇
        boolean scanTest = false;
        try {
            scanTest = handleTextScan(contentAndImage.get("content"),wmNews);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if(!scanTest) {
            //审核不成功
            log.info("文章审核不通过，视觉智能开放平台 ----包含敏感词");
            return;
        }
        //todo 图片审核
        boolean sacnImages =  handleImageScan(contentAndImage.get("images"),wmNews);
        if(!sacnImages) {
            //审核不成功
            log.info("照片审核不通过，视觉智能开放平台 ----包含敏感词");
            return;
        }
        //todo 将文章状态改为8
        updateNews(WmNews.Status.SUCCESS.getCode(), "审核通过",wmNews);

    }

    @Autowired
    GreenImageScan greenImageScan;
    private boolean handleImageScan(Object images, WmNews wmNews) {
        boolean flag = false;
        try {
            Map map = greenImageScan.imageScan((List<String>) images);
            String suggestion = (String)map.get("suggestion");
            switch (suggestion) {
                case "block":
                    updateNews(WmNews.Status.FAIL.getCode(), "照片违规",wmNews);
                    break;
                case "review":
                    //人工
                    updateNews(WmNews.Status.FAIL.getCode(), "照片违规有不确定的因素，需要进一步人工审核",wmNews);
                    break;
                case "pass":
                    flag = true;
                    break;
                default:
                    updateNews(WmNews.Status.FAIL.getCode(), "调用异常，需要进一步人工审核",wmNews);
                    break;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return flag;
    }

    @Autowired
    private GreenTextScan greenTextScan;
    private boolean handleTextScan(Object content,WmNews wmNews) throws Exception {
        boolean flag = false;
        Map map = greenTextScan.greeTextScan((String) content);
        String suggestion = (String)map.get("suggestion");
        String data = (String) map.get("data");
        String label = (String) map.get("label");
        //pass：文本正常。
        //review：需要人工审核。
        //block：文本违规，可以直接删除或者做限制处理
        switch (suggestion) {
            case "block":
                updateNews(WmNews.Status.FAIL.getCode(), "文章内容包含违规词汇<<"+data+">>",wmNews);
                break;
            case "review":
                //人工
                updateNews(WmNews.Status.FAIL.getCode(), "文章有不确定的因素，需要进一步人工审核",wmNews);
                break;
            case "pass":
                flag = true;
                break;
            default:
                updateNews(WmNews.Status.FAIL.getCode(), "调用异常，需要进一步人工审核",wmNews);
                break;
        }
        return flag;
    }

    @Autowired
    AdminFegin adminFegin;

    private boolean handleSensitive(Object content, WmNews wmNews) {
        boolean flag = true;
        //远程调用敏感词列表
        ResponseResult<List<String>> sensitives = adminFegin.sensitives();
        if(!sensitives.checkCode()) {
            CustException.cust(AppHttpCodeEnum.REMOTE_SERVER_ERROR);
        }
        List<String> sensitivesData = sensitives.getData();
        //将敏感词转为dfa数据
        SensitiveWordUtil.initMap(sensitivesData);
        //基于dfa扫描，找到敏感词
        Map<String, Integer> map = SensitiveWordUtil.matchWords((String) content);
        //如果有敏感词 修改为2
        if(!CollectionUtils.isEmpty(map)) {
            updateNews(WmNews.Status.FAIL.getCode(),"文章中包含敏感词"+map,wmNews );
            flag = false;
        }
        return false;
    }

    private void updateNews(Short status,String reason,WmNews wmNews) {
        wmNews.setStatus(status);
        wmNews.setReason(reason);
        wmNewsMapper.updateById(wmNews);
    }


    //抽取文章中的所有文章内容 所有图片内容
    private Map<String, Object> handleTextAndImages(WmNews wmNews) {
        Map<String,Object> result = new HashMap<>();
        //判断不为空转成list<map> [{},{}]
        String contentJson = wmNews.getContent();
        if(StringUtils.isBlank(contentJson)) {
            CustException.cust(AppHttpCodeEnum.PARAM_INVALID,"没有值");
        }
        List<Map> contentMaps = JSONArray.parseArray(contentJson, Map.class);
        //抽取content中的所有文本 并拼接成一个字符串
        String content = contentMaps.stream()
                .filter(map -> "text".equals(map.get("type")))
                .map(map -> (String)map.get("value").toString())
                .collect(Collectors.joining("_hmtt_"));
        //将文本内容和标题拼接成一个字符串
        content =wmNews.getTitle()+ "_hmtt_"+content ;
        //将总的文本内容装进map
        result.put("content",content);

        //抽取文章中所有图片

        List<String> images = contentMaps.stream()
                .filter(m ->"image".equals(m.get("type")))
                .map(m->m.get("value").toString())
                .collect(Collectors.toList());

        //文章内容图片
        //封面中的图片
        String coverStr = wmNews.getImages();
        List<String> collect = Arrays.stream(coverStr.split(","))
                .map(url -> webSite + url)
                .collect(Collectors.toList());
        //合并内容图片和封面图片
        images.addAll(collect);
        images = images.stream().distinct().collect(Collectors.toList());
        result.put("images",images);
        return result;
    }
}
