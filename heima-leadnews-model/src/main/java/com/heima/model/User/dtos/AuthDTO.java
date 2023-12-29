package com.heima.model.User.dtos;

import com.heima.model.common.dtos.PageRequestDTO;
import lombok.Data;

/**
 * ClassName: AuthDTO
 * Package: com.heima.model.User.dtos
 * Description:
 *
 * @Author R
 * @Create 2023/12/29 8:05
 * @Version 1.0
 */
@Data
public class AuthDTO extends PageRequestDTO {
    //状态
    private Short status;
    // 认证用户ID
    private Integer id;
    //驳回的信息
    private String msg;
}
