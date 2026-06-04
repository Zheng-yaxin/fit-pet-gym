package com.gym.modules.coach.controller;

import com.gym.common.result.R;
import com.gym.common.utils.SecurityUtils;
import com.gym.modules.coach.domain.dto.CoachUpdateDto;
import com.gym.modules.coach.domain.vo.CoachInfoVo;
import com.gym.modules.coach.service.ICoachService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 教练信息管理Controller
 */
@Tag(name = "教练信息管理")
@RestController
@RequestMapping("/coach/profile")
public class CoachProfileController {

    @Autowired
    private ICoachService coachService;

    @GetMapping("/info")
    @Operation(summary = "获取教练信息")
    public R<CoachInfoVo> getInfo() {
        Long coachId = SecurityUtils.getUserId();
        CoachInfoVo info = coachService.getCoachInfo(coachId);
        return R.ok(info);
    }

    @PutMapping("/update")
    @Operation(summary = "更新教练信息")
    public R<Void> updateInfo(@RequestBody CoachUpdateDto dto) {
        Long coachId = SecurityUtils.getUserId();
        coachService.updateCoachInfo(coachId, dto);
        return R.ok(null, "更新成功");
    }
}
