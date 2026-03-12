package com.kmbeast.pojo.dto;

import lombok.Data;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class ManualSendDto {

    @NotNull(message = "用户ID列表不能为空")
    private List<Integer> userIds;

    @NotNull(message = "宠物ID列表不能为空")
    private List<Integer> petIds;

    @NotNull(message = "问卷模板ID不能为空")
    private Integer templateId;

    private Integer questionType = 3; // 默认预警触发类型

    private String remark; // 发放备注

    @AssertTrue(message = "用户和宠物数量不匹配")
    private boolean isValid() {
        return userIds != null && petIds != null && userIds.size() == petIds.size();
    }
}