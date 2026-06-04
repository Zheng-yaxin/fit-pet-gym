package com.gym.modules.coach.controller;

import com.gym.common.result.R;
import com.gym.modules.coach.domain.vo.CoachInfoVo;
import com.gym.modules.coach.domain.vo.PersonalTrainingSlotVo;
import com.gym.modules.coach.service.ICoachService;
import com.gym.modules.coach.service.IPersonalTrainingSlotService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 会员端-教练信息查询 Controller
 * 对应前端 /member/coach/* 路径
 */
@Tag(name = "会员端-教练信息")
@RestController
@RequestMapping("/member/coach")
public class MemberCoachController {

    @Autowired
    private ICoachService coachService;

    @Autowired
    private IPersonalTrainingSlotService slotService;

    @GetMapping("/list")
    @Operation(summary = "获取所有教练列表")
    public R<List<CoachInfoVo>> getCoachList() {
        // 获取状态为 0 (正常) 且未删除的教练
        List<CoachInfoVo> coaches = coachService.list().stream()
                .filter(coach -> "0".equals(coach.getStatus()) && coach.getDeleted() == 0)
                .map(coach -> {
                    CoachInfoVo vo = new CoachInfoVo();
                    vo.setId(coach.getId());
                    vo.setName(coach.getName());
                    vo.setPhone(coach.getPhone()); // 注意隐私，可视情况脱敏
                    vo.setGender(coach.getGender());
                    vo.setAvatar(coach.getAvatar());
                    vo.setBio(coach.getBio());
                    vo.setSpecialties(coach.getSpecialties());
                    vo.setExperienceYears(coach.getExperienceYears());
                    vo.setCertification(coach.getCertification());
                    vo.setHourlyRate(coach.getHourlyRate());
                    return vo;
                })
                .toList();
        return R.ok(coaches);
    }

    @GetMapping("/{coachId}/slots")
    @Operation(summary = "获取教练可预约时间段")
    public R<List<PersonalTrainingSlotVo>> getCoachAvailableSlots(
            @PathVariable("coachId") Long coachId,
            @RequestParam(name = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        // 如果只传了 date，则 startDate = endDate = date
        List<PersonalTrainingSlotVo> slots = slotService.getAvailableSlots(coachId, date, date);
        return R.ok(slots);
    }
}
