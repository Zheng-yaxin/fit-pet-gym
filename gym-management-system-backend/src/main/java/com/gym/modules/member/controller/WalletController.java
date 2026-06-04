package com.gym.modules.member.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gym.common.result.PageResult;
import com.gym.common.result.R;
import com.gym.modules.auth.domain.entity.Member;
import com.gym.modules.member.domain.entity.MemberTransaction;
import com.gym.modules.member.mapper.MemberTransactionMapper;
import com.gym.modules.member.service.IMemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.Date;

@RestController
@RequestMapping("/member/wallet")
@Tag(name = "会员钱包", description = "充值与交易记录接口")
public class WalletController {

    @Autowired
    private IMemberService memberService;
    @Autowired
    private MemberTransactionMapper transactionMapper;

    @GetMapping("/balance/{memberId}")
    @Operation(summary = "查询余额")
    public R<BigDecimal> getBalance(@PathVariable("memberId") Long memberId) {
        Member member = memberService.getById(memberId);
        if (member == null || member.getDeleted() == 1) {
            return R.fail("会员不存在");
        }
        return R.ok(member.getBalance() == null ? BigDecimal.ZERO : member.getBalance());
    }

    @PostMapping("/recharge")
    @Operation(summary = "会员充值")
    public R<Void> recharge(@RequestParam("memberId") Long memberId,
                            @RequestParam("amount") BigDecimal amount,
                            @RequestParam(name = "remark", required = false) String remark) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            return R.fail("充值金额必须大于0");
        }

        Member member = memberService.getById(memberId);
        if (member == null || member.getDeleted() == 1) {
            return R.fail("会员不存在");
        }

        // 更新余额
        member.setBalance(member.getBalance() == null ? amount : member.getBalance().add(amount));
        member.setUpdateTime(new Date());
        memberService.updateById(member);

        // 记录交易
        MemberTransaction transaction = new MemberTransaction();
        transaction.setMemberId(memberId);
        transaction.setTransactionType("充值");
        transaction.setAmount(amount);
        transaction.setRemark(remark != null ? remark : "余额充值");
        transaction.setCreateTime(new Date());
        transaction.setOperator("system");
        transactionMapper.insert(transaction);

        return R.ok();
    }

    @GetMapping("/transactions")
    @Operation(summary = "查询交易记录")
    public R<PageResult<MemberTransaction>> getTransactions(
            @RequestParam("memberId") Long memberId,
            @RequestParam(name = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {

        // 1. 初始化分页对象
        Page<MemberTransaction> page = new Page<>(pageNum, pageSize);

        // 2. 构建查询条件（修正 LambdaQueryWrapper 链式调用）
        LambdaQueryWrapper<MemberTransaction> wrapper = new LambdaQueryWrapper<MemberTransaction>()
                .eq(MemberTransaction::getMemberId, memberId) // 关联会员ID
                .orderByDesc(MemberTransaction::getCreateTime); // 按交易时间降序

        // 3. 执行分页查询（transactionMapper 需注入且继承 BaseMapper）
        transactionMapper.selectPage(page, wrapper);

        // 4. 返回分页结果
        return R.ok(new PageResult<>(page.getRecords(), page.getTotal()));
    }
}
