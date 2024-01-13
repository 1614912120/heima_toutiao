package com.heima.model.behavior.pojos;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

@Data
@Document(value = "ap_likes_behavior")
public class ApLikesBehavior implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    /**
     * 实体ID
     */
    @Field("entry_id")
    private Integer entryId;
    @NotNull(message = "文章id不能为空")
    // 文章id
    @Field("article_id")
    private Long articleId;
    /**
     * 点赞内容类型
     * 0文章
     * 1动态
     */
    private Short type;
    /**
     * 0 点赞
     * 1 取消点赞
     */
    @Min(value = 0,message = "操作必须是0或者1")
    @Max(value = 1,message = "操作必须是0或者1")
    private Short operation;
    /**
     * 创建时间
     */
    @Field("created_time")
    private Date createdTime;
}