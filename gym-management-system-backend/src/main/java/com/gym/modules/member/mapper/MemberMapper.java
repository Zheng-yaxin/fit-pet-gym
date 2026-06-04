package com.gym.modules.member.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gym.modules.auth.domain.entity.Member;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员数据访问层
 * 配合 src/main/resources/mapper/member/MemberMapper.xml 使用
 */
@Mapper
public interface MemberMapper extends BaseMapper<Member> {
    // MyBatis Plus 已内置 selectOne, insert, updateById 等方法
    // 无需额外手写 SQL，即可支持根据 phone 查询等操作
}