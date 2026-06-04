package com.gym.modules.equipment.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Schema(description = "报修记录VO")
@Data
public class RepairLogVO {
    /** 报修记录ID */
    @Schema(description = "报修记录ID")
    private Long id;

    /** 器材ID */
    @Schema(description = "器材ID")
    private Long equipmentId;

    /** 器材名称（关联字段） */
    @Schema(description = "器材名称")
    private String equipmentName;

    /** 报修人ID */
    @Schema(description = "报修人ID")
    private Long repairBy;

    /** 报修人姓名（关联字段） */
    @Schema(description = "报修人姓名")
    private String repairByName;

    /** 报修时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Schema(description = "报修时间")
    private Date repairTime;

    /** 故障描述 */
    @Schema(description = "故障描述")
    private String faultDesc;

    /** 维修状态：0-待处理 1-维修中 2-已完成 3-已驳回 */
    @Schema(description = "维修状态：0-待处理 1-维修中 2-已完成 3-已驳回")
    private Integer status;

    /** 维修状态描述 */
    @Schema(description = "维修状态描述")
    private String statusDesc;

    /** 处理人ID */
    @Schema(description = "处理人ID")
    private Long handleBy;

    /** 处理人姓名 */
    @Schema(description = "处理人姓名")
    private String handleByName;

    /** 维修完成时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Schema(description = "维修完成时间")
    private Date finishTime;

    /** 维修费用 */
    @Schema(description = "维修费用")
    private BigDecimal cost;

    /** 维修备注 */
    @Schema(description = "维修备注")
    private String repairRemark;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Schema(description = "创建时间")
    private Date createTime;
}