package com.gym.modules.member.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gym.common.result.PageResult;
import com.gym.common.result.R;
import com.gym.modules.member.domain.dto.CardBuyDto;
import com.gym.modules.member.domain.entity.MemberCard;
import com.gym.modules.member.domain.vo.MemberBenefitSummaryVo;
import com.gym.modules.member.service.IMemberCardService;
import com.gym.modules.member.service.IMemberService;
import com.gym.modules.auth.domain.entity.Member;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/member/card")
@Tag(name = "会员卡管理", description = "会员卡办理、续费与停卡接口")
public class MemberCardController {

    @Autowired
    private IMemberCardService memberCardService;

    @Autowired
    private IMemberService memberService;

    @GetMapping("/list")
    @Operation(summary = "分页查询会员卡列表")
    public R<PageResult<MemberCard>> list(@RequestParam(name = "pageNum", defaultValue = "1") Integer pageNum,
                                          @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                          @RequestParam(name = "keyword", required = false) String keyword,
                                          @RequestParam(name = "status", required = false) String status) {
        LambdaQueryWrapper<MemberCard> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StringUtils.hasText(status), MemberCard::getStatus, status)
                .like(StringUtils.hasText(keyword), MemberCard::getCardNo, keyword)
                .orderByDesc(MemberCard::getCreateTime);

        Page<MemberCard> page = new Page<>(pageNum, pageSize);
        memberCardService.page(page, wrapper);

        List<Long> memberIds = page.getRecords().stream()
                .map(MemberCard::getMemberId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        Map<Long, String> memberNameMap = memberIds.isEmpty()
                ? Map.of()
                : memberService.listByIds(memberIds).stream()
                .collect(Collectors.toMap(Member::getId, Member::getName, (a, b) -> a));

        page.getRecords().forEach(card -> {
            String memberName = memberNameMap.get(card.getMemberId());
            if (memberName != null) {
                card.setMemberName(memberName);
            }
        });

        return R.ok(new PageResult<>(page.getRecords(), page.getTotal()));
    }

    @PostMapping("/buy")
    @Operation(summary = "办理会员卡")
    public R<Void> buyCard(@Valid @RequestBody CardBuyDto dto) {
        return memberCardService.buyCard(dto) ?
                R.ok() : R.fail("办卡失败");
    }

    @PostMapping("/renew")
    @Operation(summary = "会员卡续费")
    public R<Void> renewCard(@Valid @RequestBody CardBuyDto dto) {
        return memberCardService.renewCard(dto) ?
                R.ok() : R.fail("续费失败");
    }

    @PostMapping("/suspend")
    @Operation(summary = "停卡/延期")
    public R<Void> suspendCard(@RequestParam("cardId") Long cardId, @RequestParam("months") Integer months) {
        return memberCardService.suspendCard(cardId, months) ?
                R.ok() : R.fail("停卡申请失败");
    }

    @PostMapping("/loss/{cardId}")
    @Operation(summary = "挂失会员卡")
    public R<Void> reportLoss(@PathVariable("cardId") Long cardId) {
        return memberCardService.reportLoss(cardId) ?
                R.ok() : R.fail("挂失失败");
    }

    @GetMapping("/valid/{memberId}")
    @Operation(summary = "查询有效会员卡")
    public R<MemberCard> getValidCard(@PathVariable("memberId") Long memberId) {
        return R.ok(memberCardService.getValidCardByMemberId(memberId));
    }

    @GetMapping("/benefits/{memberId}")
    @Operation(summary = "Query member benefits and venue asset access")
    public R<MemberBenefitSummaryVo> getBenefitSummary(@PathVariable("memberId") Long memberId) {
        return R.ok(memberCardService.getBenefitSummary(memberId));
    }
}
