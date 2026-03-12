package com.kmbeast.controller;

import com.kmbeast.aop.Pager;
import com.kmbeast.pojo.api.ApiResult;
import com.kmbeast.pojo.api.Result;
import com.kmbeast.pojo.dto.NoticeQueryDto;
import com.kmbeast.pojo.entity.Notice;
import com.kmbeast.pojo.vo.NoticeListItemVO;
import com.kmbeast.service.NoticeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 公告控制器
 * 企业级优化：删除冗余注解、添加日志/接口文档、参数校验
 */
@Api(tags = "公告管理接口")
@RestController
@RequestMapping("/notice")
public class NoticeController {

    private static final Logger log = LoggerFactory.getLogger(NoticeController.class);

    @Resource
    private NoticeService noticeService;

    /**
     * 新增公告
     */
    @ApiOperation("新增公告")
    @PostMapping("/save")
    public Result<String> save(
            @ApiParam(value = "公告实体数据", required = true)
            @RequestBody Notice notice) {
        log.info("新增公告：{}", notice.getTitle());
        return noticeService.saveEntity(notice);
    }

    /**
     * 修改公告
     */
    @ApiOperation("修改公告内容")
    @PostMapping("/update")
    public Result<String> update(
            @ApiParam(value = "公告实体数据", required = true)
            @RequestBody Notice notice) {
        if (notice.getId() == null || notice.getId() <= 0) {
            log.warn("修改公告参数非法：id={}", notice.getId());
            return ApiResult.error("公告ID不能为空");
        }
        log.info("修改公告{}：{}", notice.getId(), notice.getTitle());
        return noticeService.update(notice);
    }

    /**
     * 删除公告
     */
    @ApiOperation("根据ID删除公告")
    @DeleteMapping("/{id}")
    public Result<String> deleteById(
            @ApiParam(value = "公告ID", required = true)
            @PathVariable Integer id) {
        if (id == null || id <= 0) {
            log.warn("删除公告参数非法：id={}", id);
            return ApiResult.error("公告ID必须为正整数");
        }
        log.info("删除公告{}", id);
        noticeService.removeById(id);
        return ApiResult.success("公告删除成功");
    }

    /**
     * 查询公告详情
     */
    @ApiOperation("根据ID查询公告详情")
    @GetMapping("/{id}") // 优化路径：/getById/{id} → /{id}
    public Result<Notice> getById(
            @ApiParam(value = "公告ID", required = true)
            @PathVariable Integer id) {
        if (id == null || id <= 0) {
            log.warn("查询公告参数非法：id={}", id);
            return ApiResult.error("公告ID必须为正整数");
        }
        log.info("查询公告{}详情", id);
        return ApiResult.success(noticeService.getById(id));
    }

    /**
     * 分页查询公告列表
     */
    @ApiOperation("分页查询公告列表")
    @Pager
    @PostMapping("/list")
    public Result<List<NoticeListItemVO>> query(
            @ApiParam(value = "公告查询条件", required = true)
            @RequestBody NoticeQueryDto noticeQueryDto) {
        log.info("分页查询公告，条件：{}", noticeQueryDto);
        return noticeService.query(noticeQueryDto);
    }

}