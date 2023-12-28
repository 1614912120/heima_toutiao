package com.heima.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.admin.mapper.ChannelMapper;
import com.heima.admin.service.AdChannelService;
import com.heima.common.exception.CustException;
import com.heima.common.exception.CustomException;
import com.heima.common.exception.ExceptionCatch;
import com.heima.model.admin.dtos.ChannelDTO;
import com.heima.model.admin.pojos.AdChannel;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.print.attribute.standard.PagesPerMinute;
import java.util.Date;
import java.util.concurrent.Executors;

/**
 * ClassName: AdChannelServiceImpl
 * Package: com.heima.admin.service.impl
 * Description:
 *
 * @Author R
 * @Create 2023/12/27 16:02
 * @Version 1.0
 */
@Service
public class AdChannelServiceImpl extends ServiceImpl<ChannelMapper, AdChannel> implements AdChannelService {
    @Override
    public ResponseResult findByNameAndPage(ChannelDTO dto) {
        //校验参数
        if(dto == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        //封装查询
        Page<AdChannel> pageReq = new Page<>(dto.getPage(), dto.getSize());
        LambdaQueryWrapper<AdChannel> wrapper = Wrappers.<AdChannel>lambdaQuery();
        if(StringUtils.isNotBlank(dto.getName())){
            wrapper.like(AdChannel::getName,dto.getName());
        }
        if(dto.getStatus() != null) {
            wrapper.eq(AdChannel::getStatus,dto.getStatus());
        }
        wrapper.orderByAsc(AdChannel::getOrd);
        IPage<AdChannel> page = this.page(pageReq, wrapper);
        return new PageResponseResult(dto.getPage(),dto.getSize(),page.getTotal(),page.getRecords());
    }

    @Override
    public ResponseResult insert(AdChannel channel) {
        //参数校验
        String name= channel.getName();
        if(StringUtils.isBlank(name)) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        if(name.length()>10) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID,"频道名称长度不能大于10");

        }
        //查询名字是否重复
        int count = count(Wrappers.<AdChannel>lambdaQuery().eq(AdChannel::getName, channel.getName()));
        if(count>0){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID,"频道名称有误");
        }
        //保存频道
        channel.setCreatedTime(new Date());
        save(channel);
        //返回
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult update(AdChannel channel) {
        if(channel.getId() == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        //获取id判断是不是存在
        AdChannel channelOld = getById(channel.getId());
        if(channelOld == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST,"频道信息不存在");
        }
        //查询名字是否重复
        if(StringUtils.isNotBlank(channel.getName())&& !channel.getName().equals(channelOld.getName())){
            int count = count(Wrappers.<AdChannel>lambdaQuery().eq(AdChannel::getName, channel.getName()));
            if(count>0){
                return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID,"频道已存在");
            }
        }
        updateById(channel);
        return ResponseResult.okResult();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResponseResult deleteById(Integer id) {
        if(id == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);

        }
        AdChannel adChannel = getById(id);
        if(adChannel == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST);
        }
        if(adChannel.getStatus()) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_REQUIRE,"频道无效不能删除");
        }
        removeById(id);
        if(id== 47) {
            //int i = 30/0;
            //return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST);正常结束不会回滚
            CustException.cust(AppHttpCodeEnum.PARAM_INVALID);
        }
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }
}
