package com.gym.modules.member.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gym.common.exception.ServiceException;
import com.gym.common.utils.SecurityUtils;
import com.gym.modules.auth.domain.entity.Member;
import com.gym.modules.member.domain.dto.MemberAddDto;
import com.gym.modules.member.mapper.MemberMapper;
import com.gym.modules.member.service.IMemberService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class MemberServiceImpl extends ServiceImpl<MemberMapper, Member> implements IMemberService {

    @Override
    public boolean addMember(MemberAddDto dto) {
        if (checkPhoneExists(dto.getPhone())) {
            throw new ServiceException("手机号已被注册");
        }

        Member member = new Member();
        BeanUtils.copyProperties(dto, member);

        // 密码处理
        String password = dto.getPassword() == null ?
                (dto.getPhone().length() >= 6 ? dto.getPhone().substring(dto.getPhone().length() - 6) : "123456") : dto.getPassword();
        member.setPassword(SecurityUtils.encryptPassword(password));

        // 日期处理
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            member.setJoinDate(dto.getJoinDate() != null ? sdf.parse(dto.getJoinDate()) : new Date());
            if (dto.getExpireDate() != null) member.setExpireDate(sdf.parse(dto.getExpireDate()));
        } catch (ParseException e) {
            throw new ServiceException("日期格式不正确");
        }

        member.setStatus("0");
        member.setDeleted(0);

        // [已移除] 原有的 member.setUsername(member.getPhone()) 代码

        return save(member);
    }

    @Override
    public Member getByPhone(String phone) {
        return baseMapper.selectOne(new LambdaQueryWrapper<Member>()
                .eq(Member::getPhone, phone)
                .eq(Member::getDeleted, 0));
    }

    @Override
    public boolean checkPhoneExists(String phone) {
        return baseMapper.selectCount(new LambdaQueryWrapper<Member>()
                .eq(Member::getPhone, phone)
                .eq(Member::getDeleted, 0)) > 0;
    }
}