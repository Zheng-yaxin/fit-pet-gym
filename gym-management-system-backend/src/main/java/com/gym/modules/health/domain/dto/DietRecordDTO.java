package com.gym.modules.health.domain.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.gym.common.json.FlexibleDateDeserializer;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class DietRecordDTO {
    private Long foodId;
    /**
     * 摄入量(g)
     */
    private BigDecimal amount;
    /**
     * 餐别 (1:早餐, 2:午餐, 3:晚餐, 4:加餐)
     */
    private Integer mealType;
    /**
     * 进食日期 (yyyy-MM-dd)
     */
    @JsonDeserialize(using = FlexibleDateDeserializer.class)
    private Date eatDate;
}
