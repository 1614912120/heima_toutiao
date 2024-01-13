package com.heima.behavior.service.impl;

import com.alibaba.fastjson.JSON;
import com.heima.behavior.service.ApBehaviorEntryService;
import com.heima.behavior.service.ApLikesBehaviorService;
import com.heima.common.exception.CustException;
import com.heima.model.User.pojos.ApUser;
import com.heima.model.behavior.dto.LikesBehaviorDTO;
import com.heima.model.behavior.pojos.ApBehaviorEntry;
import com.heima.model.behavior.pojos.ApLikesBehavior;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.threadlocal.AppThreadLocalUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * ClassName: ApLikesBehaviorControllerImpl
 * Package: com.heima.behavior.service.impl
 * Description:
 *
 * @Author R
 * @Create 2024/1/10 11:05
 * @Version 1.0
 */
@Service
@Slf4j
public class ApLikesBehaviorServiceImpl implements ApLikesBehaviorService {
    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    private ApBehaviorEntryService behaviorEntryService;
    @Override
    public ResponseResult like(LikesBehaviorDTO dto) {
        if(dto.getEquipmentId() == null) {
            CustException.cust(AppHttpCodeEnum.PARAM_INVALID);
        }
        //判断登录是否是用户 非用户默认8888888
        ApUser user = AppThreadLocalUtils.getUser();
        Integer id = 0;
        if(user == null) {
            id = dto.getEquipmentId();
        }else {
           id = user.getId();
        }
        //获取文章id
        Long articleId = dto.getArticleId();
        Query query = Query.query(Criteria.where("article_id").is(articleId).and("entry_id").is(id));

        ApLikesBehavior apLikesBehavior = mongoTemplate.findOne(query, ApLikesBehavior.class);
        //判断是否点赞
        if(apLikesBehavior != null) {
            Query queryLike = Query.query(Criteria.where("id").is(apLikesBehavior.getId()));
            mongoTemplate.remove(queryLike,ApLikesBehavior.class);
            return ResponseResult.okResult(dto.getOperation());
        }
        ApLikesBehavior apLikesBehaviors = new ApLikesBehavior();
        apLikesBehaviors.setType(dto.getType());
        apLikesBehaviors.setCreatedTime(new Date());
        apLikesBehaviors.setArticleId(dto.getArticleId());
        apLikesBehaviors.setEntryId(user==null?dto.getEquipmentId():user.getId());
        apLikesBehaviors.setOperation(dto.getOperation());
        //点赞
        ApLikesBehavior save = mongoTemplate.save(apLikesBehaviors);
        log.info("保存的喜欢数据{save}",save);
        ResponseResult responseResult = ResponseResult.okResult(apLikesBehaviors);
        return responseResult;


    }
}
