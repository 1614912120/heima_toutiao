package com.heima.model.admin.pojos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.heima.model.common.validator.ValidatorAdd;
import com.heima.model.common.validator.ValidatorUpdate;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * ClassName: AdChannel
 * Package: com.heima.model.admin.pojos
 * Description:
 *
 * @Author R
 * @Create 2023/12/27 15:38
 * @Version 1.0
 */
@Data
@TableName("ad_channel")
public class AdChannel {    private static final long serialVersionUID = 1L;
    @TableId(value = "id", type = IdType.AUTO)
    @NotNull(message = "id字段不能为null" ,groups = {ValidatorUpdate.class})
    private Integer id;
    /**
     * 频道名称
     */
    @TableField("name")
    @NotBlank(message = "频道名称不能为空",groups = {ValidatorAdd.class})
    @Length(max = 10,message = "频道名称长度不能大于10",groups = {ValidatorAdd.class,ValidatorUpdate.class})
    private String name;
    /**
     * 频道描述
     */
    @TableField("description")
    private String description;
    /**
     * 是否默认频道
     */
    @TableField("is_default")
    private Boolean isDefault;
    @TableField("status")
    private Boolean status;
    /**
     * 默认排序
     */
    @TableField("ord")
    private Integer ord;
    /**
     * 创建时间
     */
    @TableField("created_time")
    private Date createdTime;
}

