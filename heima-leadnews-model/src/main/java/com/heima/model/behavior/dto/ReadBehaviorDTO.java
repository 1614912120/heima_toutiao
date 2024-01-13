package com.heima.model.behavior.dto;
import lombok.Data;
@Data
public class ReadBehaviorDTO {
    // 设备ID
    Integer equipmentId;
    // 文章、动态、评论等ID
    Long articleId;
    /**
     * 阅读次数  
     */
    Short count;
}