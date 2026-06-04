package com.gym.modules.equipment.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserNickNameMapper {
    @Select("<script>" +
            "SELECT user_id AS userId, nickname AS nickName " +
            "FROM sys_user " +
            "WHERE user_id IN " +
            "<foreach collection='userIds' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    List<Map<String, Object>> selectNickNameByIds(@Param("userIds") List<Long> userIds);
}
