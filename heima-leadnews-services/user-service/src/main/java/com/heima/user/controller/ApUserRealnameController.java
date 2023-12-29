package com.heima.user.controller;

import com.heima.common.constants.admin.AdminConstants.AdminConstants;
import com.heima.model.User.dtos.AuthDTO;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.user.service.ApUserRealnameService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ClassName: ApUserRealnameController
 * Package: com.heima.user.controller
 * Description:
 *
 * @Author R
 * @Create 2023/12/29 8:30
 * @Version 1.0
 */
@Api(value = "app用户实名认证",tags = "app用户实名认证")
@RestController
@RequestMapping("/api/v1/auth")
public class ApUserRealnameController {
    @Autowired
    private ApUserRealnameService userRealnameService;
    @ApiOperation("根据状态查询实名认证列表")
    @PostMapping("/list")
    public ResponseResult loadListByStatus(@RequestBody AuthDTO dto) {
        return userRealnameService.loadListByStatus(dto);
    }

    @ApiOperation("实名认证通过")
    @PostMapping("/authPass")
    public ResponseResult authPass(@RequestBody AuthDTO DTO) {
        return userRealnameService.updateStatusById(DTO, AdminConstants.PASS_AUTH);
    }
    @ApiOperation("实名认证失败")
    @PostMapping("/authFail")
    public ResponseResult authFail(@RequestBody AuthDTO DTO) {
        return userRealnameService.updateStatusById(DTO, AdminConstants.FAIL_AUTH);
    }
}
