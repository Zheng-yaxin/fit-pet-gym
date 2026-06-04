package com.gym.modules.coach.controller;

import com.gym.common.result.R;
import com.gym.common.utils.SecurityUtils;
import com.gym.modules.coach.domain.dto.PersonalTrainingSlotAddDto;
import com.gym.modules.coach.domain.vo.PersonalTrainingBookingVo;
import com.gym.modules.coach.domain.vo.PersonalTrainingSlotVo;
import com.gym.modules.coach.service.IPersonalTrainingBookingService;
import com.gym.modules.coach.service.IPersonalTrainingSlotService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;

/**
 * 教练端私教管理Controller
 */
@Tag(name = "教练端-私教管理")
@RestController
@RequestMapping("/coach/personal-training")
public class CoachPersonalTrainingController {

    @Autowired
    private IPersonalTrainingSlotService slotService;

    @Autowired
    private IPersonalTrainingBookingService bookingService;

    @GetMapping("/slots")
    @Operation(summary = "查询自己的时间段")
    public R<List<PersonalTrainingSlotVo>> getMySlots(
            @RequestParam(name = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(name = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Long coachId = SecurityUtils.getUserId();
        List<PersonalTrainingSlotVo> slots = slotService.getCoachSlots(coachId, startDate, endDate);
        return R.ok(slots);
    }

    @PostMapping("/slots")
    @Operation(summary = "添加可预约时间段")
    public R<Void> addSlot(@Valid @RequestBody PersonalTrainingSlotAddDto dto) {
        Long coachId = SecurityUtils.getUserId();
        slotService.addSlot(coachId, dto);
        return R.ok(null, "添加成功");
    }

    @DeleteMapping("/slots/{slotId}")
    @Operation(summary = "删除时间段")
    public R<Void> deleteSlot(@PathVariable("slotId") Long slotId) {
        Long coachId = SecurityUtils.getUserId();
        slotService.deleteSlot(coachId, slotId);
        return R.ok(null, "删除成功");
    }

    @GetMapping("/bookings")
    @Operation(summary = "查询自己的预约记录")
    public R<List<PersonalTrainingBookingVo>> getMyBookings() {
        Long coachId = SecurityUtils.getUserId();
        List<PersonalTrainingBookingVo> bookings = bookingService.getCoachBookings(coachId);
        return R.ok(bookings);
    }

    @PutMapping("/bookings/{bookingId}/confirm")
    @Operation(summary = "确认预约")
    public R<Void> confirmBooking(@PathVariable("bookingId") Long bookingId) {
        Long coachId = SecurityUtils.getUserId();
        bookingService.confirmBooking(coachId, bookingId);
        return R.ok(null, "确认成功");
    }

    @PutMapping("/bookings/{bookingId}/complete")
    @Operation(summary = "完成预约")
    public R<Void> completeBooking(@PathVariable("bookingId") Long bookingId) {
        Long coachId = SecurityUtils.getUserId();
        bookingService.completeBooking(coachId, bookingId);
        return R.ok(null, "完成成功");
    }

    @PutMapping("/bookings/{bookingId}/cancel")
    @Operation(summary = "取消预约")
    public R<Void> cancelBooking(@PathVariable("bookingId") Long bookingId) {
        Long coachId = SecurityUtils.getUserId();
        bookingService.cancelBooking(bookingId, coachId, "coach");
        return R.ok(null, "取消成功");
    }
}
