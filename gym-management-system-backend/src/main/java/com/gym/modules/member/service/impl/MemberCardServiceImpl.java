package com.gym.modules.member.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gym.common.exception.ServiceException;
import com.gym.modules.auth.domain.entity.Member;
import com.gym.modules.equipment.domain.dto.EquipmentQueryDTO;
import com.gym.modules.equipment.domain.vo.EquipmentVO;
import com.gym.modules.equipment.service.EquipmentService;
import com.gym.modules.member.domain.dto.CardBuyDto;
import com.gym.modules.member.domain.entity.MemberCard;
import com.gym.modules.member.domain.entity.MemberTransaction;
import com.gym.modules.member.domain.vo.MemberBenefitSummaryVo;
import com.gym.modules.member.mapper.MemberCardMapper;
import com.gym.modules.member.mapper.MemberMapper;
import com.gym.modules.member.mapper.MemberTransactionMapper;
import com.gym.modules.member.service.IMemberCardService;
import com.gym.modules.traffic.domain.entity.GymArea;
import com.gym.modules.traffic.domain.entity.TrafficSnapshot;
import com.gym.modules.traffic.service.IGymAreaService;
import com.gym.modules.traffic.service.ITrafficSnapshotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class MemberCardServiceImpl extends ServiceImpl<MemberCardMapper, MemberCard> implements IMemberCardService {

    @Autowired
    private MemberMapper memberMapper;
    @Autowired
    private MemberTransactionMapper transactionMapper;
    @Autowired
    private EquipmentService equipmentService;
    @Autowired
    private ITrafficSnapshotService trafficSnapshotService;
    @Autowired
    private IGymAreaService gymAreaService;

    @Override
    @Transactional
    public boolean buyCard(CardBuyDto dto) {
        // 1. 检查是否已有同类型的卡（逻辑优化：如果已有同类型卡，视为续费）
        MemberCard existingCard = baseMapper.selectOne(new LambdaQueryWrapper<MemberCard>()
                .eq(MemberCard::getMemberId, dto.getMemberId())
                .eq(MemberCard::getCardType, dto.getCardType())
                .eq(MemberCard::getDeleted, 0)
                .orderByDesc(MemberCard::getExpireDate)
                .last("limit 1"));

        if (existingCard != null) {
            return renewCard(dto);
        }

        // 2. 正常办卡流程
        Member member = memberMapper.selectById(dto.getMemberId());
        if (member == null) throw new ServiceException("会员不存在");

        String cardNo = generateCardNo();
        MemberCard card = new MemberCard();
        card.setMemberId(dto.getMemberId());
        card.setCardNo(cardNo);
        card.setCardType(dto.getCardType());
        card.setIssueDate(new Date());
        card.setStatus("0");
        card.setDeleted(0);
        card.setCreateTime(new Date());
        card.setSuspendCount(0);
        card.setSuspendYear(Calendar.getInstance().get(Calendar.YEAR));

        // 设置过期日期
Calendar calendar = Calendar.getInstance();
        if ("年卡".equals(dto.getCardType())) {
            calendar.add(Calendar.YEAR, dto.getDuration() != null ? dto.getDuration() : 1);
        } else if ("月卡".equals(dto.getCardType())) {
            calendar.add(Calendar.MONTH, dto.getDuration() != null ? dto.getDuration() : 1);
        } else if ("周卡".equals(dto.getCardType())) {
            calendar.add(Calendar.DAY_OF_YEAR, dto.getDuration() != null ? dto.getDuration() : 7);
        } else if ("日卡".equals(dto.getCardType())) {
            calendar.add(Calendar.DAY_OF_YEAR, dto.getDuration() != null ? dto.getDuration() : 1);
        } else if ("次卡".equals(dto.getCardType())) {
            calendar.add(Calendar.YEAR, 1);
            card.setRemainingTimes(dto.getTimes() != null ? dto.getTimes() : 30);
        } else {
            calendar.add(Calendar.MONTH, 1);
        }
        card.setExpireDate(calendar.getTime());

        if (!save(card)) throw new ServiceException("办卡失败");

        // 更新会员信息
        member.setCardNo(cardNo);
        member.setCardType(dto.getCardType());
        member.setJoinDate(new Date());
        member.setExpireDate(card.getExpireDate());
        memberMapper.updateById(member);

        // 记录办卡金额流水（如果有金额）
        if (dto.getAmount() != null && dto.getAmount().doubleValue() > 0) {
            recordTransaction(dto.getMemberId(), dto.getAmount(), cardNo, "办卡", "新办" + dto.getCardType());
        }

        return true;
    }

    @Override
    @Transactional
    public boolean renewCard(CardBuyDto dto) {
        // 核心修复：查找指定类型的卡片，允许查询已过期的卡
        MemberCard card = baseMapper.selectOne(new LambdaQueryWrapper<MemberCard>()
                .eq(MemberCard::getMemberId, dto.getMemberId())
                .eq(MemberCard::getCardType, dto.getCardType())
                .eq(MemberCard::getDeleted, 0)
                .orderByDesc(MemberCard::getExpireDate) // 取最近的一张
                .last("limit 1"));

        if (card == null) {
            throw new ServiceException("未找到该类型的会员卡，请先办理新卡");
        }

        Calendar calendar = Calendar.getInstance();
        // 逻辑修复：如果卡已过期，从当前时间开始算；如果未过期，从原过期时间顺延
        // 解决“有效期显示不正确”的核心逻辑
        Date now = new Date();
        Date baseDate = (card.getExpireDate() != null && card.getExpireDate().after(now))
                ? card.getExpireDate()
                : now;

        calendar.setTime(baseDate);

        if ("年卡".equals(card.getCardType())) {
            calendar.add(Calendar.YEAR, dto.getDuration() != null ? dto.getDuration() : 1);
        } else if ("月卡".equals(card.getCardType())) {
            calendar.add(Calendar.MONTH, dto.getDuration() != null ? dto.getDuration() : 1);
        } else if ("次卡".equals(card.getCardType())) {
            calendar.add(Calendar.YEAR, 1); // 次卡续费延期一年
            int addTimes = dto.getTimes() != null ? dto.getTimes() : 30;
            card.setRemainingTimes((card.getRemainingTimes() == null ? 0 : card.getRemainingTimes()) + addTimes);
        }

        // 恢复状态
        if ("2".equals(card.getStatus())) {
            card.setStatus("0");
        }

        card.setExpireDate(calendar.getTime());
        if (!updateById(card)) throw new ServiceException("续费失败");

        // 同步更新用户表 Member 的过期时间
        Member member = memberMapper.selectById(dto.getMemberId());
        member.setExpireDate(card.getExpireDate());
        memberMapper.updateById(member);

        // 记录续费流水
        if (dto.getAmount() != null && dto.getAmount().doubleValue() > 0) {
            recordTransaction(dto.getMemberId(), dto.getAmount(), card.getCardNo(), "续费", "续费" + card.getCardType());
        }

        return true;
    }

    @Override
    @Transactional
    public boolean suspendCard(Long cardId, Integer months) {
        if (months == null || months < 1 || months > 3) {
            throw new ServiceException("停卡时间限制为1-3个月");
        }

        MemberCard card = getById(cardId);
        if (card == null) throw new ServiceException("会员卡不存在");
        if (card.getExpireDate().before(new Date())) throw new ServiceException("会员卡已过期，无法办理停卡");

        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        if (card.getSuspendYear() == null || card.getSuspendYear() != currentYear) {
            card.setSuspendYear(currentYear);
            card.setSuspendCount(0);
        }

        if (card.getSuspendCount() >= 2) {
            throw new ServiceException("本年度停卡次数已用完 (限2次/年)");
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(card.getExpireDate());
        calendar.add(Calendar.MONTH, months);
        card.setExpireDate(calendar.getTime());

        card.setSuspendCount(card.getSuspendCount() + 1);

        if (!updateById(card)) throw new ServiceException("停卡操作失败");

        Member member = memberMapper.selectById(card.getMemberId());
        if (member != null) {
            member.setExpireDate(card.getExpireDate());
            memberMapper.updateById(member);
        }

        return true;
    }

    @Override
    public boolean reportLoss(Long cardId) {
        MemberCard card = getById(cardId);
        if (card == null) throw new ServiceException("会员卡不存在");
        card.setStatus("1");
        return updateById(card);
    }

    @Override
    public MemberCard getValidCardByMemberId(Long memberId) {
        return baseMapper.selectOne(new LambdaQueryWrapper<MemberCard>()
                .eq(MemberCard::getMemberId, memberId)
                .eq(MemberCard::getStatus, "0")
                .eq(MemberCard::getDeleted, 0)
                .gt(MemberCard::getExpireDate, new Date())
                .orderByDesc(MemberCard::getExpireDate)
                .last("limit 1"));
    }

    @Override
    public MemberBenefitSummaryVo getBenefitSummary(Long memberId) {
        Member member = memberMapper.selectById(memberId);
        if (member == null || Integer.valueOf(1).equals(member.getDeleted())) {
            throw new ServiceException("会员不存在");
        }

        MemberCard card = getValidCardByMemberId(memberId);
        MemberBenefitSummaryVo vo = new MemberBenefitSummaryVo();
        vo.setMemberId(memberId);
        vo.setMemberName(member.getName());
        vo.setWalletBalance(member.getBalance() == null ? BigDecimal.ZERO : member.getBalance());
        vo.setCard(card);
        vo.setActive(card != null);

        if (card == null) {
            vo.setStatusLabel("暂无有效会员卡");
            vo.setGroupCourseQuota(0);
            vo.setPrivateTrainingQuota(0);
            vo.setUnlimitedEntry(false);
            vo.setLockerAccess(false);
            vo.getActions().add("先购买或续费会员卡，再预约高价值场馆资产。");
            vo.getActions().add("如果准备购卡，请先确认钱包余额充足。");
        } else {
            applyBenefitRules(vo, card);
        }

        vo.setAvailableAssets(buildAvailableAssets());
        vo.setRecommendedAreas(buildRecommendedAreas());
        if (vo.getAvailableAssets().isEmpty()) {
            vo.getActions().add("当前没有可用器材记录，到店前请让前台确认。");
        }
        if (vo.getRecommendedAreas().isEmpty()) {
            vo.getActions().add("场馆暂无实时人流快照，请以现场区域状态为准。");
        }
        return vo;
    }

    private void applyBenefitRules(MemberBenefitSummaryVo vo, MemberCard card) {
        int daysLeft = card.getExpireDate() == null ? 0 : Math.max(0,
                (int) ChronoUnit.DAYS.between(new Date().toInstant(), card.getExpireDate().toInstant()));
        String cardType = card.getCardType() == null ? "" : card.getCardType();
        vo.setDaysLeft(daysLeft);
        vo.setStatusLabel(daysLeft <= 7 ? "即将到期" : "权益有效");
        vo.setUnlimitedEntry(!containsAny(cardType, "次", "娆"));
        vo.setLockerAccess(containsAny(cardType, "年", "骞"));

        if (containsAny(cardType, "年", "骞")) {
            vo.setGroupCourseQuota(999);
            vo.setPrivateTrainingQuota(12);
            vo.getEntitlements().add("年卡权益：全年入场与团课预约。");
            vo.getEntitlements().add("包含 12 节私教课程和储物柜权益。");
        } else if (containsAny(cardType, "月", "鏈")) {
            vo.setGroupCourseQuota(999);
            vo.setPrivateTrainingQuota(2);
            vo.getEntitlements().add("月卡权益：本周期入场与团课预约。");
            vo.getEntitlements().add("包含 2 节私教课程。");
        } else if (containsAny(cardType, "周", "鍛")) {
            vo.setGroupCourseQuota(7);
            vo.setPrivateTrainingQuota(1);
            vo.getEntitlements().add("周卡权益：7 天入场与团课预约。");
            vo.getEntitlements().add("包含 1 节私教体验课。");
        } else if (containsAny(cardType, "日", "鏃")) {
            vo.setGroupCourseQuota(1);
            vo.setPrivateTrainingQuota(0);
            vo.getEntitlements().add("日卡权益：当天入场与基础器材使用。");
        } else if (containsAny(cardType, "次", "娆")) {
            int times = card.getRemainingTimes() == null ? 0 : card.getRemainingTimes();
            vo.setGroupCourseQuota(Math.max(0, times));
            vo.setPrivateTrainingQuota(0);
            vo.getEntitlements().add("次卡权益：按剩余次数入场和预约。");
        } else {
            vo.setGroupCourseQuota(1);
            vo.setPrivateTrainingQuota(0);
            vo.getEntitlements().add("标准权益：入场和基础器材使用。");
        }

        if (daysLeft <= 7) {
            vo.getActions().add("会员卡即将到期，请尽快续费以保持入场权益。");
        }
        if (Boolean.TRUE.equals(vo.getLockerAccess())) {
            vo.getActions().add("储物柜权益已激活，可到前台绑定柜号。");
        }
    }

    private List<MemberBenefitSummaryVo.BenefitAssetVo> buildAvailableAssets() {
        EquipmentQueryDTO query = new EquipmentQueryDTO();
        query.setPageNum(1);
        query.setPageSize(8);
        query.setStatus(0);
        Page<EquipmentVO> page = equipmentService.memberOptions(query);
        return page.getRecords().stream().map(item -> {
            MemberBenefitSummaryVo.BenefitAssetVo vo = new MemberBenefitSummaryVo.BenefitAssetVo();
            vo.setEquipmentId(item.getId());
            vo.setName(item.getName());
            vo.setCategoryName(item.getCategoryName());
            vo.setLocation(item.getLocation());
            vo.setStatus(item.getStatus());
            vo.setStatusDesc(item.getStatusDesc());
            vo.setReason("当前会员权益可使用的场馆器材。");
            return vo;
        }).toList();
    }

    private List<MemberBenefitSummaryVo.BenefitAreaVo> buildRecommendedAreas() {
        List<TrafficSnapshot> snapshots = trafficSnapshotService.list(new LambdaQueryWrapper<TrafficSnapshot>()
                .orderByDesc(TrafficSnapshot::getSnapshotTime)
                .last("limit 120"));
        Map<Long, TrafficSnapshot> latestByArea = new LinkedHashMap<>();
        for (TrafficSnapshot snapshot : snapshots) {
            if (snapshot.getAreaId() != null && !latestByArea.containsKey(snapshot.getAreaId())) {
                latestByArea.put(snapshot.getAreaId(), snapshot);
            }
        }

        Map<Long, GymArea> areas = gymAreaService.list().stream()
                .collect(Collectors.toMap(GymArea::getId, area -> area, (a, b) -> a, LinkedHashMap::new));
        return areas.values().stream()
                .map(area -> toBenefitArea(area, latestByArea.get(area.getId())))
                .sorted((a, b) -> Integer.compare(nullToZero(a.getOccupancyPercent()), nullToZero(b.getOccupancyPercent())))
                .limit(4)
                .toList();
    }

    private MemberBenefitSummaryVo.BenefitAreaVo toBenefitArea(GymArea area, TrafficSnapshot snapshot) {
        int capacity = firstPositive(snapshot == null ? null : snapshot.getCapacity(), area.getCapacity(), 30);
        int currentCount = Math.max(0, snapshot == null || snapshot.getCurrentCount() == null ? 0 : snapshot.getCurrentCount());
        int occupancy = capacity <= 0 ? 0 : Math.min(100, currentCount * 100 / capacity);

        MemberBenefitSummaryVo.BenefitAreaVo vo = new MemberBenefitSummaryVo.BenefitAreaVo();
        vo.setAreaId(area.getId());
        vo.setAreaName(area.getName());
        vo.setLocation(area.getLocation());
        vo.setCapacity(capacity);
        vo.setCurrentCount(currentCount);
        vo.setOccupancyPercent(occupancy);
        vo.setStatusLabel(occupancy >= 80 ? "拥挤" : occupancy >= 55 ? "偏忙" : occupancy >= 25 ? "舒适" : "空闲");
        vo.setAction(occupancy >= 80 ? "建议延后或选择其他场区。" : "适合作为下一组训练区域。");
        return vo;
    }

    private boolean containsAny(String value, String... needles) {
        for (String needle : needles) {
            if (value.contains(needle)) {
                return true;
            }
        }
        return false;
    }

    private int firstPositive(Integer first, Integer second, int fallback) {
        if (first != null && first > 0) {
            return first;
        }
        if (second != null && second > 0) {
            return second;
        }
        return fallback;
    }

    private int nullToZero(Integer value) {
        return value == null ? 0 : value;
    }

    private String generateCardNo() {
        String cardNo = "8000" + String.format("%06d", new Random().nextInt(1000000));
        if (baseMapper.selectCount(new LambdaQueryWrapper<MemberCard>().eq(MemberCard::getCardNo, cardNo)) > 0) {
            return generateCardNo();
        }
        return cardNo;
    }

    // 提取的通用记录流水方法
    private void recordTransaction(Long memberId, java.math.BigDecimal amount, String cardNo, String type, String remark) {
        MemberTransaction transaction = new MemberTransaction();
        transaction.setMemberId(memberId);
        transaction.setTransactionType(type);
        transaction.setAmount(amount);
        transaction.setCardNo(cardNo);
        transaction.setRemark(remark);
        transaction.setCreateTime(new Date());
        transaction.setOperator("system");
        transactionMapper.insert(transaction);
    }
}
