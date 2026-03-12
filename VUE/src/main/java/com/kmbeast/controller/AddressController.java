package com.kmbeast.controller;

import com.kmbeast.aop.Pager;
import com.kmbeast.context.LocalThreadHolder;
import com.kmbeast.pojo.api.ApiResult;
import com.kmbeast.pojo.api.Result;
import com.kmbeast.pojo.dto.AddressQueryDto;
import com.kmbeast.pojo.entity.Address;
import com.kmbeast.service.AddressService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 收货地址控制器
 * 企业级优化：删除冗余注解、添加日志/接口文档、参数校验
 */
@Api(tags = "收货地址接口")
@RestController
@RequestMapping("/address")
public class AddressController {

    private static final Logger log = LoggerFactory.getLogger(AddressController.class);

    @Resource
    private AddressService addressService;

    /**
     * 新增收货地址
     */
    @ApiOperation("新增用户收货地址")
    @PostMapping("/save")
    public Result<String> save(
            @ApiParam(value = "收货地址实体数据", required = true)
            @RequestBody Address address) {
        log.info("用户{}新增收货地址：{}", LocalThreadHolder.getUserId(), address.getDetail());
        return addressService.saveEntity(address);
    }

    /**
     * 修改收货地址
     */
    @ApiOperation("修改用户收货地址")
    @PutMapping("/update")
    public Result<String> update(
            @ApiParam(value = "收货地址实体数据", required = true)
            @RequestBody Address address) {
        // 参数校验
        if (address.getId() == null || address.getId() <= 0) {
            log.warn("修改收货地址参数非法：id={}", address.getId());
            return ApiResult.error("地址ID必须为正整数");
        }
        log.info("用户{}修改收货地址{}", LocalThreadHolder.getUserId(), address.getId());
        return addressService.update(address);
    }

    /**
     * 删除收货地址
     */
    @ApiOperation("根据ID删除收货地址")
    @DeleteMapping("/{id}")
    public Result<String> deleteById(
            @ApiParam(value = "收货地址ID", required = true)
            @PathVariable Integer id) {
        if (id == null || id <= 0) {
            log.warn("删除收货地址参数非法：id={}", id);
            return ApiResult.error("地址ID必须为正整数");
        }
        log.info("用户{}删除收货地址{}", LocalThreadHolder.getUserId(), id);
        addressService.removeById(id);
        return ApiResult.success("删除成功");
    }

    /**
     * 查询当前用户的收货地址
     */
    @ApiOperation("分页查询当前用户的收货地址")
    @Pager
    @PostMapping("/queryUser")
    public Result<List<Address>> queryUser(
            @ApiParam(value = "地址查询条件", required = true)
            @RequestBody AddressQueryDto addressQueryDto) {
        Integer userId = LocalThreadHolder.getUserId();
        addressQueryDto.setUserId(userId);
        log.info("用户{}查询收货地址", userId);
        return addressService.query(addressQueryDto);
    }

    /**
     * 管理员查询所有收货地址
     */
    @ApiOperation("分页查询所有收货地址（管理员）")
    @Pager
    @PostMapping("/query")
    public Result<List<Address>> query(
            @ApiParam(value = "地址查询条件", required = true)
            @RequestBody AddressQueryDto addressQueryDto) {
        log.info("管理员查询收货地址，条件：{}", addressQueryDto);
        return addressService.query(addressQueryDto);
    }

}