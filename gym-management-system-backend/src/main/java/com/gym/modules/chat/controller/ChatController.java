package com.gym.modules.chat.controller;

import com.gym.common.result.R;
import com.gym.modules.chat.domain.dto.ChatMessageSendDto;
import com.gym.modules.chat.domain.vo.ChatContactVo;
import com.gym.modules.chat.domain.vo.ChatMessageVO;
import com.gym.modules.chat.service.IChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Chat")
@RestController
@RequestMapping("/chat")
public class ChatController {

    @Resource
    private IChatService chatService;

    @Operation(summary = "Get chat contacts")
    @GetMapping("/contacts")
    public R<List<ChatContactVo>> getContacts() {
        return R.ok(chatService.getContacts());
    }

    @Operation(summary = "Get chat history")
    @GetMapping("/list")
    public R<List<ChatMessageVO>> getMessages(@RequestParam("contactId") Long contactId, @RequestParam("contactRole") String contactRole) {
        return R.ok(chatService.getMessages(contactId, contactRole));
    }

    @Operation(summary = "Send chat message")
    @PostMapping("/send")
    public R<ChatMessageVO> sendMessage(@Valid @RequestBody ChatMessageSendDto dto) {
        return R.ok(chatService.sendMessage(dto), "sent");
    }

    @Operation(summary = "Get unread chat count")
    @GetMapping("/unread-count")
    public R<Long> getUnreadCount() {
        return R.ok(chatService.getUnreadCount());
    }
}
