package com.kmbeast.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kmbeast.pojo.dto.PetAdoptOrderQueryDto;
import com.kmbeast.pojo.entity.PetAdoptOrder;
import com.kmbeast.pojo.vo.AdoptRelationVO;
import com.kmbeast.pojo.vo.PetAdoptOrderVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 宠物领养订单持久化接口
 */
@Mapper
public interface PetAdoptOrderMapper extends BaseMapper<PetAdoptOrder> {

    /**
     * 查询宠物领养订单信息
     */
    List<PetAdoptOrderVO> list(PetAdoptOrderQueryDto petAdoptOrderQueryDto);

    /**
     * 查询符合条件的记录数
     */
    Integer listCount(PetAdoptOrderQueryDto petAdoptOrderQueryDto);

    /**
     * 统计宠物已完成领养订单的数量
     */
    Integer countCompletedOrdersByPetId(@Param("petId") Integer petId);

    /**
     * 查询所有已完成领养的用户-宠物关系
     */
    List<AdoptRelationVO> selectAdoptedUsers();
}