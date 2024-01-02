package com.heima.wemedia.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.common.exception.CustException;
import com.heima.file.service.FileStorageService;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.threadlocal.WmThreadLocalUtils;
import com.heima.model.wemedia.dtos.WmMaterialDTO;
import com.heima.model.wemedia.pojos.WmMaterial;
import com.heima.model.wemedia.pojos.WmNewsMaterial;
import com.heima.model.wemedia.pojos.WmUser;
import com.heima.wemedia.mapper.WmMaterialMapper;
import com.heima.wemedia.mapper.WmNewsMaterialMapper;
import com.heima.wemedia.mapper.WmUserMapper;
import com.heima.wemedia.service.WmMaterialService;
import com.netflix.hystrix.exception.ExceptionNotWrappedByHystrix;
import com.sun.deploy.panel.ITreeNode;
import com.sun.org.apache.bcel.internal.generic.NEW;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * ClassName: WmMaterialServiceImpl
 * Package: com.heima.wemedia.service.impl
 * Description:
 *
 * @Author R
 * @Create 2023/12/30 13:52
 * @Version 1.0
 */
@Service
@Slf4j
public class WmMaterialServiceImpl extends ServiceImpl<WmMaterialMapper, WmMaterial> implements WmMaterialService {

    @Autowired
    FileStorageService fileStorageService;

    @Autowired
    private WmUserMapper wmUserMapper;
    @Value("${file.oss.prefix}")
    String prefix;
    @Value("${file.oss.web-site}")
    String webSite;
    @Override
    public ResponseResult uploadPicture(MultipartFile multipartFile) {
        //参数校验
        if(multipartFile == null) {
            CustException.cust(AppHttpCodeEnum.PARAM_INVALID,"请上传正确的文件");
        }
        //当前线程获取用户id
        WmUser user = WmThreadLocalUtils.getUser();
        if(user == null) {
            CustException.cust(AppHttpCodeEnum.NO_OPERATOR_AUTH);
        }
        //检查文件格式
        //获取原始文件名称
        String originalFilename = multipartFile.getOriginalFilename();
        if(!checkFileSuffix(originalFilename)) {
            CustException.cust(AppHttpCodeEnum.PARAM_IMAGE_FORMAT_ERROR,"请上传正确的素材格式");
        }
        //上传oss
        String filePath = null;
        try {
            String filename = UUID.randomUUID().toString().replace("-", "");
            String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
            filePath = fileStorageService.store(prefix, filename + suffix, multipartFile.getInputStream());
            log.info("阿里云OSS 文件 fileId: {}",filePath);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("阿里云文件上传失败 uploadPicture error: {}", filePath);
            CustException.cust(AppHttpCodeEnum.SERVER_ERROR,"服务器繁忙请稍后重试");
        }
        //封装数据保存到数据库
        // 3 封装数据并保持到素材库中
        WmMaterial wmMaterial = new WmMaterial();
        wmMaterial.setIsCollection((short)0);
        wmMaterial.setType((short)0);
        wmMaterial.setCreatedTime(new Date());
        wmMaterial.setUrl(filePath);
        //
        WmUser wmUser = wmUserMapper.selectOne(Wrappers.<WmUser>lambdaQuery().eq(WmUser::getApUserId, user.getId()));
        wmMaterial.setUserId(wmUser.getId());
        save(wmMaterial);

        // 前端显示
        wmMaterial.setUrl(webSite+filePath);
        return ResponseResult.okResult(wmMaterial);
    }

    //查询
    @Override
    public ResponseResult findList(WmMaterialDTO dto) {
        dto.checkParam();
        LambdaQueryWrapper<WmMaterial> wrapper = new LambdaQueryWrapper<>();
        //
        if(dto.getIsCollection() != null && dto.getIsCollection() == 1) {
            wrapper.eq(WmMaterial::getIsCollection,dto.getIsCollection());
        }
        //判断一下当前用户
        WmUser user = WmThreadLocalUtils.getUser();
        WmUser wmUser = wmUserMapper.selectOne(Wrappers.<WmUser>lambdaQuery().eq(WmUser::getApUserId, user.getId()));
        wrapper.eq(WmMaterial::getUserId,wmUser.getId());
        Page<WmMaterial> page = new Page<>(dto.getPage(), dto.getSize());
        IPage<WmMaterial> results = page(page, wrapper);
        List<WmMaterial> records = results.getRecords();
        for (WmMaterial record : records) {
            //图片路径
            record.setUrl(webSite + record.getUrl());
        }

        return new PageResponseResult(dto.getPage(),dto.getSize(),page.getTotal(),records);
    }

    @Autowired
    WmNewsMaterialMapper wmNewsMaterialMapper;
    @Override
    public ResponseResult delPicture(Integer id) {
        if(id == null) {
            CustException.cust(AppHttpCodeEnum.PARAM_INVALID,"图片不存在");
        }
        //判断是否被引用
        WmMaterial wmMaterial = getById(id);
        if(wmMaterial == null) {
            CustException.cust(AppHttpCodeEnum.DATA_NOT_EXIST);
        }
        LambdaQueryWrapper<WmNewsMaterial> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WmNewsMaterial::getMaterialId,id);
        Integer count = wmNewsMaterialMapper.selectCount(wrapper);
        if(count>0) {
            CustException.cust(AppHttpCodeEnum.DATA_NOT_ALLOW,"与文章关联的图片不能删除");
        }
        //删除素材库
        removeById(id);

        //删除oos
        fileStorageService.delete(wmMaterial.getUrl());
        //封装结果
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    @Override
    public ResponseResult updateStatus(Integer id, Short type) {
        if(id == null) {
            CustException.cust(AppHttpCodeEnum.LOGIN_STATUS_ERROR);
        }
        //更新状态
        WmMaterial wmMaterial = getById(id);
        if(wmMaterial == null) {
            CustException.cust(AppHttpCodeEnum.DATA_NOT_EXIST,"素材信息不存在");
        }
        //获取当前用户
        WmUser user = WmThreadLocalUtils.getUser();
        if(!wmMaterial.getUserId().equals(user.getId())) {
            CustException.cust(AppHttpCodeEnum.DATA_NOT_ALLOW,"只允许自己操作自己的");
        }
        wmMaterial.setIsCollection(type);
        updateById(wmMaterial);
        return ResponseResult.okResult();
    }

    //检查文件后缀
    private boolean checkFileSuffix(String originalFilename) {
        if(StringUtils.isNotBlank(originalFilename)) {
            List<String> allowSuffix = Arrays.asList("jpg", "jpeg", "png", "gif");
            for(String suffix:allowSuffix) {
                if(originalFilename.endsWith(suffix)) {
                    return true;
                }
            }
        }
        return false;
    }
}
