package com.heima.model.behavior.pojos;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.models.auth.In;
import lombok.Data;
import org.apache.ibatis.type.Alias;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.Date;
/**
 * APP收藏信息表
 * @author itheima
 */
@Data
@Document("ap_collection")
public class ApCollection implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    /**
     * 实体ID
     */
    @Field("entry_id")
    private Integer entryId;
    /**
     * 文章ID
     */
    @Field("article_id")
    private Long articleId;
    /**
     * 点赞内容类型
     0文章
     1动态
     */
    private Short type;
    /**
     * 创建时间
     */
    @Field("collection_time")
    private Date collectionTime;
    public enum Type{
        ARTICLE((short)0),DYNAMIC((short)1);
        short code;
        Type(short code){
            this.code = code;
        }
        public short getCode(){
            return this.code;
        }
    }
    public boolean isCollectionArticle(){
        return (this.getType()!=null&&this.getType().equals(Type.ARTICLE));
    }
}