package com.kmbeast.controller;

import com.kmbeast.context.LocalThreadHolder;
import com.kmbeast.pojo.api.ApiResult;
import com.kmbeast.pojo.api.Result;
import com.kmbeast.pojo.dto.ManualSendDto;
import com.kmbeast.pojo.dto.QuestionnaireSubmitDto;
import com.kmbeast.pojo.dto.WarningQuestionnaireQueryDto;
import com.kmbeast.pojo.dto.WarningRecordQueryDto;
import com.kmbeast.pojo.entity.WarningQuestionTemplate;
import com.kmbeast.pojo.vo.AvailableUserVO;
import com.kmbeast.pojo.vo.QuestionnaireStatsVO;
import com.kmbeast.pojo.vo.WarningQuestionnaireVO;
import com.kmbeast.pojo.vo.WarningRecordVO;
import com.kmbeast.pojo.vo.WarningStatisticsVO;
import com.kmbeast.service.WarningService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 预警中心控制器
 * 功能分区：用户功能 | 管理员功能 | 系统功能
 */
@Api(tags = "预警中心接口") // 企业级接口文档分类
@RestController
@RequestMapping("/warning")
public class WarningController {

    // 企业级日志框架（替代System.out，支持分级/持久化）
    private static final Logger log = LoggerFactory.getLogger(WarningController.class);

    @Autowired
    private WarningService warningService;

    // ==================== 公共方法：管理员权限校验（抽取重复逻辑） ====================
    /**
     * 校验是否为管理员角色（企业级权限控制规范）
     * @throws SecurityException 非管理员抛出权限异常
     */
    private void checkAdminPermission() {
        Integer roleId = LocalThreadHolder.getRoleId();
        if (roleId == null || roleId != 1) {
            log.warn("非管理员尝试访问预警中心管理员接口，用户角色ID：{}", roleId);
            throw new SecurityException("无管理员权限，禁止访问");
        }
    }

    // ==================== 用户功能区（普通用户可访问） ====================

    /**
     * 获取用户待回答问卷列表
     */
    @ApiOperation("获取当前用户待回答的预警问卷列表")
    @GetMapping("/questionnaire/pending")
    public Result<List<WarningQuestionnaireVO>> getUserPendingQuestionnaires() {
        Integer userId = LocalThreadHolder.getUserId();
        log.info("用户{}查询待回答预警问卷", userId);
        return warningService.getUserPendingQuestionnaires();
    }

    /**
     * 获取用户历史问卷列表
     */
    @ApiOperation("获取当前用户的预警问卷历史记录")
    @GetMapping("/questionnaire/history")
    public Result<List<WarningQuestionnaireVO>> getUserQuestionnaireHistory() {
        Integer userId = LocalThreadHolder.getUserId();
        log.info("用户{}查询预警问卷历史记录", userId);
        return warningService.getUserQuestionnaireHistory();
    }

    /**
     * 提交问卷
     */
    @ApiOperation("用户提交预警问卷回答")
    @PostMapping("/questionnaire/submit")
    public Result<String> submitQuestionnaire(
            @ApiParam(value = "问卷提交参数", required = true)
            @RequestBody QuestionnaireSubmitDto dto) {
        // 企业级参数校验（前置防御）
        if (dto == null || dto.getQuestionnaireId() == null) {
            log.warn("用户提交问卷参数非法：{}", dto);
            return ApiResult.error("问卷ID不能为空");
        }
        log.info("用户{}提交预警问卷{}", LocalThreadHolder.getUserId(), dto.getQuestionnaireId());
        return warningService.submitQuestionnaire(dto);
    }

    // ==================== 管理员功能区（仅管理员可访问） ====================

    /**
     * 获取预警记录列表
     */
    @ApiOperation("管理员查询预警记录列表")
    @PostMapping("/record/list")
    public Result<List<WarningRecordVO>> getWarningRecords(
            @ApiParam(value = "预警记录查询参数", required = true)
            @RequestBody WarningRecordQueryDto queryDto) {
        checkAdminPermission(); // 调用公共权限校验方法
        log.info("管理员查询预警记录，条件：{}", queryDto);
        return warningService.getWarningRecords(queryDto);
    }

    /**
     * 处理预警记录
     */
    @ApiOperation("管理员处理预警记录（标记为已处理）")
    @PostMapping("/record/process/{warningId}")
    public Result<String> processWarning(
            @ApiParam(value = "预警记录ID", required = true)
            @PathVariable Integer warningId) {
        checkAdminPermission();
        // 企业级参数校验
        if (warningId == null || warningId <= 0) {
            log.warn("管理员处理预警参数非法：warningId={}", warningId);
            return ApiResult.error("预警ID必须为正整数");
        }
        log.info("管理员处理预警记录{}", warningId);
        return warningService.processWarning(warningId);
    }

    /**
     * 获取预警统计数据
     */
    @ApiOperation("获取预警中心核心统计数据")
    @GetMapping("/statistics")
    public Result<WarningStatisticsVO> getWarningStatistics() {
        log.info("查询预警中心统计数据");
        return warningService.getWarningStatistics();
    }

    /**
     * 管理员分页查询所有问卷
     */
    @ApiOperation("管理员分页查询所有预警问卷")
    @PostMapping("/admin/questionnaire/list")
    public ApiResult<List<WarningQuestionnaireVO>> getQuestionnaireListByAdmin(
            @ApiParam(value = "问卷查询参数（含分页）", required = true)
            @RequestBody WarningQuestionnaireQueryDto queryDto) {
        checkAdminPermission();

        // 企业级分页参数默认值（避免空指针）
        if (queryDto.getCurrent() == null || queryDto.getCurrent() <= 0) {
            queryDto.setCurrent(1);
        }
        if (queryDto.getSize() == null || queryDto.getSize() <= 0 || queryDto.getSize() > 50) {
            queryDto.setSize(10); // 限制最大分页条数，防止性能问题
        }
        queryDto.setOffset((queryDto.getCurrent() - 1) * queryDto.getSize());

        log.info("管理员分页查询问卷，条件：{}", queryDto);
        List<WarningQuestionnaireVO> list = warningService.getQuestionnaireListByAdmin(queryDto);
        Integer total = warningService.countQuestionnaireByAdmin(queryDto);

        return ApiResult.success(list, total);
    }

    /**
     * 管理员获取统计概览
     */
    @ApiOperation("管理员获取预警问卷统计概览")
    @GetMapping("/admin/statistics")
    public Result<QuestionnaireStatsVO> getAdminStatistics() {
        checkAdminPermission();
        log.info("管理员查询预警问卷统计概览");
        return warningService.getAdminStatistics();
    }

    /**
     * 获取可发放问卷的用户列表（已完成领养的用户）
     */
    @ApiOperation("管理员查询可发放预警问卷的用户列表")
    @GetMapping("/admin/available-users")
    public Result<List<AvailableUserVO>> getAvailableUsers(
            @ApiParam(value = "预警等级筛选（可选）")
            @RequestParam(required = false) Integer warningLevel,
            @ApiParam(value = "用户名模糊筛选（可选）")
            @RequestParam(required = false) String username,
            @ApiParam(value = "宠物名模糊筛选（可选）")
            @RequestParam(required = false) String petName) {
        checkAdminPermission();
        log.info("管理员查询可发放问卷用户，筛选条件：warningLevel={}, username={}, petName={}",
                warningLevel, username, petName);

        List<AvailableUserVO> users = warningService.getAvailableUsers(warningLevel, username, petName);
        log.info("返回可发放问卷用户{}个", users != null ? users.size() : 0);

        return ApiResult.success(users);
    }

    /**
     * 手动发放问卷
     */
    @ApiOperation("管理员手动向指定用户发放预警问卷")
    @PostMapping("/admin/manual-send")
    public Result<String> manualSendQuestionnaire(
            @ApiParam(value = "手动发放问卷参数", required = true)
            @RequestBody ManualSendDto dto) {
        checkAdminPermission();
        // 企业级参数校验
        if (dto == null || dto.getUserIds() == null || dto.getUserIds().isEmpty()) {
            log.warn("管理员手动发放问卷参数非法：{}", dto);
            return ApiResult.error("请选择至少一个发放用户");
        }
        log.info("管理员手动发放问卷，用户列表：{}，模板ID：{}", dto.getUserIds(), dto.getTemplateId());
        warningService.manualSendQuestionnaire(dto);
        return ApiResult.success("问卷发放成功");
    }

    /**
     * 获取问卷模板列表
     */
    @ApiOperation("管理员查询预警问卷模板列表")
    @GetMapping("/admin/templates")
    public Result<List<WarningQuestionTemplate>> getTemplates(
            @ApiParam(value = "模板类型筛选（可选）")
            @RequestParam(required = false) Integer templateType) {
        checkAdminPermission();
        log.info("管理员查询预警问卷模板，类型筛选：{}", templateType);
        List<WarningQuestionTemplate> templates = warningService.getTemplates(templateType);
        return ApiResult.success(templates);
    }

    // ==================== 系统功能区（定时任务调用）====================

    /**
     * 发送定期关怀问卷（每月1号）
     */
    @ApiOperation("系统自动发送定期关怀预警问卷（每月1号执行）")
    @PostMapping("/system/send-regular")
    public Result<String> sendRegularQuestionnaires() {
        checkAdminPermission(); // 定时任务也需权限控制
        log.info("执行系统定时任务：发送定期关怀预警问卷");
        warningService.sendRegularQuestionnaires();
        return ApiResult.success("定期问卷发送成功");
    }

    /**
     * 发送随机抽查问卷（每周一）
     */
    @ApiOperation("系统自动发送随机抽查预警问卷（每周一执行）")
    @PostMapping("/system/send-random")
    public Result<String> sendRandomQuestionnaires() {
        checkAdminPermission();
        log.info("执行系统定时任务：发送随机抽查预警问卷");
        warningService.sendRandomQuestionnaires();
        return ApiResult.success("随机问卷发送成功");
    }

}