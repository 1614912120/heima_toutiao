package com.heima.wemedia.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.model.wemedia.pojos.WmUser;
import com.heima.wemedia.mapper.WmUserMapper;
import com.heima.wemedia.service.WmUserService;
import org.springframework.stereotype.Service;

/**
 * ClassName: WmUserServiceImpl
 * Package: com.heima.wemedia.service.impl
 * Description:
 *
 * @Author R
 * @Create 2023/12/29 9:15
 * @Version 1.0
 */
@Service
public class WmUserServiceImpl extends ServiceImpl<WmUserMapper, WmUser> implements WmUserService {
}
