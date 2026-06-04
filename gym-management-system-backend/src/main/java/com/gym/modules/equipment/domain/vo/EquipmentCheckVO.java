package com.gym.modules.equipment.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Schema(description = "器材巡检记录返回VO")
@Data
public class EquipmentCheckVO {

    @Schema(description = "巡检ID")
    private Long id;

    @Schema(description = "器材ID")
    private Long equipmentId;

    @Schema(description = "器材名称")
    private String equipmentName;

    @Schema(description = "巡检人ID")
    private Long checkerId;

    @Schema(description = "巡检人姓名")
    private String checkerName;

    @Schema(description = "巡检时间")
    private Date checkTime;

    @Schema(description = "巡检结果 0正常 1异常")
    private Integer result;

    @Schema(description = "异常描述")
    private String abnormalDesc;

    @Schema(description = "处理建议")
    private String suggestion;

    @Schema(description = "创建时间")
    private Date createTime;
}