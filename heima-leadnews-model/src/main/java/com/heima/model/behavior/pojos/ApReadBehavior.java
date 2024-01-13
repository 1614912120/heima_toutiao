package com.heima.model.behavior.pojos;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.Date;
@Data
@Document("ap_read_behavior")
public class ApReadBehavior implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    /**
     * 用户ID
     */
    @Field("entry_id")
    private Integer entryId;
    /**
     * 文章ID
     */
    @Field("article_id")
    private Long articleId;
    /**
     * 阅读次数
     */
    private Short count;
    /**
     * 登录时间
     */
    @Field("created_time")
    private Date createdTime;
    /**
     * 更新时间
     */
    @Field("updated_time")
    private Date updatedTime;
}