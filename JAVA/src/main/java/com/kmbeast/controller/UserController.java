package com.kmbeast.controller;

import com.kmbeast.aop.Pager;
import com.kmbeast.pojo.api.Result;
import com.kmbeast.pojo.dto.*;
import com.kmbeast.pojo.entity.User;
import com.kmbeast.pojo.vo.UserVO;
import com.kmbeast.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 用户管理控制器
 */
@Api(tags = "用户管理接口")
@RestController
@RequestMapping("/user")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Resource
    private UserService userService;

    /**
     * 用户登录
     */
    @ApiOperation("用户登录（返回JWT Token）")
    @PostMapping("/login")
    public Result<Object> login(@RequestBody UserLoginDto userLoginDTO) {
        log.info("用户登录请求，用户名：{}", userLoginDTO.getAccount());
        return userService.login(userLoginDTO);
    }

    /**
     * Token校验
     */
    @ApiOperation("校验Token有效性（获取当前用户信息）")
    @GetMapping("/auth")
    public Result<UserVO> auth() {
        return userService.auth();
    }

    /**
     * 查询用户详情
     */
    @ApiOperation("根据ID查询用户详情")
    @GetMapping("/{id}") // ✅ 优化：统一接口路径（原/getById/{id}）
    public Result<UserVO> getById(@PathVariable Integer id) {
        return userService.getById(id);
    }

    /**
     * 用户注册
     */
    @ApiOperation("用户注册")
    @PostMapping("/register")
    public Result<String> register(@RequestBody UserRegisterDto userRegisterDTO) {
        return userService.register(userRegisterDTO);
    }

    /**
     * 后台新增用户
     */
    @ApiOperation("管理员新增用户")
    @PostMapping("/save")
    public Result<String> save(@RequestBody UserRegisterDto userRegisterDTO) {
        return userService.save(userRegisterDTO);
    }

    /**
     * 用户修改个人信息
     */
    @ApiOperation("用户修改个人信息")
    @PutMapping("/update")
    public Result<UserVO> update(@RequestBody UserUpdateDto userUpdateDTO) {
        return userService.update(userUpdateDTO);
    }

    /**
     * 管理员修改用户信息
     */
    @ApiOperation("管理员修改用户信息")
    @PutMapping("/backUpdate")
    public Result<String> backUpdate(@RequestBody User user) {
        return userService.backUpdate(user);
    }

    /**
     * 用户修改密码
     */
    @ApiOperation("用户修改密码")
    @PutMapping("/updatePassword")
    public Result<String> updatePassword(@RequestBody UserUpdatePasswordDto userUpdatePasswordDto) {
        return userService.updatePassword(userUpdatePasswordDto);
    }

    /**
     * 删除用户
     */
    @ApiOperation("管理员删除用户")
    @DeleteMapping("/{id}")
    public Result<String> deleteById(@PathVariable Integer id) {
        return userService.deleteById(id);
    }

    /**
     * 用户列表分页查询
     */
    @ApiOperation("管理员分页查询用户列表")
    @Pager
    @PostMapping("/list") // ✅ 优化：统一接口命名（原/query）
    public Result<List<User>> list(@RequestBody UserQueryDto userQueryDto) {
        return userService.query(userQueryDto);
    }

}