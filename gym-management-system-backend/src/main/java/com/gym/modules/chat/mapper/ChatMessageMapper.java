package com.gym.modules.chat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gym.modules.chat.domain.entity.ChatMessage;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ChatMessageMapper extends BaseMapper<ChatMessage> {
}
