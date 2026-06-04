package com.gym.modules.coach.controller;

import com.gym.common.result.R;
import com.gym.common.utils.SecurityUtils;
import com.gym.modules.coach.domain.dto.PersonalTrainingBookingDto;
import com.gym.modules.coach.domain.vo.PersonalTrainingBookingVo;
import com.gym.modules.coach.service.IPersonalTrainingBookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * 会员端-私教预约动作 Controller
 */
@Tag(name = "会员端-私教预约管理")
@RestController
@RequestMapping("/member/personal-training")
public class MemberPersonalTrainingController {

    @Autowired
    private IPersonalTrainingBookingService bookingService;

    @PostMapping("/book")
    @Operation(summary = "提交预约")
    public R<Void> bookPersonalTraining(@Valid @RequestBody PersonalTrainingBookingDto dto) {
        Long memberId = SecurityUtils.getUserId();
        // 调用 Service 层的事务方法：检查Slot -> 扣余额 -> 生成订单 -> 更新Slot状态
        bookingService.bookPersonalTraining(memberId, dto);
        return R.ok(null, "预约成功");
    }

    @GetMapping("/bookings")
    @Operation(summary = "查询自己的预约记录")
    public R<List<PersonalTrainingBookingVo>> getMyBookings() {
        Long memberId = SecurityUtils.getUserId();
        List<PersonalTrainingBookingVo> bookings = bookingService.getMemberBookings(memberId);
        return R.ok(bookings);
    }

    @PutMapping("/bookings/{bookingId}/cancel")
    @Operation(summary = "取消预约")
    public R<Void> cancelBooking(@PathVariable("bookingId") Long bookingId) {
        Long memberId = SecurityUtils.getUserId();
        bookingService.cancelBooking(bookingId, memberId, "member");
        return R.ok(null, "取消成功");
    }
}
