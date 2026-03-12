package com.kmbeast.controller;

import com.kmbeast.aop.Pager;
import com.kmbeast.context.LocalThreadHolder;
import com.kmbeast.pojo.api.ApiResult;
import com.kmbeast.pojo.api.Result;
import com.kmbeast.pojo.dto.PetPostQueryDto;
import com.kmbeast.pojo.entity.PetPost;
import com.kmbeast.pojo.vo.PetListItemVO;
import com.kmbeast.pojo.vo.PetPostListItemVO;
import com.kmbeast.pojo.vo.PetPostSelectedVO;
import com.kmbeast.pojo.vo.PetPostVO;
import com.kmbeast.service.PetPostService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 宠物经验帖子控制器
 * 企业级优化：删除冗余注解、添加日志/接口文档、参数校验
 */
@Api(tags = "宠物经验帖子接口")
@RestController
@RequestMapping("/pet-post")
public class PetPostController {

    private static final Logger log = LoggerFactory.getLogger(PetPostController.class);

    @Resource
    private PetPostService petPostService;

    /**
     * 新增宠物经验帖子
     */
    @ApiOperation("新增宠物经验帖子")
    @PostMapping("/save")
    public Result<String> save(
            @ApiParam(value = "宠物帖子实体数据", required = true)
            @RequestBody PetPost petPost) {
        log.info("用户{}新增宠物帖子：{}",
                LocalThreadHolder.getUserId(),
                petPost.getTitle());
        return petPostService.saveEntity(petPost);
    }

    /**
     * 审核宠物帖子
     */
    @ApiOperation("管理员审核宠物帖子")
    @PutMapping("/audit/{id}")
    public Result<String> audit(
            @ApiParam(value = "帖子ID", required = true)
            @PathVariable Integer id) {
        if (id == null || id <= 0) {
            log.warn("审核帖子参数非法：id={}", id);
            return ApiResult.error("帖子ID必须为正整数");
        }
        log.info("管理员审核帖子{}", id);
        return petPostService.audit(id);
    }

    /**
     * 修改宠物帖子
     */
    @ApiOperation("修改宠物帖子内容")
    @PutMapping("/update")
    public Result<String> updateEntity(
            @ApiParam(value = "宠物帖子实体数据", required = true)
            @RequestBody PetPost petPost) {
        if (petPost.getId() == null || petPost.getId() <= 0) {
            log.warn("修改帖子参数非法：id={}", petPost.getId());
            return ApiResult.error("帖子ID不能为空");
        }
        log.info("修改帖子{}：{}", petPost.getId(), petPost.getTitle());
        return petPostService.updateEntity(petPost);
    }

    /**
     * 删除宠物帖子
     */
    @ApiOperation("根据ID删除宠物帖子")
    @DeleteMapping("/{id}")
    public Result<String> deleteById(
            @ApiParam(value = "帖子ID", required = true)
            @PathVariable Integer id) {
        if (id == null || id <= 0) {
            log.warn("删除帖子参数非法：id={}", id);
            return ApiResult.error("帖子ID必须为正整数");
        }
        log.info("删除帖子{}", id);
        petPostService.removeById(id);
        return ApiResult.success("删除成功");
    }

    /**
     * 查询帖子详情
     */
    @ApiOperation("根据ID查询帖子详情")
    @GetMapping("/{id}") // 优化路径：/getById/{id} → /{id}
    public Result<PetPostVO> getById(
            @ApiParam(value = "帖子ID", required = true)
            @PathVariable Integer id) {
        if (id == null || id <= 0) {
            log.warn("查询帖子参数非法：id={}", id);
            return ApiResult.error("帖子ID必须为正整数");
        }
        log.info("查询帖子{}详情", id);
        return petPostService.getById(id);
    }

    /**
     * 查询当前用户的帖子列表
     */
    @ApiOperation("分页查询当前用户的帖子列表")
    @Pager
    @PostMapping("/listUser")
    public Result<List<PetPostListItemVO>> listUser(
            @ApiParam(value = "帖子查询条件", required = true)
            @RequestBody PetPostQueryDto petPostQueryDto) {
        Integer userId = LocalThreadHolder.getUserId();
        petPostQueryDto.setUserId(userId);
        log.info("用户{}查询帖子列表", userId);
        return petPostService.list(petPostQueryDto);
    }

    /**
     * 查询所有帖子列表
     */
    @ApiOperation("分页查询所有帖子列表（管理员）")
    @Pager
    @PostMapping("/list")
    public Result<List<PetPostListItemVO>> list(
            @ApiParam(value = "帖子查询条件", required = true)
            @RequestBody PetPostQueryDto petPostQueryDto) {
        log.info("查询帖子列表，条件：{}", petPostQueryDto);
        return petPostService.list(petPostQueryDto);
    }

    /**
     * 智能推荐宠物帖子
     */
    @ApiOperation("智能推荐宠物帖子")
    @GetMapping("/autoRecommend/{count}")
    public Result<List<PetPostListItemVO>> autoRecommend(
            @ApiParam(value = "推荐条数", required = true, example = "10")
            @PathVariable Integer count) {
        if (count == null || count <= 0 || count > 20) {
            log.warn("智能推荐参数非法：count={}，默认返回10条", count);
            count = 10;
        }
        log.info("为用户{}智能推荐{}条宠物帖子", LocalThreadHolder.getUserId(), count);
        return petPostService.autoRecommend(count);
    }

    /**
     * 查询用户收藏的帖子列表
     */
    @ApiOperation("查询当前用户收藏的宠物帖子列表")
    @GetMapping("/saveList")
    public Result<List<PetPostListItemVO>> saveList() {
        log.info("用户{}查询收藏的帖子列表", LocalThreadHolder.getUserId());
        return petPostService.saveList();
    }

    /**
     * 查询用户帖子下拉选择器数据
     */
    @ApiOperation("查询当前用户帖子下拉选择器数据")
    @GetMapping("/listPetPostSelect")
    public Result<List<PetPostSelectedVO>> listPetPostSelect() {
        log.info("用户{}查询帖子下拉选择器数据", LocalThreadHolder.getUserId());
        return petPostService.listPetPostSelect();
    }

    /**
     * 置顶宠物帖子
     */
    @ApiOperation("管理员置顶宠物帖子")
    @PutMapping("/top/{id}")
    public Result<String> top(
            @ApiParam(value = "帖子ID", required = true)
            @PathVariable Integer id) {
        if (id == null || id <= 0) {
            log.warn("置顶帖子参数非法：id={}", id);
            return ApiResult.error("帖子ID必须为正整数");
        }
        log.info("置顶帖子{}", id);
        return petPostService.top(id);
    }

    /**
     * 取消置顶宠物帖子
     */
    @ApiOperation("管理员取消置顶宠物帖子")
    @PutMapping("/cancel-top/{id}")
    public Result<String> cancelTop(
            @ApiParam(value = "帖子ID", required = true)
            @PathVariable Integer id) {
        if (id == null || id <= 0) {
            log.warn("取消置顶帖子参数非法：id={}", id);
            return ApiResult.error("帖子ID必须为正整数");
        }
        log.info("取消置顶帖子{}", id);
        return petPostService.cancelTop(id);
    }

    /**
     * 获取置顶帖子列表
     */
    @ApiOperation("获取置顶的宠物帖子列表")
    @GetMapping("/top")
    public Result<List<PetPostListItemVO>> getTopPosts() {
        log.info("查询置顶宠物帖子列表");
        return petPostService.getTopPosts();
    }

}