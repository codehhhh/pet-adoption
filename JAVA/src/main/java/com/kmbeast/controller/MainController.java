package com.kmbeast.controller;

import com.kmbeast.pojo.api.ApiResult;
import com.kmbeast.pojo.api.Result;
import com.kmbeast.pojo.vo.ChartVO;
import com.kmbeast.service.MainService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 首页控制器
 * 企业级优化：删除冗余注解、添加日志/接口文档、参数校验
 */
@Api(tags = "首页数据接口")
@RestController
@RequestMapping("/main")
public class MainController {

    private static final Logger log = LoggerFactory.getLogger(MainController.class);

    @Resource
    private MainService mainService;

    /**
     * 查询静态数据
     */
    @ApiOperation("查询首页静态统计数据")
    @GetMapping("/staticData")
    public Result<List<ChartVO>> staticData() {
        log.info("查询首页静态统计数据");
        return mainService.staticData();
    }

    /**
     * 查询各宠物类别下的宠物数量
     */
    @ApiOperation("查询各宠物类别下的宠物数量统计")
    @GetMapping("/petTypeCount")
    public Result<List<ChartVO>> petTypeCount() {
        log.info("查询宠物类别数量统计");
        return mainService.petTypeCount();
    }

    /**
     * 查询指定天数内的宠物领养订单数目
     */
    @ApiOperation("查询指定天数内的宠物领养订单数量")
    @GetMapping("/petAdoptOrderCount/{days}")
    public Result<List<ChartVO>> petAdoptOrderCount(
            @ApiParam(value = "查询天数（如7/30/90）", required = true, example = "7")
            @PathVariable Integer days) {
        // 参数校验
        if (days == null || days <= 0 || days > 365) {
            log.warn("查询领养订单数量参数非法：days={}", days);
            return ApiResult.error("天数必须为1-365之间的整数");
        }
        log.info("查询近{}天宠物领养订单数量", days);
        return mainService.petAdoptOrderCount(days);
    }

}