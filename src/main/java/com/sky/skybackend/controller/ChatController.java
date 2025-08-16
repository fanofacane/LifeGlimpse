package com.sky.skybackend.controller;

import com.sky.skybackend.domain.pojo.Result;
import com.sky.skybackend.service.ChatService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chat")
public class ChatController {
    @Autowired
    private ChatClient chatClient;
    @Autowired
    private ChatMemory chatMemory;
    @Autowired
    private ChatService chatService;
    @GetMapping("/userList")
    public Result getChatUserList(@RequestParam(required = false) Integer targetId){
        return Result.success(chatService.getChatUserList(targetId));
    }
    @GetMapping("/chatRecord/{targetId}")
    public Result getChatMessageList(@PathVariable Integer targetId){
        return Result.success(chatService.getChatMessageList(targetId));
    }
    @PostMapping("/clearUnread/{id}")
    public Result clearUnread(@PathVariable Integer id){
        return chatService.clearUnread(id);
    }
    @GetMapping(value = "/AIService",produces = "text/html;charset=utf-8")
    public String chatAI(@RequestParam String msg,@RequestParam Integer userId){
        return chatClient.prompt()
                .user(msg)
                .advisors(a-> a.param(chatMemory.CONVERSATION_ID,userId))
                .call()
                .content();
    }
}
