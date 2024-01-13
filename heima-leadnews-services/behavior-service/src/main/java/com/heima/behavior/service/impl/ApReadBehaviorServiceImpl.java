package com.heima.behavior.service.impl;

import com.heima.behavior.service.ApBehaviorEntryService;
import com.heima.behavior.service.ApReadBehaviorService;
import com.heima.common.exception.CustException;
import com.heima.model.User.pojos.ApUser;
import com.heima.model.behavior.dto.ReadBehaviorDTO;
import com.heima.model.behavior.pojos.ApBehaviorEntry;
import com.heima.model.behavior.pojos.ApLikesBehavior;
import com.heima.model.behavior.pojos.ApReadBehavior;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.threadlocal.AppThreadLocalUtils;
import com.heima.utils.common.DateUtils;
import com.mongodb.client.result.UpdateResult;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.statement.select.MinusOp;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * ClassName: ApReadBehaviorServiceImpl
 * Package: com.heima.behavior.service.impl
 * Description:
 *
 * @Author R
 * @Create 2024/1/12 13:14
 * @Version 1.0
 */
@Service
@Slf4j
public class ApReadBehaviorServiceImpl implements ApReadBehaviorService {

    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private ApBehaviorEntryServiceImpl apBehaviorEntryService;
    @Override
    public ResponseResult readBehavior(ReadBehaviorDTO dto) {
        //校验参数
        Long articleId = dto.getArticleId();
        if(articleId==null){
            CustException.cust(AppHttpCodeEnum.DATA_NOT_EXIST,"文章id为null");
        }
        ApUser user = AppThreadLocalUtils.getUser();
        //根据登录用户id或者设备id查询
        ApBehaviorEntry byUserIdOrEquipmentId = apBehaviorEntryService.findByUserIdOrEquipmentId(user.getId(), dto.getEquipmentId());
        if(byUserIdOrEquipmentId == null) {
            CustException.cust(AppHttpCodeEnum.PARAM_INVALID,"参数有误");
        }
        //判断阅读行为是否存在
        Query query = Query.query(Criteria.where("entry_id").is(byUserIdOrEquipmentId.getEntryId()).and("article_id").is(dto.getArticleId()));
        ApReadBehavior one = mongoTemplate.findOne(query, ApReadBehavior.class);
        if(one != null) {
            //存在将阅读count字段加1

            Update update = new Update();
            update.set("count",one.getCount()+1);
            update.set("updated_time",new Date());
            UpdateResult updateResult = mongoTemplate.updateMulti(query, update, ApReadBehavior.class);
            log.info(updateResult.toString());
        }else{
            //不存在 创建阅读行为并初始化count字段值为 1
            ApReadBehavior apReadBehavior = new ApReadBehavior();
            apReadBehavior.setEntryId(byUserIdOrEquipmentId.getEntryId());
            apReadBehavior.setCount((short) 1);
            apReadBehavior.setArticleId(dto.getArticleId());
            apReadBehavior.setCreatedTime(new Date());

            mongoTemplate.save(apReadBehavior);
        }

        return ResponseResult.okResult();
    }
}
