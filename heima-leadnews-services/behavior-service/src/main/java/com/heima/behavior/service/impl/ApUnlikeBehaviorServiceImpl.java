package com.heima.behavior.service.impl;

import com.heima.behavior.service.ApUnlikeBehaviorService;
import com.heima.common.exception.CustException;
import com.heima.model.User.pojos.ApUser;
import com.heima.model.behavior.dto.UnLikesBehaviorDTO;
import com.heima.model.behavior.pojos.ApBehaviorEntry;
import com.heima.model.behavior.pojos.ApLikesBehavior;
import com.heima.model.behavior.pojos.ApUnlikesBehavior;
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
 * ClassName: ApUnlikeBehaviorServiceImpl
 * Package: com.heima.behavior.service.impl
 * Description:
 *
 * @Author R
 * @Create 2024/1/13 11:52
 * @Version 1.0
 */
@Service
public class ApUnlikeBehaviorServiceImpl implements ApUnlikeBehaviorService {
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private ApBehaviorEntryServiceImpl apBehaviorEntryServiceImpl;
    @Override
    public ResponseResult unlikeBehavior(UnLikesBehaviorDTO dto) {

//      1. 校验参数 文章id不能为空  需要登录 不喜欢类型取值为 0 或 1

        Long articleId = dto.getArticleId();
        if(articleId == null) {
            CustException.cust(AppHttpCodeEnum.PARAM_INVALID,"参数不确定");
        }
//      2. 查询行为实体数据
        ApUser user = AppThreadLocalUtils.getUser();
        ApBehaviorEntry byUserIdOrEquipmentId = apBehaviorEntryServiceImpl.findByUserIdOrEquipmentId(user.getId(), dto.getEquipmentId());
        if(byUserIdOrEquipmentId == null) {
            CustException.cust(AppHttpCodeEnum.PARAM_INVALID);
        }
//      3. 如果是 不喜欢操作 查询不喜欢行为是否存在
        Query query = Query.query(Criteria.where("entry_id").is(byUserIdOrEquipmentId.getEntryId()).and("article_id").is(dto.getArticleId()));
        ApUnlikesBehavior one = mongoTemplate.findOne(query, ApUnlikesBehavior.class);

        if(one != null) {
            //      5. 如果是 取消不喜欢操作  删除对应的不喜欢数据
//            Query queryLike = Query.query(Criteria.where("id").is(apLikesBehavior.getId()));
//            mongoTemplate.remove(queryLike, ApLikesBehavior.class);

            mongoTemplate.remove(one);
           return ResponseResult.okResult(dto.getType());
        }
        //      4. 如果不存在   添加不喜欢行为
        ApUnlikesBehavior apUnlikesBehavior = new ApUnlikesBehavior();
        apUnlikesBehavior.setArticleId(dto.getArticleId());
        apUnlikesBehavior.setEntryId(byUserIdOrEquipmentId.getEntryId());
        apUnlikesBehavior.setCreatedTime(new Date());
        apUnlikesBehavior.setType(dto.getType());
        mongoTemplate.save(apUnlikesBehavior);
        ResponseResult responseResult = ResponseResult.okResult(apUnlikesBehavior);
        return responseResult;
    }
}
