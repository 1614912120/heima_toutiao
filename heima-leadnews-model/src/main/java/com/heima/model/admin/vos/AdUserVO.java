package com.heima.model.admin.vos;

import lombok.Data;

import java.util.Date;

/**
 * ClassName: AdUserVO
 * Package: com.heima.model.admin.vos
 * Description:
 *
 * @Author R
 * @Create 2023/12/28 15:35
 * @Version 1.0
 */
@Data
public class AdUserVO {
    private Integer id;
    private String name;
    private String nickname;
    private String image;
    private String email;
    private Date loginTime;
    private Date createdTime;
}
