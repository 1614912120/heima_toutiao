package com.heima.model.admin.dtos;

import com.heima.model.common.dtos.PageRequestDTO;
import io.swagger.models.auth.In;
import lombok.Data;

/**
 * ClassName: ChannelDTO
 * Package: com.heima.model.admin.dtos
 * Description:
 *
 * @Author R
 * @Create 2023/12/27 15:56
 * @Version 1.0
 */
@Data
public class ChannelDTO extends PageRequestDTO {
    /**
     * 频道名称
     */
    private String name;
    /**
     * 频道状态
     */
    private Integer status;
}
