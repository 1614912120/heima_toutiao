package com.heima.model.wemedia.vos;

import com.heima.model.wemedia.pojos.WmNews;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.implementations.PrivateKeyResolver;
import lombok.Data;

/**
 * ClassName: WmNewsVO
 * Package: com.heima.model.wemedia.vos
 * Description:
 *
 * @Author R
 * @Create 2024/1/4 9:33
 * @Version 1.0
 */
@Data
public class WmNewsVO  extends WmNews {
    private String authorName;
}
