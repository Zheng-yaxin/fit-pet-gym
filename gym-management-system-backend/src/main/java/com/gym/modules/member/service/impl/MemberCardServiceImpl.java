package com.gym.modules.member.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gym.common.exception.ServiceException;
import com.gym.modules.auth.domain.entity.Member;
import com.gym.modules.member.domain.dto.CardBuyDto;
import com.gym.modules.member.domain.entity.MemberCard;
import com.gym.modules.member.domain.entity.MemberTransaction;
import com.gym.modules.member.mapper.MemberCardMapper;
import com.gym.modules.member.mapper.MemberMapper;
import com.gym.modules.member.mapper.MemberTransactionMapper;
import com.gym.modules.member.service.IMemberCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

@Service
public class MemberCardServiceImpl extends ServiceImpl<MemberCardMapper, MemberCard> implements IMemberCardService {

    @Autowired
    private MemberMapper memberMapper;
    @Autowired
    private MemberTransactionMapper transactionMapper;

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