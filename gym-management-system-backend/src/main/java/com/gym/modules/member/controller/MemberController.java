// com/gym/modules/member/controller/MemberController.java
package com.gym.modules.member.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gym.common.result.PageResult;
import com.gym.common.result.R;
import com.gym.common.utils.SecurityUtils;
import com.gym.modules.auth.domain.entity.Member;
import com.gym.modules.member.domain.dto.MemberAddDto;
import com.gym.modules.member.service.IMemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import java.util.Date;

@RestController
@RequestMapping("/member")
@Tag(name = "会员管理", description = "会员档案管理接口")
public class MemberController {

    @Autowired
    private IMemberService memberService;

    @GetMapping("/list")
    @Operation(summary = "分页查询会员列表")
    public R<PageResult<Member>> list(@RequestParam(name = "pageNum", defaultValue = "1") Integer pageNum,
                                      @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                      @RequestParam(name = "keyword", required = false) String keyword) {
        Page<Member> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Member> wrapper = new LambdaQueryWrapper<>();
        // @TableLogic 会自动处理 deleted=0 的条件，无需手动添加

        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Member::getName, keyword)
                    .or().like(Member::getPhone, keyword)
                    .or().like(Member::getCardNo, keyword));
        }

        wrapper.orderByDesc(Member::getCreateTime);
        memberService.page(page, wrapper);

        return R.ok(new PageResult<>(page.getRecords(), page.getTotal()));
    }

    @GetMapping("/profile")
    @Operation(summary = "获取当前登录会员信息")
    public R<Member> getProfile() {
        // 直接从 SecurityContext 中获取当前登录用户的 ID
        Long userId = SecurityUtils.getUserId();

        Member member = memberService.getById(userId);
        if (member == null) {
            return R.fail("会员信息不存在");
        }
        member.setPassword(null); // 隐藏密码
        return R.ok(member);
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询会员")
    public R<Member> getById(@PathVariable("id") Long id) {
        Member member = memberService.getById(id);
        if (member == null) {
            return R.fail("会员不存在");
        }
        member.setPassword(null); // 隐藏密码
        return R.ok(member);
    }

    @PostMapping
    @Operation(summary = "新增会员")
    public R<Void> add(@Valid @RequestBody MemberAddDto dto) {
        return memberService.addMember(dto) ?
                R.ok() : R.fail("会员添加失败");
    }

    @PutMapping
    @Operation(summary = "修改会员")
    public R<Void> update(@RequestBody Member member) {
        if (member.getId() == null) return R.fail("会员ID不能为空");

        Member exist = memberService.getById(member.getId());
        if (exist == null) return R.fail("会员不存在");

        member.setPassword(null); // 禁止直接修改密码
        member.setCardNo(null); // 禁止直接修改卡号
        member.setUpdateTime(new Date());

        return memberService.updateById(member) ?
                R.ok() : R.fail("更新失败");
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除会员")
    public R<Void> delete(@PathVariable("id") Long id) {
        // 修改处：使用 removeById 触发 MyBatis Plus 的逻辑删除机制
        // MP 会自动生成 UPDATE gym_member SET deleted=1 ... WHERE id=?
        return memberService.removeById(id) ?
                R.ok() : R.fail("删除失败");
    }
}
