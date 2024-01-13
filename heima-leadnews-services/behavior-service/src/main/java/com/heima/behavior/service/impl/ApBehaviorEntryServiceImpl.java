package com.heima.behavior.service.impl;

import com.heima.behavior.service.ApBehaviorEntryService;
import com.heima.common.exception.CustException;
import com.heima.model.behavior.pojos.ApBehaviorEntry;
import com.heima.model.common.enums.AppHttpCodeEnum;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

/**
 * ClassName: ApBehaviorEntryServiceImpl
 * Package: com.heima.behavior.service.impl
 * Description:
 *
 * @Author R
 * @Create 2024/1/10 10:50
 * @Version 1.0
 */
@Service
public class ApBehaviorEntryServiceImpl implements ApBehaviorEntryService {
    @Autowired
    MongoTemplate mongoTemplate;
    @Override
    public ApBehaviorEntry findByUserIdOrEquipmentId(Integer userId, Integer equipmentId) {
        if(userId != null) {
            Query entry_id = Query.query(Criteria.where("entry_id").is(userId));
            ApBehaviorEntry one = mongoTemplate.findOne(entry_id, ApBehaviorEntry.class);
            return one;
        }else if(equipmentId != null){
            Query entry_id = Query.query(Criteria.where("entry_id").is(equipmentId));
            ApBehaviorEntry one = mongoTemplate.findOne(entry_id, ApBehaviorEntry.class);
            return one;
        }
        return null;
    }
}
