package com.heima.user.service.impl;
import java.util.Date;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.errorprone.annotations.Var;
import com.heima.common.constants.admin.AdminConstants.AdminConstants;
import com.heima.common.exception.CustException;
import com.heima.feigns.ArticleFegin;
import com.heima.feigns.WemediaFeign;
import com.heima.model.User.dtos.AuthDTO;
import com.heima.model.User.pojos.ApUser;
import com.heima.model.User.pojos.ApUserRealname;
import com.heima.model.article.pojos.ApAuthor;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.wemedia.pojos.WmUser;
import com.heima.user.mapper.ApUserMapper;
import com.heima.user.mapper.ApUserRealnameMapper;
import com.heima.user.service.ApUserRealnameService;
import io.seata.spring.annotation.GlobalTransactional;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.beanvalidation.OptionalValidatorFactoryBean;

/**
 * ClassName: ApUserRealnameServiceImpl
 * Package: com.heima.user.service.impl
 * Description:
 *
 * @Author R
 * @Create 2023/12/29 8:22
 * @Version 1.0
 */
@Service
public class ApUserRealnameServiceImpl extends ServiceImpl<ApUserRealnameMapper, ApUserRealname>implements ApUserRealnameService {
    @Override
    public ResponseResult loadListByStatus(AuthDTO dto) {
        //校验参数
        dto.checkParam();
        //封装查询条件
        Page<ApUserRealname> page = new Page<>(dto.getPage(),dto.getSize());
        LambdaQueryWrapper<ApUserRealname> queryWrapper = Wrappers.<ApUserRealname>lambdaQuery();
        queryWrapper.eq(dto.getStatus() != null,ApUserRealname::getStatus,dto.getStatus());
        //执行查询 封装返回结果
        IPage<ApUserRealname> iPage = this.page(page, queryWrapper);
        return new PageResponseResult(dto.getPage(),dto.getSize(),iPage.getTotal(),iPage.getRecords());
    }

    @Autowired
    private ApUserMapper apUserMapper;
    //用户审核 realname的id
    @GlobalTransactional(rollbackFor = Exception.class,timeoutMills = 300000)
    @Override
    public ResponseResult updateStatusById(AuthDTO dto, Short status) {
        //校验参数
        if(dto.getId()== null) {
            CustException.cust(AppHttpCodeEnum.PARAM_INVALID,"实名认证id不能为空");
        }
        //根据实名认证id，查realname
        ApUserRealname userRealname = getById(dto.getId());
        //根据实名认证信息关联的apuser id 查询user
        if(!AdminConstants.WAIT_AUTH.equals(userRealname.getStatus())) {
            CustException.cust(AppHttpCodeEnum.DATA_NOT_ALLOW,"实名认证状态不是待审核");
        }
        ApUser apUser = apUserMapper.selectById(userRealname.getUserId());
        if(apUser ==null) {
            CustException.cust(AppHttpCodeEnum.DATA_NOT_ALLOW,"实名认证关联的用户不存在");
        }
        //修改实名认证状态
        userRealname.setStatus(status);
        if(StringUtils.isNotBlank(dto.getMsg())){
            userRealname.setReason(dto.getMsg());
        }
        //修改状态完毕
        updateById(userRealname);
        //判断 1 2 9
        if(AdminConstants.FAIL_AUTH.equals(status)){
            return ResponseResult.okResult();
        }
        //9审核通过
        //开通媒体账户 查询是否开通过 保存自媒体信息
        WmUser wmUser = createWmUser(apUser);
        //创建作者信息 查询是否创建 报错作者信息
        createAuthor(apUser,wmUser);
        return ResponseResult.okResult();
    }

    @Autowired
    private ArticleFegin articleFegin;
    private void createAuthor(ApUser apUser, WmUser wmUser) {
        //根据id 查作者信息是否存在
        ResponseResult<ApAuthor> result = articleFegin.findByUserId(apUser.getId());
        if(!result.checkCode()) {
            CustException.cust(AppHttpCodeEnum.REMOTE_SERVER_ERROR,result.getErrorMessage());
        }
        //存在
        ApAuthor apAuthor = result.getData();
        if(apAuthor!= null) {
            CustException.cust(AppHttpCodeEnum.DATA_EXIST,"作者信息已存在");
        }
        ApAuthor apAuthor1 = new ApAuthor();
        apAuthor1.setName(apUser.getName());
        apAuthor1.setType(2);
        apAuthor1.setUserId(apUser.getId());
        apAuthor1.setCreatedTime(new Date());
        apAuthor1.setWmUserId(wmUser.getApUserId());
        ResponseResult saveResult = articleFegin.save(apAuthor1);
        if(!saveResult.checkCode()){
            CustException.cust(AppHttpCodeEnum.REMOTE_SERVER_ERROR,result.getErrorMessage());
        }
    }

    @Autowired
    private WemediaFeign wemediaFeign;
    //开通自媒体用户信息
    private WmUser createWmUser(ApUser apUser) {
        //根据用户名查询自媒体信息
        ResponseResult<WmUser> result = wemediaFeign.findByName(apUser.getName());
        if(!result.checkCode()){
            CustException.cust(AppHttpCodeEnum.REMOTE_SERVER_ERROR,result.getErrorMessage());
        }
        //获取返回值
        WmUser wmUser = result.getData();


        //存在抛出异常
        if(wmUser != null) {
            CustException.cust(AppHttpCodeEnum.DATA_EXIST,"用户已经存在");
        }
        //保存
        WmUser wmUser1 = new WmUser();
        wmUser1.setApUserId(apUser.getId());
        wmUser1.setName(apUser.getName());
        wmUser1.setPassword(apUser.getPassword());
        wmUser1.setSalt(apUser.getSalt());
        wmUser1.setImage(apUser.getImage());
        wmUser1.setPhone(apUser.getPhone());
        wmUser1.setStatus(9);
        wmUser1.setType(0);
        wmUser1.setCreatedTime(new Date());
        ResponseResult<WmUser> saveResult = wemediaFeign.save(wmUser1);
        if(!saveResult.checkCode()){
            CustException.cust(AppHttpCodeEnum.REMOTE_SERVER_ERROR,result.getErrorMessage());
        }
        return saveResult.getData();
    }
}
