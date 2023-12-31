package com.heima.model.wemedia.dtos;

import com.heima.model.common.dtos.PageRequestDTO;
import lombok.Data;

/**
 * ClassName: WmMaterialDTO
 * Package: com.heima.model.wemedia.dtos
 * Description:
 *
 * @Author R
 * @Create 2023/12/30 15:19
 * @Version 1.0
 */
@Data
public class WmMaterialDTO extends PageRequestDTO {
    Short isCollection; //1收藏 0
}
