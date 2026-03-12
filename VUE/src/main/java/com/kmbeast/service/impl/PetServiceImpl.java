package com.kmbeast.service.impl;

import com.kmbeast.context.LocalThreadHolder;
import com.kmbeast.mapper.ActiveNetMapper;
import com.kmbeast.mapper.PetMapper;
import com.kmbeast.mapper.PetTypeMapper;
import com.kmbeast.pojo.api.ApiResult;
import com.kmbeast.pojo.api.Result;
import com.kmbeast.pojo.dto.ActiveNetQueryDto;
import com.kmbeast.pojo.dto.PetQueryDto;
import com.kmbeast.pojo.em.ActiveNetType;
import com.kmbeast.pojo.em.IsAdoptEnum;
import com.kmbeast.pojo.em.IsRecommendEnum;
import com.kmbeast.pojo.entity.ActiveNet;
import com.kmbeast.pojo.entity.Pet;
import com.kmbeast.pojo.entity.PetType;
import com.kmbeast.pojo.vo.PetListItemVO;
import com.kmbeast.pojo.vo.PetVO;
import com.kmbeast.pojo.vo.ScoreVO;
import com.kmbeast.service.PetService;
import com.kmbeast.utils.AssertUtils;
import com.kmbeast.utils.UserBasedCFUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 宠物信息业务逻辑实现类
 */
@Service
public class PetServiceImpl implements PetService {

    private static final Logger log = LoggerFactory.getLogger(PetServiceImpl.class); // ✅ 新增：企业级日志

    @Resource
    private PetMapper petMapper;
    @Resource
    private ActiveNetMapper activeNetMapper;
    @Resource
    private PetTypeMapper petTypeMapper;

    /**
     * 宠物新增
     */
    @Override
    public Result<String> save(Pet pet) {
        // ✅ 优化：调用公共方法，消除重复逻辑
        handlePetType(pet);

        petParamCheck(pet);
        pet.setIsAdopt(IsAdoptEnum.NO_ADOPT.getStatus());
        pet.setCreateTime(LocalDateTime.now());
        petMapper.save(pet);

        log.info("新增宠物成功，宠物ID：{}", pet.getId()); // ✅ 新增：日志记录
        return ApiResult.success("宠物信息新增成功");
    }

    /**
     * 宠物参数校验（公共方法）
     */
    private void petParamCheck(Pet pet) {
        AssertUtils.hasText(pet.getName(), "宠物名不能为空");
        AssertUtils.hasText(pet.getCover(), "请上传一张封面");
        AssertUtils.hasText(pet.getAddress(), "请补充宠物所在地");
        AssertUtils.hasText(pet.getDetail(), "请补充宠物描述");
        AssertUtils.notNull(pet.getPetTypeId(), "请选择宠物类型");
        AssertUtils.notNull(pet.getAge(), "请填写宠物年龄");
        AssertUtils.notNull(pet.getIsVaccine(), "请选择宠物是否已经接种疫苗");
        AssertUtils.notNull(pet.getIsRecommend(), "请选择是否推荐");
    }

    /**
     * 处理宠物类别（公共方法，抽取重复逻辑）
     */
    private void handlePetType(Pet pet) { // ✅ 新增：公共方法，消除重复
        if (pet.getPetTypeName() != null && !pet.getPetTypeName().trim().isEmpty()) {
            String typeName = pet.getPetTypeName().trim();
            PetType existingType = petTypeMapper.queryByName(typeName);

            if (existingType != null) {
                pet.setPetTypeId(existingType.getId());
            } else {
                PetType newType = new PetType();
                newType.setName(typeName);
                petTypeMapper.save(newType);

                // ✅ 优化：增加判空，避免空指针（企业级防御性编程）
                PetType createdType = petTypeMapper.queryByName(typeName);
                if (createdType != null) {
                    pet.setPetTypeId(createdType.getId());
                    log.info("创建新宠物类别：{}，ID：{}", typeName, createdType.getId());
                } else {
                    log.warn("创建宠物类别失败，类别名：{}", typeName);
                    throw new IllegalArgumentException("宠物类别创建失败");
                }
            }
        }
    }

    /**
     * 宠物修改
     */
    @Override
    public Result<String> update(Pet pet) {
        // ✅ 优化：调用公共方法，消除重复逻辑
        handlePetType(pet);

        petParamCheck(pet);
        petMapper.update(pet);

        log.info("修改宠物成功，宠物ID：{}", pet.getId());
        return ApiResult.success("宠物信息修改成功");
    }

    /**
     * 删除宠物
     */
    @Override
    public Result<String> deleteById(Integer id) {
        // ✅ 新增：删除前校验（企业级数据安全）
        PetVO pet = petMapper.getById(id);
        if (pet == null) {
            log.warn("删除宠物失败，宠物ID：{} 不存在", id);
            return ApiResult.error("宠物不存在");
        }

        petMapper.deleteById(id);
        log.info("删除宠物成功，宠物ID：{}", id);
        return ApiResult.success("宠物信息删除成功");
    }

    /**
     * 查询宠物详情（登录用户）
     */
    @Override
    public Result<PetVO> getById(Integer id) {
        PetVO petVO = petMapper.getById(id);
        if (petVO == null) {
            log.warn("查询宠物失败，宠物ID：{} 不存在", id);
            return ApiResult.error("宠物不存在");
        }

        // 浏览记录逻辑
        Integer userId = LocalThreadHolder.getUserId();
        ActiveNetQueryDto queryDto = new ActiveNetQueryDto();
        queryDto.setUserId(userId);
        queryDto.setContentId(id);
        queryDto.setContentType("PET");
        queryDto.setType(ActiveNetType.VIEW.getStatus());

        Integer count = activeNetMapper.queryCount(queryDto);
        if (count == 0) {
            ActiveNet activeNet = new ActiveNet();
            activeNet.setUserId(userId);
            activeNet.setContentId(id);
            activeNet.setContentType("PET");
            activeNet.setType(ActiveNetType.VIEW.getStatus());
            activeNet.setCreateTime(LocalDateTime.now());
            activeNetMapper.insert(activeNet);

            log.info("新增宠物浏览记录，用户ID：{}，宠物ID：{}", userId, id);
        }

        return ApiResult.success(petVO);
    }

    /**
     * 查询宠物详情（游客）
     */
    @Override
    public Result<PetVO> viewById(Integer id) {
        PetVO petVO = petMapper.getById(id);
        if (petVO == null) {
            log.warn("游客查询宠物失败，宠物ID：{} 不存在", id);
            return ApiResult.error("宠物不存在");
        }
        return ApiResult.success(petVO);
    }

    /**
     * 宠物列表查询
     */
    @Override
    public Result<List<PetListItemVO>> list(PetQueryDto petQueryDto) {
        List<PetListItemVO> list = petMapper.queryListItem(petQueryDto);
        Integer count = petMapper.queryCount(petQueryDto);

        log.info("查询宠物列表，条件：{}，总数：{}", petQueryDto, count);
        return ApiResult.success(list, count);
    }

    /**
     * 手动推荐宠物查询
     */
    @Override
    public Result<List<PetListItemVO>> recommend() {
        PetQueryDto queryDto = new PetQueryDto();
        queryDto.setIsRecommend(IsRecommendEnum.RECOMMEND.getStatus());
        List<PetListItemVO> list = petMapper.queryListItem(queryDto);

        // 兜底逻辑
        if (list.isEmpty()) {
            queryDto.setCurrent(0);
            queryDto.setSize(3);
            list = petMapper.queryListItem(queryDto);
            log.info("手动推荐宠物为空，返回默认宠物列表，条数：{}", list.size());
        }

        return ApiResult.success(list);
    }

    /**
     * 智能推荐宠物
     */
    @Override
    public Result<List<PetListItemVO>> autoRecommend(Integer count) {
        // ✅ 新增：参数校验（企业级）
        if (count == null || count <= 0 || count > 20) {
            log.warn("智能推荐参数非法，count：{}，默认返回10条", count);
            count = 10;
        }

        List<Integer> petIds = petMapper.queryAllIds();
        List<ScoreVO> scoreVOS = activeNetMapper.queryScore("PET");

        List<UserBasedCFUtil.Score> scoreList = scoreVOS.stream()
                .map(vo -> new UserBasedCFUtil.Score(vo.getUserId(), vo.getContentId(), vo.getScore()))
                .collect(Collectors.toList());

        Map<Integer, Map<Integer, Double>> matrix = UserBasedCFUtil.buildUserItemMatrix(petIds, scoreList);
        UserBasedCFUtil cfUtil = new UserBasedCFUtil(matrix);
        List<Integer> recommendItems = cfUtil.recommendItems(LocalThreadHolder.getUserId(), count);

        // ✅ 优化：替换System.out为日志
        log.info("为用户「{}」智能推荐宠物ID列表：{}", LocalThreadHolder.getUserId(), recommendItems);

        // 冷启动逻辑
        if (recommendItems.isEmpty()) {
            List<ScoreVO> scoreVOList = activeNetMapper.queryAllIds("PET", ActiveNetType.VIEW.getStatus(), count);
            if (scoreVOList.isEmpty()) {
                log.info("智能推荐冷启动无数据，返回空列表");
                return ApiResult.success(new ArrayList<>());
            }

            List<Integer> petNetIds = scoreVOList.stream()
                    .map(ScoreVO::getContentId)
                    .collect(Collectors.toList());
            List<PetListItemVO> list = petMapper.queryListItemByIds(petNetIds);
            return ApiResult.success(list);
        }

        List<PetListItemVO> list = petMapper.queryListItemByIds(recommendItems);
        return ApiResult.success(list);
    }

    /**
     * 查询用户收藏的宠物列表
     */
    @Override
    public Result<List<PetListItemVO>> saveList() {
        Integer userId = LocalThreadHolder.getUserId();
        ActiveNetQueryDto queryDto = new ActiveNetQueryDto();
        queryDto.setUserId(userId);
        queryDto.setContentType("PET");
        queryDto.setType(ActiveNetType.SAVE.getStatus());

        List<ActiveNet> activeNetList = activeNetMapper.query(queryDto);
        if (activeNetList.isEmpty()) {
            log.info("用户「{}」暂无收藏的宠物", userId);
            return ApiResult.success(new ArrayList<>());
        }

        List<Integer> petIds = activeNetList.stream()
                .map(ActiveNet::getContentId)
                .collect(Collectors.toList());
        List<PetListItemVO> list = petMapper.queryListItemByIds(petIds);

        log.info("用户「{}」收藏宠物数：{}", userId, list.size());
        return ApiResult.success(list);
    }
}