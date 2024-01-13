package com.heima.model.behavior.vo;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.scene.control.cell.PropertyValueFactory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ClassName: behaviorVo
 * Package: com.heima.model.behavior
 * Description:
 *
 * @Author R
 * @Create 2024/1/13 19:57
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class behaviorVo {
    private boolean isfollow;
    private boolean islike;
    private boolean isunlike;
    private boolean iscollection;
    public String toJsonString() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(this);
        } catch (Exception e) {
            // 处理异常，例如日志记录或抛出自定义异常
            e.printStackTrace();
            return "{}"; // 返回空JSON对象作为默认值
        }
    }
}
