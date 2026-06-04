package com.gym.modules.dashboard.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gym.modules.course.service.ICourseService;
import com.gym.modules.dashboard.service.DashboardService;
import com.gym.modules.equipment.service.EquipmentService;
import com.gym.modules.member.domain.entity.MemberCard;
import com.gym.modules.member.service.IMemberCardService;
import com.gym.modules.member.service.IMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class DashboardServiceImpl implements DashboardService {
    @Autowired
    private IMemberService memberService;

    @Autowired
    private IMemberCardService memberCardService;

    @Autowired
    private EquipmentService equipmentService;

    @Autowired
    private ICourseService courseService;

    @Override
    public Map<String, Object> overview() {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("memberCount", memberService.count());

        // 有效会员卡数量
        long activeCardCount = memberCardService.count(new LambdaQueryWrapper<MemberCard>()
                .eq(MemberCard::getStatus, "0")
                .gt(MemberCard::getExpireDate, new Date()));
        data.put("activeCardCount", activeCardCount);

        data.put("equipmentCount", equipmentService.count());
        data.put("courseCount", courseService.count());

        data.put("todayTrainingCount", 0);
        data.put("todayLogCount", 0);
        data.put("monthlyRevenue", 0);

        return data;
    }
}