package com.heima.model.wemedia.dtos;

import com.heima.model.common.dtos.PageRequestDTO;
import lombok.Data;

/**
 * ClassName: NewsAuthDTO
 * Package: com.heima.model.wemedia.dtos
 * Description:
 *
 * @Author R
 * @Create 2024/1/4 9:34
 * @Version 1.0
 */
@Data
public class NewsAuthDTO extends PageRequestDTO {
    private String title;
    private Short status;
    /**
     * 文章id
     */
    private Integer id;

    /**
     * 失败原因
     */
    private String msg;
}
