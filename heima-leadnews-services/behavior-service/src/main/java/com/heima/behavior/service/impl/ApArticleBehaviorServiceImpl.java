package com.heima.behavior.service.impl;

import com.alibaba.fastjson.JSON;
import com.heima.behavior.service.ApArticleBehaviorService;
import com.heima.behavior.service.ApBehaviorEntryService;
import com.heima.behavior.service.ApLikesBehaviorService;
import com.heima.common.constants.admin.AdminConstants.UserRelationConstants;
import com.heima.common.exception.CustException;
import com.heima.feigns.UserFegin;
import com.heima.model.User.pojos.ApUser;
import com.heima.model.behavior.dto.ArticleBehaviorDTO;
import com.heima.model.behavior.pojos.ApBehaviorEntry;
import com.heima.model.behavior.pojos.ApCollection;
import com.heima.model.behavior.pojos.ApLikesBehavior;
import com.heima.model.behavior.pojos.ApUnlikesBehavior;
import com.heima.model.behavior.vo.behaviorVo;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.threadlocal.AppThreadLocalUtils;
import org.apache.commons.collections.FastArrayList;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

/**
 * ClassName: ApArticleBehaviorServiceImpl
 * Package: com.heima.behavior.service.impl
 * Description:
 *
 * @Author R
 * @Create 2024/1/13 13:35
 * @Version 1.0
 */
@Service
public class ApArticleBehaviorServiceImpl implements ApArticleBehaviorService {

    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private ApLikesBehaviorService apLikesBehaviorService;

    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private ApBehaviorEntryService apBehaviorEntryService;
    @Override
    public String loadArticleBehavior(ArticleBehaviorDTO dto) {
//        1. 判断用户是否登录
        ApUser user = AppThreadLocalUtils.getUser();
        behaviorVo behaviorVo = new behaviorVo();
        if(user == null) {
            //        2. 未登录 直接将 4种行为 均返回false
            com.heima.model.behavior.vo.behaviorVo behaviorVo1 = new behaviorVo();
            behaviorVo1.setIslike(false);
            behaviorVo1.setIsfollow(false);
            behaviorVo1.setIscollection(false);
            behaviorVo1.setIsunlike(false);
        }else {
            ApBehaviorEntry byUserIdOrEquipmentId = apBehaviorEntryService.findByUserIdOrEquipmentId(user.getId(), dto.getEquipmentId());

            Query entry_id = Query.query(Criteria.where("entry_id").is(byUserIdOrEquipmentId.getEntryId()));
//        3. 如果登录
            ApBehaviorEntry one = mongoTemplate.findOne(entry_id, ApBehaviorEntry.class);
//        4. 查询行为实体
            if(one == null) {
                CustException.cust(AppHttpCodeEnum.PARAM_INVALID);
            }

//        5. 根据行为实体 文章ID查询 是否点赞
            Query query = Query.query(Criteria.where("entry_id").is(one.getEntryId()).and("article_id").is(dto.getArticleId()));
            ApLikesBehavior like = mongoTemplate.findOne(query, ApLikesBehavior.class);
            if(like != null) {
                behaviorVo.setIslike(true);
            }
//        6. 根据行为实体 文章ID查询 是否不喜欢
            ApUnlikesBehavior unlike = mongoTemplate.findOne(query, ApUnlikesBehavior.class);
            if(unlike != null) {
                behaviorVo.setIsunlike(true);
            }
//        7. 根据行为实体 文章ID查询 是否收藏
            ApCollection collect = mongoTemplate.findOne(query, ApCollection.class);
            if(collect != null) {
                behaviorVo.setIscollection(true);
            }
//        8. 更具登录用户id 去redis中查询是否关注该作者
            ZSetOperations<String, String> zSetOperations = redisTemplate.opsForZSet();
//            zSetOperations.score(UserRelationConstants.FOLLOW_LIST+user.getId(),)
            Integer authorApUserId = dto.getAuthorApUserId();
            Double score = zSetOperations.score(UserRelationConstants.FOLLOW_LIST + user.getId(), authorApUserId.toString());
            if(score !=null) {
                behaviorVo.setIsfollow(true);
            }else {
                behaviorVo.setIsfollow(false);
            }
//        9. 封装结果 返回
        }
//        String behaviorVos = behaviorVo.toJsonString();
        String s = JSON.toJSONString(behaviorVo);
        return s;
    }
}
