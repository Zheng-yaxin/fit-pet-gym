// com/gym/modules/member/service/IMemberCardService.java
package com.gym.modules.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gym.modules.member.domain.dto.CardBuyDto;
import com.gym.modules.member.domain.entity.MemberCard;
import com.gym.modules.member.domain.vo.MemberBenefitSummaryVo;

public interface IMemberCardService extends IService<MemberCard> {
    boolean buyCard(CardBuyDto dto);
    boolean renewCard(CardBuyDto dto);

    // 新增方法
    boolean suspendCard(Long cardId, Integer months);

    boolean reportLoss(Long cardId);
    MemberCard getValidCardByMemberId(Long memberId);
    MemberBenefitSummaryVo getBenefitSummary(Long memberId);
}
