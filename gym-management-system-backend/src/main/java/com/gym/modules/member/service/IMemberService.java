package com.gym.modules.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gym.modules.auth.domain.entity.Member;
import com.gym.modules.member.domain.dto.MemberAddDto;

public interface IMemberService extends IService<Member> {
    boolean addMember(MemberAddDto memberAddDto);
    Member getByPhone(String phone);
    boolean checkPhoneExists(String phone);
}