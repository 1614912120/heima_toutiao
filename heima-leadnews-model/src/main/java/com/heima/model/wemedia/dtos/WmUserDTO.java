package com.heima.model.wemedia.dtos;

import lombok.Data;

/**
 * ClassName: WmUserDTO
 * Package: com.heima.model.wemedia.dtos
 * Description:
 *
 * @Author R
 * @Create 2023/12/30 11:28
 * @Version 1.0
 */
@Data
public class WmUserDTO {
    //用户名 密码
    private String name;

    private String password;
}
