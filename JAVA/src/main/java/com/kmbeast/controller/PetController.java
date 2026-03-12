package com.kmbeast.controller;

import com.kmbeast.aop.Pager;
import com.kmbeast.pojo.api.Result;
import com.kmbeast.pojo.dto.PetQueryDto;
import com.kmbeast.pojo.entity.Pet;
import com.kmbeast.pojo.vo.PetListItemVO;
import com.kmbeast.pojo.vo.PetVO;
import com.kmbeast.service.PetService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 宠物控制器
 */
@Api(tags = "宠物管理接口") // ✅ 新增：企业级接口文档注解
@RestController
@RequestMapping("/pet")
public class PetController {

    @Resource
    private PetService petService;

    /**
     * 宠物新增
     */
    @ApiOperation("新增宠物信息") // ✅ 新增：接口描述
    @PostMapping("/save")
    public Result<String> save(@RequestBody Pet pet) {
        return petService.save(pet);
    }

    /**
     * 宠物修改
     */
    @ApiOperation("修改宠物信息")
    @PutMapping("/update")
    public Result<String> update(@RequestBody Pet pet) {
        return petService.update(pet);
    }

    /**
     * 宠物删除
     */
    @ApiOperation("删除宠物信息")
    @DeleteMapping("/{id}")
    public Result<String> deleteById(@PathVariable Integer id) {
        return petService.deleteById(id);
    }

    /**
     * 查询宠物详情（合并getById+viewById，通过参数区分游客/登录用户）
     */
    @ApiOperation("查询宠物详情（支持游客/登录用户）")
    @GetMapping("/{id}")
    public Result<PetVO> getById(
            @PathVariable Integer id,
            @RequestParam(required = false, defaultValue = "false") Boolean isVisitor) { // ✅ 新增：游客标识
        if (isVisitor) {
            return petService.viewById(id); // 游客逻辑
        }
        return petService.getById(id); // 登录用户逻辑（含浏览记录）
    }

    /**
     * 宠物列表分页查询
     */
    @ApiOperation("宠物列表分页查询")
    @Pager
    @PostMapping("/list")
    public Result<List<PetListItemVO>> list(@RequestBody PetQueryDto petQueryDto) {
        return petService.list(petQueryDto);
    }

    /**
     * 手动推荐宠物查询
     */
    @ApiOperation("查询手动推荐宠物")
    @GetMapping("/recommend")
    public Result<List<PetListItemVO>> recommend() {
        return petService.recommend();
    }

    /**
     * 用户收藏宠物列表查询
     */
    @ApiOperation("查询用户收藏的宠物列表")
    @GetMapping("/saveList")
    public Result<List<PetListItemVO>> saveList() {
        return petService.saveList();
    }

    /**
     * 智能推荐宠物查询
     */
    @ApiOperation("智能推荐宠物（指定条数）")
    @GetMapping("/autoRecommend/{count}")
    public Result<List<PetListItemVO>> autoRecommend(@PathVariable Integer count) {
        return petService.autoRecommend(count);
    }

}