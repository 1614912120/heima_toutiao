package com.heima.behavior.service.impl;

import com.heima.behavior.service.ApBehaviorEntryService;
import com.heima.behavior.service.ApCollectionBehaviorService;
import com.heima.common.exception.CustException;
import com.heima.model.User.pojos.ApUser;
import com.heima.model.behavior.dto.CollectionBehaviorDTO;
import com.heima.model.behavior.pojos.ApBehaviorEntry;
import com.heima.model.behavior.pojos.ApCollection;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.threadlocal.AppThreadLocalUtils;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * ClassName: ApCollectionBehaviorServiceImpl
 * Package: com.heima.behavior.service.impl
 * Description:
 *
 * @Author R
 * @Create 2024/1/13 12:52
 * @Version 1.0
 */
@Service
public class ApCollectionBehaviorServiceImpl implements ApCollectionBehaviorService {
    @Autowired
    private ApBehaviorEntryServiceImpl apBehaviorEntryServiceImpl;

    @Autowired
    private MongoTemplate mongoTemplate;
    @Override
    public ResponseResult collectBehavior(CollectionBehaviorDTO dto) {
//        校验参数 需要登录 文章id不能null  操作类型 0 或 1
        ApUser user = AppThreadLocalUtils.getUser();
        if(user == null) {
            CustException.cust(AppHttpCodeEnum.DATA_NOT_EXIST,"用户未登录");
        }
        Long articleId = dto.getArticleId();
        if(articleId == null) {
            CustException.cust(AppHttpCodeEnum.PARAM_INVALID);
        }

//        根据userId查询行为实体数据
        ApBehaviorEntry byUserIdOrEquipmentId = apBehaviorEntryServiceImpl.findByUserIdOrEquipmentId(user.getId(), dto.getEquipmentId());
        if(byUserIdOrEquipmentId == null) {
            CustException.cust(AppHttpCodeEnum.PARAM_INVALID);
        }
//        如果是收藏操作 判断是否已经收藏
        Query query = Query.query(Criteria.where("entry_id").is(byUserIdOrEquipmentId.getEntryId()).and("article_id").is(dto.getArticleId()));
        ApCollection one = mongoTemplate.findOne(query, ApCollection.class);
//        如果未收藏 新增收藏行为
        if(one == null) {
            ApCollection apCollection = new ApCollection();
            apCollection.setType(dto.getType());
            apCollection.setArticleId(dto.getArticleId());
            apCollection.setEntryId(byUserIdOrEquipmentId.getEntryId());
            apCollection.setCollectionTime(new Date());
            mongoTemplate.save(apCollection);
            return ResponseResult.okResult(apCollection);
        }
//        如果是取消收藏操作  删除收藏行为

        mongoTemplate.remove(one);
        return ResponseResult.okResult();
    }
}
