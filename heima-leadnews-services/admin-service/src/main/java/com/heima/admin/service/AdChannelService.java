package com.heima.admin.service;

import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.admin.dtos.ChannelDTO;
import com.heima.model.admin.pojos.AdChannel;
import com.heima.model.common.dtos.ResponseResult;
import io.swagger.models.auth.In;
import org.checkerframework.checker.units.qual.A;

import java.time.format.ResolverStyle;

/**
 * ClassName: AdChannelMapper
 * Package: com.heima.admin.service
 * Description:
 *
 * @Author R
 * @Create 2023/12/27 16:00
 * @Version 1.0
 */
public interface AdChannelService extends IService<AdChannel> {

    public ResponseResult findByNameAndPage(ChannelDTO dto);

    public ResponseResult insert (AdChannel channel);

    //修改
    public ResponseResult update(AdChannel channel);

    public ResponseResult deleteById(Integer id);
}
